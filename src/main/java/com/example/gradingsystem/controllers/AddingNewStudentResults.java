package com.example.gradingsystem.controllers;

import com.example.gradingsystem.dao.TestDAO;
import com.example.gradingsystem.datamodel.StudentResult;
import com.example.gradingsystem.datamodel.Task;
import com.example.gradingsystem.datamodel.Test;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;

import java.util.List;

public class AddingNewStudentResults {
    @FXML
    private GridPane taskGrid;
    @FXML
    private TextField studentNameField;
    @FXML
    private Label testTitleLabel;

    private Test selectedTest;

    public void setTest(Test test) {
        this.selectedTest = test;
        testTitleLabel.setText(test.toString());
        loadTasks();
    }

    private void loadTasks() {
        List<Task> allTasks = selectedTest.getTasksOnTest();
        for (int i = 0; i < allTasks.size(); i++) {
            Label taskLabel = new Label(String.valueOf(allTasks.get(i).getNumberOfTask()));
            TextField pointsField = new TextField();
            pointsField.setPromptText("Points");

            Label maxPointsLabel = new Label(String.valueOf(allTasks.get(i).getMaxPoints()));

            taskGrid.add(taskLabel, 0, i + 1);
            taskGrid.add(pointsField, 1, i + 1);
            taskGrid.add(maxPointsLabel, 2, i + 1);
        }
    }

    @FXML
    public void handleAddingStudentResults() {
        List<Task> allTasks = selectedTest.getTasksOnTest();
        String studentName = studentNameField.getText();
        StudentResult studentResult = new StudentResult(studentName);
        boolean hasErrors = false;

        for (Node node : taskGrid.getChildren()) {
            if (GridPane.getColumnIndex(node) != null && GridPane.getColumnIndex(node) == 1
                    && node instanceof TextField textField) {

                int rowIndex = GridPane.getRowIndex(node) != null ? GridPane.getRowIndex(node) - 1 : -1;
                if (rowIndex < 0 || rowIndex >= allTasks.size()) continue;

                Task task = allTasks.get(rowIndex);
                String text = textField.getText().trim();

                try {
                    int score = Integer.parseInt(text);
                    if (score < 0 || score > task.getMaxPoints()) {
                        System.out.println("Incorrect number of points for the task " + task.getNumberOfTask());
                        hasErrors = true;
                        break;
                    }
                    studentResult.addGrade(task, score);
                } catch (NumberFormatException e) {
                    System.out.println("Invalid number format in the problem " + task.getNumberOfTask());
                    hasErrors = true;
                    break;
                }
            }
        }

        if (!hasErrors) {
            selectedTest.addStudentResult(studentResult);
            TestDAO.getInstance().addStudentResultToTest(selectedTest.getId(), studentResult);

            studentNameField.clear();

        }

        for (Node node : taskGrid.getChildren()) {
            if (node instanceof TextField textField) {
                textField.clear();
            }
        }
        studentNameField.clear();
    }
}
