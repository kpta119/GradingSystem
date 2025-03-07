package com.example.gradingsystem;

import com.example.gradingsystem.datamodel.GradingSystemData;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.layout.BorderPane;

import java.io.IOException;
import java.util.Optional;

public class MainWindowController {
//    private List<Test> testList = new ArrayList<>();
    @FXML
    private ListView testListView;
    @FXML
    private BorderPane mainBorderPane;


    public void initialize() {
//        Test englishTest = new Test("English", LocalDate.of(2025, 3, 1));
//        Test englishTest2 = new Test("English2", LocalDate.of(2025, 3, 22));
//        Task task1 = new Task(1, 10, TaskType.LISTENING);
//        Task task2 = new Task(2, 15, TaskType.USE_OF_ENGLISH);
//        Task task3 = new Task(3, 20, TaskType.WRITING);
//        Task task4 = new Task(3, 20, TaskType.WRITING);
//        englishTest.addTask(task1);
//        englishTest.addTask(task2);
//        englishTest.addTask(task3);
//        englishTest2.addTask(task4);
//        StudentResult student1 = new StudentResult("John Doe");
//        student1.addGrade(task1, 8);
//        student1.addGrade(task2, 12);
//        student1.addGrade(task3, 18);
//
//        StudentResult student2 = new StudentResult("Jane Smith");
//        student2.addGrade(task1, 7);
//        student2.addGrade(task2, 14);
//        student2.addGrade(task3, 16);
//
//        StudentResult student3 = new StudentResult("Jane Smith");
//        student3.addGrade(task4, 15);
//
//        englishTest.addStudentResult(student1);
//        englishTest.addStudentResult(student2);
//        englishTest2.addStudentResult(student3);
//        testList.add(englishTest);
//        testList.add(englishTest2);
//
//        GradingSystemData.getInstance().setTestItems(testList);
        testListView.getItems().setAll(GradingSystemData.getInstance().getTestItems());
        testListView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
    }

    @FXML
    public void showNewTestDialog(){
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.initOwner(mainBorderPane.getScene().getWindow());
        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(getClass().getResource("addingNewTest.fxml"));
        try {
            dialog.getDialogPane().setContent(fxmlLoader.load());
        } catch(IOException e){
            System.out.println("Couldn't load the dialog");
            return;
        }
        dialog.getDialogPane().getButtonTypes().add(ButtonType.OK);
        dialog.getDialogPane().getButtonTypes().add(ButtonType.CANCEL);

        Optional<ButtonType> result = dialog.showAndWait();
        if(result.isPresent() && result.get() == ButtonType.OK){
            System.out.println("Ok pressed");
        }
        else{
            System.out.println("Cancel pressed");
        }

    }

}