# Fee Management System

## 📌 Project Overview

Fee Management System ek Java-based desktop application hai jo
students ki fee ko manage aur track karne ke liye banaya gaya hai.

Is application ke through admin:

- Students add/update/delete kar sakta hai
- Courses manage kar sakta hai
- Student ki fee collect kar sakta hai
- Payment history dekh sakta hai
- Pending fees check kar sakta hai
- Fee receipt generate aur print kar sakta hai
- System users manage kar sakta hai

---

## 🛠️ Technologies Used

- Java 17
- Java Swing
- MySQL
- JDBC
- Maven
- VS Code

---

## 📂 Project Structure

```text
FeeManagementSystem/
│
├── pom.xml
│
├── README.md
│
├── database/
│   └── fee_management.sql
│
└── src/
    └── main/
        └── java/
            └── com/
                └── feemanager/
                    │
                    ├── Main.java
                    ├── DBConnection.java
                    │
                    ├── login/
                    │   └── LoginFrame.java
                    │
                    ├── dashboard/
                    │   └── DashboardFrame.java
                    │
                    ├── student/
                    │   └── StudentFrame.java
                    │
                    ├── course/
                    │   └── CourseFrame.java
                    │
                    ├── fee/
                    │   └── FeeFrame.java
                    │
                    ├── payment/
                    │   └── PaymentHistoryFrame.java
                    │
                    ├── pendingfee/
                    │   └── PendingFeeFrame.java
                    │
                    ├── receipt/
                    │   └── ReceiptFrame.java
                    │
                    └── user/
                        └── UserManagementFrame.java