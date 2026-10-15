package com.feemanager.login;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import javax.imageio.ImageIO;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.Normalizer.Form;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.text.JTextComponent;

import com.feemanager.DBConnection;
import com.feemanager.dashboard.DashboardFrame;

/**
 * LoginFrame
 *
 * Modern login screen:
 *  - Left side  : maroon panel (logo + school name)
 *  - Right side : white panel (username, password, captcha checkbox, login button)
 */
public class LoginFrame extends JFrame {

    // ---------- Colors ----------
    private static final Color MAROON    = new Color(0x9B, 0x2C, 0x2F);
    private static final Color MAROON_DK = new Color(0x7A, 0x1F, 0x24);
    private static final Color GOLD      = new Color(0xE8, 0xB4, 0x4C);
    private static final Color INPUT_BG  = new Color(0xF4, 0xF1, 0xF0);
    private static final Color TEXT_DARK = new Color(0x2B, 0x2B, 0x2B);
    private static final Color TEXT_GRAY = new Color(0x77, 0x77, 0x77);

    // ---------- Fonts ----------
    private static final Font FONT_BOLD  = new Font("Segoe UI", Font.BOLD, 13);
    private static final Font FONT_PLAIN = new Font("Segoe UI", Font.PLAIN, 13);
    private static final Font FONT_SMALL = new Font("Segoe UI", Font.PLAIN, 11);

    // ---------- Input fields ----------
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton loginButton;

    public LoginFrame() {
        setTitle("Fee Management System - Login");
        setSize(900, 560);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        createUI();
    }

    // =====================================================================
    //  UI
    // =====================================================================

    private void createUI() {
        JPanel root = new JPanel(new BorderLayout());
        root.add(createLeftPanel(), BorderLayout.WEST);
        root.add(createRightPanel(), BorderLayout.CENTER);
        setContentPane(root);
    }

    /** Left maroon panel with decorative circles, logo and school name. */
    private JPanel createLeftPanel() {

        JPanel left = new JPanel(new GridBagLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                        RenderingHints.VALUE_ANTIALIAS_ON);

                int w = getWidth();
                int h = getHeight();

                // Background
                g2.setColor(MAROON);
                g2.fillRect(0, 0, w, h);

                // Soft dark circle (middle-left)
                g2.setColor(MAROON_DK);
                g2.fillOval(-120, h - 260, 260, 260);

                // Top-left decorative circles
                g2.setColor(new Color(0xF0, 0x9A, 0x2A));
                g2.fillOval(-35, -35, 110, 110);
                g2.setColor(GOLD);
                g2.fillOval(25, -20, 60, 60);
                g2.setColor(new Color(0xFF, 0xF3, 0xD6));
                g2.fillOval(-10, 40, 40, 40);

                // Bottom-right decorative circles
                g2.setColor(new Color(0xF0, 0x9A, 0x2A));
                g2.fillOval(w - 70, h - 70, 110, 110);
                g2.setColor(GOLD);
                g2.fillOval(w - 100, h - 35, 55, 55);

                g2.dispose();
            }
        };
        left.setPreferredSize(new Dimension(400, 0));

        // Logo + school name (row)
        JPanel brandRow = new JPanel(new FlowLayout(FlowLayout.CENTER, 12, 0));
        brandRow.setOpaque(false);

        brandRow.add(new LogoComponent(52));

        JLabel schoolName = new JLabel(
            "<html>College of Management Studies</html>");
        schoolName.setFont(new Font("Segoe UI", Font.BOLD, 20));
        schoolName.setForeground(Color.WHITE);
        brandRow.add(schoolName);

        left.add(brandRow);
        return left;
    }

    /** Right white panel with the login form. */
    private JPanel createRightPanel() {

        JPanel right = new JPanel(new GridBagLayout());
        right.setBackground(Color.WHITE);

        JPanel form = new JPanel();
        form.setOpaque(false);
        form.setLayout(new BoxLayout(form, BoxLayout.Y_AXIS));
        form.setPreferredSize(new Dimension(330, 440));

        // ----- Header: small logo + welcome text -----
        LogoComponent smallLogo = new LogoComponent(40);
        smallLogo.setAlignmentX(Component.CENTER_ALIGNMENT);
        form.add(smallLogo);
        form.add(Box.createVerticalStrut(8));

        JLabel welcome = new JLabel("Welcome to");
        welcome.setFont(FONT_PLAIN);
        welcome.setForeground(TEXT_DARK);
        welcome.setAlignmentX(Component.CENTER_ALIGNMENT);
        form.add(welcome);

        JLabel portal = new JLabel("Fee Management System");
        portal.setFont(new Font("Segoe UI", Font.BOLD, 14));
        portal.setForeground(MAROON);
        portal.setAlignmentX(Component.CENTER_ALIGNMENT);
        form.add(portal);

        form.add(Box.createVerticalStrut(28));

        // ----- Username -----
        form.add(leftAligned(boldLabel("Username")));
        form.add(Box.createVerticalStrut(6));

        usernameField = new JTextField();
        form.add(leftAligned(createInputBox(usernameField, null)));
        form.add(Box.createVerticalStrut(16));

        // ----- Password row (label + Forgot Password?) -----
        JPanel passwordRow = new JPanel(new BorderLayout());
        passwordRow.setOpaque(false);
        passwordRow.add(boldLabel("Password"), BorderLayout.WEST);

        JLabel forgot = new JLabel("Forgot Password?");
        forgot.setFont(new Font("Segoe UI", Font.BOLD, 11));
        forgot.setForeground(MAROON);
        forgot.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        forgot.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                JOptionPane.showMessageDialog(
                        LoginFrame.this,
                        "Please contact the administrator to reset your password.",
                        "Forgot Password",
                        JOptionPane.INFORMATION_MESSAGE);
            }
        });
        passwordRow.add(forgot, BorderLayout.EAST);
        passwordRow.setMaximumSize(new Dimension(330, 20));
        form.add(leftAligned(passwordRow));
        form.add(Box.createVerticalStrut(6));

        // ----- Password field + show/hide toggle -----
        passwordField = new JPasswordField();
        passwordField.setEchoChar('\u2022');

        JLabel toggle = new JLabel("Show");
        toggle.setFont(FONT_SMALL);
        toggle.setForeground(TEXT_GRAY);
        toggle.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        toggle.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (passwordField.getEchoChar() == (char) 0) {
                    passwordField.setEchoChar('\u2022');
                    toggle.setText("Show");
                } else {
                    passwordField.setEchoChar((char) 0);
                    toggle.setText("Hide");
                }
            }
        });
        form.add(leftAligned(createInputBox(passwordField, toggle)));
        form.add(Box.createVerticalStrut(24));

        // ----- Login button -----
        loginButton = new RoundedButton("Login");
        loginButton.setPreferredSize(new Dimension(120, 38));
        loginButton.setMaximumSize(new Dimension(120, 38));
        loginButton.addActionListener(e -> loginUser());
        form.add(leftAligned(loginButton));

        // Login on press ENTER
        passwordField.addActionListener(e -> loginUser());
        usernameField.addActionListener(e -> passwordField.requestFocus());
        getRootPane().setDefaultButton(loginButton);

        right.add(form);
        return right;
    }

    // =====================================================================
    //  Small UI helpers
    // =====================================================================

    private JLabel boldLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(FONT_BOLD);
        label.setForeground(TEXT_DARK);
        return label;
    }

    private JComponent leftAligned(JComponent c) {
        c.setAlignmentX(Component.LEFT_ALIGNMENT);
        return c;
    }

    /** Rounded light-gray input box that wraps a text field (+ optional right component). */
    private JPanel createInputBox(JTextComponent field, JComponent rightComp) {
        field.setOpaque(false);
        field.setBorder(BorderFactory.createEmptyBorder());
        field.setFont(FONT_PLAIN);
        field.setForeground(TEXT_DARK);
        field.setCaretColor(MAROON);

        RoundedPanel box = new RoundedPanel(12, INPUT_BG);
        box.setLayout(new BorderLayout(8, 0));
        box.setBorder(BorderFactory.createEmptyBorder(0, 14, 0, 14));
        box.add(field, BorderLayout.CENTER);
        if (rightComp != null) {
            box.add(rightComp, BorderLayout.EAST);
        }
        box.setPreferredSize(new Dimension(330, 44));
        box.setMaximumSize(new Dimension(330, 44));
        return box;
    }

    // ---------------------------------------------------------------------
    //  Custom components
    // ---------------------------------------------------------------------

    /** Panel with rounded corners and optional border. */
    private static class RoundedPanel extends JPanel {
        private final int radius;
        private final Color fill;
        private Color borderColor;

        RoundedPanel(int radius, Color fill) {
            this.radius = radius;
            this.fill = fill;
            setOpaque(false);
        }

        void setBorderColor(Color c) {
            this.borderColor = c;
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(fill);
            g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, radius, radius);
            if (borderColor != null) {
                g2.setColor(borderColor);
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, radius, radius);
            }
            g2.dispose();
            super.paintComponent(g);
        }
    }

    /** Maroon rounded button with hover effect. */
    private static class RoundedButton extends JButton {
        private boolean hover = false;

        RoundedButton(String text) {
            super(text);
            setFont(new Font("Segoe UI", Font.BOLD, 13));
            setForeground(Color.WHITE);
            setContentAreaFilled(false);
            setFocusPainted(false);
            setBorderPainted(false);
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            addMouseListener(new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent e) {
                    hover = true;
                    repaint();
                }

                @Override
                public void mouseExited(MouseEvent e) {
                    hover = false;
                    repaint();
                }
            });
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(hover ? MAROON_DK : MAROON);
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
            g2.dispose();
            super.paintComponent(g);
        }
    }

    /**
     * Logo image loader that searches both classpath and disk paths,
     * ensuring cmsLogo.png is always found and scaled smoothly.
     */
    private static BufferedImage loadLogoImage(int targetSize) {
        BufferedImage raw = null;

        // 1. Try Classpath Resource via Class and ClassLoader
        try {
            java.net.URL url = LoginFrame.class.getResource("/com/feemanager/resources/cmsLogo.png");
            if (url == null) {
                url = LoginFrame.class.getResource("/cmsLogo.png");
            }
            if (url == null && LoginFrame.class.getClassLoader() != null) {
                url = LoginFrame.class.getClassLoader().getResource("com/feemanager/resources/cmsLogo.png");
            }
            if (url != null) {
                raw = ImageIO.read(url);
            }
        } catch (Exception ignored) {
        }

        // 2. Try Direct File Paths on Disk
        if (raw == null) {
            String[] possiblePaths = {
                "src/main/java/com/feemanager/resources/cmsLogo.png",
                "src/main/resources/com/feemanager/resources/cmsLogo.png",
                "src/main/resources/cmsLogo.png",
                "resources/cmsLogo.png",
                "cmsLogo.png"
            };

            for (String path : possiblePaths) {
                File file = new File(path);
                if (file.exists()) {
                    try {
                        raw = ImageIO.read(file);
                        if (raw != null) {
                            // Ensure resource copy in src/main/resources for standard packaging
                            try {
                                File resDir = new File("src/main/resources/com/feemanager/resources");
                                if (!resDir.exists()) resDir.mkdirs();
                                File target = new File(resDir, "cmsLogo.png");
                                if (!target.exists()) {
                                    Files.copy(file.toPath(), target.toPath(), StandardCopyOption.REPLACE_EXISTING);
                                }
                            } catch (Exception ignored) {
                            }
                            break;
                        }
                    } catch (Exception ignored) {
                    }
                }
            }
        }

        // 3. Try CodeSource protection domain relative path (for IDE build output)
        if (raw == null) {
            try {
                File codeLocation = new File(LoginFrame.class.getProtectionDomain().getCodeSource().getLocation().toURI());
                File dir = codeLocation.isDirectory() ? codeLocation : codeLocation.getParentFile();
                while (dir != null) {
                    File candidate = new File(dir, "src/main/java/com/feemanager/resources/cmsLogo.png");
                    if (candidate.exists()) {
                        raw = ImageIO.read(candidate);
                        break;
                    }
                    dir = dir.getParentFile();
                }
            } catch (Exception ignored) {
            }
        }

        if (raw == null) {
            return null;
        }

        // Scale to target size with high quality
        BufferedImage scaled = new BufferedImage(targetSize, targetSize, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = scaled.createGraphics();
        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.drawImage(raw, 0, 0, targetSize, targetSize, null);
        g2.dispose();

        return scaled;
    }

    /**
     * Logo Component that renders the CMS official badge.
     */
    private static class LogoComponent extends JComponent {
        private final int size;
        private final BufferedImage image;

        LogoComponent(int size) {
            this.size = size;
            setPreferredSize(new Dimension(size, size));
            setMinimumSize(new Dimension(size, size));
            setMaximumSize(new Dimension(size, size));
            this.image = loadLogoImage(size);
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON);

            if (image != null) {
                // Circular white background for crisp contrast
                g2.setColor(Color.WHITE);
                g2.fillOval(0, 0, size, size);
                g2.drawImage(image, 0, 0, this);
            } else {
                g2.setColor(GOLD);
                g2.fillOval(0, 0, size - 1, size - 1);
                g2.setColor(Color.WHITE);
                g2.fillOval(4, 4, size - 9, size - 9);
                g2.setColor(MAROON);
                g2.setFont(new Font("Serif", Font.BOLD, size / 2));
                FontMetrics fm = g2.getFontMetrics();
                String cms = "CMS";
                g2.drawString(cms,
                        (size - fm.stringWidth(cms)) / 2,
                        (size - fm.getHeight()) / 2 + fm.getAscent());
            }
            g2.dispose();
        }
    }
    //  Login logic

     // User login validation.
     
    private void loginUser() {

        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());

        // Empty fields check
        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(
                    this,
                    "Enter Username and Password.",
                    "Login Error",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        // PreparedStatement => SQL Injection se protection
        String sql = "SELECT * FROM users WHERE username = ? AND password = ?";

        try (
                Connection connection = DBConnection.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, username);
            statement.setString(2, password);

            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {

                JOptionPane.showMessageDialog(
                        this,
                        "Login Successful!",
                        "Success",
                        JOptionPane.INFORMATION_MESSAGE);

                // Login window close karke Dashboard open karna
                dispose();
                DashboardFrame dashboardFrame = new DashboardFrame();
                dashboardFrame.setVisible(true);

            } else {

                JOptionPane.showMessageDialog(
                        this,
                        "Invalid Username or Password!",
                        "Login Failed",
                        JOptionPane.ERROR_MESSAGE);

                passwordField.setText("");
            }

        } catch (Exception e) {

            JOptionPane.showMessageDialog(
                    this,
                    "Database Error:\n" + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Application start hone par Login screen show karega.
     */
    public static void main(String[] args) {

        SwingUtilities.invokeLater(() -> {
            LoginFrame loginFrame = new LoginFrame();
            loginFrame.setVisible(true);
        });
    }
}