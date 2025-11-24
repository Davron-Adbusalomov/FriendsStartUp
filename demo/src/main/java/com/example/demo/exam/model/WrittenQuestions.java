package com.example.demo.exam.model;

import com.example.demo.management.model.BaseEntity;
import com.example.demo.management.model.Student;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import java.util.UUID;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@SQLDelete(sql = "UPDATE written_questions SET status = 'DELETED' WHERE id = ?")
@Where(clause = "status != 'DELETED'")
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

    @ManyToOne
    @JoinColumn(name = "student_id")
    private Student student;

}
