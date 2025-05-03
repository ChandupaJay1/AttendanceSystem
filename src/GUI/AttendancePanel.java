package GUI;

import Model.DBConnection;
import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class AttendancePanel extends JPanel {
    private JTable attendanceTable;
    private JScrollPane scrollPane;
    private JButton refreshButton;
    private JButton markPresentButton;
    private JButton markAbsentButton;

    public AttendancePanel() {
        initComponents();
        loadAttendanceData();
    }

    private void initComponents() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        // Create table model
        String[] columns = {"Employee ID", "Name", "Department", "Status", "Check In", "Check Out"};
        DefaultTableModel model = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Make table non-editable
            }
        };

        // Create table
        attendanceTable = new JTable(model);
        attendanceTable.setRowHeight(30);
        attendanceTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        attendanceTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        attendanceTable.getColumnModel().getColumn(3).setCellRenderer(new StatusRenderer());

        // Add double-click listener for editing
        attendanceTable.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    editSelectedAttendance();
                }
            }
        });

        scrollPane = new JScrollPane(attendanceTable);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Attendance Records"));

        // Create button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        
        refreshButton = new JButton("Refresh");
        refreshButton.addActionListener(e -> loadAttendanceData());
        
        markPresentButton = new JButton("Mark Present");
        markPresentButton.addActionListener(e -> markSelected("Present"));
        
        markAbsentButton = new JButton("Mark Absent");
        markAbsentButton.addActionListener(e -> markSelected("Absent"));

        buttonPanel.add(refreshButton);
        buttonPanel.add(markPresentButton);
        buttonPanel.add(markAbsentButton);

        // Add components to panel
        add(buttonPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
    }

    public void loadAttendanceData() {
        DefaultTableModel model = (DefaultTableModel) attendanceTable.getModel();
        model.setRowCount(0); // Clear existing data

        String today = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        String query = "SELECT e.employee_id, e.full_name, e.department, " +
                      "a.status, a.check_in, a.check_out " +
                      "FROM employees e " +
                      "LEFT JOIN attendance a ON e.employee_id = a.employee_id AND DATE(a.attendance_date) = ? " +
                      "ORDER BY e.department, e.full_name";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pst = conn.prepareStatement(query)) {
            
            pst.setString(1, today);
            ResultSet rs = pst.executeQuery();

            while (rs.next()) {
                String status = rs.getString("status");
                if (status == null) {
                    status = "Absent"; // Default status if no record exists
                }
                
                String checkIn = rs.getTime("check_in") != null ? rs.getTime("check_in").toString() : "-";
                String checkOut = rs.getTime("check_out") != null ? rs.getTime("check_out").toString() : "-";
                
                model.addRow(new Object[]{
                    rs.getString("employee_id"),
                    rs.getString("full_name"),
                    rs.getString("department"),
                    status,
                    checkIn,
                    checkOut
                });
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error loading attendance data: " + e.getMessage(),
                    "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void editSelectedAttendance() {
        int selectedRow = attendanceTable.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, "Please select an employee to edit", 
                    "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String employeeId = (String) attendanceTable.getValueAt(selectedRow, 0);
        String currentStatus = (String) attendanceTable.getValueAt(selectedRow, 3);
        String currentCheckIn = attendanceTable.getValueAt(selectedRow, 4).toString();
        String currentCheckOut = attendanceTable.getValueAt(selectedRow, 5).toString();

        // Create dialog for editing
        JDialog editDialog = new JDialog((JFrame)SwingUtilities.getWindowAncestor(this), "Edit Attendance", true);
        editDialog.setLayout(new BorderLayout());
        editDialog.setSize(400, 250);
        editDialog.setLocationRelativeTo(this);

        JPanel formPanel = new JPanel(new GridLayout(4, 2, 10, 10));
        formPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        // Status dropdown
        JLabel statusLabel = new JLabel("Status:");
        JComboBox<String> statusCombo = new JComboBox<>(new String[]{"Present", "Absent", "Late", "On Leave"});
        statusCombo.setSelectedItem(currentStatus);

        // Check-in field
        JLabel checkInLabel = new JLabel("Check In (HH:MM:SS):");
        JTextField checkInField = new JTextField(currentCheckIn.equals("-") ? "" : currentCheckIn);

        // Check-out field
        JLabel checkOutLabel = new JLabel("Check Out (HH:MM:SS):");
        JTextField checkOutField = new JTextField(currentCheckOut.equals("-") ? "" : currentCheckOut);

        formPanel.add(statusLabel);
        formPanel.add(statusCombo);
        formPanel.add(checkInLabel);
        formPanel.add(checkInField);
        formPanel.add(checkOutLabel);
        formPanel.add(checkOutField);

        // Save button
        JButton saveButton = new JButton("Save");
        saveButton.addActionListener(e -> {
            try {
                updateAttendance(
                    employeeId,
                    (String) statusCombo.getSelectedItem(),
                    checkInField.getText(),
                    checkOutField.getText()
                );
                editDialog.dispose();
                loadAttendanceData();
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(editDialog, "Error saving attendance: " + ex.getMessage(),
                        "Database Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        editDialog.add(formPanel, BorderLayout.CENTER);
        editDialog.add(saveButton, BorderLayout.SOUTH);
        editDialog.setVisible(true);
    }

    private void markSelected(String status) {
        int selectedRow = attendanceTable.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, "Please select an employee first", 
                    "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String employeeId = (String) attendanceTable.getValueAt(selectedRow, 0);
        
        try {
            updateAttendance(employeeId, status, "", "");
            loadAttendanceData();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error updating attendance: " + e.getMessage(),
                    "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateAttendance(String employeeId, String status, String checkIn, String checkOut) throws SQLException {
        String today = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        String query = "INSERT INTO attendance (employee_id, attendance_date, status, check_in, check_out) " +
                       "VALUES (?, ?, ?, ?, ?) " +
                       "ON DUPLICATE KEY UPDATE status = VALUES(status), " +
                       "check_in = VALUES(check_in), check_out = VALUES(check_out)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pst = conn.prepareStatement(query)) {
            
            pst.setString(1, employeeId);
            pst.setString(2, today);
            pst.setString(3, status);
            
            if (!checkIn.isEmpty()) {
                pst.setTime(4, Time.valueOf(checkIn));
            } else {
                pst.setNull(4, Types.TIME);
            }
            
            if (!checkOut.isEmpty()) {
                pst.setTime(5, Time.valueOf(checkOut));
            } else {
                pst.setNull(5, Types.TIME);
            }
            
            pst.executeUpdate();
        }
    }

    private static class StatusRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                boolean isSelected, boolean hasFocus, int row, int column) {
            
            Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            
            if ("Present".equals(value)) {
                c.setBackground(new Color(200, 255, 200)); // Light green
            } else if ("Absent".equals(value)) {
                c.setBackground(new Color(255, 200, 200)); // Light red
            } else if ("Late".equals(value)) {
                c.setBackground(new Color(255, 255, 150)); // Light yellow
            } else if ("On Leave".equals(value)) {
                c.setBackground(new Color(200, 200, 255)); // Light blue
            }

            if (isSelected) {
                c.setBackground(table.getSelectionBackground());
                c.setForeground(table.getSelectionForeground());
            } else {
                c.setForeground(Color.BLACK);
            }

            return c;
        }
    }
}