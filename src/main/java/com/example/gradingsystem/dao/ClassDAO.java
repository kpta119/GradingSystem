package com.example.gradingsystem.dao;

import com.example.gradingsystem.datamodel.Student;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;

import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Updates.addToSet;

public class ClassDAO {

    private static ClassDAO instance;
    private final MongoCollection<Document> classCollection;

    private ClassDAO(MongoDatabase database) {
        this.classCollection = database.getCollection("classes");
    }

    public static void init(MongoDatabase database) {
        if (instance == null) {
            instance = new ClassDAO(database);
        }
    }

    public static ClassDAO getInstance() {
        return instance;
    }

    public void addStudentToClass(String className, Student student) {
        Document studentDoc = new Document("fullName", student.getFullName())
                .append("className", student.getClassName());

        classCollection.updateOne(
                eq("className", className),
                addToSet("students", studentDoc),
                new com.mongodb.client.model.UpdateOptions().upsert(true)
        );
    }
}
