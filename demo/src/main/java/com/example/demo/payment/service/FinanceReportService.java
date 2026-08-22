package com.example.demo.payment.service;

import com.example.demo.config.TenantContext;
import com.example.demo.enums.InvoiceStatus;
import com.example.demo.enums.InvoiceType;
import com.example.demo.enums.PaymentStatus;
import com.example.demo.enums.PaymentType;
import com.example.demo.management.model.Student;
import com.example.demo.management.repository.StudentRepository;
import com.example.demo.payment.dto.DebtorResponse;
import com.example.demo.payment.dto.FinanceSummaryResponse;
import com.example.demo.payment.model.Invoice;
import com.example.demo.payment.model.Payment;
import com.example.demo.payment.repository.InvoiceRepository;
import com.example.demo.payment.repository.PaymentAllocationRepository;
import com.example.demo.payment.repository.PaymentRepository;
import com.example.demo.payment.specification.PaymentSpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FinanceReportService {

    private final PaymentRepository paymentRepository;
    private final InvoiceRepository invoiceRepository;
    private final PaymentAllocationRepository allocationRepository;
    private final StudentRepository studentRepository;

    public FinanceSummaryResponse summary(LocalDateTime from, LocalDateTime to) {
        var centerId = TenantContext.getCenterId();
        var spec = PaymentSpecification.advancedFilter(centerId, null, null, null, null, null, from, to);
        List<Payment> payments = paymentRepository.findAll(spec).stream()
                .filter(p -> p.getPaymentStatus() != PaymentStatus.VOID)
                .toList();

        BigDecimal totalIncome = sumWhere(payments, p -> p.getType() == PaymentType.RECEIPT);
        BigDecimal totalExpense = sumWhere(payments, p -> p.getType() == PaymentType.EXPENSE);
        BigDecimal netCashFlow = payments.stream()
                .map(Payment::signedAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        Map<String, BigDecimal> incomeByMethod = payments.stream()
                .filter(p -> p.getType() == PaymentType.RECEIPT)
                .collect(Collectors.groupingBy(
                        p -> p.getMethod().name(),
                        LinkedHashMap::new,
                        Collectors.reducing(BigDecimal.ZERO, Payment::getAmount, BigDecimal::add)));

        Map<String, BigDecimal> amountByCategory = payments.stream()
                .collect(Collectors.groupingBy(
                        p -> p.getCategory().name(),
                        LinkedHashMap::new,
                        Collectors.reducing(BigDecimal.ZERO, Payment::signedAmount, BigDecimal::add)));

        List<Invoice> outstandingInvoices = invoiceRepository.findByCenterIdAndInvoiceStatusIn(
                centerId, List.of(InvoiceStatus.PENDING, InvoiceStatus.PARTIALLY_PAID));
        BigDecimal totalOutstanding = outstandingInvoices.stream()
                .map(this::outstandingAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        FinanceSummaryResponse response = new FinanceSummaryResponse();
        response.setTotalIncome(totalIncome);
        response.setTotalExpense(totalExpense);
        response.setNetCashFlow(netCashFlow);
        response.setTotalOutstanding(totalOutstanding);
        response.setIncomeByMethod(incomeByMethod);
        response.setAmountByCategory(amountByCategory);
        return response;
    }

    public List<DebtorResponse> debtors() {
        var centerId = TenantContext.getCenterId();
        List<Invoice> outstanding = invoiceRepository.findByCenterIdAndTypeAndInvoiceStatusIn(
                centerId, InvoiceType.STUDENT_TUITION, List.of(InvoiceStatus.PENDING, InvoiceStatus.PARTIALLY_PAID));

        Map<Long, List<Invoice>> byStudent = outstanding.stream()
                .collect(Collectors.groupingBy(Invoice::getUserId));

        LocalDate today = LocalDate.now();
        return byStudent.entrySet().stream().map(entry -> {
            Long studentId = entry.getKey();
            List<Invoice> invoices = entry.getValue();

            DebtorResponse response = new DebtorResponse();
            response.setStudentId(studentId);
            response.setTotalOwed(invoices.stream().map(this::outstandingAmount).reduce(BigDecimal.ZERO, BigDecimal::add));
            response.setOverdueInvoiceCount(invoices.stream()
                    .filter(i -> i.getDueDate() != null && i.getDueDate().isBefore(today))
                    .count());
            studentRepository.findById(studentId).map(Student::getFullName).ifPresent(response::setStudentFullName);
            return response;
        }).sorted((a, b) -> b.getTotalOwed().compareTo(a.getTotalOwed())).toList();
    }

    /** O'qituvchi dashboardi uchun: shu o'qituvchi guruhlaridan berilgan oyda yig'ilgan o'quv to'lovi. */
    public BigDecimal monthlyIncomeForTeacher(Long teacherId, YearMonth month) {
        LocalDateTime from = month.atDay(1).atStartOfDay();
        LocalDateTime to = month.plusMonths(1).atDay(1).atStartOfDay();
        return paymentRepository.sumTuitionIncomeForTeacherGroups(teacherId, from, to);
    }

    private BigDecimal outstandingAmount(Invoice invoice) {
        BigDecimal allocated = allocationRepository.sumAllocatedByInvoiceId(invoice.getId());
        return invoice.netAmount().subtract(allocated);
    }

    private BigDecimal sumWhere(List<Payment> payments, java.util.function.Predicate<Payment> predicate) {
        return payments.stream().filter(predicate).map(Payment::getAmount).reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
