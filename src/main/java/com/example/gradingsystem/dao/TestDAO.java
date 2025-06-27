package com.example.gradingsystem.dao;

import com.example.gradingsystem.MongoConnector;
import com.example.gradingsystem.datamodel.StudentResult;
import com.example.gradingsystem.datamodel.Test;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.bson.Document;
import org.bson.types.ObjectId;

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

    public void addStudentResultToTest(ObjectId testId, StudentResult studentResult) {
        Document doc = studentResult.toDocument();
        testCollection.updateOne(
                Filters.eq("_id", testId),
                Updates.push("studentResults", doc));
    }

    public void deleteTest(ObjectId testId){
        testCollection.deleteOne(Filters.eq("_id", testId));
    }
}

