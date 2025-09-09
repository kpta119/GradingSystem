package com.example.gradingsystem.dao;

import com.example.gradingsystem.datamodel.StudentResult;
import com.example.gradingsystem.datamodel.TaskType;
import com.example.gradingsystem.datamodel.Test;
import com.example.gradingsystem.dtos.StudentTestStats;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.bson.Document;
import org.bson.types.ObjectId;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;
import java.util.function.Predicate;

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

    public List<String> getStudents(){
        return testCollection.distinct("studentResults.studentName", String.class)
                .into(new ArrayList<>());
    }

    public List<String> getStudents(Predicate<String> predicate){
        List<String> allStudents = getStudents();
        List<String> result = new ArrayList<>();
        for (String student: allStudents){
            if (predicate.test(student.toLowerCase())){
                result.add(student);
            }
        }
        return result;
    }

    public List<StudentTestStats> getStudentsStats(String studentName) {
        List<StudentTestStats> statsList = new ArrayList<>();
        FindIterable<Document> tests = testCollection.find(Filters.eq("studentResults.studentName", studentName));

        for (Document testDoc: tests){
            Date whenTaken = testDoc.getDate("whenTaken");
            LocalDate testDate = whenTaken.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();

            List<Document> tasks = testDoc.getList("tasks", Document.class);
            List<Document> studentResults = testDoc.getList("studentResults", Document.class);

            Document studentResult = studentResults.stream()
                    .filter(s -> studentName.equals(s.getString("studentName")))
                    .findFirst()
                    .orElse(null);

            if (studentResult == null) continue;

            Document allGrades = studentResult.get("allGrades", Document.class);

            TaskScores scores =  calculateTaskScores(tasks, allGrades);

            double overallPercentage = (scores.totalMax > 0) ? (100.0 * scores.totalScore / scores.totalMax) : 0;

            Map<TaskType, Double> categoryPercentages = new HashMap<>();
            for (TaskType type : scores.scoredByCategory.keySet()) {
                int score = scores.scoredByCategory.get(type);
                int max = scores.maxByCategory.get(type);
                double percent = (max > 0) ? (100.0 * score / max) : 0;
                categoryPercentages.put(type, percent);
            }

            statsList.add(new StudentTestStats(testDate, overallPercentage, categoryPercentages));
        }
        return statsList;
    }

    private TaskScores calculateTaskScores(List<Document> tasks, Document allGrades) {
        TaskScores scores = new TaskScores();

        for (Document task : tasks) {
            String typeStr = task.getString("type");
            TaskType type = TaskType.valueOf(typeStr);

            int maxPoints = task.getInteger("maxPoints");
            scores.totalMax += maxPoints;
            scores.maxByCategory.merge(type, maxPoints, Integer::sum);

            String key = "Task number:" + task.getString("numberOfTask") +
                    ", maxPoints=" + maxPoints +
                    ", type=" + typeStr;

            Document gradeDoc = allGrades.get(key, Document.class);
            int score = (gradeDoc != null) ? gradeDoc.getInteger("score") : 0;

            scores.totalScore += score;
            scores.scoredByCategory.merge(type, score, Integer::sum);
        }

        return scores;
    }

    private static class TaskScores {
        int totalScore = 0;
        int totalMax = 0;
        Map<TaskType, Integer> scoredByCategory = new HashMap<>();
        Map<TaskType, Integer> maxByCategory = new HashMap<>();
    }
}

