package com.example.gradingsystem;

import com.example.gradingsystem.datamodel.GradingSystemData;
import com.example.gradingsystem.datamodel.Task;
import com.example.gradingsystem.datamodel.Test;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TextArea;

import java.util.List;

public class DeletingTest {
    @FXML
    private ListView<Test> testListView;
    @FXML
    private TextArea testTextArea;

    public void initialize() {
        testListView.setItems(GradingSystemData.getInstance().getTestItems());
        testListView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);

        testListView.getSelectionModel().selectedItemProperty().addListener((obs, oldTest, newTest) -> {
            if (newTest != null) {
                updateTestDetails(newTest);
            }
        });

        if (!testListView.getItems().isEmpty()) {
            testListView.getSelectionModel().selectFirst();
        }
    }

    public void updateTestDetails(Test newTest){
        List<Task> taskOfTest = newTest.getTasksOnTest();
        StringBuilder sb = new StringBuilder();
        for (Task task : taskOfTest) {
            sb.append(String.format("Zadanie %d:\n Typ zadania: %s\n Maksymalna liczba punktów: %d\n",
                    task.getNumberOfTask(), task.getType(), task.getMaxPoints()));
            sb.append("-------------------------------------\n");
        }
        testTextArea.setText(sb.toString());
    }
}
