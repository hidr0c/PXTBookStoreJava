package org.example;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;

public class MongoDBConnection {
    private static final String CONNECTION_STRING = "mongodb+srv://baonguyentc6:0916753443aA@cluster0.xqz8l9y.mongodb.net/?retryWrites=true&w=majority&appName=Cluster0";
    private static final String DATABASE_NAME = "SmartLib";
    
    private static MongoClient mongoClient = null;
    private static MongoDatabase database = null;
    
    public static MongoDatabase getDatabase() {
        if (database == null) {
            try {
                mongoClient = MongoClients.create(CONNECTION_STRING);
                database = mongoClient.getDatabase(DATABASE_NAME);
                System.out.println("Connected to MongoDB Atlas successfully");
            } catch (Exception e) {
                System.out.println("MongoDB Atlas connection failed: " + e.getMessage());
                e.printStackTrace();
            }
        }
        return database;
    }
    
    public static void closeConnection() {
        if (mongoClient != null) {
            try {
                mongoClient.close();
                System.out.println("MongoDB Atlas connection closed");
            } catch (Exception e) {
                System.out.println("Error closing MongoDB Atlas connection: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }
} 