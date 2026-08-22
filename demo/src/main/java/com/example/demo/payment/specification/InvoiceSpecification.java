package com.example.demo.payment.specification;

import com.example.demo.enums.InvoiceStatus;
import com.example.demo.enums.InvoiceType;
import com.example.demo.payment.model.Invoice;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;
import java.util.UUID;

public class InvoiceSpecification {

    public static Specification<Invoice> hasCenterId(UUID centerId) {
        return (root, query, cb) -> centerId == null ? null : cb.equal(root.get("centerId"), centerId);
    }

    public static Specification<Invoice> hasType(InvoiceType type) {
        return (root, query, cb) -> type == null ? null : cb.equal(root.get("type"), type);
    }

    public static Specification<Invoice> hasUserId(Long userId) {
        return (root, query, cb) -> userId == null ? null : cb.equal(root.get("userId"), userId);
    }

    public static Specification<Invoice> hasGroupId(UUID groupId) {
        return (root, query, cb) -> groupId == null ? null : cb.equal(root.get("groupId"), groupId);
    }

    public static Specification<Invoice> hasStatus(InvoiceStatus status) {
        return (root, query, cb) -> status == null ? null : cb.equal(root.get("invoiceStatus"), status);
    }

    public static Specification<Invoice> hasPeriod(LocalDate period) {
        return (root, query, cb) -> period == null ? null : cb.equal(root.get("period"), period);
    }

    public static Specification<Invoice> advancedFilter(
            UUID centerId, InvoiceType type, Long userId, UUID groupId, InvoiceStatus status, LocalDate period) {
        return Specification
                .where(hasCenterId(centerId))
                .and(hasType(type))
                .and(hasUserId(userId))
                .and(hasGroupId(groupId))
                .and(hasStatus(status))
                .and(hasPeriod(period));
    }
}
