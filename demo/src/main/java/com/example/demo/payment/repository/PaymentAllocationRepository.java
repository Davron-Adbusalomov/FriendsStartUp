package com.example.demo.payment.repository;

import com.example.demo.payment.model.PaymentAllocation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Repository
public interface PaymentAllocationRepository extends JpaRepository<PaymentAllocation, UUID> {

    List<PaymentAllocation> findByInvoiceId(UUID invoiceId);

    List<PaymentAllocation> findByPaymentId(UUID paymentId);

    @Query("select coalesce(sum(a.allocatedAmount), 0) from PaymentAllocation a where a.invoiceId = :invoiceId")
    BigDecimal sumAllocatedByInvoiceId(@Param("invoiceId") UUID invoiceId);

    @Query("select coalesce(sum(a.allocatedAmount), 0) from PaymentAllocation a where a.paymentId = :paymentId")
    BigDecimal sumAllocatedByPaymentId(@Param("paymentId") UUID paymentId);
}
