package com.feemanager.student;
import com.feemanager.DBConnection;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

/**
 * StudentFrame
 *
 * Ye screen students ki information manage karne ke liye hai.
 *
 * Features:
 * 1. Add Student
 * 2. Update Student
 * 3. Delete Student
 * 4. Search Student
 * 5. Student List
 */
public class StudentFrame extends JFrame {

    // -------------------------------
    // Form Fields
    // -------------------------------

    private JTextField studentIdField;
    private JTextField nameField;
    private JTextField fatherNameField;
    private JTextField mobileField;
    private JTextField emailField;
    private JTextField addressField;

    private JComboBox<String> genderComboBox;
    private JComboBox<CourseItem> courseComboBox;

    // -------------------------------
    // Buttons
    // -------------------------------

    private JButton addButton;
    private JButton updateButton;
    private JButton deleteButton;
    private JButton clearButton;
    private JButton searchButton;
        private JButton backButton;
   

    // -------------------------------
    // Search Field
    // -------------------------------

    private JTextField searchField;

    // -------------------------------
    // Table
    // -------------------------------

    private JTable studentTable;
    private DefaultTableModel tableModel;


    /**
     * Constructor
     */
    public StudentFrame() {

        setTitle("Fee Management System - Student Management");
        setSize(1100, 700);

        setDefaultCloseOperation(
                JFrame.DISPOSE_ON_CLOSE
        );

        setLocationRelativeTo(null);

        createUI();

        // Database se courses load karna
        loadCourses();

        // Database se students load karna
        loadStudents();
    }


    /**
     * Complete Student Management UI create karta hai.
     */
    private void createUI() {

        JPanel mainPanel = new JPanel(
                new BorderLayout(15, 15)
        );

        mainPanel.setBorder(
                BorderFactory.createEmptyBorder(
                        15, 15, 15, 15
                )
        );


        // =====================================================
        // TITLE
        // =====================================================

        JLabel titleLabel = new JLabel(
                "STUDENT MANAGEMENT"
        );

        titleLabel.setFont(
                new Font("Arial", Font.BOLD, 24)
        );

        mainPanel.add(
                titleLabel,
                BorderLayout.NORTH
        );


        // =====================================================
        // FORM PANEL
        // =====================================================

        JPanel formPanel = new JPanel(
                new GridLayout(4, 4, 10, 10)
        );

        formPanel.setBorder(
                BorderFactory.createTitledBorder(
                        "Student Information"
                )
        );


        // Student ID
        formPanel.add(
                new JLabel("Student ID:")
        );

        studentIdField = new JTextField();
        formPanel.add(studentIdField);


        // Student Name
        formPanel.add(
                new JLabel("Student Name:")
        );

        nameField = new JTextField();
        formPanel.add(nameField);


        // Father's Name
        formPanel.add(
                new JLabel("Father's Name:")
        );

        fatherNameField = new JTextField();
        formPanel.add(fatherNameField);


        // Mobile
        formPanel.add(
                new JLabel("Mobile:")
        );

        mobileField = new JTextField();
        formPanel.add(mobileField);


        // Email
        formPanel.add(
                new JLabel("Email:")
        );

        emailField = new JTextField();
        formPanel.add(emailField);


        // Gender
        formPanel.add(
                new JLabel("Gender:")
        );

        genderComboBox = new JComboBox<>(
                new String[]{
                        "Male",
                        "Female",
                        "Other"
                }
        );

        formPanel.add(genderComboBox);


        // Course
        formPanel.add(
                new JLabel("Course:")
        );

        courseComboBox = new JComboBox<>();
        formPanel.add(courseComboBox);


        // Address
        formPanel.add(
                new JLabel("Address:")
        );

        addressField = new JTextField();
        formPanel.add(addressField);


        mainPanel.add(
                formPanel,
                BorderLayout.CENTER
        );


        // =====================================================
        // BUTTON PANEL
        // =====================================================

        JPanel buttonPanel = new JPanel(
                new FlowLayout(
                        FlowLayout.CENTER,
                        10,
                        10
                )
        );

        addButton = new JButton("Add");
        updateButton = new JButton("Update");
        deleteButton = new JButton("Delete");
        clearButton = new JButton("Clear");
        backButton = new JButton("Back");

        buttonPanel.add(addButton);
        buttonPanel.add(updateButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(clearButton);
        buttonPanel.add(backButton);


        // =====================================================
        // SEARCH PANEL
        // =====================================================

        JPanel searchPanel = new JPanel(
                new FlowLayout(
                        FlowLayout.LEFT
                )
        );

        searchPanel.add(
                new JLabel("Search Student:")
        );

        searchField = new JTextField(20);

        searchPanel.add(searchField);

        searchButton = new JButton("Search");

        searchPanel.add(searchButton);


        // Buttons + Search ko combine karna
        JPanel bottomPanel = new JPanel(
                new BorderLayout()
        );

        bottomPanel.add(
                buttonPanel,
                BorderLayout.NORTH
        );

        bottomPanel.add(
                searchPanel,
                BorderLayout.SOUTH
        );


        mainPanel.add(
                bottomPanel,
                BorderLayout.SOUTH
        );


        // =====================================================
        // STUDENT TABLE
        // =====================================================

        String[] columns = {
                "ID",
                "Student ID",
                "Name",
                "Father Name",
                "Mobile",
                "Email",
                "Gender",
                "Course",
                "Address"
        };


        tableModel = new DefaultTableModel(
                columns,
                0
        ) {

            /*
             * Table ko directly edit hone se rok rahe hain.
             */
            @Override
            public boolean isCellEditable(
                    int row,
                    int column
            ) {
                return false;
            }
        };


        studentTable = new JTable(tableModel);

        studentTable.setSelectionMode(
                ListSelectionModel.SINGLE_SELECTION
        );


        /*
         * Table ko scroll pane mein rakhenge,
         * taaki zyada students hone par scroll kar sakein.
         */
        JScrollPane tableScrollPane =
                new JScrollPane(studentTable);


        /*
         * Table ko center mein add karna.
         */
        mainPanel.add(
                tableScrollPane,
                BorderLayout.EAST
        );


        /*
         * Better layout ke liye main content ko
         * split pane mein rakhenge.
         */
        JPanel contentPanel = new JPanel(
                new BorderLayout(10, 10)
        );

        contentPanel.add(
                formPanel,
                BorderLayout.NORTH
        );

        contentPanel.add(
                tableScrollPane,
                BorderLayout.CENTER
        );

        contentPanel.add(
                bottomPanel,
                BorderLayout.SOUTH
        );

        mainPanel.remove(formPanel);
        mainPanel.remove(tableScrollPane);
        mainPanel.remove(bottomPanel);

        mainPanel.add(
                contentPanel,
                BorderLayout.CENTER
        );


        // =====================================================
        // ACTION LISTENERS
        // =====================================================

        addButton.addActionListener(
                e -> addStudent()
        );

        updateButton.addActionListener(
                e -> updateStudent()
        );

        deleteButton.addActionListener(
                e -> deleteStudent()
        );

        clearButton.addActionListener(
                e -> clearFields()
        );

        searchButton.addActionListener(
                e -> searchStudent()
        );
        
        backButton.addActionListener(
                e -> {
                    // Back button action
                    dispose(); // Close the current frame
                }
        );

        
       

        /*
         * Table ki row par click karne par
         * us student ki information form mein aa jayegi.
         */
        studentTable.getSelectionModel()
                .addListSelectionListener(e -> {

                    if (!e.getValueIsAdjusting()) {
                        fillFormFromTable();
                    }
                });


        add(mainPanel);
    }


    // =========================================================
    // ADD STUDENT
    // =========================================================

    /**
     * New student database mein add karta hai.
     */
    private void addStudent() {

        if (!validateFields()) {
            return;
        }


        CourseItem course =
                (CourseItem) courseComboBox.getSelectedItem();


        String sql =
                "INSERT INTO students " +
                "(student_id, name, father_name, mobile, " +
                "email, gender, course_id, address) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";


        try (
                Connection connection =
                        DBConnection.getConnection();

                PreparedStatement statement =
                        connection.prepareStatement(sql)
        ) {

            statement.setString(
                    1,
                    studentIdField.getText().trim()
            );

            statement.setString(
                    2,
                    nameField.getText().trim()
            );

            statement.setString(
                    3,
                    fatherNameField.getText().trim()
            );

            statement.setString(
                    4,
                    mobileField.getText().trim()
            );

            statement.setString(
                    5,
                    emailField.getText().trim()
            );

            statement.setString(
                    6,
                    genderComboBox.getSelectedItem().toString()
            );

            statement.setInt(
                    7,
                    course.id
            );

            statement.setString(
                    8,
                    addressField.getText().trim()
            );


            statement.executeUpdate();


            JOptionPane.showMessageDialog(
                    this,
                    "Student successfully added!",
                    "Success",
                    JOptionPane.INFORMATION_MESSAGE
            );


            clearFields();
            loadStudents();


        } catch (SQLException e) {

            showDatabaseError(e);
        }
    }


    // =========================================================
    // UPDATE STUDENT
    // =========================================================

    /**
     * Selected student ki information update karta hai.
     */
    private void updateStudent() {

        if (studentTable.getSelectedRow() == -1) {

            JOptionPane.showMessageDialog(
                    this,
                    "Please select a student from the table first.",
                    "Warning",
                    JOptionPane.WARNING_MESSAGE
            );

            return;
        }


        if (!validateFields()) {
            return;
        }


        CourseItem course =
                (CourseItem) courseComboBox.getSelectedItem();


        int databaseId =
                Integer.parseInt(
                        tableModel.getValueAt(
                                studentTable.getSelectedRow(),
                                0
                        ).toString()
                );


        String sql =
                "UPDATE students SET " +
                "student_id = ?, " +
                "name = ?, " +
                "father_name = ?, " +
                "mobile = ?, " +
                "email = ?, " +
                "gender = ?, " +
                "course_id = ?, " +
                "address = ? " +
                "WHERE id = ?";


        try (
                Connection connection =
                        DBConnection.getConnection();

                PreparedStatement statement =
                        connection.prepareStatement(sql)
        ) {

            statement.setString(
                    1,
                    studentIdField.getText().trim()
            );

            statement.setString(
                    2,
                    nameField.getText().trim()
            );

            statement.setString(
                    3,
                    fatherNameField.getText().trim()
            );

            statement.setString(
                    4,
                    mobileField.getText().trim()
            );

            statement.setString(
                    5,
                    emailField.getText().trim()
            );

            statement.setString(
                    6,
                    genderComboBox.getSelectedItem().toString()
            );

            statement.setInt(
                    7,
                    course.id
            );

            statement.setString(
                    8,
                    addressField.getText().trim()
            );

            statement.setInt(
                    9,
                    databaseId
            );


            statement.executeUpdate();


            JOptionPane.showMessageDialog(
                    this,
                    "Student successfully updated!",
                    "Success",
                    JOptionPane.INFORMATION_MESSAGE
            );


            clearFields();
            loadStudents();


        } catch (SQLException e) {

            showDatabaseError(e);
        }
    }


    // =========================================================
    // DELETE STUDENT
    // =========================================================

    /**
     * Selected student ko database se delete karta hai.
     */
    private void deleteStudent() {

        int selectedRow =
                studentTable.getSelectedRow();


        if (selectedRow == -1) {

            JOptionPane.showMessageDialog(
                    this,
                    "Please select a student to delete.",
                    "Warning",
                    JOptionPane.WARNING_MESSAGE
            );

            return;
        }


        int choice =
                JOptionPane.showConfirmDialog(
                        this,
                        "Are you sure you want to delete the selected student?",
                        "Confirm Delete",
                        JOptionPane.YES_NO_OPTION
                );


        if (choice != JOptionPane.YES_OPTION) {
            return;
        }


        int databaseId =
                Integer.parseInt(
                        tableModel.getValueAt(
                                selectedRow,
                                0
                        ).toString()
                );


        String sql =
                "DELETE FROM students WHERE id = ?";


        try (
                Connection connection =
                        DBConnection.getConnection();

                PreparedStatement statement =
                        connection.prepareStatement(sql)
        ) {

            statement.setInt(
                    1,
                    databaseId
            );


            statement.executeUpdate();


            JOptionPane.showMessageDialog(
                    this,
                    "Student successfully deleted!",
                    "Success",
                    JOptionPane.INFORMATION_MESSAGE
            );


            clearFields();
            loadStudents();


        } catch (SQLException e) {

            showDatabaseError(e);
        }
    }


    // =========================================================
    // LOAD STUDENTS
    // =========================================================

    /**
     * Database se saare students table mein load karta hai.
     */
    private void loadStudents() {

        tableModel.setRowCount(0);


        String sql =
                "SELECT s.id, s.student_id, s.name, " +
                "s.father_name, s.mobile, s.email, " +
                "s.gender, c.course_name, s.address " +
                "FROM students s " +
                "LEFT JOIN courses c ON s.course_id = c.id " +
                "ORDER BY s.id DESC";


        try (
                Connection connection =
                        DBConnection.getConnection();

                PreparedStatement statement =
                        connection.prepareStatement(sql);

                ResultSet resultSet =
                        statement.executeQuery()
        ) {


            while (resultSet.next()) {

                tableModel.addRow(
                        new Object[]{
                                resultSet.getInt("id"),
                                resultSet.getString("student_id"),
                                resultSet.getString("name"),
                                resultSet.getString("father_name"),
                                resultSet.getString("mobile"),
                                resultSet.getString("email"),
                                resultSet.getString("gender"),
                                resultSet.getString("course_name"),
                                resultSet.getString("address")
                        }
                );
            }


        } catch (SQLException e) {

            showDatabaseError(e);
        }
    }


    // =========================================================
    // LOAD COURSES
    // =========================================================

    /**
     * Courses table se courses dropdown mein load karta hai.
     */
    private void loadCourses() {

        courseComboBox.removeAllItems();


        String sql =
                "SELECT id, course_name " +
                "FROM courses " +
                "ORDER BY course_name";


        try (
                Connection connection =
                        DBConnection.getConnection();

                PreparedStatement statement =
                        connection.prepareStatement(sql);

                ResultSet resultSet =
                        statement.executeQuery()
        ) {


            while (resultSet.next()) {

                courseComboBox.addItem(
                        new CourseItem(
                                resultSet.getInt("id"),
                                resultSet.getString("course_name")
                        )
                );
            }


        } catch (SQLException e) {

            showDatabaseError(e);
        }
    }


    // =========================================================
    // SEARCH STUDENT
    // =========================================================

    /**
     * Student ID, name ya mobile ke basis par search karta hai.
     */
    private void searchStudent() {

        String searchText =
                searchField.getText().trim();


        if (searchText.isEmpty()) {

            loadStudents();
            return;
        }


        tableModel.setRowCount(0);


        String sql =
                "SELECT s.id, s.student_id, s.name, " +
                "s.father_name, s.mobile, s.email, " +
                "s.gender, c.course_name, s.address " +
                "FROM students s " +
                "LEFT JOIN courses c ON s.course_id = c.id " +
                "WHERE s.student_id LIKE ? " +
                "OR s.name LIKE ? " +
                "OR s.mobile LIKE ? " +
                "ORDER BY s.id DESC";


        try (
                Connection connection =
                        DBConnection.getConnection();

                PreparedStatement statement =
                        connection.prepareStatement(sql)
        ) {


            String searchPattern =
                    "%" + searchText + "%";


            statement.setString(
                    1,
                    searchPattern
            );

            statement.setString(
                    2,
                    searchPattern
            );

            statement.setString(
                    3,
                    searchPattern
            );


            ResultSet resultSet =
                    statement.executeQuery();


            while (resultSet.next()) {

                tableModel.addRow(
                        new Object[]{
                                resultSet.getInt("id"),
                                resultSet.getString("student_id"),
                                resultSet.getString("name"),
                                resultSet.getString("father_name"),
                                resultSet.getString("mobile"),
                                resultSet.getString("email"),
                                resultSet.getString("gender"),
                                resultSet.getString("course_name"),
                                resultSet.getString("address")
                        }
                );
            }


        } catch (SQLException e) {

            showDatabaseError(e);
        }
    }


    // =========================================================
    // FILL FORM
    // =========================================================

    /**
     * Table mein selected student ka data
     * form fields mein fill karta hai.
     */
    private void fillFormFromTable() {

        int row =
                studentTable.getSelectedRow();


        if (row == -1) {
            return;
        }


        studentIdField.setText(
                tableModel.getValueAt(
                        row,
                        1
                ).toString()
        );


        nameField.setText(
                tableModel.getValueAt(
                        row,
                        2
                ).toString()
        );


        fatherNameField.setText(
                tableModel.getValueAt(
                        row,
                        3
                ).toString()
        );


        mobileField.setText(
                tableModel.getValueAt(
                        row,
                        4
                ).toString()
        );


        emailField.setText(
                tableModel.getValueAt(
                        row,
                        5
                ).toString()
        );


        genderComboBox.setSelectedItem(
                tableModel.getValueAt(
                        row,
                        6
                ).toString()
        );


        String courseName =
                tableModel.getValueAt(
                        row,
                        7
                ).toString();


        /*
         * Course dropdown mein matching course select karna.
         */
        for (int i = 0;
             i < courseComboBox.getItemCount();
             i++) {

            CourseItem item =
                    courseComboBox.getItemAt(i);

            if (item.name.equals(courseName)) {

                courseComboBox.setSelectedIndex(i);
                break;
            }
        }


        addressField.setText(
                tableModel.getValueAt(
                        row,
                        8
                ).toString()
        );
    }


    // =========================================================
    // VALIDATION
    // =========================================================

    /**
     * Required fields check karta hai.
     */
    private boolean validateFields() {

        if (
                studentIdField.getText().trim().isEmpty()
                        ||
                nameField.getText().trim().isEmpty()
        ) {

            JOptionPane.showMessageDialog(
                    this,
                    "Student ID and Name are required.",
                    "Validation Error",
                    JOptionPane.WARNING_MESSAGE
            );

            return false;
        }


        if (courseComboBox.getSelectedItem() == null) {

            JOptionPane.showMessageDialog(
                    this,
                    "Please select a course.",
                    "Validation Error",
                    JOptionPane.WARNING_MESSAGE
            );

            return false;
        }


        return true;
    }


    // =========================================================
    // CLEAR FORM
    // =========================================================

    /**
     * Saare form fields clear karta hai.
     */
    private void clearFields() {

        studentIdField.setText("");
        nameField.setText("");
        fatherNameField.setText("");
        mobileField.setText("");
        emailField.setText("");
        addressField.setText("");

        genderComboBox.setSelectedIndex(0);

        if (courseComboBox.getItemCount() > 0) {
            courseComboBox.setSelectedIndex(0);
        }

        searchField.setText("");

        studentTable.clearSelection();

        studentIdField.requestFocus();
    }


    // =========================================================
    // DATABASE ERROR
    // =========================================================

    /**
     * Database error ko user-friendly way mein show karta hai.
     */
    private void showDatabaseError(SQLException e) {

        JOptionPane.showMessageDialog(
                this,
                "Database Error:\n" + e.getMessage(),
                "Database Error",
                JOptionPane.ERROR_MESSAGE
        );
    }


    // =========================================================
    // COURSE ITEM CLASS
    // =========================================================

    /**
     * Course ka ID aur name dropdown mein store karne ke liye
     * ye small helper class use kar rahe hain.
     */
    private static class CourseItem {

        private final int id;
        private final String name;


        public CourseItem(
                int id,
                String name
        ) {

            this.id = id;
            this.name = name;
        }


        /*
         * ComboBox mein course ka name show hoga.
         */
        @Override
        public String toString() {

            return name;
        }
    }


    // =========================================================
    // TEST MAIN
    // =========================================================

    /**
     * Sirf StudentFrame ko separately test karne ke liye.
     *
     * Main application mein iski zarurat nahi hogi.
     */
    public static void main(String[] args) {

        SwingUtilities.invokeLater(() -> {

            StudentFrame frame =
                    new StudentFrame();

            frame.setVisible(true);
        });
    }
}