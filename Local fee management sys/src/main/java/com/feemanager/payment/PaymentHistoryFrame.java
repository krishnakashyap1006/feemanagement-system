package com.feemanager.payment;

import com.feemanager.DBConnection;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

/**
 * PaymentHistoryFrame
 *
 * Ye screen students ki complete payment history
 * dikhane ke liye use hogi.
 *
 * Features:
 * 1. All payments dekhna
 * 2. Student ID / Name se search
 * 3. Total collected amount
 * 4. Payment details table
 */
public class PaymentHistoryFrame extends JFrame {

    // Search field
    private JTextField searchField;

    // Search button
    private JButton searchButton;

    // Refresh button
    private JButton refreshButton;

    // Payment table
    private JTable paymentTable;

    // Table model
    private DefaultTableModel tableModel;

    // Total collection label
    private JLabel totalCollectionLabel;

    /**
     * Constructor
     */
    public PaymentHistoryFrame() {

        setTitle("Fee Management System - Payment History");
        setSize(1100, 650);

        setDefaultCloseOperation(
                JFrame.DISPOSE_ON_CLOSE
        );

        setLocationRelativeTo(null);

        createUI();

        // Application open hote hi payments load karo
        loadPayments();
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
                "PAYMENT HISTORY"
        );

        titleLabel.setFont(
                new Font("Arial", Font.BOLD, 24)
        );

        mainPanel.add(
                titleLabel,
                BorderLayout.NORTH
        );

        // =====================================================
        // SEARCH PANEL
        // =====================================================

        JPanel searchPanel = new JPanel(
                new FlowLayout(FlowLayout.LEFT, 10, 10)
        );

        searchPanel.add(
                new JLabel("Search Student:")
        );

        searchField = new JTextField(20);

        searchPanel.add(searchField);

        searchButton = new JButton("Search");
        refreshButton = new JButton("Refresh");

        searchPanel.add(searchButton);
        searchPanel.add(refreshButton);

        mainPanel.add(
                searchPanel,
                BorderLayout.CENTER
        );

        // =====================================================
        // TABLE
        // =====================================================

        String[] columns = {
                "Fee ID",
                "Student ID",
                "Student Name",
                "Total Fee",
                "Paid Amount",
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
             * Table ko directly edit nahi karne denge.
             */
            @Override
            public boolean isCellEditable(
                    int row,
                    int column
            ) {
                return false;
            }
        };

        paymentTable = new JTable(tableModel);

        paymentTable.setAutoResizeMode(
                JTable.AUTO_RESIZE_ALL_COLUMNS
        );

        JScrollPane scrollPane =
                new JScrollPane(paymentTable);

        mainPanel.add(
                scrollPane,
                BorderLayout.SOUTH
        );

        // =====================================================
        // BOTTOM PANEL
        // =====================================================

        JPanel bottomPanel = new JPanel(
                new FlowLayout(FlowLayout.RIGHT)
        );

        totalCollectionLabel = new JLabel(
                "Total Collected: ₹0.00"
        );

        totalCollectionLabel.setFont(
                new Font("Arial", Font.BOLD, 16)
        );

        bottomPanel.add(
                totalCollectionLabel
        );

        /*
         * Main layout ko properly arrange karne ke liye
         * content panel use kar rahe hain.
         */
        JPanel contentPanel = new JPanel(
                new BorderLayout(10, 10)
        );

        contentPanel.add(
                searchPanel,
                BorderLayout.NORTH
        );

        contentPanel.add(
                scrollPane,
                BorderLayout.CENTER
        );

        contentPanel.add(
                bottomPanel,
                BorderLayout.SOUTH
        );

        mainPanel.remove(searchPanel);
        mainPanel.remove(scrollPane);

        mainPanel.add(
                contentPanel,
                BorderLayout.CENTER
        );

        // =====================================================
        // ACTION LISTENERS
        // =====================================================

        searchButton.addActionListener(
                e -> searchPayments()
        );

        refreshButton.addActionListener(
                e -> {
                    searchField.setText("");
                    loadPayments();
                }
        );

        /*
         * Enter press karne par search hoga.
         */
        searchField.addActionListener(
                e -> searchPayments()
        );

        add(mainPanel);
    }

    // =========================================================
    // LOAD PAYMENTS
    // =========================================================

    /**
     * Database se saari payment history load karta hai.
     */
    private void loadPayments() {

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

            double totalCollected = 0;

            while (resultSet.next()) {

                double paidAmount =
                        resultSet.getDouble("paid_amount");

                totalCollected += paidAmount;

                tableModel.addRow(
                        new Object[]{
                                resultSet.getInt("id"),
                                resultSet.getString("student_id"),
                                resultSet.getString("name"),
                                resultSet.getDouble("total_fee"),
                                paidAmount,
                                resultSet.getDouble("remaining_amount"),
                                resultSet.getDate("payment_date"),
                                resultSet.getString("payment_mode"),
                                resultSet.getString("remarks")
                        }
                );
            }

            updateTotalCollection(totalCollected);

        } catch (SQLException e) {

            showDatabaseError(e);
        }
    }

    // =========================================================
    // SEARCH PAYMENTS
    // =========================================================

    /**
     * Student ID ya student name ke basis par
     * payment history search karta hai.
     */
    private void searchPayments() {

        String searchText =
                searchField.getText().trim();

        if (searchText.isEmpty()) {

            loadPayments();
            return;
        }

        tableModel.setRowCount(0);

        String sql =
                "SELECT f.id, s.student_id, s.name, " +
                "f.total_fee, f.paid_amount, " +
                "f.remaining_amount, f.payment_date, " +
                "f.payment_mode, f.remarks " +
                "FROM fees f " +
                "INNER JOIN students s " +
                "ON f.student_id = s.id " +
                "WHERE s.student_id LIKE ? " +
                "OR s.name LIKE ? " +
                "ORDER BY f.id DESC";

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

            ResultSet resultSet =
                    statement.executeQuery();

            double totalCollected = 0;

            while (resultSet.next()) {

                double paidAmount =
                        resultSet.getDouble("paid_amount");

                totalCollected += paidAmount;

                tableModel.addRow(
                        new Object[]{
                                resultSet.getInt("id"),
                                resultSet.getString("student_id"),
                                resultSet.getString("name"),
                                resultSet.getDouble("total_fee"),
                                paidAmount,
                                resultSet.getDouble("remaining_amount"),
                                resultSet.getDate("payment_date"),
                                resultSet.getString("payment_mode"),
                                resultSet.getString("remarks")
                        }
                );
            }

            updateTotalCollection(totalCollected);

        } catch (SQLException e) {

            showDatabaseError(e);
        }
    }

    // =========================================================
    // TOTAL COLLECTION
    // =========================================================

    /**
     * Total collected amount ko bottom label mein show karta hai.
     */
    private void updateTotalCollection(
            double totalCollected
    ) {

        totalCollectionLabel.setText(
                String.format(
                        "Total Collected: ₹%.2f",
                        totalCollected
                )
        );
    }

    // =========================================================
    // DATABASE ERROR
    // =========================================================

    /**
     * Database error ko user-friendly message mein show karta hai.
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
    // TEST MAIN
    // =========================================================

    /**
     * Sirf PaymentHistoryFrame ko separately test karne ke liye.
     */
    public static void main(String[] args) {

        SwingUtilities.invokeLater(() -> {

            PaymentHistoryFrame frame =
                    new PaymentHistoryFrame();

            frame.setVisible(true);
        });
    }
}