package com.bbdu.placement.model;

import org.bson.Document;
import java.util.ArrayList;
import java.util.List;

/**
 * Model class representing a recruiting company.
 * Mapped to the 'companies' collection in MongoDB.
 */
public class Company {
    private String companyId;
    private String name;
    private String industry;
    private String roleOffered;
    private double minCgpa;
    private List<String> requiredSkills;
    private double packageOffered; // LPA
    private String driveDate;
    private String location;

    public Company() {
        this.requiredSkills = new ArrayList<>();
    }

    public Company(String companyId, String name, String industry, String roleOffered,
                   double minCgpa, List<String> requiredSkills, double packageOffered,
                   String driveDate, String location) {
        this.companyId = companyId;
        this.name = name;
        this.industry = industry;
        this.roleOffered = roleOffered;
        this.minCgpa = minCgpa;
        this.requiredSkills = requiredSkills != null ? requiredSkills : new ArrayList<>();
        this.packageOffered = packageOffered;
        this.driveDate = driveDate;
        this.location = location;
    }

    public static Company fromDocument(Document doc) {
        if (doc == null) return null;
        Company c = new Company();
        c.setCompanyId(doc.getString("company_id"));
        c.setName(doc.getString("name"));
        c.setIndustry(doc.getString("industry"));
        c.setRoleOffered(doc.getString("role_offered"));
        Number minCgpa = doc.get("min_cgpa", Number.class);
        c.setMinCgpa(minCgpa != null ? minCgpa.doubleValue() : 0.0);
        List<String> skills = doc.getList("required_skills", String.class);
        c.setRequiredSkills(skills != null ? skills : new ArrayList<>());
        Number pkg = doc.get("package_offered", Number.class);
        c.setPackageOffered(pkg != null ? pkg.doubleValue() : 0.0);
        c.setDriveDate(doc.getString("drive_date"));
        c.setLocation(doc.getString("location"));
        return c;
    }

    public Document toDocument() {
        Document doc = new Document();
        doc.append("company_id", companyId);
        doc.append("name", name);
        doc.append("industry", industry);
        doc.append("role_offered", roleOffered);
        doc.append("min_cgpa", minCgpa);
        doc.append("required_skills", requiredSkills);
        doc.append("package_offered", packageOffered);
        doc.append("drive_date", driveDate);
        doc.append("location", location);
        return doc;
    }

    public String getCompanyId() { return companyId; }
    public void setCompanyId(String companyId) { this.companyId = companyId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getIndustry() { return industry; }
    public void setIndustry(String industry) { this.industry = industry; }

    public String getRoleOffered() { return roleOffered; }
    public void setRoleOffered(String roleOffered) { this.roleOffered = roleOffered; }

    public double getMinCgpa() { return minCgpa; }
    public void setMinCgpa(double minCgpa) { this.minCgpa = minCgpa; }

    public List<String> getRequiredSkills() { return requiredSkills; }
    public void setRequiredSkills(List<String> requiredSkills) { this.requiredSkills = requiredSkills; }

    public double getPackageOffered() { return packageOffered; }
    public void setPackageOffered(double packageOffered) { this.packageOffered = packageOffered; }

    public String getDriveDate() { return driveDate; }
    public void setDriveDate(String driveDate) { this.driveDate = driveDate; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    @Override
    public String toString() {
        return String.format("[%s] %s (%s) | Role: %s | Min CGPA: %.1f | Pkg: %.1f LPA | Drive: %s",
                companyId, name, industry, roleOffered, minCgpa, packageOffered, driveDate);
    }
}
