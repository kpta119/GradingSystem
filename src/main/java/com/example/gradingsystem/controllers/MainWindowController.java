package com.example.gradingsystem.controllers;

import com.example.gradingsystem.dao.TestDAO;
import com.example.gradingsystem.datamodel.Grade;
import com.example.gradingsystem.datamodel.StudentResult;
import com.example.gradingsystem.datamodel.Task;
import com.example.gradingsystem.datamodel.Test;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

import java.io.IOException;
import java.net.URL;
import java.util.*;

public class MainWindowController {
    @FXML
    private ListView<Test> testListView;
    @FXML
    private BorderPane mainBorderPane;
    @FXML
    private VBox vboxArea;


    public void initialize() {
        testListView.setItems(TestDAO.getInstance().getAllTests());
        testListView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        testListView.getSelectionModel().selectedItemProperty().addListener((obs, oldTest, newTest) -> {
            if (newTest != null) {
                showTestGeneralStatistics();
            }
        });
    }


    private void showTestGeneralStatistics(){
        Test selectedTest = testListView.getSelectionModel().getSelectedItem();
        VBox.setVgrow(vboxArea, Priority.ALWAYS);
        vboxArea.getChildren().clear();
        vboxArea.setMinHeight(Region.USE_PREF_SIZE);
        Label testHeader = new Label();

        if (selectedTest == null) {
            testHeader.setText("No test has been selected!");
            vboxArea.getChildren().add(testHeader);
            return;
        }

        String headerTestDescription = "Test statistics: " + selectedTest.getName() + "\n";
        testHeader.setText(headerTestDescription);

        List<Task> taskFromSelectedTest = selectedTest.getTasksOnTest();
        List<StudentResult> resultsFromSelectedTest = selectedTest.getStudentResults();

        for (Task task : taskFromSelectedTest) {
            StringBuilder stats = new StringBuilder();
            List<Integer> scores = new ArrayList<>();
            Map<Integer, String> scoreToStudent = new HashMap<>();

            for (StudentResult studentRes : resultsFromSelectedTest) {
                if (studentRes.getAllGrades().containsKey(task)) {
                    Grade grade = studentRes.getAllGrades().get(task);
                    int score = grade.getScore();
                    scores.add(score);
                    scoreToStudent.put(score, studentRes.getStudentName());
                }
            }

            if (scores.isEmpty()) continue;

            int maxScore = Collections.max(scores);
            int minScore = Collections.min(scores);
            String bestStudent = scoreToStudent.get(maxScore);
            String worstStudent = scoreToStudent.get(minScore);

            double mean = scores.stream().mapToInt(Integer::intValue).average().orElse(0);
            double variance = scores.stream().mapToDouble(s -> Math.pow(s - mean, 2)).average().orElse(0);
            double stdDev = Math.sqrt(variance);

            VBox taskContainer = new VBox();
            VBox.setVgrow(taskContainer, Priority.NEVER);
           // taskContainer.setPadding(new Insets(10, 0, 20, 0));
            taskContainer.setStyle("-fx-border-color: lightgray; -fx-border-radius: 5; -fx-padding: 10;");

            Label headerTaskLabel = new Label("Task " + task.getNumberOfTask());
            headerTaskLabel.getStyleClass().add("task-header");

            stats.append(String.format("Max Points: %d\n", task.getMaxPoints()));
            stats.append(String.format("The best student: %s (%d) / %d \n", bestStudent, maxScore, task.getMaxPoints()));
            stats.append(String.format("The worst student: %s (%d) / %d\n", worstStudent, minScore, task.getMaxPoints()));
            stats.append(String.format("Average: %.2f\n", mean));
            stats.append(String.format("Standard deviation: %.2f\n", stdDev));

            TextFlow statsFlow = new TextFlow();
            statsFlow.setMaxWidth(300);
            statsFlow.setPadding(new Insets(5));
            statsFlow.getStyleClass().add("stats-flow");
            statsFlow.setMaxWidth(Double.MAX_VALUE);

            Text statsText = new Text(stats.toString());
            statsText.getStyleClass().add("stats-text");
            statsFlow.getChildren().add(statsText);
            taskContainer.getChildren().addAll(headerTaskLabel, statsFlow);
            vboxArea.getChildren().add(taskContainer);
        }
    }


    @FXML
    public void showNewTestDialog() {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.initOwner(mainBorderPane.getScene().getWindow());
        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(getClass().getResource("/com/example/gradingsystem/addingNewTest.fxml"));
        try {
            dialog.getDialogPane().setContent(fxmlLoader.load());
        } catch (IOException e) {
            System.out.println("Couldn't load the dialog");
            return;
        }
        dialog.getDialogPane().getButtonTypes().add(ButtonType.OK);
        dialog.getDialogPane().getButtonTypes().add(ButtonType.CANCEL);
        Optional<ButtonType> result = dialog.showAndWait();
        if (result.isPresent()) {
            testListView.setItems(TestDAO.getInstance().getAllTests());
        }
    }

    public void showNewStudentResultsDialog() {
        Test chosenTest = testListView.getSelectionModel().getSelectedItem();
        if (chosenTest == null) {
            System.out.println("No test has been selected!");
            return;
        }

        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.initOwner(mainBorderPane.getScene().getWindow());
        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(getClass().getResource("/com/example/gradingsystem/addingNewStudentResults.fxml"));
        try {
            dialog.getDialogPane().setContent(fxmlLoader.load());

            AddingNewStudentResults controller = fxmlLoader.getController();
            controller.setTest(chosenTest);

        } catch (IOException e) {
            System.out.println("Couldn't load the dialog");
            return;
        }
        dialog.getDialogPane().getButtonTypes().add(ButtonType.OK);
        dialog.getDialogPane().getButtonTypes().add(ButtonType.CANCEL);
        Optional<ButtonType> result =  dialog.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            showTestGeneralStatistics();
        }
    }

    public void showDeleteTestDialog(){
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.initOwner(mainBorderPane.getScene().getWindow());
        FXMLLoader fxmlLoader = new FXMLLoader();
        URL fxmlResource = getClass().getResource("/com/example/gradingsystem/deletingTest.fxml");
        if (fxmlResource == null) {
            System.out.println("FXML file not found!");
        } else {
            fxmlLoader.setLocation(fxmlResource);
        }

        try {
            dialog.getDialogPane().setContent(fxmlLoader.load());
        } catch (IOException e) {
            System.out.println("Couldn't load the dialog");
            return;
        }
        dialog.getDialogPane().getButtonTypes().add(ButtonType.OK);
        dialog.getDialogPane().getButtonTypes().add(ButtonType.CANCEL);
        dialog.showAndWait();
    }
}

