package GUI;

import Model.DBConnection;
import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;
import org.krysalis.barcode4j.impl.code128.Code128Bean;
import org.krysalis.barcode4j.output.bitmap.BitmapCanvasProvider;

/**
 *
 * @author Chandupa
 */
public class EmployeePanel extends javax.swing.JPanel {

    private JButton updateButton;
    private JButton saveButton;
    private JButton generateBarcodeButton;
    private String selectedEmployeeId;
    private String selectedEmployeeName;
    private String barcodeSavePath = System.getProperty("user.home") + "/Documents/Barcodes/";

    public EmployeePanel() {
        initComponents();
        initCustomComponents();
        createBarcodeDirectory();
    }

    private void createBarcodeDirectory() {
        File directory = new File(barcodeSavePath);
        if (!directory.exists()) {
            directory.mkdirs();
        }
    }

    private void initCustomComponents() {
        DefaultTableModel model = new DefaultTableModel(
                new Object[][]{},
                new String[]{"ID", "Full Name", "Department", "Address", "Province", "City", "Mobile Number", "Email"}
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        jTable1.setModel(model);

        // Add double-click listener
        jTable1.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    int row = jTable1.getSelectedRow();
                    if (row >= 0) {
                        selectedEmployeeId = jTable1.getValueAt(row, 0).toString();
                        selectedEmployeeName = jTable1.getValueAt(row, 1).toString();
                        showBarcodeOptions();
                    }
                }
            }
        });

        // Create buttons panel
        JPanel buttonPanel = new JPanel();

        updateButton = new JButton("Update Employee");
        updateButton.addActionListener((ActionEvent e) -> {
            updateEmployee();
        });

        saveButton = new JButton("Save Changes");
        saveButton.addActionListener((ActionEvent e) -> {
            saveEmployeeChanges();
        });

        generateBarcodeButton = new JButton("Generate Barcode");
        generateBarcodeButton.addActionListener((ActionEvent e) -> {
            int row = jTable1.getSelectedRow();
            if (row >= 0) {
                selectedEmployeeId = jTable1.getValueAt(row, 0).toString();
                selectedEmployeeName = jTable1.getValueAt(row, 1).toString();
                showBarcodeOptions();
            } else {
                JOptionPane.showMessageDialog(this, "Please select an employee first",
                        "No Selection", JOptionPane.WARNING_MESSAGE);
            }
        });

        buttonPanel.add(updateButton);
        buttonPanel.add(saveButton);
        buttonPanel.add(generateBarcodeButton);

        this.setLayout(new BorderLayout());
        this.add(jScrollPane1, BorderLayout.CENTER);
        this.add(buttonPanel, BorderLayout.SOUTH);

        loadEmployeeData();
    }

    private void showBarcodeOptions() {
        int option = JOptionPane.showConfirmDialog(this,
                "Generate barcode for: " + selectedEmployeeName + "?",
                "Barcode Generation", JOptionPane.YES_NO_OPTION);

        if (option == JOptionPane.YES_OPTION) {
            generateAndSaveBarcode();
        }
    }

    private void generateAndSaveBarcode() {
        try {
            // 1. Generate barcode
            Code128Bean barcodeGenerator = new Code128Bean();
            barcodeGenerator.setHeight(15);
            barcodeGenerator.setModuleWidth(0.3);
            barcodeGenerator.doQuietZone(true);

            // 2. Create image
            BitmapCanvasProvider canvas = new BitmapCanvasProvider(
                    160, BufferedImage.TYPE_BYTE_BINARY, false, 0);
            barcodeGenerator.generateBarcode(canvas, selectedEmployeeId);
            canvas.finish();

            // 3. Save image
            String filename = "emp_" + selectedEmployeeId + "_"
                    + selectedEmployeeName.replaceAll(" ", "_") + ".png";
            File outputFile = new File(barcodeSavePath + filename);
            ImageIO.write(canvas.getBufferedImage(), "png", outputFile);

            // 4. Save to database
            saveBarcodeToDatabase(filename);

            // 5. Show success message
            JOptionPane.showMessageDialog(this,
                    "Barcode generated successfully!\nSaved to: " + outputFile.getAbsolutePath(),
                    "Success", JOptionPane.INFORMATION_MESSAGE);

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error generating barcode: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void saveBarcodeToDatabase(String barcodeFilename) {
        // Updated query to use employee_id instead of id
        String query = "UPDATE employees SET barcode_filename = ? WHERE employee_id = ?";

        try (Connection conn = DBConnection.getConnection(); PreparedStatement pst = conn.prepareStatement(query)) {

            pst.setString(1, barcodeFilename);
            pst.setString(2, selectedEmployeeId);
            int rowsAffected = pst.executeUpdate();

            if (rowsAffected == 0) {
                JOptionPane.showMessageDialog(this,
                        "No employee found with ID: " + selectedEmployeeId,
                        "Update Failed", JOptionPane.WARNING_MESSAGE);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this,
                    "Error saving barcode to database: " + e.getMessage(),
                    "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateEmployee() {
        int row = jTable1.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Please select an employee to update",
                    "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Get employee data from the selected row
        String employeeId = jTable1.getValueAt(row, 0).toString();
        String fullName = jTable1.getValueAt(row, 1).toString();
        String department = jTable1.getValueAt(row, 2).toString();
        String mobile = jTable1.getValueAt(row, 3).toString();
        String email = jTable1.getValueAt(row, 4).toString();

        // Create and configure the dialog
        JDialog editDialog = new JDialog();
        editDialog.setTitle("Edit Employee - " + employeeId);
        editDialog.setModal(true);
        editDialog.setSize(400, 300);
        editDialog.setLayout(new BorderLayout());
        editDialog.setLocationRelativeTo(this);

        // Create form panel
        JPanel formPanel = new JPanel(new GridLayout(5, 2, 10, 10));
        formPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        // Form components
        JLabel lblName = new JLabel("Full Name:");
        JTextField txtName = new JTextField(fullName);

        JLabel lblDept = new JLabel("Department:");
        JTextField txtDept = new JTextField(department);

        JLabel lblMobile = new JLabel("Mobile Number:");
        JTextField txtMobile = new JTextField(mobile);

        JLabel lblEmail = new JLabel("Email:");
        JTextField txtEmail = new JTextField(email);

        JButton btnSave = new JButton("Save Changes");
        JButton btnCancel = new JButton("Cancel");

        // Add components to form
        formPanel.add(lblName);
        formPanel.add(txtName);
        formPanel.add(lblDept);
        formPanel.add(txtDept);
        formPanel.add(lblMobile);
        formPanel.add(txtMobile);
        formPanel.add(lblEmail);
        formPanel.add(txtEmail);
        formPanel.add(btnSave);
        formPanel.add(btnCancel);

        // Button actions
        btnSave.addActionListener(e -> {
            // Save logic here
            try {
                String query = "UPDATE employees SET full_name = ?, department = ?, "
                        + "mobile_number = ?, email = ? WHERE employee_id = ?";

                try (Connection conn = DBConnection.getConnection(); PreparedStatement pst = conn.prepareStatement(query)) {

                    pst.setString(1, txtName.getText());
                    pst.setString(2, txtDept.getText());
                    pst.setString(3, txtMobile.getText());
                    pst.setString(4, txtEmail.getText());
                    pst.setString(5, employeeId);
                    pst.executeUpdate();

                    JOptionPane.showMessageDialog(editDialog,
                            "Employee updated successfully!",
                            "Success", JOptionPane.INFORMATION_MESSAGE);

                    // Refresh the table
                    loadEmployeeData();
                    editDialog.dispose();
                }
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(editDialog,
                        "Error updating employee: " + ex.getMessage(),
                        "Database Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        btnCancel.addActionListener(e -> editDialog.dispose());

        // Add components to dialog
        editDialog.add(formPanel, BorderLayout.CENTER);
        editDialog.setVisible(true);
    }

    private void saveEmployeeChanges() {
        // In a real app, this would save changes from the edit dialog
        JOptionPane.showMessageDialog(this,
                "Would save employee changes to database",
                "Save Changes", JOptionPane.INFORMATION_MESSAGE);
    }

    private void loadEmployeeData() {
        DefaultTableModel model = (DefaultTableModel) jTable1.getModel();
        model.setRowCount(0);

        String query = "SELECT employee_id, full_name, department, address, province, city, mobile_number, email, barcode_filename FROM employees";

        try (Connection conn = DBConnection.getConnection(); PreparedStatement pst = conn.prepareStatement(query); ResultSet rs = pst.executeQuery()) {

            while (rs.next()) {
                model.addRow(new Object[]{
                    rs.getString("employee_id"),
                    rs.getString("full_name"),
                    rs.getString("department"),
                    rs.getString("address"),
                    rs.getString("province"),
                    rs.getString("city"),
                    rs.getString("mobile_number"),
                    rs.getString("email"),
                    rs.getString("barcode_filename")
                });
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error loading employee data: " + e.getMessage(),
                    "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane1 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();

        jTable1.setModel(new javax.swing.table.DefaultTableModel(
                new Object [][] {
                        {null, null, null, null},
                        {null, null, null, null},
                        {null, null, null, null},
                        {null, null, null, null}
                },
                new String [] {
                        "Title 1", "Title 2", "Title 3", "Title 4"
                }
        ));
        jScrollPane1.setViewportView(jTable1);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 750, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 494, Short.MAX_VALUE)
        );
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable jTable1;
    // End of variables declaration//GEN-END:variables
}
