package com.example.gradingsystem;

import com.example.gradingsystem.datamodel.Task;
import com.example.gradingsystem.datamodel.TaskType;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;

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
            System.out.println("Błąd Wprowadź numer zadania, maksymalną liczbę punktów i wybierz kategorię!");
            return;
        }

        int points;
        int number;
        try {
            points = Integer.parseInt(maxPoints);
            number = Integer.parseInt(numberOfTask);
            if (points <= 0) {
                System.out.println("Błąd Liczba punktów musi być większa niż 0!");
                return;
            }
        } catch (NumberFormatException e) {
            System.out.println("Błąd Maksymalna liczba punktów musi być liczbą!");
            return;
        }

        Task task = new Task(number, points, category);
        taskList.add(task);

        numberOfTaskField.clear();
        maxPointsField.clear();
        categoryComboBox.getSelectionModel().clearSelection();
    }

    @FXML
    public void handleAddTest(){

    }

}
