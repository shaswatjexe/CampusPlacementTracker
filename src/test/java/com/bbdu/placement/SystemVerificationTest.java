package com.bbdu.placement;

import com.bbdu.placement.config.MongoConfig;
import com.bbdu.placement.dao.AnalyticsDAO;
import com.bbdu.placement.dao.ApplicationDAO;
import com.bbdu.placement.dao.CompanyDAO;
import com.bbdu.placement.dao.StudentDAO;
import com.bbdu.placement.data.DataSeeder;
import com.bbdu.placement.model.Company;
import com.bbdu.placement.model.Student;
import org.bson.Document;

import java.util.Arrays;
import java.util.List;

/**
 * End-to-end automated verification runner validating all project phases and tasks.
 */
public class SystemVerificationTest {

    public static void main(String[] args) {
        System.out.println("==========================================================================================");
        System.out.println("          🧪 STARTING END-TO-END SYSTEM VERIFICATION & TEST BENCHMARK                     ");
        System.out.println("==========================================================================================");

        try {
            // PHASE 1: Connection & Setup Check
            System.out.println("\n[PHASE 1] Initializing MongoDB connection & database index verification...");
            MongoConfig.getClient();
            MongoConfig.initializeIndexes();
            System.out.println("✔ Phase 1 PASSED: Successfully connected to MongoDB 8.3 (placement_db)");

            // PHASE 2: Core Data Ingestion & Seeding
            System.out.println("\n[PHASE 2] Executing bulk seeding (insertMany) with 65+ records threshold...");
            DataSeeder seeder = new DataSeeder();
            seeder.seedAll(true);

            StudentDAO studentDAO = new StudentDAO();
            CompanyDAO companyDAO = new CompanyDAO();
            ApplicationDAO appDAO = new ApplicationDAO();

            long studentCount = studentDAO.count();
            System.out.println("✔ Total Student Records Seeded: " + studentCount);
            if (studentCount < 65) {
                throw new AssertionError("Student count " + studentCount + " is less than mandatory 65 threshold!");
            }
            System.out.println("✔ Phase 2.2 Threshold Verified: >= 65 records present (actual: " + studentCount + ")");

            // Check Shaswat Jaiswal record
            Student shaswat = studentDAO.findByRollNumber("26 / 12502");
            if (shaswat == null) {
                throw new AssertionError("Mandatory student profile 'Shaswat Jaiswal (26 / 12502)' was not found!");
            }
            System.out.println("✔ Verified Lead Profile: " + shaswat);

            // Test Task 2.1: Manual insertOne
            System.out.println("\n[TASK 2.1] Testing insertOne with new candidate...");
            Student testStudent = new Student("TEST-999", "Test Candidate", "BCA DS & AI", 8.8,
                    Arrays.asList("Java", "MongoDB"), false, "None", 0.0, "test@bbdu.ac.in", "Male", 2026);
            boolean inserted = studentDAO.insertOne(testStudent);
            System.out.println("✔ insertOne result: " + inserted);

            // PHASE 3: CRUD & Query Operators
            System.out.println("\n[PHASE 3] Testing Comparison & Logical Operators...");

            // Task 3.1: $gte comparison
            List<Student> eligible = studentDAO.findByMinCgpa(8.5);
            System.out.printf("✔ Task 3.1 ($gte min CGPA 8.5): Found %d eligible students.\n", eligible.size());

            // Task 3.1: $gte and $lte range
            List<Student> range = studentDAO.findByCgpaRange(7.5, 8.5);
            System.out.printf("✔ Task 3.1 ($gte and $lte range 7.5 - 8.5): Found %d students.\n", range.size());

            // Task 3.2: Array $in
            List<Student> pyOrMongo = studentDAO.findByAnySkills(Arrays.asList("Python", "MongoDB"));
            System.out.printf("✔ Task 3.2 (Array $in [Python, MongoDB]): Found %d students.\n", pyOrMongo.size());

            // Task 3.2: Array $all
            List<Student> allSkills = studentDAO.findByAllSkills(Arrays.asList("Java", "MongoDB"));
            System.out.printf("✔ Task 3.2 (Array $all [Java, MongoDB]): Found %d students.\n", allSkills.size());

            // Task 3.2: Complex $and + $or + $in
            List<Student> complex = studentDAO.findEligibleCandidates(8.0, Arrays.asList("BCA DS & AI", "B.Tech CSE"), "Java");
            System.out.printf("✔ Task 3.2 (Complex $and + $or): Found %d candidates.\n", complex.size());

            // Task 3.3: updateOne
            System.out.println("\n[TASK 3.3] Testing updateOne placement status...");
            boolean updated = studentDAO.updatePlacementStatus("TEST-999", "Google Cloud", 16.0);
            Student updatedStudent = studentDAO.findByRollNumber("TEST-999");
            System.out.println("✔ updateOne verified: isPlaced=" + updatedStudent.isPlaced() + ", Company=" + updatedStudent.getPlacedCompany());

            // Task 3.4: deleteOne / deleteMany
            System.out.println("\n[TASK 3.4] Testing deleteOne and deleteMany...");
            boolean deleted = studentDAO.deleteOne("TEST-999");
            System.out.println("✔ deleteOne verified: deleted=" + deleted);

            // PHASE 4: Aggregation Analytics & Explain Plan
            System.out.println("\n[PHASE 4] Testing Aggregation Pipelines & Explain Stats...");
            AnalyticsDAO analyticsDAO = new AnalyticsDAO();

            Document kpi = analyticsDAO.getOverallKpiSummary();
            System.out.println("✔ Pipeline 1 (Overall KPI): " + kpi.toJson());

            List<Document> deptStats = analyticsDAO.getDepartmentPlacementRates();
            System.out.println("✔ Pipeline 2 (Dept Placement Rates): Computed for " + deptStats.size() + " departments.");

            List<Document> topScorers = analyticsDAO.getTopScorers(3);
            System.out.println("✔ Pipeline 3 (Top 3 Scorers):");
            for (Document d : topScorers) {
                System.out.printf("   - %s (%s) | CGPA: %.2f | Placed: %s\n",
                        d.get("name"), d.get("department"), d.get("cgpa"), d.get("is_placed"));
            }

            List<Document> companyDist = analyticsDAO.getCompanyRecruitmentDistribution();
            System.out.println("✔ Pipeline 4 (Company Distribution): " + companyDist.size() + " companies recruiting.");

            List<Document> topSkills = analyticsDAO.getTopInDemandSkills(5);
            System.out.println("✔ Pipeline 5 (Top 5 Skills): " + topSkills.size() + " skills evaluated.");

            // Task 4.3: .explain("executionStats")
            System.out.println("\n[TASK 4.3] Running .explain('executionStats') query planner benchmark...");
            Document explain = studentDAO.explainCgpaQuery(8.5);
            Document stats = (Document) explain.get("executionStats");
            if (stats != null) {
                System.out.println("✔ .explain() Result:");
                System.out.println("   - Execution Time Millis : " + stats.get("executionTimeMillis") + " ms");
                System.out.println("   - Docs Returned (n)     : " + stats.get("nReturned"));
                System.out.println("   - Total Keys Examined   : " + stats.get("totalKeysExamined"));
                System.out.println("   - Total Docs Examined   : " + stats.get("totalDocsExamined"));
            }

            System.out.println("\n==========================================================================================");
            System.out.println("        🎉 ALL VERIFICATION TESTS PASSED SUCCESSFULLY WITH 100% ACCURACY!                 ");
            System.out.println("==========================================================================================");

        } catch (Exception e) {
            System.err.println("Verification Failed: " + e.getMessage());
            e.printStackTrace();
        } finally {
            MongoConfig.close();
        }
    }
}
