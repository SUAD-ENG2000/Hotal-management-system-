// ملف: views/ManagerDashboard.java
package views;

import models.*;
import database.DatabaseManager;
import exceptions.HotelManagementException;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class ManagerDashboard extends JFrame {
    private Manager manager;
    
    public ManagerDashboard(Manager manager) {
        this.manager = manager;
        setTitle("Hotel Management System - Manager Dashboard");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1000, 700);
        setLocationRelativeTo(null);
        
        initComponents();
    }
    
    private void initComponents() {
        // Main panel with BorderLayout
        JPanel mainPanel = new JPanel(new BorderLayout());
        
        // Header panel
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(70, 130, 180));
        headerPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        
        JLabel welcomeLabel = new JLabel("Welcome, Manager!");
        welcomeLabel.setFont(new Font("Tahoma", Font.BOLD, 20));
        welcomeLabel.setForeground(Color.WHITE);
        
        JLabel userLabel = new JLabel("User: " + manager.getUserId());
        userLabel.setFont(new Font("Tahoma", Font.PLAIN, 14));
        userLabel.setForeground(Color.WHITE);
        
        headerPanel.add(welcomeLabel, BorderLayout.WEST);
        headerPanel.add(userLabel, BorderLayout.EAST);
        
        // Toolbar panel
        JPanel toolbarPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        toolbarPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        String[] buttonLabels = {
            "Add Room", "Remove Room", "View All Rooms",
            "View All Bookings", "View All Bills", "Logout"
        };
        
        for (String label : buttonLabels) {
            JButton button = new JButton(label);
            button.setFont(new Font("Tahoma", Font.PLAIN, 12));
            button.addActionListener(e -> handleButtonClick(label));
            toolbarPanel.add(button);
        }
        
        // Content panel (card layout for different views)
        JPanel contentPanel = new JPanel(new CardLayout());
        contentPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Default welcome panel
        JPanel welcomePanel = new JPanel(new BorderLayout());
        JLabel defaultLabel = new JLabel("Select an option from the toolbar to begin", 
            SwingConstants.CENTER);
        defaultLabel.setFont(new Font("Tahoma", Font.PLAIN, 16));
        welcomePanel.add(defaultLabel, BorderLayout.CENTER);
        
        contentPanel.add(welcomePanel, "WELCOME");
        
        // Add panels to main panel
        mainPanel.add(headerPanel, BorderLayout.NORTH);
        mainPanel.add(toolbarPanel, BorderLayout.CENTER);
        mainPanel.add(contentPanel, BorderLayout.SOUTH);
        
        add(mainPanel);
    }
    
    private void handleButtonClick(String action) {
        try {
            switch (action) {
                case "Add Room":
                    showAddRoomDialog();
                    break;
                case "Remove Room":
                    showRemoveRoomDialog();
                    break;
                case "View All Rooms":
                    showAllRooms();
                    break;
                case "View All Bookings":
                    showAllBookings();
                    break;
                case "View All Bills":
                    showAllBills();
                    break;
                case "Logout":
                    logout();
                    break;
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, 
                "Error: " + e.getMessage(), 
                "Operation Failed", 
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void showAddRoomDialog() {
        JDialog dialog = new JDialog(this, "Add New Room", true);
        dialog.setSize(400, 300);
        dialog.setLocationRelativeTo(this);
        
        JPanel panel = new JPanel(new GridLayout(5, 2, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        panel.add(new JLabel("Room Number:"));
        JTextField roomNumberField = new JTextField();
        panel.add(roomNumberField);
        
        panel.add(new JLabel("Room Type:"));
        String[] types = {"Single", "Double", "Suite", "Deluxe"};
        JComboBox<String> typeCombo = new JComboBox<>(types);
        panel.add(typeCombo);
        
        panel.add(new JLabel("Price per Night ($):"));
        JTextField priceField = new JTextField();
        panel.add(priceField);
        
        // Empty row for spacing
        panel.add(new JLabel());
        panel.add(new JLabel());
        
        JButton saveButton = new JButton("Save");
        saveButton.addActionListener(e -> {
            try {
                Room room = new Room(
                    roomNumberField.getText().trim(),
                    (String) typeCombo.getSelectedItem(),
                    Double.parseDouble(priceField.getText().trim()),
                    true
                );
                DatabaseManager.addRoom(room);
                JOptionPane.showMessageDialog(dialog, "Room added successfully!");
                dialog.dispose();
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(dialog, 
                    "Please enter a valid price", 
                    "Input Error", 
                    JOptionPane.ERROR_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog, 
                    "Error: " + ex.getMessage(), 
                    "Save Failed", 
                    JOptionPane.ERROR_MESSAGE);
            }
        });
        
        JButton cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(e -> dialog.dispose());
        
        panel.add(saveButton);
        panel.add(cancelButton);
        
        dialog.add(panel);
        dialog.setVisible(true);
    }
    
    private void showRemoveRoomDialog() {
        String roomNumber = JOptionPane.showInputDialog(this, 
            "Enter room number to remove:", 
            "Remove Room", 
            JOptionPane.QUESTION_MESSAGE);
        
        if (roomNumber != null && !roomNumber.trim().isEmpty()) {
            int confirm = JOptionPane.showConfirmDialog(this, 
                "Are you sure you want to remove room " + roomNumber + "?", 
                "Confirm Removal", 
                JOptionPane.YES_NO_OPTION);
            
            if (confirm == JOptionPane.YES_OPTION) {
                try {
                    DatabaseManager.removeRoom(roomNumber.trim());
                    JOptionPane.showMessageDialog(this, "Room removed successfully!");
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(this, 
                        "Error: " + e.getMessage(), 
                        "Removal Failed", 
                        JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    }
    
    private void showAllRooms() {
        try {
            List<Room> rooms = DatabaseManager.getAllRooms();
            
            String[] columns = {"Room #", "Type", "Price/Night", "Available"};
            Object[][] data = new Object[rooms.size()][4];
            
            for (int i = 0; i < rooms.size(); i++) {
                Room room = rooms.get(i);
                data[i][0] = room.getRoomNumber();
                data[i][1] = room.getRoomType();
                data[i][2] = String.format("$%.2f", room.getPricePerNight());
                data[i][3] = room.isAvailable() ? "Yes" : "No";
            }
            
            DefaultTableModel model = new DefaultTableModel(data, columns) {
                @Override
                public boolean isCellEditable(int row, int column) {
                    return false;
                }
            };
            
            JTable table = new JTable(model);
            table.setFillsViewportHeight(true);
            table.setRowHeight(25);
            
            JScrollPane scrollPane = new JScrollPane(table);
            scrollPane.setPreferredSize(new Dimension(800, 400));
            
            JDialog dialog = new JDialog(this, "All Rooms", true);
            dialog.setSize(850, 450);
            dialog.setLocationRelativeTo(this);
            dialog.add(scrollPane);
            dialog.setVisible(true);
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, 
                "Error: " + e.getMessage(), 
                "Load Failed", 
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void showAllBookings() {
        try {
            List<Booking> bookings = DatabaseManager.getAllBookings();
            
            String[] columns = {"Booking ID", "Customer", "Room", "Check-in", "Check-out", "Nights", "Status"};
            Object[][] data = new Object[bookings.size()][7];
            
            for (int i = 0; i < bookings.size(); i++) {
                Booking booking = bookings.get(i);
                data[i][0] = booking.getBookingId();
                data[i][1] = booking.getCustomerName();
                data[i][2] = booking.getRoomNumber();
                data[i][3] = booking.getCheckInDate().toString();
                data[i][4] = booking.getCheckOutDate().toString();
                data[i][5] = booking.calculateNights();
                data[i][6] = booking.isActive() ? "Active" : "Inactive";
            }
            
            DefaultTableModel model = new DefaultTableModel(data, columns);
            JTable table = new JTable(model);
            
            JScrollPane scrollPane = new JScrollPane(table);
            scrollPane.setPreferredSize(new Dimension(900, 400));
            
            JDialog dialog = new JDialog(this, "All Bookings", true);
            dialog.setSize(950, 450);
            dialog.setLocationRelativeTo(this);
            dialog.add(scrollPane);
            dialog.setVisible(true);
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, 
                "Error: " + e.getMessage(), 
                "Load Failed", 
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void showAllBills() {
        try {
            List<Bill> bills = DatabaseManager.getAllBills();
            
            String[] columns = {"Bill ID", "Booking ID", "Amount", "Generated Date", "Status"};
            Object[][] data = new Object[bills.size()][5];
            
            for (int i = 0; i < bills.size(); i++) {
                Bill bill = bills.get(i);
                data[i][0] = bill.getBillId();
                data[i][1] = bill.getBookingId();
                data[i][2] = String.format("$%.2f", bill.getTotalAmount());
                data[i][3] = bill.getGeneratedDate().toLocalDate().toString();
                data[i][4] = bill.isPaid() ? "Paid" : "Unpaid";
            }
            
            DefaultTableModel model = new DefaultTableModel(data, columns);
            JTable table = new JTable(model);
            
            // Calculate totals
            double totalPaid = bills.stream()
                .filter(Bill::isPaid)
                .mapToDouble(Bill::getTotalAmount)
                .sum();
            
            double totalUnpaid = bills.stream()
                .filter(bill -> !bill.isPaid())
                .mapToDouble(Bill::getTotalAmount)
                .sum();
            
            JPanel summaryPanel = new JPanel(new GridLayout(2, 2, 10, 5));
            summaryPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
            summaryPanel.add(new JLabel("Total Bills:"));
            summaryPanel.add(new JLabel(String.valueOf(bills.size())));
            summaryPanel.add(new JLabel("Total Paid:"));
            summaryPanel.add(new JLabel(String.format("$%.2f", totalPaid)));
            summaryPanel.add(new JLabel("Total Unpaid:"));
            summaryPanel.add(new JLabel(String.format("$%.2f", totalUnpaid)));
            
            JDialog dialog = new JDialog(this, "All Bills", true);
            dialog.setSize(900, 500);
            dialog.setLocationRelativeTo(this);
            dialog.setLayout(new BorderLayout());
            
            dialog.add(new JScrollPane(table), BorderLayout.CENTER);
            dialog.add(summaryPanel, BorderLayout.SOUTH);
            dialog.setVisible(true);
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, 
                "Error: " + e.getMessage(), 
                "Load Failed", 
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void logout() {
        int confirm = JOptionPane.showConfirmDialog(this, 
            "Are you sure you want to logout?", 
            "Confirm Logout", 
            JOptionPane.YES_NO_OPTION);
        
        if (confirm == JOptionPane.YES_OPTION) {
            dispose();
            new LoginWindow().setVisible(true);
        }
    }
}