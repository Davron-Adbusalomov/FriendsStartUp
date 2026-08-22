package com.example.demo.payment.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

/**
 * O'quvchining joriy moliyaviy balansi: {@code netBalance > 0} bo'lsa — kredit (ortiqcha to'lagan,
 * keyingi oyga o'tadi), {@code netBalance < 0} bo'lsa — qarz (to'lanmagan invoice bor).
 */
@Getter
@Setter
public class StudentBalanceResponse {
    private Long studentId;
    private BigDecimal totalCredit;
    private BigDecimal totalOwed;
    private BigDecimal netBalance;
    private List<GroupBalanceItem> groups;
}
