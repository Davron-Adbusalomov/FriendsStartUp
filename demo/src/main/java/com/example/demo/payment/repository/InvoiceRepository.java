package com.example.demo.payment.repository;

import com.example.demo.enums.InvoiceStatus;
import com.example.demo.enums.InvoiceType;
import com.example.demo.payment.model.Invoice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Repository
public interface InvoiceRepository extends JpaRepository<Invoice, UUID>, JpaSpecificationExecutor<Invoice> {

    boolean existsByTypeAndUserIdAndGroupIdAndPeriod(InvoiceType type, Long userId, UUID groupId, LocalDate period);

    boolean existsByTypeAndUserIdAndPeriod(InvoiceType type, Long userId, LocalDate period);

    List<Invoice> findByTypeAndUserIdAndGroupIdAndInvoiceStatusInOrderByPeriodAsc(
            InvoiceType type, Long userId, UUID groupId, List<InvoiceStatus> statuses);

    List<Invoice> findByTypeAndUserIdAndInvoiceStatusInOrderByPeriodAsc(
            InvoiceType type, Long userId, List<InvoiceStatus> statuses);

    List<Invoice> findByCenterIdAndInvoiceStatusIn(UUID centerId, List<InvoiceStatus> statuses);

    List<Invoice> findByCenterIdAndTypeAndInvoiceStatusIn(UUID centerId, InvoiceType type, List<InvoiceStatus> statuses);
}
