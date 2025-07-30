package com.example.gradingsystem.dao;

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

    private TestDAO(MongoDatabase database) {
        this.testCollection = database.getCollection("tests");
    }

    public static void init(MongoDatabase database) {
        if (instance == null) {
            instance = new TestDAO(database);
        }
    }

    public static TestDAO getInstance() {
        if (instance == null) {
            throw new IllegalStateException("TestDAO is not initialized. Call init(database) first.");
        }
        return instance;
    }

    public ObservableList<Test> getTests(){
        ObservableList<Test> tests = FXCollections.observableArrayList();
        for (Document doc : testCollection.find()){
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

    public void deleteStudentResult(ObjectId testId, String studentName){
        testCollection.updateOne(
                Filters.eq("_id", testId),
                Updates.pull("studentResults", Filters.eq("studentName", studentName))
        );
    }

    public void updateGradeInStudentResult(ObjectId testId, String studentName, String taskKey, int newScore){
        testCollection.updateOne(
                Filters.and(
                        Filters.eq("_id", testId),
                        Filters.elemMatch("studentResults", Filters.eq("studentName", studentName))),
                Updates.set("studentResults.$.allGrades." + taskKey + ".score", newScore)
        );
    }

}

