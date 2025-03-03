package com.example.gradingsystem.datamodel;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.io.File;
import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class GradingSystemData {
    private static GradingSystemData instance = new GradingSystemData();
    private static String filename = "gradingSystem.txt";

    private List<Test> testItems;
    private DateTimeFormatter formatter;

    public static GradingSystemData getInstance() {
        return instance;
    }

    public List<Test> getTestItems() {
        return testItems;
    }

    private GradingSystemData() {
        formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
    }

    public void setTestItems(List<Test> testItems) {
        this.testItems = testItems;
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
}