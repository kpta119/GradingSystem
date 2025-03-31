package com.example.gradingsystem;

import com.example.gradingsystem.datamodel.*;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;

import java.io.IOException;
import java.net.URL;
import java.util.*;

public class MainWindowController {
    @FXML
    private ListView<Test> testListView;
    @FXML
    private BorderPane mainBorderPane;
    @FXML
    private TextArea testDetailsTextArea;


    public void initialize() {
        testListView.setItems(GradingSystemData.getInstance().getTestItems());
        testListView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        testListView.getSelectionModel().selectedItemProperty().addListener((obs, oldTest, newTest) -> {
            if (newTest != null) {
                showTestStatistics();
            }
        });
    }


    private void showTestStatistics(){
        Test selectedTest = testListView.getSelectionModel().getSelectedItem();
        if (selectedTest == null) {
            testDetailsTextArea.setText("Nie wybrano testu.");
            return;
        }

        StringBuilder stats = new StringBuilder("Statystyki dla testu: " + selectedTest.getName() + "\n");

        List<Task> taskFromSelectedTest = selectedTest.getTasksOnTest();
        List<StudentResult> resultsFromSelectedTest = selectedTest.getStudentResults();
        for (Task task : taskFromSelectedTest) {
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


            stats.append(String.format("\nZadanie: %s ", task.getNumberOfTask()));
            stats.append(String.format("Maksymalna liczba punktów: %d\n", task.getMaxPoints()));
            stats.append(String.format("Najlepszy uczeń: %s (%d) / %d \n", bestStudent, maxScore, task.getMaxPoints()));
            stats.append(String.format("Najgorszy uczeń: %s (%d) / %d\n", worstStudent, minScore, task.getMaxPoints()));
            stats.append(String.format("Średnia: %.2f\n", mean));
            stats.append(String.format("Odchylenie standardowe: %.2f\n", stdDev));
        }

        testDetailsTextArea.setText(stats.toString());
    }


    @FXML
    public void showNewTestDialog() {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.initOwner(mainBorderPane.getScene().getWindow());
        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(getClass().getResource("addingNewTest.fxml"));
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

    public void showNewStudentResultsDialog() {
        Test chosenTest = testListView.getSelectionModel().getSelectedItem();
        if (chosenTest == null) {
            System.out.println("Nie wybrano testu!");
            return;
        }

        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.initOwner(mainBorderPane.getScene().getWindow());
        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(getClass().getResource("addingNewStudentResults.fxml"));
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
        dialog.showAndWait();
    }

    public void showDeleteTestDialog(){
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.initOwner(mainBorderPane.getScene().getWindow());
        FXMLLoader fxmlLoader = new FXMLLoader();
        URL fxmlResource = getClass().getResource("deletingTest.fxml");
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

