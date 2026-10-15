package com.feemanager.pendingfee;

import com.feemanager.DBConnection;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

/**
 * PendingFeeFrame
 *
 * Ye screen un students ki information dikhati hai
 * jinki fee abhi complete pay nahi hui hai.
 *
 * Features:
 * 1. Pending fee list
 * 2. Student ID / Name search
 * 3. Total pending amount
 * 4. Refresh option
 */
public class PendingFeeFrame extends JFrame {

    // Search field
    private JTextField searchField;

    // Buttons
    private JButton searchButton;
    private JButton refreshButton;

    // Pending fee table
    private JTable pendingFeeTable;

    // Table model
    private DefaultTableModel tableModel;

    // Total pending amount
    private JLabel totalPendingLabel;

    /**
     * Constructor
     */
    public PendingFeeFrame() {

        setTitle("Fee Management System - Pending Fees");
        setSize(1000, 600);

        setDefaultCloseOperation(
                JFrame.DISPOSE_ON_CLOSE
        );

        setLocationRelativeTo(null);

        createUI();

        // Screen open hote hi pending fees load karo
        loadPendingFees();
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
                "PENDING FEES"
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
                new FlowLayout(
                        FlowLayout.LEFT,
                        10,
                        10
                )
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

        // =====================================================
        // TABLE
        // =====================================================

        String[] columns = {
                "Student ID",
                "Student Name",
                "Course",
                "Total Fee",
                "Total Paid",
                "Pending Fee"
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

        pendingFeeTable = new JTable(tableModel);

        JScrollPane scrollPane =
                new JScrollPane(pendingFeeTable);

        scrollPane.setBorder(
                BorderFactory.createTitledBorder(
                        "Students With Pending Fees"
                )
        );

        // =====================================================
        // BOTTOM PANEL
        // =====================================================

        JPanel bottomPanel = new JPanel(
                new FlowLayout(FlowLayout.RIGHT)
        );

        totalPendingLabel = new JLabel(
                "Total Pending: ₹0.00"
        );

        totalPendingLabel.setFont(
                new Font("Arial", Font.BOLD, 16)
        );

        bottomPanel.add(
                totalPendingLabel
        );

        // =====================================================
        // CONTENT PANEL
        // =====================================================

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

        mainPanel.add(
                contentPanel,
                BorderLayout.CENTER
        );

        // =====================================================
        // ACTION LISTENERS
        // =====================================================

        searchButton.addActionListener(
                e -> searchPendingFees()
        );

        refreshButton.addActionListener(
                e -> {

                    searchField.setText("");

                    loadPendingFees();
                }
        );

        // Enter press karne par search
        searchField.addActionListener(
                e -> searchPendingFees()
        );

        add(mainPanel);
    }

    // =========================================================
    // LOAD PENDING FEES
    // =========================================================

    /**
     * Database se pending fee information load karta hai.
     *
     * GROUP BY ka use isliye kiya hai kyunki ek student
     * multiple installments mein payment kar sakta hai.
     */
    private void loadPendingFees() {

        tableModel.setRowCount(0);

        String sql =
                "SELECT s.student_id, s.name, " +
                "c.course_name, " +
                "c.total_fee, " +
                "COALESCE(SUM(f.paid_amount), 0) AS total_paid, " +
                "(c.total_fee - " +
                "COALESCE(SUM(f.paid_amount), 0)) AS pending_fee " +
                "FROM students s " +
                "LEFT JOIN courses c " +
                "ON s.course_id = c.id " +
                "LEFT JOIN fees f " +
                "ON s.id = f.student_id " +
                "GROUP BY s.id, s.student_id, s.name, " +
                "c.course_name, c.total_fee " +
                "HAVING pending_fee > 0 " +
                "ORDER BY pending_fee DESC";

        executePendingFeeQuery(
                sql,
                null
        );
    }

    // =========================================================
    // SEARCH PENDING FEES
    // =========================================================

    /**
     * Student ID ya Name ke basis par pending fee search karta hai.
     */
    private void searchPendingFees() {

        String searchText =
                searchField.getText().trim();

        if (searchText.isEmpty()) {

            loadPendingFees();

            return;
        }

        tableModel.setRowCount(0);

        String sql =
                "SELECT s.student_id, s.name, " +
                "c.course_name, " +
                "c.total_fee, " +
                "COALESCE(SUM(f.paid_amount), 0) AS total_paid, " +
                "(c.total_fee - " +
                "COALESCE(SUM(f.paid_amount), 0)) AS pending_fee " +
                "FROM students s " +
                "LEFT JOIN courses c " +
                "ON s.course_id = c.id " +
                "LEFT JOIN fees f " +
                "ON s.id = f.student_id " +
                "WHERE s.student_id LIKE ? " +
                "OR s.name LIKE ? " +
                "GROUP BY s.id, s.student_id, s.name, " +
                "c.course_name, c.total_fee " +
                "HAVING pending_fee > 0 " +
                "ORDER BY pending_fee DESC";

        String searchPattern =
                "%" + searchText + "%";

        executePendingFeeQuery(
                sql,
                searchPattern
        );
    }

    // =========================================================
    // EXECUTE QUERY
    // =========================================================

    /**
     * Pending fee query execute karta hai.
     *
     * Agar searchPattern null hai to query mein parameters
     * set nahi honge.
     */
    private void executePendingFeeQuery(
            String sql,
            String searchPattern
    ) {

        try (
                Connection connection =
                        DBConnection.getConnection();

                PreparedStatement statement =
                        connection.prepareStatement(sql)
        ) {

            // Search query ke parameters set karna
            if (searchPattern != null) {

                statement.setString(
                        1,
                        searchPattern
                );

                statement.setString(
                        2,
                        searchPattern
                );
            }

            ResultSet resultSet =
                    statement.executeQuery();

            double totalPending = 0;

            while (resultSet.next()) {

                double pendingFee =
                        resultSet.getDouble(
                                "pending_fee"
                        );

                totalPending += pendingFee;

                tableModel.addRow(
                        new Object[]{
                                resultSet.getString(
                                        "student_id"
                                ),

                                resultSet.getString(
                                        "name"
                                ),

                                resultSet.getString(
                                        "course_name"
                                ),

                                resultSet.getDouble(
                                        "total_fee"
                                ),

                                resultSet.getDouble(
                                        "total_paid"
                                ),

                                pendingFee
                        }
                );
            }

            // Total pending amount update karo
            updateTotalPending(totalPending);

        } catch (SQLException e) {

            showDatabaseError(e);
        }
    }

    // =========================================================
    // TOTAL PENDING
    // =========================================================

    /**
     * Total pending amount ko label mein show karta hai.
     */
    private void updateTotalPending(
            double totalPending
    ) {

        totalPendingLabel.setText(
                String.format(
                        "Total Pending: ₹%.2f",
                        totalPending
                )
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
    // TEST MAIN
    // =========================================================

    /**
     * Sirf PendingFeeFrame ko separately test karne ke liye.
     */
    public static void main(String[] args) {

        SwingUtilities.invokeLater(() -> {

            PendingFeeFrame frame =
                    new PendingFeeFrame();

            frame.setVisible(true);
        });
    }
}