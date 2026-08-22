package com.example.demo.payment.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Setter
public class GroupBalanceItem {
    private UUID groupId;
    private String groupName;
    private BigDecimal credit;
    private BigDecimal owed;
    private BigDecimal netBalance;
}
