package com.example.gradingsystem.controllers;

import com.example.gradingsystem.dao.ClassDAO;
import com.example.gradingsystem.datamodel.Student;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;

public class AddingNewClass {
    @FXML
    private ListView<String> classListView;

    @FXML
    private TextField classNameTextField;

    @FXML
    private TextField fullNameTextField;

    private final ObservableList<String > studentsList = FXCollections.observableArrayList();

    public void initialize() {
        classListView.setItems(studentsList);
    }

    @FXML
    public void handleAddStudentToClass() {
        String className = classNameTextField.getText().trim();
        String fullName = fullNameTextField.getText().trim();

        if (className.isEmpty() || fullName.isEmpty()){
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Invalid input");
            alert.setHeaderText(null);
            alert.setContentText("Class name and student name cannot be empty!");
            alert.showAndWait();
            return;
        }

        Student student = new Student(fullName, className);
        ClassDAO.getInstance().addStudentToClass(className, student);

        studentsList.add(fullName);
        fullNameTextField.clear();
    }
}
