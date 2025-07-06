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
    //private final ObservableList<Test> tests = FXCollections.observableArrayList();

    public TestDAO() {
        MongoDatabase database = MongoConnector.getInstance().getDatabase();
        this.testCollection = database.getCollection("tests");
        //loadTestsFromDatabase();
    }

    public static TestDAO getInstance() {
        if (instance == null) {
            instance = new TestDAO();
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
        //tests.add(test);
    }

    public void addStudentResultToTest(ObjectId testId, StudentResult studentResult) {
        Document doc = studentResult.toDocument();
        testCollection.updateOne(
                Filters.eq("_id", testId),
                Updates.push("studentResults", doc));
    }

    public void deleteTest(ObjectId testId){
        testCollection.deleteOne(Filters.eq("_id", testId));
        //tests.removeIf(test -> test.getId().equals(testId));
    }

//    private void loadTestsFromDatabase() {
//        tests.clear();
//        for (Document doc : testCollection.find()) {
//            tests.add(Test.fromDocument(doc));
//        }
//    }
}

