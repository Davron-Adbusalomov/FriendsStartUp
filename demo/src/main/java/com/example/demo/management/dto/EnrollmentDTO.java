package com.example.demo.management.dto;

import com.example.demo.enums.EnrollmentStatus;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.UUID;

@Getter
@Setter
public class EnrollmentDTO {
    private UUID id;

    private Long studentId;

    private UUID groupId;

    private EnrollmentStatus enrollmentStatus = EnrollmentStatus.REQUESTED;

    private Date enrollmentDate;

    private StudentDTO student;

    private GroupDTO grouping;

    private UUID centerId;
}
