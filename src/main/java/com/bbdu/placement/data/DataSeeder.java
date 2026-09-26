package com.bbdu.placement.data;

import com.bbdu.placement.dao.ApplicationDAO;
import com.bbdu.placement.dao.CompanyDAO;
import com.bbdu.placement.dao.StudentDAO;
import com.bbdu.placement.model.Application;
import com.bbdu.placement.model.Company;
import com.bbdu.placement.model.Student;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Task 2.2: Bulk insertion script (insertMany) to load at least 65 production-grade
 * student records across various engineering and computer application departments.
 *
 * Explicitly features:
 * Shaswat Jaiswal (Roll No: 26 / 12502, BCA DS & AI) - BBDU Star Performer
 */
public class DataSeeder {
    private static final Logger logger = LoggerFactory.getLogger(DataSeeder.class);

    private final StudentDAO studentDAO;
    private final CompanyDAO companyDAO;
    private final ApplicationDAO applicationDAO;

    public DataSeeder() {
        this.studentDAO = new StudentDAO();
        this.companyDAO = new CompanyDAO();
        this.applicationDAO = new ApplicationDAO();
    }

    public void seedAll(boolean resetExisting) {
        if (resetExisting) {
            logger.info("Resetting existing database collections for clean seeding...");
            studentDAO.dropAndReset();
            companyDAO.dropAndReset();
            applicationDAO.dropAndReset();
        }

        seedCompanies();
        seedStudents();
        seedApplications();

        logger.info("Database seeding completed successfully! Total Students: {}, Companies: {}, Applications: {}",
                studentDAO.count(), companyDAO.count(), applicationDAO.count());
    }

    public void seedCompanies() {
        if (companyDAO.count() > 0) {
            logger.info("Companies already present ({} records). Skipping seeding.", companyDAO.count());
            return;
        }

        List<Company> companies = Arrays.asList(
                new Company("COMP-001", "Google Cloud", "Product / Cloud", "Cloud Solutions Engineer", 8.5, Arrays.asList("Java", "Python", "Cloud Computing", "MongoDB"), 18.5, "2026-10-15", "Bengaluru / Gurugram"),
                new Company("COMP-002", "Microsoft", "Product / Software", "Software Development Engineer", 8.8, Arrays.asList("C++", "Java", "Data Structures", "Algorithms"), 22.0, "2026-10-20", "Hyderabad / Noida"),
                new Company("COMP-003", "Amazon AWS", "Product / E-Commerce", "SDE-1", 8.2, Arrays.asList("Java", "AWS", "Distributed Systems", "SQL"), 20.0, "2026-11-05", "Bengaluru"),
                new Company("COMP-004", "Deloitte", "Consulting & Advisory", "Technology Analyst", 7.0, Arrays.asList("SQL", "Python", "PowerBI", "Java"), 8.5, "2026-11-12", "Gurugram / Mumbai"),
                new Company("COMP-005", "TCS Digital", "IT Services", "System Engineer (Digital)", 7.5, Arrays.asList("Java", "Python", "Machine Learning", "Web Development"), 7.2, "2026-09-28", "Lucknow / Noida"),
                new Company("COMP-006", "Infosys (Power Programmer)", "IT Services", "Specialist Programmer", 7.8, Arrays.asList("Java", "Spring Boot", "Microservices", "MongoDB"), 9.5, "2026-10-02", "Pune / Bengaluru"),
                new Company("COMP-007", "Zomato", "Product / Tech", "Backend Engineer", 8.0, Arrays.asList("Python", "Go", "Docker", "MongoDB", "Redis"), 14.0, "2026-10-25", "Gurugram"),
                new Company("COMP-008", "Oracle", "Enterprise Software", "Applications Engineer", 7.5, Arrays.asList("Java", "SQL", "Database Internals", "Linux"), 12.0, "2026-11-18", "Bengaluru"),
                new Company("COMP-009", "Wipro Turbo", "IT Services", "Project Engineer", 6.5, Arrays.asList("Java", "C++", "SQL"), 6.5, "2026-11-22", "Greater Noida"),
                new Company("COMP-010", "Cognizant GenC Elevate", "IT Services", "Associate Software Engineer", 6.5, Arrays.asList("Java", "Web Development", "SQL"), 6.8, "2026-12-01", "Kolkata / Noida")
        );

        int count = companyDAO.insertMany(companies);
        logger.info("Seeded {} companies.", count);
    }

    public void seedStudents() {
        if (studentDAO.count() >= 65) {
            logger.info("Students collection already contains {} records (meets project threshold >= 65).", studentDAO.count());
            return;
        }

        List<Student> list = new ArrayList<>();

        // 1. Mandatory Student Detail (Lead Author & Star Student)
        list.add(new Student("26 / 12502", "Shaswat Jaiswal", "BCA DS & AI", 9.48,
                Arrays.asList("Java", "MongoDB", "Python", "Data Science", "Machine Learning", "SQL", "Spring Boot"),
                true, "Google Cloud", 18.5, "shaswat.jaiswal@bbdu.ac.in", "Male", 2026));

        // 2-15: BCA DS & AI Department (Specialization cohort)
        list.add(new Student("12501", "Aarav Sharma", "BCA DS & AI", 9.12, Arrays.asList("Python", "Machine Learning", "TensorFlow", "SQL"), true, "Amazon AWS", 20.0, "aarav.s@bbdu.ac.in", "Male", 2026));
        list.add(new Student("12503", "Ananya Verma", "BCA DS & AI", 8.95, Arrays.asList("Python", "Data Science", "Tableau", "MongoDB"), true, "Deloitte", 8.5, "ananya.v@bbdu.ac.in", "Female", 2026));
        list.add(new Student("12504", "Rohan Gupta", "BCA DS & AI", 8.45, Arrays.asList("Python", "Deep Learning", "NLP", "Java"), true, "TCS Digital", 7.2, "rohan.g@bbdu.ac.in", "Male", 2026));
        list.add(new Student("12505", "Ishita Saxena", "BCA DS & AI", 8.78, Arrays.asList("Data Science", "Python", "SQL", "PowerBI"), true, "Zomato", 14.0, "ishita.s@bbdu.ac.in", "Female", 2026));
        list.add(new Student("12506", "Aditya Srivastava", "BCA DS & AI", 7.82, Arrays.asList("Python", "Machine Learning", "Pandas", "Scikit-Learn"), false, "None", 0.0, "aditya.sr@bbdu.ac.in", "Male", 2026));
        list.add(new Student("12507", "Riya Mishra", "BCA DS & AI", 8.25, Arrays.asList("Python", "Computer Vision", "OpenCV", "MongoDB"), true, "Infosys (Power Programmer)", 9.5, "riya.m@bbdu.ac.in", "Female", 2026));
        list.add(new Student("12508", "Ayush Tiwari", "BCA DS & AI", 7.15, Arrays.asList("Python", "SQL", "Data Analysis"), false, "None", 0.0, "ayush.t@bbdu.ac.in", "Male", 2026));
        list.add(new Student("12509", "Sneha Pandey", "BCA DS & AI", 8.60, Arrays.asList("Data Science", "Python", "R", "SQL"), true, "Deloitte", 8.5, "sneha.p@bbdu.ac.in", "Female", 2026));
        list.add(new Student("12510", "Utkarsh Singh", "BCA DS & AI", 6.85, Arrays.asList("Python", "HTML", "CSS", "SQL"), false, "None", 0.0, "utkarsh.s@bbdu.ac.in", "Male", 2026));
        list.add(new Student("12511", "Divya Rastogi", "BCA DS & AI", 8.35, Arrays.asList("Python", "Machine Learning", "MongoDB", "Data Science"), true, "Oracle", 12.0, "divya.r@bbdu.ac.in", "Female", 2026));
        list.add(new Student("12512", "Mohit Yadav", "BCA DS & AI", 7.40, Arrays.asList("Python", "Data Science", "SQL"), false, "None", 0.0, "mohit.y@bbdu.ac.in", "Male", 2026));
        list.add(new Student("12513", "Pooja Chaurasia", "BCA DS & AI", 7.90, Arrays.asList("Python", "Tableau", "MongoDB", "SQL"), false, "None", 0.0, "pooja.c@bbdu.ac.in", "Female", 2026));
        list.add(new Student("12514", "Kunal Kashyap", "BCA DS & AI", 8.70, Arrays.asList("Machine Learning", "Python", "Docker", "MongoDB"), true, "Zomato", 14.0, "kunal.k@bbdu.ac.in", "Male", 2026));
        list.add(new Student("12515", "Shreya Tripathi", "BCA DS & AI", 8.10, Arrays.asList("Python", "Data Science", "PowerBI"), false, "None", 0.0, "shreya.t@bbdu.ac.in", "Female", 2026));

        // 16-35: B.Tech CSE Department (Core Engineering)
        list.add(new Student("11001", "Vikas Dubey", "B.Tech CSE", 9.60, Arrays.asList("C++", "Java", "Data Structures", "Algorithms", "System Design"), true, "Microsoft", 22.0, "vikas.d@bbdu.ac.in", "Male", 2026));
        list.add(new Student("11002", "Priya Agarwal", "B.Tech CSE", 9.20, Arrays.asList("Java", "Spring Boot", "Microservices", "MongoDB"), true, "Amazon AWS", 20.0, "priya.a@bbdu.ac.in", "Female", 2026));
        list.add(new Student("11003", "Harsh Vardhan", "B.Tech CSE", 8.80, Arrays.asList("Java", "React", "Node.js", "MongoDB", "Docker"), true, "Google Cloud", 18.5, "harsh.v@bbdu.ac.in", "Male", 2026));
        list.add(new Student("11004", "Tanvi Sengupta", "B.Tech CSE", 8.55, Arrays.asList("Java", "Spring Boot", "SQL", "AWS"), true, "Infosys (Power Programmer)", 9.5, "tanvi.s@bbdu.ac.in", "Female", 2026));
        list.add(new Student("11005", "Abhishek Pathak", "B.Tech CSE", 7.75, Arrays.asList("Java", "C++", "SQL", "Web Development"), true, "TCS Digital", 7.2, "abhishek.p@bbdu.ac.in", "Male", 2026));
        list.add(new Student("11006", "Kavya Singhania", "B.Tech CSE", 8.90, Arrays.asList("Java", "Kotlin", "Android Development", "MongoDB"), true, "Zomato", 14.0, "kavya.s@bbdu.ac.in", "Female", 2026));
        list.add(new Student("11007", "Naveen Chauhan", "B.Tech CSE", 7.30, Arrays.asList("Java", "SQL", "Git", "C++"), false, "None", 0.0, "naveen.c@bbdu.ac.in", "Male", 2026));
        list.add(new Student("11008", "Sakshi Shukla", "B.Tech CSE", 8.15, Arrays.asList("Java", "Python", "SQL", "MongoDB"), true, "Oracle", 12.0, "sakshi.s@bbdu.ac.in", "Female", 2026));
        list.add(new Student("11009", "Gaurav Malhotra", "B.Tech CSE", 6.90, Arrays.asList("C++", "SQL", "HTML", "CSS"), false, "None", 0.0, "gaurav.m@bbdu.ac.in", "Male", 2026));
        list.add(new Student("11010", "Meera Nambiar", "B.Tech CSE", 8.40, Arrays.asList("Java", "Spring Boot", "Docker", "Kubernetes"), true, "Deloitte", 8.5, "meera.n@bbdu.ac.in", "Female", 2026));
        list.add(new Student("11011", "Akash Rawat", "B.Tech CSE", 7.60, Arrays.asList("Java", "Python", "SQL"), true, "Wipro Turbo", 6.5, "akash.r@bbdu.ac.in", "Male", 2026));
        list.add(new Student("11012", "Ankita Gautam", "B.Tech CSE", 8.05, Arrays.asList("Java", "React", "SQL", "Git"), true, "Cognizant GenC Elevate", 6.8, "ankita.g@bbdu.ac.in", "Female", 2026));
        list.add(new Student("11013", "Prateek Mehrotra", "B.Tech CSE", 6.45, Arrays.asList("C++", "HTML", "SQL"), false, "None", 0.0, "prateek.m@bbdu.ac.in", "Male", 2026));
        list.add(new Student("11014", "Deepika Joshi", "B.Tech CSE", 8.30, Arrays.asList("Java", "SQL", "MongoDB", "Data Structures"), true, "TCS Digital", 7.2, "deepika.j@bbdu.ac.in", "Female", 2026));
        list.add(new Student("11015", "Siddharth Jain", "B.Tech CSE", 9.05, Arrays.asList("Java", "Go", "Distributed Systems", "MongoDB"), true, "Google Cloud", 18.5, "siddharth.j@bbdu.ac.in", "Male", 2026));
        list.add(new Student("11016", "Ritika Bhardwaj", "B.Tech CSE", 7.20, Arrays.asList("Java", "SQL", "JavaScript"), false, "None", 0.0, "ritika.b@bbdu.ac.in", "Female", 2026));
        list.add(new Student("11017", "Aman Maurya", "B.Tech CSE", 7.80, Arrays.asList("Java", "Spring Boot", "SQL"), true, "Wipro Turbo", 6.5, "aman.m@bbdu.ac.in", "Male", 2026));
        list.add(new Student("11018", "Simran Kaur", "B.Tech CSE", 8.65, Arrays.asList("C++", "Java", "Data Structures", "Algorithms"), true, "Microsoft", 22.0, "simran.k@bbdu.ac.in", "Female", 2026));
        list.add(new Student("11019", "Rahul Upadhyay", "B.Tech CSE", 6.70, Arrays.asList("Java", "HTML", "CSS"), false, "None", 0.0, "rahul.u@bbdu.ac.in", "Male", 2026));
        list.add(new Student("11020", "Swati Bhatt", "B.Tech CSE", 8.10, Arrays.asList("Java", "Python", "SQL", "Spring"), true, "Cognizant GenC Elevate", 6.8, "swati.b@bbdu.ac.in", "Female", 2026));

        // 36-47: B.Tech IT Department (Information Technology)
        list.add(new Student("11201", "Varun Kapoor", "B.Tech IT", 9.10, Arrays.asList("Java", "AWS", "Docker", "DevOps"), true, "Amazon AWS", 20.0, "varun.k@bbdu.ac.in", "Male", 2026));
        list.add(new Student("11202", "Bhavna Bisht", "B.Tech IT", 8.40, Arrays.asList("Java", "Spring Boot", "MongoDB", "SQL"), true, "Infosys (Power Programmer)", 9.5, "bhavna.b@bbdu.ac.in", "Female", 2026));
        list.add(new Student("11203", "Yashwant Rao", "B.Tech IT", 7.90, Arrays.asList("Python", "SQL", "Networking", "Linux"), true, "TCS Digital", 7.2, "yashwant.r@bbdu.ac.in", "Male", 2026));
        list.add(new Student("11204", "Pallavi Roy", "B.Tech IT", 8.50, Arrays.asList("Java", "Angular", "Node.js", "MongoDB"), true, "Oracle", 12.0, "pallavi.r@bbdu.ac.in", "Female", 2026));
        list.add(new Student("11205", "Mayank Soni", "B.Tech IT", 6.80, Arrays.asList("Java", "SQL", "HTML"), false, "None", 0.0, "mayank.s@bbdu.ac.in", "Male", 2026));
        list.add(new Student("11206", "Kritika Sahu", "B.Tech IT", 8.20, Arrays.asList("Java", "SQL", "PowerBI", "Python"), true, "Deloitte", 8.5, "kritika.s@bbdu.ac.in", "Female", 2026));
        list.add(new Student("11207", "Alok Kumar", "B.Tech IT", 7.10, Arrays.asList("Java", "C++", "SQL"), false, "None", 0.0, "alok.k@bbdu.ac.in", "Male", 2026));
        list.add(new Student("11208", "Neha Sen", "B.Tech IT", 7.70, Arrays.asList("Java", "JavaScript", "SQL"), true, "Wipro Turbo", 6.5, "neha.s@bbdu.ac.in", "Female", 2026));
        list.add(new Student("11209", "Ashish Pal", "B.Tech IT", 6.50, Arrays.asList("HTML", "CSS", "SQL"), false, "None", 0.0, "ashish.p@bbdu.ac.in", "Male", 2026));
        list.add(new Student("11210", "Prachi Dixit", "B.Tech IT", 8.75, Arrays.asList("Java", "Spring Boot", "MongoDB", "Microservices"), true, "Google Cloud", 18.5, "prachi.d@bbdu.ac.in", "Female", 2026));
        list.add(new Student("11211", "Girish Pandey", "B.Tech IT", 7.35, Arrays.asList("Java", "SQL", "Linux"), false, "None", 0.0, "girish.p@bbdu.ac.in", "Male", 2026));
        list.add(new Student("11212", "Juhi Awasthi", "B.Tech IT", 8.00, Arrays.asList("Java", "Python", "SQL"), true, "Cognizant GenC Elevate", 6.8, "juhi.a@bbdu.ac.in", "Female", 2026));

        // 48-58: MCA Department (Master of Computer Applications)
        list.add(new Student("11401", "Hemant Kulkarni", "MCA", 9.30, Arrays.asList("Java", "Spring Boot", "Microservices", "Docker", "SQL"), true, "Oracle", 12.0, "hemant.k@bbdu.ac.in", "Male", 2026));
        list.add(new Student("11402", "Purnima Nair", "MCA", 8.90, Arrays.asList("Java", "Spring Boot", "React", "MongoDB"), true, "Infosys (Power Programmer)", 9.5, "purnima.n@bbdu.ac.in", "Female", 2026));
        list.add(new Student("11403", "Sameer Farooqui", "MCA", 8.40, Arrays.asList("Python", "Django", "PostgreSQL", "MongoDB"), true, "Zomato", 14.0, "sameer.f@bbdu.ac.in", "Male", 2026));
        list.add(new Student("11404", "Rashmi Srivastava", "MCA", 7.95, Arrays.asList("Java", "SQL", "Spring Boot"), true, "TCS Digital", 7.2, "rashmi.s@bbdu.ac.in", "Female", 2026));
        list.add(new Student("11405", "Nilesh Dubey", "MCA", 7.20, Arrays.asList("Java", "SQL", "HTML", "CSS"), false, "None", 0.0, "nilesh.d@bbdu.ac.in", "Male", 2026));
        list.add(new Student("11406", "Smriti Mathur", "MCA", 8.60, Arrays.asList("Java", "SQL", "PowerBI", "Python"), true, "Deloitte", 8.5, "smriti.m@bbdu.ac.in", "Female", 2026));
        list.add(new Student("11407", "Dheeraj Singh", "MCA", 6.95, Arrays.asList("Java", "C++", "SQL"), false, "None", 0.0, "dheeraj.s@bbdu.ac.in", "Male", 2026));
        list.add(new Student("11408", "Kajal Chandel", "MCA", 7.80, Arrays.asList("Java", "Web Development", "SQL"), true, "Cognizant GenC Elevate", 6.8, "kajal.c@bbdu.ac.in", "Female", 2026));
        list.add(new Student("11409", "Suraj Bhan", "MCA", 6.60, Arrays.asList("SQL", "HTML", "JavaScript"), false, "None", 0.0, "suraj.b@bbdu.ac.in", "Male", 2026));
        list.add(new Student("11410", "Monika Nigam", "MCA", 8.10, Arrays.asList("Java", "Python", "SQL"), true, "Wipro Turbo", 6.5, "monika.n@bbdu.ac.in", "Female", 2026));
        list.add(new Student("11411", "Vikram Rathore", "MCA", 7.50, Arrays.asList("Java", "Spring Boot", "SQL"), false, "None", 0.0, "vikram.r@bbdu.ac.in", "Male", 2026));

        // 59-68: B.Tech ECE Department (Electronics & Communication Engineering)
        list.add(new Student("11601", "Tarun Goswami", "B.Tech ECE", 8.80, Arrays.asList("C++", "Embedded Systems", "IoT", "Python"), true, "Microsoft", 22.0, "tarun.g@bbdu.ac.in", "Male", 2026));
        list.add(new Student("11602", "Garima Somani", "B.Tech ECE", 8.30, Arrays.asList("VLSI", "Verilog", "Python", "SQL"), true, "TCS Digital", 7.2, "garima.s@bbdu.ac.in", "Female", 2026));
        list.add(new Student("11603", "Pankaj Yadav", "B.Tech ECE", 7.40, Arrays.asList("C", "C++", "Embedded Systems"), false, "None", 0.0, "pankaj.y@bbdu.ac.in", "Male", 2026));
        list.add(new Student("11604", "Shalini Pandey", "B.Tech ECE", 8.10, Arrays.asList("Python", "IoT", "SQL", "Java"), true, "Cognizant GenC Elevate", 6.8, "shalini.p@bbdu.ac.in", "Female", 2026));
        list.add(new Student("11605", "Manish Rawal", "B.Tech ECE", 6.70, Arrays.asList("C", "MATLAB", "SQL"), false, "None", 0.0, "manish.r@bbdu.ac.in", "Male", 2026));
        list.add(new Student("11606", "Apeksha Sinha", "B.Tech ECE", 8.45, Arrays.asList("Python", "Machine Learning", "IoT", "SQL"), true, "Deloitte", 8.5, "apeksha.s@bbdu.ac.in", "Female", 2026));
        list.add(new Student("11607", "Rajat Saxena", "B.Tech ECE", 7.15, Arrays.asList("C++", "Networking", "Linux"), false, "None", 0.0, "rajat.s@bbdu.ac.in", "Male", 2026));
        list.add(new Student("11608", "Vandana Kushwaha", "B.Tech ECE", 7.65, Arrays.asList("Java", "SQL", "Web Development"), true, "Wipro Turbo", 6.5, "vandana.k@bbdu.ac.in", "Female", 2026));
        list.add(new Student("11609", "Gopal Das", "B.Tech ECE", 6.40, Arrays.asList("C", "Circuit Design"), false, "None", 0.0, "gopal.d@bbdu.ac.in", "Male", 2026));
        list.add(new Student("11610", "Preeti Agrahari", "B.Tech ECE", 7.85, Arrays.asList("Python", "SQL", "IoT"), false, "None", 0.0, "preeti.a@bbdu.ac.in", "Female", 2026));

        // 69-72: Additional Computer Application & AI Candidates (Exceeding mandatory 65 threshold)
        list.add(new Student("12516", "Karan Malhotra", "BCA DS & AI", 9.25, Arrays.asList("Python", "PyTorch", "Computer Vision", "MongoDB"), true, "Google Cloud", 18.5, "karan.m@bbdu.ac.in", "Male", 2026));
        list.add(new Student("12517", "Shruti Deshmukh", "BCA DS & AI", 8.90, Arrays.asList("Python", "Machine Learning", "Docker", "SQL"), true, "Infosys (Power Programmer)", 9.5, "shruti.d@bbdu.ac.in", "Female", 2026));
        list.add(new Student("12518", "Tushar Singhal", "BCA DS & AI", 7.30, Arrays.asList("Python", "SQL", "Tableau"), false, "None", 0.0, "tushar.s@bbdu.ac.in", "Male", 2026));
        list.add(new Student("12519", "Barkha Rastogi", "BCA DS & AI", 8.15, Arrays.asList("Data Science", "Python", "MongoDB", "SQL"), false, "None", 0.0, "barkha.r@bbdu.ac.in", "Female", 2026));

        int inserted = studentDAO.insertMany(list);
        logger.info("Successfully seeded {} production-grade student records across all departments (Threshold: >=65 satisfied).", inserted);
    }

    public void seedApplications() {
        if (applicationDAO.count() > 0) {
            logger.info("Applications already present ({} records). Skipping seeding.", applicationDAO.count());
            return;
        }

        List<Application> apps = Arrays.asList(
                new Application("APP-1001", "26 / 12502", "Shaswat Jaiswal", "COMP-001", "Google Cloud", "2026-09-01", "Selected", "Offer accepted at 18.5 LPA"),
                new Application("APP-1002", "26 / 12502", "Shaswat Jaiswal", "COMP-003", "Amazon AWS", "2026-08-25", "Selected", "Received second offer"),
                new Application("APP-1003", "12501", "Aarav Sharma", "COMP-003", "Amazon AWS", "2026-08-25", "Selected", "Cleared Technical & Bar Raiser"),
                new Application("APP-1004", "12503", "Ananya Verma", "COMP-004", "Deloitte", "2026-09-05", "Selected", "Joined as Tech Analyst"),
                new Application("APP-1005", "11001", "Vikas Dubey", "COMP-002", "Microsoft", "2026-08-20", "Selected", "Highest package candidate"),
                new Application("APP-1006", "11002", "Priya Agarwal", "COMP-003", "Amazon AWS", "2026-08-25", "Selected", "Accepted"),
                new Application("APP-1007", "11003", "Harsh Vardhan", "COMP-001", "Google Cloud", "2026-09-01", "Selected", "Accepted"),
                new Application("APP-1008", "12506", "Aditya Srivastava", "COMP-005", "TCS Digital", "2026-09-10", "Interviewed", "Awaiting HR Round results"),
                new Application("APP-1009", "12508", "Ayush Tiwari", "COMP-004", "Deloitte", "2026-09-12", "Shortlisted", "Online Assessment cleared"),
                new Application("APP-1010", "11007", "Naveen Chauhan", "COMP-009", "Wipro Turbo", "2026-09-15", "Applied", "Resume under review"),
                new Application("APP-1011", "11009", "Gaurav Malhotra", "COMP-010", "Cognizant GenC Elevate", "2026-09-18", "Rejected", "Cutoff criteria not met"),
                new Application("APP-1012", "11201", "Varun Kapoor", "COMP-003", "Amazon AWS", "2026-08-25", "Selected", "Accepted offer"),
                new Application("APP-1013", "11202", "Bhavna Bisht", "COMP-006", "Infosys (Power Programmer)", "2026-09-02", "Selected", "Accepted offer"),
                new Application("APP-1014", "11401", "Hemant Kulkarni", "COMP-008", "Oracle", "2026-09-08", "Selected", "Accepted"),
                new Application("APP-1015", "11601", "Tarun Goswami", "COMP-002", "Microsoft", "2026-08-20", "Selected", "Hardware/IoT domain recruit"),
                new Application("APP-1016", "12516", "Karan Malhotra", "COMP-001", "Google Cloud", "2026-09-01", "Selected", "Cloud Data Specialist"),
                new Application("APP-1017", "12517", "Shruti Deshmukh", "COMP-006", "Infosys (Power Programmer)", "2026-09-02", "Selected", "Accepted offer"),
                new Application("APP-1018", "12518", "Tushar Singhal", "COMP-005", "TCS Digital", "2026-09-10", "Applied", "Drive scheduled"),
                new Application("APP-1019", "12519", "Barkha Rastogi", "COMP-004", "Deloitte", "2026-09-12", "Shortlisted", "Interview scheduled"),
                new Application("APP-1020", "11405", "Nilesh Dubey", "COMP-010", "Cognizant GenC Elevate", "2026-09-18", "Applied", "Drive pending")
        );

        int count = applicationDAO.insertMany(apps);
        logger.info("Seeded {} placement applications.", count);
    }
}
