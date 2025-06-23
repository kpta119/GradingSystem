package com.example.gradingsystem;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import io.github.cdimascio.dotenv.Dotenv;
import org.bson.Document;

public class MongoConnector {
    private static MongoConnector instance;
    private final MongoClient mongoClient;
    private final MongoDatabase database;

    public MongoConnector() {
        Dotenv dotenv = Dotenv.load();
        String uri = dotenv.get("MONGODB_URI");
        mongoClient = MongoClients.create(uri);
        database = mongoClient.getDatabase("DatabaseGradingsys");
    }

    public static MongoConnector getInstance() {
        if (instance == null) {
            instance = new MongoConnector();
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