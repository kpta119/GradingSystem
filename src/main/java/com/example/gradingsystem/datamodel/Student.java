package com.example.gradingsystem.datamodel;

public class Student {
    private String fullName;
    private String className;

    public Student(String fullName, String className) {
        this.fullName = fullName;
        this.className = className;
    }

    public String getFullName() {
        return fullName;
    }

    public String getClassName() {
        return className;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public void setClassName(String className) {
        this.className = className;
    }
}
