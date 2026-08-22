package com.example.demo.payment.repository;

import com.example.demo.enums.FinanceCategory;
import com.example.demo.enums.PaymentStatus;
import com.example.demo.enums.PaymentType;
import com.example.demo.payment.model.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, UUID>, JpaSpecificationExecutor<Payment> {

    List<Payment> findByTypeAndCategoryAndUserIdAndGroupIdAndPaymentStatusOrderByPaidAtAsc(
            PaymentType type, FinanceCategory category, Long userId, UUID groupId, PaymentStatus paymentStatus);

    List<Payment> findByTypeAndCategoryAndUserIdAndPaymentStatusOrderByPaidAtAsc(
            PaymentType type, FinanceCategory category, Long userId, PaymentStatus paymentStatus);

    List<Payment> findByRelatedPaymentId(UUID relatedPaymentId);

    @Query("select coalesce(sum(p.amount), 0) from Payment p where p.type = 'RECEIPT' and p.category = 'TUITION_FEE' " +
            "and p.paymentStatus = 'COMPLETED' and p.paidAt >= :from and p.paidAt < :to " +
            "and p.groupId in (select g.id from Grouping g where g.teacherId = :teacherId)")
    BigDecimal sumTuitionIncomeForTeacherGroups(
            @Param("teacherId") Long teacherId, @Param("from") LocalDateTime from, @Param("to") LocalDateTime to);
}
