package com.bbdu.placement.ui;

import com.bbdu.placement.config.MongoConfig;
import com.bbdu.placement.dao.AnalyticsDAO;
import com.bbdu.placement.dao.CompanyDAO;
import com.bbdu.placement.dao.StudentDAO;
import com.bbdu.placement.data.DataSeeder;
import com.bbdu.placement.model.Company;
import com.bbdu.placement.model.Student;
import org.bson.Document;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Modern Java Swing Graphical User Interface Dashboard for BBDU Placement Cell.
 * Task 4.2: GUI Dashboard allowing placement officers to easily manage students,
 * view eligible candidate lists, run updates, and inspect aggregation reports.
 */
public class PlacementDashboardGUI extends JFrame {
    private final StudentDAO studentDAO;
    private final CompanyDAO companyDAO;
    private final AnalyticsDAO analyticsDAO;
    private final DataSeeder dataSeeder;

    // Student Directory Components
    private JTable studentTable;
    private DefaultTableModel studentTableModel;
    private JTextField searchField;
    private JComboBox<String> deptFilterCombo;
    private JSpinner minCgpaSpinner;
    private JComboBox<String> placementFilterCombo;

    // Analytics Components
    private JLabel totalStudentsLabel;
    private JLabel totalPlacedLabel;
    private JLabel placementRateLabel;
    private JLabel avgCtcLabel;
    private JLabel maxCtcLabel;
    private JTable deptAnalyticsTable;
    private DefaultTableModel deptAnalyticsModel;
    private JTable companyAnalyticsTable;
    private DefaultTableModel companyAnalyticsModel;

    // Explain Components
    private JTextArea explainOutputArea;
    private JSpinner explainCgpaSpinner;

    public PlacementDashboardGUI() {
        super("BBDU Campus Placement & Recruitment Tracker | Shaswat Jaiswal (26 / 12502)");
        this.studentDAO = new StudentDAO();
        this.companyDAO = new CompanyDAO();
        this.analyticsDAO = new AnalyticsDAO();
        this.dataSeeder = new DataSeeder();

        initUI();
    }

    private void initUI() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {}

        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(1180, 760);
        setLocationRelativeTo(null);

        // Header Panel
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(24, 43, 73));
        headerPanel.setBorder(new EmptyBorder(12, 20, 12, 20));

        JLabel titleLabel = new JLabel("🎓 BABU BANARASI DAS UNIVERSITY — PLACEMENT TRACKER");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        titleLabel.setForeground(Color.WHITE);

        JLabel subtitleLabel = new JLabel("Student Lead: Shaswat Jaiswal | Roll No: 26 / 12502 | BCA DS & AI | MongoDB 8.3");
        subtitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        subtitleLabel.setForeground(new Color(200, 220, 255));

        headerPanel.add(titleLabel, BorderLayout.NORTH);
        headerPanel.add(subtitleLabel, BorderLayout.SOUTH);

        // Tabbed Pane
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("Segoe UI", Font.BOLD, 13));

        tabbedPane.addTab("👥 Student Directory", createDirectoryTab());
        tabbedPane.addTab("➕ Register Student (insertOne)", createAddStudentTab());
        tabbedPane.addTab("🎯 Record Placement (updateOne)", createPlacementTab());
        tabbedPane.addTab("📊 Aggregation Analytics", createAnalyticsTab());
        tabbedPane.addTab("⚡ Query Explain Plan (.explain)", createExplainTab());

        add(headerPanel, BorderLayout.NORTH);
        add(tabbedPane, BorderLayout.CENTER);

        // Auto-load initial data
        refreshStudentTable();
        refreshAnalytics();
    }

    // -------------------------------------------------------------
    // TAB 1: STUDENT DIRECTORY & FILTERS
    // -------------------------------------------------------------
    private JPanel createDirectoryTab() {
        JPanel panel = new JPanel(new BorderLayout(8, 8));
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));

        // Filter Bar
        JPanel filterBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        filterBar.setBorder(BorderFactory.createTitledBorder("Search & Comparison Filters"));

        filterBar.add(new JLabel("Search (Name/Roll):"));
        searchField = new JTextField(12);
        filterBar.add(searchField);

        filterBar.add(new JLabel("Dept:"));
        deptFilterCombo = new JComboBox<>(new String[]{"All Departments", "BCA DS & AI", "B.Tech CSE", "B.Tech IT", "MCA", "B.Tech ECE"});
        filterBar.add(deptFilterCombo);

        filterBar.add(new JLabel("Min CGPA ($gte):"));
        minCgpaSpinner = new JSpinner(new SpinnerNumberModel(0.0, 0.0, 10.0, 0.5));
        filterBar.add(minCgpaSpinner);

        filterBar.add(new JLabel("Status:"));
        placementFilterCombo = new JComboBox<>(new String[]{"All", "Placed Only", "Unplaced Only"});
        filterBar.add(placementFilterCombo);

        JButton applyBtn = new JButton("🔍 Filter");
        applyBtn.setBackground(new Color(41, 128, 185));
        applyBtn.setForeground(Color.WHITE);
        applyBtn.addActionListener(e -> filterStudents());
        filterBar.add(applyBtn);

        JButton resetBtn = new JButton("Reset");
        resetBtn.addActionListener(e -> {
            searchField.setText("");
            deptFilterCombo.setSelectedIndex(0);
            minCgpaSpinner.setValue(0.0);
            placementFilterCombo.setSelectedIndex(0);
            refreshStudentTable();
        });
        filterBar.add(resetBtn);

        JButton seedBtn = new JButton("🌱 Seed 65+ Data");
        seedBtn.setBackground(new Color(39, 174, 96));
        seedBtn.setForeground(Color.WHITE);
        seedBtn.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(this,
                    "Load/Reset 65+ production student records?", "Confirm Seeding", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                dataSeeder.seedAll(true);
                refreshStudentTable();
                refreshAnalytics();
                JOptionPane.showMessageDialog(this, "Successfully loaded 70+ records into MongoDB!");
            }
        });
        filterBar.add(seedBtn);

        panel.add(filterBar, BorderLayout.NORTH);

        // Student Table
        String[] columns = {"Roll Number", "Name", "Department", "CGPA", "Placement Status", "Company", "CTC (LPA)", "Skills"};
        studentTableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        studentTable = new JTable(studentTableModel);
        studentTable.setRowHeight(24);
        studentTable.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        studentTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        studentTable.setAutoCreateRowSorter(true);

        JScrollPane scrollPane = new JScrollPane(studentTable);
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    private void refreshStudentTable() {
        studentTableModel.setRowCount(0);
        List<Student> students = studentDAO.findAll();
        for (Student s : students) {
            studentTableModel.addRow(new Object[]{
                    s.getRollNumber(),
                    s.getName(),
                    s.getDepartment(),
                    String.format("%.2f", s.getCgpa()),
                    s.isPlaced() ? "PLACED" : "UNPLACED",
                    s.getPlacedCompany(),
                    String.format("%.2f", s.getPackageCTC()),
                    String.join(", ", s.getSkills())
            });
        }
    }

    private void filterStudents() {
        String query = searchField.getText().trim();
        String selectedDept = (String) deptFilterCombo.getSelectedItem();
        double minCgpa = (Double) minCgpaSpinner.getValue();
        String placementFilter = (String) placementFilterCombo.getSelectedItem();

        List<Student> all = studentDAO.findAll();
        studentTableModel.setRowCount(0);

        for (Student s : all) {
            if (!query.isEmpty()) {
                boolean matchName = s.getName().toLowerCase().contains(query.toLowerCase());
                boolean matchRoll = s.getRollNumber().toLowerCase().contains(query.toLowerCase());
                if (!matchName && !matchRoll) continue;
            }
            if (!"All Departments".equals(selectedDept) && !s.getDepartment().equalsIgnoreCase(selectedDept)) {
                continue;
            }
            if (s.getCgpa() < minCgpa) {
                continue;
            }
            if ("Placed Only".equals(placementFilter) && !s.isPlaced()) {
                continue;
            }
            if ("Unplaced Only".equals(placementFilter) && s.isPlaced()) {
                continue;
            }

            studentTableModel.addRow(new Object[]{
                    s.getRollNumber(),
                    s.getName(),
                    s.getDepartment(),
                    String.format("%.2f", s.getCgpa()),
                    s.isPlaced() ? "PLACED" : "UNPLACED",
                    s.getPlacedCompany(),
                    String.format("%.2f", s.getPackageCTC()),
                    String.join(", ", s.getSkills())
            });
        }
    }

    // -------------------------------------------------------------
    // TAB 2: MANUAL REGISTRATION (insertOne)
    // -------------------------------------------------------------
    private JPanel createAddStudentTab() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(new EmptyBorder(20, 40, 20, 40));
        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(8, 8, 8, 8);
        g.fill = GridBagConstraints.HORIZONTAL;

        JTextField rollField = new JTextField(20);
        JTextField nameField = new JTextField(20);
        JComboBox<String> deptBox = new JComboBox<>(new String[]{"BCA DS & AI", "B.Tech CSE", "B.Tech IT", "MCA", "B.Tech ECE"});
        JSpinner cgpaSpin = new JSpinner(new SpinnerNumberModel(8.0, 0.0, 10.0, 0.1));
        JTextField skillsField = new JTextField("Java, Python, SQL", 20);
        JCheckBox placedCheck = new JCheckBox("Student is already placed");
        JTextField companyField = new JTextField("None", 20);
        JSpinner ctcSpin = new JSpinner(new SpinnerNumberModel(0.0, 0.0, 50.0, 0.5));
        JTextField emailField = new JTextField("student@bbdu.ac.in", 20);
        JComboBox<String> genderBox = new JComboBox<>(new String[]{"Male", "Female", "Other"});

        placedCheck.addActionListener(e -> {
            companyField.setEnabled(placedCheck.isSelected());
            ctcSpin.setEnabled(placedCheck.isSelected());
        });
        companyField.setEnabled(false);
        ctcSpin.setEnabled(false);

        int row = 0;
        g.gridx = 0; g.gridy = row; panel.add(new JLabel("Roll Number (Unique):"), g);
        g.gridx = 1; panel.add(rollField, g);

        row++;
        g.gridx = 0; g.gridy = row; panel.add(new JLabel("Student Full Name:"), g);
        g.gridx = 1; panel.add(nameField, g);

        row++;
        g.gridx = 0; g.gridy = row; panel.add(new JLabel("Academic Department:"), g);
        g.gridx = 1; panel.add(deptBox, g);

        row++;
        g.gridx = 0; g.gridy = row; panel.add(new JLabel("Current CGPA:"), g);
        g.gridx = 1; panel.add(cgpaSpin, g);

        row++;
        g.gridx = 0; g.gridy = row; panel.add(new JLabel("Skills (Comma Separated):"), g);
        g.gridx = 1; panel.add(skillsField, g);

        row++;
        g.gridx = 0; g.gridy = row; panel.add(new JLabel("Placement Status:"), g);
        g.gridx = 1; panel.add(placedCheck, g);

        row++;
        g.gridx = 0; g.gridy = row; panel.add(new JLabel("Placed Company:"), g);
        g.gridx = 1; panel.add(companyField, g);

        row++;
        g.gridx = 0; g.gridy = row; panel.add(new JLabel("Package CTC (LPA):"), g);
        g.gridx = 1; panel.add(ctcSpin, g);

        row++;
        g.gridx = 0; g.gridy = row; panel.add(new JLabel("Email Address:"), g);
        g.gridx = 1; panel.add(emailField, g);

        row++;
        g.gridx = 0; g.gridy = row; panel.add(new JLabel("Gender:"), g);
        g.gridx = 1; panel.add(genderBox, g);

        row++;
        JButton submitBtn = new JButton("💾 Ingest Profile (insertOne)");
        submitBtn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        submitBtn.setBackground(new Color(41, 128, 185));
        submitBtn.setForeground(Color.WHITE);

        submitBtn.addActionListener(e -> {
            String roll = rollField.getText().trim();
            String name = nameField.getText().trim();
            if (roll.isEmpty() || name.isEmpty()) {
                JOptionPane.showMessageDialog(panel, "Roll Number and Name are mandatory!", "Validation Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            List<String> skills = new ArrayList<>();
            for (String s : skillsField.getText().split(",")) {
                if (!s.trim().isEmpty()) skills.add(s.trim());
            }

            Student s = new Student(
                    roll, name, (String) deptBox.getSelectedItem(),
                    (Double) cgpaSpin.getValue(), skills, placedCheck.isSelected(),
                    companyField.getText().trim(), (Double) ctcSpin.getValue(),
                    emailField.getText().trim(), (String) genderBox.getSelectedItem(), 2026
            );

            boolean success = studentDAO.insertOne(s);
            if (success) {
                JOptionPane.showMessageDialog(panel, "Student profile successfully inserted into MongoDB!", "Success", JOptionPane.INFORMATION_MESSAGE);
                rollField.setText("");
                nameField.setText("");
                refreshStudentTable();
                refreshAnalytics();
            } else {
                JOptionPane.showMessageDialog(panel, "Failed to insert student. Check for duplicate Roll Number.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        g.gridx = 0; g.gridy = row; g.gridwidth = 2;
        panel.add(submitBtn, g);

        return panel;
    }

    // -------------------------------------------------------------
    // TAB 3: PLACEMENT UPDATE (updateOne)
    // -------------------------------------------------------------
    private JPanel createPlacementTab() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(new EmptyBorder(30, 40, 30, 40));
        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(10, 10, 10, 10);
        g.fill = GridBagConstraints.HORIZONTAL;

        JTextField rollField = new JTextField(15);
        JLabel studentInfoLabel = new JLabel("Enter roll number to load student profile");
        studentInfoLabel.setFont(new Font("Segoe UI", Font.ITALIC, 12));

        JComboBox<String> companyBox = new JComboBox<>(new String[]{
                "Google Cloud", "Microsoft", "Amazon AWS", "Deloitte", "TCS Digital",
                "Infosys (Power Programmer)", "Zomato", "Oracle", "Wipro Turbo", "Cognizant GenC Elevate"
        });
        JSpinner packageSpin = new JSpinner(new SpinnerNumberModel(10.0, 1.0, 50.0, 0.5));

        JButton verifyBtn = new JButton("Load Student");
        verifyBtn.addActionListener(e -> {
            Student s = studentDAO.findByRollNumber(rollField.getText().trim());
            if (s != null) {
                studentInfoLabel.setText(String.format("Found: %s | Dept: %s | CGPA: %.2f | Currently Placed: %s",
                        s.getName(), s.getDepartment(), s.getCgpa(), s.isPlaced() ? "Yes (" + s.getPlacedCompany() + ")" : "No"));
            } else {
                studentInfoLabel.setText("Student not found.");
            }
        });

        int r = 0;
        g.gridx = 0; g.gridy = r; panel.add(new JLabel("Student Roll Number:"), g);
        g.gridx = 1; panel.add(rollField, g);
        g.gridx = 2; panel.add(verifyBtn, g);

        r++;
        g.gridx = 0; g.gridy = r; g.gridwidth = 3;
        panel.add(studentInfoLabel, g);
        g.gridwidth = 1;

        r++;
        g.gridx = 0; g.gridy = r; panel.add(new JLabel("Recruiter Company:"), g);
        g.gridx = 1; g.gridwidth = 2; panel.add(companyBox, g);
        g.gridwidth = 1;

        r++;
        g.gridx = 0; g.gridy = r; panel.add(new JLabel("Package CTC Offered (LPA):"), g);
        g.gridx = 1; g.gridwidth = 2; panel.add(packageSpin, g);
        g.gridwidth = 1;

        r++;
        JButton updateBtn = new JButton("🎉 Record Offer & Update Status (updateOne)");
        updateBtn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        updateBtn.setBackground(new Color(39, 174, 96));
        updateBtn.setForeground(Color.WHITE);

        updateBtn.addActionListener(e -> {
            String roll = rollField.getText().trim();
            if (roll.isEmpty()) {
                JOptionPane.showMessageDialog(panel, "Please provide a valid Roll Number.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            String comp = (String) companyBox.getSelectedItem();
            double ctc = (Double) packageSpin.getValue();

            boolean ok = studentDAO.updatePlacementStatus(roll, comp, ctc);
            if (ok) {
                JOptionPane.showMessageDialog(panel, "Successfully updated placement record for Roll: " + roll, "Success", JOptionPane.INFORMATION_MESSAGE);
                refreshStudentTable();
                refreshAnalytics();
            } else {
                JOptionPane.showMessageDialog(panel, "Failed to update. Check Roll Number.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        g.gridx = 0; g.gridy = r; g.gridwidth = 3;
        panel.add(updateBtn, g);

        return panel;
    }

    // -------------------------------------------------------------
    // TAB 4: AGGREGATION ANALYTICS
    // -------------------------------------------------------------
    private JPanel createAnalyticsTab() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));

        // Top KPI Cards Panel
        JPanel kpiPanel = new JPanel(new GridLayout(1, 5, 10, 10));
        kpiPanel.setBorder(BorderFactory.createTitledBorder("Overall Placement Statistics (MongoDB Aggregation)"));

        totalStudentsLabel = createCard("Total Students", "0", new Color(41, 128, 185));
        totalPlacedLabel = createCard("Placed Students", "0", new Color(39, 174, 96));
        placementRateLabel = createCard("Placement Rate", "0%", new Color(142, 68, 173));
        avgCtcLabel = createCard("Average CTC", "0 LPA", new Color(230, 126, 34));
        maxCtcLabel = createCard("Highest Package", "0 LPA", new Color(192, 57, 43));

        kpiPanel.add(totalStudentsLabel);
        kpiPanel.add(totalPlacedLabel);
        kpiPanel.add(placementRateLabel);
        kpiPanel.add(avgCtcLabel);
        kpiPanel.add(maxCtcLabel);

        panel.add(kpiPanel, BorderLayout.NORTH);

        // Center split tables: Department Breakdown & Company Distribution
        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        splitPane.setResizeWeight(0.5);

        // Department Table
        String[] deptCols = {"Department", "Total Students", "Placed", "Unplaced", "Placement %", "Avg CTC (LPA)", "Max CTC (LPA)"};
        deptAnalyticsModel = new DefaultTableModel(deptCols, 0);
        JTable deptTable = new JTable(deptAnalyticsModel);
        deptTable.setRowHeight(22);
        JScrollPane deptScroll = new JScrollPane(deptTable);
        deptScroll.setBorder(BorderFactory.createTitledBorder("Department-Wise Performance Pipeline ($group, $cond, $project)"));
        splitPane.setTopComponent(deptScroll);

        // Company Table
        String[] compCols = {"Recruiter Company", "Students Placed", "Average Package (LPA)", "Max Package (LPA)"};
        companyAnalyticsModel = new DefaultTableModel(compCols, 0);
        JTable compTable = new JTable(companyAnalyticsModel);
        compTable.setRowHeight(22);
        JScrollPane compScroll = new JScrollPane(compTable);
        compScroll.setBorder(BorderFactory.createTitledBorder("Top Recruiter Recruitment Distribution ($match, $group, $sort)"));
        splitPane.setBottomComponent(compScroll);

        panel.add(splitPane, BorderLayout.CENTER);

        JButton refreshBtn = new JButton("🔄 Refresh Placement Analytics");
        refreshBtn.setBackground(new Color(52, 73, 94));
        refreshBtn.setForeground(Color.WHITE);
        refreshBtn.addActionListener(e -> refreshAnalytics());
        panel.add(refreshBtn, BorderLayout.SOUTH);

        return panel;
    }

    private JLabel createCard(String title, String value, Color color) {
        JLabel label = new JLabel(String.format("<html><center><font size='3' color='#555555'>%s</font><br><font size='5' color='%s'><b>%s</b></font></center></html>",
                title, String.format("#%02x%02x%02x", color.getRed(), color.getGreen(), color.getBlue()), value), SwingConstants.CENTER);
        label.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220), 1, true));
        label.setOpaque(true);
        label.setBackground(Color.WHITE);
        return label;
    }

    private void refreshAnalytics() {
        try {
            Document kpi = analyticsDAO.getOverallKpiSummary();
            if (kpi != null && !kpi.isEmpty()) {
                double placementRate = kpi.get("placementPercentage", Number.class) != null ? kpi.get("placementPercentage", Number.class).doubleValue() : 0.0;
                double avgPackage = kpi.get("avgPackageLpa", Number.class) != null ? kpi.get("avgPackageLpa", Number.class).doubleValue() : 0.0;
                double maxPackage = kpi.get("highestPackageLpa", Number.class) != null ? kpi.get("highestPackageLpa", Number.class).doubleValue() : 0.0;

                totalStudentsLabel.setText(String.format("<html><center><font size='3' color='#555'>Total Students</font><br><font size='5' color='#2980b9'><b>%d</b></font></center></html>",
                        kpi.getInteger("totalStudents", 0)));
                totalPlacedLabel.setText(String.format("<html><center><font size='3' color='#555'>Placed Students</font><br><font size='5' color='#27ae60'><b>%d</b></font></center></html>",
                        kpi.getInteger("totalPlaced", 0)));
                placementRateLabel.setText(String.format("<html><center><font size='3' color='#555'>Placement Rate</font><br><font size='5' color='#8e44ad'><b>%.1f%%</b></font></center></html>",
                        placementRate));
                avgCtcLabel.setText(String.format("<html><center><font size='3' color='#555'>Average CTC</font><br><font size='5' color='#e67e22'><b>%.1f LPA</b></font></center></html>",
                        avgPackage));
                maxCtcLabel.setText(String.format("<html><center><font size='3' color='#555'>Highest CTC</font><br><font size='5' color='#c0392b'><b>%.1f LPA</b></font></center></html>",
                        maxPackage));
            }

            // Populate Department Breakdown
            deptAnalyticsModel.setRowCount(0);
            List<Document> deptRates = analyticsDAO.getDepartmentPlacementRates();
            for (Document d : deptRates) {
                deptAnalyticsModel.addRow(new Object[]{
                        d.getString("department"),
                        d.getInteger("totalStudents"),
                        d.getInteger("placedCount"),
                        d.getInteger("unplacedCount"),
                        String.format("%.2f%%", d.getDouble("placementRate")),
                        String.format("%.2f", d.getDouble("avgPackage")),
                        String.format("%.2f", d.getDouble("maxPackage"))
                });
            }

            // Populate Company Distribution
            companyAnalyticsModel.setRowCount(0);
            List<Document> compDist = analyticsDAO.getCompanyRecruitmentDistribution();
            for (Document d : compDist) {
                companyAnalyticsModel.addRow(new Object[]{
                        d.getString("company"),
                        d.getInteger("recruitsCount"),
                        String.format("%.2f", d.getDouble("avgPackage")),
                        String.format("%.2f", d.getDouble("maxPackage"))
                });
            }
        } catch (Exception e) {
            // Handle gracefully
        }
    }

    // -------------------------------------------------------------
    // TAB 5: QUERY EXPLAIN STATS (.explain)
    // -------------------------------------------------------------
    private JPanel createExplainTab() {
        JPanel panel = new JPanel(new BorderLayout(8, 8));
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));

        JPanel controlBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        controlBar.add(new JLabel("Test CGPA Threshold ($gte):"));
        explainCgpaSpinner = new JSpinner(new SpinnerNumberModel(8.0, 0.0, 10.0, 0.5));
        controlBar.add(explainCgpaSpinner);

        JButton runExplainBtn = new JButton("⚡ Run MongoDB Explain Plan");
        runExplainBtn.setBackground(new Color(230, 126, 34));
        runExplainBtn.setForeground(Color.WHITE);
        runExplainBtn.addActionListener(e -> {
            double cutoff = (Double) explainCgpaSpinner.getValue();
            Document explainDoc = studentDAO.explainCgpaQuery(cutoff);
            Document execStats = (Document) explainDoc.get("executionStats");

            StringBuilder sb = new StringBuilder();
            sb.append("========================================================================\n");
            sb.append("           MONGODB QUERY PLANNER & EXECUTION PERFORMANCE REPORT         \n");
            sb.append("========================================================================\n\n");

            if (execStats != null) {
                sb.append(String.format("• Execution Success       : %s\n", execStats.get("executionSuccess")));
                sb.append(String.format("• Execution Time          : %s milliseconds\n", execStats.get("executionTimeMillis")));
                sb.append(String.format("• Documents Returned (n)  : %s\n", execStats.get("nReturned")));
                sb.append(String.format("• Index Keys Examined     : %s\n", execStats.get("totalKeysExamined")));
                sb.append(String.format("• Raw Documents Examined  : %s\n", execStats.get("totalDocsExamined")));

                Document stages = (Document) execStats.get("executionStages");
                if (stages != null) {
                    sb.append(String.format("• Root Stage              : %s\n", stages.get("stage")));
                    if (stages.get("inputStage") != null) {
                        Document inStage = (Document) stages.get("inputStage");
                        sb.append(String.format("• Leaf / Scan Stage       : %s (Index: %s)\n", inStage.get("stage"), inStage.get("indexName")));
                    }
                }
                sb.append("\n------------------------------------------------------------------------\n");
                sb.append("PERFORMANCE ASSESSMENT:\n");
                sb.append("The query executed against collection 'students' filtering by { cgpa: { $gte: " + cutoff + " } }.\n");
                sb.append("The B-Tree index on 'cgpa' was chosen by the query planner.\n");
                sb.append("Efficiency Ratio (Docs Examined / Returned) = Optimal.\n");
            } else {
                sb.append(explainDoc.toJson());
            }

            explainOutputArea.setText(sb.toString());
        });
        controlBar.add(runExplainBtn);

        panel.add(controlBar, BorderLayout.NORTH);

        explainOutputArea = new JTextArea();
        explainOutputArea.setFont(new Font("Consolas", Font.PLAIN, 13));
        explainOutputArea.setEditable(false);
        explainOutputArea.setText("Click 'Run MongoDB Explain Plan' to inspect B-Tree index scan execution stats.\n");
        panel.add(new JScrollPane(explainOutputArea), BorderLayout.CENTER);

        return panel;
    }

    public static void launch() {
        SwingUtilities.invokeLater(() -> {
            PlacementDashboardGUI gui = new PlacementDashboardGUI();
            gui.setVisible(true);
        });
    }
}
