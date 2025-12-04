package com.example.demo.exam.model;

import com.example.demo.management.model.BaseEntity;
import com.example.demo.management.model.Grouping;
import com.example.demo.management.model.Teacher;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@SQLDelete(sql = "UPDATE quiz SET status = 'DELETED' WHERE id = ?")
@Where(clause = "status != 'DELETED'")
public class Quiz extends BaseEntity{

    @Id
    @GeneratedValue(generator = "uuid2")
    @org.hibernate.annotations.GenericGenerator(name = "uuid2", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(columnDefinition = "UUID")
    private UUID id;

    private String title;

    private int questions_num;

    private Long duration;

    private LocalDateTime startTime;

    @Column(name = "grouping_id")
    private UUID groupingId;

    @ManyToOne
    @JsonIgnore
    @JoinColumn(name = "grouping_id", insertable = false, updatable = false)
    private Grouping grouping;

    @Column(name = "teacher_id", nullable = false)
    private Long teacherId;

    @ManyToOne
    @JoinColumn(name = "teacher_id", insertable = false, updatable = false)
    @JsonIgnoreProperties("quizzes")
    private Teacher teacher;

    @ManyToMany
    @JoinTable(
            name = "quiz_question",
            joinColumns = @JoinColumn(name = "quiz_id"),
            inverseJoinColumns = @JoinColumn(name = "question_id")
    )
    private List<Question> questions = new ArrayList<>();

    @OneToMany(mappedBy = "quiz", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Quiz_Results> quizResult;

    public void assignQuestion(Question question) {
        questions.add(question);
    }
}
