package com.example.demo.payment.model;

import com.example.demo.enums.InvoiceStatus;
import com.example.demo.enums.InvoiceType;
import com.example.demo.management.model.BaseEntity;
import com.example.demo.management.model.Center;
import com.example.demo.management.model.Grouping;
import com.example.demo.management.model.UserEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Filter;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Getter
@Setter
@Table(name = "invoice")
@SQLDelete(sql = "UPDATE invoice SET status = 'DELETED' WHERE id = ?")
@Where(clause = "status != 'DELETED'")
@Filter(name = "centerFilter", condition = "center_id = :centerId")
public class Invoice extends BaseEntity {

    @Id
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(columnDefinition = "UUID")
    private UUID id;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    private InvoiceType type;

    @Column(name = "user_id")
    private Long userId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", insertable = false, updatable = false)
    private UserEntity user;

    @Column(name = "group_id")
    private UUID groupId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "group_id", insertable = false, updatable = false)
    private Grouping grouping;

    @Column(name = "vendor_name")
    private String vendorName;

    @Column(name = "period")
    private LocalDate period;

    @Column(name = "amount", nullable = false)
    private BigDecimal amount;

    @Column(name = "discount_amount")
    private BigDecimal discountAmount = BigDecimal.ZERO;

    @Column(name = "due_date")
    private LocalDate dueDate;

    @Column(name = "description")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(name = "invoice_status")
    private InvoiceStatus invoiceStatus = InvoiceStatus.PENDING;

    @Column(name = "is_trial")
    private Boolean isTrial = false;

    @Column(name = "center_id", nullable = false)
    private UUID centerId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "center_id", referencedColumnName = "id", insertable = false, updatable = false)
    private Center center;

    public BigDecimal netAmount() {
        BigDecimal discount = discountAmount != null ? discountAmount : BigDecimal.ZERO;
        return amount.subtract(discount);
    }
}
