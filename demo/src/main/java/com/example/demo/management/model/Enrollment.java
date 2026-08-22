package com.example.demo.management.model;

import com.example.demo.enums.EnrollmentStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import java.math.BigDecimal;
import java.util.Date;
import java.util.UUID;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
@SQLDelete(sql = "UPDATE enrollment SET deleted = true WHERE id = ?")
@Where(clause = "deleted = false")
public class Enrollment extends BaseEntity {
    @Id
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(columnDefinition = "UUID")
    private UUID id;

    @Column(name = "student_id", nullable = false)
    private Long studentId;

    @ManyToOne
    @JoinColumn(name = "student_id", insertable = false, updatable = false)
    private Student student;

    @Column(name = "group_id", nullable = false)
    private UUID groupId;

    @ManyToOne
    @JoinColumn(name = "group_id", insertable = false, updatable = false)
    private Grouping grouping;

    @Column(name = "enrollment_status")
    private EnrollmentStatus enrollmentStatus = EnrollmentStatus.REQUESTED;

    @Column(name = "enrollment_date")
    private Date enrollmentDate;

    @Column(name = "custom_fee")
    private BigDecimal customFee;

    @Column(name = "trial_lessons_granted")
    private Integer trialLessonsGranted = 0;

    @Column(name = "trial_lessons_used")
    private Integer trialLessonsUsed = 0;

    @Column(name = "center_id", nullable = false)
    private UUID centerId;

    @ManyToOne
    @JoinColumn(name = "center_id", referencedColumnName = "id", insertable = false, updatable = false)
    private Center center;

}
