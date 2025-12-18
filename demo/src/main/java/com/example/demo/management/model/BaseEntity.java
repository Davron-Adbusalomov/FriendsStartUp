package com.example.demo.management.model;

import com.example.demo.config.CurrentUserUtils;
import com.example.demo.enums.Status;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.FilterDef;
import org.hibernate.annotations.ParamDef;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Getter
@Setter
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
@FilterDef(
        name = "centerFilter",
        parameters = @ParamDef(name = "centerId", type = java.util.UUID.class)
)
public abstract class BaseEntity {

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "status", nullable = false)
    @Enumerated(EnumType.STRING)
    private Status status = Status.CREATED;

    @CreatedBy
    @Column(name = "created_by", updatable = false)
    private Long createdBy;

    @LastModifiedBy
    @Column(name = "updated_by")
    private Long updatedBy;

    @PrePersist
    @PreUpdate
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        createdBy = CurrentUserUtils.getUserId();
        status = Status.CREATED;

        handleDerivedFields();
    }

    public void updateEntity() {
        if (this.updatedAt == null) {
            this.updatedAt = LocalDateTime.now();
        }
        if (this.updatedBy == null) {
            this.updatedBy = 1L;
        }
        if (this.status == null) {
            this.status = Status.CREATED;
        }

        if (this.createdAt == null) {
            this.createdAt = LocalDateTime.now();
        }
        if (this.createdBy == null) {
            this.createdBy = 1L;
        }
    }

    public void handleDerivedFields() {
    }

}
