package GUI;

import Model.DBConnection;
import java.awt.*;
import java.sql.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import javax.swing.*;
import javax.swing.table.*;

public class Dashboard extends javax.swing.JPanel {

    private JPanel statsPanel;
    private JButton refreshDashboardBtn;

    public Dashboard() {
        initComponents();
        initDashboardPanel();
        showAttendanceStats();
    }

    private void initDashboardPanel() {
        jPanel4.setLayout(new BorderLayout(10, 10));
        jPanel4.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        // Create stats panel (top)
        statsPanel = new JPanel(new GridLayout(1, 4, 10, 0));
        statsPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 15, 0));

        // Add refresh button
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        refreshDashboardBtn = new JButton("Refresh Dashboard");
        refreshDashboardBtn.addActionListener(e -> {
            showAttendanceStats();
            if (jPanel4.getComponentCount() > 1) {
                Component centerComp = jPanel4.getComponent(1);
                if (centerComp instanceof AttendancePanel) {
                    ((AttendancePanel) centerComp).loadAttendanceData();
                }
            }
        });
        buttonPanel.add(refreshDashboardBtn);

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(statsPanel, BorderLayout.CENTER);
        topPanel.add(buttonPanel, BorderLayout.EAST);
        jPanel4.add(topPanel, BorderLayout.NORTH);

        // Welcome message
        JLabel welcomeLabel = new JLabel("<html><div style='text-align: center;'>"
                + "<h1>Welcome to Attendance System</h1>"
                + "<p>Select an option from the menu to get started</p></div></html>");
        welcomeLabel.setHorizontalAlignment(SwingConstants.CENTER);
        welcomeLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        jPanel4.add(welcomeLabel, BorderLayout.CENTER);
    }

    private void showAttendanceStats() {
        String today = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));

        try (Connection conn = DBConnection.getConnection()) {
            // Total employees
            int totalEmployees = getEmployeeCount(conn);

            // Present employees
            int presentCount = getAttendanceCount(conn, today, "Present");

            // Late employees
            int lateCount = getAttendanceCount(conn, today, "Late");

            // Absent employees
            int absentCount = getAttendanceCount(conn, today, "Absent");

            // Percentage present
            double presentPercentage = totalEmployees > 0
                    ? ((presentCount + lateCount) * 100.0) / totalEmployees : 0;

            // Clear existing stats
            statsPanel.removeAll();

            // Add stat cards
            statsPanel.add(createStatCard("Total Employees",
                    String.valueOf(totalEmployees),
                    new Color(70, 130, 180)));

            statsPanel.add(createStatCard("Present",
                    presentCount + "",
                    new Color(46, 125, 50)));

            statsPanel.add(createStatCard("Late",
                    lateCount + "",
                    new Color(255, 153, 0)));

            statsPanel.add(createStatCard("Absent",
                    absentCount + " (" + String.format("%.1f", 100 - presentPercentage) + "%)",
                    new Color(198, 40, 40)));

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error loading stats: " + e.getMessage(),
                    "Database Error", JOptionPane.ERROR_MESSAGE);
        }

        statsPanel.revalidate();
        statsPanel.repaint();
    }

    private JPanel createStatCard(String title, String value, Color color) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200)),
                BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        titleLabel.setForeground(new Color(100, 100, 100));

        JLabel valueLabel = new JLabel(value);
        valueLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        valueLabel.setForeground(color);
        valueLabel.setHorizontalAlignment(SwingConstants.CENTER);

        card.add(titleLabel, BorderLayout.NORTH);
        card.add(valueLabel, BorderLayout.CENTER);

        return card;
    }

    private int getEmployeeCount(Connection conn) throws SQLException {
        String query = "SELECT COUNT(*) FROM employees";
        try (PreparedStatement pst = conn.prepareStatement(query)) {
            ResultSet rs = pst.executeQuery();
            return rs.next() ? rs.getInt(1) : 0;
        }
    }

    private int getAttendanceCount(Connection conn, String date, String status) throws SQLException {
        String query = "SELECT COUNT(*) FROM attendance WHERE DATE(attendance_date) = ? AND status = ?";
        try (PreparedStatement pst = conn.prepareStatement(query)) {
            pst.setString(1, date);
            pst.setString(2, status);
            ResultSet rs = pst.executeQuery();
            return rs.next() ? rs.getInt(1) : 0;
        }
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">
    private void initComponents() {
        // Main panel with BorderLayout
        jPanel1 = new javax.swing.JPanel();
        jPanel1.setLayout(new BorderLayout());
        jPanel1.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Header Panel (North)
        jPanel2 = new javax.swing.JPanel();
        jPanel2.setLayout(new BorderLayout());
        jPanel2.setBackground(new Color(70, 130, 180));
        jPanel2.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));

        jLabel1 = new javax.swing.JLabel();
        jLabel1.setFont(new Font("Segoe UI", Font.BOLD, 22));
        jLabel1.setForeground(Color.WHITE);
        jLabel1.setText("Attendance Management System");
        jPanel2.add(jLabel1, BorderLayout.CENTER);

        jButton5 = new JButton("Change Theme");
        jButton5.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        jButton5.setBackground(new Color(245, 245, 245));
        jButton5.setFocusPainted(false);
        jButton5.addActionListener(this::jButton5ActionPerformed);
        jPanel2.add(jButton5, BorderLayout.EAST);

        jPanel1.add(jPanel2, BorderLayout.NORTH);

        // Sidebar Panel (West)
        jPanel3 = new javax.swing.JPanel();
        jPanel3.setLayout(new BoxLayout(jPanel3, BoxLayout.Y_AXIS));
        jPanel3.setBackground(new Color(240, 240, 240));
        jPanel3.setBorder(BorderFactory.createEmptyBorder(20, 15, 20, 15));
        jPanel3.setPreferredSize(new Dimension(200, 0));

        // Create menu buttons with consistent styling
        JButton[] menuButtons = {
            createMenuButton("Dashboard", "dashboard.png"),
            createMenuButton("Attendance", "attendance.png"),
            createMenuButton("Employee", "employee.png"),
            createMenuButton("Logout", "logout.png")
        };

        menuButtons[0].addActionListener(this::jButton1ActionPerformed);
        menuButtons[1].addActionListener(this::jButton2ActionPerformed);
        menuButtons[2].addActionListener(this::jButton3ActionPerformed);
        menuButtons[3].addActionListener(this::jButton4ActionPerformed);

        // Add buttons with spacing
        for (JButton button : menuButtons) {
            jPanel3.add(button);
            jPanel3.add(Box.createRigidArea(new Dimension(0, 15)));
        }

        // Add flexible space at the bottom
        jPanel3.add(Box.createVerticalGlue());
        jPanel1.add(jPanel3, BorderLayout.WEST);

        // Content Panel (Center)
        jPanel4 = new javax.swing.JPanel();
        jPanel4.setLayout(new BorderLayout());
        jPanel4.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        jPanel1.add(jPanel4, BorderLayout.CENTER);

        // Set the main layout
        this.setLayout(new BorderLayout());
        this.add(jPanel1, BorderLayout.CENTER);

        // Set minimum size
        this.setMinimumSize(new Dimension(800, 600));
    }

    private JButton createMenuButton(String text, String iconName) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        button.setAlignmentX(Component.LEFT_ALIGNMENT);
        button.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        button.setBackground(new Color(240, 240, 240));
        button.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        button.setHorizontalAlignment(SwingConstants.LEFT);
        button.setFocusPainted(false);

        try {
            ImageIcon icon = new ImageIcon(getClass().getResource("/icons/" + iconName));
            button.setIcon(icon);
            button.setIconTextGap(10);
        } catch (Exception e) {
            // Icon not found
        }

        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(220, 220, 220));
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(240, 240, 240));
            }
        });

        return button;
    }

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {
        jPanel4.removeAll();

        // Create and add stats panel if not already present
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(statsPanel, BorderLayout.CENTER);

        // Add refresh button
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.add(refreshDashboardBtn);
        topPanel.add(buttonPanel, BorderLayout.EAST);

        jPanel4.add(topPanel, BorderLayout.NORTH);

        // Create and add AttendancePanel
        AttendancePanel attendancePanel = new AttendancePanel();
        jPanel4.add(attendancePanel, BorderLayout.CENTER);

        jPanel4.revalidate();
        jPanel4.repaint();
    }

    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {
        jPanel4.removeAll();

        EmployeePanel employeePanel = new EmployeePanel();
        jPanel4.add(employeePanel, BorderLayout.CENTER);

        jPanel4.revalidate();
        jPanel4.repaint();
    }

    private void jButton4ActionPerformed(java.awt.event.ActionEvent evt) {
        JFrame login = new Login();
        login.setVisible(true);
        JFrame topFrame = (JFrame) SwingUtilities.getWindowAncestor(this);
        if (topFrame != null) {
            topFrame.dispose();
        }
    }

    private void jButton5ActionPerformed(java.awt.event.ActionEvent evt) {
        JFrame topFrame = (JFrame) SwingUtilities.getWindowAncestor(jButton5);
        new ThemeSelectorDialog(topFrame, true).setVisible(true);
    }

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {
        jPanel4.removeAll();

        // Recreate the stats panel and top panel
        statsPanel.removeAll();
        showAttendanceStats(); // This will repopulate the statsPanel

        // Recreate the top panel with stats and refresh button
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(statsPanel, BorderLayout.CENTER);

        // Add refresh button
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.add(refreshDashboardBtn);
        topPanel.add(buttonPanel, BorderLayout.EAST);

        jPanel4.add(topPanel, BorderLayout.NORTH);

        // Add welcome message
        JLabel welcomeLabel = new JLabel("<html><div style='text-align: center;'>"
                + "<h1>Welcome to Dashboard</h1>"
                + "<p>Select an option from the menu to get started</p></div></html>");
        welcomeLabel.setHorizontalAlignment(SwingConstants.CENTER);
        welcomeLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        jPanel4.add(welcomeLabel, BorderLayout.CENTER);

        jPanel4.revalidate();
        jPanel4.repaint();
    }

    // Variables declaration
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JButton jButton4;
    private javax.swing.JButton jButton5;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    // End of variables declaration
}
