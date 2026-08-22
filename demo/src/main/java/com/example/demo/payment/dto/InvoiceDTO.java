package com.example.demo.payment.dto;

import com.example.demo.enums.InvoiceStatus;
import com.example.demo.enums.InvoiceType;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Getter
@Setter
public class InvoiceDTO {
    private UUID id;
    private InvoiceType type;
    private Long userId;
    private String userFullName;
    private UUID groupId;
    private String groupName;
    private String vendorName;
    private LocalDate period;
    private BigDecimal amount;
    private BigDecimal discountAmount;
    private BigDecimal paidAmount;
    private LocalDate dueDate;
    private String description;
    private InvoiceStatus invoiceStatus;
    private Boolean isTrial;
    private UUID centerId;
}
