package com.example.gradingsystem.datamodel;

import org.bson.Document;

import java.util.HashMap;
import java.util.Map;

public class StudentResult {
    private String studentName;
    private Map<Task, Grade> grades;

    public StudentResult(String studentName) {
        this.studentName = studentName;
        this.grades = new HashMap<>();
    }

    public static StudentResult fromDocument(Document doc) {
        String studentName = doc.getString("studentName");
        Document allGradesDoc = (Document) doc.get("allGrades");

        StudentResult studentResult = new StudentResult(studentName);

        for (Map.Entry<String, Object> entry : allGradesDoc.entrySet()) {
            String taskKey = entry.getKey();
            Document gradeDoc = (Document) entry.getValue();

            Task task = parseTaskFromKey(taskKey);
            int score = gradeDoc.getInteger("score");

            studentResult.addGrade(task, score);
        }

        return studentResult;
    }

    public String getStudentName() {
        return studentName;
    }

    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }

    public void addGrade(Task task,int score) {
        if (score > task.getMaxPoints()){
            throw new InvalidScoreException("Score cannot be greater than max points in task");
        }
        grades.put(task, new Grade(score));
    }

    public Grade getGrade(Task task) {
        return grades.get(task);
    }

    public Map<Task, Grade> getAllGrades() {
        return grades;
    }

    @Override
    public String toString() {
        return "\n Name Of Student: " + studentName + " - Results: " + grades;
    }

    public Document toDocument() {
        Document studentDoc = new Document("studentName", studentName);
        Document allGradesDoc = new Document();

        for (Map.Entry<Task, Grade> entry : grades.entrySet()) {
            Task task = entry.getKey();
            Grade grade = entry.getValue();

            String taskKey = task.toString();

            allGradesDoc.append(taskKey, grade.toDocument());
        }

        studentDoc.append("allGrades", allGradesDoc);
        return studentDoc;
    }

    private static Task parseTaskFromKey(String key) {
        String[] parts = key.split(", ");
        int numberOfTask = Integer.parseInt(parts[0].split(":")[1]);
        int maxPoints = Integer.parseInt(parts[1].split("=")[1]);
        String typeStr = parts[2].split("=")[1].replace(" ", "_");

        TaskType type = TaskType.valueOf(typeStr);
        return new Task(numberOfTask, maxPoints, type);
    }

    private static class InvalidScoreException extends RuntimeException {
        public InvalidScoreException(String message) {
            super(message);
        }
    }
}


