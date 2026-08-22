package com.example.demo.payment.model;

import com.example.demo.enums.CounterpartyType;
import com.example.demo.enums.FinanceCategory;
import com.example.demo.enums.PaymentMethod;
import com.example.demo.enums.PaymentStatus;
import com.example.demo.enums.PaymentType;
import com.example.demo.management.model.BaseEntity;
import com.example.demo.management.model.Center;
import com.example.demo.management.model.Grouping;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Filter;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Getter
@Setter
@Table(name = "payment")
@SQLDelete(sql = "UPDATE payment SET status = 'DELETED' WHERE id = ?")
@Where(clause = "status != 'DELETED'")
@Filter(name = "centerFilter", condition = "center_id = :centerId")
public class Payment extends BaseEntity {

    @Id
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(columnDefinition = "UUID")
    private UUID id;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    private PaymentType type;

    @Column(name = "direction", nullable = false)
    private Short direction;

    @Enumerated(EnumType.STRING)
    @Column(name = "category", nullable = false)
    private FinanceCategory category;

    @Enumerated(EnumType.STRING)
    @Column(name = "counterparty_type", nullable = false)
    private CounterpartyType counterpartyType;

    @Column(name = "user_id")
    private Long userId;

    @Column(name = "group_id")
    private UUID groupId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "group_id", insertable = false, updatable = false)
    private Grouping grouping;

    @Column(name = "vendor_name")
    private String vendorName;

    @Column(name = "amount", nullable = false)
    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    @Column(name = "method", nullable = false)
    private PaymentMethod method;

    @Column(name = "paid_at", nullable = false)
    private LocalDateTime paidAt;

    @Column(name = "processed_by")
    private Long processedBy;

    @Column(name = "note")
    private String note;

    @Enumerated(EnumType.STRING)
    @Column(name = "payment_status")
    private PaymentStatus paymentStatus = PaymentStatus.COMPLETED;

    @Column(name = "related_payment_id")
    private UUID relatedPaymentId;

    @Column(name = "center_id", nullable = false)
    private UUID centerId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "center_id", referencedColumnName = "id", insertable = false, updatable = false)
    private Center center;

    /** Signed contribution of this payment to cash flow: amount * direction. */
    public BigDecimal signedAmount() {
        return amount.multiply(BigDecimal.valueOf(direction));
    }
}
