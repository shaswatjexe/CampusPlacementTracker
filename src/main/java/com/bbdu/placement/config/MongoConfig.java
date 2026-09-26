package com.bbdu.placement.config;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.IndexOptions;
import com.mongodb.client.model.Indexes;
import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * MongoDB Configuration and Connection Manager.
 * Connects to MongoDB Community Server (v8.3) running at mongodb://localhost:27017
 * Manages database: placement_db and collections: students, companies, applications
 */
public class MongoConfig {
    private static final Logger logger = LoggerFactory.getLogger(MongoConfig.class);

    public static final String CONNECTION_URI = "mongodb://localhost:27017";
    public static final String DATABASE_NAME = "placement_db";
    public static final String COLLECTION_STUDENTS = "students";
    public static final String COLLECTION_COMPANIES = "companies";
    public static final String COLLECTION_APPLICATIONS = "applications";

    private static MongoClient mongoClient = null;

    private MongoConfig() {}

    /**
     * Singleton MongoClient instance.
     */
    public static synchronized MongoClient getClient() {
        if (mongoClient == null) {
            try {
                logger.info("Initializing MongoDB Client connection to {}", CONNECTION_URI);
                mongoClient = MongoClients.create(CONNECTION_URI);
                // Ping database to verify connection
                MongoDatabase adminDb = mongoClient.getDatabase("admin");
                adminDb.runCommand(new Document("ping", 1));
                logger.info("Successfully connected to MongoDB server at {}", CONNECTION_URI);
            } catch (Exception e) {
                logger.error("Failed to connect to MongoDB server: {}", e.getMessage());
                throw new RuntimeException("Could not connect to MongoDB at " + CONNECTION_URI, e);
            }
        }
        return mongoClient;
    }

    /**
     * Get the placement_db MongoDatabase instance.
     */
    public static MongoDatabase getDatabase() {
        return getClient().getDatabase(DATABASE_NAME);
    }

    /**
     * Get students collection.
     */
    public static MongoCollection<Document> getStudentsCollection() {
        return getDatabase().getCollection(COLLECTION_STUDENTS);
    }

    /**
     * Get companies collection.
     */
    public static MongoCollection<Document> getCompaniesCollection() {
        return getDatabase().getCollection(COLLECTION_COMPANIES);
    }

    /**
     * Get applications collection.
     */
    public static MongoCollection<Document> getApplicationsCollection() {
        return getDatabase().getCollection(COLLECTION_APPLICATIONS);
    }

    /**
     * Initialize collections and create production indexes for performance optimization.
     */
    public static void initializeIndexes() {
        try {
            MongoCollection<Document> students = getStudentsCollection();
            // Unique index on roll_number
            students.createIndex(Indexes.ascending("roll_number"), new IndexOptions().unique(true));
            // Secondary index on cgpa
            students.createIndex(Indexes.descending("cgpa"));
            // Compound index on department and is_placed
            students.createIndex(Indexes.ascending("department", "is_placed"));
            // Multikey index on skills
            students.createIndex(Indexes.ascending("skills"));

            MongoCollection<Document> companies = getCompaniesCollection();
            companies.createIndex(Indexes.ascending("company_id"), new IndexOptions().unique(true));

            MongoCollection<Document> applications = getApplicationsCollection();
            applications.createIndex(Indexes.ascending("application_id"), new IndexOptions().unique(true));
            applications.createIndex(Indexes.ascending("roll_number", "company_id"));

            logger.info("Database indexes verified/created successfully on collections.");
        } catch (Exception e) {
            logger.warn("Index creation notice: {}", e.getMessage());
        }
    }

    /**
     * Close MongoClient connection.
     */
    public static synchronized void close() {
        if (mongoClient != null) {
            mongoClient.close();
            mongoClient = null;
            logger.info("MongoDB connection closed.");
        }
    }
}
