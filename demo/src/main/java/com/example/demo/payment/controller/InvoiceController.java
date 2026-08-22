package com.example.demo.payment.controller;

import com.example.demo.enums.InvoiceStatus;
import com.example.demo.enums.InvoiceType;
import com.example.demo.payment.dto.BillingSettingsDTO;
import com.example.demo.payment.dto.InvoiceDTO;
import com.example.demo.payment.service.InvoiceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.UUID;

@RestController
@RequestMapping("api/v1/invoices")
@RequiredArgsConstructor
public class InvoiceController {

    private final InvoiceService invoiceService;

    @Operation(summary = "Get list of invoices", responses = {@ApiResponse(responseCode = "200", description = "Success")})
    @PreAuthorize("hasAnyAuthority('GET_INVOICES_LIST')")
    @GetMapping
    public Page<InvoiceDTO> getAll(
            @RequestParam(required = false) InvoiceType type,
            @RequestParam(required = false) Long userId,
            @RequestParam(required = false) UUID groupId,
            @RequestParam(required = false) InvoiceStatus status,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate period,
            Pageable pageable
    ) {
        return invoiceService.getAll(type, userId, groupId, status, period, pageable);
    }

    @Operation(summary = "Get invoice by id", responses = {@ApiResponse(responseCode = "200", description = "Success"), @ApiResponse(responseCode = "404", description = "Not found")})
    @PreAuthorize("hasAnyAuthority('GET_INVOICE')")
    @GetMapping("/{id}")
    public InvoiceDTO getById(@PathVariable UUID id) {
        return invoiceService.getById(id);
    }

    @Operation(summary = "Manually generate this month's (or a given month's) invoices for the current center",
            responses = {@ApiResponse(responseCode = "200", description = "Success")})
    @PreAuthorize("hasAnyAuthority('GENERATE_INVOICES')")
    @PostMapping("/generate")
    public int generate(@RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM") YearMonth month) {
        return invoiceService.generate(month != null ? month : YearMonth.now());
    }

    @Operation(summary = "Cancel an invoice", responses = {@ApiResponse(responseCode = "200", description = "Success"), @ApiResponse(responseCode = "404", description = "Not found")})
    @PreAuthorize("hasAnyAuthority('CANCEL_INVOICE')")
    @PostMapping("/{id}/cancel")
    public InvoiceDTO cancel(@PathVariable UUID id) {
        return invoiceService.cancel(id);
    }

    @Operation(summary = "Shu student/teacherning bog'lanmagan (unallocated) oldingi to'lovlarini " +
            "ataylab shu invoice'ga qo'llash (avtomatik hech qachon ishlamaydi)",
            responses = {@ApiResponse(responseCode = "200", description = "Success"), @ApiResponse(responseCode = "404", description = "Not found")})
    @PreAuthorize("hasAnyAuthority('CREATE_PAYMENT')")
    @PostMapping("/{id}/apply-credit")
    public InvoiceDTO applyCredit(@PathVariable UUID id) {
        return invoiceService.applyCredit(id);
    }

    @Operation(summary = "Joriy centerning invoice avtomatik generatsiya kunini olish (1-31)",
            responses = {@ApiResponse(responseCode = "200", description = "Success")})
    @PreAuthorize("hasAnyAuthority('GET_BILLING_SETTINGS')")
    @GetMapping("/settings/billing-day")
    public BillingSettingsDTO getBillingSettings() {
        return invoiceService.getBillingSettings();
    }

    @Operation(summary = "Joriy centerning invoice avtomatik generatsiya kunini o'rnatish (1-31)",
            responses = {@ApiResponse(responseCode = "200", description = "Success"), @ApiResponse(responseCode = "400", description = "Bad request")})
    @PreAuthorize("hasAnyAuthority('UPDATE_BILLING_SETTINGS')")
    @PutMapping("/settings/billing-day")
    public BillingSettingsDTO updateBillingSettings(@RequestBody BillingSettingsDTO request) {
        return invoiceService.updateBillingSettings(request);
    }
}
