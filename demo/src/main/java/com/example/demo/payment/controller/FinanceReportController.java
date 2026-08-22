package com.example.demo.payment.controller;

import com.example.demo.payment.dto.DebtorResponse;
import com.example.demo.payment.dto.FinanceSummaryResponse;
import com.example.demo.payment.service.FinanceReportService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("api/v1/finance")
@RequiredArgsConstructor
public class FinanceReportController {

    private final FinanceReportService financeReportService;

    @Operation(summary = "Kirim/chiqim/net cash flow xulosasi", responses = {@ApiResponse(responseCode = "200", description = "Success")})
    @PreAuthorize("hasAnyAuthority('GET_FINANCE_SUMMARY')")
    @GetMapping("/summary")
    public FinanceSummaryResponse summary(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime to
    ) {
        return financeReportService.summary(from, to);
    }

    @Operation(summary = "Qarzdor o'quvchilar ro'yxati", responses = {@ApiResponse(responseCode = "200", description = "Success")})
    @PreAuthorize("hasAnyAuthority('GET_DEBTORS_LIST')")
    @GetMapping("/debtors")
    public List<DebtorResponse> debtors() {
        return financeReportService.debtors();
    }
}
