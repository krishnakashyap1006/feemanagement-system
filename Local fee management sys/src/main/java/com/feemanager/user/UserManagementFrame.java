package com.feemanager.user;

import com.feemanager.DBConnection;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

/**
 * UserManagementFrame
 *
 * Admin login users ko manage karne ke liye screen.
 *
 * Features:
 * 1. User Add
 * 2. User Update
 * 3. User Delete
 * 4. User Search
 * 5. User List
 */
public class UserManagementFrame extends JFrame {

    // =====================================================
    // FORM FIELDS
    // =====================================================

    private JTextField usernameField;
    private JPasswordField passwordField;
    private JTextField fullNameField;
    private JTextField searchField;

    // =====================================================
    // BUTTONS
    // =====================================================

    private JButton addButton;
    private JButton updateButton;
    private JButton deleteButton;
    private JButton clearButton;
    private JButton searchButton;
    private JButton refreshButton;

    // =====================================================
    // TABLE
    // =====================================================

    private JTable userTable;
    private DefaultTableModel tableModel;

    // Selected user database ID
    private int selectedUserId = -1;

    /**
     * Constructor
     */
    public UserManagementFrame() {

        setTitle(
                "Fee Management System - User Management"
        );

        setSize(900, 600);

        setDefaultCloseOperation(
                JFrame.DISPOSE_ON_CLOSE
        );

        setLocationRelativeTo(null);

        createUI();

        loadUsers();
    }

    // =====================================================
    // CREATE UI
    // =====================================================

    /**
     * Complete User Management UI create karta hai.
     */
    private void createUI() {

        JPanel mainPanel = new JPanel(
                new BorderLayout(15, 15)
        );

        mainPanel.setBorder(
                BorderFactory.createEmptyBorder(
                        15,
                        15,
                        15,
                        15
                )
        );

        // =================================================
        // TITLE
        // =================================================

        JLabel titleLabel =
                new JLabel("USER MANAGEMENT");

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

        // =================================================
        // FORM PANEL
        // =================================================

        JPanel formPanel = new JPanel(
                new GridBagLayout()
        );

        formPanel.setBorder(
                BorderFactory.createTitledBorder(
                        "User Details"
                )
        );

        GridBagConstraints gbc =
                new GridBagConstraints();

        gbc.insets =
                new Insets(6, 6, 6, 6);

        gbc.fill =
                GridBagConstraints.HORIZONTAL;

        // Username
        gbc.gridx = 0;
        gbc.gridy = 0;

        formPanel.add(
                new JLabel("Username:"),
                gbc
        );

        usernameField =
                new JTextField(20);

        gbc.gridx = 1;
        gbc.weightx = 1;

        formPanel.add(
                usernameField,
                gbc
        );

        // Password
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 0;

        formPanel.add(
                new JLabel("Password:"),
                gbc
        );

        passwordField =
                new JPasswordField(20);

        gbc.gridx = 1;
        gbc.weightx = 1;

        formPanel.add(
                passwordField,
                gbc
        );

        // Full Name
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.weightx = 0;

        formPanel.add(
                new JLabel("Full Name:"),
                gbc
        );

        fullNameField =
                new JTextField(20);

        gbc.gridx = 1;
        gbc.weightx = 1;

        formPanel.add(
                fullNameField,
                gbc
        );

        // =================================================
        // BUTTON PANEL
        // =================================================

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

        // =================================================
        // SEARCH PANEL
        // =================================================

        JPanel searchPanel = new JPanel(
                new FlowLayout(
                        FlowLayout.LEFT
                )
        );

        searchPanel.add(
                new JLabel("Search User:")
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

        // =================================================
        // TABLE
        // =================================================

        String[] columns = {
                "ID",
                "Username",
                "Full Name"
        };

        tableModel =
                new DefaultTableModel(
                        columns,
                        0
                ) {

                    // Table ko directly editable nahi hone denge
                    @Override
                    public boolean isCellEditable(
                            int row,
                            int column
                    ) {
                        return false;
                    }
                };

        userTable =
                new JTable(tableModel);

        JScrollPane scrollPane =
                new JScrollPane(userTable);

        scrollPane.setBorder(
                BorderFactory.createTitledBorder(
                        "User List"
                )
        );

        // =================================================
        // TOP PANEL
        // =================================================

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

        // =================================================
        // ACTION LISTENERS
        // =================================================

        addButton.addActionListener(
                e -> addUser()
        );

        updateButton.addActionListener(
                e -> updateUser()
        );

        deleteButton.addActionListener(
                e -> deleteUser()
        );

        clearButton.addActionListener(
                e -> clearFields()
        );

        searchButton.addActionListener(
                e -> searchUser()
        );

        refreshButton.addActionListener(
                e -> {

                    searchField.setText("");

                    loadUsers();
                }
        );

        // Table row select
        userTable.getSelectionModel()
                .addListSelectionListener(
                        e -> fillFormFromTable()
                );

        // Enter press = Search
        searchField.addActionListener(
                e -> searchUser()
        );
    }

    // =====================================================
    // ADD USER
    // =====================================================

    /**
     * New login user database mein add karta hai.
     */
    private void addUser() {

        String username =
                usernameField.getText().trim();

        String password =
                new String(
                        passwordField.getPassword()
                ).trim();

        String fullName =
                fullNameField.getText().trim();

        // Validation
        if (username.isEmpty()
                || password.isEmpty()
                || fullName.isEmpty()) {

            JOptionPane.showMessageDialog(
                    this,
                    "Please fill all fields.",
                    "Validation Error",
                    JOptionPane.WARNING_MESSAGE
            );

            return;
        }

        String sql =
                "INSERT INTO users " +
                "(username, password, full_name) " +
                "VALUES (?, ?, ?)";

        try (
                Connection connection =
                        DBConnection.getConnection();

                PreparedStatement statement =
                        connection.prepareStatement(sql)
        ) {

            statement.setString(
                    1,
                    username
            );

            statement.setString(
                    2,
                    password
            );

            statement.setString(
                    3,
                    fullName
            );

            statement.executeUpdate();

            JOptionPane.showMessageDialog(
                    this,
                    "User successfully added.",
                    "Success",
                    JOptionPane.INFORMATION_MESSAGE
            );

            clearFields();

            loadUsers();

        } catch (SQLException e) {

            if (e.getMessage()
                    .toLowerCase()
                    .contains("duplicate")) {

                JOptionPane.showMessageDialog(
                        this,
                        "This username already exists.",
                        "Duplicate Username",
                        JOptionPane.WARNING_MESSAGE
                );

            } else {

                showDatabaseError(e);
            }
        }
    }

    // =====================================================
    // UPDATE USER
    // =====================================================

    /**
     * Selected user ki details update karta hai.
     */
    private void updateUser() {

        if (selectedUserId == -1) {

            JOptionPane.showMessageDialog(
                    this,
                    "Please select a user from the table.",
                    "Warning",
                    JOptionPane.WARNING_MESSAGE
            );

            return;
        }

        String username =
                usernameField.getText().trim();

        String password =
                new String(
                        passwordField.getPassword()
                ).trim();

        String fullName =
                fullNameField.getText().trim();

        if (username.isEmpty()
                || fullName.isEmpty()) {

            JOptionPane.showMessageDialog(
                    this,
                    "Username and Full Name are required.",
                    "Validation Error",
                    JOptionPane.WARNING_MESSAGE
            );

            return;
        }

        /*
         * Password blank hai to old password
         * change nahi hoga.
         */
        String sql;

        if (password.isEmpty()) {

            sql =
                    "UPDATE users SET " +
                    "username = ?, " +
                    "full_name = ? " +
                    "WHERE id = ?";

        } else {

            sql =
                    "UPDATE users SET " +
                    "username = ?, " +
                    "password = ?, " +
                    "full_name = ? " +
                    "WHERE id = ?";
        }

        try (
                Connection connection =
                        DBConnection.getConnection();

                PreparedStatement statement =
                        connection.prepareStatement(sql)
        ) {

            if (password.isEmpty()) {

                statement.setString(
                        1,
                        username
                );

                statement.setString(
                        2,
                        fullName
                );

                statement.setInt(
                        3,
                        selectedUserId
                );

            } else {

                statement.setString(
                        1,
                        username
                );

                statement.setString(
                        2,
                        password
                );

                statement.setString(
                        3,
                        fullName
                );

                statement.setInt(
                        4,
                        selectedUserId
                );
            }

            statement.executeUpdate();

            JOptionPane.showMessageDialog(
                    this,
                    "User successfully updated.",
                    "Success",
                    JOptionPane.INFORMATION_MESSAGE
            );

            clearFields();

            loadUsers();

        } catch (SQLException e) {

            showDatabaseError(e);
        }
    }

    // =====================================================
    // DELETE USER
    // =====================================================

    /**
     * Selected user delete karta hai.
     */
    private void deleteUser() {

        if (selectedUserId == -1) {

            JOptionPane.showMessageDialog(
                    this,
                    "Please select a user from the table.",
                    "Warning",
                    JOptionPane.WARNING_MESSAGE
            );

            return;
        }

        int choice =
                JOptionPane.showConfirmDialog(
                        this,
                        "Are you sure you want to delete this user?",
                        "Confirm Delete",
                        JOptionPane.YES_NO_OPTION
                );

        if (choice != JOptionPane.YES_OPTION) {
            return;
        }

        String sql =
                "DELETE FROM users WHERE id = ?";

        try (
                Connection connection =
                        DBConnection.getConnection();

                PreparedStatement statement =
                        connection.prepareStatement(sql)
        ) {

            statement.setInt(
                    1,
                    selectedUserId
            );

            statement.executeUpdate();

            JOptionPane.showMessageDialog(
                    this,
                    "User successfully deleted.",
                    "Success",
                    JOptionPane.INFORMATION_MESSAGE
            );

            clearFields();

            loadUsers();

        } catch (SQLException e) {

            showDatabaseError(e);
        }
    }

    // =====================================================
    // LOAD USERS
    // =====================================================

    /**
     * Database ke saare users table mein load karta hai.
     */
    private void loadUsers() {

        tableModel.setRowCount(0);

        String sql =
                "SELECT id, username, full_name " +
                "FROM users " +
                "ORDER BY id DESC";

        executeUserQuery(
                sql,
                null
        );
    }

    // =====================================================
    // SEARCH USER
    // =====================================================

    /**
     * Username ya Full Name ke basis par search karta hai.
     */
    private void searchUser() {

        String searchText =
                searchField.getText().trim();

        if (searchText.isEmpty()) {

            loadUsers();

            return;
        }

        tableModel.setRowCount(0);

        String sql =
                "SELECT id, username, full_name " +
                "FROM users " +
                "WHERE username LIKE ? " +
                "OR full_name LIKE ? " +
                "ORDER BY id DESC";

        String searchPattern =
                "%" + searchText + "%";

        try (
                Connection connection =
                        DBConnection.getConnection();

                PreparedStatement statement =
                        connection.prepareStatement(sql)
        ) {

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

            while (resultSet.next()) {

                tableModel.addRow(
                        new Object[]{
                                resultSet.getInt("id"),
                                resultSet.getString("username"),
                                resultSet.getString("full_name")
                        }
                );
            }

        } catch (SQLException e) {

            showDatabaseError(e);
        }
    }

    // =====================================================
    // EXECUTE USER QUERY
    // =====================================================

    private void executeUserQuery(
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
                                resultSet.getString("username"),
                                resultSet.getString("full_name")
                        }
                );
            }

        } catch (SQLException e) {

            showDatabaseError(e);
        }
    }

    // =====================================================
    // FILL FORM FROM TABLE
    // =====================================================

    /**
     * Table row select hone par form mein data fill karta hai.
     */
    private void fillFormFromTable() {

        int selectedRow =
                userTable.getSelectedRow();

        if (selectedRow == -1) {
            return;
        }

        selectedUserId =
                Integer.parseInt(
                        tableModel.getValueAt(
                                selectedRow,
                                0
                        ).toString()
                );

        usernameField.setText(
                tableModel.getValueAt(
                        selectedRow,
                        1
                ).toString()
        );

        /*
         * Security ke liye old password
         * table mein show nahi kar rahe.
         */
        passwordField.setText("");

        fullNameField.setText(
                tableModel.getValueAt(
                        selectedRow,
                        2
                ).toString()
        );
    }

    // =====================================================
    // CLEAR
    // =====================================================

    private void clearFields() {

        usernameField.setText("");

        passwordField.setText("");

        fullNameField.setText("");

        selectedUserId = -1;

        userTable.clearSelection();

        usernameField.requestFocus();
    }

    // =====================================================
    // DATABASE ERROR
    // =====================================================

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

    // =====================================================
    // TEST MAIN
    // =====================================================

    public static void main(String[] args) {

        SwingUtilities.invokeLater(() -> {

            UserManagementFrame frame =
                    new UserManagementFrame();

            frame.setVisible(true);
        });
    }
}