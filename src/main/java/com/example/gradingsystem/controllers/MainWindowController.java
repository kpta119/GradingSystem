package com.example.gradingsystem.controllers;

import com.example.gradingsystem.dao.TestDAO;
import com.example.gradingsystem.datamodel.Grade;
import com.example.gradingsystem.datamodel.StudentResult;
import com.example.gradingsystem.datamodel.Task;
import com.example.gradingsystem.datamodel.Test;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

import java.io.IOException;
import java.time.LocalDate;
import java.util.*;
import java.util.function.Predicate;

public class MainWindowController {
    @FXML
    private ListView<Test> testListView;
    @FXML
    private BorderPane mainBorderPane;
    @FXML
    private VBox vboxArea;
    @FXML
    private ContextMenu listContextMenu;
    @FXML
    private Button filterDateButton;
    @FXML
    private ImageView imageViewPlusLogo;

    private FilteredList<Test> filteredList;
    private Predicate<Test> wantAllTests;

    public void initialize() {
        listContextMenu = new ContextMenu();
        MenuItem deleteMenuTest = new MenuItem("Delete");
        deleteMenuTest.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                Test test = testListView.getSelectionModel().getSelectedItem();
                deleteTest(test);
            }
        });
        listContextMenu.getItems().addAll(deleteMenuTest);

        Image image = new Image(getClass().getResourceAsStream("/com/example/gradingsystem/images/plus-solid.png"));
        imageViewPlusLogo.setImage(image);

        wantAllTests = new Predicate<Test>() {
            @Override
            public boolean test(Test test) {
                return true;
            }
        };
        filteredList = new FilteredList<>(TestDAO.getInstance().getAllTests(), wantAllTests);

        testListView.setItems(filteredList);
        testListView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        testListView.getSelectionModel().selectedItemProperty().addListener((obs, oldTest, newTest) -> {
            if (newTest != null) {
                showTestGeneralStatistics();
            }
        });
        testListView.getSelectionModel().selectFirst();

        testListView.setCellFactory(listView -> {
            ListCell<Test> cell = new ListCell<>() {
                @Override
                protected void updateItem(Test item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty || item == null) {
                        setText(null);
                        setContextMenu(null);
                    } else {
                        setText(item.toString());
                        setContextMenu(listContextMenu);
                    }
                }
            };
            return cell;
        });
    }


    public void deleteTest(Test test) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Delete Test");
        List<Task>  taskOfTest = test.getTasksOnTest();
        StringBuilder sb = new StringBuilder();
        for (Task task : taskOfTest) {
            sb.append(String.format("\nTask %d:\n Type of Task: %s\n Maximal number of points: %d\n",
                    task.getNumberOfTask(), task.getType(), task.getMaxPoints()));
            sb.append("-------------------------------------\n");
        }
        alert.setHeaderText("Deleting test: " + test.toString() + sb.toString());
        alert.setContentText("Are you sure? Press OK to confirm, or cancel to back out");
        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            TestDAO.getInstance().deleteTest(test.getId());
        }
    }

    private void showTestGeneralStatistics() {
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
    }

    @FXML
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
        Optional<ButtonType> result = dialog.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            showTestGeneralStatistics();
        }
    }

    public void handleFilterDateButton(){
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Filter tests by date");
        dialog.initOwner(mainBorderPane.getScene().getWindow());

        DatePicker fromDatePicker = new DatePicker();
        DatePicker toDatePicker = new DatePicker();

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        grid.add(new Label("From:"), 0, 0);
        grid.add(fromDatePicker, 1, 0);
        grid.add(new Label("To:"), 0, 1);
        grid.add(toDatePicker, 1, 1);

        dialog.getDialogPane().setContent(grid);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        Optional<ButtonType> result = dialog.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            LocalDate fromDate = fromDatePicker.getValue();
            LocalDate toDate = toDatePicker.getValue();

            if (fromDate != null && toDate != null) {
                filterDateButton.setText("From: " + fromDate + " To: " + toDate);
                filterTestsByDate(fromDate, toDate);
            }
        }
    }

    private void filterTestsByDate(LocalDate fromDate, LocalDate toDate){
        Test selectedTest = testListView.getSelectionModel().getSelectedItem();
        filteredList.setPredicate(new Predicate<Test>() {
            @Override
            public boolean test(Test test) {
                if (test.getWhenTaken() == null){
                    return false;
                }
                return ((!test.getWhenTaken().isBefore(fromDate)) && (!test.getWhenTaken().isAfter(toDate)));
            }
        });
        if (filteredList.isEmpty()){
            vboxArea.getChildren().clear();
        } else if(filteredList.contains(selectedTest)){
            testListView.getSelectionModel().select(selectedTest);
        } else{
            testListView.getSelectionModel().selectFirst();
        }

    }
}


