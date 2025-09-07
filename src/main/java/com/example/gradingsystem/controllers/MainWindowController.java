package com.example.gradingsystem.controllers;

import com.example.gradingsystem.dao.TaskStatistics;
import com.example.gradingsystem.dao.TestDAO;
import com.example.gradingsystem.datamodel.Grade;
import com.example.gradingsystem.datamodel.StudentResult;
import com.example.gradingsystem.datamodel.Task;
import com.example.gradingsystem.datamodel.Test;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.jetbrains.annotations.NotNull;

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
    @FXML
    private ImageView imageViewRollback;
    @FXML
    private Button newTestButton;

    private final ObservableList<Test> baseList = FXCollections.observableArrayList(TestDAO.getInstance().getTests());
    private FilteredList<Test> filteredList;
    private SortedList<Test> sortedList;
    private Predicate<Test> wantAllTests;

    public void initialize() {
        listContextMenu = new ContextMenu();
        MenuItem deleteMenuTest = new MenuItem("Delete");
        deleteMenuTest.setOnAction(new EventHandler<>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                Test test = testListView.getSelectionModel().getSelectedItem();
                deleteTest(test);
            }
        });
        listContextMenu.getItems().addAll(deleteMenuTest);

        HBox.setMargin(newTestButton, new Insets(0, 20, 0, 0));
        HBox.setMargin(filterDateButton, new Insets(0, 8, 0, 0));
        Image imagePlus = new Image(getClass().getResourceAsStream("/com/example/gradingsystem/images/plus-solid.png"));
        imageViewPlusLogo.setImage(imagePlus);

        Image imageRollback = new Image(getClass().getResourceAsStream("/com/example/gradingsystem/images/rollback.png"));
        imageViewRollback.setImage(imageRollback);



        wantAllTests = test -> true;
        filteredList = new FilteredList<>(baseList, wantAllTests);

        sortedList = new SortedList<>(filteredList, new Comparator<>() {
            @Override
            public int compare(Test o1, Test o2) {
                return o1.getWhenTaken().compareTo(o2.getWhenTaken());
            }
        });

        testListView.setItems(sortedList);
        testListView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        testListView.getSelectionModel().selectedItemProperty().addListener((obs, oldTest, newTest) -> {
            if (newTest != null) {
                showTestGeneralStatistics();
            }
        });
        testListView.getSelectionModel().selectFirst();

        testListView.setCellFactory(listView -> {
            return new ListCell<>() {
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
        });
    }

    private Label createHeader(Test test){
        String headerText = "Test statistics: " + test.getName() + "\n";
        Label header = new Label(headerText);
        header.setFont(Font.font("Times", FontWeight.BOLD, 20));
        header.setTextFill(Color.BLACK);
        return header;
    }

    private TaskStatistics calculateStatistics(Task task, List<StudentResult> results) {
        List<Integer> scores = new ArrayList<>();
        Map<Integer, String> scoreToStudent = new HashMap<>();

        for (StudentResult res : results) {
            Grade grade = res.getAllGrades().get(task);
            if (grade != null) {
                int score = grade.getScore();
                scores.add(score);
                scoreToStudent.put(score, res.getStudentName());
            }
        }

        if (scores.isEmpty()) {
            return new TaskStatistics(false, 0, 0, "", "", 0, 0);
        }

        int max = Collections.max(scores);
        int min = Collections.min(scores);
        double mean = scores.stream().mapToInt(i -> i).average().orElse(0);
        double variance = scores.stream().mapToDouble(s -> Math.pow(s - mean, 2)).average().orElse(0);

        return new TaskStatistics(true, max, min, scoreToStudent.get(max), scoreToStudent.get(min), mean, Math.sqrt(variance));
    }

    private VBox createTaskStatistics(Task task, List<StudentResult> results) {
        TaskStatistics stats = calculateStatistics(task, results);

        VBox taskContainer = new VBox();
        taskContainer.getStyleClass().add("task-container");

        Label headerTaskLabel = new Label("Task " + task.getNumberOfTask() + " " + task.getType().name());
        headerTaskLabel.getStyleClass().add("task-header");

        if (stats.hasResults()) {
            String text = String.format(
                    "Max Points: %d\nThe best student: %s (%d) / %d\nThe worst student: %s (%d) / %d\nAverage: %.2f\nStandard deviation: %.2f",
                    task.getMaxPoints(),
                    stats.bestStudent(), stats.maxScore(), task.getMaxPoints(),
                    stats.worstStudent(), stats.minScore(), task.getMaxPoints(),
                    stats.mean(), stats.stdDev()
            );
            TextFlow statsFlow = new TextFlow(new Text(text));
            statsFlow.getStyleClass().add("stats-flow");
            taskContainer.getChildren().addAll(headerTaskLabel, statsFlow);
        } else {
            Label noResultsLabel = new Label("No Results available");
            noResultsLabel.getStyleClass().add("no-results-label");
            taskContainer.getChildren().addAll(headerTaskLabel, noResultsLabel);
        }

        return taskContainer;
    }

    private Button createDetailedResultsButton(Test test){
        Button showDetailedResultsButton = new Button();
        showDetailedResultsButton.setText("Show detailed test results");
        showDetailedResultsButton.getStyleClass().add("green-button");
        showDetailedResultsButton.setOnAction(event -> openDetailedResultsWindow(test));
        return showDetailedResultsButton;
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

        vboxArea.getChildren().add(createHeader(selectedTest));

        List<Task> taskFromSelectedTest = selectedTest.getTasksOnTest();
        for (Task task : taskFromSelectedTest) {
            VBox taskStatsVbox = createTaskStatistics(task, selectedTest.getStudentResults());
            vboxArea.getChildren().add(taskStatsVbox);

            }
        vboxArea.getChildren().add(createDetailedResultsButton(selectedTest));
    }

    public void deleteTest(@NotNull Test test) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Delete Test");
        List<Task>  taskOfTest = test.getTasksOnTest();
        StringBuilder sb = new StringBuilder();
        for (Task task : taskOfTest) {
            sb.append(String.format("\nTask %s:\n Type of Task: %s\n Maximal number of points: %d\n",
                    task.getNumberOfTask(), task.getType(), task.getMaxPoints()));
            sb.append("-------------------------------------\n");
        }
        alert.setHeaderText("Deleting test: " + test + sb);
        alert.setContentText("Are you sure? Press OK to confirm, or cancel to back out");
        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            TestDAO.getInstance().deleteTest(test.getId());
            setTestListViewToFilteredListWantAllTests();
            filterDateButton.setText("All tests");
        }
    }


    @FXML
    public void showNewTestDialog() {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.initOwner(mainBorderPane.getScene().getWindow());
        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(getClass().getResource("/com/example/gradingsystem/views/addingNewTest.fxml"));
        try {
            dialog.getDialogPane().setContent(fxmlLoader.load());
        } catch (IOException e) {
            System.out.println("Couldn't load the dialog");
            return;
        }
        dialog.getDialogPane().getButtonTypes().add(ButtonType.OK);
        dialog.getDialogPane().getButtonTypes().add(ButtonType.CANCEL);
        Optional<ButtonType> result = dialog.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            setTestListViewToFilteredListWantAllTests();
            filterDateButton.setText("All tests");
        }
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
        fxmlLoader.setLocation(getClass().getResource("/com/example/gradingsystem/views/addingNewStudentResults.fxml"));
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

    @FXML
    public StudentsStats showStudentsStatsWindow() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/gradingsystem/views/studentsStats.fxml"));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.setTitle("Students Statistics");
            stage.setScene(new Scene(root, 1500, 1000));

            StudentsStats controller = loader.getController();
            stage.initOwner(mainBorderPane.getScene().getWindow());
            stage.initModality(Modality.APPLICATION_MODAL);

            stage.show();
            return controller;

        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    @FXML
    public void deleteStudentResults() {
        Test chosenTest = testListView.getSelectionModel().getSelectedItem();
        if (chosenTest == null) {
            System.out.println("No test has been selected!");
            return;
        }
        openDetailedResultsWindow(chosenTest);
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
                setTestListViewToFilteredListBasedOnDates(fromDate, toDate);
            }
        }
    }

    public void rollbackToAllTests(){
        setTestListViewToFilteredListWantAllTests();
        filterDateButton.setText("All tests");
    }


    private void setTestListViewToFilteredListWantAllTests(){
        Test selectedTest = testListView.getSelectionModel().getSelectedItem();
        ObservableList<Test> allTests = TestDAO.getInstance().getTests();
        baseList.setAll(allTests);
        filteredList.setPredicate(wantAllTests);
        testListView.setItems(sortedList);
        refreshItemsOnTestListView(selectedTest);
    }

    private void setTestListViewToFilteredListBasedOnDates(LocalDate fromDate, LocalDate toDate){
        Test selectedTest = testListView.getSelectionModel().getSelectedItem();
        filteredList.setPredicate(test -> {
            if (test.getWhenTaken() == null){
                return false;
            }
            return ((!test.getWhenTaken().isBefore(fromDate)) && (!test.getWhenTaken().isAfter(toDate)));
        });
        testListView.setItems(sortedList);
        refreshItemsOnTestListView(selectedTest);
    }

    private void openDetailedResultsWindow(Test selectedTest) {
        Stage stage = new Stage();
        stage.setTitle("Detailed Results");

        TableView<StudentResult> tableView = new TableView<>();
        tableView.setEditable(true);

        TableColumn<StudentResult, String> nameCol = new TableColumn<>("Name");
        nameCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getStudentName()));
        nameCol.setCellFactory(col -> {
            TableCell<StudentResult, String> cell = new TableCell<>() {
                @Override
                protected void updateItem(String item, boolean empty) {
                    super.updateItem(item, empty);
                    setText(empty ? null : item);
                }
            };

            cell.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && !cell.isEmpty()) {
                    StudentResult sr = cell.getTableRow().getItem();
                    StudentsStats controller = showStudentsStatsWindow();
                    if (controller != null) {
                        controller.showStageForChosenStudent(sr.getStudentName());
                    }
                }
            });

            return cell;
        });
        tableView.getColumns().add(nameCol);

        List<StudentResult> studentResults = selectedTest.getStudentResults();
        Set<Task> allTasks = new LinkedHashSet<>();
        for (StudentResult sr : studentResults) {
            allTasks.addAll(sr.getAllGrades().keySet());
        }

        List<Task> sortedTask = allTasks.stream().sorted(new Comparator<>() {
            @Override
            public int compare(Task o1, Task o2) {
                return o1.getNumberOfTask().compareTo(o2.getNumberOfTask());
            }
        }).toList();

        for (Task task : sortedTask) {
            TableColumn<StudentResult, String> taskCol = getStudentResultStringTableColumn(selectedTest, task, tableView);
            tableView.getColumns().add(taskCol);
        }

        TableColumn<StudentResult, Void> deleteCol = getStudentResultDeleteTableColumn(selectedTest, tableView);
        tableView.getColumns().add(deleteCol);

        tableView.setItems(FXCollections.observableArrayList(studentResults));
        tableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        VBox layout = new VBox(tableView);
        Scene scene = new Scene(layout, 1200, 900);
        scene.getStylesheets().add(getClass().getResource("/com/example/gradingsystem/styles/styles.css").toExternalForm());
        stage.setScene(scene);

        stage.initModality(Modality.APPLICATION_MODAL);
        stage.show();
        stage.setOnHiding(e -> {
            rollbackToAllTests();
        });
    }

    @NotNull
    private static TableColumn<StudentResult, String> getStudentResultStringTableColumn(Test selectedTest, Task task, TableView<StudentResult> tableView) {
        TableColumn<StudentResult, String> taskCol = new TableColumn<>("Task " + task.getNumberOfTask() + "\n" + task.getType().name());
        taskCol.setCellValueFactory(data -> {
            Grade grade = data.getValue().getAllGrades().get(task);
            String scoreStr = grade != null ? String.valueOf(grade.getScore()) : "-";
            return new SimpleStringProperty(scoreStr);
        });
        taskCol.setCellFactory(TextFieldTableCell.forTableColumn());
        taskCol.setOnEditCommit(event -> {
            StudentResult sr = event.getRowValue();
            String newScoreStr = event.getNewValue();

            try {
                int newScore = Integer.parseInt(newScoreStr);
                sr.editGrade(task, newScore);
                String studentName = sr.getStudentName();
                TestDAO.getInstance().updateGradeInStudentResult(selectedTest.getId(),studentName, task.toString(), newScore );
            } catch (NumberFormatException e) {
                System.out.println("Invalid format: " + newScoreStr);
            } catch (RuntimeException e) {
                System.out.println(e.getMessage());
            }

            tableView.refresh();

        });

        taskCol.setStyle("-fx-alignment: CENTER");
        return taskCol;
    }

    @NotNull
    private TableColumn<StudentResult, Void> getStudentResultDeleteTableColumn(Test selectedTest, TableView<StudentResult> tableView) {
        TableColumn<StudentResult, Void> deleteCol = new TableColumn<>("Delete");
        deleteCol.setCellFactory(col -> new TableCell<>() {
            private final Button deleteBtn = new Button("Delete results");

            {
                deleteBtn.setOnAction(event -> {
                    StudentResult sr = getTableView().getItems().get(getIndex());
                    selectedTest.getStudentResults().remove(sr);
                    tableView.getItems().remove(sr);
                    TestDAO.getInstance().deleteStudentResult(selectedTest.getId(), sr.getStudentName());
                    setTestListViewToFilteredListWantAllTests();
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(deleteBtn);
                }
            }
        });
        deleteCol.setStyle("-fx-alignment: CENTER");
        return deleteCol;
    }

    private void refreshItemsOnTestListView(Test selectedTest){
        if (filteredList.isEmpty()){
            vboxArea.getChildren().clear();
        } else if(filteredList.contains(selectedTest)){
            testListView.getSelectionModel().select(selectedTest);
        } else{
            testListView.getSelectionModel().selectFirst();
        }
    }
}


