package com.bbdu.placement.model;

import org.bson.Document;
import java.util.ArrayList;
import java.util.List;

/**
 * Model class representing a Student profile in the Campus Placement Tracker.
 * Mapped to the 'students' collection in MongoDB.
 *
 * Designed for BBDU Campus Placement System.
 * Author: Shaswat Jaiswal (Roll No: 26 / 12502, BCA DS & AI)
 */
public class Student {
    private String rollNumber;
    private String name;
    private String department;
    private double cgpa;
    private List<String> skills;
    private boolean isPlaced;
    private String placedCompany;
    private double packageCTC; // in LPA
    private String email;
    private String gender;
    private int graduationYear;

    public Student() {
        this.skills = new ArrayList<>();
        this.isPlaced = false;
        this.placedCompany = "None";
        this.packageCTC = 0.0;
        this.graduationYear = 2026;
    }

    public Student(String rollNumber, String name, String department, double cgpa,
                   List<String> skills, boolean isPlaced, String placedCompany,
                   double packageCTC, String email, String gender, int graduationYear) {
        this.rollNumber = rollNumber;
        this.name = name;
        this.department = department;
        this.cgpa = cgpa;
        this.skills = skills != null ? skills : new ArrayList<>();
        this.isPlaced = isPlaced;
        this.placedCompany = placedCompany != null ? placedCompany : "None";
        this.packageCTC = packageCTC;
        this.email = email;
        this.gender = gender;
        this.graduationYear = graduationYear;
    }

    // Convert Document to Student
    public static Student fromDocument(Document doc) {
        if (doc == null) return null;
        Student s = new Student();
        s.setRollNumber(doc.getString("roll_number"));
        s.setName(doc.getString("name"));
        s.setDepartment(doc.getString("department"));
        Number cgpaNum = doc.get("cgpa", Number.class);
        s.setCgpa(cgpaNum != null ? cgpaNum.doubleValue() : 0.0);
        List<String> rawSkills = doc.getList("skills", String.class);
        s.setSkills(rawSkills != null ? rawSkills : new ArrayList<>());
        s.setPlaced(Boolean.TRUE.equals(doc.getBoolean("is_placed")));
        s.setPlacedCompany(doc.getString("placed_company") != null ? doc.getString("placed_company") : "None");
        Number ctcNum = doc.get("package_ctc", Number.class);
        s.setPackageCTC(ctcNum != null ? ctcNum.doubleValue() : 0.0);
        s.setEmail(doc.getString("email"));
        s.setGender(doc.getString("gender"));
        Number gradYear = doc.get("graduation_year", Number.class);
        s.setGraduationYear(gradYear != null ? gradYear.intValue() : 2026);
        return s;
    }

    // Convert Student to Document for MongoDB ingestion
    public Document toDocument() {
        Document doc = new Document();
        doc.append("roll_number", rollNumber);
        doc.append("name", name);
        doc.append("department", department);
        doc.append("cgpa", cgpa);
        doc.append("skills", skills);
        doc.append("is_placed", isPlaced);
        doc.append("placed_company", placedCompany != null ? placedCompany : "None");
        doc.append("package_ctc", packageCTC);
        doc.append("email", email);
        doc.append("gender", gender);
        doc.append("graduation_year", graduationYear);
        return doc;
    }

    // Getters and Setters
    public String getRollNumber() { return rollNumber; }
    public void setRollNumber(String rollNumber) { this.rollNumber = rollNumber; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDepartment() { return department; }
    public void setDepartment(String department) { this.department = department; }

    public double getCgpa() { return cgpa; }
    public void setCgpa(double cgpa) { this.cgpa = cgpa; }

    public List<String> getSkills() { return skills; }
    public void setSkills(List<String> skills) { this.skills = skills; }

    public boolean isPlaced() { return isPlaced; }
    public void setPlaced(boolean placed) { isPlaced = placed; }

    public String getPlacedCompany() { return placedCompany; }
    public void setPlacedCompany(String placedCompany) { this.placedCompany = placedCompany; }

    public double getPackageCTC() { return packageCTC; }
    public void setPackageCTC(double packageCTC) { this.packageCTC = packageCTC; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getGender() { return gender; }
    public void setGender(String gender) { this.gender = gender; }

    public int getGraduationYear() { return graduationYear; }
    public void setGraduationYear(int graduationYear) { this.graduationYear = graduationYear; }

    @Override
    public String toString() {
        return String.format("[%s] %s | Dept: %s | CGPA: %.2f | Skills: %s | Placed: %s (%s, %.1f LPA)",
                rollNumber, name, department, cgpa, skills, isPlaced ? "Yes" : "No", placedCompany, packageCTC);
    }
}
