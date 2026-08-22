package com.example.demo.payment.service;

import com.example.demo.config.CurrentUserUtils;
import com.example.demo.config.TenantContext;
import com.example.demo.enums.FinanceCategory;
import com.example.demo.enums.InvoiceStatus;
import com.example.demo.enums.InvoiceType;
import com.example.demo.enums.PaymentMethod;
import com.example.demo.enums.PaymentStatus;
import com.example.demo.enums.PaymentType;
import com.example.demo.management.model.Grouping;
import com.example.demo.management.repository.GroupRepository;
import com.example.demo.payment.dto.AllocationRequest;
import com.example.demo.payment.dto.CreatePaymentRequest;
import com.example.demo.payment.dto.GroupBalanceItem;
import com.example.demo.payment.dto.PaymentDTO;
import com.example.demo.payment.dto.RefundRequest;
import com.example.demo.payment.dto.StudentBalanceResponse;
import com.example.demo.payment.mapper.PaymentMapper;
import com.example.demo.payment.model.Invoice;
import com.example.demo.payment.model.Payment;
import com.example.demo.payment.model.PaymentAllocation;
import com.example.demo.payment.repository.InvoiceRepository;
import com.example.demo.payment.repository.PaymentAllocationRepository;
import com.example.demo.payment.repository.PaymentRepository;
import com.example.demo.payment.specification.PaymentSpecification;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final InvoiceRepository invoiceRepository;
    private final PaymentAllocationRepository allocationRepository;
    private final PaymentMapper paymentMapper;
    private final GroupRepository groupRepository;

    @Transactional
    public PaymentDTO createPayment(CreatePaymentRequest request) {
        if (request.getType() == PaymentType.REFUND) {
            throw new IllegalArgumentException("Qaytarish uchun /payments/{id}/refund endpoint ishlatiladi");
        }

        Payment payment = new Payment();
        payment.setType(request.getType());
        payment.setDirection((short) (request.getType() == PaymentType.RECEIPT ? 1 : -1));
        payment.setCategory(request.getCategory());
        payment.setCounterpartyType(request.getCounterpartyType());
        payment.setUserId(request.getUserId());
        payment.setGroupId(request.getGroupId());
        payment.setVendorName(request.getVendorName());
        payment.setAmount(request.getAmount());
        payment.setMethod(request.getMethod());
        payment.setPaidAt(request.getPaidAt() != null ? request.getPaidAt() : LocalDateTime.now());
        payment.setNote(request.getNote());
        payment.setProcessedBy(CurrentUserUtils.getUserId());
        payment.setPaymentStatus(PaymentStatus.COMPLETED);
        payment.setCenterId(TenantContext.getCenterId());
        payment = paymentRepository.save(payment);

        List<AllocationRequest> allocations = request.getAllocations();
        if (allocations != null && !allocations.isEmpty()) {
            applyManualAllocations(payment, allocations);
        } else if (Boolean.TRUE.equals(request.getAutoAllocate())) {
            applyAutoFifoAllocation(payment);
        }
        // Aks holda hech narsa allocate qilinmaydi — to'lov ataylab "bog'lanmagan" holda qoladi.
        // Hech qanday invoice (hozirgi yoki kelajakdagi) buni avtomatik o'ziga tortib olmaydi;
        // faqat admin keyinroq /payments/{id}/allocate orqali ataylab bog'lashi mumkin.

        return paymentMapper.toDto(payment);
    }

    /** Admin mavjud (yozilgan) to'lovni keyinroq, ataylab, ma'lum bir invoice'ga bog'laydi. */
    @Transactional
    public PaymentDTO allocateToInvoice(UUID paymentId, UUID invoiceId, BigDecimal amount) {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new EntityNotFoundException("Payment not found with id: " + paymentId));
        if (payment.getPaymentStatus() != PaymentStatus.COMPLETED) {
            throw new IllegalArgumentException("Faqat COMPLETED to'lovni allocate qilish mumkin");
        }
        Invoice invoice = invoiceRepository.findById(invoiceId)
                .orElseThrow(() -> new EntityNotFoundException("Invoice not found with id: " + invoiceId));

        BigDecimal alreadyAllocated = allocationRepository.sumAllocatedByPaymentId(paymentId);
        BigDecimal unallocated = payment.getAmount().subtract(alreadyAllocated);
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0 || amount.compareTo(unallocated) > 0) {
            throw new IllegalArgumentException("Summasi 0 dan katta va to'lovning bog'lanmagan qoldig'i (" + unallocated + ") dan oshmasligi kerak");
        }

        allocate(payment, invoice, amount);
        return paymentMapper.toDto(payment);
    }

    @Transactional
    public PaymentDTO refund(RefundRequest request) {
        Payment original = paymentRepository.findById(request.getPaymentId())
                .orElseThrow(() -> new EntityNotFoundException("Payment not found with id: " + request.getPaymentId()));
        if (original.getPaymentStatus() != PaymentStatus.COMPLETED) {
            throw new IllegalArgumentException("Faqat COMPLETED to'lovni qaytarish mumkin");
        }
        if (original.getType() == PaymentType.REFUND) {
            throw new IllegalArgumentException("Qaytarishni qayta qaytarib bo'lmaydi");
        }

        BigDecimal alreadyRefunded = paymentRepository.findByRelatedPaymentId(original.getId()).stream()
                .filter(p -> p.getPaymentStatus() == PaymentStatus.COMPLETED)
                .map(Payment::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal refundable = original.getAmount().subtract(alreadyRefunded);
        if (request.getAmount() == null || request.getAmount().compareTo(BigDecimal.ZERO) <= 0
                || request.getAmount().compareTo(refundable) > 0) {
            throw new IllegalArgumentException("Qaytarish summasi 0 dan katta va qolgan " + refundable + " dan oshmasligi kerak");
        }

        Payment refund = new Payment();
        refund.setType(PaymentType.REFUND);
        refund.setDirection((short) -original.getDirection());
        refund.setCategory(original.getCategory());
        refund.setCounterpartyType(original.getCounterpartyType());
        refund.setUserId(original.getUserId());
        refund.setGroupId(original.getGroupId());
        refund.setVendorName(original.getVendorName());
        refund.setAmount(request.getAmount());
        refund.setMethod(original.getMethod());
        refund.setPaidAt(LocalDateTime.now());
        refund.setNote(request.getNote());
        refund.setProcessedBy(CurrentUserUtils.getUserId());
        refund.setPaymentStatus(PaymentStatus.COMPLETED);
        refund.setRelatedPaymentId(original.getId());
        refund.setCenterId(original.getCenterId());
        refund = paymentRepository.save(refund);

        reverseAllocationsForRefund(original, request.getAmount());

        return paymentMapper.toDto(refund);
    }

    @Transactional
    public PaymentDTO voidPayment(UUID paymentId) {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new EntityNotFoundException("Payment not found with id: " + paymentId));
        if (payment.getPaymentStatus() == PaymentStatus.VOID) {
            return paymentMapper.toDto(payment);
        }

        for (PaymentAllocation allocation : allocationRepository.findByPaymentId(paymentId)) {
            Invoice invoice = invoiceRepository.findById(allocation.getInvoiceId())
                    .orElseThrow(() -> new EntityNotFoundException("Invoice not found with id: " + allocation.getInvoiceId()));
            allocationRepository.delete(allocation);
            recomputeInvoiceStatus(invoice);
        }

        payment.setPaymentStatus(PaymentStatus.VOID);
        paymentRepository.save(payment);
        return paymentMapper.toDto(payment);
    }

    public Page<PaymentDTO> getAll(
            PaymentType type, FinanceCategory category, Long userId, PaymentMethod method,
            PaymentStatus status, LocalDateTime from, LocalDateTime to, Pageable pageable
    ) {
        var spec = PaymentSpecification.advancedFilter(
                TenantContext.getCenterId(), type, category, userId, method, status, from, to);
        return paymentRepository.findAll(spec, pageable).map(paymentMapper::toDto);
    }

    /**
     * Yangi generatsiya qilingan invoice uchun shu student/teacher'ning oldin ortiqcha to'lagan
     * (hali to'liq allocate qilinmagan) to'lovlarini avtomatik qo'llaydi — "credit" mexanizmi.
     */
    @Transactional
    public void applyAvailableCredit(Invoice invoice) {
        PaymentType paymentType;
        FinanceCategory category;
        if (invoice.getType() == InvoiceType.STUDENT_TUITION) {
            paymentType = PaymentType.RECEIPT;
            category = FinanceCategory.TUITION_FEE;
        } else if (invoice.getType() == InvoiceType.TEACHER_SALARY) {
            paymentType = PaymentType.EXPENSE;
            category = FinanceCategory.TEACHER_SALARY;
        } else {
            return;
        }

        List<Payment> candidates = invoice.getType() == InvoiceType.STUDENT_TUITION
                ? paymentRepository.findByTypeAndCategoryAndUserIdAndGroupIdAndPaymentStatusOrderByPaidAtAsc(
                        paymentType, category, invoice.getUserId(), invoice.getGroupId(), PaymentStatus.COMPLETED)
                : paymentRepository.findByTypeAndCategoryAndUserIdAndPaymentStatusOrderByPaidAtAsc(
                        paymentType, category, invoice.getUserId(), PaymentStatus.COMPLETED);

        for (Payment payment : candidates) {
            if (outstanding(invoice).compareTo(BigDecimal.ZERO) <= 0) break;
            BigDecimal unallocated = payment.getAmount().subtract(allocationRepository.sumAllocatedByPaymentId(payment.getId()));
            if (unallocated.compareTo(BigDecimal.ZERO) <= 0) continue;
            BigDecimal take = unallocated.min(outstanding(invoice));
            allocate(payment, invoice, take);
        }
    }

    private void applyManualAllocations(Payment payment, List<AllocationRequest> allocations) {
        BigDecimal totalRequested = allocations.stream()
                .map(AllocationRequest::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        if (totalRequested.compareTo(payment.getAmount()) > 0) {
            throw new IllegalArgumentException("Allocation summasi to'lov summasidan (" + payment.getAmount() + ") oshmasligi kerak");
        }
        for (AllocationRequest allocationRequest : allocations) {
            Invoice invoice = invoiceRepository.findById(allocationRequest.getInvoiceId())
                    .orElseThrow(() -> new EntityNotFoundException("Invoice not found with id: " + allocationRequest.getInvoiceId()));
            allocate(payment, invoice, allocationRequest.getAmount());
        }
    }

    private void applyAutoFifoAllocation(Payment payment) {
        InvoiceType targetType = resolveObligationType(payment);
        if (targetType == null) {
            return;
        }

        List<Invoice> candidates = targetType == InvoiceType.STUDENT_TUITION
                ? invoiceRepository.findByTypeAndUserIdAndGroupIdAndInvoiceStatusInOrderByPeriodAsc(
                        targetType, payment.getUserId(), payment.getGroupId(),
                        List.of(InvoiceStatus.PENDING, InvoiceStatus.PARTIALLY_PAID))
                : invoiceRepository.findByTypeAndUserIdAndInvoiceStatusInOrderByPeriodAsc(
                        targetType, payment.getUserId(),
                        List.of(InvoiceStatus.PENDING, InvoiceStatus.PARTIALLY_PAID));

        BigDecimal remaining = payment.getAmount();
        for (Invoice invoice : candidates) {
            if (remaining.compareTo(BigDecimal.ZERO) <= 0) break;
            BigDecimal outstanding = outstanding(invoice);
            if (outstanding.compareTo(BigDecimal.ZERO) <= 0) continue;
            BigDecimal take = remaining.min(outstanding);
            allocate(payment, invoice, take);
            remaining = remaining.subtract(take);
        }
        // qolgan (allocate qilinmagan) qism keyingi oy majburiyati yaratilganda
        // applyAvailableCredit orqali avtomatik ishlatiladi (kredit sifatida).
    }

    private InvoiceType resolveObligationType(Payment payment) {
        if (payment.getType() == PaymentType.RECEIPT && payment.getCategory() == FinanceCategory.TUITION_FEE) {
            return InvoiceType.STUDENT_TUITION;
        }
        if (payment.getType() == PaymentType.EXPENSE && payment.getCategory() == FinanceCategory.TEACHER_SALARY) {
            return InvoiceType.TEACHER_SALARY;
        }
        return null;
    }

    private void allocate(Payment payment, Invoice invoice, BigDecimal amount) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            return;
        }
        PaymentAllocation allocation = new PaymentAllocation();
        allocation.setPaymentId(payment.getId());
        allocation.setInvoiceId(invoice.getId());
        allocation.setAllocatedAmount(amount);
        allocation.setCenterId(payment.getCenterId());
        allocationRepository.save(allocation);
        recomputeInvoiceStatus(invoice);
    }

    private void reverseAllocationsForRefund(Payment original, BigDecimal refundAmount) {
        List<PaymentAllocation> allocations = allocationRepository.findByPaymentId(original.getId());
        allocations.sort(Comparator.comparing(PaymentAllocation::getCreatedAt, Comparator.nullsLast(Comparator.reverseOrder())));

        BigDecimal remainingToReverse = refundAmount;
        for (PaymentAllocation allocation : allocations) {
            if (remainingToReverse.compareTo(BigDecimal.ZERO) <= 0) break;
            BigDecimal reduceBy = remainingToReverse.min(allocation.getAllocatedAmount());
            allocation.setAllocatedAmount(allocation.getAllocatedAmount().subtract(reduceBy));

            Invoice invoice = invoiceRepository.findById(allocation.getInvoiceId())
                    .orElseThrow(() -> new EntityNotFoundException("Invoice not found with id: " + allocation.getInvoiceId()));

            if (allocation.getAllocatedAmount().compareTo(BigDecimal.ZERO) == 0) {
                allocationRepository.delete(allocation);
            } else {
                allocationRepository.save(allocation);
            }
            recomputeInvoiceStatus(invoice);
            remainingToReverse = remainingToReverse.subtract(reduceBy);
        }
    }

    /**
     * O'quvchining joriy balansi: har guruh bo'yicha (va umumiy) kredit (ortiqcha to'lagan, hali
     * allocate qilinmagan qoldiq) va qarz (to'lanmagan invoice summasi). {@code netBalance > 0} —
     * balansda pul bor (keyingi oyga avtomatik o'tadi), {@code < 0} — qarz.
     */
    public StudentBalanceResponse getStudentBalance(Long studentId) {
        List<Invoice> outstandingInvoices = invoiceRepository.findByTypeAndUserIdAndInvoiceStatusInOrderByPeriodAsc(
                InvoiceType.STUDENT_TUITION, studentId, List.of(InvoiceStatus.PENDING, InvoiceStatus.PARTIALLY_PAID));
        List<Payment> tuitionPayments = paymentRepository.findByTypeAndCategoryAndUserIdAndPaymentStatusOrderByPaidAtAsc(
                PaymentType.RECEIPT, FinanceCategory.TUITION_FEE, studentId, PaymentStatus.COMPLETED);

        Map<UUID, BigDecimal> owedByGroup = new LinkedHashMap<>();
        for (Invoice invoice : outstandingInvoices) {
            owedByGroup.merge(invoice.getGroupId(), outstanding(invoice), BigDecimal::add);
        }

        Map<UUID, BigDecimal> creditByGroup = new LinkedHashMap<>();
        for (Payment payment : tuitionPayments) {
            BigDecimal unallocated = payment.getAmount().subtract(allocationRepository.sumAllocatedByPaymentId(payment.getId()));
            if (unallocated.compareTo(BigDecimal.ZERO) > 0) {
                creditByGroup.merge(payment.getGroupId(), unallocated, BigDecimal::add);
            }
        }

        Set<UUID> groupIds = new LinkedHashSet<>();
        groupIds.addAll(owedByGroup.keySet());
        groupIds.addAll(creditByGroup.keySet());

        List<GroupBalanceItem> items = new ArrayList<>();
        BigDecimal totalCredit = BigDecimal.ZERO;
        BigDecimal totalOwed = BigDecimal.ZERO;
        for (UUID groupId : groupIds) {
            BigDecimal credit = creditByGroup.getOrDefault(groupId, BigDecimal.ZERO);
            BigDecimal owed = owedByGroup.getOrDefault(groupId, BigDecimal.ZERO);
            totalCredit = totalCredit.add(credit);
            totalOwed = totalOwed.add(owed);

            GroupBalanceItem item = new GroupBalanceItem();
            item.setGroupId(groupId);
            groupRepository.findById(groupId).map(Grouping::getName).ifPresent(item::setGroupName);
            item.setCredit(credit);
            item.setOwed(owed);
            item.setNetBalance(credit.subtract(owed));
            items.add(item);
        }

        StudentBalanceResponse response = new StudentBalanceResponse();
        response.setStudentId(studentId);
        response.setTotalCredit(totalCredit);
        response.setTotalOwed(totalOwed);
        response.setNetBalance(totalCredit.subtract(totalOwed));
        response.setGroups(items);
        return response;
    }

    private BigDecimal outstanding(Invoice invoice) {
        BigDecimal allocated = allocationRepository.sumAllocatedByInvoiceId(invoice.getId());
        return invoice.netAmount().subtract(allocated);
    }

    private void recomputeInvoiceStatus(Invoice invoice) {
        if (invoice.getInvoiceStatus() == InvoiceStatus.CANCELLED) {
            return;
        }
        BigDecimal allocated = allocationRepository.sumAllocatedByInvoiceId(invoice.getId());
        BigDecimal net = invoice.netAmount();
        if (allocated.compareTo(net) >= 0) {
            invoice.setInvoiceStatus(InvoiceStatus.PAID);
        } else if (allocated.compareTo(BigDecimal.ZERO) > 0) {
            invoice.setInvoiceStatus(InvoiceStatus.PARTIALLY_PAID);
        } else {
            invoice.setInvoiceStatus(InvoiceStatus.PENDING);
        }
        invoiceRepository.save(invoice);
    }
}
