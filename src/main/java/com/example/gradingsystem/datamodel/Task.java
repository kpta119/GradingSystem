package com.example.gradingsystem.datamodel;


import org.bson.Document;

import java.util.Objects;

public class Task {
    private int numberOfTask;
    private int maxPoints;
    private TaskType type;

    public Task(int numberOfTask, int maxPoints, TaskType type) {
        this.numberOfTask = numberOfTask;
        this.maxPoints = maxPoints;
        this.type = type;
    }

    public static Task fromDocument(Document taskDoc) {
        int numberOfTask = taskDoc.getInteger("numberOfTask");
        int maxPoints = taskDoc.getInteger("maxPoints");
        String typeString  = taskDoc.getString("type");

        TaskType type = TaskType.valueOf(typeString);
        return new Task(numberOfTask, maxPoints, type);
    }

    public Document toDocument(){
        return new Document("numberOfTask", numberOfTask).append("maxPoints", maxPoints).append("type", type.toString());
    }

    public int getNumberOfTask() {
        return numberOfTask;
    }

    public TaskType getType() {
        return type;
    }

    public int getMaxPoints() {
        return maxPoints;
    }

    public void setNumberOfTask(int numberOfTask) {
        if (numberOfTask < 0){
            throw new IllegalArgumentException("Number of task should not be negative");
        }
        this.numberOfTask = numberOfTask;
    }

    public void setType(TaskType type) {
        if (type.equals(null)) {
            throw new NullPointerException("Setting type of task shuold not be null");
        }
        this.type = type;
    }


    public void setMaxPoints(int maxPoints) {
        if (maxPoints <= 0){
            throw new IllegalArgumentException("The maxPoints must be postive number");
        }
        this.maxPoints = maxPoints;
    }

    @Override
    public String toString() {
        return "Task number:" + numberOfTask +
                ", maxPoints=" + maxPoints +
                ", type=" + type;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;

        Task other = (Task) obj;
        return numberOfTask == other.numberOfTask &&
                maxPoints == other.maxPoints;
    }

    @Override
    public int hashCode() {
        return Objects.hash(numberOfTask, maxPoints);
    }
}
