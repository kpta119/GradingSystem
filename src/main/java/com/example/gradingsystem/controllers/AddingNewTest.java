package com.example.gradingsystem.controllers;

import com.example.gradingsystem.dao.TestDAO;
import com.example.gradingsystem.datamodel.Task;
import com.example.gradingsystem.datamodel.TaskType;
import com.example.gradingsystem.datamodel.Test;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;

import java.time.LocalDate;
import java.util.Objects;

public class AddingNewTest {
    @FXML
    private TextField numberOfTaskField;

    @FXML
    private TextField testNameField;

    @FXML
    private DatePicker testDatePicker;

    @FXML
    private ListView<Task> taskListView;

    @FXML
    private TextField maxPointsField;

    @FXML
    private ComboBox<TaskType> categoryComboBox;

    private final ObservableList<Task> taskList = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        taskListView.setItems(taskList);
        categoryComboBox.setItems(FXCollections.observableArrayList(TaskType.values()));
    }

    @FXML
    private void handleAddTask() {
        String numberOfTask = numberOfTaskField.getText();
        String maxPoints = maxPointsField.getText();
        TaskType category = categoryComboBox.getValue();

        if (numberOfTask.isEmpty() || maxPoints.isEmpty() || Objects.isNull(category)) {
            System.out.println("Enter the task number, maximum number of points and select a category!");
            return;
        }

        int points;
        try {
            points = Integer.parseInt(maxPoints);
            if (points <= 0) {
                System.out.println("Error Number of points must be greater than 0!");
                return;
            }
        } catch (NumberFormatException e) {
            System.out.println("Error Maximum points must be a number!");
            return;
        }

        Task task = new Task(numberOfTask, points, category);
        taskList.add(task);

        numberOfTaskField.clear();
        maxPointsField.clear();
        categoryComboBox.getSelectionModel().clearSelection();
    }

    @FXML
    public void handleAddTest(){
        String testName = testNameField.getText();
        LocalDate dateOfTest = testDatePicker.getValue();
        if(testName.isEmpty() || dateOfTest == null) {
            System.out.println("The test must have a name and the date when it took place");
            return;
        }
        TestDAO.getInstance().insertTest(new Test(testName, dateOfTest, taskList));

        testNameField.clear();
        testDatePicker.setValue(null);
        taskList.clear();
    }

}
