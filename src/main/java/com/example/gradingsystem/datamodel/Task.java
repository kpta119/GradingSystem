package com.example.gradingsystem.datamodel;


public class Task {
    private int numberOfTask;
    private int maxPoints;
    private TaskType type;

    public Task(int numberOfTask, int maxPoints, TaskType type) {
        this.numberOfTask = numberOfTask;
        this.maxPoints = maxPoints;
        this.type = type;
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
        this.numberOfTask = numberOfTask;
    }

    public void setType(TaskType type) {
        this.type = type;
    }


    public void setMaxPoints(int maxPoints) {
        this.maxPoints = maxPoints;
    }

    @Override
    public String toString() {
        return "Task number:" + numberOfTask +
                ", maxPoints=" + maxPoints +
                ", type=" + type;
    }
}
