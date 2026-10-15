package com.feemanager.dashboard;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import com.feemanager.DBConnection;
import com.feemanager.course.CourseFrame;
import com.feemanager.fee.FeeFrame;
import com.feemanager.login.LoginFrame;
import com.feemanager.payment.PaymentHistoryFrame;
import com.feemanager.pendingfee.PendingFeeFrame;
import com.feemanager.receipt.ReceiptFrame;
import com.feemanager.student.StudentFrame;
import com.feemanager.user.UserManagementFrame;

/**
 * Simplified & Optimized DashboardFrame
 * Intermediate level coders ke liye simple, clean aur readable code.
 *
 * Features:
 * 1. hamburger button that toggle the top sidebar.
 * 2. Clean modern UI (Burgundy & White theme).
 * 3. 4 KPI Metric Cards (Students, Courses, Collection, Pending).
 * 4. Quick Module buttons & Academic overview.
 * 5. Simple standard Swing components - no complex Graphics2D math.
 */
public class DashboardFrame extends JFrame {

    // Theme Colors
    private static final Color COLOR_PRIMARY = new Color(122, 28, 46);       // Deep Maroon (#7A1C2E)
    private static final Color COLOR_PRIMARY_HOVER = new Color(150, 35, 57);
    private static final Color COLOR_BG = new Color(245, 247, 250);          // Soft Grey Canvas
    private static final Color COLOR_BORDER = new Color(226, 232, 240);      // Light Border
    private static final Color COLOR_ACTIVE_BG = new Color(254, 235, 237);   // Soft Pink
    private static final Color COLOR_TEXT_DARK = new Color(30, 41, 59);
    private static final Color COLOR_TEXT_MUTED = new Color(100, 116, 139);

    // Sidebar panel (toggle karne ke liye global reference)
    private final JPanel sidebarPanel;

    // Statistics Labels
    private JLabel totalStudentsLabel;
    private JLabel totalCoursesLabel;
    private JLabel totalCollectionLabel;
    private JLabel pendingFeesLabel;
    private JLabel dbStatusLabel;

    public DashboardFrame() {
        setTitle("Academic Portal & Fee Management System - Dashboard");
        setSize(1200, 800);
        setMinimumSize(new Dimension(950, 650));
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Root container
        JPanel rootPanel = new JPanel(new BorderLayout());
        rootPanel.setBackground(COLOR_BG);

        // 1. Left Sidebar
        sidebarPanel = createSidebar();
        rootPanel.add(sidebarPanel, BorderLayout.WEST);

        // 2. Main Content (Header + Scrollable Dashboard Cards)
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(COLOR_BG);

        // Top Header (with Hamburger Menu button)
        mainPanel.add(createHeader(), BorderLayout.NORTH);

        // Dashboard Body
        JScrollPane scrollPane = new JScrollPane(createDashboardBody());
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        scrollPane.setBackground(COLOR_BG);
        scrollPane.getViewport().setBackground(COLOR_BG);

        mainPanel.add(scrollPane, BorderLayout.CENTER);
        rootPanel.add(mainPanel, BorderLayout.CENTER);

        add(rootPanel);

        // Database se statistics load karna
        loadDashboardStats();
    }

    // =========================================================================
    // 1. TOP HEADER (Contains "☰" Hamburger Menu Button)
    // =========================================================================
    private JPanel createHeader() {
        JPanel header = new JPanel(new BorderLayout(15, 0));
        header.setBackground(Color.WHITE);
        header.setPreferredSize(new Dimension(0, 65));
        header.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, COLOR_BORDER),
                BorderFactory.createEmptyBorder(10, 15, 10, 20)
        ));

        // Left Side: Three Line (☰) Button + Greeting
        JPanel leftSide = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 5));
        leftSide.setOpaque(false);

        // --- UPSIDE THREE LINE (HAMBURGER) BUTTON ---
        JButton menuButton = new JButton("...");
        menuButton.setFont(new Font("Segoe UI", Font.BOLD, 22));
        menuButton.setForeground(COLOR_PRIMARY);
        menuButton.setBackground(Color.WHITE);
        menuButton.setFocusPainted(false);
        menuButton.setBorder(BorderFactory.createEmptyBorder(2, 8, 2, 8));
        menuButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        menuButton.setToolTipText("Toggle Sidebar");

        // Click karne par sidebar open / close hota hai
        menuButton.addActionListener(e -> {
            sidebarPanel.setVisible(!sidebarPanel.isVisible());
            revalidate();
            repaint();
        });
        leftSide.add(menuButton);

        // Greeting Text
        JPanel greetingPanel = new JPanel(new GridLayout(2, 1, 0, 2));
        greetingPanel.setOpaque(false);

        JLabel greetingLabel = new JLabel("Welcome, Administrator");
        greetingLabel.setFont(new Font("Segoe UI", Font.BOLD, 17));
        greetingLabel.setForeground(COLOR_TEXT_DARK);

        String dateStr = LocalDate.now().format(DateTimeFormatter.ofPattern("EEEE, d MMMM uuuu", Locale.ENGLISH));
        JLabel dateLabel = new JLabel(dateStr + "  •  Academic Portal");
        dateLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        dateLabel.setForeground(COLOR_TEXT_MUTED);

        greetingPanel.add(greetingLabel);
        greetingPanel.add(dateLabel);
        leftSide.add(greetingPanel);

        header.add(leftSide, BorderLayout.WEST);

        // Right Side: Live Status, Bell, User Chip
        JPanel rightSide = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 8));
        rightSide.setOpaque(false);

        dbStatusLabel = new JLabel("● Database Connected");
        dbStatusLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        dbStatusLabel.setForeground(new Color(16, 185, 129));
        rightSide.add(dbStatusLabel);

        JLabel bellLabel = new JLabel("🔔 3");
        bellLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        bellLabel.setForeground(COLOR_PRIMARY);
        bellLabel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(COLOR_BORDER, 1, true),
                BorderFactory.createEmptyBorder(4, 10, 4, 10)
        ));
        rightSide.add(bellLabel);

        JLabel userLabel = new JLabel("👤 Admin User");
        userLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        userLabel.setForeground(COLOR_TEXT_DARK);
        rightSide.add(userLabel);

        header.add(rightSide, BorderLayout.EAST);
        return header;
    }

    // =========================================================================
    // 2. SIDEBAR
    // =========================================================================
    private JPanel createSidebar() {
        JPanel sidebar = new JPanel();
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBackground(Color.WHITE);
        sidebar.setPreferredSize(new Dimension(240, 0));
        sidebar.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 0, 1, COLOR_BORDER),
                BorderFactory.createEmptyBorder(15, 12, 15, 12)
        ));

        // Brand / Logo
        JLabel brandTitle = new JLabel("🎓 School Name");
        brandTitle.setFont(new Font("Segoe UI", Font.BOLD, 17));
        brandTitle.setForeground(COLOR_PRIMARY);
        brandTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        sidebar.add(brandTitle);

        JLabel brandSub = new JLabel("Fee Portal System");
        brandSub.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        brandSub.setForeground(COLOR_TEXT_MUTED);
        brandSub.setAlignmentX(Component.LEFT_ALIGNMENT);
        sidebar.add(brandSub);
        sidebar.add(Box.createVerticalStrut(20));

        // Menu Sections & Buttons
        addSectionHeader(sidebar, "DASHBOARD");
        sidebar.add(createMenuButton("Dashboard", "✦", null, true));
        sidebar.add(Box.createVerticalStrut(12));

        addSectionHeader(sidebar, "ACADEMICS & STUDENTS");
        sidebar.add(createMenuButton("Student Management", "●", e -> new StudentFrame().setVisible(true), false));
        sidebar.add(createMenuButton("Course Management", "●", e -> new CourseFrame().setVisible(true), false));
        sidebar.add(createMenuButton("User Management", "●", e -> new UserManagementFrame().setVisible(true), false));
        sidebar.add(Box.createVerticalStrut(12));

        addSectionHeader(sidebar, "FEES & FINANCE");
        sidebar.add(createMenuButton("Fee Collection", "●", e -> new FeeFrame().setVisible(true), false));
        sidebar.add(createMenuButton("Payment History", "●", e -> new PaymentHistoryFrame().setVisible(true), false));
        sidebar.add(createMenuButton("Pending Fees", "●", e -> new PendingFeeFrame().setVisible(true), false));
        sidebar.add(createMenuButton("Fee Receipt", "●", e -> new ReceiptFrame().setVisible(true), false));
        sidebar.add(Box.createVerticalStrut(12));

        addSectionHeader(sidebar, "SYSTEM");
        sidebar.add(createMenuButton("Refresh Stats", "↻", e -> loadDashboardStats(), false));
        sidebar.add(createMenuButton("Logout", "↪", e -> logout(), false));

        sidebar.add(Box.createVerticalGlue());

        JLabel statusLabel = new JLabel("● Online  |  v1.0");
        statusLabel.setFont(new Font("Segoe UI", Font.BOLD, 11));
        statusLabel.setForeground(new Color(16, 185, 129));
        statusLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        sidebar.add(statusLabel);

        return sidebar;
    }

    private void addSectionHeader(JPanel panel, String title) {
        JLabel label = new JLabel(title);
        label.setFont(new Font("Segoe UI", Font.BOLD, 10));
        label.setForeground(new Color(148, 163, 184));
        label.setBorder(BorderFactory.createEmptyBorder(6, 8, 4, 0));
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(label);
    }

    private JButton createMenuButton(String text, String icon, ActionListener action, boolean isActive) {
        JButton btn = new JButton("  " + icon + "  " + text);
        btn.setFont(new Font("Segoe UI", isActive ? Font.BOLD : Font.PLAIN, 13));
        btn.setForeground(isActive ? COLOR_PRIMARY : COLOR_TEXT_MUTED);
        btn.setBackground(isActive ? COLOR_ACTIVE_BG : Color.WHITE);
        btn.setHorizontalAlignment(SwingConstants.LEFT);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setMaximumSize(new Dimension(Short.MAX_VALUE, 35));
        btn.setAlignmentX(Component.LEFT_ALIGNMENT);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        if (action != null) {
            btn.addActionListener(action);
        }
        return btn;
    }

    // =========================================================================
    // 3. DASHBOARD CARDS BODY
    // =========================================================================
    private JPanel createDashboardBody() {
        JPanel body = new JPanel();
        body.setLayout(new BoxLayout(body, BoxLayout.Y_AXIS));
        body.setBackground(COLOR_BG);
        body.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // ROW 1: Administrator Profile & Notice Board
        JPanel row1 = new JPanel(new GridLayout(1, 2, 16, 0));
        row1.setOpaque(false);
        row1.setMaximumSize(new Dimension(Short.MAX_VALUE, 170));

        row1.add(createProfileCard());
        row1.add(createNoticeCard());
        body.add(row1);
        body.add(Box.createVerticalStrut(16));

        // ROW 2: 4 KPI Statistics Cards
        JPanel row2 = new JPanel(new GridLayout(1, 4, 14, 0));
        row2.setOpaque(false);
        row2.setMaximumSize(new Dimension(Short.MAX_VALUE, 110));

        totalStudentsLabel = new JLabel("0");
        totalCoursesLabel = new JLabel("0");
        totalCollectionLabel = new JLabel("Rs. 0.00");
        pendingFeesLabel = new JLabel("Rs. 0.00");

        row2.add(createKpiCard("Total Students", totalStudentsLabel, "Enrolled students", "👥", new Color(79, 70, 229)));
        row2.add(createKpiCard("Total Courses", totalCoursesLabel, "Active courses", "📚", new Color(217, 119, 6)));
        row2.add(createKpiCard("Total Collection", totalCollectionLabel, "Revenue collected", "💰", new Color(5, 150, 105)));
        row2.add(createKpiCard("Pending Fees", pendingFeesLabel, "Outstanding dues", "⏳", new Color(225, 29, 72)));
        body.add(row2);
        body.add(Box.createVerticalStrut(16));

        // ROW 3: Quick Modules + Academic Status
        JPanel row3 = new JPanel(new GridLayout(1, 2, 16, 0));
        row3.setOpaque(false);
        row3.setMaximumSize(new Dimension(Short.MAX_VALUE, 280));

        row3.add(createQuickLauncherCard());
        row3.add(createAcademicOverviewCard());
        body.add(row3);

        return body;
    }

    private JPanel createCardPanel() {
        JPanel panel = new JPanel();
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(COLOR_BORDER, 1, true),
                BorderFactory.createEmptyBorder(14, 16, 14, 16)
        ));
        return panel;
    }

    private JPanel createProfileCard() {
        JPanel card = createCardPanel();
        card.setLayout(new BorderLayout(12, 10));

        JLabel title = new JLabel("Administrator Profile");
        title.setFont(new Font("Segoe UI", Font.BOLD, 14));
        title.setForeground(COLOR_TEXT_DARK);
        card.add(title, BorderLayout.NORTH);

        JPanel details = new JPanel(new GridLayout(3, 1, 0, 4));
        details.setOpaque(false);

        JLabel nameLabel = new JLabel("Admin Authority  (Administrator)");
        nameLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        nameLabel.setForeground(COLOR_PRIMARY);

        JLabel idLabel = new JLabel("ID: ADM-2026-9901  •  admin@feemanager.edu");
        idLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        idLabel.setForeground(COLOR_TEXT_MUTED);

        JLabel deptLabel = new JLabel("Department of Financial Services & Student Accounts");
        deptLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        deptLabel.setForeground(COLOR_TEXT_DARK);

        details.add(nameLabel);
        details.add(idLabel);
        details.add(deptLabel);
        card.add(details, BorderLayout.CENTER);

        return card;
    }

    private JPanel createNoticeCard() {
        JPanel card = createCardPanel();
        card.setLayout(new BorderLayout(0, 8));

        JLabel title = new JLabel("Information Portal & Notices");
        title.setFont(new Font("Segoe UI", Font.BOLD, 14));
        title.setForeground(COLOR_TEXT_DARK);
        card.add(title, BorderLayout.NORTH);

        JPanel list = new JPanel(new GridLayout(2, 1, 0, 6));
        list.setOpaque(false);

        JLabel notice1 = new JLabel("<html><b>• Fee Payment Circular:</b> All semester fee installments must be cleared through administrative counters or official channels.</html>");
        notice1.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        notice1.setForeground(COLOR_TEXT_MUTED);

        JLabel notice2 = new JLabel("<html><b>• Financial Reconciliation:</b> Ensure transaction reference numbers and official payment receipts are properly recorded.</html>");
        notice2.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        notice2.setForeground(COLOR_TEXT_MUTED);

        list.add(notice1);
        list.add(notice2);
        card.add(list, BorderLayout.CENTER);

        return card;
    }

    private JPanel createKpiCard(String title, JLabel valueLabel, String subtitle, String icon, Color accentColor) {
        JPanel card = createCardPanel();
        card.setLayout(new BorderLayout(0, 5));

        JPanel top = new JPanel(new BorderLayout());
        top.setOpaque(false);

        JLabel titleLbl = new JLabel(title);
        titleLbl.setFont(new Font("Segoe UI", Font.BOLD, 12));
        titleLbl.setForeground(COLOR_TEXT_MUTED);
        top.add(titleLbl, BorderLayout.WEST);

        JLabel iconLbl = new JLabel(icon);
        iconLbl.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 16));
        top.add(iconLbl, BorderLayout.EAST);
        card.add(top, BorderLayout.NORTH);

        valueLabel.setFont(new Font("Segoe UI", Font.BOLD, 19));
        valueLabel.setForeground(accentColor);
        card.add(valueLabel, BorderLayout.CENTER);

        JLabel subLbl = new JLabel(subtitle);
        subLbl.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        subLbl.setForeground(COLOR_TEXT_MUTED);
        card.add(subLbl, BorderLayout.SOUTH);

        return card;
    }

    private JPanel createQuickLauncherCard() {
        JPanel card = createCardPanel();
        card.setLayout(new BorderLayout(0, 10));

        JLabel title = new JLabel("Quick Management Modules");
        title.setFont(new Font("Segoe UI", Font.BOLD, 14));
        title.setForeground(COLOR_TEXT_DARK);
        card.add(title, BorderLayout.NORTH);

        JPanel btnGrid = new JPanel(new GridLayout(4, 1, 0, 8));
        btnGrid.setOpaque(false);

        btnGrid.add(createActionRow("Student Management", "Register and manage student profiles", "Open Students", e -> new StudentFrame().setVisible(true)));
        btnGrid.add(createActionRow("Fee Collection Counter", "Collect tuition fees & save payments", "Collect Fee", e -> new FeeFrame().setVisible(true)));
        btnGrid.add(createActionRow("Payment History", "View complete collection logs", "View History", e -> new PaymentHistoryFrame().setVisible(true)));
        btnGrid.add(createActionRow("Official Fee Receipt", "Print official payment receipts", "Print Receipt", e -> new ReceiptFrame().setVisible(true)));

        card.add(btnGrid, BorderLayout.CENTER);
        return card;
    }

    private JPanel createActionRow(String title, String subtitle, String btnText, ActionListener action) {
        JPanel row = new JPanel(new BorderLayout(10, 0));
        row.setOpaque(false);
        row.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(241, 245, 249)));

        JPanel textPanel = new JPanel(new GridLayout(2, 1));
        textPanel.setOpaque(false);

        JLabel t = new JLabel(title);
        t.setFont(new Font("Segoe UI", Font.BOLD, 12));
        t.setForeground(COLOR_TEXT_DARK);

        JLabel s = new JLabel(subtitle);
        s.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        s.setForeground(COLOR_TEXT_MUTED);

        textPanel.add(t);
        textPanel.add(s);
        row.add(textPanel, BorderLayout.CENTER);

        JButton btn = new JButton(btnText);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 11));
        btn.setForeground(COLOR_PRIMARY);
        btn.setBackground(COLOR_ACTIVE_BG);
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createEmptyBorder(5, 12, 5, 12));
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.addActionListener(action);
        row.add(btn, BorderLayout.EAST);

        return row;
    }

    private JPanel createAcademicOverviewCard() {
        JPanel card = createCardPanel();
        card.setLayout(new BorderLayout(0, 12));

        JLabel title = new JLabel("Academic & Financial Overview");
        title.setFont(new Font("Segoe UI", Font.BOLD, 14));
        title.setForeground(COLOR_TEXT_DARK);
        card.add(title, BorderLayout.NORTH);

        JPanel content = new JPanel(new GridLayout(4, 1, 0, 8));
        content.setOpaque(false);

        // Status row
        JLabel sessionLbl = new JLabel("• Academic Year: 2025/2026 (Spring Semester)  |  Status: ACTIVE");
        sessionLbl.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        sessionLbl.setForeground(COLOR_TEXT_DARK);
        content.add(sessionLbl);

        // Big Primary Maroon Button for Pending Fees
        JButton pendingBtn = new JButton("Manage Student Dues (Pending Fees)");
        pendingBtn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        pendingBtn.setForeground(Color.WHITE);
        pendingBtn.setBackground(COLOR_PRIMARY);
        pendingBtn.setFocusPainted(false);
        pendingBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        pendingBtn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                pendingBtn.setBackground(COLOR_PRIMARY_HOVER);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                pendingBtn.setBackground(COLOR_PRIMARY);
            }
        });
        pendingBtn.addActionListener(e -> new PendingFeeFrame().setVisible(true));
        content.add(pendingBtn);

        // Course Management button
        JButton courseBtn = new JButton("Course Catalog & Fee Management");
        courseBtn.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        courseBtn.setBackground(Color.WHITE);
        courseBtn.setForeground(COLOR_TEXT_DARK);
        courseBtn.setFocusPainted(false);
        courseBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        courseBtn.addActionListener(e -> new CourseFrame().setVisible(true));
        content.add(courseBtn);

        // User Management button
        JButton userBtn = new JButton("Admin User Accounts");
        userBtn.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        userBtn.setBackground(Color.WHITE);
        userBtn.setForeground(COLOR_TEXT_DARK);
        userBtn.setFocusPainted(false);
        userBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        userBtn.addActionListener(e -> new UserManagementFrame().setVisible(true));
        content.add(userBtn);

        card.add(content, BorderLayout.CENTER);
        return card;
    }

    // =========================================================================
    // 4. DATABASE STATS & LOGOUT
    // =========================================================================
    private void loadDashboardStats() {
        String studentSql = "SELECT COUNT(*) FROM students";
        String courseSql = "SELECT COUNT(*) FROM courses";
        String collectionSql = "SELECT COALESCE(SUM(paid_amount), 0) FROM fees";
        String pendingSql =
                "SELECT COALESCE(SUM(pending_fee), 0) " +
                "FROM (" +
                "SELECT c.total_fee - COALESCE(SUM(f.paid_amount), 0) AS pending_fee " +
                "FROM students s " +
                "JOIN courses c ON s.course_id = c.id " +
                "LEFT JOIN fees f ON s.id = f.student_id " +
                "GROUP BY s.id, c.total_fee " +
                "HAVING pending_fee > 0" +
                ") AS pending_table";

        try (Connection connection = DBConnection.getConnection()) {
            if (connection == null) {
                if (dbStatusLabel != null) {
                    dbStatusLabel.setText("● Database Offline");
                    dbStatusLabel.setForeground(new Color(239, 68, 68));
                }
                return;
            }

            if (dbStatusLabel != null) {
                dbStatusLabel.setText("● Database Connected");
                dbStatusLabel.setForeground(new Color(16, 185, 129));
            }

            // Students
            try (PreparedStatement stmt = connection.prepareStatement(studentSql);
                 ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) totalStudentsLabel.setText(String.format("%,d", rs.getInt(1)));
            }

            // Courses
            try (PreparedStatement stmt = connection.prepareStatement(courseSql);
                 ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) totalCoursesLabel.setText(String.format("%,d", rs.getInt(1)));
            }

            // Total Collection
            try (PreparedStatement stmt = connection.prepareStatement(collectionSql);
                 ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) totalCollectionLabel.setText(String.format("Rs. %,.2f", rs.getDouble(1)));
            }

            // Pending Fees
            try (PreparedStatement stmt = connection.prepareStatement(pendingSql);
                 ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) pendingFeesLabel.setText(String.format("Rs. %,.2f", rs.getDouble(1)));
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Failed to load dashboard statistics!\n\n" + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void logout() {
        int choice = JOptionPane.showConfirmDialog(this, "Are you sure you want to log out?", "Logout Confirmation", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
        if (choice == JOptionPane.YES_OPTION) {
            dispose();
            new LoginFrame().setVisible(true);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException ignored) {
            }
            new DashboardFrame().setVisible(true);
        });
    }
}