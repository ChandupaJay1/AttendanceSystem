package GUI;

import Model.DBConnection;
import Model.ThemeManager;

import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import javax.swing.*;
import javax.swing.border.*;
import java.sql.*;

public class Login extends JFrame {

    public Login() {
        initComponents();
        ThemeManager.initialize();
        setModernLook();
    }

    private void setModernLook() {
        // Set modern font
        Font modernFont = new Font("Segoe UI", Font.PLAIN, 14);
        UIManager.put("Button.font", modernFont);
        UIManager.put("Label.font", modernFont);
        UIManager.put("TextField.font", modernFont);
        UIManager.put("PasswordField.font", modernFont);

        // Set modern colors
        Color accentColor = new Color(0, 120, 215);
        Color backgroundColor = new Color(245, 245, 245);

        jPanel1.setBackground(backgroundColor);
        jButton1.setBackground(accentColor);
        jButton1.setForeground(Color.WHITE);
        jButton1.setFocusPainted(false);
        jButton1.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        // Add hover effect to button
        jButton1.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                jButton1.setBackground(accentColor.darker());
            }

            @Override
            public void mouseExited(MouseEvent e) {
                jButton1.setBackground(accentColor);
            }
        });

        // Modern border for input fields
        Border roundedBorder = new RoundBorder(accentColor, 1, 8);
        usernameField.setBorder(roundedBorder);
        passwordField.setBorder(roundedBorder);

        // Add padding to input fields
        usernameField.setMargin(new Insets(10, 10, 10, 10));
        passwordField.setMargin(new Insets(10, 10, 10, 10));

        // Center window on screen
        setLocationRelativeTo(null);
    }

    private boolean validateLogin(String username, String password) {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pst = conn.prepareStatement("SELECT * FROM admin WHERE username=? AND password=?")) {

            pst.setString(1, username);
            pst.setString(2, password);

            try (ResultSet rs = pst.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Database error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }

    @SuppressWarnings("unchecked")
    private void initComponents() {
        jPanel1 = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;

                // Gradient background
                Color color1 = new Color(240, 240, 240);
                Color color2 = new Color(220, 220, 220);
                GradientPaint gp = new GradientPaint(0, 0, color1, getWidth(), getHeight(), color2);
                g2d.setPaint(gp);
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        jLabel1 = new JLabel();
        jLabel2 = new JLabel();
        passwordField = new JPasswordField();
        usernameField = new JTextField();
        jLabel3 = new JLabel();
        jLabel4 = new JLabel();
        jButton1 = new JButton();

        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setTitle("Attendance System Login");
        setMinimumSize(new Dimension(400, 500));
        setResizable(false);

        jLabel1.setFont(new Font("Segoe UI", Font.BOLD, 24));
        jLabel1.setHorizontalAlignment(SwingConstants.CENTER);
        jLabel1.setText("ATTENDANCE SYSTEM");
        jLabel1.setForeground(new Color(0, 120, 215));

        jLabel2.setHorizontalAlignment(SwingConstants.CENTER);
        jLabel2.setText("Powered By NerdTech LK V1.0");
        jLabel2.setForeground(new Color(100, 100, 100));

        passwordField.setPreferredSize(new Dimension(200, 35));
        passwordField.addActionListener(this::passwordFieldActionPerformed);

        usernameField.setPreferredSize(new Dimension(200, 35));
        usernameField.addActionListener(this::usernameFieldActionPerformed);

        jLabel3.setFont(new Font("Segoe UI", Font.BOLD, 14));
        jLabel3.setText("Password:");
        jLabel3.setForeground(new Color(80, 80, 80));

        jLabel4.setFont(new Font("Segoe UI", Font.BOLD, 14));
        jLabel4.setText("Username:");
        jLabel4.setForeground(new Color(80, 80, 80));

        jButton1.setText("LOGIN");
        jButton1.setPreferredSize(new Dimension(120, 40));
        jButton1.addActionListener(this::jButton1ActionPerformed);

        // Layout using GridBagLayout for better responsiveness
        jPanel1.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;

        // Title
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        jPanel1.add(jLabel1, gbc);

        // Username row
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        gbc.anchor = GridBagConstraints.LINE_END;
        jPanel1.add(jLabel4, gbc);

        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.LINE_START;
        jPanel1.add(usernameField, gbc);

        // Password row
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.anchor = GridBagConstraints.LINE_END;
        jPanel1.add(jLabel3, gbc);

        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.LINE_START;
        jPanel1.add(passwordField, gbc);

        // Button
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        jPanel1.add(jButton1, gbc);

        // Footer
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.anchor = GridBagConstraints.PAGE_END;
        jPanel1.add(jLabel2, gbc);

        // Add panel to frame
        getContentPane().add(jPanel1, BorderLayout.CENTER);
        pack();
    }

    private void usernameFieldActionPerformed(ActionEvent evt) {
        passwordField.requestFocusInWindow();
    }

    private void passwordFieldActionPerformed(ActionEvent evt) {
        jButton1.doClick();
    }

    private void jButton1ActionPerformed(ActionEvent evt) {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword()).trim();

        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter both username and password",
                    "Input Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (validateLogin(username, password)) {
            JFrame frame = new JFrame("Attendance Management System");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setContentPane(new Dashboard());
            frame.pack();
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
            this.dispose();
        } else {
            JOptionPane.showMessageDialog(this, "Invalid username or password",
                    "Login Failed", JOptionPane.ERROR_MESSAGE);
            passwordField.setText("");
            usernameField.requestFocus();
        }
    }

    public static void main(String args[]) {
        ThemeManager.initialize();
        ThemeManager.configureGlobalThemeSettings();

        EventQueue.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception ex) {
                ex.printStackTrace();
            }

            Login login = new Login();
            login.setVisible(true);
        });
    }

    // Custom rounded border class
    private static class RoundBorder extends AbstractBorder {
        private final Color color;
        private final int thickness;
        private final int radius;

        public RoundBorder(Color color, int thickness, int radius) {
            this.color = color;
            this.thickness = thickness;
            this.radius = radius;
        }

        @Override
        public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(color);
            g2.setStroke(new BasicStroke(thickness));
            g2.drawRoundRect(x, y, width-1, height-1, radius, radius);
            g2.dispose();
        }

        @Override
        public Insets getBorderInsets(Component c) {
            return new Insets(radius/2, radius/2, radius/2, radius/2);
        }

        @Override
        public Insets getBorderInsets(Component c, Insets insets) {
            insets.left = insets.right = radius/2;
            insets.top = insets.bottom = radius/2;
            return insets;
        }
    }

    // Variables declaration
    private JButton jButton1;
    private JLabel jLabel1;
    private JLabel jLabel2;
    private JLabel jLabel3;
    private JLabel jLabel4;
    private JPanel jPanel1;
    private JPasswordField passwordField;
    private JTextField usernameField;
}