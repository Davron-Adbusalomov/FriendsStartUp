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
@SQLDelete(sql = "UPDATE quiz_results SET status = 'DELETED' WHERE id = ?")
@Where(clause = "status != 'DELETED'")
@Filter(name = "centerFilter", condition = "center_id = :centerId")
public class QuizResults extends BaseEntity {
    @Id
    @GeneratedValue(generator = "uuid2")
    @org.hibernate.annotations.GenericGenerator(name = "uuid2", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(columnDefinition = "UUID")
    private UUID id;

    private Long mark;

    @Column(name = "quiz_id", nullable = false)
    private UUID quizId;

    @ManyToOne
    @JoinColumn(name = "quiz_id", insertable = false, updatable = false)
    private Quiz quiz;

    @Column(name = "student_id", nullable = false)
    private Long studentId;

    @Column(name = "group_id")
    private UUID groupId;

    @ManyToOne
    @JoinColumn(name = "student_id", insertable = false, updatable = false)
    private Student student;

    @Column(name = "center_id", nullable = false)
    private UUID centerId;

    @ManyToOne
    @JoinColumn(name = "center_id", referencedColumnName = "id", insertable = false, updatable = false)
    private Center center;

    public void assignStudent(Student student) {
        this.student = student;
    }

    public void assignQuiz(Quiz quiz) {
        this.quiz=quiz;
    }
}







