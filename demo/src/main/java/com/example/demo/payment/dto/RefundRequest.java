package com.example.demo.payment.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Setter
public class RefundRequest {
    private UUID paymentId;
    private BigDecimal amount;
    private String note;
}
