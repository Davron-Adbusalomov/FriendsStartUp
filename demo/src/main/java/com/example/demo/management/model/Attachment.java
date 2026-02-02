package com.example.demo.management.model;

import com.example.demo.enums.AttachmentOwnerType;
import com.example.demo.enums.AttachmentType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Filter;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@SQLDelete(sql = "UPDATE attachment SET status = 'DELETED' WHERE id = ?")
@Where(clause = "status != 'DELETED'")
@Filter(name = "centerFilter", condition = "center_id = :centerId")
public class Attachment extends BaseEntity{
    @Id
    @GeneratedValue(generator = "uuid2")
    @org.hibernate.annotations.GenericGenerator(name = "uuid2", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(columnDefinition = "UUID")
    private UUID id;

    @Column(nullable = false)
    private String originalName;

    @Column(nullable = false)
    private String storedName;   // UUID.ext

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String contentType;   // application/pdf

    private long size;

    @Enumerated(EnumType.STRING)
    private AttachmentType type;

    @Enumerated(EnumType.STRING)
    private AttachmentOwnerType ownerType;

    private UUID ownerId; // lessonId, homeworkId, etc.

    private LocalDateTime uploadedAt;
}
