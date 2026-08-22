package com.example.demo.payment.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Setter
public class AllocationRequest {
    private UUID invoiceId;
    private BigDecimal amount;
}
