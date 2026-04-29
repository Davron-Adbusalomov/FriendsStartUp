package com.example.demo.management.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class StatsDTO {
    private int totalClasses;
    private double classesIncrease;
    private int totalStudents;
    private double studentsIncrease;
    private double totalSalary;
    private double salaryIncrease;
    private double totalIncome;
    private double incomeIncrease;
}
