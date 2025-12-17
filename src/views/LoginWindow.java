package views;

import models.*;
import service.UserService;
import exceptions.HotelManagementException;
import javax.swing.*;
import java.awt.*;

public class LoginWindow extends JFrame {
    private JTextField userIdField;
    private JPasswordField passwordField;
    private UserService userService;
    
    public LoginWindow() {
        this.userService = new UserService();
        setTitle("Hotel Management System - Login");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(400, 300);
        setLocationRelativeTo(null);
        setResizable(false);
        
        initComponents();
    }
    
    private void initComponents() {
        // Main panel with padding
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Title panel
        JPanel titlePanel = new JPanel();
        JLabel titleLabel = new JLabel("🏨 Hotel Management System");
        titleLabel.setFont(new Font("Tahoma", Font.BOLD, 22));
        titleLabel.setForeground(new Color(70, 130, 180));
        titlePanel.add(titleLabel);
        
        // Form panel
        JPanel formPanel = new JPanel(new GridLayout(3, 2, 10, 15));
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 10, 20, 10));
        
        JLabel userLabel = new JLabel("👤 User ID:");
        userLabel.setFont(new Font("Tahoma", Font.PLAIN, 14));
        userIdField = new JTextField();
        
        JLabel passLabel = new JLabel("🔒 Password:");
        passLabel.setFont(new Font("Tahoma", Font.PLAIN, 14));
        passwordField = new JPasswordField();
        
        JLabel roleLabel = new JLabel("🎭 Role:");
        roleLabel.setFont(new Font("Tahoma", Font.PLAIN, 14));
        
        JComboBox<String> roleCombo = new JComboBox<>(new String[]{"Manager", "Receptionist"});
        
        formPanel.add(userLabel);
        formPanel.add(userIdField);
        formPanel.add(passLabel);
        formPanel.add(passwordField);
        formPanel.add(roleLabel);
        formPanel.add(roleCombo);
        
        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 0));
        JButton loginButton = new JButton("🔑 Login");
        loginButton.setFont(new Font("Tahoma", Font.BOLD, 12));
        loginButton.setBackground(new Color(70, 130, 180));
        loginButton.setForeground(Color.black);
        loginButton.setFocusPainted(false);
        loginButton.setPreferredSize(new Dimension(120, 35));
        
        JButton resetButton = new JButton("🔄 Reset");
        resetButton.setFont(new Font("Tahoma", Font.PLAIN, 12));
        resetButton.setBackground(Color.LIGHT_GRAY);
        resetButton.setPreferredSize(new Dimension(100, 30));
        
        loginButton.addActionListener(e -> login());
        resetButton.addActionListener(e -> {
            userIdField.setText("");
            passwordField.setText("");
        });
        
        // Enter key to login
        getRootPane().setDefaultButton(loginButton);
        
        buttonPanel.add(loginButton);
        buttonPanel.add(resetButton);
        
        // Footer with version info
        JPanel footerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JLabel versionLabel = new JLabel("Version 2.0 | 4-Layer Architecture | © 2024");
        versionLabel.setFont(new Font("Tahoma", Font.PLAIN, 10));
        versionLabel.setForeground(Color.GRAY);
        footerPanel.add(versionLabel);
        
        // Add panels to main panel
        mainPanel.add(titlePanel, BorderLayout.NORTH);
        mainPanel.add(formPanel, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        add(mainPanel);
    }
    
    private void login() {
        String userId = userIdField.getText().trim();
        String password = new String(passwordField.getPassword());
        
        if (userId.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, 
                "Please enter both user ID and password", 
                "Validation Error", 
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        try {
            User user = userService.authenticateUser(userId, password);
            
            if (user instanceof models.Manager) {
                dispose();
                new ManagerDashboard((models.Manager) user).setVisible(true);
            } else if (user instanceof models.Receptionist) {
                dispose();
                new ReceptionistDashboard((models.Receptionist) user).setVisible(true);
            }
            
        } catch (HotelManagementException ex) {
            JOptionPane.showMessageDialog(this, 
                "Login failed: " + ex.getMessage(), 
                "Authentication Error", 
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    public static void main(String[] args) {
        // Set look and feel
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        // Run the application
        SwingUtilities.invokeLater(() -> {
            LoginWindow loginWindow = new LoginWindow();
            loginWindow.setVisible(true);
        });
    }
}