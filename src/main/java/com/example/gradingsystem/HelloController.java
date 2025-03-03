package com.example.gradingsystem;

import com.example.gradingsystem.datamodel.GradingSystemData;
import com.example.gradingsystem.datamodel.StudentResult;
import com.example.gradingsystem.datamodel.TaskType;
import com.example.gradingsystem.datamodel.Test;
import com.example.gradingsystem.datamodel.Task;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class HelloController {
    private List<Test> testList = new ArrayList<>();
    @FXML
    private ListView testListView;


    public void initialize() {
        Test englishTest = new Test("English", LocalDate.of(2025, 3, 1));

        Task task1 = new Task(1, 10, TaskType.LISTENING);
        Task task2 = new Task(2, 15, TaskType.USE_OF_ENGLISH);
        Task task3 = new Task(3, 20, TaskType.WRITING);

        englishTest.addTask(task1);
        englishTest.addTask(task2);
        englishTest.addTask(task3);

        StudentResult student1 = new StudentResult("John Doe");
        student1.addGrade(task1, 8);
        student1.addGrade(task2, 12);
        student1.addGrade(task3, 18);

        StudentResult student2 = new StudentResult("Jane Smith");
        student2.addGrade(task1, 7);
        student2.addGrade(task2, 14);
        student2.addGrade(task3, 16);


        englishTest.addStudentResult(student1);
        englishTest.addStudentResult(student2);
        testList.add(englishTest);

        GradingSystemData.getInstance().setTestItems(testList);
        testListView.getItems().setAll(GradingSystemData.getInstance().getTestItems());
        testListView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
    }

}