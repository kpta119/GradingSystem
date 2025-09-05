package com.example.gradingsystem.dao;

import com.example.gradingsystem.datamodel.TaskType;

import java.time.LocalDate;
import java.util.Map;

public class StudentTestStats {
    private LocalDate date;
    private double overallPercentage;
    private Map<TaskType, Double> categoryPercentages;

    public StudentTestStats(LocalDate date, double overallPercentage, Map<TaskType, Double> categoryPercentages) {
        this.date = date;
        this.overallPercentage = overallPercentage;
        this.categoryPercentages = categoryPercentages;
    }

    public LocalDate getDate() {
        return date;
    }

    public double getOverallPercentage() {
        return overallPercentage;
    }

    public Map<TaskType, Double> getCategoryPercentages() {
        return categoryPercentages;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public void setOverallPercentage(double overallPercentage) {
        this.overallPercentage = overallPercentage;
    }

    public void setCategoryPercentages(Map<TaskType, Double> categoryPercentages) {
        this.categoryPercentages = categoryPercentages;
    }
}
