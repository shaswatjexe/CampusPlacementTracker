package com.bbdu.placement.ui;

import com.bbdu.placement.config.MongoConfig;
import com.bbdu.placement.dao.AnalyticsDAO;
import com.bbdu.placement.dao.ApplicationDAO;
import com.bbdu.placement.dao.CompanyDAO;
import com.bbdu.placement.dao.StudentDAO;
import com.bbdu.placement.data.DataSeeder;
import com.bbdu.placement.model.Application;
import com.bbdu.placement.model.Company;
import com.bbdu.placement.model.Student;
import com.bbdu.placement.util.TablePrinter;
import org.bson.Document;

import java.util.*;

/**
 * Interactive Console CLI Menu System for BBDU Campus Placement Officers.
 * Covers Phase 2, 3, and 4 requirements.
 */
public class ConsoleMenu {
    private final StudentDAO studentDAO;
    private final CompanyDAO companyDAO;
    private final ApplicationDAO applicationDAO;
    private final AnalyticsDAO analyticsDAO;
    private final DataSeeder dataSeeder;
    private final Scanner scanner;

    public ConsoleMenu() {
        this.studentDAO = new StudentDAO();
        this.companyDAO = new CompanyDAO();
        this.applicationDAO = new ApplicationDAO();
        this.analyticsDAO = new AnalyticsDAO();
        this.dataSeeder = new DataSeeder();
        this.scanner = new Scanner(System.in);
    }

    public void start() {
        printBanner();

        boolean running = true;
        while (running) {
            printMainMenu();
            System.out.print("Enter your choice (0-15): ");
            String input = scanner.nextLine().trim();

            switch (input) {
                case "1":
                    handleViewAllStudents();
                    break;
                case "2":
                    handleInsertOneStudent();
                    break;
                case "3":
                    handleBulkSeed();
                    break;
                case "4":
                    handleCgpaFilter();
                    break;
                case "5":
                    handleCgpaRangeFilter();
                    break;
                case "6":
                    handleSkillFilter();
                    break;
                case "7":
                    handleComplexEligibilitySearch();
                    break;
                case "8":
                    handleUpdatePlacementStatus();
                    break;
                case "9":
                    handleBulkUpdateGraduationYear();
                    break;
                case "10":
                    handleDeleteOperations();
                    break;
                case "11":
                    handleAnalyticsReport();
                    break;
                case "12":
                    handleExplainQueryPerformance();
                    break;
                case "13":
                    handleViewCompanies();
                    break;
                case "14":
                    handleViewApplications();
                    break;
                case "15":
                    handleLaunchGui();
                    break;
                case "0":
                    running = false;
                    System.out.println("\n[INFO] Thank you for using BBDU Campus Placement Tracker. Goodbye!");
                    break;
                default:
                    System.out.println("\n[ERROR] Invalid choice. Please enter a number between 0 and 15.");
            }

            if (running) {
                System.out.print("\nPress ENTER to return to menu...");
                scanner.nextLine();
            }
        }
    }

    private void printBanner() {
        System.out.println("==========================================================================================");
        System.out.println("       🎓 BABU BANARASI DAS UNIVERSITY (BBDU) - CAMPUS PLACEMENT & RECRUITMENT TRACKER    ");
        System.out.println("            System Lead: Shaswat Jaiswal | Roll No: 26 / 12502 | BCA DS & AI             ");
        System.out.println("               Powered by MongoDB Community Server 8.3 & Java Synchronous Driver          ");
        System.out.println("==========================================================================================");
    }

    private void printMainMenu() {
        System.out.println("\n-------------------------------- MASTER CONSOLE MENU -------------------------------------");
        System.out.println(" [1]  View All Students (Formatted Directory)");
        System.out.println(" [2]  Add New Student Profile (Task 2.1: insertOne Manual Entry)");
        System.out.println(" [3]  Bulk Seed Database (Task 2.2: insertMany >=65 Production Records)");
        System.out.println(" [4]  Filter Students by Minimum CGPA Cutoff (Task 3.1: $gte Comparison)");
        System.out.println(" [5]  Filter Students by CGPA Range (Task 3.1: $gte + $lte Comparison)");
        System.out.println(" [6]  Search Students by Technical Skills (Task 3.2: $in / $all Array Query)");
        System.out.println(" [7]  Complex Multi-Criteria Drive Eligibility (Task 3.2: $and + $or + $in)");
        System.out.println(" [8]  Update Student Placement Status (Task 3.3: updateOne - Mark Placed)");
        System.out.println(" [9]  Bulk Update Batch Graduation Year (Task 3.3: updateMany)");
        System.out.println(" [10] Delete Operations (Task 3.4: deleteOne / deleteMany Cleanup)");
        System.out.println(" [11] Real-Time Placement Analytics & KPIs (Task 4.1: Aggregation Pipelines)");
        System.out.println(" [12] Query Performance & Execution Stats (Task 4.3: .explain('executionStats'))");
        System.out.println(" [13] View Recruiting Companies & Drive Cutoffs");
        System.out.println(" [14] View Student Drive Applications & Status");
        System.out.println(" [15] Launch Desktop GUI Dashboard (Swing Interface)");
        System.out.println(" [0]  Exit System");
        System.out.println("------------------------------------------------------------------------------------------");
    }

    // Task 2.3: Basic Find & Tabular Output
    private void handleViewAllStudents() {
        List<Student> students = studentDAO.findAll();
        System.out.println("\n>>> TOTAL REGISTERED STUDENTS: " + students.size());
        printStudentTable(students);
    }

    // Task 2.1: insertOne Module
    private void handleInsertOneStudent() {
        System.out.println("\n--- TASK 2.1: MANUAL STUDENT ENTRY (insertOne) ---");
        try {
            System.out.print("Enter Roll Number (e.g., 12520): ");
            String roll = scanner.nextLine().trim();
            if (roll.isEmpty()) {
                System.out.println("[ERROR] Roll number cannot be empty.");
                return;
            }

            Student existing = studentDAO.findByRollNumber(roll);
            if (existing != null) {
                System.out.println("[ERROR] Student with Roll Number '" + roll + "' already exists: " + existing.getName());
                return;
            }

            System.out.print("Enter Full Name: ");
            String name = scanner.nextLine().trim();

            System.out.print("Enter Department (e.g. BCA DS & AI, B.Tech CSE, MCA): ");
            String dept = scanner.nextLine().trim();

            System.out.print("Enter CGPA (0.0 - 10.0): ");
            double cgpa = Double.parseDouble(scanner.nextLine().trim());

            System.out.print("Enter Technical Skills (comma-separated, e.g. Java, Python, SQL): ");
            String skillsInput = scanner.nextLine().trim();
            List<String> skills = new ArrayList<>();
            for (String s : skillsInput.split(",")) {
                if (!s.trim().isEmpty()) skills.add(s.trim());
            }

            System.out.print("Is Placed? (true/false): ");
            boolean isPlaced = Boolean.parseBoolean(scanner.nextLine().trim());

            String company = "None";
            double ctc = 0.0;
            if (isPlaced) {
                System.out.print("Enter Placed Company Name: ");
                company = scanner.nextLine().trim();
                System.out.print("Enter CTC Package in LPA: ");
                ctc = Double.parseDouble(scanner.nextLine().trim());
            }

            System.out.print("Enter Email Address: ");
            String email = scanner.nextLine().trim();

            System.out.print("Enter Gender (Male/Female/Other): ");
            String gender = scanner.nextLine().trim();

            Student student = new Student(roll, name, dept, cgpa, skills, isPlaced, company, ctc, email, gender, 2026);
            boolean success = studentDAO.insertOne(student);

            if (success) {
                System.out.println("\n[SUCCESS] Student profile successfully ingested into MongoDB collection 'students'!");
                System.out.println("Inserted Record: " + student);
            } else {
                System.out.println("\n[ERROR] Failed to insert student record.");
            }
        } catch (Exception e) {
            System.out.println("[ERROR] Invalid input: " + e.getMessage());
        }
    }

    // Task 2.2: Bulk Ingestion (insertMany)
    private void handleBulkSeed() {
        System.out.println("\n--- TASK 2.2: BULK INGESTION (insertMany) ---");
        System.out.print("Reset existing database collections first? (y/n): ");
        String ans = scanner.nextLine().trim().toLowerCase();
        boolean reset = ans.equals("y") || ans.equals("yes");

        dataSeeder.seedAll(reset);
        System.out.println("\n[SUCCESS] Bulk seeding executed successfully!");
        System.out.println("Current student count in DB: " + studentDAO.count() + " (Threshold >=65 satisfied)");
    }

    // Task 3.1: Comparison Operator $gte
    private void handleCgpaFilter() {
        System.out.println("\n--- TASK 3.1: FILTER BY MINIMUM CGPA ($gte OPERATOR) ---");
        try {
            System.out.print("Enter Minimum CGPA Cutoff (e.g. 8.0): ");
            double minCgpa = Double.parseDouble(scanner.nextLine().trim());

            List<Student> results = studentDAO.findByMinCgpa(minCgpa);
            System.out.println("\n>>> Found " + results.size() + " students with CGPA >= " + minCgpa);
            printStudentTable(results);
        } catch (Exception e) {
            System.out.println("[ERROR] Invalid CGPA input: " + e.getMessage());
        }
    }

    // Task 3.1: Comparison Operator $gte & $lte
    private void handleCgpaRangeFilter() {
        System.out.println("\n--- TASK 3.1: FILTER BY CGPA RANGE ($gte AND $lte OPERATORS) ---");
        try {
            System.out.print("Enter Lower CGPA Bound (e.g. 7.5): ");
            double min = Double.parseDouble(scanner.nextLine().trim());
            System.out.print("Enter Upper CGPA Bound (e.g. 9.0): ");
            double max = Double.parseDouble(scanner.nextLine().trim());

            List<Student> results = studentDAO.findByCgpaRange(min, max);
            System.out.println("\n>>> Found " + results.size() + " students with CGPA between " + min + " and " + max);
            printStudentTable(results);
        } catch (Exception e) {
            System.out.println("[ERROR] Invalid range: " + e.getMessage());
        }
    }

    // Task 3.2: Array Operators ($in, $all)
    private void handleSkillFilter() {
        System.out.println("\n--- TASK 3.2: SEARCH BY TECHNICAL SKILLS ($in / $all ARRAY OPERATORS) ---");
        System.out.print("Enter comma-separated skills to search (e.g. MongoDB, Python): ");
        String skillsInput = scanner.nextLine().trim();
        List<String> skills = new ArrayList<>();
        for (String s : skillsInput.split(",")) {
            if (!s.trim().isEmpty()) skills.add(s.trim());
        }

        if (skills.isEmpty()) {
            System.out.println("[ERROR] No skills entered.");
            return;
        }

        System.out.print("Match ANY skill ($in) or ALL skills ($all)? Enter 'any' or 'all': ");
        String mode = scanner.nextLine().trim().toLowerCase();

        List<Student> results;
        if (mode.equals("all")) {
            results = studentDAO.findByAllSkills(skills);
            System.out.println("\n>>> Found " + results.size() + " students possessing ALL skills: " + skills);
        } else {
            results = studentDAO.findByAnySkills(skills);
            System.out.println("\n>>> Found " + results.size() + " students possessing ANY of: " + skills);
        }
        printStudentTable(results);
    }

    // Task 3.2: Logical Operators ($and, $or, $in)
    private void handleComplexEligibilitySearch() {
        System.out.println("\n--- TASK 3.2: COMPLEX DRIVE ELIGIBILITY ($and, $or, $in LOGICAL OPERATORS) ---");
        try {
            System.out.print("Enter Required Minimum CGPA: ");
            double minCgpa = Double.parseDouble(scanner.nextLine().trim());

            System.out.print("Enter Target Departments (comma-separated, e.g. BCA DS & AI, B.Tech CSE): ");
            String deptsInput = scanner.nextLine().trim();
            List<String> depts = new ArrayList<>();
            for (String d : deptsInput.split(",")) {
                if (!d.trim().isEmpty()) depts.add(d.trim());
            }

            System.out.print("Enter Alternative Primary Skill (e.g. Java): ");
            String skill = scanner.nextLine().trim();

            List<Student> results = studentDAO.findEligibleCandidates(minCgpa, depts, skill);
            System.out.println("\n>>> Found " + results.size() + " eligible unplaced candidates matching criteria.");
            printStudentTable(results);
        } catch (Exception e) {
            System.out.println("[ERROR] Invalid query criteria: " + e.getMessage());
        }
    }

    // Task 3.3: updateOne
    private void handleUpdatePlacementStatus() {
        System.out.println("\n--- TASK 3.3: UPDATE PLACEMENT STATUS (updateOne) ---");
        System.out.print("Enter Student Roll Number to update: ");
        String roll = scanner.nextLine().trim();

        Student student = studentDAO.findByRollNumber(roll);
        if (student == null) {
            System.out.println("[ERROR] Student with Roll Number '" + roll + "' not found.");
            return;
        }

        System.out.println("Selected Student: " + student.getName() + " | Dept: " + student.getDepartment() + " | Current Placed: " + student.isPlaced());
        System.out.print("Enter Recruiter Company Name: ");
        String company = scanner.nextLine().trim();

        System.out.print("Enter Offered Package CTC (LPA): ");
        double ctc;
        try {
            ctc = Double.parseDouble(scanner.nextLine().trim());
        } catch (Exception e) {
            System.out.println("[ERROR] Invalid CTC amount.");
            return;
        }

        boolean updated = studentDAO.updatePlacementStatus(roll, company, ctc);
        if (updated) {
            System.out.println("\n[SUCCESS] Placement record updated successfully!");
            Student refreshed = studentDAO.findByRollNumber(roll);
            System.out.println("Updated Profile: " + refreshed);
        } else {
            System.out.println("\n[ERROR] Failed to update placement status.");
        }
    }

    // Task 3.3: updateMany
    private void handleBulkUpdateGraduationYear() {
        System.out.println("\n--- TASK 3.3: BULK UPDATE (updateMany) ---");
        System.out.print("Enter Department Name to update (e.g. BCA DS & AI): ");
        String dept = scanner.nextLine().trim();

        System.out.print("Enter New Graduation Year (e.g. 2027): ");
        try {
            int year = Integer.parseInt(scanner.nextLine().trim());
            long count = studentDAO.updateGraduationYearForDepartment(dept, year);
            System.out.println("\n[SUCCESS] Bulk updated graduation_year to " + year + " for " + count + " students in " + dept);
        } catch (Exception e) {
            System.out.println("[ERROR] Invalid year input: " + e.getMessage());
        }
    }

    // Task 3.4: deleteOne / deleteMany
    private void handleDeleteOperations() {
        System.out.println("\n--- TASK 3.4: DELETE OPERATIONS (deleteOne / deleteMany) ---");
        System.out.println(" 1. Delete single student by Roll Number (deleteOne)");
        System.out.println(" 2. Clean up test records (deleteMany)");
        System.out.print("Choose action (1 or 2): ");
        String choice = scanner.nextLine().trim();

        if (choice.equals("1")) {
            System.out.print("Enter Roll Number to delete: ");
            String roll = scanner.nextLine().trim();
            System.out.print("Are you sure you want to delete student '" + roll + "'? (yes/no): ");
            String confirm = scanner.nextLine().trim().toLowerCase();
            if (confirm.equals("yes") || confirm.equals("y")) {
                boolean del = studentDAO.deleteOne(roll);
                if (del) {
                    System.out.println("[SUCCESS] Student record '" + roll + "' deleted.");
                } else {
                    System.out.println("[ERROR] No student record matched roll number '" + roll + "'.");
                }
            } else {
                System.out.println("[INFO] Deletion cancelled.");
            }
        } else if (choice.equals("2")) {
            long deleted = studentDAO.deleteTestRecords();
            System.out.println("[SUCCESS] Cleaned up " + deleted + " test student records.");
        } else {
            System.out.println("[ERROR] Invalid choice.");
        }
    }

    // Task 4.1: Aggregation Analytics
    private void handleAnalyticsReport() {
        System.out.println("\n==========================================================================================");
        System.out.println("                 📊 TASK 4.1: REAL-TIME PLACEMENT ANALYTICS & AGGREGATION                 ");
        System.out.println("==========================================================================================");

        // 1. Overall KPI Summary
        Document kpi = analyticsDAO.getOverallKpiSummary();
        System.out.println("\n>>> [1] INSTITUTION OVERALL PLACEMENT PERFORMANCE KPI:");
        System.out.println("------------------------------------------------------------------------------------------");
        System.out.printf("  * Total Students Registered : %d\n", kpi.getInteger("totalStudents", 0));
        System.out.printf("  * Placed Students           : %d\n", kpi.getInteger("totalPlaced", 0));
        System.out.printf("  * Unplaced Candidates       : %d\n", kpi.getInteger("totalUnplaced", 0));
        double placementRate = kpi.get("placementPercentage", Number.class) != null ? kpi.get("placementPercentage", Number.class).doubleValue() : 0.0;
        double avgCgpa = kpi.get("avgCgpa", Number.class) != null ? kpi.get("avgCgpa", Number.class).doubleValue() : 0.0;
        double avgPackage = kpi.get("avgPackageLpa", Number.class) != null ? kpi.get("avgPackageLpa", Number.class).doubleValue() : 0.0;
        double maxPackage = kpi.get("highestPackageLpa", Number.class) != null ? kpi.get("highestPackageLpa", Number.class).doubleValue() : 0.0;
        System.out.printf("  * Placement Success Rate    : %.2f%%\n", placementRate);
        System.out.printf("  * Overall Average CGPA      : %.2f\n", avgCgpa);
        System.out.printf("  * Average Package (Placed)  : %.2f LPA\n", avgPackage);
        System.out.printf("  * Highest Package Offered   : %.2f LPA\n", maxPackage);
        System.out.println("------------------------------------------------------------------------------------------");

        // 2. Department-wise Placement Rates
        System.out.println("\n>>> [2] DEPARTMENT-WISE PLACEMENT RATES & PACKAGES ($group, $cond, $project):");
        List<Document> deptRates = analyticsDAO.getDepartmentPlacementRates();
        String[] deptHeaders = {"Department", "Total Students", "Placed", "Unplaced", "Placement Rate (%)", "Avg CTC (LPA)", "Max CTC (LPA)"};
        List<String[]> deptRows = new ArrayList<>();
        for (Document d : deptRates) {
            deptRows.add(new String[]{
                    d.getString("department"),
                    String.valueOf(d.getInteger("totalStudents")),
                    String.valueOf(d.getInteger("placedCount")),
                    String.valueOf(d.getInteger("unplacedCount")),
                    String.format("%.2f%%", d.getDouble("placementRate")),
                    String.format("%.2f", d.getDouble("avgPackage")),
                    String.format("%.2f", d.getDouble("maxPackage"))
            });
        }
        TablePrinter.printTable(deptHeaders, deptRows);

        // 3. Department CGPA Stats
        System.out.println("\n>>> [3] DEPARTMENT-WISE ACADEMIC CGPA DISTRIBUTION ($group, $avg, $max, $min):");
        List<Document> cgpaStats = analyticsDAO.getDepartmentCgpaStats();
        String[] cgpaHeaders = {"Department", "Total Students", "Avg CGPA", "Max CGPA", "Min CGPA"};
        List<String[]> cgpaRows = new ArrayList<>();
        for (Document d : cgpaStats) {
            cgpaRows.add(new String[]{
                    d.getString("department"),
                    String.valueOf(d.getInteger("totalStudents")),
                    String.format("%.2f", d.getDouble("avgCgpa")),
                    String.format("%.2f", d.getDouble("maxCgpa")),
                    String.format("%.2f", d.getDouble("minCgpa"))
            });
        }
        TablePrinter.printTable(cgpaHeaders, cgpaRows);

        // 4. Top Scorers Pipeline
        System.out.println("\n>>> [4] TOP ACADEMIC PERFORMERS ($sort, $limit: 5):");
        List<Document> topStudents = analyticsDAO.getTopScorers(5);
        String[] topHeaders = {"Roll No", "Student Name", "Department", "CGPA", "Placement Status", "Placed Company", "CTC (LPA)"};
        List<String[]> topRows = new ArrayList<>();
        for (Document d : topStudents) {
            topRows.add(new String[]{
                    d.getString("roll_number"),
                    d.getString("name"),
                    d.getString("department"),
                    String.format("%.2f", d.getDouble("cgpa")),
                    Boolean.TRUE.equals(d.getBoolean("is_placed")) ? "PLACED" : "UNPLACED",
                    d.getString("placed_company"),
                    String.format("%.2f", d.getDouble("package_ctc"))
            });
        }
        TablePrinter.printTable(topHeaders, topRows);

        // 5. Company Recruitment Distribution
        System.out.println("\n>>> [5] RECRUITING COMPANY RECRUITMENT DISTRIBUTION ($group, $sort):");
        List<Document> companyDist = analyticsDAO.getCompanyRecruitmentDistribution();
        String[] compDistHeaders = {"Recruiter Company", "Students Recruited", "Avg CTC (LPA)", "Max CTC (LPA)"};
        List<String[]> compDistRows = new ArrayList<>();
        for (Document d : companyDist) {
            compDistRows.add(new String[]{
                    d.getString("company"),
                    String.valueOf(d.getInteger("recruitsCount")),
                    String.format("%.2f", d.getDouble("avgPackage")),
                    String.format("%.2f", d.getDouble("maxPackage"))
            });
        }
        TablePrinter.printTable(compDistHeaders, compDistRows);

        // 6. In-Demand Skills
        System.out.println("\n>>> [6] MOST IN-DEMAND TECHNICAL SKILLS ($unwind, $group, $sort):");
        List<Document> skillStats = analyticsDAO.getTopInDemandSkills(8);
        String[] skillHeaders = {"Technical Skill", "Total Candidates", "Placed Candidates", "Placement Rate (%)"};
        List<String[]> skillRows = new ArrayList<>();
        for (Document d : skillStats) {
            skillRows.add(new String[]{
                    d.getString("skill"),
                    String.valueOf(d.getInteger("totalStudents")),
                    String.valueOf(d.getInteger("placedStudents")),
                    String.format("%.1f%%", d.getDouble("placementRate"))
            });
        }
        TablePrinter.printTable(skillHeaders, skillRows);
    }

    // Task 4.3: .explain("executionStats")
    private void handleExplainQueryPerformance() {
        System.out.println("\n==========================================================================================");
        System.out.println("             ⚡ TASK 4.3: QUERY PERFORMANCE ANALYSIS (.explain('executionStats'))          ");
        System.out.println("==========================================================================================");
        System.out.print("Enter CGPA cutoff to benchmark (default 8.0): ");
        String line = scanner.nextLine().trim();
        double cutoff = line.isEmpty() ? 8.0 : Double.parseDouble(line);

        Document explainDoc = studentDAO.explainCgpaQuery(cutoff);
        Document execStats = (Document) explainDoc.get("executionStats");
        Document queryPlanner = (Document) explainDoc.get("queryPlanner");

        System.out.println("\n[MongoDB Query Optimizer Explain Output]");
        if (execStats != null) {
            System.out.println("------------------------------------------------------------------------------------------");
            System.out.printf("  * Execution Success           : %s\n", execStats.get("executionSuccess"));
            System.out.printf("  * Execution Time (Millis)     : %s ms\n", execStats.get("executionTimeMillis"));
            System.out.printf("  * Total Documents Returned    : %s\n", execStats.get("nReturned"));
            System.out.printf("  * Total Keys (Index) Examined : %s\n", execStats.get("totalKeysExamined"));
            System.out.printf("  * Total Documents Scanned     : %s\n", execStats.get("totalDocsExamined"));
            Document execStages = (Document) execStats.get("executionStages");
            if (execStages != null) {
                System.out.printf("  * Stage Plan                  : %s\n", execStages.get("stage"));
                if (execStages.get("inputStage") != null) {
                    Document inStage = (Document) execStages.get("inputStage");
                    System.out.printf("  * Index Stage                 : %s (Index: %s)\n", inStage.get("stage"), inStage.get("indexName"));
                }
            }
            System.out.println("------------------------------------------------------------------------------------------");
            System.out.println("[ANALYSIS] The query leverages the B-Tree index on 'cgpa'.");
            System.out.println("Notice that 'totalKeysExamined' closely matches 'nReturned', avoiding full-collection scan (COLLSCAN)!");
        } else {
            System.out.println(explainDoc.toJson());
        }
    }

    private void handleViewCompanies() {
        List<Company> companies = companyDAO.findAll();
        System.out.println("\n>>> REGISTERED RECRUITING COMPANIES: " + companies.size());
        String[] headers = {"Company ID", "Company Name", "Industry", "Role Offered", "Min CGPA", "Package (LPA)", "Drive Date"};
        List<String[]> rows = new ArrayList<>();
        for (Company c : companies) {
            rows.add(new String[]{
                    c.getCompanyId(),
                    c.getName(),
                    c.getIndustry(),
                    c.getRoleOffered(),
                    String.format("%.1f", c.getMinCgpa()),
                    String.format("%.1f LPA", c.getPackageOffered()),
                    c.getDriveDate()
            });
        }
        TablePrinter.printTable(headers, rows);
    }

    private void handleViewApplications() {
        List<Application> apps = applicationDAO.findAll();
        System.out.println("\n>>> TOTAL RECRUITMENT APPLICATIONS: " + apps.size());
        String[] headers = {"App ID", "Roll No", "Student Name", "Company", "Application Date", "Status", "Remarks"};
        List<String[]> rows = new ArrayList<>();
        for (Application a : apps) {
            rows.add(new String[]{
                    a.getApplicationId(),
                    a.getRollNumber(),
                    a.getStudentName(),
                    a.getCompanyName(),
                    a.getApplicationDate(),
                    a.getStatus(),
                    a.getRemarks()
            });
        }
        TablePrinter.printTable(headers, rows);
    }

    private void handleLaunchGui() {
        System.out.println("\n[INFO] Launching Desktop Swing GUI Dashboard...");
        new Thread(() -> {
            try {
                PlacementDashboardGUI.launch();
            } catch (Exception e) {
                System.out.println("[ERROR] Failed to start GUI: " + e.getMessage());
            }
        }).start();
        System.out.println("[INFO] Desktop GUI launched in background thread.");
    }

    private void printStudentTable(List<Student> students) {
        if (students == null || students.isEmpty()) {
            System.out.println("No matching student records found.");
            return;
        }

        String[] headers = {"Roll No", "Student Name", "Department", "CGPA", "Placement Status", "Placed Company", "CTC (LPA)", "Top Skills"};
        List<String[]> rows = new ArrayList<>();
        for (Student s : students) {
            String skillsStr = String.join(", ", s.getSkills());
            if (skillsStr.length() > 30) {
                skillsStr = skillsStr.substring(0, 27) + "...";
            }
            rows.add(new String[]{
                    s.getRollNumber(),
                    s.getName(),
                    s.getDepartment(),
                    String.format("%.2f", s.getCgpa()),
                    s.isPlaced() ? "PLACED" : "UNPLACED",
                    s.getPlacedCompany(),
                    String.format("%.2f", s.getPackageCTC()),
                    skillsStr
            });
        }
        TablePrinter.printTable(headers, rows);
    }
}
