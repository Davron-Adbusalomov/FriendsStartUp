package com.example.demo.exam.model;

import com.example.demo.management.model.BaseEntity;
import com.example.demo.management.model.Center;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.Filter;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Data
@SQLDelete(sql = "UPDATE student_answer SET status = 'DELETED' WHERE id = ?")
@Where(clause = "status != 'DELETED'")
@Filter(name = "centerFilter", condition = "center_id = :centerId")
public class StudentAnswer extends BaseEntity {
    @Id
    @GeneratedValue(generator = "uuid2")
    @org.hibernate.annotations.GenericGenerator(name = "uuid2", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(columnDefinition = "UUID")
    private UUID id;

    private Long studentId;
    private UUID quizId;
    private UUID questionId;

    @Column(length = 4000)
    private String answer;

    private Boolean correct;
    private Integer score;

    private Long gradedBy;
    private LocalDateTime gradedAt;

    @Column(name = "center_id")
    private UUID centerId;

    @ManyToOne
    @JoinColumn(name = "center_id", insertable = false, updatable = false)
    private Center center;
}
