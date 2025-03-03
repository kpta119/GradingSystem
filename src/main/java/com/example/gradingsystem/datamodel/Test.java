package com.example.gradingsystem.datamodel;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Test {
    private LocalDate whenTaken;
    private String name;
    private List<Task> tasksOnTest;
    private List<StudentResult> studentResults;


    public Test(String name, LocalDate whenTaken) {
        this.name = name;
        this.whenTaken = whenTaken;
        this.tasksOnTest = new ArrayList<>();
        this.studentResults = new ArrayList<>();
    }
    public Test(){}


    public void setName(String name) {
        this.name = name;
    }

    public void setWhenTaken(LocalDate whenTaken) {
        this.whenTaken = whenTaken;
    }

    public void setTasksOnTest(List<Task> tasksOnTest) {
        this.tasksOnTest = tasksOnTest;
    }

    public void setStudentResults(List<StudentResult> studentResults) {
        this.studentResults = studentResults;
    }

    public List<StudentResult> getStudentResults() {
        return studentResults;
    }

    public List<Task> getTasksOnTest() {
        return tasksOnTest;
    }

    public String getName() {
        return name;
    }

    public LocalDate getWhenTaken() {
        return whenTaken;
    }

    public void addTask(Task task){
        tasksOnTest.add(task);
    }

    public void addStudentResult(StudentResult result) {
        studentResults.add(result);
    }

    @Override
    public String toString() {
        return name + " - " + whenTaken;
    }
}
