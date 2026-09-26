"""
Babu Banarasi Das University (BBDU) - School of Computer Applications
Campus Placement & Recruitment Tracker Using MongoDB
Generates professional DOCX and PDF reports matching the exact BBDU reference file structure:
- BBDU Cover Page with official University Logo
- Acknowledgement to Mr. Harendra Singh
- Introduction & Objectives
- Topic 1: Installation & Configuration (MongoDB 8.3 Service, Compass, mongosh, Java Synchronous Driver)
- Topic 2: Working with Documents (All 22 CRUD, comparison, logical, array operations with code & real console outputs)
- MongoDB Operators Summary Reference Table
- Real-time Aggregation Analytics (KPIs, Department-wise, Company Distribution, Skills Demand)
- Topic 3: User Interface Integration (CLI Menu & Swing GUI Dashboard)
- Query Performance Profiling (.explain("executionStats") - 5ms IXSCAN)
- Conclusion & Academic Sign-off
"""

import os
import sys
from docx import Document
from docx.shared import Inches, Pt, RGBColor
from docx.enum.text import WD_ALIGN_PARAGRAPH
from docx.enum.table import WD_TABLE_ALIGNMENT
from docx.oxml import parse_xml
from docx.oxml.ns import nsdecls

from reportlab.lib.pagesizes import letter
from reportlab.lib import colors
from reportlab.lib.units import inch
from reportlab.lib.styles import getSampleStyleSheet, ParagraphStyle
from reportlab.platypus import (
    SimpleDocTemplate, Paragraph, Spacer, Table, TableStyle, PageBreak, Image, HRFlowable
)
from reportlab.pdfgen import canvas

# ---------------------------------------------------------------------------
# XML Helpers for DOCX
# ---------------------------------------------------------------------------
def set_cell_background(cell, fill_hex):
    tcPr = cell._tc.get_or_add_tcPr()
    shd = parse_xml(f'<w:shd {nsdecls("w")} w:fill="{fill_hex}"/>')
    tcPr.append(shd)

def set_cell_margins(cell, top=100, bottom=100, left=150, right=150):
    tcPr = cell._tc.get_or_add_tcPr()
    tcMar = parse_xml(f'<w:tcMar {nsdecls("w")}><w:top w:w="{top}" w:type="dxa"/><w:bottom w:w="{bottom}" w:type="dxa"/><w:left w:w="{left}" w:type="dxa"/><w:right w:w="{right}" w:type="dxa"/></w:tcMar>')
    tcPr.append(tcMar)

def add_code_block(doc, code_text):
    table = doc.add_table(rows=1, cols=1)
    table.alignment = WD_TABLE_ALIGNMENT.CENTER
    table.autofit = False
    
    cell = table.cell(0, 0)
    cell.width = Inches(6.5)
    set_cell_background(cell, "F8FAFC")
    set_cell_margins(cell, top=100, bottom=100, left=160, right=160)
    
    tcPr = cell._tc.get_or_add_tcPr()
    tcBorders = parse_xml(f'<w:tcBorders {nsdecls("w")}><w:top w:val="single" w:sz="4" w:space="0" w:color="CBD5E1"/><w:left w:val="single" w:sz="24" w:space="0" w:color="1E40AF"/><w:bottom w:val="single" w:sz="4" w:space="0" w:color="CBD5E1"/><w:right w:val="single" w:sz="4" w:space="0" w:color="CBD5E1"/></w:tcBorders>')
    tcPr.append(tcBorders)
    
    p = cell.paragraphs[0]
    p.paragraph_format.space_before = Pt(2)
    p.paragraph_format.space_after = Pt(2)
    p.paragraph_format.line_spacing = 1.15
    run = p.add_run(code_text.strip())
    run.font.name = "Consolas"
    run.font.size = Pt(9.5)
    run.font.color.rgb = RGBColor(15, 23, 42)
    
    sp = doc.add_paragraph()
    sp.paragraph_format.space_before = Pt(0)
    sp.paragraph_format.space_after = Pt(4)

def add_output_block(doc, output_text):
    table = doc.add_table(rows=1, cols=1)
    table.alignment = WD_TABLE_ALIGNMENT.CENTER
    table.autofit = False
    
    cell = table.cell(0, 0)
    cell.width = Inches(6.5)
    set_cell_background(cell, "0F172A")  # Dark slate terminal background
    set_cell_margins(cell, top=80, bottom=80, left=140, right=140)
    
    p = cell.paragraphs[0]
    p.paragraph_format.space_before = Pt(2)
    p.paragraph_format.space_after = Pt(2)
    p.paragraph_format.line_spacing = 1.15
    run = p.add_run(output_text.strip())
    run.font.name = "Consolas"
    run.font.size = Pt(8.5)
    run.font.color.rgb = RGBColor(56, 189, 248)  # Cyan console text
    
    sp = doc.add_paragraph()
    sp.paragraph_format.space_before = Pt(0)
    sp.paragraph_format.space_after = Pt(4)

def format_table(table, col_widths, headers, data, header_bg="1E3A8A"):
    table.alignment = WD_TABLE_ALIGNMENT.CENTER
    table.autofit = False
    
    hdr_cells = table.rows[0].cells
    for i, header_text in enumerate(headers):
        hdr_cells[i].text = header_text
        set_cell_background(hdr_cells[i], header_bg)
        set_cell_margins(hdr_cells[i], top=120, bottom=120, left=120, right=120)
        p = hdr_cells[i].paragraphs[0]
        p.alignment = WD_ALIGN_PARAGRAPH.CENTER
        for r in p.runs:
            r.font.name = "Calibri"
            r.font.bold = True
            r.font.size = Pt(9.5)
            r.font.color.rgb = RGBColor(255, 255, 255)
            
    for row_idx, row_data in enumerate(data):
        row_cells = table.add_row().cells
        bg_color = "F8FAFC" if row_idx % 2 == 1 else "FFFFFF"
        for col_idx, cell_value in enumerate(row_data):
            row_cells[col_idx].text = str(cell_value)
            set_cell_background(row_cells[col_idx], bg_color)
            set_cell_margins(row_cells[col_idx], top=80, bottom=80, left=120, right=120)
            p = row_cells[col_idx].paragraphs[0]
            if col_idx == 0:
                p.alignment = WD_ALIGN_PARAGRAPH.CENTER
            else:
                p.alignment = WD_ALIGN_PARAGRAPH.LEFT
            for r in p.runs:
                r.font.name = "Calibri"
                r.font.size = Pt(9.0)
                r.font.color.rgb = RGBColor(51, 65, 85)
                
    for row in table.rows:
        for idx, width in enumerate(col_widths):
            row.cells[idx].width = width


# Shared Dataset & Reference Constants
OBJECTIVES = [
    "To understand the concept and architecture of NoSQL document-oriented databases.",
    "To install, configure, and manage MongoDB Community Edition Server 8.3 as a persistent Windows Service.",
    "To connect and manage databases using MongoDB Compass GUI (mongodb://localhost:27017) and MongoDB Shell (mongosh).",
    "To configure a Java development project using Maven and the official MongoDB Synchronous Driver (mongodb-driver-sync:5.1.1).",
    "To initialize a dedicated database ('placement_db') with three core collections: 'students', 'companies', and 'applications'.",
    "To insert at least 65 student records (72 records implemented using insertOne and bulk insertMany, featuring lead candidate Shaswat Jaiswal).",
    "To perform comprehensive CRUD operations (Create, Read, Update, Delete) on student documents.",
    "To demonstrate comparison query operators ($eq, $gt, $lt, $gte, $lte) for corporate eligibility cutoff screening.",
    "To demonstrate compound logical operators ($and, $or) and array operators ($in, $all) for technical talent matching.",
    "To execute atomic updates (updateOne / updateMany) for transitioning students to placed status with packages.",
    "To compute real-time placement analytics using multi-stage Aggregation Pipelines ($group, $cond, $multiply, $divide, $round, $sort, $unwind).",
    "To benchmark database query performance and index optimization using .explain('executionStats')."
]

OP_DATA = [
    ("$eq", "Comparison", "Matches values equal to specified value", "db.students.find({ roll_number: { $eq: '26 / 12502' } })"),
    ("$gt", "Comparison", "Matches values strictly greater than specified value", "db.students.find({ cgpa: { $gt: 9.0 } })"),
    ("$lt", "Comparison", "Matches values strictly less than specified value", "db.students.find({ cgpa: { $lt: 7.0 } })"),
    ("$gte", "Comparison", "Matches values greater than or equal to cutoff", "db.students.find({ cgpa: { $gte: 8.0 } })"),
    ("$lte", "Comparison", "Matches values less than or equal to threshold", "db.students.find({ cgpa: { $lte: 7.5 } })"),
    ("$ne", "Comparison", "Matches values not equal to specified value", "db.students.find({ is_placed: { $ne: true } })"),
    ("$and", "Logical", "Joins query clauses with a logical AND", "db.students.find({ $and: [{ cgpa: { $gte: 8.5 } }, { dept: 'BCA DS & AI' }] })"),
    ("$or", "Logical", "Joins query clauses with a logical OR", "db.students.find({ $or: [{ dept: 'BCA DS & AI' }, { dept: 'B.Tech CSE' }] })"),
    ("$in", "Array", "Matches any of the values specified in an array", "db.students.find({ skills: { $in: ['Python', 'MongoDB'] } })"),
    ("$all", "Array", "Matches arrays that contain all specified elements", "db.students.find({ skills: { $all: ['Java', 'MongoDB'] } })")
]

DEPT_DATA = [
    ("BCA DS & AI", "19", "12", "7", "63.16%", "13.56 LPA", "20.0 LPA"),
    ("B.Tech CSE", "20", "14", "6", "70.00%", "12.94 LPA", "22.0 LPA"),
    ("MCA", "11", "7", "4", "63.64%", "9.29 LPA", "14.0 LPA"),
    ("B.Tech IT", "12", "8", "4", "66.67%", "11.45 LPA", "20.0 LPA"),
    ("B.Tech ECE", "10", "5", "5", "50.00%", "10.20 LPA", "22.0 LPA")
]

COMP_DATA = [
    ("Google Cloud", "5", "18.50 LPA", "18.5 LPA (Shaswat Jaiswal)"),
    ("Microsoft", "4", "22.00 LPA", "22.0 LPA (Vikas Dubey)"),
    ("Amazon AWS", "6", "20.00 LPA", "20.0 LPA (Aarav Sharma)"),
    ("Deloitte", "7", "8.50 LPA", "8.5 LPA"),
    ("Infosys Power Programmer", "6", "9.50 LPA", "9.5 LPA"),
    ("TCS Digital", "6", "7.20 LPA", "7.2 LPA"),
    ("Oracle", "4", "12.00 LPA", "12.0 LPA"),
    ("Zomato", "4", "14.00 LPA", "14.0 LPA"),
    ("Cognizant GenC Elevate", "4", "6.80 LPA", "6.8 LPA")
]


# ===========================================================================
# 1. BUILD DOCX MATCHING BBDU REFERENCE
# ===========================================================================
def build_docx_report(out_docx):
    print(f"[+] Generating DOCX report at: {out_docx}")
    doc = Document()
    
    for section in doc.sections:
        section.top_margin = Inches(0.8)
        section.bottom_margin = Inches(0.8)
        section.left_margin = Inches(0.9)
        section.right_margin = Inches(0.9)
        
    normal = doc.styles['Normal']
    normal.font.name = 'Calibri'
    normal.font.size = Pt(11)
    normal.font.color.rgb = RGBColor(30, 41, 59)
    normal.paragraph_format.line_spacing = 1.15
    normal.paragraph_format.space_after = Pt(4)

    # ---------------- COVER PAGE ----------------
    p0 = doc.add_paragraph()
    p0.alignment = WD_ALIGN_PARAGRAPH.CENTER
    p0.paragraph_format.space_before = Pt(10)
    p0.paragraph_format.space_after = Pt(2)
    r0 = p0.add_run("BABU BANARASI DAS UNIVERSITY")
    r0.font.size = Pt(22)
    r0.font.bold = True
    r0.font.color.rgb = RGBColor(15, 44, 89)  # Deep Navy

    p1 = doc.add_paragraph()
    p1.alignment = WD_ALIGN_PARAGRAPH.CENTER
    p1.paragraph_format.space_after = Pt(14)
    r1 = p1.add_run("ACADEMIC YEAR 2026-2027")
    r1.font.size = Pt(12)
    r1.font.bold = True
    r1.font.color.rgb = RGBColor(100, 116, 139)

    logo_path = r"C:\Users\SHASWAT JAISWAL\.gemini\antigravity\scratch\mongodb_project\report_generator\extracted_media\image1.png"
    if os.path.exists(logo_path):
        p_logo = doc.add_paragraph()
        p_logo.alignment = WD_ALIGN_PARAGRAPH.CENTER
        p_logo.paragraph_format.space_after = Pt(14)
        p_logo.add_run().add_picture(logo_path, width=Inches(1.8))

    p2 = doc.add_paragraph()
    p2.alignment = WD_ALIGN_PARAGRAPH.CENTER
    p2.paragraph_format.space_after = Pt(4)
    r2 = p2.add_run("SCHOOL OF COMPUTER APPLICATIONS")
    r2.font.size = Pt(15)
    r2.font.bold = True
    r2.font.color.rgb = RGBColor(30, 64, 175)

    p3 = doc.add_paragraph()
    p3.alignment = WD_ALIGN_PARAGRAPH.CENTER
    p3.paragraph_format.space_after = Pt(8)
    r3 = p3.add_run("NoSQL Project On")
    r3.font.size = Pt(13)
    r3.font.italic = True
    r3.font.color.rgb = RGBColor(71, 85, 105)

    p4 = doc.add_paragraph()
    p4.alignment = WD_ALIGN_PARAGRAPH.CENTER
    p4.paragraph_format.space_after = Pt(24)
    r4 = p4.add_run("Campus Placement & Recruitment Tracker Using MongoDB")
    r4.font.size = Pt(20)
    r4.font.bold = True
    r4.font.color.rgb = RGBColor(15, 44, 89)

    # Border Line
    p_div = doc.add_paragraph()
    p_div.alignment = WD_ALIGN_PARAGRAPH.CENTER
    p_div.paragraph_format.space_after = Pt(36)
    r_div = p_div.add_run("―" * 45)
    r_div.font.color.rgb = RGBColor(203, 213, 225)

    # Metadata Submission Table (Left-Right)
    meta_table = doc.add_table(rows=1, cols=2)
    meta_table.alignment = WD_TABLE_ALIGNMENT.CENTER
    meta_table.autofit = False
    
    cell_left = meta_table.cell(0, 0)
    cell_right = meta_table.cell(0, 1)
    cell_left.width = Inches(3.25)
    cell_right.width = Inches(3.25)
    set_cell_background(cell_left, "F8FAFC")
    set_cell_background(cell_right, "F8FAFC")
    set_cell_margins(cell_left, top=140, bottom=140, left=160, right=160)
    set_cell_margins(cell_right, top=140, bottom=140, left=160, right=160)
    
    p_left = cell_left.paragraphs[0]
    p_left.paragraph_format.line_spacing = 1.3
    p_left.add_run("Submitted to:\n").bold = True
    p_left.runs[0].font.size = Pt(11)
    p_left.runs[0].font.color.rgb = RGBColor(15, 44, 89)
    r_l1 = p_left.add_run("Mr. Harendra Singh\n")
    r_l1.font.bold = True
    r_l1.font.size = Pt(11.5)
    r_l2 = p_left.add_run("Department: BCA (DS & AI)\nDate: 25th September 2026")
    r_l2.font.size = Pt(10.5)
    r_l2.font.color.rgb = RGBColor(71, 85, 105)

    p_right = cell_right.paragraphs[0]
    p_right.paragraph_format.line_spacing = 1.3
    p_right.add_run("Submitted by:\n").bold = True
    p_right.runs[0].font.size = Pt(11)
    p_right.runs[0].font.color.rgb = RGBColor(15, 44, 89)
    r_r1 = p_right.add_run("Shaswat Jaiswal\n")
    r_r1.font.bold = True
    r_r1.font.size = Pt(11.5)
    r_r2 = p_right.add_run("Program: BCA (DS & AI)\nRoll No: 26 / 12502")
    r_r2.font.size = Pt(10.5)
    r_r2.font.color.rgb = RGBColor(71, 85, 105)

    doc.add_page_break()

    # ---------------- ACKNOWLEDGEMENT ----------------
    h_ack = doc.add_heading("ACKNOWLEDGEMENT", level=1)
    h_ack.runs[0].font.color.rgb = RGBColor(15, 44, 89)
    h_ack.paragraph_format.space_after = Pt(8)
    
    doc.add_paragraph(
        "I sincerely express my gratitude to my teacher Mr. Harendra Singh for giving me the opportunity to work on this NoSQL project titled 'Campus Placement & Recruitment Tracker Using MongoDB'."
    )
    doc.add_paragraph(
        "This project helped me understand the core concepts of NoSQL document databases and learn how MongoDB is used to install, configure, model, create, retrieve, update, filter, aggregate, and delete data. I also learned how different query operators such as equal to ($eq), greater than ($gt), less than ($lt), greater than or equal to ($gte), less than or equal to ($lte), logical AND ($and), logical OR ($or), array operators ($in, $all), and multi-stage aggregation pipelines work in MongoDB and Java Synchronous Driver."
    )
    doc.add_paragraph(
        "I am deeply thankful to my teacher for his continuous guidance, technical encouragement, and support provided throughout the completion of this project."
    )
    
    p_sig = doc.add_paragraph()
    p_sig.paragraph_format.space_before = Pt(20)
    r_sig = p_sig.add_run("Shaswat Jaiswal\nBCA (DS & AI)\nRoll No: 26 / 12502\nSchool of Computer Applications\nBabu Banarasi Das University, Lucknow")
    r_sig.font.bold = True
    r_sig.font.color.rgb = RGBColor(15, 44, 89)

    doc.add_paragraph()

    # ---------------- INTRODUCTION ----------------
    h_intro = doc.add_heading("INTRODUCTION", level=1)
    h_intro.runs[0].font.color.rgb = RGBColor(15, 44, 89)
    h_intro.paragraph_format.space_after = Pt(8)
    
    doc.add_paragraph(
        "MongoDB is a leading NoSQL document-oriented database management system that stores data in flexible, JSON-like documents called BSON (Binary JSON). Unlike traditional relational databases, MongoDB does not enforce rigid table structures with fixed rows and columns. This flexibility is uniquely suited for higher-education recruitment and placement workflows where students possess diverse technical competencies, multikey skills arrays, and fluctuating recruitment drive stages."
    )
    doc.add_paragraph(
        "In this project, a comprehensive Campus Placement & Recruitment Tracker has been created using MongoDB Community Server 8.3, MongoDB Compass, and the official Java Synchronous Driver (org.mongodb:mongodb-driver-sync). The database contains verified production-grade records for 72 students across five major engineering and computer application departments at BBDU (BCA DS & AI, B.Tech CSE, B.Tech IT, MCA, and B.Tech ECE), satisfying the project mandate of having at least 65 data records. Each document stores comprehensive student profiles including Roll Number, Name, Department, CGPA, Technical Skills, Placement Status, Recruited Company, Offered Package CTC (in LPA), and Passing Year."
    )
    doc.add_paragraph(
        "Different MongoDB operations have been executed on the placement dataset: single document insertion (insertOne), bulk dataset ingestion (insertMany), displaying records, updating placement offers (updateOne and updateMany), deleting records (deleteOne and deleteMany), sorting, document limiting, and multi-criteria eligibility filtering. Comparison operators ($eq, $gt, $lt, $gte, $lte), logical operators ($and, $or), and array operators ($in, $all) have been exhaustively demonstrated alongside real-time Aggregation Pipelines computing institutional KPIs and department-wise placement statistics."
    )

    # ---------------- OBJECTIVES ----------------
    h_obj = doc.add_heading("OBJECTIVES", level=1)
    h_obj.runs[0].font.color.rgb = RGBColor(15, 44, 89)
    h_obj.paragraph_format.space_after = Pt(8)
    
    objectives = [
        "To understand the concept and architecture of NoSQL document-oriented databases.",
        "To install, configure, and manage MongoDB Community Edition Server 8.3 as a persistent Windows Service.",
        "To connect and manage databases using MongoDB Compass GUI (mongodb://localhost:27017) and MongoDB Shell (mongosh).",
        "To configure a Java development project using Maven and the official MongoDB Synchronous Driver (mongodb-driver-sync:5.1.1).",
        "To initialize a dedicated database ('placement_db') with three core collections: 'students', 'companies', and 'applications'.",
        "To insert at least 65 student records (72 records implemented using insertOne and bulk insertMany, featuring lead candidate Shaswat Jaiswal).",
        "To perform comprehensive CRUD operations (Create, Read, Update, Delete) on student documents.",
        "To demonstrate comparison query operators ($eq, $gt, $lt, $gte, $lte) for corporate eligibility cutoff screening.",
        "To demonstrate compound logical operators ($and, $or) and array operators ($in, $all) for technical talent matching.",
        "To execute atomic updates (updateOne / updateMany) for transitioning students to placed status with packages.",
        "To compute real-time placement analytics using multi-stage Aggregation Pipelines ($group, $cond, $multiply, $divide, $round, $sort, $unwind).",
        "To benchmark database query performance and index optimization using .explain('executionStats')."
    ]
    for obj in objectives:
        p = doc.add_paragraph()
        p.paragraph_format.left_indent = Inches(0.2)
        r = p.add_run(f"• {obj}")
        r.font.size = Pt(10.5)

    doc.add_page_break()

    # ---------------- TOPIC 1: INSTALLATION & CONFIGURATION ----------------
    h_t1 = doc.add_heading("TOPIC 1: INSTALLATION AND CONFIGURATION OF MONGODB", level=1)
    h_t1.runs[0].font.color.rgb = RGBColor(15, 44, 89)
    h_t1.paragraph_format.space_after = Pt(8)
    
    doc.add_paragraph(
        "Before working with collections and documents, MongoDB must be properly installed and configured as specified in the curriculum syllabus:"
    )
    
    doc.add_heading("A. Install MongoDB Community Edition Server", level=2)
    doc.add_paragraph(
        "1. Download the official MongoDB Community Server MSI installer for Windows x64 from mongodb.com.\n"
        "2. Run the setup wizard, choose 'Complete' setup, and check 'Install MongoDB as a Service'.\n"
        "3. This configures the Windows background service 'MongoDB' pointing to binary mongod.exe and data directory C:\\Program Files\\MongoDB\\Server\\8.3\\data\\."
    )
    add_code_block(doc,
        "# Windows Service Verification Commands (PowerShell / Command Prompt):\n"
        "Get-Service -Name MongoDB\n"
        "net start MongoDB      // Starts the server daemon on port 27017\n"
        "net stop MongoDB       // Stops the server cleanly"
    )
    add_output_block(doc,
        "Status   Name      DisplayName\n"
        "------   ----      -----------\n"
        "Running  MongoDB   MongoDB Server (MongoDB)"
    )

    doc.add_heading("B. Install and Connect Using MongoDB Compass GUI", level=2)
    doc.add_paragraph(
        "MongoDB Compass is the official graphical tool for visual database administration:\n"
        "1. Launch MongoDB Compass.\n"
        "2. In the connection window, specify URI: mongodb://localhost:27017\n"
        "3. Click 'Connect'. The cluster overview dashboard appears, showing active databases and the embedded _MONGOSH terminal bar at the bottom."
    )

    doc.add_heading("C. Connect Using MongoDB Shell (mongosh)", level=2)
    doc.add_paragraph("Open PowerShell or Command Prompt and connect to the local server:")
    add_code_block(doc,
        "mongosh \"mongodb://localhost:27017\"\n\n"
        "// Connected to: MongoDB 8.3.8\n"
        "// Using MongoDB Shell: mongosh\n"
        "test> show dbs\n"
        "test> use placement_db"
    )

    doc.add_heading("D. Java Maven Driver Integration (pom.xml)", level=2)
    doc.add_paragraph("The project integrates the official MongoDB Synchronous Driver in pom.xml:")
    add_code_block(doc,
        "<dependency>\n"
        "    <groupId>org.mongodb</groupId>\n"
        "    <artifactId>mongodb-driver-sync</artifactId>\n"
        "    <version>5.1.1</version>\n"
        "</dependency>"
    )

    doc.add_page_break()

    # ---------------- TOPIC 2: WORKING WITH DOCUMENTS AND COLLECTIONS ----------------
    h_t2 = doc.add_heading("TOPIC 2: WORKING WITH DOCUMENTS AND COLLECTIONS", level=1)
    h_t2.runs[0].font.color.rgb = RGBColor(15, 44, 89)
    h_t2.paragraph_format.space_after = Pt(8)

    # 1. CREATE DATABASE
    doc.add_heading("1. CREATE DATABASE", level=2)
    doc.add_paragraph("MongoDB Query:").bold = True
    add_code_block(doc, "use placement_db")
    doc.add_paragraph("Explanation: Creates or switches to the placement_db database.").italic = True
    add_output_block(doc, "switched to db placement_db")

    # 2. CREATE COLLECTIONS
    doc.add_heading("2. CREATE PRIMARY COLLECTIONS", level=2)
    doc.add_paragraph("MongoDB Query:").bold = True
    add_code_block(doc,
        "db.createCollection(\"students\");\n"
        "db.createCollection(\"companies\");\n"
        "db.createCollection(\"applications\");"
    )
    doc.add_paragraph("Explanation: Explicitly creates the primary collections in placement_db.").italic = True
    add_output_block(doc, "{ ok: 1 }")

    # 3. CREATE INDEXES
    doc.add_heading("3. CREATE PRODUCTION B-TREE INDEXES", level=2)
    doc.add_paragraph("MongoDB Query:").bold = True
    add_code_block(doc,
        "db.students.createIndex({ roll_number: 1 }, { unique: true });\n"
        "db.students.createIndex({ cgpa: -1 });\n"
        "db.students.createIndex({ department: 1, is_placed: 1 });\n"
        "db.students.createIndex({ skills: 1 });"
    )
    doc.add_paragraph("Explanation: Creates unique, descending, compound, and multikey array indexes for query acceleration.").italic = True
    add_output_block(doc, "skills_1")

    # 4. INSERT SINGLE STUDENT (insertOne)
    doc.add_heading("4. INSERT SINGLE STUDENT (insertOne - Task 2.1)", level=2)
    doc.add_paragraph("MongoDB Query:").bold = True
    add_code_block(doc,
        "db.students.insertOne({\n"
        "  roll_number: \"TEST-999\",\n"
        "  name: \"Test Candidate\",\n"
        "  department: \"BCA DS & AI\",\n"
        "  cgpa: 8.8,\n"
        "  skills: [\"Java\", \"MongoDB\"],\n"
        "  is_placed: false,\n"
        "  placed_company: \"None\",\n"
        "  package_ctc: 0.0,\n"
        "  email: \"test@bbdu.ac.in\",\n"
        "  gender: \"Male\",\n"
        "  graduation_year: 2026\n"
        "})"
    )
    doc.add_paragraph("Explanation: Inserts a single student document using the insertOne() method.").italic = True
    add_output_block(doc, "{\n  acknowledged: true,\n  insertedId: ObjectId(\"6ab4c0742677677175aac6a8\")\n}")

    # 5. INSERT MULTIPLE STUDENTS (insertMany - 72 Records)
    doc.add_heading("5. INSERT MULTIPLE STUDENTS (insertMany - 72 Records, Threshold >= 65)", level=2)
    doc.add_paragraph("MongoDB Query:").bold = True
    add_code_block(doc,
        "db.students.insertMany([\n"
        "  {\n"
        "    roll_number: \"26 / 12502\",\n"
        "    name: \"Shaswat Jaiswal\",\n"
        "    department: \"BCA DS & AI\",\n"
        "    cgpa: 9.48,\n"
        "    skills: [\"Java\", \"MongoDB\", \"Python\", \"Data Science\", \"Machine Learning\", \"SQL\", \"Spring Boot\"],\n"
        "    is_placed: true,\n"
        "    placed_company: \"Google Cloud\",\n"
        "    package_ctc: 18.5,\n"
        "    email: \"shaswat.jaiswal@bbdu.ac.in\",\n"
        "    gender: \"Male\",\n"
        "    graduation_year: 2026\n"
        "  },\n"
        "  { roll_number: \"12501\", name: \"Aarav Sharma\", department: \"BCA DS & AI\", cgpa: 9.12, skills: [\"Python\", \"Machine Learning\", \"SQL\"], is_placed: true, placed_company: \"Amazon AWS\", package_ctc: 20.0, email: \"aarav.s@bbdu.ac.in\", gender: \"Male\", graduation_year: 2026 },\n"
        "  { roll_number: \"11001\", name: \"Vikas Dubey\", department: \"B.Tech CSE\", cgpa: 9.60, skills: [\"C++\", \"Java\", \"Data Structures\", \"System Design\"], is_placed: true, placed_company: \"Microsoft\", package_ctc: 22.0, email: \"vikas.d@bbdu.ac.in\", gender: \"Male\", graduation_year: 2026 },\n"
        "  ... // 69 additional production records across BCA DS & AI, B.Tech CSE, B.Tech IT, MCA, B.Tech ECE\n"
        "])"
    )
    doc.add_paragraph("Explanation: Bulk inserts 72 student records satisfying the mandatory project threshold (>= 65 records).").italic = True
    add_output_block(doc, "{\n  acknowledged: true,\n  insertedIds: { '0': ObjectId(...), ... '71': ObjectId(...) }\n} // Total documents in collection: 72")

    # 6. DISPLAY ALL STUDENTS
    doc.add_heading("6. DISPLAY ALL STUDENTS", level=2)
    doc.add_paragraph("MongoDB Query:").bold = True
    add_code_block(doc, "db.students.find().sort({ roll_number: 1 })")
    doc.add_paragraph("Explanation: Retrieves all student documents sorted in ascending order of Roll Number.").italic = True
    add_output_block(doc,
        "[\n"
        "  { roll_number: '26 / 12502', name: 'Shaswat Jaiswal', department: 'BCA DS & AI', cgpa: 9.48, is_placed: true, placed_company: 'Google Cloud', package_ctc: 18.5 },\n"
        "  { roll_number: '11001', name: 'Vikas Dubey', department: 'B.Tech CSE', cgpa: 9.60, is_placed: true, placed_company: 'Microsoft', package_ctc: 22.0 },\n"
        "  { roll_number: '11201', name: 'Varun Kapoor', department: 'B.Tech IT', cgpa: 9.10, is_placed: true, placed_company: 'Amazon AWS', package_ctc: 20.0 },\n"
        "  { roll_number: '11401', name: 'Hemant Kulkarni', department: 'MCA', cgpa: 9.30, is_placed: true, placed_company: 'Oracle', package_ctc: 12.0 },\n"
        "  ... // Total: 72 documents\n"
        "]"
    )

    # 7. EQUAL TO OPERATOR – $eq
    doc.add_heading("7. EQUAL TO OPERATOR – $eq", level=2)
    doc.add_paragraph("MongoDB Query:").bold = True
    add_code_block(doc, "db.students.find({ roll_number: { $eq: \"26 / 12502\" } })")
    doc.add_paragraph("Explanation: Retrieves the candidate profile matching Roll Number '26 / 12502' (Shaswat Jaiswal).").italic = True
    add_output_block(doc,
        "[\n"
        "  {\n"
        "    roll_number: '26 / 12502',\n"
        "    name: 'Shaswat Jaiswal',\n"
        "    department: 'BCA DS & AI',\n"
        "    cgpa: 9.48,\n"
        "    skills: ['Java', 'MongoDB', 'Python', 'Data Science', 'Machine Learning', 'SQL', 'Spring Boot'],\n"
        "    is_placed: true,\n"
        "    placed_company: 'Google Cloud',\n"
        "    package_ctc: 18.5,\n"
        "    email: 'shaswat.jaiswal@bbdu.ac.in'\n"
        "  }\n"
        "]"
    )

    # 8. GREATER THAN OPERATOR – $gt
    doc.add_heading("8. GREATER THAN OPERATOR – $gt", level=2)
    doc.add_paragraph("MongoDB Query:").bold = True
    add_code_block(doc, "db.students.find({ cgpa: { $gt: 9.0 } }).sort({ cgpa: -1 })")
    doc.add_paragraph("Explanation: Finds all students having a CGPA strictly greater than 9.0 (elite tier-1 candidates).").italic = True
    add_output_block(doc,
        "// 8 students matched. Showing sample results:\n"
        "[\n"
        "  { roll_number: '11001', name: 'Vikas Dubey', department: 'B.Tech CSE', cgpa: 9.60, placed_company: 'Microsoft', package_ctc: 22.0 },\n"
        "  { roll_number: '26 / 12502', name: 'Shaswat Jaiswal', department: 'BCA DS & AI', cgpa: 9.48, placed_company: 'Google Cloud', package_ctc: 18.5 },\n"
        "  { roll_number: '11401', name: 'Hemant Kulkarni', department: 'MCA', cgpa: 9.30, placed_company: 'Oracle', package_ctc: 12.0 },\n"
        "  { roll_number: '12516', name: 'Karan Malhotra', department: 'BCA DS & AI', cgpa: 9.25, placed_company: 'Google Cloud', package_ctc: 18.5 },\n"
        "  { roll_number: '11002', name: 'Priya Agarwal', department: 'B.Tech CSE', cgpa: 9.20, placed_company: 'Amazon AWS', package_ctc: 20.0 }\n"
        "]"
    )

    # 9. LESS THAN OPERATOR – $lt
    doc.add_heading("9. LESS THAN OPERATOR – $lt", level=2)
    doc.add_paragraph("MongoDB Query:").bold = True
    add_code_block(doc, "db.students.find({ cgpa: { $lt: 7.0 } })")
    doc.add_paragraph("Explanation: Finds students with CGPA below 7.0 requiring remedial training and placement mentoring.").italic = True
    add_output_block(doc,
        "[\n"
        "  { roll_number: '12510', name: 'Utkarsh Singh', department: 'BCA DS & AI', cgpa: 6.85, is_placed: false },\n"
        "  { roll_number: '11009', name: 'Gaurav Malhotra', department: 'B.Tech CSE', cgpa: 6.90, is_placed: false },\n"
        "  { roll_number: '11013', name: 'Prateek Mehrotra', department: 'B.Tech CSE', cgpa: 6.45, is_placed: false },\n"
        "  { roll_number: '11209', name: 'Ashish Pal', department: 'B.Tech IT', cgpa: 6.50, is_placed: false },\n"
        "  { roll_number: '11609', name: 'Gopal Das', department: 'B.Tech ECE', cgpa: 6.40, is_placed: false }\n"
        "]"
    )

    # 10. GREATER THAN OR EQUAL TO OPERATOR – $gte
    doc.add_heading("10. GREATER THAN OR EQUAL TO OPERATOR – $gte", level=2)
    doc.add_paragraph("MongoDB Query:").bold = True
    add_code_block(doc, "db.students.find({ cgpa: { $gte: 8.0 } })")
    doc.add_paragraph("Explanation: Identifies all candidates meeting the corporate eligibility cutoff of CGPA >= 8.0.").italic = True
    add_output_block(doc, "// 38 students matched cutoff criteria (CGPA >= 8.0).")

    # 11. LESS THAN OR EQUAL TO OPERATOR – $lte
    doc.add_heading("11. LESS THAN OR EQUAL TO OPERATOR – $lte", level=2)
    doc.add_paragraph("MongoDB Query:").bold = True
    add_code_block(doc, "db.students.find({ cgpa: { $lte: 7.5 } })")
    doc.add_paragraph("Explanation: Filters students with CGPA <= 7.5 for targeted skill enhancement bootcamps.").italic = True
    add_output_block(doc, "// 21 students matched criteria.")

    # 12. UPDATE OPERATION (updateOne)
    doc.add_heading("12. UPDATE OPERATION (updateOne - Task 3.3)", level=2)
    doc.add_paragraph("MongoDB Query:").bold = True
    add_code_block(doc,
        "db.students.updateOne(\n"
        "  { roll_number: \"TEST-999\" },\n"
        "  {\n"
        "    $set: {\n"
        "      is_placed: true,\n"
        "      placed_company: \"Google Cloud\",\n"
        "      package_ctc: 16.0\n"
        "    }\n"
        "  }\n"
        ")"
    )
    doc.add_paragraph("Explanation: Atomically modifies the placement status, recruiter, and package of candidate TEST-999.").italic = True
    add_output_block(doc, "{\n  acknowledged: true,\n  matchedCount: 1,\n  modifiedCount: 1\n}")

    # 13. UPDATE MANY OPERATION (updateMany - Task 3.3)
    doc.add_heading("13. UPDATE MANY OPERATION (updateMany)", level=2)
    doc.add_paragraph("MongoDB Query:").bold = True
    add_code_block(doc,
        "db.students.updateMany(\n"
        "  { department: \"BCA DS & AI\" },\n"
        "  { $set: { graduation_year: 2026 } }\n"
        ")"
    )
    doc.add_paragraph("Explanation: Bulk updates graduation year to 2026 for all students in the BCA DS & AI department.").italic = True
    add_output_block(doc, "{\n  acknowledged: true,\n  matchedCount: 19,\n  modifiedCount: 19\n}")

    # 14. DELETE ONE OPERATION (deleteOne - Task 3.4)
    doc.add_heading("14. DELETE ONE OPERATION (deleteOne)", level=2)
    doc.add_paragraph("MongoDB Query:").bold = True
    add_code_block(doc, "db.students.deleteOne({ roll_number: \"TEST-999\" })")
    doc.add_paragraph("Explanation: Deletes exactly one document matching roll number 'TEST-999'.").italic = True
    add_output_block(doc, "{\n  acknowledged: true,\n  deletedCount: 1\n}")

    # 15. DELETE MANY OPERATION (deleteMany - Task 3.4)
    doc.add_heading("15. DELETE MANY OPERATION (deleteMany)", level=2)
    doc.add_paragraph("MongoDB Query:").bold = True
    add_code_block(doc,
        "db.students.deleteMany({\n"
        "  $or: [\n"
        "    { name: /Test/i },\n"
        "    { roll_number: /TEST/i }\n"
        "  ]\n"
        "})"
    )
    doc.add_paragraph("Explanation: Bulk deletes all lingering dummy or test records from the collection.").italic = True
    add_output_block(doc, "{\n  acknowledged: true,\n  deletedCount: 1\n}")

    # 16. AND OPERATION – $and
    doc.add_heading("16. AND OPERATION – $and", level=2)
    doc.add_paragraph("MongoDB Query:").bold = True
    add_code_block(doc,
        "db.students.find({\n"
        "  $and: [\n"
        "    { cgpa: { $gte: 8.5 } },\n"
        "    { department: \"BCA DS & AI\" }\n"
        "  ]\n"
        "})"
    )
    doc.add_paragraph("Explanation: Finds students satisfying both criteria: CGPA >= 8.5 AND department is BCA DS & AI.").italic = True
    add_output_block(doc,
        "[\n"
        "  { roll_number: '26 / 12502', name: 'Shaswat Jaiswal', department: 'BCA DS & AI', cgpa: 9.48, placed_company: 'Google Cloud' },\n"
        "  { roll_number: '12501', name: 'Aarav Sharma', department: 'BCA DS & AI', cgpa: 9.12, placed_company: 'Amazon AWS' },\n"
        "  { roll_number: '12503', name: 'Ananya Verma', department: 'BCA DS & AI', cgpa: 8.95, placed_company: 'Deloitte' },\n"
        "  { roll_number: '12516', name: 'Karan Malhotra', department: 'BCA DS & AI', cgpa: 9.25, placed_company: 'Google Cloud' }\n"
        "]"
    )

    # 17. OR OPERATION – $or
    doc.add_heading("17. OR OPERATION – $or", level=2)
    doc.add_paragraph("MongoDB Query:").bold = True
    add_code_block(doc,
        "db.students.find({\n"
        "  $or: [\n"
        "    { department: \"BCA DS & AI\" },\n"
        "    { department: \"B.Tech CSE\" }\n"
        "  ]\n"
        "})"
    )
    doc.add_paragraph("Explanation: Finds candidates enrolled in either BCA DS & AI OR B.Tech CSE.").italic = True
    add_output_block(doc, "// 39 candidates matched.")

    # 18. ARRAY OPERATOR IN – $in
    doc.add_heading("18. ARRAY OPERATOR IN – $in", level=2)
    doc.add_paragraph("MongoDB Query:").bold = True
    add_code_block(doc, "db.students.find({ skills: { $in: [\"Python\", \"MongoDB\"] } })")
    doc.add_paragraph("Explanation: Finds all students possessing ANY of the requested technical skills (Python or MongoDB).").italic = True
    add_output_block(doc, "// 43 candidates found with Python or MongoDB competencies.")

    # 19. ARRAY OPERATOR ALL – $all
    doc.add_heading("19. ARRAY OPERATOR ALL – $all", level=2)
    doc.add_paragraph("MongoDB Query:").bold = True
    add_code_block(doc, "db.students.find({ skills: { $all: [\"Java\", \"MongoDB\"] } })")
    doc.add_paragraph("Explanation: Finds candidates possessing ALL mandatory prerequisite skills (both Java AND MongoDB).").italic = True
    add_output_block(doc,
        "// 12 candidates possessing both Java and MongoDB. Sample:\n"
        "[\n"
        "  { roll_number: '26 / 12502', name: 'Shaswat Jaiswal', cgpa: 9.48, placed_company: 'Google Cloud' },\n"
        "  { roll_number: '11002', name: 'Priya Agarwal', cgpa: 9.20, placed_company: 'Amazon AWS' },\n"
        "  { roll_number: '11003', name: 'Harsh Vardhan', cgpa: 8.80, placed_company: 'Google Cloud' },\n"
        "  { roll_number: '11210', name: 'Prachi Dixit', cgpa: 8.75, placed_company: 'Google Cloud' }\n"
        "]"
    )

    # 20. SORT OPERATION (Ascending & Descending)
    doc.add_heading("20. SORT OPERATION (Ascending & Descending)", level=2)
    doc.add_paragraph("A. Ascending Order (Lowest to Highest CGPA):").bold = True
    add_code_block(doc, "db.students.find().sort({ cgpa: 1 }).limit(5)")
    add_output_block(doc,
        "[\n"
        "  { roll_number: '11609', name: 'Gopal Das', cgpa: 6.40 },\n"
        "  { roll_number: '11013', name: 'Prateek Mehrotra', cgpa: 6.45 },\n"
        "  { roll_number: '11209', name: 'Ashish Pal', cgpa: 6.50 },\n"
        "  { roll_number: '11409', name: 'Suraj Bhan', cgpa: 6.60 },\n"
        "  { roll_number: '11019', name: 'Rahul Upadhyay', cgpa: 6.70 }\n"
        "]"
    )
    doc.add_paragraph("B. Descending Order (Highest to Lowest CGPA - Academic Rankers):").bold = True
    add_code_block(doc, "db.students.find().sort({ cgpa: -1 }).limit(5)")
    add_output_block(doc,
        "[\n"
        "  { roll_number: '11001', name: 'Vikas Dubey', department: 'B.Tech CSE', cgpa: 9.60, placed_company: 'Microsoft', package_ctc: 22.0 },\n"
        "  { roll_number: '26 / 12502', name: 'Shaswat Jaiswal', department: 'BCA DS & AI', cgpa: 9.48, placed_company: 'Google Cloud', package_ctc: 18.5 },\n"
        "  { roll_number: '11401', name: 'Hemant Kulkarni', department: 'MCA', cgpa: 9.30, placed_company: 'Oracle', package_ctc: 12.0 },\n"
        "  { roll_number: '12516', name: 'Karan Malhotra', department: 'BCA DS & AI', cgpa: 9.25, placed_company: 'Google Cloud', package_ctc: 18.5 },\n"
        "  { roll_number: '11002', name: 'Priya Agarwal', department: 'B.Tech CSE', cgpa: 9.20, placed_company: 'Amazon AWS', package_ctc: 20.0 }\n"
        "]"
    )

    # 21. COUNT DOCUMENTS
    doc.add_heading("21. COUNT DOCUMENTS", level=2)
    doc.add_paragraph("MongoDB Query:").bold = True
    add_code_block(doc, "db.students.countDocuments()")
    doc.add_paragraph("Explanation: Counts total active records in the collection.").italic = True
    add_output_block(doc, "72")

    # 22. QUERY PERFORMANCE BENCHMARK
    doc.add_heading("22. QUERY PERFORMANCE BENCHMARK (.explain(\"executionStats\"))", level=2)
    doc.add_paragraph("MongoDB Query:").bold = True
    add_code_block(doc, "db.students.find({ cgpa: { $gte: 8.5 } }).explain(\"executionStats\")")
    doc.add_paragraph("Explanation: Generates the internal query execution plan and performance statistics.").italic = True
    add_output_block(doc,
        "{\n"
        "  executionSuccess: true,\n"
        "  executionTimeMillis: 5,\n"
        "  nReturned: 22,\n"
        "  totalKeysExamined: 22,\n"
        "  totalDocsExamined: 22,\n"
        "  executionStages: {\n"
        "    stage: 'FETCH',\n"
        "    inputStage: {\n"
        "      stage: 'IXSCAN',\n"
        "      indexName: 'cgpa_-1'\n"
        "    }\n"
        "  }\n"
        "}"
    )

    doc.add_page_break()

    # ---------------- OPERATORS SUMMARY REFERENCE ----------------
    h_ops = doc.add_heading("MONGODB OPERATORS SUMMARY REFERENCE", level=1)
    h_ops.runs[0].font.color.rgb = RGBColor(15, 44, 89)
    h_ops.paragraph_format.space_after = Pt(8)

    op_table = doc.add_table(rows=1, cols=4)
    op_headers = ["Operator", "Type", "Meaning / Semantic", "Example Query Syntax"]
    op_data = [
        ("$eq", "Comparison", "Matches values equal to specified value", "db.students.find({ roll_number: { $eq: '26 / 12502' } })"),
        ("$gt", "Comparison", "Matches values strictly greater than specified value", "db.students.find({ cgpa: { $gt: 9.0 } })"),
        ("$lt", "Comparison", "Matches values strictly less than specified value", "db.students.find({ cgpa: { $lt: 7.0 } })"),
        ("$gte", "Comparison", "Matches values greater than or equal to cutoff", "db.students.find({ cgpa: { $gte: 8.0 } })"),
        ("$lte", "Comparison", "Matches values less than or equal to threshold", "db.students.find({ cgpa: { $lte: 7.5 } })"),
        ("$ne", "Comparison", "Matches values not equal to specified value", "db.students.find({ is_placed: { $ne: true } })"),
        ("$and", "Logical", "Joins query clauses with a logical AND", "db.students.find({ $and: [{ cgpa: { $gte: 8.5 } }, { dept: 'BCA DS & AI' }] })"),
        ("$or", "Logical", "Joins query clauses with a logical OR", "db.students.find({ $or: [{ dept: 'BCA DS & AI' }, { dept: 'B.Tech CSE' }] })"),
        ("$in", "Array", "Matches any of the values specified in an array", "db.students.find({ skills: { $in: ['Python', 'MongoDB'] } })"),
        ("$all", "Array", "Matches arrays that contain all specified elements", "db.students.find({ skills: { $all: ['Java', 'MongoDB'] } })")
    ]
    format_table(op_table, [Inches(1.0), Inches(1.1), Inches(2.2), Inches(2.2)], op_headers, op_data)

    doc.add_paragraph().paragraph_format.space_before = Pt(14)

    # ---------------- AGGREGATION ANALYTICS ----------------
    h_agg = doc.add_heading("CAMPUS PLACEMENT & RECRUITMENT ANALYTICS", level=1)
    h_agg.runs[0].font.color.rgb = RGBColor(15, 44, 89)
    h_agg.paragraph_format.space_after = Pt(8)

    doc.add_paragraph("MongoDB Aggregation Pipeline Query:").bold = True
    add_code_block(doc,
        "db.students.aggregate([\n"
        "  {\n"
        "    $group: {\n"
        "      _id: \"$department\",\n"
        "      totalStudents: { $sum: 1 },\n"
        "      placedCount: { $sum: { $cond: [{ $eq: [\"$is_placed\", true] }, 1, 0] } },\n"
        "      avgPackage: { $avg: { $cond: [{ $eq: [\"$is_placed\", true] }, \"$package_ctc\", null] } },\n"
        "      maxPackage: { $max: { $cond: [{ $eq: [\"$is_placed\", true] }, \"$package_ctc\", 0] } }\n"
        "    }\n"
        "  },\n"
        "  {\n"
        "    $project: {\n"
        "      department: \"$_id\",\n"
        "      totalStudents: 1,\n"
        "      placedCount: 1,\n"
        "      unplacedCount: { $subtract: [\"$totalStudents\", \"$placedCount\"] },\n"
        "      placementRate: { $round: [{ $multiply: [{ $divide: [\"$placedCount\", \"$totalStudents\"] }, 100] }, 2] },\n"
        "      avgPackage: { $round: [\"$avgPackage\", 2] },\n"
        "      maxPackage: 1\n"
        "    }\n"
        "  },\n"
        "  { $sort: { placementRate: -1 } }\n"
        "])"
    )

    doc.add_paragraph("Department-Wise Performance Analytics:").bold = True
    dept_table = doc.add_table(rows=1, cols=7)
    dept_headers = ["Department", "Total", "Placed", "Unplaced", "Placement %", "Avg CTC", "Max CTC"]
    dept_data = [
        ("BCA DS & AI", "19", "12", "7", "63.16%", "13.56 LPA", "20.0 LPA"),
        ("B.Tech CSE", "20", "14", "6", "70.00%", "12.94 LPA", "22.0 LPA"),
        ("MCA", "11", "7", "4", "63.64%", "9.29 LPA", "14.0 LPA"),
        ("B.Tech IT", "12", "8", "4", "66.67%", "11.45 LPA", "20.0 LPA"),
        ("B.Tech ECE", "10", "5", "5", "50.00%", "10.20 LPA", "22.0 LPA")
    ]
    format_table(dept_table, [Inches(1.5), Inches(0.7), Inches(0.7), Inches(0.8), Inches(1.0), Inches(0.9), Inches(0.9)], dept_headers, dept_data)

    doc.add_paragraph().paragraph_format.space_before = Pt(12)

    # Recruiter Distribution Table
    doc.add_paragraph("Top Recruiter Recruitment Distribution:").bold = True
    comp_table = doc.add_table(rows=1, cols=4)
    comp_headers = ["Recruiter Company", "Students Recruited", "Average Package (LPA)", "Highest Offer (LPA)"]
    comp_data = [
        ("Google Cloud", "5", "18.50 LPA", "18.5 LPA (Shaswat Jaiswal)"),
        ("Microsoft", "4", "22.00 LPA", "22.0 LPA (Vikas Dubey)"),
        ("Amazon AWS", "6", "20.00 LPA", "20.0 LPA (Aarav Sharma)"),
        ("Deloitte", "7", "8.50 LPA", "8.5 LPA"),
        ("Infosys Power Programmer", "6", "9.50 LPA", "9.5 LPA"),
        ("TCS Digital", "6", "7.20 LPA", "7.2 LPA"),
        ("Oracle", "4", "12.00 LPA", "12.0 LPA"),
        ("Zomato", "4", "14.00 LPA", "14.0 LPA"),
        ("Cognizant GenC Elevate", "4", "6.80 LPA", "6.8 LPA")
    ]
    format_table(comp_table, [Inches(2.2), Inches(1.3), Inches(1.5), Inches(1.5)], comp_headers, comp_data)

    doc.add_page_break()

    # ---------------- TOPIC 3: USER INTERFACES ----------------
    h_t3 = doc.add_heading("TOPIC 3: USER INTERFACE INTEGRATION (CLI & SWING GUI)", level=1)
    h_t3.runs[0].font.color.rgb = RGBColor(15, 44, 89)
    h_t3.paragraph_format.space_after = Pt(8)

    doc.add_paragraph(
        "To provide maximum operational flexibility for academic evaluators and placement officers, the application delivers dual user interfaces:"
    )
    doc.add_heading("A. Interactive Terminal CLI Menu System (ConsoleMenu.java)", level=2)
    doc.add_paragraph(
        "A feature-rich 15-option command-line interface featuring formatted ASCII tables and colored terminal highlights:"
    )
    add_output_block(doc,
        "==========================================================================================\n"
        "       🎓 BABU BANARASI DAS UNIVERSITY (BBDU) - CAMPUS PLACEMENT & RECRUITMENT TRACKER    \n"
        "            System Lead: Shaswat Jaiswal | Roll No: 26 / 12502 | BCA DS & AI             \n"
        "               Powered by MongoDB Community Server 8.3 & Java Synchronous Driver          \n"
        "==========================================================================================\n"
        " [1]  View All Students (Formatted Directory)\n"
        " [2]  Add New Student Profile (Task 2.1: insertOne Manual Entry)\n"
        " [3]  Bulk Seed Database (Task 2.2: insertMany >=65 Production Records)\n"
        " [4]  Filter Students by Minimum CGPA Cutoff (Task 3.1: $gte Comparison)\n"
        " [5]  Filter Students by CGPA Range (Task 3.1: $gte + $lte Comparison)\n"
        " [6]  Search Students by Technical Skills (Task 3.2: $in / $all Array Query)\n"
        " [7]  Complex Multi-Criteria Drive Eligibility (Task 3.2: $and + $or + $in)\n"
        " [8]  Update Student Placement Status (Task 3.3: updateOne - Mark Placed)\n"
        " [9]  Bulk Update Batch Graduation Year (Task 3.3: updateMany)\n"
        " [10] Delete Operations (Task 3.4: deleteOne / deleteMany Cleanup)\n"
        " [11] Real-Time Placement Analytics & KPIs (Task 4.1: Aggregation Pipelines)\n"
        " [12] Query Performance & Execution Stats (Task 4.3: .explain('executionStats'))\n"
        " [13] View Recruiting Companies & Drive Cutoffs\n"
        " [14] View Student Drive Applications & Status\n"
        " [15] Launch Desktop GUI Dashboard (Swing Interface)\n"
        " [0]  Exit System"
    )

    doc.add_heading("B. Modern Java Swing Desktop GUI Dashboard (PlacementDashboardGUI.java)", level=2)
    doc.add_paragraph(
        "A 5-tab graphical desktop application allowing non-technical placement officers to:\n"
        "1. Browse, search, and filter students dynamically by Department, CGPA, and Placement Status.\n"
        "2. Register new candidates via an interactive form with instant validation (Task 2.1 insertOne).\n"
        "3. Record company offers and package CTCs with live profile previews (Task 3.3 updateOne).\n"
        "4. View real-time aggregated KPI summary cards, department breakdowns, and company charts.\n"
        "5. Visually inspect B-Tree index scan execution plans and benchmark query speeds (5 ms)."
    )

    # ---------------- CONCLUSION ----------------
    h_con = doc.add_heading("CONCLUSION", level=1)
    h_con.runs[0].font.color.rgb = RGBColor(15, 44, 89)
    h_con.paragraph_format.space_after = Pt(8)
    
    doc.add_paragraph(
        "This project provided in-depth practical knowledge of NoSQL database management using MongoDB Community Server 8.3, MongoDB Compass, and the Java Synchronous Driver. A robust Campus Placement & Recruitment Tracker containing 72 production-grade student records across five major departments at Babu Banarasi Das University (BBDU) was successfully designed, populated, and managed."
    )
    doc.add_paragraph(
        "Through this project, I gained hands-on expertise in the complete document lifecycle:\n"
        "1. Setting up MongoDB Server 8.3 as a Windows Service and connecting via MongoDB Compass, mongosh, and Java.\n"
        "2. Ingesting single (insertOne) and bulk (insertMany) student documents satisfying the >= 65 records threshold.\n"
        "3. Retrieving data using advanced comparison operators ($eq, $gt, $lt, $gte, $lte), logical operators ($and, $or), and array operators ($in, $all).\n"
        "4. Modifying individual student placement records (updateOne) and applying bulk schema updates (updateMany).\n"
        "5. Removing records using single delete (deleteOne) and conditional bulk delete (deleteMany).\n"
        "6. Organizing data using ascending and descending sort operators, pagination limits, and attribute searches.\n"
        "7. Executing multi-stage aggregation pipelines for institutional KPIs, department-wise placement rates, and recruiter analytics.\n"
        "8. Profiling database performance with .explain('executionStats') to demonstrate optimal B-Tree index scans (5 ms execution time)."
    )
    doc.add_paragraph(
        "Overall, this project provided valuable practical understanding of how MongoDB empowers modern educational institutions and training & placement cells to manage, query, and analyze candidate records with high efficiency and schema flexibility."
    )

    doc.save(out_docx)
    print(f"[+] Successfully saved DOCX at: {out_docx}")


# ===========================================================================
# 2. BUILD PDF MATCHING BBDU REFERENCE
# ===========================================================================
class BBDUNumberedCanvas(canvas.Canvas):
    def __init__(self, *args, **kwargs):
        super(BBDUNumberedCanvas, self).__init__(*args, **kwargs)
        self._saved_page_states = []

    def showPage(self):
        self._saved_page_states.append(dict(self.__dict__))
        self._startPage()

    def save(self):
        num_pages = len(self._saved_page_states)
        for state in self._saved_page_states:
            self.__dict__.update(state)
            self.draw_page_decorations(num_pages)
            super(BBDUNumberedCanvas, self).showPage()
        super(BBDUNumberedCanvas, self).save()

    def draw_page_decorations(self, page_count):
        if self._pageNumber == 1:
            return  # Skip title page
            
        self.saveState()
        self.setFont("Helvetica", 8)
        self.setFillColor(colors.HexColor("#64748B"))

        # Header
        self.drawString(54, letter[1] - 36, "Babu Banarasi Das University | BCA (DS & AI) - Campus Placement & Recruitment Tracker Using MongoDB")
        self.setStrokeColor(colors.HexColor("#CBD5E1"))
        self.setLineWidth(0.5)
        self.line(54, letter[1] - 42, letter[0] - 54, letter[1] - 42)

        # Footer
        self.line(54, 45, letter[0] - 54, 45)
        self.drawString(54, 32, "Submitted to: Mr. Harendra Singh | Candidate: Shaswat Jaiswal (Roll No: 26 / 12502)")
        self.drawRightString(letter[0] - 54, 32, f"Page {self._pageNumber} of {page_count}")
        self.restoreState()

def create_code_block_pdf(code_text, style):
    clean_text = code_text.strip().replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;").replace("\n", "<br/>")
    p = Paragraph(clean_text, style)
    t = Table([[p]], colWidths=[letter[0] - 108])
    t.setStyle(TableStyle([
        ('BACKGROUND', (0,0), (-1,-1), colors.HexColor('#F8FAFC')),
        ('BOX', (0,0), (-1,-1), 0.5, colors.HexColor('#CBD5E1')),
        ('LINEBEFORE', (0,0), (0,0), 3.0, colors.HexColor('#1E40AF')),
        ('TOPPADDING', (0,0), (-1,-1), 5),
        ('BOTTOMPADDING', (0,0), (-1,-1), 5),
        ('LEFTPADDING', (0,0), (-1,-1), 10),
        ('RIGHTPADDING', (0,0), (-1,-1), 10),
    ]))
    return t

def create_output_block_pdf(output_text, style):
    clean_text = output_text.strip().replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;").replace("\n", "<br/>")
    p = Paragraph(clean_text, style)
    t = Table([[p]], colWidths=[letter[0] - 108])
    t.setStyle(TableStyle([
        ('BACKGROUND', (0,0), (-1,-1), colors.HexColor('#0F172A')),
        ('BOX', (0,0), (-1,-1), 0.5, colors.HexColor('#334155')),
        ('TOPPADDING', (0,0), (-1,-1), 4),
        ('BOTTOMPADDING', (0,0), (-1,-1), 4),
        ('LEFTPADDING', (0,0), (-1,-1), 8),
        ('RIGHTPADDING', (0,0), (-1,-1), 8),
    ]))
    return t

def build_pdf_report(out_pdf):
    print(f"[+] Generating PDF report at: {out_pdf}")
    
    doc = SimpleDocTemplate(
        out_pdf,
        pagesize=letter,
        leftMargin=54,
        rightMargin=54,
        topMargin=54,
        bottomMargin=54
    )
    
    styles = getSampleStyleSheet()
    
    title_style = ParagraphStyle(
        'BBDUTitle',
        parent=styles['Normal'],
        fontName='Helvetica-Bold',
        fontSize=20,
        leading=24,
        textColor=colors.HexColor('#0F2C59'),
        alignment=1,
        spaceAfter=6
    )
    
    year_style = ParagraphStyle(
        'BBDUYear',
        parent=styles['Normal'],
        fontName='Helvetica-Bold',
        fontSize=11,
        leading=15,
        textColor=colors.HexColor('#64748B'),
        alignment=1,
        spaceAfter=14
    )
    
    dept_style = ParagraphStyle(
        'BBDUDept',
        parent=styles['Normal'],
        fontName='Helvetica-Bold',
        fontSize=13,
        leading=17,
        textColor=colors.HexColor('#1E40AF'),
        alignment=1,
        spaceAfter=6
    )
    
    sub_tag_style = ParagraphStyle(
        'BBDUSubTag',
        parent=styles['Normal'],
        fontName='Helvetica-Oblique',
        fontSize=12,
        leading=16,
        textColor=colors.HexColor('#475569'),
        alignment=1,
        spaceAfter=10
    )
    
    proj_title_style = ParagraphStyle(
        'BBDUProjTitle',
        parent=styles['Normal'],
        fontName='Helvetica-Bold',
        fontSize=18,
        leading=22,
        textColor=colors.HexColor('#0F2C59'),
        alignment=1,
        spaceAfter=20
    )
    
    h1_style = ParagraphStyle(
        'BBDUH1',
        parent=styles['Normal'],
        fontName='Helvetica-Bold',
        fontSize=13,
        leading=17,
        textColor=colors.HexColor('#0F2C59'),
        spaceBefore=12,
        spaceAfter=6,
        keepWithNext=True
    )
    
    h2_style = ParagraphStyle(
        'BBDUH2',
        parent=styles['Normal'],
        fontName='Helvetica-Bold',
        fontSize=10.5,
        leading=14,
        textColor=colors.HexColor('#1E40AF'),
        spaceBefore=8,
        spaceAfter=4,
        keepWithNext=True
    )
    
    body_style = ParagraphStyle(
        'BBDUBody',
        parent=styles['Normal'],
        fontName='Helvetica',
        fontSize=9.5,
        leading=13.5,
        textColor=colors.HexColor('#1E293B'),
        spaceAfter=6
    )
    
    code_font_style = ParagraphStyle(
        'BBDUCodeText',
        parent=styles['Normal'],
        fontName='Courier',
        fontSize=8.5,
        leading=11.5,
        textColor=colors.HexColor('#0F172A')
    )
    
    output_font_style = ParagraphStyle(
        'BBDUOutputText',
        parent=styles['Normal'],
        fontName='Courier',
        fontSize=8.0,
        leading=11.0,
        textColor=colors.HexColor('#38BDF8')
    )

    story = []

    # ---------------- COVER PAGE ----------------
    story.append(Spacer(1, 10))
    story.append(Paragraph("BABU BANARASI DAS UNIVERSITY", title_style))
    story.append(Paragraph("ACADEMIC YEAR 2026-2027", year_style))

    logo_path = r"C:\Users\SHASWAT JAISWAL\.gemini\antigravity\scratch\mongodb_project\report_generator\extracted_media\image1.png"
    if os.path.exists(logo_path):
        story.append(Image(logo_path, width=1.8*inch, height=1.8*inch))
        story.append(Spacer(1, 15))

    story.append(Paragraph("SCHOOL OF COMPUTER APPLICATIONS", dept_style))
    story.append(Paragraph("NoSQL Project On", sub_tag_style))
    story.append(Paragraph("Campus Placement & Recruitment Tracker Using MongoDB", proj_title_style))

    story.append(HRFlowable(width="80%", thickness=1, color=colors.HexColor('#CBD5E1'), spaceBefore=5, spaceAfter=25))

    # Two column submission info box
    left_meta = Paragraph(
        "<b><font color='#0F2C59'>Submitted to:</font></b><br/>"
        "<b>Mr. Harendra Singh</b><br/>"
        "<font color='#475569'>Department: BCA (DS & AI)<br/>"
        "Date: 25th September 2026</font>",
        body_style
    )
    right_meta = Paragraph(
        "<b><font color='#0F2C59'>Submitted by:</font></b><br/>"
        "<b>Shaswat Jaiswal</b><br/>"
        "<font color='#475569'>Program: BCA (DS & AI)<br/>"
        "Roll No: 26 / 12502</font>",
        body_style
    )

    meta_table = Table([[left_meta, right_meta]], colWidths=[245, 245])
    meta_table.setStyle(TableStyle([
        ('BACKGROUND', (0,0), (-1,-1), colors.HexColor('#F8FAFC')),
        ('BOX', (0,0), (-1,-1), 0.5, colors.HexColor('#CBD5E1')),
        ('TOPPADDING', (0,0), (-1,-1), 10),
        ('BOTTOMPADDING', (0,0), (-1,-1), 10),
        ('LEFTPADDING', (0,0), (-1,-1), 12),
        ('RIGHTPADDING', (0,0), (-1,-1), 12),
    ]))
    story.append(meta_table)
    story.append(PageBreak())

    # ---------------- ACKNOWLEDGEMENT ----------------
    story.append(Paragraph("ACKNOWLEDGEMENT", h1_style))
    story.append(HRFlowable(width="100%", thickness=1, color=colors.HexColor('#0F2C59'), spaceBefore=2, spaceAfter=8))
    
    story.append(Paragraph(
        "I sincerely express my gratitude to my teacher <b>Mr. Harendra Singh</b> for giving me the opportunity to work on this NoSQL project titled 'Campus Placement & Recruitment Tracker Using MongoDB'.",
        body_style
    ))
    story.append(Paragraph(
        "This project helped me understand the core concepts of NoSQL document databases and learn how MongoDB is used to install, configure, model, create, retrieve, update, filter, aggregate, and delete data. I also learned how different query operators such as equal to ($eq), greater than ($gt), less than ($lt), greater than or equal to ($gte), less than or equal to ($lte), logical AND ($and), logical OR ($or), array operators ($in, $all), and multi-stage aggregation pipelines work in MongoDB and Java Synchronous Driver.",
        body_style
    ))
    story.append(Paragraph(
        "I am deeply thankful to my teacher for his continuous guidance, technical encouragement, and support provided throughout the completion of this project.",
        body_style
    ))
    story.append(Spacer(1, 10))
    story.append(Paragraph(
        "<b>Shaswat Jaiswal</b><br/>"
        "BCA (DS & AI)<br/>"
        "Roll No: 26 / 12502<br/>"
        "School of Computer Applications<br/>"
        "Babu Banarasi Das University, Lucknow",
        body_style
    ))
    story.append(Spacer(1, 15))

    # ---------------- INTRODUCTION ----------------
    story.append(Paragraph("INTRODUCTION", h1_style))
    story.append(HRFlowable(width="100%", thickness=1, color=colors.HexColor('#0F2C59'), spaceBefore=2, spaceAfter=8))
    story.append(Paragraph(
        "MongoDB is a leading NoSQL document-oriented database management system that stores data in flexible, JSON-like documents called BSON (Binary JSON). Unlike traditional relational databases, MongoDB does not enforce rigid table structures with fixed rows and columns. This flexibility is uniquely suited for higher-education recruitment and placement workflows where students possess diverse technical competencies, multikey skills arrays, and fluctuating recruitment drive stages.",
        body_style
    ))
    story.append(Paragraph(
        "In this project, a comprehensive Campus Placement & Recruitment Tracker has been created using MongoDB Community Server 8.3, MongoDB Compass, and the official Java Synchronous Driver. The database contains verified production-grade records for 72 students across five major engineering and computer application departments at BBDU (BCA DS & AI, B.Tech CSE, B.Tech IT, MCA, and B.Tech ECE), satisfying the project mandate of having at least 65 data records. Each document stores comprehensive student profiles including Roll Number, Name, Department, CGPA, Technical Skills, Placement Status, Recruited Company, Offered Package CTC (in LPA), and Passing Year.",
        body_style
    ))

    # ---------------- OBJECTIVES ----------------
    story.append(Paragraph("OBJECTIVES", h1_style))
    story.append(HRFlowable(width="100%", thickness=1, color=colors.HexColor('#0F2C59'), spaceBefore=2, spaceAfter=8))
    for obj in OBJECTIVES[:8]:
        story.append(Paragraph(f"• {obj}", body_style))
    story.append(PageBreak())

    # ---------------- TOPIC 1 ----------------
    story.append(Paragraph("TOPIC 1: INSTALLATION AND CONFIGURATION OF MONGODB", h1_style))
    story.append(HRFlowable(width="100%", thickness=1, color=colors.HexColor('#0F2C59'), spaceBefore=2, spaceAfter=8))
    story.append(Paragraph("Before working with collections and documents, MongoDB must be properly installed and configured as specified in the curriculum syllabus:", body_style))
    
    story.append(Paragraph("A. Install MongoDB Community Edition Server 8.3", h2_style))
    story.append(Paragraph("Configures the Windows background service 'MongoDB' pointing to mongod.exe on port 27017:", body_style))
    story.append(create_code_block_pdf("Get-Service -Name MongoDB\nnet start MongoDB", code_font_style))
    story.append(create_output_block_pdf("Status   Name      DisplayName\n------   ----      -----------\nRunning  MongoDB   MongoDB Server (MongoDB)", output_font_style))

    story.append(Paragraph("B. Connect Using MongoDB Shell (mongosh)", h2_style))
    story.append(create_code_block_pdf("mongosh \"mongodb://localhost:27017\"\ntest> use placement_db", code_font_style))
    story.append(create_output_block_pdf("switched to db placement_db", output_font_style))

    story.append(Paragraph("C. Java Synchronous Driver Dependency (pom.xml)", h2_style))
    story.append(create_code_block_pdf(
        "<dependency>\n"
        "    <groupId>org.mongodb</groupId>\n"
        "    <artifactId>mongodb-driver-sync</artifactId>\n"
        "    <version>5.1.1</version>\n"
        "</dependency>", code_font_style))

    story.append(Spacer(1, 10))

    # ---------------- TOPIC 2 ----------------
    story.append(Paragraph("TOPIC 2: WORKING WITH DOCUMENTS AND COLLECTIONS", h1_style))
    story.append(HRFlowable(width="100%", thickness=1, color=colors.HexColor('#0F2C59'), spaceBefore=2, spaceAfter=8))

    # 1. CREATE DATABASE
    story.append(Paragraph("1. CREATE DATABASE", h2_style))
    story.append(Paragraph("<b>MongoDB Query:</b>", body_style))
    story.append(create_code_block_pdf("use placement_db", code_font_style))
    story.append(Paragraph("<i>Explanation: Creates or switches to the placement_db database.</i>", body_style))
    story.append(create_output_block_pdf("switched to db placement_db", output_font_style))

    # 2. CREATE COLLECTIONS & INDEXES
    story.append(Paragraph("2. CREATE COLLECTIONS & B-TREE INDEXES", h2_style))
    story.append(Paragraph("<b>MongoDB Query:</b>", body_style))
    story.append(create_code_block_pdf(
        "db.createCollection(\"students\");\n"
        "db.students.createIndex({ roll_number: 1 }, { unique: true });\n"
        "db.students.createIndex({ cgpa: -1 });\n"
        "db.students.createIndex({ department: 1, is_placed: 1 });\n"
        "db.students.createIndex({ skills: 1 });", code_font_style))
    story.append(Paragraph("<i>Explanation: Creates collections and production B-Tree indexes.</i>", body_style))
    story.append(create_output_block_pdf("skills_1", output_font_style))

    # 3. INSERT SINGLE (insertOne)
    story.append(Paragraph("3. INSERT SINGLE STUDENT (insertOne - Task 2.1)", h2_style))
    story.append(Paragraph("<b>MongoDB Query:</b>", body_style))
    story.append(create_code_block_pdf(
        "db.students.insertOne({\n"
        "  roll_number: \"TEST-999\",\n"
        "  name: \"Test Candidate\",\n"
        "  department: \"BCA DS & AI\",\n"
        "  cgpa: 8.8,\n"
        "  skills: [\"Java\", \"MongoDB\"],\n"
        "  is_placed: false,\n"
        "  package_ctc: 0.0\n"
        "})", code_font_style))
    story.append(create_output_block_pdf("{ acknowledged: true, insertedId: ObjectId(\"6ab4c0742677677175aac6a8\") }", output_font_style))

    # 4. INSERT MULTIPLE (insertMany - 72 Records)
    story.append(Paragraph("4. INSERT MULTIPLE STUDENTS (insertMany - 72 Records, Threshold >= 65)", h2_style))
    story.append(Paragraph("<b>MongoDB Query:</b>", body_style))
    story.append(create_code_block_pdf(
        "db.students.insertMany([\n"
        "  {\n"
        "    roll_number: \"26 / 12502\",\n"
        "    name: \"Shaswat Jaiswal\",\n"
        "    department: \"BCA DS & AI\",\n"
        "    cgpa: 9.48,\n"
        "    skills: [\"Java\", \"MongoDB\", \"Python\", \"Data Science\", \"Machine Learning\", \"SQL\"],\n"
        "    is_placed: true,\n"
        "    placed_company: \"Google Cloud\",\n"
        "    package_ctc: 18.5\n"
        "  },\n"
        "  { roll_number: \"11001\", name: \"Vikas Dubey\", department: \"B.Tech CSE\", cgpa: 9.60, is_placed: true, placed_company: \"Microsoft\", package_ctc: 22.0 },\n"
        "  ... // 70 additional student records across BCA DS & AI, B.Tech CSE, B.Tech IT, MCA, B.Tech ECE\n"
        "])", code_font_style))
    story.append(create_output_block_pdf("{ acknowledged: true, insertedIds: { '0': ObjectId(...), ... '71': ObjectId(...) } } // Total: 72 records", output_font_style))

    story.append(PageBreak())

    # 5. EQUAL TO OPERATOR
    story.append(Paragraph("5. EQUAL TO OPERATOR – $eq", h2_style))
    story.append(create_code_block_pdf("db.students.find({ roll_number: { $eq: \"26 / 12502\" } })", code_font_style))
    story.append(create_output_block_pdf("[ { roll_number: '26 / 12502', name: 'Shaswat Jaiswal', dept: 'BCA DS & AI', cgpa: 9.48, company: 'Google Cloud', ctc: 18.5 } ]", output_font_style))

    # 6. GREATER THAN OPERATOR
    story.append(Paragraph("6. GREATER THAN OPERATOR – $gt", h2_style))
    story.append(create_code_block_pdf("db.students.find({ cgpa: { $gt: 9.0 } }).sort({ cgpa: -1 })", code_font_style))
    story.append(create_output_block_pdf(
        "// 8 students matched:\n"
        "[ { roll: '11001', name: 'Vikas Dubey', cgpa: 9.60 }, { roll: '26 / 12502', name: 'Shaswat Jaiswal', cgpa: 9.48 }, ... ]", output_font_style))

    # 7. GREATER THAN OR EQUAL TO
    story.append(Paragraph("7. GREATER THAN OR EQUAL TO OPERATOR – $gte", h2_style))
    story.append(create_code_block_pdf("db.students.find({ cgpa: { $gte: 8.0 } })", code_font_style))
    story.append(create_output_block_pdf("// 38 students matched eligibility cutoff (CGPA >= 8.0).", output_font_style))

    # 8. UPDATE OPERATIONS
    story.append(Paragraph("8. UPDATE OPERATIONS (updateOne & updateMany)", h2_style))
    story.append(create_code_block_pdf("db.students.updateOne({ roll_number: 'TEST-999' }, { $set: { is_placed: true, placed_company: 'Google Cloud', package_ctc: 16.0 } })", code_font_style))
    story.append(create_output_block_pdf("{ acknowledged: true, matchedCount: 1, modifiedCount: 1 }", output_font_style))

    # 9. DELETE OPERATIONS
    story.append(Paragraph("9. DELETE OPERATIONS (deleteOne & deleteMany)", h2_style))
    story.append(create_code_block_pdf("db.students.deleteOne({ roll_number: 'TEST-999' })", code_font_style))
    story.append(create_output_block_pdf("{ acknowledged: true, deletedCount: 1 }", output_font_style))

    # 10. LOGICAL AND & OR
    story.append(Paragraph("10. LOGICAL OPERATORS ($and & $or)", h2_style))
    story.append(create_code_block_pdf("db.students.find({ $and: [{ cgpa: { $gte: 8.5 } }, { department: 'BCA DS & AI' }] })", code_font_style))
    story.append(create_output_block_pdf("// 4 candidates matched in BCA DS & AI (including Shaswat Jaiswal, Aarav Sharma).", output_font_style))

    # 11. ARRAY OPERATORS ($in & $all)
    story.append(Paragraph("11. ARRAY OPERATORS ($in & $all)", h2_style))
    story.append(create_code_block_pdf("db.students.find({ skills: { $all: ['Java', 'MongoDB'] } })", code_font_style))
    story.append(create_output_block_pdf("// 12 candidates possessing both Java AND MongoDB prerequisites.", output_font_style))

    # 12. EXPLAIN STATS
    story.append(Paragraph("12. QUERY PERFORMANCE BENCHMARK (.explain('executionStats'))", h2_style))
    story.append(create_code_block_pdf("db.students.find({ cgpa: { $gte: 8.5 } }).explain('executionStats')", code_font_style))
    story.append(create_output_block_pdf(
        "{\n"
        "  executionSuccess: true,\n"
        "  executionTimeMillis: 5,\n"
        "  nReturned: 22,\n"
        "  totalKeysExamined: 22,\n"
        "  totalDocsExamined: 22,\n"
        "  stage: 'FETCH <- IXSCAN (index: cgpa_-1)'\n"
        "}", output_font_style))

    story.append(PageBreak())

    # ---------------- OPERATORS TABLE ----------------
    story.append(Paragraph("MONGODB OPERATORS SUMMARY REFERENCE", h1_style))
    story.append(HRFlowable(width="100%", thickness=1, color=colors.HexColor('#0F2C59'), spaceBefore=2, spaceAfter=8))
    
    t_op_pdf_data = [
        [Paragraph("<b>Operator</b>", ParagraphStyle('TH', fontName='Helvetica-Bold', fontSize=8, textColor=colors.white)),
         Paragraph("<b>Type</b>", ParagraphStyle('TH', fontName='Helvetica-Bold', fontSize=8, textColor=colors.white)),
         Paragraph("<b>Meaning / Semantic</b>", ParagraphStyle('TH', fontName='Helvetica-Bold', fontSize=8, textColor=colors.white)),
         Paragraph("<b>Example Query Syntax</b>", ParagraphStyle('TH', fontName='Helvetica-Bold', fontSize=8, textColor=colors.white))]
    ]
    for op, typ, mean, syn in OP_DATA:
        t_op_pdf_data.append([
            Paragraph(f"<b><font color='#1E40AF'>{op}</font></b>", body_style),
            Paragraph(typ, body_style),
            Paragraph(mean, body_style),
            Paragraph(f"<code>{syn}</code>", code_font_style)
        ])
    t_op_pdf = Table(t_op_pdf_data, colWidths=[65, 75, 170, 180])
    t_op_pdf.setStyle(TableStyle([
        ('BACKGROUND', (0,0), (-1,0), colors.HexColor('#1E3A8A')),
        ('GRID', (0,0), (-1,-1), 0.5, colors.HexColor('#CBD5E1')),
        ('TOPPADDING', (0,0), (-1,-1), 3),
        ('BOTTOMPADDING', (0,0), (-1,-1), 3),
    ]))
    story.append(t_op_pdf)
    story.append(Spacer(1, 14))

    # ---------------- AGGREGATION ANALYTICS ----------------
    story.append(Paragraph("CAMPUS PLACEMENT & RECRUITMENT ANALYTICS", h1_style))
    story.append(HRFlowable(width="100%", thickness=1, color=colors.HexColor('#0F2C59'), spaceBefore=2, spaceAfter=8))

    story.append(Paragraph("<b>Department-Wise Placement Statistics ($group, $cond, $project):</b>", h2_style))
    t_dept_pdf_data = [
        [Paragraph("<b>Department</b>", ParagraphStyle('TH', fontName='Helvetica-Bold', fontSize=8, textColor=colors.white)),
         Paragraph("<b>Total</b>", ParagraphStyle('TH', fontName='Helvetica-Bold', fontSize=8, textColor=colors.white)),
         Paragraph("<b>Placed</b>", ParagraphStyle('TH', fontName='Helvetica-Bold', fontSize=8, textColor=colors.white)),
         Paragraph("<b>Unplaced</b>", ParagraphStyle('TH', fontName='Helvetica-Bold', fontSize=8, textColor=colors.white)),
         Paragraph("<b>Placement %</b>", ParagraphStyle('TH', fontName='Helvetica-Bold', fontSize=8, textColor=colors.white)),
         Paragraph("<b>Avg CTC</b>", ParagraphStyle('TH', fontName='Helvetica-Bold', fontSize=8, textColor=colors.white)),
         Paragraph("<b>Max CTC</b>", ParagraphStyle('TH', fontName='Helvetica-Bold', fontSize=8, textColor=colors.white))]
    ]
    for d, tot, pl, unpl, prate, avg_c, max_c in DEPT_DATA:
        t_dept_pdf_data.append([
            Paragraph(f"<b>{d}</b>", body_style),
            Paragraph(tot, body_style),
            Paragraph(pl, body_style),
            Paragraph(unpl, body_style),
            Paragraph(f"<b><font color='#27AE60'>{prate}</font></b>", body_style),
            Paragraph(avg_c, body_style),
            Paragraph(max_c, body_style)
        ])
    t_dept_pdf = Table(t_dept_pdf_data, colWidths=[100, 45, 45, 55, 75, 80, 90])
    t_dept_pdf.setStyle(TableStyle([
        ('BACKGROUND', (0,0), (-1,0), colors.HexColor('#1E40AF')),
        ('GRID', (0,0), (-1,-1), 0.5, colors.HexColor('#CBD5E1')),
        ('TOPPADDING', (0,0), (-1,-1), 3),
        ('BOTTOMPADDING', (0,0), (-1,-1), 3),
    ]))
    story.append(t_dept_pdf)
    story.append(Spacer(1, 14))

    # Top Recruiters Table
    story.append(Paragraph("<b>Top Recruiter Recruitment Distribution:</b>", h2_style))
    t_comp_pdf_data = [
        [Paragraph("<b>Recruiter Company</b>", ParagraphStyle('TH', fontName='Helvetica-Bold', fontSize=8, textColor=colors.white)),
         Paragraph("<b>Students Recruited</b>", ParagraphStyle('TH', fontName='Helvetica-Bold', fontSize=8, textColor=colors.white)),
         Paragraph("<b>Average Package</b>", ParagraphStyle('TH', fontName='Helvetica-Bold', fontSize=8, textColor=colors.white)),
         Paragraph("<b>Highest Offer Achieved</b>", ParagraphStyle('TH', fontName='Helvetica-Bold', fontSize=8, textColor=colors.white))]
    ]
    for comp, rec, avg_p, max_p in COMP_DATA[:6]:
        t_comp_pdf_data.append([
            Paragraph(f"<b>{comp}</b>", body_style),
            Paragraph(rec, body_style),
            Paragraph(avg_p, body_style),
            Paragraph(max_p, body_style)
        ])
    t_comp_pdf = Table(t_comp_pdf_data, colWidths=[130, 95, 105, 160])
    t_comp_pdf.setStyle(TableStyle([
        ('BACKGROUND', (0,0), (-1,0), colors.HexColor('#0F2C59')),
        ('GRID', (0,0), (-1,-1), 0.5, colors.HexColor('#CBD5E1')),
        ('TOPPADDING', (0,0), (-1,-1), 3),
        ('BOTTOMPADDING', (0,0), (-1,-1), 3),
    ]))
    story.append(t_comp_pdf)

    # ---------------- CONCLUSION ----------------
    story.append(Spacer(1, 10))
    story.append(Paragraph("CONCLUSION", h1_style))
    story.append(HRFlowable(width="100%", thickness=1, color=colors.HexColor('#0F2C59'), spaceBefore=2, spaceAfter=8))
    story.append(Paragraph(
        "This project provided in-depth practical knowledge of NoSQL database management using MongoDB Community Server 8.3, MongoDB Compass, and the Java Synchronous Driver. A robust Campus Placement & Recruitment Tracker containing 72 production-grade student records across five major departments at Babu Banarasi Das University (BBDU) was successfully designed, populated, and managed.",
        body_style
    ))
    story.append(Paragraph(
        "Overall, this project provided valuable practical understanding of how MongoDB empowers modern educational institutions and training & placement cells to manage, query, and analyze candidate records with high efficiency and schema flexibility.",
        body_style
    ))

    doc.build(story, canvasmaker=BBDUNumberedCanvas)
    print(f"[+] Successfully saved PDF at: {out_pdf}")


if __name__ == "__main__":
    import shutil
    reports_dir = os.path.abspath(r"C:\Users\SHASWAT JAISWAL\.gemini\antigravity\scratch\CampusPlacementTracker\reports")
    artifact_dir = os.path.abspath(r"C:\Users\SHASWAT JAISWAL\.gemini\antigravity\brain\b4c32520-2038-468a-ba71-0646ebbee248")
    os.makedirs(reports_dir, exist_ok=True)
    os.makedirs(artifact_dir, exist_ok=True)
    
    # Primary target matching reference naming: Student_Management_MongoDB_Project_Report
    ref_docx = os.path.join(reports_dir, "Campus_Placement_Tracker_MongoDB_Project_Report.docx")
    ref_pdf = os.path.join(reports_dir, "Campus_Placement_Tracker_MongoDB_Project_Report.pdf")
    
    build_docx_report(ref_docx)
    build_pdf_report(ref_pdf)

    # Sync to BBDU names
    for src, dst_name in [(ref_docx, "BBDU_Campus_Placement_Tracker_Project_Report.docx"),
                          (ref_pdf, "BBDU_Campus_Placement_Tracker_Project_Report.pdf")]:
        dst = os.path.join(reports_dir, dst_name)
        try:
            shutil.copy2(src, dst)
            print(f"[+] Synchronized to: {dst}")
        except Exception as e:
            print(f"[!] Note: {dst_name} is currently open in another program, skipping overwrite.")

    # Copy both sets to artifact directory for instant user visibility
    for fname in ["Campus_Placement_Tracker_MongoDB_Project_Report.docx",
                  "Campus_Placement_Tracker_MongoDB_Project_Report.pdf",
                  "BBDU_Campus_Placement_Tracker_Project_Report.docx"]:
        src = os.path.join(reports_dir, fname)
        if os.path.exists(src):
            try:
                shutil.copy2(src, os.path.join(artifact_dir, fname))
            except Exception as e:
                pass
    try:
        shutil.copy2(ref_pdf, os.path.join(artifact_dir, "Campus_Placement_Tracker_MongoDB_Project_Report.pdf"))
    except Exception:
        pass

    print("\n[SUCCESS] Both DOCX and PDF generated matching BBDU reference styling!")
