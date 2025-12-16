package com.example.demo.exam.model;

import com.example.demo.management.model.BaseEntity;
import com.example.demo.management.model.Center;
import com.example.demo.management.model.Student;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.*;

import java.util.UUID;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@SQLDelete(sql = "UPDATE written_questions SET status = 'DELETED' WHERE id = ?")
@Where(clause = "status != 'DELETED'")
@Filter(name = "centerFilter", condition = "center_id = :centerId")
public class WrittenQuestions extends BaseEntity {
    @Id
    @GeneratedValue(generator = "uuid2")
    @org.hibernate.annotations.GenericGenerator(name = "uuid2", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(columnDefinition = "UUID")
    private UUID id;

    private UUID questionId;

    private UUID quizId;

    private String studentAnswer;

    private Long max_score;

    private String questionTitle;

    private String correct_answer;

    private Long score;

    @Column(name = "center_id", nullable = false)
    private UUID centerId;

    @ManyToOne
    @JoinColumn(name = "center_id", referencedColumnName = "id", insertable = false, updatable = false)
    private Center center;

    @Column(name = "student_id", nullable = false)
    private Long studentId;

    @ManyToOne
    @JoinColumn(name = "student_id", insertable = false, updatable = false)
    private Student student;

}
