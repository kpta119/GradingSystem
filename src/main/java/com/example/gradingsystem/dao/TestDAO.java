package com.example.gradingsystem.dao;

import com.example.gradingsystem.datamodel.MongoConnector;
import com.example.gradingsystem.datamodel.TaskType;
import com.example.gradingsystem.datamodel.Test;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.bson.Document;

public class TestDAO {

    private static TestDAO instance;
    private final MongoCollection<Document> testCollection;

    public TestDAO() {
        MongoDatabase database = MongoConnector.getInstance().getDatabase();
        this.testCollection = database.getCollection("tests");
    }

    public static TestDAO getInstance() {
        if (instance == null) {
            instance = new TestDAO();
        }
        return instance;
    }

    public ObservableList<Test> getAllTests(){
        ObservableList<Test> tests = FXCollections.observableArrayList();
        for (Document doc : testCollection.find()) {
            tests.add(Test.fromDocument(doc));
        }
        return tests;
    }

    public void insertTest(Test test) {
        Document doc = test.toDocument();
        testCollection.insertOne(doc);
    }

    public static Document createTask(int numberOfTask, int maxPoints, TaskType type) {
        return new Document()
                .append("numberOfTask", numberOfTask)
                .append("maxPoints", maxPoints)
                .append("type", type);
    }


}

