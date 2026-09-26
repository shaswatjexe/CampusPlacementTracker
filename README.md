# 🎓 Campus Placement & Recruitment Tracker
### Academic Project — Babu Banarasi Das University (BBDU)
**Author :** Shaswat Jaiswal  

**Degree & Specialization:** BCA (Data Science & Artificial Intelligence)  
**Database Technology:** MongoDB Community Server 8.3 & MongoDB Compass  
**Application Stack:** Java SE 26 (Runtime target 17+), Maven 3.9.9, MongoDB Synchronous Java Driver (`org.mongodb:mongodb-driver-sync`)

---

## 📌 Project Overview
The **Campus Placement & Recruitment Tracker** is a production-grade, enterprise-modeled desktop and command-line information management system built for university placement cells (such as BBDU Training & Placement Division). It manages student candidate pools, company recruitment drives, eligibility filtering, application lifecycle status, and generates real-time institutional placement analytics through MongoDB aggregation pipelines.

---

## 🚀 Key Features by Implementation Phase

### Phase 1: Environment & Architecture
* **MongoDB Community Server (v8.3):** Operating as a dedicated background Windows service (`net start MongoDB`).
* **MongoDB Compass Integration:** Visual schema exploration, document inspection, and index management via `mongodb://localhost:27017`.
* **Database & Collections:** Initialized database `placement_db` with three collections:
  - `students`: Stores candidate academic records, technical skills array, and placement outcomes.
  - `companies`: Stores recruiter profiles, drive dates, minimum CGPA cutoffs, and salary packages.
  - `applications`: Tracks individual student drive applications through stages (Applied, Shortlisted, Interviewed, Selected, Rejected).
* **Indexes:** Optimized B-Tree indexes on `roll_number` (Unique), `cgpa` (Descending), compound `[department, is_placed]`, and multikey index on `skills`.

### Phase 2: Core Data Ingestion & Seeding
* **Single Student Profile Entry (`insertOne`):** Interactive console and graphical form for manual ingestion with validation.
* **Bulk Production Ingestion (`insertMany`):** Pre-seeded with **72 production-grade records** (exceeding the university requirement of >=65 records) spanning multiple departments:
  - `BCA DS & AI` (Featuring lead profile: **Shaswat Jaiswal**)
  - `B.Tech CSE` (Computer Science & Engineering)
  - `B.Tech IT` (Information Technology)
  - `MCA` (Master of Computer Applications)
  - `B.Tech ECE` (Electronics & Communication Engineering)
* **10 Recruiter Profiles:** Google Cloud, Microsoft, Amazon AWS, Deloitte, TCS Digital, Infosys Power Programmer, Zomato, Oracle, Wipro Turbo, and Cognizant.
* **20 Drive Applications:** Seeded with realistic progression data.

### Phase 3: CRUD Operations & MongoDB Operators
* **Comparison Operators (`$eq`, `$gt`, `$gte`, `$lt`, `$lte`, `$ne`):**
  - `$gte` for corporate eligibility screening (e.g. CGPA >= 8.0).
  - `$gte` & `$lte` for CGPA range filtering.
  - `$ne` for identifying unplaced students awaiting placement drives.
* **Array & Logical Operators (`$in`, `$all`, `$and`, `$or`, `$regex`):**
  - `$in` and `$all` on the `skills` array for technical talent matching.
  - Complex nested logic: `$and` with `$or` for multi-department and skill fallback criteria.
* **Update Operations (`updateOne`, `updateMany`):**
  - `updateOne` with `$set` to transition students to `is_placed: true`, assign recruiter company and CTC package.
  - `updateMany` for batch operations (e.g. updating department graduation years).
* **Delete Operations (`deleteOne`, `deleteMany`):**
  - `deleteOne` for withdrawn candidates.
  - `deleteMany` for automated cleanup of test profiles.

### Phase 4: Real-Time Aggregation Analytics & Interfaces
* **Multi-Stage Aggregation Pipelines:**
  1. **Institutional KPI Summary:** Overall student count, placement rate (%), average CGPA, average CTC package, highest offer.
  2. **Department Performance Breakdown:** Uses `$group`, `$sum`, `$cond`, `$multiply`, `$divide`, and `$round` to calculate department-specific placement percentages.
  3. **Top Academic Scorers:** Uses `$sort`, `$limit`, and `$project`.
  4. **Recruiter Distribution:** Uses `$match`, `$group`, and `$sort` to determine top hiring partners.
  5. **In-Demand Skills Frequency:** Uses `$unwind`, `$group`, and `$sort` on the multikey skills array.
* **Dual Interfaces:**
  - **Console CLI Menu:** ANSI-styled ASCII table-based interactive management system.
  - **Swing GUI Dashboard:** Visual tabbed interface featuring filter toolbars, registration forms, KPI summary cards, and live analytics tables.
* **Query Performance Benchmarking:** Incorporates `.explain("executionStats")` reporting total execution time, `nReturned`, `totalKeysExamined`, and `totalDocsExamined`.

---

## 📂 Project Directory Structure

```
CampusPlacementTracker/
├── pom.xml                                      # Maven project configuration with MongoDB Driver
├── README.md                                    # Project documentation and user manual
├── run-cli.bat                                  # Batch script: launch interactive CLI Menu
├── run-gui.bat                                  # Batch script: launch Swing GUI Dashboard
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/bbdu/placement/
│   │   │       ├── Main.java                    # Application bootstrap & entry point
│   │   │       ├── config/
│   │   │       │   └── MongoConfig.java         # Connection pool, db setup, index management
│   │   │       ├── dao/
│   │   │       │   ├── StudentDAO.java          # Student CRUD, comparison, logical, explain
│   │   │       │   ├── CompanyDAO.java          # Company catalog and eligibility
│   │   │       │   ├── ApplicationDAO.java      # Application workflow management
│   │   │       │   └── AnalyticsDAO.java        # Multi-stage MongoDB Aggregation Pipelines
│   │   │       ├── data/
│   │   │       │   └── DataSeeder.java          # Ingestion script for 72 students, 10 companies
│   │   │       ├── model/
│   │   │       │   ├── Student.java             # Student BSON POJO
│   │   │       │   ├── Company.java             # Company BSON POJO
│   │   │       │   └── Application.java         # Application BSON POJO
│   │   │       ├── ui/
│   │   │       │   ├── ConsoleMenu.java         # Interactive CLI Terminal Menu
│   │   │       │   └── PlacementDashboardGUI.java # Modern Swing GUI Dashboard
│   │   │       └── util/
│   │   │           └── TablePrinter.java        # ASCII tabular formatting utility
│   │   └── resources/
│   │       └── logback.xml                      # Logback logging configuration
│   └── test/
│       └── java/com/bbdu/placement/
│           └── SystemVerificationTest.java      # Comprehensive automated verification suite
├── target/
│   └── campus-placement-tracker-1.0.0-jar-with-dependencies.jar # Standalone executable fat JAR
└── reports/
    └── BBDU_Campus_Placement_Tracker_Project_Report.md # Formal academic project report
```

---

## 🛠️ How to Run the Project

### Prerequisites
1. **MongoDB Community Server 8.3:** Running on default port `27017` (`net start MongoDB`).
2. **Java 17 or higher (Java 26 verified).**

### Quick Launch Batch Scripts
Double-click either batch file from the project directory:
* **`run-cli.bat`** — Launches the interactive Console Menu.
* **`run-gui.bat`** — Launches the desktop Swing GUI Dashboard.

### Running via Command Line
```powershell
# Run the Interactive Console Menu
java -jar target\campus-placement-tracker-1.0.0-jar-with-dependencies.jar

# Run the Graphical User Interface (GUI) directly
java -jar target\campus-placement-tracker-1.0.0-jar-with-dependencies.jar --gui
```

### Running the End-to-End Verification Test Suite
```powershell
java -cp "target\test-classes;target\campus-placement-tracker-1.0.0-jar-with-dependencies.jar" com.bbdu.placement.SystemVerificationTest
```

---

## 🔍 Connecting via MongoDB Compass
1. Open **MongoDB Compass**.
2. Paste the connection string: `mongodb://localhost:27017` and click **Connect**.
3. Locate the database: **`placement_db`**.
4. You will observe the three collections:
   - `students` (72+ documents)
   - `companies` (10 documents)
   - `applications` (20 documents)
5. Under `students` -> **Indexes**, inspect the B-Tree indexes: `roll_number_1`, `cgpa_-1`, `department_1_is_placed_1`, `skills_1`.
