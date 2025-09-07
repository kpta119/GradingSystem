package com.example.gradingsystem.controllers;

import com.example.gradingsystem.dao.StudentTestStats;
import com.example.gradingsystem.dao.TestDAO;
import com.example.gradingsystem.datamodel.TaskType;
import javafx.fxml.FXML;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;

import java.util.List;
import java.util.Map;


public class StudentsStats {
    @FXML
    private TextField searchField;
    @FXML
    private ListView<String> studentsListView;
    @FXML
    private VBox vboxArea;

    @FXML
    public void initialize() {
        studentsListView.getItems().addAll(TestDAO.getInstance().getStudents());
        studentsListView.getSelectionModel().selectFirst();

        studentsListView.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                showStudentChart(newVal);
            }
        });
    }

    public void onSearchClicked() {
        String query = searchField.getText().toLowerCase();
        studentsListView.getItems().setAll(TestDAO.getInstance()
                .getStudents(name -> name.contains(query)));
        vboxArea.getChildren().clear();
    }

    public void showStageForChosenStudent(String studentName){
        studentsListView.getSelectionModel().select(studentName);
        showStudentChart(studentName);
    }

    private void showStudentChart(String studentName) {
        vboxArea.getChildren().clear();

        List<StudentTestStats> studentsStats = TestDAO.getInstance().getStudentsStats(studentName);
        if (studentsStats.isEmpty()) return;

        NumberAxis xAxis1 = new NumberAxis();
        xAxis1.setLabel("Chronological Tests");

        NumberAxis yAxis1 = new NumberAxis(0, 100, 10);
        yAxis1.setLabel("Result %");

        LineChart<Number, Number> overallChart = new LineChart<>(xAxis1, yAxis1);
        overallChart.setTitle("Overall Progress: " + studentName);

        XYChart.Series<Number, Number> overallSeries = new XYChart.Series<>();
        overallSeries.setName("Overall %");

        int index = 1;
        for (StudentTestStats stats : studentsStats) {
            overallSeries.getData().add(new XYChart.Data<>(index, stats.getOverallPercentage()));
            index++;
        }
        overallChart.getData().add(overallSeries);
        vboxArea.getChildren().add(overallChart);

        for (TaskType type : TaskType.values()) {
            NumberAxis xAxis = new NumberAxis();
            xAxis.setLabel("Chronological Tests");

            NumberAxis yAxis = new NumberAxis(0, 100, 10);
            yAxis.setLabel(type + " %");

            LineChart<Number, Number> chart = new LineChart<>(xAxis, yAxis);
            chart.setTitle(type + " Progress");

            XYChart.Series<Number, Number> series = new XYChart.Series<>();
            series.setName(type + " %");

            index = 1;
            for (StudentTestStats stats : studentsStats) {
                Map<TaskType, Double> map = stats.getCategoryPercentages();
                if (map.containsKey(type)) {
                    double value = map.get(type);
                    series.getData().add(new XYChart.Data<>(index, value));
                    index++;
                }
            }

            chart.getData().add(series);
            vboxArea.getChildren().add(chart);
        }
    }
}

