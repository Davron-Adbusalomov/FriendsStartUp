package com.example.demo.payment.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class DebtorResponse {
    private Long studentId;
    private String studentFullName;
    private BigDecimal totalOwed;
    private long overdueInvoiceCount;
}
