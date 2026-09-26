package com.bbdu.placement.dao;

import com.bbdu.placement.config.MongoConfig;
import com.mongodb.client.AggregateIterable;
import com.mongodb.client.MongoCollection;
import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Task 4.1: Multi-stage Aggregation Pipelines in Java to compute real-time placement analytics.
 * Includes:
 * 1. Average CGPA & Min/Max per Department ($group, $avg, $max, $min, $project, $sort)
 * 2. Placement Rate (%) & Average Package per Department ($group, $cond, $multiply, $divide, $round)
 * 3. Overall Placement KPI Summary (Total Students, Placed %, Avg CTC, Highest CTC)
 * 4. Top Scorers Pipeline ($sort, $limit, $project)
 * 5. Company Recruitment Distribution ($match, $group, $sort)
 * 6. Most In-Demand Skills Pipeline ($unwind, $group, $sort, $limit)
 */
public class AnalyticsDAO {
    private static final Logger logger = LoggerFactory.getLogger(AnalyticsDAO.class);
    private final MongoCollection<Document> studentCollection;

    public AnalyticsDAO() {
        this.studentCollection = MongoConfig.getStudentsCollection();
    }

    /**
     * Pipeline 1: Department-wise CGPA Analytics
     */
    public List<Document> getDepartmentCgpaStats() {
        List<Document> pipeline = Arrays.asList(
                // Stage 1: Group by department and compute statistics
                new Document("$group", new Document("_id", "$department")
                        .append("totalStudents", new Document("$sum", 1))
                        .append("avgCgpa", new Document("$avg", "$cgpa"))
                        .append("maxCgpa", new Document("$max", "$cgpa"))
                        .append("minCgpa", new Document("$min", "$cgpa"))
                ),
                // Stage 2: Format and round values
                new Document("$project", new Document("department", "$_id")
                        .append("totalStudents", 1)
                        .append("avgCgpa", new Document("$round", Arrays.asList("$avgCgpa", 2)))
                        .append("maxCgpa", 1)
                        .append("minCgpa", 1)
                        .append("_id", 0)
                ),
                // Stage 3: Sort by highest average CGPA descending
                new Document("$sort", new Document("avgCgpa", -1))
        );

        List<Document> results = new ArrayList<>();
        studentCollection.aggregate(pipeline).into(results);
        return results;
    }

    /**
     * Pipeline 2: Placement Rate & Package per Department
     */
    public List<Document> getDepartmentPlacementRates() {
        List<Document> pipeline = Arrays.asList(
                // Stage 1: Group by department with conditional sums
                new Document("$group", new Document("_id", "$department")
                        .append("totalStudents", new Document("$sum", 1))
                        .append("placedCount", new Document("$sum",
                                new Document("$cond", Arrays.asList(
                                        new Document("$eq", Arrays.asList("$is_placed", true)), 1, 0
                                ))
                        ))
                        .append("avgPackage", new Document("$avg",
                                new Document("$cond", Arrays.asList(
                                        new Document("$eq", Arrays.asList("$is_placed", true)), "$package_ctc", null
                                ))
                        ))
                        .append("maxPackage", new Document("$max",
                                new Document("$cond", Arrays.asList(
                                        new Document("$eq", Arrays.asList("$is_placed", true)), "$package_ctc", 0.0
                                ))
                        ))
                ),
                // Stage 2: Compute placement percentage and round figures
                new Document("$project", new Document("department", "$_id")
                        .append("totalStudents", 1)
                        .append("placedCount", 1)
                        .append("unplacedCount", new Document("$subtract", Arrays.asList("$totalStudents", "$placedCount")))
                        .append("placementRate", new Document("$round", Arrays.asList(
                                new Document("$multiply", Arrays.asList(
                                        new Document("$divide", Arrays.asList("$placedCount", "$totalStudents")), 100.0
                                )), 2
                        )))
                        .append("avgPackage", new Document("$round", Arrays.asList(
                                new Document("$ifNull", Arrays.asList("$avgPackage", 0.0)), 2
                        )))
                        .append("maxPackage", 1)
                        .append("_id", 0)
                ),
                // Stage 3: Sort by placement rate descending
                new Document("$sort", new Document("placementRate", -1))
        );

        List<Document> results = new ArrayList<>();
        studentCollection.aggregate(pipeline).into(results);
        return results;
    }

    /**
     * Pipeline 3: Overall Placement KPI Summary (Total, Placed %, Avg CTC, Max CTC)
     */
    public Document getOverallKpiSummary() {
        List<Document> pipeline = Arrays.asList(
                new Document("$group", new Document("_id", null)
                        .append("totalStudents", new Document("$sum", 1))
                        .append("totalPlaced", new Document("$sum",
                                new Document("$cond", Arrays.asList(
                                        new Document("$eq", Arrays.asList("$is_placed", true)), 1, 0
                                ))
                        ))
                        .append("avgCgpa", new Document("$avg", "$cgpa"))
                        .append("maxCgpa", new Document("$max", "$cgpa"))
                        .append("avgPackage", new Document("$avg",
                                new Document("$cond", Arrays.asList(
                                        new Document("$eq", Arrays.asList("$is_placed", true)), "$package_ctc", null
                                ))
                        ))
                        .append("maxPackage", new Document("$max", "$package_ctc"))
                ),
                new Document("$project", new Document("_id", 0)
                        .append("totalStudents", 1)
                        .append("totalPlaced", 1)
                        .append("totalUnplaced", new Document("$subtract", Arrays.asList("$totalStudents", "$totalPlaced")))
                        .append("placementPercentage", new Document("$round", Arrays.asList(
                                new Document("$multiply", Arrays.asList(
                                        new Document("$divide", Arrays.asList("$totalPlaced", "$totalStudents")), 100.0
                                )), 2
                        )))
                        .append("avgCgpa", new Document("$round", Arrays.asList("$avgCgpa", 2)))
                        .append("maxCgpa", 1)
                        .append("avgPackageLpa", new Document("$round", Arrays.asList(
                                new Document("$ifNull", Arrays.asList("$avgPackage", 0.0)), 2
                        )))
                        .append("highestPackageLpa", "$maxPackage")
                )
        );

        AggregateIterable<Document> iter = studentCollection.aggregate(pipeline);
        Document result = iter.first();
        return result != null ? result : new Document();
    }

    /**
     * Pipeline 4: Top N Academic Performers ($sort, $limit, $project)
     */
    public List<Document> getTopScorers(int limit) {
        List<Document> pipeline = Arrays.asList(
                new Document("$sort", new Document("cgpa", -1).append("package_ctc", -1)),
                new Document("$limit", limit),
                new Document("$project", new Document("_id", 0)
                        .append("roll_number", 1)
                        .append("name", 1)
                        .append("department", 1)
                        .append("cgpa", 1)
                        .append("is_placed", 1)
                        .append("placed_company", 1)
                        .append("package_ctc", 1)
                        .append("skills", 1)
                )
        );

        List<Document> results = new ArrayList<>();
        studentCollection.aggregate(pipeline).into(results);
        return results;
    }

    /**
     * Pipeline 5: Company Recruitment Distribution ($match, $group, $sort)
     */
    public List<Document> getCompanyRecruitmentDistribution() {
        List<Document> pipeline = Arrays.asList(
                // Filter only placed students with legitimate company names
                new Document("$match", new Document("is_placed", true)
                        .append("placed_company", new Document("$nin", Arrays.asList("None", "", null)))
                ),
                // Group by company
                new Document("$group", new Document("_id", "$placed_company")
                        .append("recruitsCount", new Document("$sum", 1))
                        .append("avgPackage", new Document("$avg", "$package_ctc"))
                        .append("maxPackage", new Document("$max", "$package_ctc"))
                ),
                new Document("$project", new Document("company", "$_id")
                        .append("recruitsCount", 1)
                        .append("avgPackage", new Document("$round", Arrays.asList("$avgPackage", 2)))
                        .append("maxPackage", 1)
                        .append("_id", 0)
                ),
                new Document("$sort", new Document("recruitsCount", -1).append("avgPackage", -1))
        );

        List<Document> results = new ArrayList<>();
        studentCollection.aggregate(pipeline).into(results);
        return results;
    }

    /**
     * Pipeline 6: Top In-Demand Skills Pipeline ($unwind, $group, $sort, $limit)
     */
    public List<Document> getTopInDemandSkills(int limit) {
        List<Document> pipeline = Arrays.asList(
                new Document("$unwind", "$skills"),
                new Document("$group", new Document("_id", "$skills")
                        .append("totalStudents", new Document("$sum", 1))
                        .append("placedStudents", new Document("$sum",
                                new Document("$cond", Arrays.asList(
                                        new Document("$eq", Arrays.asList("$is_placed", true)), 1, 0
                                ))
                        ))
                ),
                new Document("$project", new Document("skill", "$_id")
                        .append("totalStudents", 1)
                        .append("placedStudents", 1)
                        .append("placementRate", new Document("$round", Arrays.asList(
                                new Document("$multiply", Arrays.asList(
                                        new Document("$divide", Arrays.asList("$placedStudents", "$totalStudents")), 100.0
                                )), 1
                        )))
                        .append("_id", 0)
                ),
                new Document("$sort", new Document("totalStudents", -1)),
                new Document("$limit", limit)
        );

        List<Document> results = new ArrayList<>();
        studentCollection.aggregate(pipeline).into(results);
        return results;
    }
}
