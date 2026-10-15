package com.feemanager.course;

import com.feemanager.DBConnection;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

/**
 * CourseFrame
 *
 * Course Management ke liye GUI.
 *
 * Features:
 * 1. Course Add
 * 2. Course Update
 * 3. Course Delete
 * 4. Course Search
 * 5. Course List
 */
public class CourseFrame extends JFrame {

    // =========================
    // Form Fields
    // =========================

    private JTextField courseNameField;
    private JTextField durationField;
    private JTextField totalFeeField;
    private JTextField searchField;

    // =========================
    // Buttons
    // =========================

    private JButton addButton;
    private JButton updateButton;
    private JButton deleteButton;
    private JButton clearButton;
    private JButton searchButton;
    private JButton refreshButton;

    // =========================
    // Table
    // =========================

    private JTable courseTable;
    private DefaultTableModel tableModel;

    // Selected database ID
    private int selectedCourseId = -1;

    /**
     * Constructor
     */
    public CourseFrame() {

        setTitle("Fee Management System - Course Management");

        setSize(900, 600);

        setDefaultCloseOperation(
                JFrame.DISPOSE_ON_CLOSE
        );

        setLocationRelativeTo(null);

        createUI();

        // Screen open hote hi courses load karo
        loadCourses();
    }

    /**
     * Complete UI create karta hai.
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

        JLabel titleLabel =
                new JLabel("COURSE MANAGEMENT");

        titleLabel.setFont(
                new Font(
                        "Arial",
                        Font.BOLD,
                        24
                )
        );

        mainPanel.add(
                titleLabel,
                BorderLayout.NORTH
        );

        // =====================================================
        // FORM PANEL
        // =====================================================

        JPanel formPanel = new JPanel(
                new GridBagLayout()
        );

        formPanel.setBorder(
                BorderFactory.createTitledBorder(
                        "Course Details"
                )
        );

        GridBagConstraints gbc =
                new GridBagConstraints();

        gbc.insets =
                new Insets(6, 6, 6, 6);

        gbc.fill =
                GridBagConstraints.HORIZONTAL;

        // Course Name
        gbc.gridx = 0;
        gbc.gridy = 0;

        formPanel.add(
                new JLabel("Course Name:"),
                gbc
        );

        courseNameField =
                new JTextField(20);

        gbc.gridx = 1;
        gbc.weightx = 1;

        formPanel.add(
                courseNameField,
                gbc
        );

        // Duration
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 0;

        formPanel.add(
                new JLabel("Duration:"),
                gbc
        );

        durationField =
                new JTextField(20);

        gbc.gridx = 1;
        gbc.weightx = 1;

        formPanel.add(
                durationField,
                gbc
        );

        // Total Fee
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.weightx = 0;

        formPanel.add(
                new JLabel("Total Fee:"),
                gbc
        );

        totalFeeField =
                new JTextField(20);

        gbc.gridx = 1;
        gbc.weightx = 1;

        formPanel.add(
                totalFeeField,
                gbc
        );

        // =====================================================
        // BUTTON PANEL
        // =====================================================

        JPanel buttonPanel = new JPanel(
                new FlowLayout(
                        FlowLayout.LEFT
                )
        );

        addButton =
                new JButton("Add");

        updateButton =
                new JButton("Update");

        deleteButton =
                new JButton("Delete");

        clearButton =
                new JButton("Clear");

        buttonPanel.add(addButton);
        buttonPanel.add(updateButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(clearButton);

        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;

        formPanel.add(
                buttonPanel,
                gbc
        );

        // =====================================================
        // SEARCH PANEL
        // =====================================================

        JPanel searchPanel = new JPanel(
                new FlowLayout(
                        FlowLayout.LEFT
                )
        );

        searchPanel.add(
                new JLabel("Search Course:")
        );

        searchField =
                new JTextField(20);

        searchPanel.add(searchField);

        searchButton =
                new JButton("Search");

        refreshButton =
                new JButton("Refresh");

        searchPanel.add(searchButton);
        searchPanel.add(refreshButton);

        // =====================================================
        // TABLE
        // =====================================================

        String[] columns = {
                "ID",
                "Course Name",
                "Duration",
                "Total Fee"
        };

        tableModel =
                new DefaultTableModel(
                        columns,
                        0
                ) {

                    // Table ko directly edit nahi karne denge
                    @Override
                    public boolean isCellEditable(
                            int row,
                            int column
                    ) {
                        return false;
                    }
                };

        courseTable =
                new JTable(tableModel);

        JScrollPane scrollPane =
                new JScrollPane(courseTable);

        scrollPane.setBorder(
                BorderFactory.createTitledBorder(
                        "Course List"
                )
        );

        // =====================================================
        // TOP CONTENT
        // =====================================================

        JPanel topPanel = new JPanel(
                new BorderLayout(10, 10)
        );

        topPanel.add(
                formPanel,
                BorderLayout.CENTER
        );

        topPanel.add(
                searchPanel,
                BorderLayout.SOUTH
        );

        mainPanel.add(
                topPanel,
                BorderLayout.NORTH
        );

        mainPanel.add(
                scrollPane,
                BorderLayout.CENTER
        );

        add(mainPanel);

        // =====================================================
        // ACTION LISTENERS
        // =====================================================

        addButton.addActionListener(
                e -> addCourse()
        );

        updateButton.addActionListener(
                e -> updateCourse()
        );

        deleteButton.addActionListener(
                e -> deleteCourse()
        );

        clearButton.addActionListener(
                e -> clearFields()
        );

        searchButton.addActionListener(
                e -> searchCourse()
        );

        refreshButton.addActionListener(
                e -> {

                    searchField.setText("");

                    loadCourses();
                }
        );

        // Table row select hone par form fill karo
        courseTable.getSelectionModel()
                .addListSelectionListener(
                        e -> fillFormFromTable()
                );

        // Enter press karne par search
        searchField.addActionListener(
                e -> searchCourse()
        );
    }

    // =========================================================
    // ADD COURSE
    // =========================================================

    /**
     * New course database mein add karta hai.
     */
    private void addCourse() {

        String courseName =
                courseNameField.getText().trim();

        String duration =
                durationField.getText().trim();

        String totalFeeText =
                totalFeeField.getText().trim();

        // Basic validation
        if (courseName.isEmpty()
                || duration.isEmpty()
                || totalFeeText.isEmpty()) {

            JOptionPane.showMessageDialog(
                    this,
                    "Please fill all fields.",
                    "Validation Error",
                    JOptionPane.WARNING_MESSAGE
            );

            return;
        }

        double totalFee;

        try {

            totalFee =
                    Double.parseDouble(
                            totalFeeText
                    );

            if (totalFee <= 0) {

                throw new NumberFormatException();
            }

        } catch (NumberFormatException e) {

            JOptionPane.showMessageDialog(
                    this,
                    "Total Fee must be a valid number.",
                    "Validation Error",
                    JOptionPane.WARNING_MESSAGE
            );

            return;
        }

        String sql =
                "INSERT INTO courses " +
                "(course_name, duration, total_fee) " +
                "VALUES (?, ?, ?)";

        try (
                Connection connection =
                        DBConnection.getConnection();

                PreparedStatement statement =
                        connection.prepareStatement(sql)
        ) {

            statement.setString(
                    1,
                    courseName
            );

            statement.setString(
                    2,
                    duration
            );

            statement.setDouble(
                    3,
                    totalFee
            );

            statement.executeUpdate();

            JOptionPane.showMessageDialog(
                    this,
                    "Course successfully added.",
                    "Success",
                    JOptionPane.INFORMATION_MESSAGE
            );

            clearFields();

            loadCourses();

        } catch (SQLException e) {

            // Duplicate course name ke case mein
            if (e.getMessage()
                    .toLowerCase()
                    .contains("duplicate")) {

                JOptionPane.showMessageDialog(
                        this,
                        "This course already exists.",
                        "Error",
                        JOptionPane.ERROR_MESSAGE
                );

            } else {

                showDatabaseError(e);
            }
        }
    }

    // =========================================================
    // UPDATE COURSE
    // =========================================================

    /**
     * Selected course ko update karta hai.
     */
    private void updateCourse() {

        if (selectedCourseId == -1) {

            JOptionPane.showMessageDialog(
                    this,
                    "Please select a course from the table.",
                    "Warning",
                    JOptionPane.WARNING_MESSAGE
            );

            return;
        }

        String courseName =
                courseNameField.getText().trim();

        String duration =
                durationField.getText().trim();

        String totalFeeText =
                totalFeeField.getText().trim();

        if (courseName.isEmpty()
                || duration.isEmpty()
                || totalFeeText.isEmpty()) {

            JOptionPane.showMessageDialog(
                    this,
                    "Please fill all fields.",
                    "Validation Error",
                    JOptionPane.WARNING_MESSAGE
            );

            return;
        }

        double totalFee;

        try {

            totalFee =
                    Double.parseDouble(
                            totalFeeText
                    );

            if (totalFee <= 0) {

                throw new NumberFormatException();
            }

        } catch (NumberFormatException e) {

            JOptionPane.showMessageDialog(
                    this,
                    "Total Fee must be a valid number.",
                    "Validation Error",
                    JOptionPane.WARNING_MESSAGE
            );

            return;
        }

        String sql =
                "UPDATE courses SET " +
                "course_name = ?, " +
                "duration = ?, " +
                "total_fee = ? " +
                "WHERE id = ?";

        try (
                Connection connection =
                        DBConnection.getConnection();

                PreparedStatement statement =
                        connection.prepareStatement(sql)
        ) {

            statement.setString(
                    1,
                    courseName
            );

            statement.setString(
                    2,
                    duration
            );

            statement.setDouble(
                    3,
                    totalFee
            );

            statement.setInt(
                    4,
                    selectedCourseId
            );

            statement.executeUpdate();

            JOptionPane.showMessageDialog(
                    this,
                    "Course successfully updated.",
                    "Success",
                    JOptionPane.INFORMATION_MESSAGE
            );

            clearFields();

            loadCourses();

        } catch (SQLException e) {

            showDatabaseError(e);
        }
    }

    // =========================================================
    // DELETE COURSE
    // =========================================================

    /**
     * Selected course ko delete karta hai.
     */
    private void deleteCourse() {

        if (selectedCourseId == -1) {

            JOptionPane.showMessageDialog(
                    this,
                    "Please select a course from the table.",
                    "Warning",
                    JOptionPane.WARNING_MESSAGE
            );

            return;
        }

        int choice =
                JOptionPane.showConfirmDialog(
                        this,
                        "Are you sure you want to delete this course?",
                        "Confirm Delete",
                        JOptionPane.YES_NO_OPTION
                );

        if (choice != JOptionPane.YES_OPTION) {
            return;
        }

        String sql =
                "DELETE FROM courses WHERE id = ?";

        try (
                Connection connection =
                        DBConnection.getConnection();

                PreparedStatement statement =
                        connection.prepareStatement(sql)
        ) {

            statement.setInt(
                    1,
                    selectedCourseId
            );

            statement.executeUpdate();

            JOptionPane.showMessageDialog(
                    this,
                    "Course successfully deleted.",
                    "Success",
                    JOptionPane.INFORMATION_MESSAGE
            );

            clearFields();

            loadCourses();

        } catch (SQLException e) {

            /*
             * Agar course kisi student ke saath linked hai
             * to foreign key ki wajah se delete nahi hoga.
             */
            if (e.getMessage()
                    .toLowerCase()
                    .contains("foreign key")) {

                JOptionPane.showMessageDialog(
                        this,
                        "This course is linked to existing students.\n"
                                + "Please update or remove related students first.",
                        "Cannot Delete",
                        JOptionPane.WARNING_MESSAGE
                );

            } else {

                showDatabaseError(e);
            }
        }
    }

    // =========================================================
    // LOAD COURSES
    // =========================================================

    /**
     * Database ke saare courses table mein load karta hai.
     */
    private void loadCourses() {

        tableModel.setRowCount(0);

        String sql =
                "SELECT id, course_name, duration, total_fee " +
                "FROM courses " +
                "ORDER BY id DESC";

        executeCourseQuery(
                sql,
                null
        );
    }

    // =========================================================
    // SEARCH COURSE
    // =========================================================

    /**
     * Course name ke basis par search karta hai.
     */
    private void searchCourse() {

        String searchText =
                searchField.getText().trim();

        if (searchText.isEmpty()) {

            loadCourses();

            return;
        }

        tableModel.setRowCount(0);

        String sql =
                "SELECT id, course_name, duration, total_fee " +
                "FROM courses " +
                "WHERE course_name LIKE ? " +
                "ORDER BY id DESC";

        executeCourseQuery(
                sql,
                "%" + searchText + "%"
        );
    }

    // =========================================================
    // EXECUTE COURSE QUERY
    // =========================================================

    /**
     * Course query execute karta hai.
     */
    private void executeCourseQuery(
            String sql,
            String searchPattern
    ) {

        try (
                Connection connection =
                        DBConnection.getConnection();

                PreparedStatement statement =
                        connection.prepareStatement(sql)
        ) {

            if (searchPattern != null) {

                statement.setString(
                        1,
                        searchPattern
                );
            }

            ResultSet resultSet =
                    statement.executeQuery();

            while (resultSet.next()) {

                tableModel.addRow(
                        new Object[]{
                                resultSet.getInt("id"),

                                resultSet.getString(
                                        "course_name"
                                ),

                                resultSet.getString(
                                        "duration"
                                ),

                                resultSet.getDouble(
                                        "total_fee"
                                )
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
     * Table mein selected row ki information
     * form fields mein fill karta hai.
     */
    private void fillFormFromTable() {

        int selectedRow =
                courseTable.getSelectedRow();

        if (selectedRow == -1) {
            return;
        }

        selectedCourseId =
                Integer.parseInt(
                        tableModel.getValueAt(
                                selectedRow,
                                0
                        ).toString()
                );

        courseNameField.setText(
                tableModel.getValueAt(
                        selectedRow,
                        1
                ).toString()
        );

        durationField.setText(
                tableModel.getValueAt(
                        selectedRow,
                        2
                ).toString()
        );

        totalFeeField.setText(
                tableModel.getValueAt(
                        selectedRow,
                        3
                ).toString()
        );
    }

    // =========================================================
    // CLEAR
    // =========================================================

    /**
     * Form ko reset karta hai.
     */
    private void clearFields() {

        courseNameField.setText("");

        durationField.setText("");

        totalFeeField.setText("");

        selectedCourseId = -1;

        courseTable.clearSelection();

        courseNameField.requestFocus();
    }

    // =========================================================
    // DATABASE ERROR
    // =========================================================

    /**
     * Database error display karta hai.
     */
    private void showDatabaseError(
            SQLException e
    ) {

        JOptionPane.showMessageDialog(
                this,
                "Database Error:\n"
                        + e.getMessage(),
                "Database Error",
                JOptionPane.ERROR_MESSAGE
        );
    }

    // =========================================================
    // TEST MAIN
    // =========================================================

    public static void main(String[] args) {

        SwingUtilities.invokeLater(() -> {

            CourseFrame frame =
                    new CourseFrame();

            frame.setVisible(true);
        });
    }
}