package com.feemanager.fee;

import com.feemanager.DBConnection;
// import com.feemanager.dashboard.DashboardFrame;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * FeeFrame
 *
 * Ye class student ki fee collect karne ke liye hai.
 *
 * Main features:
 * 1. Student select karna
 * 2. Total fee dekhna
 * 3. Paid amount enter karna
 * 4. Remaining fee calculate karna
 * 5. Payment mode select karna
 * 6. Payment save karna
 * 7. Payment history dekhna
 */
public class FeeFrame extends JFrame {

    // Student dropdown
    private JComboBox<StudentItem> studentComboBox;

    // Fee fields
    private JTextField totalFeeField;
    private JTextField paidAmountField;
    private JTextField remainingAmountField;

    // Payment details
    private JComboBox<String> paymentModeComboBox;
    private JTextField paymentDateField;
    private JTextField remarksField;

    // Buttons
    private JButton saveButton;
    private JButton clearButton;
    private JButton backButton;

    // Payment history table
    private JTable feeTable;
    private DefaultTableModel tableModel;


    /**
     * Constructor
     */
    public FeeFrame() {

        setTitle("Fee Management System - Fee Collection");
        setSize(1100, 700);

        setDefaultCloseOperation(
                JFrame.DISPOSE_ON_CLOSE
        );

        setSize(1100, 800);
setLocationRelativeTo(null);

        createUI();

        // Database se students load karna
        loadStudents();

        // Payment history load karna
        loadFeeHistory();
    }


    /**
     * Complete Fee Collection UI create karta hai.
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
                "FEE COLLECTION"
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
        new GridLayout(7, 2, 10, 8)
);

// Form ko minimum height denge taaki saare fields dikhein
formPanel.setPreferredSize(new Dimension(500, 280));


        formPanel.setBorder(
                BorderFactory.createTitledBorder(
                        "Payment Information"
                )
        );


        // Student
        formPanel.add(
                new JLabel("Select Student:")
        );

        studentComboBox = new JComboBox<>();

        formPanel.add(
                studentComboBox
        );


        // Total Fee
        formPanel.add(
                new JLabel("Total Fee:")
        );

        totalFeeField = new JTextField();

        // Total fee manually change nahi karni
        totalFeeField.setEditable(false);

        formPanel.add(
                totalFeeField
        );


        // Paid Amount
        formPanel.add(
                new JLabel("Paid Amount:")
        );

        paidAmountField = new JTextField();

        formPanel.add(
                paidAmountField
        );


        // Remaining Amount
        formPanel.add(
                new JLabel("Remaining Amount:")
        );

        remainingAmountField = new JTextField();

        // Remaining amount automatically calculate hoga
        remainingAmountField.setEditable(false);

        formPanel.add(
                remainingAmountField
        );


        // Payment Mode
        formPanel.add(
                new JLabel("Payment Mode:")
        );

        paymentModeComboBox = new JComboBox<>(
                new String[]{
                        "Cash",
                        "UPI",
                        "Card",
                        "Bank Transfer"
                }
        );

        formPanel.add(
                paymentModeComboBox
        );


        // Payment Date
        formPanel.add(
                new JLabel("Payment Date:")
        );

        paymentDateField = new JTextField();

        // Current date automatically set karna
        paymentDateField.setText(
                getCurrentDate()
        );

        formPanel.add(
                paymentDateField
        );


        // Remarks
        formPanel.add(
                new JLabel("Remarks:")
        );

        remarksField = new JTextField();

        formPanel.add(
                remarksField
        );


        // =====================================================
        // BUTTON PANEL
        // =====================================================

        JPanel buttonPanel = new JPanel(
                new FlowLayout(
                        FlowLayout.CENTER,
                        15,
                        10
                )
        );

        saveButton = new JButton(
                "Save Payment"
        );

        clearButton = new JButton(
                "Clear"
        );
        backButton = new JButton(
                "Back"
        );

        buttonPanel.add(saveButton);
        buttonPanel.add(clearButton);
        buttonPanel.add(backButton);


        // =====================================================
        // FORM CONTAINER
        // =====================================================

        JPanel topPanel = new JPanel(
                new BorderLayout()
        );

        topPanel.add(
                formPanel,
                BorderLayout.CENTER
        );

        topPanel.add(
                buttonPanel,
                BorderLayout.SOUTH
        );


      
mainPanel.add(
        topPanel,
        BorderLayout.CENTER
);

// Payment form ko history table se alag jagah denge
topPanel.setPreferredSize(new Dimension(1000, 340));



        // =====================================================
        // PAYMENT HISTORY TABLE
        // =====================================================

        String[] columns = {
                "Fee ID",
                "Student ID",
                "Student Name",
                "Total Fee",
                "Paid",
                "Remaining",
                "Payment Date",
                "Payment Mode",
                "Remarks"
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


        feeTable = new JTable(tableModel);

        feeTable.setSelectionMode(
                ListSelectionModel.SINGLE_SELECTION
        );


      
JScrollPane tableScrollPane =
        new JScrollPane(feeTable);

// Table ki rows ki height set karna
feeTable.setRowHeight(25);

// History table ki height control karna
tableScrollPane.setPreferredSize(
        new Dimension(1000, 250)
);

tableScrollPane.setBorder(
        BorderFactory.createTitledBorder(
                "Payment History"
        )
);



        mainPanel.add(
                tableScrollPane,
                BorderLayout.SOUTH
        );


        // =====================================================
        // ACTION LISTENERS
        // === ==================================================

        /*
         * Student change hone par total fee update hogi.
         */
        studentComboBox.addActionListener(
                e -> loadSelectedStudentFee()
        );


        /*
         * Paid amount change hone par
         * remaining amount calculate hoga.
         */
        paidAmountField.addKeyListener(
                new java.awt.event.KeyAdapter() {

                    @Override
                    public void keyReleased(
                            java.awt.event.KeyEvent e
                    ) {

                        calculateRemainingFee();
                    }
                }
        );


        saveButton.addActionListener(
                e -> savePayment()
        );


        clearButton.addActionListener(
                e -> clearFields()
        );
        backButton.addActionListener(e -> {
    this.dispose();
});
            


        add(mainPanel);
    }


    // =========================================================
    // LOAD STUDENTS
    // =========================================================

    /**
     * Students table se students dropdown mein load karta hai.
     */
    private void loadStudents() {

        studentComboBox.removeAllItems();


        String sql =
                "SELECT s.id, s.student_id, s.name, " +
                "c.course_name, c.total_fee " +
                "FROM students s " +
                "LEFT JOIN courses c " +
                "ON s.course_id = c.id " +
                "ORDER BY s.name";


        try (
                Connection connection =
                        DBConnection.getConnection();

                PreparedStatement statement =
                        connection.prepareStatement(sql);

                ResultSet resultSet =
                        statement.executeQuery()
        ) {


            while (resultSet.next()) {

                studentComboBox.addItem(
                        new StudentItem(
                                resultSet.getInt("id"),
                                resultSet.getString("student_id"),
                                resultSet.getString("name"),
                                resultSet.getDouble("total_fee")
                        )
                );
            }


        } catch (SQLException e) {

            showDatabaseError(e);
        }
    }


    // =========================================================
    // LOAD SELECTED STUDENT FEE
    // =========================================================

    /**
     * Selected student ki course fee ko
     * Total Fee field mein show karta hai.
     */
    private void loadSelectedStudentFee() {

        StudentItem student =
                (StudentItem)
                        studentComboBox.getSelectedItem();


        if (student == null) {
            return;
        }


        double totalFee =
                student.totalFee;


        totalFeeField.setText(
                String.format(
                        "%.2f",
                        totalFee
                )
        );


        calculateRemainingFee();
    }


    // =========================================================
    // CALCULATE REMAINING FEE
    // =========================================================

    /**
     * Total fee - Paid amount = Remaining fee
     */
    private void calculateRemainingFee() {

        try {

            double totalFee =
                    Double.parseDouble(
                            totalFeeField.getText()
                    );


            String paidText =
                    paidAmountField.getText().trim();


            double paidAmount = 0;


            if (!paidText.isEmpty()) {

                paidAmount =
                        Double.parseDouble(
                                paidText
                        );
            }


            double remaining =
                    totalFee - paidAmount;


            remainingAmountField.setText(
                    String.format(
                            "%.2f",
                            remaining
                    )
            );


        } catch (NumberFormatException e) {

            remainingAmountField.setText(
                    "0.00"
            );
        }
    }


    // =========================================================
    // SAVE PAYMENT
    // =========================================================

    /**
     * Payment database mein save karta hai.
     */
    private void savePayment() {

        StudentItem student =
                (StudentItem)
                        studentComboBox.getSelectedItem();


        if (student == null) {

            JOptionPane.showMessageDialog(
                    this,
                    "Please student select karein.",
                    "Validation Error",
                    JOptionPane.WARNING_MESSAGE
            );

            return;
        }


        String paidText =
                paidAmountField.getText().trim();


        if (paidText.isEmpty()) {

            JOptionPane.showMessageDialog(
                    this,
                    "Paid amount enter karein.",
                    "Validation Error",
                    JOptionPane.WARNING_MESSAGE
            );

            return;
        }


        double totalFee;
        double paidAmount;
        double remainingAmount;


        try {

            totalFee =
                    Double.parseDouble(
                            totalFeeField.getText()
                    );

            paidAmount =
                    Double.parseDouble(
                            paidText
                    );

            remainingAmount =
                    totalFee - paidAmount;


        } catch (NumberFormatException e) {

            JOptionPane.showMessageDialog(
                    this,
                    "Paid amount valid number hona chahiye.",
                    "Validation Error",
                    JOptionPane.WARNING_MESSAGE
            );

            return;
        }


        // Negative payment allow nahi karenge
        if (paidAmount <= 0) {

            JOptionPane.showMessageDialog(
                    this,
                    "Paid amount 0 se greater hona chahiye.",
                    "Validation Error",
                    JOptionPane.WARNING_MESSAGE
            );

            return;
        }


        // Total fee se zyada payment allow nahi karenge
        if (paidAmount > totalFee) {

            JOptionPane.showMessageDialog(
                    this,
                    "Paid amount total fee se zyada nahi ho sakta.",
                    "Validation Error",
                    JOptionPane.WARNING_MESSAGE
            );

            return;
        }


        String sql =
                "INSERT INTO fees " +
                "(student_id, total_fee, paid_amount, " +
                "remaining_amount, payment_date, " +
                "payment_mode, remarks) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?)";


        try (
                Connection connection =
                        DBConnection.getConnection();

                PreparedStatement statement =
                        connection.prepareStatement(sql)
        ) {


            statement.setInt(
                    1,
                    student.id
            );


            statement.setDouble(
                    2,
                    totalFee
            );


            statement.setDouble(
                    3,
                    paidAmount
            );


            statement.setDouble(
                    4,
                    remainingAmount
            );


            /*
             * Date string ko SQL Date mein convert kar rahe hain.
             */
            Date parsedDate =
                    new SimpleDateFormat(
                            "yyyy-MM-dd"
                    ).parse(
                            paymentDateField.getText().trim()
                    );


            statement.setDate(
                    5,
                    new java.sql.Date(
                            parsedDate.getTime()
                    )
            );


            statement.setString(
                    6,
                    paymentModeComboBox
                            .getSelectedItem()
                            .toString()
            );


            statement.setString(
                    7,
                    remarksField.getText().trim()
            );


            statement.executeUpdate();


            JOptionPane.showMessageDialog(
                    this,
                    "Payment successfully saved!",
                    "Success",
                    JOptionPane.INFORMATION_MESSAGE
            );


            clearFields();

            loadFeeHistory();


        } catch (Exception e) {

            JOptionPane.showMessageDialog(
                    this,
                    "Payment save nahi ho paya.\n"
                            + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }


    // =========================================================
    // LOAD PAYMENT HISTORY
    // =========================================================

    /**
     * Database se payment history table mein load karta hai.
     */
    private void loadFeeHistory() {

        tableModel.setRowCount(0);


        String sql =
                "SELECT f.id, s.student_id, s.name, " +
                "f.total_fee, f.paid_amount, " +
                "f.remaining_amount, f.payment_date, " +
                "f.payment_mode, f.remarks " +
                "FROM fees f " +
                "INNER JOIN students s " +
                "ON f.student_id = s.id " +
                "ORDER BY f.id DESC";


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
                                resultSet.getDouble("total_fee"),
                                resultSet.getDouble("paid_amount"),
                                resultSet.getDouble("remaining_amount"),
                                resultSet.getDate("payment_date"),
                                resultSet.getString("payment_mode"),
                                resultSet.getString("remarks")
                        }
                );
            }


        } catch (SQLException e) {

            showDatabaseError(e);
        }
    }


    // =========================================================
    // CLEAR FIELDS
    // =========================================================

    /**
     * Fee form clear karta hai.
     */
    private void clearFields() {

        paidAmountField.setText("");
        remainingAmountField.setText("");
        remarksField.setText("");

        paymentModeComboBox.setSelectedIndex(0);

        paymentDateField.setText(
                getCurrentDate()
        );


        if (studentComboBox.getItemCount() > 0) {

            studentComboBox.setSelectedIndex(0);
        }


        paidAmountField.requestFocus();
    }


    // =========================================================
    // CURRENT DATE
    // =========================================================

    /**
     * Current date ko yyyy-MM-dd format mein return karta hai.
     */
    private String getCurrentDate() {

        SimpleDateFormat dateFormat =
                new SimpleDateFormat(
                        "yyyy-MM-dd"
                );

        return dateFormat.format(
                new Date()
        );
    }


    // =========================================================
    // DATABASE ERROR
    // =========================================================

    /**
     * Database error show karta hai.
     */
    private void showDatabaseError(SQLException e) {

        JOptionPane.showMessageDialog(
                this,
                "Database Error:\n"
                        + e.getMessage(),
                "Database Error",
                JOptionPane.ERROR_MESSAGE
        );
    }


    // =========================================================
    // STUDENT ITEM CLASS
    // =========================================================

    /**
     * Student dropdown ke liye helper class.
     *
     * Student ka ID, name aur total course fee
     * ek object mein store kar rahe hain.
     */
    private static class StudentItem {

        private final int id;
        private final String studentId;
        private final String name;
        private final double totalFee;


        public StudentItem(
                int id,
                String studentId,
                String name,
                double totalFee
        ) {

            this.id = id;
            this.studentId = studentId;
            this.name = name;
            this.totalFee = totalFee;
        }


        /*
         * ComboBox mein ye format show hoga:
         *
         * STU001 - Rahul Kumar
         */
        @Override
        public String toString() {

            return studentId + " - " + name;
        }
    }


    // =========================================================
    // TEST MAIN
    // =========================================================

    /**
     * Sirf FeeFrame ko separately test karne ke liye.
     */
    public static void main(String[] args) {

        SwingUtilities.invokeLater(() -> {

            FeeFrame frame =
                    new FeeFrame();

            frame.setVisible(true);
        });
    }
}