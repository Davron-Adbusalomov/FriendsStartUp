package com.example.demo.payment.controller;

import com.example.demo.enums.FinanceCategory;
import com.example.demo.enums.PaymentMethod;
import com.example.demo.enums.PaymentStatus;
import com.example.demo.enums.PaymentType;
import com.example.demo.payment.dto.AllocationRequest;
import com.example.demo.payment.dto.CreatePaymentRequest;
import com.example.demo.payment.dto.PaymentDTO;
import com.example.demo.payment.dto.RefundRequest;
import com.example.demo.payment.dto.StudentBalanceResponse;
import com.example.demo.payment.service.PaymentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.UUID;

@RestController
@RequestMapping("api/v1/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    @Operation(summary = "Record a cash receipt (kirim) or expense (chiqim)",
            responses = {@ApiResponse(responseCode = "200", description = "Success")})
    @PreAuthorize("hasAnyAuthority('CREATE_PAYMENT')")
    @PostMapping
    public PaymentDTO createPayment(@RequestBody CreatePaymentRequest request) {
        return paymentService.createPayment(request);
    }

    @Operation(summary = "Get list of payments", responses = {@ApiResponse(responseCode = "200", description = "Success")})
    @PreAuthorize("hasAnyAuthority('GET_PAYMENTS_LIST')")
    @GetMapping
    public Page<PaymentDTO> getAll(
            @RequestParam(required = false) PaymentType type,
            @RequestParam(required = false) FinanceCategory category,
            @RequestParam(required = false) Long userId,
            @RequestParam(required = false) PaymentMethod method,
            @RequestParam(required = false) PaymentStatus status,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime to,
            Pageable pageable
    ) {
        return paymentService.getAll(type, category, userId, method, status, from, to, pageable);
    }

    @Operation(summary = "Refund a completed payment (vozvrat)",
            responses = {@ApiResponse(responseCode = "200", description = "Success"), @ApiResponse(responseCode = "404", description = "Not found")})
    @PreAuthorize("hasAnyAuthority('REFUND_PAYMENT')")
    @PostMapping("/{id}/refund")
    public PaymentDTO refund(@PathVariable UUID id, @RequestBody RefundRequest request) {
        request.setPaymentId(id);
        return paymentService.refund(request);
    }

    @Operation(summary = "Void an incorrectly recorded payment",
            responses = {@ApiResponse(responseCode = "200", description = "Success"), @ApiResponse(responseCode = "404", description = "Not found")})
    @PreAuthorize("hasAnyAuthority('VOID_PAYMENT')")
    @PostMapping("/{id}/void")
    public PaymentDTO voidPayment(@PathVariable UUID id) {
        return paymentService.voidPayment(id);
    }

    @Operation(summary = "Mavjud (allocate qilinmagan) to'lovni ataylab ma'lum bir invoice'ga bog'lash",
            responses = {@ApiResponse(responseCode = "200", description = "Success"), @ApiResponse(responseCode = "404", description = "Not found")})
    @PreAuthorize("hasAnyAuthority('CREATE_PAYMENT')")
    @PostMapping("/{id}/allocate")
    public PaymentDTO allocate(@PathVariable UUID id, @RequestBody AllocationRequest request) {
        return paymentService.allocateToInvoice(id, request.getInvoiceId(), request.getAmount());
    }

    @Operation(summary = "O'quvchining joriy balansi (kredit/qarz, guruh bo'yicha va umumiy)",
            responses = {@ApiResponse(responseCode = "200", description = "Success")})
    @PreAuthorize("hasAnyAuthority('GET_PAYMENTS_LIST')")
    @GetMapping("/balance")
    public StudentBalanceResponse getBalance(@RequestParam Long studentId) {
        return paymentService.getStudentBalance(studentId);
    }
}
