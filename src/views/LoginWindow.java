// ملف: views/LoginWindow.java
package views;

import models.*;
import database.DatabaseManager;
import exceptions.HotelManagementException;
import javax.swing.*;
import java.awt.*;

public class LoginWindow extends JFrame {
    private JTextField userIdField;
    private JPasswordField passwordField;
    
    public LoginWindow() {
        setTitle("Hotel Management System - Login");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(400, 250);
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
        JLabel titleLabel = new JLabel("Hotel Management System");
        titleLabel.setFont(new Font("Tahoma", Font.BOLD, 18));
        titlePanel.add(titleLabel);
        
        // Form panel
        JPanel formPanel = new JPanel(new GridLayout(2, 2, 10, 10));
        
        JLabel userLabel = new JLabel("User ID:");
        userLabel.setFont(new Font("Tahoma", Font.PLAIN, 14));
        userIdField = new JTextField();
        
        JLabel passLabel = new JLabel("Password:");
        passLabel.setFont(new Font("Tahoma", Font.PLAIN, 14));
        passwordField = new JPasswordField();
        
        formPanel.add(userLabel);
        formPanel.add(userIdField);
        formPanel.add(passLabel);
        formPanel.add(passwordField);
        
        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        JButton loginButton = new JButton("Login");
        loginButton.setFont(new Font("Tahoma", Font.BOLD, 14));
        loginButton.setBackground(new Color(70, 130, 180));
        loginButton.setForeground(Color.WHITE);
        loginButton.setFocusPainted(false);
        
        loginButton.addActionListener(e -> login());
        
        // Enter key to login
        getRootPane().setDefaultButton(loginButton);
        
        buttonPanel.add(loginButton);
        
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
            User user = DatabaseManager.authenticateUser(userId, password);
            
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
}