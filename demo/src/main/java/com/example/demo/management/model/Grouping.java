package com.example.demo.management.model;

import jakarta.persistence.*;
import com.example.demo.management.model.Course;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;
// Quiz list removed — quizzes now belong to Course

@Entity
@Getter
@Setter
@Table(name = "groups")
@SQLDelete(sql = "UPDATE groups SET status = 'DELETED' WHERE id = ?")
@Where(clause = "status != 'DELETED'")
@Filter(name = "centerFilter", condition = "center_id = :centerId")
public class Grouping extends BaseEntity {
    @Id
    @GeneratedValue(generator = "uuid2")
    @org.hibernate.annotations.GenericGenerator(name = "uuid2", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(columnDefinition = "UUID")
    private UUID id;

    private String name;

    private String image;

    private String time;

    private String description;

    private Date startDate;

    private Integer durationInMonths;

    @Column(name = "subject_id")
    private UUID subjectId;

    @Column(name = "teacher_id")
    private Long teacherId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "teacher_id", insertable = false, updatable = false)
    private Teacher teacher;

    @ManyToMany
    @JoinTable(
            name = "group_student",
            joinColumns = @JoinColumn(name = "group_id"),
            inverseJoinColumns = @JoinColumn(name = "student_id"))
    private List<Student> students = new ArrayList<>();

    @Column(name = "course_id")
    private UUID courseId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_id", insertable = false, updatable = false)
    private Course course;

    @Column(name = "center_id", nullable = false)
    private UUID centerId;

    @ManyToOne
    @JoinColumn(name = "center_id", referencedColumnName = "id", insertable = false, updatable = false)
    private Center center;

    public void assignStudent(Student student) {
        students.add(student);
    }

    public void deassignStudent(Student student) {
        students.remove(student);
    }

    public void assignTeacher(Teacher teacher) {
        this.teacher = teacher;
    }
}