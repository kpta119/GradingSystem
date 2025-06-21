package com.example.gradingsystem.datamodel;

import org.bson.Document;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Test {
    private LocalDate whenTaken;
    private String name;
    private List<Task> tasksOnTest = new ArrayList<>();
    private List<StudentResult> studentResults = new ArrayList<>();


    public Test(String name, LocalDate whenTaken, List<Task> tasksOnTest) {
        this.name = name;
        this.whenTaken = whenTaken;
        this.tasksOnTest = new ArrayList<>(tasksOnTest);
        this.studentResults = new ArrayList<>();
    }

    public Test(String name, LocalDate whenTaken) {
        this.name = name;
        this.whenTaken = whenTaken;
        this.tasksOnTest = new ArrayList<>();
        this.studentResults = new ArrayList<>();
    }
    public Test(){}

    public static Test fromDocument(Document doc) {
        String name = doc.getString("name");
        Date whenTakenDate = doc.getDate("whenTaken");
        LocalDate whenTaken = whenTakenDate.toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDate();

        List<Document> taskDocs = doc.getList("tasks", Document.class);
        List<Task> tasks = new ArrayList<>();
        for (Document taskDoc : taskDocs) {
            tasks.add(Task.fromDocument(taskDoc));
        }

        List<Document> resultDocs = doc.getList("studentResults", Document.class);
        List<StudentResult> studentResults = new ArrayList<>();
        for (Document resultDoc : resultDocs) {
            studentResults.add(StudentResult.fromDocument(resultDoc));
        }

        Test test = new Test(name, whenTaken, tasks);
        for (StudentResult sr : studentResults) {
            test.addStudentResult(sr);
        }

        return test;
    }

    public Document toDocument(){
        List<Document> taskDocuments = tasksOnTest.stream()
                .map(Task::toDocument)
                .toList();

        List<Document> studentsResultsDocuments = studentResults.stream()
                .map(StudentResult::toDocument)
                .toList();


        return new Document("name", name)
                .append("whenTaken", whenTaken)
                .append("tasks", taskDocuments)
                .append("studentResults", studentsResultsDocuments);
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setWhenTaken(LocalDate whenTaken) {
        this.whenTaken = whenTaken;
    }

    public void setTasksOnTest(List<Task> tasksOnTest) {
        this.tasksOnTest = tasksOnTest;
    }

    public void setStudentResults(List<StudentResult> studentResults) {
        this.studentResults = studentResults;
    }

    public List<StudentResult> getStudentResults() {
        return studentResults;
    }

    public List<Task> getTasksOnTest() {
        return tasksOnTest;
    }

    public String getName() {
        return name;
    }

    public String getWhenTaken() {
        return whenTaken.format(DateTimeFormatter.ofPattern("dd-MM-yyyy"));
    }

    public void addTask(Task task){
        tasksOnTest.add(task);
    }

    public void addStudentResult(StudentResult result) {
        studentResults.add(result);
    }

    @Override
    public String toString() {
        return name + " - When Taken: " +  getWhenTaken() ;
    }
}
