package com.example.gradingsystem.controllers;

import com.example.gradingsystem.dao.TestDAO;
import javafx.fxml.FXML;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;



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

    private void showStudentChart(String studentName) {
        vboxArea.getChildren().clear();

        NumberAxis xAxis = new NumberAxis();
        NumberAxis yAxis = new NumberAxis();
        LineChart<Number, Number> lineChart = new LineChart<>(xAxis, yAxis);
        lineChart.setTitle("Progress: " + studentName);

        XYChart.Series<Number, Number> series = new XYChart.Series<>();
        series.setName("Results");
        series.getData().add(new XYChart.Data<>(1, 50));
        series.getData().add(new XYChart.Data<>(2, 65));
        series.getData().add(new XYChart.Data<>(3, 80));
        series.getData().add(new XYChart.Data<>(4, 90));

        lineChart.getData().add(series);

        vboxArea.getChildren().add(lineChart);
    }
}

