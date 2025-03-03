package com.example.gradingsystem.datamodel;

import java.util.HashMap;
import java.util.Map;

public class StudentResult {
    private String studentName;
    private Map<Task, Grade> grades;

    public StudentResult(String studentName) {
        this.studentName = studentName;
        this.grades = new HashMap<>();
    }

    public String getStudentName() {
        return studentName;
    }

    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }

    public void addGrade(Task task, int score) {
        grades.put(task, new Grade(score));
    }

    public Grade getGrade(Task task) {
        return grades.get(task);
    }

    public Map<Task, Grade> getAllGrades() {
        return grades;
    }

    @Override
    public String toString() {
        return studentName + " - Wyniki: " + grades;
    }
}


