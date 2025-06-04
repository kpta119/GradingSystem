package com.example.gradingsystem.datamodel;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;

class TestClassTest {

    private com.example.gradingsystem.datamodel.Test test;

    @BeforeEach
    void setup(){
        test = new com.example.gradingsystem.datamodel.Test();
        Task task1 = new Task(1,100, TaskType.LISTENING);
        Task task2 = new Task(2,20, TaskType.READING);
        Task task3 = new Task(3,50, TaskType.WRITING);
        test.setTasksOnTest(Arrays.asList(task1, task2, task3));
    }

    @Test
    void setName() {
        String newName = "English example test";
        test.setName(newName);
        assertEquals(newName, test.getName());
    }

    @Test
    void setWhenTaken() {
        LocalDate date = LocalDate.of(2023, 10, 5);
        test.setWhenTaken(date);
        DateTimeFormatter formatter = GradingSystemData.getInstance().getDateFormatter();
        assertEquals(date.format(formatter), test.getWhenTaken());
    }

    @Test
    void setTasksOnTest() {
        List<Task> newTasks = Collections.singletonList(new Task(4, 75, TaskType.USE_OF_ENGLISH));
        test.setTasksOnTest(newTasks);
        assertEquals(1, test.getTasksOnTest().size());
        assertEquals(75, test.getTasksOnTest().get(0).getMaxPoints());
    }

    @Test
    void setStudentResults() {
        String studentName = "John Doe";
        StudentResult studentResult = new StudentResult(studentName);
        Task task = test.getTasksOnTest().get(0);
        studentResult.addGrade(task, 85);
        test.addStudentResult(studentResult);

        assertEquals(1, test.getStudentResults().size());
        assertEquals(85, test.getStudentResults().get(0).getAllGrades().get(task).getScore());
    }
}