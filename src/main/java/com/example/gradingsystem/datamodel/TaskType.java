package com.example.gradingsystem.datamodel;

public enum TaskType {
    LISTENING, READING, USE_OF_ENGLISH, WRITING, VOCABULARY;

    @Override
    public String toString() {
        return name().replace("_", " ");
    }
}
