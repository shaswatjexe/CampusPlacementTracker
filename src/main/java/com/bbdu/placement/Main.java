package com.bbdu.placement;

import com.bbdu.placement.config.MongoConfig;
import com.bbdu.placement.dao.StudentDAO;
import com.bbdu.placement.data.DataSeeder;
import com.bbdu.placement.ui.ConsoleMenu;
import com.bbdu.placement.ui.PlacementDashboardGUI;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Master Application Entry Point for BBDU Campus Placement & Recruitment Tracker.
 *
 * Project Lead: Shaswat Jaiswal
 * Roll No: 26 / 12502
 * Department: BCA DS & AI (Babu Banarasi Das University)
 * Database: MongoDB Community Server 8.3 (Synchronous Java Driver)
 */
public class Main {
    private static final Logger logger = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) {
        // Register shutdown hook for clean MongoClient connection cleanup
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            logger.info("Executing graceful system shutdown...");
            MongoConfig.close();
        }));

        System.out.println("==========================================================================================");
        System.out.println("      🚀 INITIALIZING BBDU CAMPUS PLACEMENT & RECRUITMENT TRACKER SYSTEM                 ");
        System.out.println("==========================================================================================");

        try {
            // Task 1.1 & 1.4: Verify MongoDB connection & initialize database collections/indexes
            logger.info("Verifying MongoDB Server connection at {}", MongoConfig.CONNECTION_URI);
            MongoConfig.getClient();
            MongoConfig.initializeIndexes();

            // Check if database needs automatic first-time seeding
            StudentDAO studentDAO = new StudentDAO();
            if (studentDAO.count() < 65) {
                logger.info("Database student count ({}) is below mandatory project threshold (65).", studentDAO.count());
                logger.info("Initiating automatic production seeding for BBDU departments...");
                DataSeeder seeder = new DataSeeder();
                seeder.seedAll(true);
            } else {
                logger.info("Database verified with {} student records.", studentDAO.count());
            }

            // Check command line arguments for mode
            boolean launchGui = false;
            for (String arg : args) {
                if ("--gui".equalsIgnoreCase(arg) || "-g".equalsIgnoreCase(arg)) {
                    launchGui = true;
                    break;
                }
            }

            if (launchGui) {
                logger.info("Launching Desktop Swing GUI Dashboard...");
                PlacementDashboardGUI.launch();
            } else {
                logger.info("Starting Master Interactive Console Menu...");
                ConsoleMenu menu = new ConsoleMenu();
                menu.start();
            }

        } catch (Exception e) {
            System.err.println("\n[FATAL ERROR] Failed to start application: " + e.getMessage());
            System.err.println("Please make sure MongoDB Community Server is running (net start MongoDB).");
            e.printStackTrace();
        }
    }
}
