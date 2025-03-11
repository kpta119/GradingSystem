package com.example.gradingsystem.datamodel;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import javafx.collections.ObservableList;

import java.io.File;
import java.io.IOException;
import java.time.format.DateTimeFormatter;

public class GradingSystemData {
    private static GradingSystemData instance = new GradingSystemData();
    private static String filename = "gradingSystem.json";
    private JsonClass jsonObject;
    private ObservableList<Test> testItems;
    private DateTimeFormatter formatter;

    public static GradingSystemData getInstance() {
        return instance;
    }

    public ObservableList<Test> getTestItems() {
        return testItems;
    }

    public String getFilename(){
        return filename;
    }

    private GradingSystemData() {
        formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        jsonObject = new JsonClass();
    }

    public void setTestItems(ObservableList<Test> testItems){
        this.testItems = testItems;
    }

    public DateTimeFormatter getDateFormatter() {
        return formatter;
    }

    public void writeTestItems() throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.enable(SerializationFeature.INDENT_OUTPUT);

        try {
            objectMapper.writeValue(new File(filename), testItems);
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }
    public void loadTestItems() throws Exception {
        jsonObject.loadTestItems();
    }

    public void addTestItem(Test newTest) {
        testItems.add(newTest);
    }
}