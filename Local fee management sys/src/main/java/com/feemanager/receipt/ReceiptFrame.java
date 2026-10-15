package com.feemanager.receipt;

import com.feemanager.DBConnection;

import javax.swing.*;
import java.awt.*;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * ReceiptFrame
 *
 * Ye screen student ki fee payment ki receipt generate karti hai.
 *
 * Features:
 * 1. Student select karna
 * 2. Payment select karna
 * 3. Receipt display karna
 * 4. Print receipt
 */
public class ReceiptFrame extends JFrame {

    // Student dropdown
    private JComboBox<StudentItem> studentComboBox;

    // Payment dropdown
    private JComboBox<PaymentItem> paymentComboBox;

    // Buttons
    private JButton generateButton;
    private JButton printButton;
    private JButton clearButton;

    // Receipt area
    private JTextArea receiptArea;

    /**
     * Constructor
     */
    public ReceiptFrame() {

        setTitle("Fee Management System - Fee Receipt");
        setSize(800, 650);

        setDefaultCloseOperation(
                JFrame.DISPOSE_ON_CLOSE
        );

        setLocationRelativeTo(null);

        createUI();

        // Students load karo
        loadStudents();
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

        JLabel titleLabel = new JLabel(
                "FEE RECEIPT"
        );

        titleLabel.setFont(
                new Font("Arial", Font.BOLD, 24)
        );

        mainPanel.add(
                titleLabel,
                BorderLayout.NORTH
        );

        // =====================================================
        // SELECTION PANEL
        // =====================================================

        JPanel selectionPanel = new JPanel(
                new GridBagLayout()
        );

        selectionPanel.setBorder(
                BorderFactory.createTitledBorder(
                        "Select Payment"
                )
        );

        GridBagConstraints gbc =
                new GridBagConstraints();

        gbc.insets = new Insets(8, 8, 8, 8);

        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Student label
        gbc.gridx = 0;
        gbc.gridy = 0;

        selectionPanel.add(
                new JLabel("Student:"),
                gbc
        );

        // Student combo
        studentComboBox =
                new JComboBox<>();

        gbc.gridx = 1;
        gbc.weightx = 1;

        selectionPanel.add(
                studentComboBox,
                gbc
        );

        // Payment label
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 0;

        selectionPanel.add(
                new JLabel("Payment:"),
                gbc
        );

        // Payment combo
        paymentComboBox =
                new JComboBox<>();

        gbc.gridx = 1;
        gbc.weightx = 1;

        selectionPanel.add(
                paymentComboBox,
                gbc
        );

        // =====================================================
        // BUTTON PANEL
        // =====================================================

        JPanel buttonPanel = new JPanel(
                new FlowLayout()
        );

        generateButton =
                new JButton("Generate Receipt");

        printButton =
                new JButton("Print Receipt");

        clearButton =
                new JButton("Clear");

        // Starting mein print disabled
        printButton.setEnabled(false);

        buttonPanel.add(generateButton);
        buttonPanel.add(printButton);
        buttonPanel.add(clearButton);

        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;

        selectionPanel.add(
                buttonPanel,
                gbc
        );

        // =====================================================
        // RECEIPT AREA
        // =====================================================

        receiptArea = new JTextArea();

        receiptArea.setFont(
                new Font(
                        "Monospaced",
                        Font.PLAIN,
                        14
                )
        );

        receiptArea.setEditable(false);

        receiptArea.setMargin(
                new Insets(15, 15, 15, 15)
        );

        JScrollPane receiptScrollPane =
                new JScrollPane(receiptArea);

        receiptScrollPane.setBorder(
                BorderFactory.createTitledBorder(
                        "Receipt Preview"
                )
        );

        // =====================================================
        // CONTENT
        // =====================================================

        JPanel contentPanel = new JPanel(
                new BorderLayout(10, 10)
        );

        contentPanel.add(
                selectionPanel,
                BorderLayout.NORTH
        );

        contentPanel.add(
                receiptScrollPane,
                BorderLayout.CENTER
        );

        mainPanel.add(
                contentPanel,
                BorderLayout.CENTER
        );

        add(mainPanel);

        // =====================================================
        // ACTION LISTENERS
        // =====================================================

        // Student change hone par payments load karo
        studentComboBox.addActionListener(
                e -> loadPayments()
        );

        // Receipt generate
        generateButton.addActionListener(
                e -> generateReceipt()
        );

        // Receipt print
        printButton.addActionListener(
                e -> printReceipt()
        );

        // Clear
        clearButton.addActionListener(
                e -> clearReceipt()
        );
    }

    // =========================================================
    // LOAD STUDENTS
    // =========================================================

    /**
     * Database se students load karta hai.
     */
    private void loadStudents() {

        studentComboBox.removeAllItems();

        String sql =
                "SELECT id, student_id, name " +
                "FROM students " +
                "ORDER BY name";

        try (
                Connection connection =
                        DBConnection.getConnection();

                PreparedStatement statement =
                        connection.prepareStatement(sql);

                ResultSet resultSet =
                        statement.executeQuery()
        ) {

            while (resultSet.next()) {

                StudentItem student =
                        new StudentItem(
                                resultSet.getInt("id"),
                                resultSet.getString("student_id"),
                                resultSet.getString("name")
                        );

                studentComboBox.addItem(student);
            }

            // Students load hone ke baad payments load
            if (studentComboBox.getItemCount() > 0) {

                loadPayments();
            }

        } catch (SQLException e) {

            showDatabaseError(e);
        }
    }

    // =========================================================
    // LOAD PAYMENTS
    // =========================================================

    /**
     * Selected student ki payment history load karta hai.
     */
    private void loadPayments() {

        paymentComboBox.removeAllItems();

        StudentItem student =
                (StudentItem) studentComboBox.getSelectedItem();

        if (student == null) {
            return;
        }

        String sql =
                "SELECT id, paid_amount, payment_date, " +
                "payment_mode " +
                "FROM fees " +
                "WHERE student_id = ? " +
                "ORDER BY payment_date DESC, id DESC";

        try (
                Connection connection =
                        DBConnection.getConnection();

                PreparedStatement statement =
                        connection.prepareStatement(sql)
        ) {

            statement.setInt(
                    1,
                    student.getId()
            );

            ResultSet resultSet =
                    statement.executeQuery();

            while (resultSet.next()) {

                PaymentItem payment =
                        new PaymentItem(
                                resultSet.getInt("id"),
                                resultSet.getDouble("paid_amount"),
                                resultSet.getDate("payment_date"),
                                resultSet.getString("payment_mode")
                        );

                paymentComboBox.addItem(payment);
            }

        } catch (SQLException e) {

            showDatabaseError(e);
        }
    }

    // =========================================================
    // GENERATE RECEIPT
    // =========================================================

    /**
     * Selected payment ki complete receipt generate karta hai.
     */
    private void generateReceipt() {

        StudentItem student =
                (StudentItem) studentComboBox.getSelectedItem();

        PaymentItem payment =
                (PaymentItem) paymentComboBox.getSelectedItem();

        if (student == null) {

            JOptionPane.showMessageDialog(
                    this,
                    "Please select a student.",
                    "Warning",
                    JOptionPane.WARNING_MESSAGE
            );

            return;
        }

        if (payment == null) {

            JOptionPane.showMessageDialog(
                    this,
                    "No payment records available for the selected student.",
                    "Warning",
                    JOptionPane.WARNING_MESSAGE
            );

            return;
        }

        String sql =
                "SELECT f.id AS payment_id, s.student_id, s.name, " +
                "s.father_name, s.mobile, " +
                "c.course_name, c.total_fee, " +
                "f.paid_amount, f.remaining_amount, " +
                "f.payment_date, f.payment_mode, " +
                "f.remarks " +
                "FROM fees f " +
                "JOIN students s ON f.student_id = s.id " +
                "JOIN courses c ON s.course_id = c.id " +
                "WHERE f.id = ?";

        try (
                Connection connection =
                        DBConnection.getConnection();

                PreparedStatement statement =
                        connection.prepareStatement(sql)
        ) {

            statement.setInt(
                    1,
                    payment.getId()
            );

            ResultSet resultSet =
                    statement.executeQuery();

            if (resultSet.next()) {

                createReceiptText(resultSet);
            }

        } catch (SQLException e) {

            showDatabaseError(e);
        }
    }

    // =========================================================
    // CREATE RECEIPT TEXT
    // =========================================================

    /**
     * Receipt ka formatted text banata hai.
     */
    private void createReceiptText(
            ResultSet resultSet
    ) throws SQLException {

        StringBuilder receipt =
                new StringBuilder();

        receipt.append(
                "\n"
        );

        receipt.append(
                "================================================\n"
        );

        receipt.append(
                "             FEE MANAGEMENT SYSTEM             \n"
        );

        receipt.append(
                "                  FEE RECEIPT                  \n"
        );

        receipt.append(
                "================================================\n"
        );

        receipt.append(
                "Receipt No. : "
        );

        receipt.append(
                resultSet.getInt("payment_id")
        );

        receipt.append("\n");

        receipt.append(
                "Payment Date: "
        );

        receipt.append(
                resultSet.getDate("payment_date")
        );

        receipt.append("\n");

        receipt.append(
                "------------------------------------------------\n"
        );

        receipt.append(
                "Student ID  : "
        );

        receipt.append(
                resultSet.getString("student_id")
        );

        receipt.append("\n");

        receipt.append(
                "Student Name: "
        );

        receipt.append(
                resultSet.getString("name")
        );

        receipt.append("\n");

        receipt.append(
                "Father Name : "
        );

        receipt.append(
                resultSet.getString("father_name")
        );

        receipt.append("\n");

        receipt.append(
                "Mobile      : "
        );

        receipt.append(
                resultSet.getString("mobile")
        );

        receipt.append("\n");

        receipt.append(
                "Course      : "
        );

        receipt.append(
                resultSet.getString("course_name")
        );

        receipt.append("\n");

        receipt.append(
                "------------------------------------------------\n"
        );

        receipt.append(
                String.format(
                        "Total Course Fee : ₹%.2f%n",
                        resultSet.getDouble("total_fee")
                )
        );

        receipt.append(
                String.format(
                        "Amount Paid      : ₹%.2f%n",
                        resultSet.getDouble("paid_amount")
                )
        );

        receipt.append(
                String.format(
                        "Remaining Fee    : ₹%.2f%n",
                        resultSet.getDouble("remaining_amount")
                )
        );

        receipt.append(
                "Payment Mode     : "
        );

        receipt.append(
                resultSet.getString("payment_mode")
        );

        receipt.append("\n");

        receipt.append(
                "------------------------------------------------\n"
        );

        receipt.append(
                "Remarks          : "
        );

        String remarks =
                resultSet.getString("remarks");

        receipt.append(
                remarks == null ? "-" : remarks
        );

        receipt.append("\n");

        receipt.append(
                "================================================\n"
        );

        receipt.append(
                "          Thank You For Your Payment!           \n"
        );

        receipt.append(
                "================================================\n"
        );

        receipt.append("\n");

        receipt.append(
                "Generated On: "
        );

        receipt.append(
                new SimpleDateFormat(
                        "dd-MM-yyyy HH:mm:ss"
                ).format(new Date())
        );

        receiptArea.setText(
                receipt.toString()
        );

        receiptArea.setCaretPosition(0);

        printButton.setEnabled(true);
    }

    // =========================================================
    // PRINT RECEIPT
    // =========================================================

    /**
     * Receipt ko printer par print karta hai.
     */
    private void printReceipt() {

        try {

            boolean printed =
                    receiptArea.print();

            if (printed) {

                JOptionPane.showMessageDialog(
                        this,
                        "Receipt printed successfully.",
                        "Print",
                        JOptionPane.INFORMATION_MESSAGE
                );
            }

        } catch (Exception e) {

            JOptionPane.showMessageDialog(
                    this,
                    "Unable to print receipt.\n"
                            + e.getMessage(),
                    "Print Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    // =========================================================
    // CLEAR
    // =========================================================

    /**
     * Receipt preview clear karta hai.
     */
    private void clearReceipt() {

        receiptArea.setText("");

        printButton.setEnabled(false);
    }

    // =========================================================
    // DATABASE ERROR
    // =========================================================

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
     * Student dropdown mein readable format show karne ke liye.
     */
    private static class StudentItem {

        private final int id;
        private final String studentId;
        private final String name;

        public StudentItem(
                int id,
                String studentId,
                String name
        ) {

            this.id = id;
            this.studentId = studentId;
            this.name = name;
        }

        public int getId() {
            return id;
        }

        @Override
        public String toString() {

            return studentId + " - " + name;
        }
    }

    // =========================================================
    // PAYMENT ITEM CLASS
    // =========================================================

    /**
     * Payment dropdown mein payment details show karne ke liye.
     */
    private static class PaymentItem {

        private final int id;
        private final double paidAmount;
        private final Date paymentDate;
        private final String paymentMode;

        public PaymentItem(
                int id,
                double paidAmount,
                Date paymentDate,
                String paymentMode
        ) {

            this.id = id;
            this.paidAmount = paidAmount;
            this.paymentDate = paymentDate;
            this.paymentMode = paymentMode;
        }

        public int getId() {
            return id;
        }

        @Override
        public String toString() {

            return "Payment #"
                    + id
                    + " | ₹"
                    + String.format(
                            "%.2f",
                            paidAmount
                    )
                    + " | "
                    + paymentDate
                    + " | "
                    + paymentMode;
        }
    }

    // =========================================================
    // TEST MAIN
    // =========================================================

    public static void main(String[] args) {

        SwingUtilities.invokeLater(() -> {

            ReceiptFrame frame =
                    new ReceiptFrame();

            frame.setVisible(true);
        });
    }
}