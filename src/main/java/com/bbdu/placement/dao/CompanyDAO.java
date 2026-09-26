package com.bbdu.placement.dao;

import com.bbdu.placement.config.MongoConfig;
import com.bbdu.placement.model.Company;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Sorts;
import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.InsertManyResult;
import com.mongodb.client.result.InsertOneResult;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for Company collection operations.
 */
public class CompanyDAO {
    private static final Logger logger = LoggerFactory.getLogger(CompanyDAO.class);
    private final MongoCollection<Document> collection;

    public CompanyDAO() {
        this.collection = MongoConfig.getCompaniesCollection();
    }

    public boolean insertOne(Company company) {
        try {
            InsertOneResult res = collection.insertOne(company.toDocument());
            return res.wasAcknowledged();
        } catch (Exception e) {
            logger.error("Error inserting company {}: {}", company.getName(), e.getMessage());
            return false;
        }
    }

    public int insertMany(List<Company> companies) {
        if (companies == null || companies.isEmpty()) return 0;
        try {
            List<Document> docs = new ArrayList<>();
            for (Company c : companies) docs.add(c.toDocument());
            InsertManyResult res = collection.insertMany(docs);
            return res.getInsertedIds().size();
        } catch (Exception e) {
            logger.error("Error bulk inserting companies: {}", e.getMessage());
            return 0;
        }
    }

    public List<Company> findAll() {
        List<Company> list = new ArrayList<>();
        FindIterable<Document> docs = collection.find().sort(Sorts.descending("package_offered"));
        for (Document d : docs) {
            list.add(Company.fromDocument(d));
        }
        return list;
    }

    public Company findById(String companyId) {
        Document doc = collection.find(Filters.eq("company_id", companyId)).first();
        return Company.fromDocument(doc);
    }

    public List<Company> findEligibleCompanies(double studentCgpa) {
        Bson filter = Filters.lte("min_cgpa", studentCgpa);
        List<Company> list = new ArrayList<>();
        for (Document d : collection.find(filter).sort(Sorts.descending("package_offered"))) {
            list.add(Company.fromDocument(d));
        }
        return list;
    }

    public long count() {
        return collection.countDocuments();
    }

    public long dropAndReset() {
        DeleteResult res = collection.deleteMany(new Document());
        return res.getDeletedCount();
    }
}
