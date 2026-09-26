package com.bbdu.placement.dao;

import com.bbdu.placement.config.MongoConfig;
import com.bbdu.placement.model.Application;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Sorts;
import com.mongodb.client.model.Updates;
import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.InsertManyResult;
import com.mongodb.client.result.InsertOneResult;
import com.mongodb.client.result.UpdateResult;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for Application management.
 */
public class ApplicationDAO {
    private static final Logger logger = LoggerFactory.getLogger(ApplicationDAO.class);
    private final MongoCollection<Document> collection;

    public ApplicationDAO() {
        this.collection = MongoConfig.getApplicationsCollection();
    }

    public boolean submitApplication(Application app) {
        try {
            InsertOneResult res = collection.insertOne(app.toDocument());
            return res.wasAcknowledged();
        } catch (Exception e) {
            logger.error("Error submitting application: {}", e.getMessage());
            return false;
        }
    }

    public int insertMany(List<Application> apps) {
        if (apps == null || apps.isEmpty()) return 0;
        try {
            List<Document> docs = new ArrayList<>();
            for (Application a : apps) docs.add(a.toDocument());
            InsertManyResult res = collection.insertMany(docs);
            return res.getInsertedIds().size();
        } catch (Exception e) {
            logger.error("Error bulk inserting applications: {}", e.getMessage());
            return 0;
        }
    }

    public List<Application> findAll() {
        List<Application> list = new ArrayList<>();
        FindIterable<Document> docs = collection.find().sort(Sorts.descending("application_date"));
        for (Document d : docs) {
            list.add(Application.fromDocument(d));
        }
        return list;
    }

    public List<Application> findByRollNumber(String rollNumber) {
        List<Application> list = new ArrayList<>();
        for (Document d : collection.find(Filters.eq("roll_number", rollNumber))) {
            list.add(Application.fromDocument(d));
        }
        return list;
    }

    public boolean updateStatus(String applicationId, String status, String remarks) {
        try {
            Bson filter = Filters.eq("application_id", applicationId);
            Bson update = Updates.combine(
                    Updates.set("status", status),
                    Updates.set("remarks", remarks)
            );
            UpdateResult res = collection.updateOne(filter, update);
            return res.getModifiedCount() > 0;
        } catch (Exception e) {
            logger.error("Error updating application status: {}", e.getMessage());
            return false;
        }
    }

    public boolean deleteOne(String applicationId) {
        DeleteResult res = collection.deleteOne(Filters.eq("application_id", applicationId));
        return res.getDeletedCount() > 0;
    }

    public long count() {
        return collection.countDocuments();
    }

    public long dropAndReset() {
        DeleteResult res = collection.deleteMany(new Document());
        return res.getDeletedCount();
    }
}
