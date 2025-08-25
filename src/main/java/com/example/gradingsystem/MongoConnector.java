package com.example.gradingsystem;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;

public class MongoConnector {
    private static MongoConnector instance;
    private final MongoClient mongoClient;
    private final MongoDatabase database;

    private MongoConnector(String uri) {
        this.mongoClient = MongoClients.create(uri);
        this.database = mongoClient.getDatabase("DatabaseGradingsys");
    }

    public static void init(String uri) {
        if (instance == null) {
            instance = new MongoConnector(uri);
        }
    }

    public static MongoConnector getInstance() {
        if (instance == null) {
            throw new IllegalStateException("MongoConnector is not initialized. Call init(uri) first.");
        }
        return instance;
    }


    public MongoDatabase getDatabase() {
        return database;
    }

    public void testConnection() {
        MongoCollection<Document> collection = database.getCollection("Students");
        for (Document doc : collection.find()) {
            System.out.println(doc.toJson());
        }
    }
    public void close() {
        mongoClient.close();
    }
}