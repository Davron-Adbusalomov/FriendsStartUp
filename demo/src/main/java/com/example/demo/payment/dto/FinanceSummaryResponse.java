package com.example.demo.payment.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Map;

@Getter
@Setter
public class FinanceSummaryResponse {
    private BigDecimal totalIncome;
    private BigDecimal totalExpense;
    private BigDecimal netCashFlow;
    private BigDecimal totalOutstanding;
    private Map<String, BigDecimal> incomeByMethod;
    private Map<String, BigDecimal> amountByCategory;
}
