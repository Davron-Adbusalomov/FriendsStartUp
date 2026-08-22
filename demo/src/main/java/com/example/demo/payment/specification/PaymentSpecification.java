package com.example.demo.payment.specification;

import com.example.demo.enums.FinanceCategory;
import com.example.demo.enums.PaymentMethod;
import com.example.demo.enums.PaymentStatus;
import com.example.demo.enums.PaymentType;
import com.example.demo.payment.model.Payment;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDateTime;
import java.util.UUID;

public class PaymentSpecification {

    public static Specification<Payment> hasCenterId(UUID centerId) {
        return (root, query, cb) -> centerId == null ? null : cb.equal(root.get("centerId"), centerId);
    }

    public static Specification<Payment> hasType(PaymentType type) {
        return (root, query, cb) -> type == null ? null : cb.equal(root.get("type"), type);
    }

    public static Specification<Payment> hasCategory(FinanceCategory category) {
        return (root, query, cb) -> category == null ? null : cb.equal(root.get("category"), category);
    }

    public static Specification<Payment> hasUserId(Long userId) {
        return (root, query, cb) -> userId == null ? null : cb.equal(root.get("userId"), userId);
    }

    public static Specification<Payment> hasMethod(PaymentMethod method) {
        return (root, query, cb) -> method == null ? null : cb.equal(root.get("method"), method);
    }

    public static Specification<Payment> hasStatus(PaymentStatus status) {
        return (root, query, cb) -> status == null ? null : cb.equal(root.get("paymentStatus"), status);
    }

    public static Specification<Payment> paidBetween(LocalDateTime from, LocalDateTime to) {
        return (root, query, cb) -> {
            if (from == null && to == null) return null;
            if (from == null) return cb.lessThanOrEqualTo(root.get("paidAt"), to);
            if (to == null) return cb.greaterThanOrEqualTo(root.get("paidAt"), from);
            return cb.between(root.get("paidAt"), from, to);
        };
    }

    public static Specification<Payment> advancedFilter(
            UUID centerId, PaymentType type, FinanceCategory category, Long userId,
            PaymentMethod method, PaymentStatus status, LocalDateTime from, LocalDateTime to) {
        return Specification
                .where(hasCenterId(centerId))
                .and(hasType(type))
                .and(hasCategory(category))
                .and(hasUserId(userId))
                .and(hasMethod(method))
                .and(hasStatus(status))
                .and(paidBetween(from, to));
    }
}
