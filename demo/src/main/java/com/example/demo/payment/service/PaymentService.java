package com.example.demo.payment.service;

import com.example.demo.config.CurrentUserUtils;
import com.example.demo.config.TenantContext;
import com.example.demo.enums.*;
import com.example.demo.management.model.Grouping;
import com.example.demo.management.repository.GroupRepository;
import com.example.demo.payment.dto.*;
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
import java.util.*;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final InvoiceRepository invoiceRepository;
    private final PaymentAllocationRepository allocationRepository;
    private final PaymentMapper paymentMapper;
    private final GroupRepository groupRepository;

    /**
     * Payment yaratadi.
     * <p>
     * Allocation uch xil rejimda ishlaydi:
     * <p>
     * 1. allocations berilgan bo'lsa -> MANUAL
     * 2. autoAllocate = true bo'lsa -> AUTO FIFO
     * 3. ikkalasi ham bo'lmasa -> UNALLOCATED
     * <p>
     * Muhim:
     * Payment groupga bog'lanmaydi.
     * Group allocation qilingan Invoice orqali aniqlanadi.
     */
    @Transactional
    public PaymentDTO createPayment(CreatePaymentRequest request) {

        if (request.getType() == PaymentType.REFUND) {
            throw new IllegalArgumentException(
                    "Qaytarish uchun /payments/{id}/refund endpoint ishlatiladi"
            );
        }

        validatePaymentRequest(request);

        Payment payment = new Payment();

        payment.setType(request.getType());
        payment.setDirection(
                (short) (request.getType() == PaymentType.RECEIPT ? 1 : -1)
        );
        payment.setCategory(request.getCategory());
        payment.setCounterpartyType(request.getCounterpartyType());
        payment.setUserId(request.getUserId());

        /*
         * Payment.groupId intentionally NOT set.
         *
         * Group:
         * Payment -> PaymentAllocation -> Invoice -> Group
         */

        payment.setVendorName(request.getVendorName());
        payment.setAmount(request.getAmount());
        payment.setMethod(request.getMethod());
        payment.setPaidAt(
                request.getPaidAt() != null
                        ? request.getPaidAt()
                        : LocalDateTime.now()
        );
        payment.setNote(request.getNote());
        payment.setProcessedBy(CurrentUserUtils.getUserId());
        payment.setPaymentStatus(PaymentStatus.COMPLETED);
        payment.setCenterId(TenantContext.getCenterId());

        payment = paymentRepository.save(payment);

        List<AllocationRequest> allocations = request.getAllocations();

        if (allocations != null && !allocations.isEmpty()) {
            // MANUAL
            applyManualAllocations(payment, allocations);

        } else if (Boolean.TRUE.equals(request.getAutoAllocate())) {
            // AUTO FIFO
            applyAutoFifoAllocation(payment);

        } else {
            // UNALLOCATED
            // Nothing to do.
            //
            // Bu payment kelajakdagi invoice'ga avtomatik tortilmaydi.
            // Admin keyinchalik explicit allocation qilishi mumkin.
        }

        return paymentMapper.toDto(payment);
    }

    /**
     * Mavjud paymentning unallocated qismini ma'lum invoice'ga allocate qiladi.
     */
    @Transactional
    public PaymentDTO allocateToInvoice(
            UUID paymentId,
            UUID invoiceId,
            BigDecimal amount
    ) {

        Payment payment = getPayment(paymentId);

        validateCompletedPayment(payment);

        Invoice invoice = getInvoice(invoiceId);

        validatePaymentInvoiceRelation(payment, invoice);

        BigDecimal unallocated = getUnallocatedAmount(payment);

        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException(
                    "Allocation summasi 0 dan katta bo'lishi kerak"
            );
        }

        if (amount.compareTo(unallocated) > 0) {
            throw new IllegalArgumentException(
                    "Allocation summasi paymentning unallocated qoldig'i ("
                            + unallocated
                            + ") dan oshmasligi kerak"
            );
        }

        validateInvoiceCanReceiveAllocation(invoice, amount);

        allocate(payment, invoice, amount);

        return paymentMapper.toDto(payment);
    }

    /**
     * Paymentni refund qiladi.
     * <p>
     * Refund qilingan summa:
     * <p>
     * - allocated bo'lsa -> allocationlar reverse qilinadi;
     * - unallocated bo'lsa -> unallocated credit avtomatik kamayadi.
     * <p>
     * Shu sababli refund uchun alohida group kerak emas.
     */
    @Transactional
    public PaymentDTO refund(RefundRequest request) {

        Payment original = paymentRepository.findById(request.getPaymentId())
                .orElseThrow(() ->
                        new EntityNotFoundException(
                                "Payment not found with id: "
                                        + request.getPaymentId()
                        )
                );

        validateCompletedPayment(original);

        if (original.getType() == PaymentType.REFUND) {
            throw new IllegalArgumentException(
                    "Qaytarishni qayta qaytarib bo'lmaydi"
            );
        }

        BigDecimal alreadyRefunded =
                getAlreadyRefundedAmount(original);

        BigDecimal refundable =
                original.getAmount().subtract(alreadyRefunded);

        if (request.getAmount() == null
                || request.getAmount().compareTo(BigDecimal.ZERO) <= 0
                || request.getAmount().compareTo(refundable) > 0) {

            throw new IllegalArgumentException(
                    "Qaytarish summasi 0 dan katta va qolgan "
                            + refundable
                            + " dan oshmasligi kerak"
            );
        }

        Payment refund = new Payment();

        refund.setType(PaymentType.REFUND);
        refund.setDirection((short) -original.getDirection());
        refund.setCategory(original.getCategory());
        refund.setCounterpartyType(original.getCounterpartyType());
        refund.setUserId(original.getUserId());

        /*
         * Refund ham groupga bog'lanmaydi.
         */

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

        /*
         * Avval allocated qismini reverse qilamiz.
         *
         * Agar refund unallocated paymentdan bo'lsa,
         * bu metod hech narsa reverse qilmaydi.
         * Lekin getUnallocatedAmount() refundlarni ham hisobga olgani
         * sababli credit baribir to'g'ri kamayadi.
         */
        reverseAllocationsForRefund(
                original,
                request.getAmount()
        );

        return paymentMapper.toDto(refund);
    }

    /**
     * Paymentni VOID qiladi.
     * <p>
     * Allocationlar o'chiriladi va invoice statuslari qayta hisoblanadi.
     */
    @Transactional
    public PaymentDTO voidPayment(UUID paymentId) {

        Payment payment = getPayment(paymentId);

        if (payment.getPaymentStatus() == PaymentStatus.VOID) {
            return paymentMapper.toDto(payment);
        }

        List<PaymentAllocation> allocations =
                allocationRepository.findByPaymentId(paymentId);

        for (PaymentAllocation allocation : allocations) {

            Invoice invoice = invoiceRepository
                    .findById(allocation.getInvoiceId())
                    .orElseThrow(() ->
                            new EntityNotFoundException(
                                    "Invoice not found with id: "
                                            + allocation.getInvoiceId()
                            )
                    );

            allocationRepository.delete(allocation);

            recomputeInvoiceStatus(invoice);
        }

        payment.setPaymentStatus(PaymentStatus.VOID);

        paymentRepository.save(payment);

        return paymentMapper.toDto(payment);
    }

    /**
     * Paymentlar ro'yxati.
     */
    public Page<PaymentDTO> getAll(
            PaymentType type,
            FinanceCategory category,
            Long userId,
            PaymentMethod method,
            PaymentStatus status,
            LocalDateTime from,
            LocalDateTime to,
            Pageable pageable
    ) {

        var spec = PaymentSpecification.advancedFilter(
                TenantContext.getCenterId(),
                type,
                category,
                userId,
                method,
                status,
                from,
                to
        );

        return paymentRepository
                .findAll(spec, pageable)
                .map(paymentMapper::toDto);
    }

    /**
     * Bu metod faqat agar biznes qoidang:
     * <p>
     * "UNALLOCATED credit keyingi invoice yaratilganda avtomatik ishlatiladi"
     * <p>
     * bo'lsa kerak.
     * <p>
     * Lekin sen tanlagan modelda UNALLOCATED:
     * "admin keyin o'zi allocate qiladi"
     * <p>
     * bo'lgani uchun bu metodni chaqirish shart emas.
     * <p>
     * Agar keyinchalik shu feature kerak bo'lsa, groupId ishlatmasdan
     * studentning barcha unallocated paymentlarini ko'rib chiqadi.
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

        List<Payment> candidates =
                paymentRepository
                        .findByTypeAndCategoryAndUserIdAndPaymentStatusOrderByPaidAtAsc(
                                paymentType,
                                category,
                                invoice.getUserId(),
                                PaymentStatus.COMPLETED
                        );

        for (Payment payment : candidates) {

            if (outstanding(invoice).compareTo(BigDecimal.ZERO) <= 0) {
                break;
            }

            BigDecimal unallocated =
                    getUnallocatedAmount(payment);

            if (unallocated.compareTo(BigDecimal.ZERO) <= 0) {
                continue;
            }

            BigDecimal take =
                    unallocated.min(outstanding(invoice));

            allocate(payment, invoice, take);
        }
    }

    /**
     * Manual allocation.
     */
    private void applyManualAllocations(
            Payment payment,
            List<AllocationRequest> allocations
    ) {

        if (allocations == null || allocations.isEmpty()) {
            return;
        }

        /*
         * MUHIM:
         * paymentning butun amounti emas,
         * hozirgi unallocated qoldig'i hisobga olinadi.
         */
        BigDecimal available =
                getUnallocatedAmount(payment);

        BigDecimal totalRequested =
                allocations.stream()
                        .map(AllocationRequest::getAmount)
                        .filter(amount -> amount != null)
                        .reduce(BigDecimal.ZERO, BigDecimal::add);

        if (totalRequested.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException(
                    "Kamida bitta musbat allocation summasi bo'lishi kerak"
            );
        }

        if (totalRequested.compareTo(available) > 0) {
            throw new IllegalArgumentException(
                    "Allocation summasi paymentning mavjud unallocated qoldig'i ("
                            + available
                            + ") dan oshmasligi kerak"
            );
        }

        for (AllocationRequest allocationRequest : allocations) {

            if (allocationRequest.getAmount() == null
                    || allocationRequest.getAmount()
                    .compareTo(BigDecimal.ZERO) <= 0) {

                throw new IllegalArgumentException(
                        "Allocation summasi 0 dan katta bo'lishi kerak"
                );
            }

            Invoice invoice =
                    getInvoice(allocationRequest.getInvoiceId());

            validatePaymentInvoiceRelation(payment, invoice);

            validateInvoiceCanReceiveAllocation(
                    invoice,
                    allocationRequest.getAmount()
            );

            allocate(
                    payment,
                    invoice,
                    allocationRequest.getAmount()
            );
        }
    }

    /**
     * AUTO FIFO allocation.
     * <p>
     * Payment qaysi groupdan kelganini bilmaymiz va bilishimiz ham shart emas.
     * <p>
     * Studentning barcha guruhlaridagi invoice'lar:
     * <p>
     * period ASC
     * <p>
     * bo'yicha yopiladi.
     */
    private void applyAutoFifoAllocation(Payment payment) {

        InvoiceType targetType =
                resolveObligationType(payment);

        if (targetType == null) {
            return;
        }

        List<Invoice> candidates =
                invoiceRepository
                        .findByTypeAndUserIdAndInvoiceStatusInOrderByPeriodAsc(
                                targetType,
                                payment.getUserId(),
                                List.of(
                                        InvoiceStatus.PENDING,
                                        InvoiceStatus.PARTIALLY_PAID
                                )
                        );

        BigDecimal remaining =
                getUnallocatedAmount(payment);

        for (Invoice invoice : candidates) {

            if (remaining.compareTo(BigDecimal.ZERO) <= 0) {
                break;
            }

            BigDecimal invoiceOutstanding =
                    outstanding(invoice);

            if (invoiceOutstanding.compareTo(BigDecimal.ZERO) <= 0) {
                continue;
            }

            BigDecimal take =
                    remaining.min(invoiceOutstanding);

            allocate(payment, invoice, take);

            remaining =
                    remaining.subtract(take);
        }

        /*
         * Agar remaining > 0 bo'lsa:
         *
         * Paymentning shu qismi UNALLOCATED credit sifatida qoladi.
         *
         * U keyingi invoice'ga avtomatik o'tmaydi.
         */
    }

    /**
     * Payment qaysi turdagi obligationni yopishi mumkinligini aniqlaydi.
     */
    private InvoiceType resolveObligationType(Payment payment) {

        if (payment.getType() == PaymentType.RECEIPT
                && payment.getCategory() == FinanceCategory.TUITION_FEE) {

            return InvoiceType.STUDENT_TUITION;
        }

        if (payment.getType() == PaymentType.EXPENSE
                && payment.getCategory() == FinanceCategory.TEACHER_SALARY) {

            return InvoiceType.TEACHER_SALARY;
        }

        return null;
    }

    /**
     * Payment -> Invoice allocation yaratadi.
     * <p>
     * Barcha business validationlar tashqarida bajariladi,
     * lekin methodning o'zi ham asosiy xavfsizliklarni tekshiradi.
     */
    private void allocate(
            Payment payment,
            Invoice invoice,
            BigDecimal amount
    ) {

        if (amount == null
                || amount.compareTo(BigDecimal.ZERO) <= 0) {

            throw new IllegalArgumentException(
                    "Allocation summasi 0 dan katta bo'lishi kerak"
            );
        }

        validatePaymentInvoiceRelation(payment, invoice);
        validateInvoiceCanReceiveAllocation(invoice, amount);

        BigDecimal available =
                getUnallocatedAmount(payment);

        if (amount.compareTo(available) > 0) {
            throw new IllegalArgumentException(
                    "Allocation summasi paymentning unallocated qoldig'i ("
                            + available
                            + ") dan oshmasligi kerak"
            );
        }

        PaymentAllocation allocation =
                new PaymentAllocation();

        allocation.setPaymentId(payment.getId());
        allocation.setInvoiceId(invoice.getId());
        allocation.setAllocatedAmount(amount);
        allocation.setCenterId(payment.getCenterId());

        allocationRepository.save(allocation);

        recomputeInvoiceStatus(invoice);
    }

    /**
     * Refund qilinganda original paymentning allocationlarini
     * eng oxirgisidan boshlab reverse qiladi.
     */
    private void reverseAllocationsForRefund(
            Payment original,
            BigDecimal refundAmount
    ) {

        List<PaymentAllocation> allocations =
                allocationRepository.findByPaymentId(
                        original.getId()
                );

        allocations.sort(
                Comparator.comparing(
                        PaymentAllocation::getCreatedAt,
                        Comparator.nullsLast(
                                Comparator.reverseOrder()
                        )
                )
        );

        BigDecimal remainingToReverse =
                refundAmount;

        for (PaymentAllocation allocation : allocations) {

            if (remainingToReverse.compareTo(BigDecimal.ZERO) <= 0) {
                break;
            }

            BigDecimal allocated =
                    allocation.getAllocatedAmount();

            BigDecimal reduceBy =
                    remainingToReverse.min(allocated);

            BigDecimal newAmount =
                    allocated.subtract(reduceBy);

            Invoice invoice =
                    invoiceRepository
                            .findById(allocation.getInvoiceId())
                            .orElseThrow(() ->
                                    new EntityNotFoundException(
                                            "Invoice not found with id: "
                                                    + allocation.getInvoiceId()
                                    )
                            );

            if (newAmount.compareTo(BigDecimal.ZERO) == 0) {

                allocationRepository.delete(allocation);

            } else {

                allocation.setAllocatedAmount(newAmount);

                allocationRepository.save(allocation);
            }

            recomputeInvoiceStatus(invoice);

            remainingToReverse =
                    remainingToReverse.subtract(reduceBy);
        }

        /*
         * Agar refund summasi allocationlardan katta bo'lsa,
         * qolgan qism original paymentning unallocated creditidan
         * qaytgan hisoblanadi.
         *
         * Bu qism uchun allocation yaratish yoki reverse qilish shart emas.
         * getUnallocatedAmount() refundlarni hisobga oladi.
         */
    }

    /**
     * Studentning balansini hisoblaydi.
     * <p>
     * Group:
     * faqat Invoice.groupId orqali aniqlanadi.
     * <p>
     * Credit:
     * hali hech qaysi Invoice'ga allocate qilinmagan payment qoldig'i.
     * <p>
     * Unallocated credit hech qaysi groupga tegishli emas.
     */
    public StudentBalanceResponse getStudentBalance(
            Long studentId
    ) {

        List<Invoice> outstandingInvoices =
                invoiceRepository
                        .findByTypeAndUserIdAndInvoiceStatusInOrderByPeriodAsc(
                                InvoiceType.STUDENT_TUITION,
                                studentId,
                                List.of(
                                        InvoiceStatus.PENDING,
                                        InvoiceStatus.PARTIALLY_PAID
                                )
                        );

        List<Payment> tuitionPayments =
                paymentRepository
                        .findByTypeAndCategoryAndUserIdAndPaymentStatusOrderByPaidAtAsc(
                                PaymentType.RECEIPT,
                                FinanceCategory.TUITION_FEE,
                                studentId,
                                PaymentStatus.COMPLETED
                        );

        /*
         * ------------------------------------------------------------
         * 1. Qarzdorlikni GROUP bo'yicha hisoblash
         * ------------------------------------------------------------
         */
        Map<UUID, BigDecimal> owedByGroup =
                new LinkedHashMap<>();

        for (Invoice invoice : outstandingInvoices) {

            BigDecimal outstandingAmount =
                    outstanding(invoice);

            if (outstandingAmount.compareTo(BigDecimal.ZERO) <= 0) {
                continue;
            }

            UUID groupId =
                    invoice.getGroupId();

            if (groupId == null) {
                continue;
            }

            owedByGroup.merge(
                    groupId,
                    outstandingAmount,
                    BigDecimal::add
            );
        }

        /*
         * ------------------------------------------------------------
         * 2. UNALLOCATED CREDIT
         *
         * Payment groupga bog'lanmaydi.
         * Shuning uchun credit ham group bo'yicha emas,
         * student bo'yicha umumiy hisoblanadi.
         * ------------------------------------------------------------
         */
        BigDecimal totalUnallocatedCredit =
                BigDecimal.ZERO;

        for (Payment payment : tuitionPayments) {

            BigDecimal unallocated =
                    getUnallocatedAmount(payment);

            if (unallocated.compareTo(BigDecimal.ZERO) > 0) {

                totalUnallocatedCredit =
                        totalUnallocatedCredit.add(unallocated);
            }
        }

        /*
         * ------------------------------------------------------------
         * 3. GROUP BALANCE
         * ------------------------------------------------------------
         */
        List<GroupBalanceItem> items =
                new ArrayList<>();

        BigDecimal totalOwed =
                BigDecimal.ZERO;

        for (Map.Entry<UUID, BigDecimal> entry
                : owedByGroup.entrySet()) {

            UUID groupId =
                    entry.getKey();

            BigDecimal owed =
                    entry.getValue();

            totalOwed =
                    totalOwed.add(owed);

            GroupBalanceItem item =
                    new GroupBalanceItem();

            item.setGroupId(groupId);

            groupRepository
                    .findById(groupId)
                    .map(Grouping::getName)
                    .ifPresent(item::setGroupName);

            /*
             * Unallocated credit hech qaysi groupga tegishli emas.
             */
            item.setCredit(BigDecimal.ZERO);

            item.setOwed(owed);

            item.setNetBalance(
                    BigDecimal.ZERO.subtract(owed)
            );

            items.add(item);
        }

        /*
         * ------------------------------------------------------------
         * 4. OVERALL BALANCE
         * ------------------------------------------------------------
         */
        BigDecimal netBalance =
                totalUnallocatedCredit.subtract(totalOwed);

        StudentBalanceResponse response =
                new StudentBalanceResponse();

        response.setStudentId(studentId);
        response.setTotalCredit(totalUnallocatedCredit);
        response.setTotalOwed(totalOwed);
        response.setNetBalance(netBalance);
        response.setGroups(items);

        return response;
    }

    /**
     * Invoice bo'yicha hali qancha qarz borligini hisoblaydi.
     */
    private BigDecimal outstanding(Invoice invoice) {

        BigDecimal allocated =
                allocationRepository
                        .sumAllocatedByInvoiceId(invoice.getId());

        if (allocated == null) {
            allocated = BigDecimal.ZERO;
        }

        BigDecimal outstanding =
                invoice.netAmount()
                        .subtract(allocated);

        /*
         * Himoya:
         * normal holatda manfiy bo'lmasligi kerak.
         */
        return outstanding.max(BigDecimal.ZERO);
    }

    /**
     * Paymentning haqiqiy unallocated qoldig'i.
     * <p>
     * Formula:
     * <p>
     * payment amount
     * - allocated amount
     * - completed refunds
     * <p>
     * Misol:
     * <p>
     * Payment = 300k
     * Allocated = 200k
     * Refund = 50k
     * <p>
     * Unallocated = 50k
     * <p>
     * Yoki:
     * <p>
     * Payment = 300k
     * Allocated = 0
     * Refund = 100k
     * <p>
     * Unallocated = 200k
     */
    private BigDecimal getUnallocatedAmount(
            Payment payment
    ) {

        BigDecimal allocated =
                allocationRepository
                        .sumAllocatedByPaymentId(payment.getId());

        if (allocated == null) {
            allocated = BigDecimal.ZERO;
        }

        BigDecimal refunded =
                getAlreadyRefundedAmount(payment);

        BigDecimal unallocated =
                payment.getAmount()
                        .subtract(allocated)
                        .subtract(refunded);

        return unallocated.max(BigDecimal.ZERO);
    }

    /**
     * Original payment bo'yicha COMPLETED refundlar summasi.
     */
    private BigDecimal getAlreadyRefundedAmount(
            Payment original
    ) {

        return paymentRepository
                .findByRelatedPaymentId(original.getId())
                .stream()
                .filter(payment ->
                        payment.getPaymentStatus()
                                == PaymentStatus.COMPLETED
                )
                .map(Payment::getAmount)
                .reduce(
                        BigDecimal.ZERO,
                        BigDecimal::add
                );
    }

    /**
     * Payment va Invoice bir studentga va bir centerga tegishli
     * ekanligini tekshiradi.
     */
    private void validatePaymentInvoiceRelation(
            Payment payment,
            Invoice invoice
    ) {

        if (payment.getUserId() == null
                || invoice.getUserId() == null
                || !payment.getUserId()
                .equals(invoice.getUserId())) {

            throw new IllegalArgumentException(
                    "Payment va Invoice bir xil studentga tegishli bo'lishi kerak"
            );
        }

        if (payment.getCenterId() == null
                || invoice.getCenterId() == null
                || !payment.getCenterId()
                .equals(invoice.getCenterId())) {

            throw new IllegalArgumentException(
                    "Payment va Invoice bir xil centerga tegishli bo'lishi kerak"
            );
        }

        InvoiceType obligationType =
                resolveObligationType(payment);

        if (obligationType != null
                && invoice.getType() != obligationType) {

            throw new IllegalArgumentException(
                    "Payment turi ushbu Invoice turiga mos kelmaydi"
            );
        }
    }

    /**
     * Invoice allocation qabul qila olishini tekshiradi.
     */
    private void validateInvoiceCanReceiveAllocation(
            Invoice invoice,
            BigDecimal amount
    ) {

        if (invoice.getInvoiceStatus()
                == InvoiceStatus.CANCELLED) {

            throw new IllegalArgumentException(
                    "CANCELLED invoice'ga payment allocate qilib bo'lmaydi"
            );
        }

        if (invoice.getInvoiceStatus()
                == InvoiceStatus.PAID) {

            throw new IllegalArgumentException(
                    "PAID invoice'ga payment allocate qilib bo'lmaydi"
            );
        }

        BigDecimal invoiceOutstanding =
                outstanding(invoice);

        if (amount.compareTo(invoiceOutstanding) > 0) {

            throw new IllegalArgumentException(
                    "Allocation summasi invoice qarzidan ("
                            + invoiceOutstanding
                            + ") oshmasligi kerak"
            );
        }
    }

    /**
     * Payment COMPLETED bo'lishi kerak.
     */
    private void validateCompletedPayment(
            Payment payment
    ) {

        if (payment.getPaymentStatus()
                != PaymentStatus.COMPLETED) {

            throw new IllegalArgumentException(
                    "Faqat COMPLETED payment bilan ushbu amalni bajarish mumkin"
            );
        }
    }

    /**
     * Payment yaratishdagi basic validation.
     */
    private void validatePaymentRequest(
            CreatePaymentRequest request
    ) {

        if (request.getUserId() == null) {
            throw new IllegalArgumentException(
                    "User ID majburiy"
            );
        }

        if (request.getAmount() == null
                || request.getAmount()
                .compareTo(BigDecimal.ZERO) <= 0) {

            throw new IllegalArgumentException(
                    "Payment summasi 0 dan katta bo'lishi kerak"
            );
        }

        if (request.getType() == null) {
            throw new IllegalArgumentException(
                    "Payment type majburiy"
            );
        }

        if (request.getCategory() == null) {
            throw new IllegalArgumentException(
                    "Payment category majburiy"
            );
        }

        if (request.getMethod() == null) {
            throw new IllegalArgumentException(
                    "Payment method majburiy"
            );
        }
    }

    /**
     * Paymentni ID bo'yicha olish.
     */
    private Payment getPayment(UUID paymentId) {

        return paymentRepository.findById(paymentId)
                .orElseThrow(() ->
                        new EntityNotFoundException(
                                "Payment not found with id: "
                                        + paymentId
                        )
                );
    }

    /**
     * Invoice'ni ID bo'yicha olish.
     */
    private Invoice getInvoice(UUID invoiceId) {

        return invoiceRepository.findById(invoiceId)
                .orElseThrow(() ->
                        new EntityNotFoundException(
                                "Invoice not found with id: "
                                        + invoiceId
                        )
                );
    }

    /**
     * Invoice allocationlaridan kelib chiqib statusni qayta hisoblaydi.
     */
    private void recomputeInvoiceStatus(
            Invoice invoice
    ) {

        if (invoice.getInvoiceStatus()
                == InvoiceStatus.CANCELLED) {

            return;
        }

        BigDecimal allocated =
                allocationRepository
                        .sumAllocatedByInvoiceId(invoice.getId());

        if (allocated == null) {
            allocated = BigDecimal.ZERO;
        }

        BigDecimal net =
                invoice.netAmount();

        if (allocated.compareTo(net) >= 0) {

            invoice.setInvoiceStatus(
                    InvoiceStatus.PAID
            );

        } else if (allocated.compareTo(BigDecimal.ZERO) > 0) {

            invoice.setInvoiceStatus(
                    InvoiceStatus.PARTIALLY_PAID
            );

        } else {

            invoice.setInvoiceStatus(
                    InvoiceStatus.PENDING
            );
        }

        invoiceRepository.save(invoice);
    }
}