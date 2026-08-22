package com.example.demo.payment.dto;

import com.example.demo.enums.CounterpartyType;
import com.example.demo.enums.FinanceCategory;
import com.example.demo.enums.PaymentMethod;
import com.example.demo.enums.PaymentStatus;
import com.example.demo.enums.PaymentType;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
public class PaymentDTO {
    private UUID id;
    private PaymentType type;
    private Short direction;
    private FinanceCategory category;
    private CounterpartyType counterpartyType;
    private Long userId;
    private String userFullName;
    private UUID groupId;
    private String vendorName;
    private BigDecimal amount;
    private PaymentMethod method;
    private LocalDateTime paidAt;
    private Long processedBy;
    private String note;
    private PaymentStatus paymentStatus;
    private UUID relatedPaymentId;
    private UUID centerId;
}
