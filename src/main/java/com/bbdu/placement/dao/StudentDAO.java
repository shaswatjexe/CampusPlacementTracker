package com.bbdu.placement.dao;

import com.bbdu.placement.config.MongoConfig;
import com.bbdu.placement.model.Student;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Projections;
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
 * Data Access Object for Student CRUD, Comparison, Logical, Array and Execution Plan queries.
 *
 * Covers:
 * - Phase 2: insertOne (Task 2.1), insertMany (Task 2.2), find queries (Task 2.3)
 * - Phase 3: Comparison operators ($eq, $gt, $gte, $lt, $lte, $ne) (Task 3.1)
 *            Array and Logical operators ($in, $and, $or, $all, $regex) (Task 3.2)
 *            Update operations (updateOne, updateMany) (Task 3.3)
 *            Delete operations (deleteOne, deleteMany) (Task 3.4)
 * - Phase 4: Query execution performance analysis (.explain("executionStats"))
 */
public class StudentDAO {
    private static final Logger logger = LoggerFactory.getLogger(StudentDAO.class);
    private final MongoCollection<Document> collection;

    public StudentDAO() {
        this.collection = MongoConfig.getStudentsCollection();
    }

    // ==========================================
    // PHASE 2: INSERTION MODULES
    // ==========================================

    /**
     * Task 2.1: Code insertOne module in Java to allow manual entry of single student profiles.
     */
    public boolean insertOne(Student student) {
        try {
            Document doc = student.toDocument();
            InsertOneResult result = collection.insertOne(doc);
            logger.info("Successfully inserted student: {} with ID: {}", student.getRollNumber(), result.getInsertedId());
            return result.wasAcknowledged();
        } catch (Exception e) {
            logger.error("Error inserting student {}: {}", student.getRollNumber(), e.getMessage());
            return false;
        }
    }

    /**
     * Task 2.2: Build bulk insertion script (insertMany) to load at least 65 records.
     */
    public int insertMany(List<Student> students) {
        if (students == null || students.isEmpty()) return 0;
        try {
            List<Document> docs = new ArrayList<>();
            for (Student s : students) {
                docs.add(s.toDocument());
            }
            InsertManyResult result = collection.insertMany(docs);
            int count = result.getInsertedIds().size();
            logger.info("Successfully bulk inserted {} student records.", count);
            return count;
        } catch (Exception e) {
            logger.error("Error during bulk insertion: {}", e.getMessage());
            return 0;
        }
    }

    // ==========================================
    // PHASE 3: READ & COMPARISON OPERATORS (Task 3.1)
    // ==========================================

    /**
     * Retrieve all students sorted by Roll Number.
     */
    public List<Student> findAll() {
        List<Student> list = new ArrayList<>();
        FindIterable<Document> docs = collection.find().sort(Sorts.ascending("roll_number"));
        for (Document d : docs) {
            list.add(Student.fromDocument(d));
        }
        return list;
    }

    /**
     * Find single student by exact Roll Number using $eq.
     */
    public Student findByRollNumber(String rollNumber) {
        Bson filter = Filters.eq("roll_number", rollNumber);
        Document doc = collection.find(filter).first();
        return Student.fromDocument(doc);
    }

    /**
     * Comparison Operator $gte: Filter students with CGPA >= minCgpa.
     * Essential for company eligibility cutoff screening.
     */
    public List<Student> findByMinCgpa(double minCgpa) {
        Bson filter = Filters.gte("cgpa", minCgpa);
        List<Student> list = new ArrayList<>();
        for (Document d : collection.find(filter).sort(Sorts.descending("cgpa"))) {
            list.add(Student.fromDocument(d));
        }
        return list;
    }

    /**
     * Comparison Operator $gt: Filter students with CGPA strictly greater than threshold.
     */
    public List<Student> findByCgpaGreaterThan(double threshold) {
        Bson filter = Filters.gt("cgpa", threshold);
        List<Student> list = new ArrayList<>();
        for (Document d : collection.find(filter).sort(Sorts.descending("cgpa"))) {
            list.add(Student.fromDocument(d));
        }
        return list;
    }

    /**
     * Comparison Operator $lt / $lte: Find students with CGPA <= threshold (e.g. for remedial mentoring).
     */
    public List<Student> findByCgpaLessThanOrEqual(double maxCgpa) {
        Bson filter = Filters.lte("cgpa", maxCgpa);
        List<Student> list = new ArrayList<>();
        for (Document d : collection.find(filter).sort(Sorts.ascending("cgpa"))) {
            list.add(Student.fromDocument(d));
        }
        return list;
    }

    /**
     * Combined Comparison: CGPA between min and max ($gte and $lte).
     */
    public List<Student> findByCgpaRange(double minCgpa, double maxCgpa) {
        Bson filter = Filters.and(Filters.gte("cgpa", minCgpa), Filters.lte("cgpa", maxCgpa));
        List<Student> list = new ArrayList<>();
        for (Document d : collection.find(filter).sort(Sorts.descending("cgpa"))) {
            list.add(Student.fromDocument(d));
        }
        return list;
    }

    /**
     * Comparison Operator $ne: Find all unplaced students (is_placed != true).
     */
    public List<Student> findUnplacedStudents() {
        Bson filter = Filters.ne("is_placed", true);
        List<Student> list = new ArrayList<>();
        for (Document d : collection.find(filter).sort(Sorts.descending("cgpa"))) {
            list.add(Student.fromDocument(d));
        }
        return list;
    }

    // ==========================================
    // PHASE 3: ARRAY & LOGICAL OPERATORS (Task 3.2)
    // ==========================================

    /**
     * Array Operator $in: Find students having ANY of the specified skills.
     */
    public List<Student> findByAnySkills(List<String> skills) {
        Bson filter = Filters.in("skills", skills);
        List<Student> list = new ArrayList<>();
        for (Document d : collection.find(filter).sort(Sorts.descending("cgpa"))) {
            list.add(Student.fromDocument(d));
        }
        return list;
    }

    /**
     * Array Operator $all: Find students having ALL of the specified skills (Strict criteria).
     */
    public List<Student> findByAllSkills(List<String> skills) {
        Bson filter = Filters.all("skills", skills);
        List<Student> list = new ArrayList<>();
        for (Document d : collection.find(filter).sort(Sorts.descending("cgpa"))) {
            list.add(Student.fromDocument(d));
        }
        return list;
    }

    /**
     * Logical Operators $and & $or:
     * Complex eligibility query matching company requirements:
     * Must have CGPA >= minCgpa AND (Department in eligibleDepts OR has required skill)
     */
    public List<Student> findEligibleCandidates(double minCgpa, List<String> eligibleDepts, String primarySkill) {
        Bson deptFilter = Filters.in("department", eligibleDepts);
        Bson skillFilter = Filters.eq("skills", primarySkill);
        Bson orCriteria = Filters.or(deptFilter, skillFilter);

        Bson finalFilter = Filters.and(
                Filters.gte("cgpa", minCgpa),
                Filters.eq("is_placed", false),
                orCriteria
        );

        List<Student> list = new ArrayList<>();
        for (Document d : collection.find(finalFilter).sort(Sorts.descending("cgpa"))) {
            list.add(Student.fromDocument(d));
        }
        return list;
    }

    /**
     * Regex search on Name or Department ($regex).
     */
    public List<Student> searchByNameOrDept(String query) {
        Bson nameRegex = Filters.regex("name", query, "i");
        Bson deptRegex = Filters.regex("department", query, "i");
        Bson filter = Filters.or(nameRegex, deptRegex);

        List<Student> list = new ArrayList<>();
        for (Document d : collection.find(filter)) {
            list.add(Student.fromDocument(d));
        }
        return list;
    }

    // ==========================================
    // PHASE 3: UPDATE OPERATIONS (Task 3.3)
    // ==========================================

    /**
     * Task 3.3: updateOne to update a student's placement status to is_placed: true,
     * recording placed company and package CTC.
     */
    public boolean updatePlacementStatus(String rollNumber, String companyName, double ctcLpa) {
        try {
            Bson filter = Filters.eq("roll_number", rollNumber);
            Bson update = Updates.combine(
                    Updates.set("is_placed", true),
                    Updates.set("placed_company", companyName),
                    Updates.set("package_ctc", ctcLpa)
            );
            UpdateResult result = collection.updateOne(filter, update);
            logger.info("Updated placement status for student {}: matched={}, modified={}",
                    rollNumber, result.getMatchedCount(), result.getModifiedCount());
            return result.getModifiedCount() > 0;
        } catch (Exception e) {
            logger.error("Error updating placement status for {}: {}", rollNumber, e.getMessage());
            return false;
        }
    }

    /**
     * Update student CGPA or contact information.
     */
    public boolean updateStudentDetails(String rollNumber, double newCgpa, String email) {
        try {
            Bson filter = Filters.eq("roll_number", rollNumber);
            Bson update = Updates.combine(
                    Updates.set("cgpa", newCgpa),
                    Updates.set("email", email)
            );
            UpdateResult result = collection.updateOne(filter, update);
            return result.getModifiedCount() > 0;
        } catch (Exception e) {
            logger.error("Error updating student details: {}", e.getMessage());
            return false;
        }
    }

    /**
     * updateMany: Bulk mark unplaced students in a batch or add graduation year attribute.
     */
    public long updateGraduationYearForDepartment(String department, int gradYear) {
        try {
            Bson filter = Filters.eq("department", department);
            Bson update = Updates.set("graduation_year", gradYear);
            UpdateResult result = collection.updateMany(filter, update);
            logger.info("Updated graduation year for {} students in department {}", result.getModifiedCount(), department);
            return result.getModifiedCount();
        } catch (Exception e) {
            logger.error("Error in updateMany: {}", e.getMessage());
            return 0;
        }
    }

    // ==========================================
    // PHASE 3: DELETE OPERATIONS (Task 3.4)
    // ==========================================

    /**
     * Task 3.4: deleteOne - Delete a single student record (e.g. invalid entry or withdrawn student).
     */
    public boolean deleteOne(String rollNumber) {
        try {
            Bson filter = Filters.eq("roll_number", rollNumber);
            DeleteResult result = collection.deleteOne(filter);
            logger.info("Deleted student {}: deletedCount={}", rollNumber, result.getDeletedCount());
            return result.getDeletedCount() > 0;
        } catch (Exception e) {
            logger.error("Error deleting student {}: {}", rollNumber, e.getMessage());
            return false;
        }
    }

    /**
     * Task 3.4: deleteMany - Clean up test records or invalid accounts.
     */
    public long deleteTestRecords() {
        try {
            Bson filter = Filters.or(
                    Filters.regex("name", "Test", "i"),
                    Filters.regex("roll_number", "TEST", "i")
            );
            DeleteResult result = collection.deleteMany(filter);
            logger.info("Cleaned up {} test student records.", result.getDeletedCount());
            return result.getDeletedCount();
        } catch (Exception e) {
            logger.error("Error deleting test records: {}", e.getMessage());
            return 0;
        }
    }

    /**
     * Clear all student records (used before re-seeding).
     */
    public long dropAndReset() {
        try {
            DeleteResult result = collection.deleteMany(new Document());
            return result.getDeletedCount();
        } catch (Exception e) {
            logger.error("Error resetting collection: {}", e.getMessage());
            return 0;
        }
    }

    /**
     * Total count of students currently in collection.
     */
    public long count() {
        return collection.countDocuments();
    }

    // ==========================================
    // PHASE 4: QUERY PERFORMANCE EXPLAIN (.explain()) (Task 4.3)
    // ==========================================

    /**
     * Task 4.3: Execute query with .explain("executionStats")
     * Returns execution stats (executionSuccess, nReturned, executionTimeMillis, totalKeysExamined, totalDocsExamined)
     */
    public Document explainCgpaQuery(double minCgpa) {
        try {
            Bson filter = Filters.gte("cgpa", minCgpa);
            // Run explain command via MongoDB driver
            Document explainCmd = new Document("explain",
                    new Document("find", MongoConfig.COLLECTION_STUDENTS)
                            .append("filter", filter)
            ).append("verbosity", "executionStats");

            return MongoConfig.getDatabase().runCommand(explainCmd);
        } catch (Exception e) {
            logger.error("Error executing explain: {}", e.getMessage());
            return new Document("error", e.getMessage());
        }
    }
}
