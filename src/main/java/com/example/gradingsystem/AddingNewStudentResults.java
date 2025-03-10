package com.example.gradingsystem;

import com.example.gradingsystem.datamodel.Task;
import com.example.gradingsystem.datamodel.Test;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;

import java.util.List;

public class AddingNewStudentResults {
    @FXML
    private GridPane taskGrid;

    private Test selectedTest;

    public void setTest(Test test){
        this.selectedTest = test;
        loadTasks();
    }

    private void loadTasks() {
        List<Task> allTasks = selectedTest.getTasksOnTest();
        for (int i = 0; i < allTasks.size(); i++) {
            Label taskLabel = new Label(String.valueOf(allTasks.get(i).getNumberOfTask()));
            TextField pointsField = new TextField();
            pointsField.setPromptText("Punkty");

            Label maxPointsLabel = new Label(String.valueOf(allTasks.get(i).getMaxPoints()));

            taskGrid.add(taskLabel, 0, i + 1);
            taskGrid.add(pointsField, 1, i + 1);
            taskGrid.add(maxPointsLabel, 2, i + 1);
        }
    }
}
