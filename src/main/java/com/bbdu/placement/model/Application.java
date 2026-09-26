package com.bbdu.placement.model;

import org.bson.Document;

/**
 * Model class representing a Student's application for a Company drive.
 * Mapped to the 'applications' collection in MongoDB.
 */
public class Application {
    private String applicationId;
    private String rollNumber;
    private String studentName;
    private String companyId;
    private String companyName;
    private String applicationDate;
    private String status; // "Applied", "Shortlisted", "Interviewed", "Selected", "Rejected"
    private String remarks;

    public Application() {}

    public Application(String applicationId, String rollNumber, String studentName,
                       String companyId, String companyName, String applicationDate,
                       String status, String remarks) {
        this.applicationId = applicationId;
        this.rollNumber = rollNumber;
        this.studentName = studentName;
        this.companyId = companyId;
        this.companyName = companyName;
        this.applicationDate = applicationDate;
        this.status = status;
        this.remarks = remarks;
    }

    public static Application fromDocument(Document doc) {
        if (doc == null) return null;
        Application a = new Application();
        a.setApplicationId(doc.getString("application_id"));
        a.setRollNumber(doc.getString("roll_number"));
        a.setStudentName(doc.getString("student_name"));
        a.setCompanyId(doc.getString("company_id"));
        a.setCompanyName(doc.getString("company_name"));
        a.setApplicationDate(doc.getString("application_date"));
        a.setStatus(doc.getString("status"));
        a.setRemarks(doc.getString("remarks"));
        return a;
    }

    public Document toDocument() {
        Document doc = new Document();
        doc.append("application_id", applicationId);
        doc.append("roll_number", rollNumber);
        doc.append("student_name", studentName);
        doc.append("company_id", companyId);
        doc.append("company_name", companyName);
        doc.append("application_date", applicationDate);
        doc.append("status", status);
        doc.append("remarks", remarks);
        return doc;
    }

    public String getApplicationId() { return applicationId; }
    public void setApplicationId(String applicationId) { this.applicationId = applicationId; }

    public String getRollNumber() { return rollNumber; }
    public void setRollNumber(String rollNumber) { this.rollNumber = rollNumber; }

    public String getStudentName() { return studentName; }
    public void setStudentName(String studentName) { this.studentName = studentName; }

    public String getCompanyId() { return companyId; }
    public void setCompanyId(String companyId) { this.companyId = companyId; }

    public String getCompanyName() { return companyName; }
    public void setCompanyName(String companyName) { this.companyName = companyName; }

    public String getApplicationDate() { return applicationDate; }
    public void setApplicationDate(String applicationDate) { this.applicationDate = applicationDate; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getRemarks() { return remarks; }
    public void setRemarks(String remarks) { this.remarks = remarks; }

    @Override
    public String toString() {
        return String.format("[%s] Student: %s (%s) -> Company: %s | Status: %s | Remarks: %s",
                applicationId, studentName, rollNumber, companyName, status, remarks);
    }
}
