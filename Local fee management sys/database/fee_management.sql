-- =========================================================
-- FEE MANAGEMENT SYSTEM
-- Database Setup
-- =========================================================

-- Database create kar rahe hain
CREATE DATABASE IF NOT EXISTS fee_management;

-- Apne database ko select kar rahe hain
USE fee_management;


-- =========================================================
-- 1. USERS TABLE
-- Admin/Login information yahan store hogi
-- =========================================================

CREATE TABLE IF NOT EXISTS users (
    id INT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    full_name VARCHAR(100) NOT NULL
);


-- Default admin account
-- Login:
-- Username: admin
-- Password: admin123

INSERT INTO users (username, password, full_name)
VALUES ('admin', 'admin123', 'System Administrator');


-- =========================================================
-- 2. COURSES TABLE
-- Student ke course ki information yahan store hogi
-- =========================================================

CREATE TABLE IF NOT EXISTS courses (
    id INT PRIMARY KEY AUTO_INCREMENT,
    course_name VARCHAR(100) NOT NULL,
    duration VARCHAR(50),
    total_fee DECIMAL(10, 2) NOT NULL
);


-- Sample courses
INSERT INTO courses (course_name, duration, total_fee)
VALUES
('BCA', '3 Years', 90000.00),
('BBA', '3 Years', 85000.00),
('MCA', '2 Years', 120000.00);


-- =========================================================
-- 3. STUDENTS TABLE
-- Student ki complete information yahan store hogi
-- =========================================================

CREATE TABLE IF NOT EXISTS students (
    id INT PRIMARY KEY AUTO_INCREMENT,
    student_id VARCHAR(30) NOT NULL UNIQUE,
    name VARCHAR(100) NOT NULL,
    father_name VARCHAR(100),
    mobile VARCHAR(15),
    email VARCHAR(100),
    gender VARCHAR(20),
    course_id INT,
    address VARCHAR(255),

    -- Course table ke saath relation
    FOREIGN KEY (course_id)
        REFERENCES courses(id)
        ON DELETE SET NULL
        ON UPDATE CASCADE
);


-- =========================================================
-- 4. FEES TABLE
-- Student ki fee/payment information yahan store hogi
-- =========================================================

CREATE TABLE IF NOT EXISTS fees (
    id INT PRIMARY KEY AUTO_INCREMENT,

    -- Student ka ID
    student_id INT NOT NULL,

    -- Fee information
    total_fee DECIMAL(10, 2) NOT NULL,
    paid_amount DECIMAL(10, 2) NOT NULL DEFAULT 0.00,
    remaining_amount DECIMAL(10, 2) NOT NULL DEFAULT 0.00,

    -- Payment details
    payment_date DATE NOT NULL,
    payment_mode VARCHAR(30) DEFAULT 'Cash',
    remarks VARCHAR(255),

    -- Student table ke saath relation
    FOREIGN KEY (student_id)
        REFERENCES students(id)
        ON DELETE CASCADE
        ON UPDATE CASCADE
);


-- =========================================================
-- 5. SAMPLE STUDENT
-- Testing ke liye ek sample student
-- =========================================================

INSERT INTO students
(student_id, name, father_name, mobile, email, gender, course_id, address)
VALUES
('STU001',
 'Rahul Kumar',
 'Ramesh Kumar',
 '9876543210',
 'rahul@gmail.com',
 'Male',
 1,
 'Kanpur, Uttar Pradesh');


-- =========================================================
-- 6. SAMPLE FEE RECORD
-- Testing ke liye sample payment
-- =========================================================

INSERT INTO fees
(student_id, total_fee, paid_amount, remaining_amount,
 payment_date, payment_mode, remarks)
VALUES
(1, 90000.00, 30000.00, 60000.00,
 CURDATE(), 'Cash', 'First installment');


-- =========================================================
-- DATABASE SETUP COMPLETE
-- =========================================================

-- Tables check karne ke liye:
SHOW TABLES;

-- Data check karne ke liye:
SELECT * FROM users;
SELECT * FROM courses;
SELECT * FROM students;
SELECT * FROM fees;