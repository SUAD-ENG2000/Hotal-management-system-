// ملف: views/ReceptionistDashboard.java
package views;

import models.*;
import database.DatabaseManager;
import exceptions.HotelManagementException;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.util.List;

public class ReceptionistDashboard extends JFrame {
    private Receptionist receptionist;
    
    public ReceptionistDashboard(Receptionist receptionist) {
        this.receptionist = receptionist;
        setTitle("Hotel Management System - Receptionist Dashboard");
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
        headerPanel.setBackground(new Color(60, 179, 113)); // Different color for receptionist
        headerPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        
        JLabel welcomeLabel = new JLabel("Welcome, Receptionist!");
        welcomeLabel.setFont(new Font("Tahoma", Font.BOLD, 20));
        welcomeLabel.setForeground(Color.WHITE);
        
        JLabel userLabel = new JLabel("User: " + receptionist.getUserId());
        userLabel.setFont(new Font("Tahoma", Font.PLAIN, 14));
        userLabel.setForeground(Color.WHITE);
        
        headerPanel.add(welcomeLabel, BorderLayout.WEST);
        headerPanel.add(userLabel, BorderLayout.EAST);
        
        // Toolbar panel with more options for receptionist
        JPanel toolbarPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        toolbarPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        String[] buttonLabels = {
            "View Rooms", "Find Available Room", "Create Booking",
            "Generate Bill", "Mark Bill Paid", "View Unpaid Bills", "Logout"
        };
        
        for (String label : buttonLabels) {
            JButton button = new JButton(label);
            button.setFont(new Font("Tahoma", Font.PLAIN, 12));
            button.addActionListener(e -> handleButtonClick(label));
            toolbarPanel.add(button);
        }
        
        // Status panel at bottom
        JPanel statusPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        statusPanel.setBorder(BorderFactory.createTitledBorder("Quick Stats"));
        
        try {
            List<Room> rooms = DatabaseManager.getAllRooms();
            long availableRooms = rooms.stream().filter(Room::isAvailable).count();
            
            List<Booking> bookings = DatabaseManager.getAllBookings();
            long activeBookings = bookings.stream().filter(Booking::isActive).count();
            
            List<Bill> bills = DatabaseManager.getAllBills();
            long unpaidBills = bills.stream().filter(bill -> !bill.isPaid()).count();
            
            JLabel statsLabel = new JLabel(String.format(
                "Available Rooms: %d | Active Bookings: %d | Unpaid Bills: %d",
                availableRooms, activeBookings, unpaidBills
            ));
            statsLabel.setFont(new Font("Tahoma", Font.PLAIN, 12));
            statusPanel.add(statsLabel);
            
        } catch (Exception e) {
            JLabel errorLabel = new JLabel("Could not load statistics");
            statusPanel.add(errorLabel);
        }
        
        // Add panels to main panel
        mainPanel.add(headerPanel, BorderLayout.NORTH);
        mainPanel.add(toolbarPanel, BorderLayout.CENTER);
        mainPanel.add(statusPanel, BorderLayout.SOUTH);
        
        add(mainPanel);
    }
    
    private void handleButtonClick(String action) {
        try {
            switch (action) {
                case "View Rooms":
                    showAllRooms();
                    break;
                case "Find Available Room":
                    findAvailableRoom();
                    break;
                case "Create Booking":
                    createBooking();
                    break;
                case "Generate Bill":
                    generateBill();
                    break;
                case "Mark Bill Paid":
                    markBillAsPaid();
                    break;
                case "View Unpaid Bills":
                    viewUnpaidBills();
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
                data[i][3] = room.isAvailable() ? "✅ Available" : "❌ Booked";
            }
            
            DefaultTableModel model = new DefaultTableModel(data, columns);
            JTable table = new JTable(model);
            
            JScrollPane scrollPane = new JScrollPane(table);
            
            JDialog dialog = new JDialog(this, "All Rooms", true);
            dialog.setSize(600, 400);
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
    
    private void findAvailableRoom() {
        String[] roomTypes = {"Single", "Double", "Suite", "Deluxe"};
        String selectedType = (String) JOptionPane.showInputDialog(this,
            "Select room type:", "Find Available Room",
            JOptionPane.QUESTION_MESSAGE, null, roomTypes, roomTypes[0]);
        
        if (selectedType != null) {
            try {
                Room room = DatabaseManager.findAvailableRoom(selectedType);
                
                if (room != null) {
                    String message = String.format(
                        "✅ Available Room Found!\n\n" +
                        "Room Number: %s\n" +
                        "Room Type: %s\n" +
                        "Price per Night: $%.2f",
                        room.getRoomNumber(), room.getRoomType(), room.getPricePerNight()
                    );
                    
                    JOptionPane.showMessageDialog(this, 
                        message, 
                        "Room Found", 
                        JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(this, 
                        "❌ No available rooms of type: " + selectedType, 
                        "No Rooms Available", 
                        JOptionPane.WARNING_MESSAGE);
                }
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, 
                    "Error: " + e.getMessage(), 
                    "Search Failed", 
                    JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void createBooking() {
        JDialog dialog = new JDialog(this, "Create New Booking", true);
        dialog.setSize(500, 400);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout());
        
        JPanel formPanel = new JPanel(new GridLayout(5, 2, 10, 10));
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        JTextField customerNameField = new JTextField();
        JTextField roomNumberField = new JTextField();
        JTextField checkInField = new JTextField(LocalDate.now().toString());
        JTextField checkOutField = new JTextField(LocalDate.now().plusDays(1).toString());
        
        formPanel.add(new JLabel("Customer Name:"));
        formPanel.add(customerNameField);
        formPanel.add(new JLabel("Room Number:"));
        formPanel.add(roomNumberField);
        formPanel.add(new JLabel("Check-in Date (YYYY-MM-DD):"));
        formPanel.add(checkInField);
        formPanel.add(new JLabel("Check-out Date (YYYY-MM-DD):"));
        formPanel.add(checkOutField);
        
        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        JButton createButton = new JButton("Create Booking");
        createButton.setBackground(new Color(60, 179, 113));
        createButton.setForeground(Color.WHITE);
        
        createButton.addActionListener(e -> {
            try {
                String bookingId = "BOOK_" + System.currentTimeMillis();
                Booking booking = new Booking(
                    bookingId,
                    customerNameField.getText().trim(),
                    roomNumberField.getText().trim(),
                    LocalDate.parse(checkInField.getText().trim()),
                    LocalDate.parse(checkOutField.getText().trim())
                );
                
                if (!booking.areDatesValid()) {
                    JOptionPane.showMessageDialog(dialog, 
                        "Invalid dates. Check-out must be after check-in and check-in cannot be in the past.", 
                        "Validation Error", 
                        JOptionPane.ERROR_MESSAGE);
                    return;
                }
                
                DatabaseManager.createBooking(booking);
                
                String successMessage = String.format(
                    "✅ Booking Created Successfully!\n\n" +
                    "Booking ID: %s\n" +
                    "Customer: %s\n" +
                    "Room: %s\n" +
                    "Check-in: %s\n" +
                    "Check-out: %s\n" +
                    "Total Nights: %d",
                    bookingId,
                    customerNameField.getText().trim(),
                    roomNumberField.getText().trim(),
                    checkInField.getText().trim(),
                    checkOutField.getText().trim(),
                    booking.calculateNights()
                );
                
                JOptionPane.showMessageDialog(dialog, successMessage, "Success", JOptionPane.INFORMATION_MESSAGE);
                dialog.dispose();
                
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog, 
                    "Error: " + ex.getMessage(), 
                    "Creation Failed", 
                    JOptionPane.ERROR_MESSAGE);
            }
        });
        
        JButton cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(e -> dialog.dispose());
        
        buttonPanel.add(createButton);
        buttonPanel.add(cancelButton);
        
        dialog.add(formPanel, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }
    
    private void generateBill() {
        try {
            List<Booking> bookings = DatabaseManager.getAllBookings();
            List<Booking> activeBookings = bookings.stream()
                .filter(Booking::isActive)
                .toList();
            
            if (activeBookings.isEmpty()) {
                JOptionPane.showMessageDialog(this, 
                    "No active bookings found.", 
                    "No Bookings", 
                    JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            String[] bookingOptions = activeBookings.stream()
                .map(b -> String.format("%s - %s (Room: %s)", 
                    b.getBookingId(), b.getCustomerName(), b.getRoomNumber()))
                .toArray(String[]::new);
            
            String selected = (String) JOptionPane.showInputDialog(this,
                "Select booking to generate bill:", "Generate Bill",
                JOptionPane.QUESTION_MESSAGE, null, bookingOptions, bookingOptions[0]);
            
            if (selected != null) {
                String bookingId = selected.split(" - ")[0];
                Booking selectedBooking = activeBookings.stream()
                    .filter(b -> b.getBookingId().equals(bookingId))
                    .findFirst()
                    .orElse(null);
                
                if (selectedBooking != null) {
                    // Check if bill already exists
                    List<Bill> existingBills = DatabaseManager.getAllBills();
                    boolean billExists = existingBills.stream()
                        .anyMatch(bill -> bill.getBookingId().equals(bookingId));
                    
                    if (billExists) {
                        JOptionPane.showMessageDialog(this, 
                            "A bill already exists for this booking.", 
                            "Duplicate Bill", 
                            JOptionPane.WARNING_MESSAGE);
                        return;
                    }
                    
                    // Confirm generation
                    int confirm = JOptionPane.showConfirmDialog(this,
                        String.format("Generate bill for booking %s?\nCustomer: %s\nRoom: %s", 
                            bookingId, selectedBooking.getCustomerName(), selectedBooking.getRoomNumber()),
                        "Confirm Bill Generation",
                        JOptionPane.YES_NO_OPTION);
                    
                    if (confirm == JOptionPane.YES_OPTION) {
                        Bill bill = DatabaseManager.generateBill(selectedBooking);
                        
                        String billMessage = String.format(
                            "✅ Bill Generated Successfully!\n\n" +
                            "Bill ID: %s\n" +
                            "Booking ID: %s\n" +
                            "Total Amount: $%.2f\n" +
                            "Generated Date: %s\n" +
                            "Status: %s",
                            bill.getBillId(),
                            bill.getBookingId(),
                            bill.getTotalAmount(),
                            bill.getGeneratedDate().toLocalDate(),
                            bill.isPaid() ? "Paid" : "Unpaid"
                        );
                        
                        JOptionPane.showMessageDialog(this, billMessage, "Bill Generated", JOptionPane.INFORMATION_MESSAGE);
                    }
                }
            }
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, 
                "Error: " + e.getMessage(), 
                "Generation Failed", 
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void markBillAsPaid() {
        try {
            List<Bill> bills = DatabaseManager.getAllBills();
            List<Bill> unpaidBills = bills.stream()
                .filter(bill -> !bill.isPaid())
                .toList();
            
            if (unpaidBills.isEmpty()) {
                JOptionPane.showMessageDialog(this, 
                    "All bills are already paid.", 
                    "No Unpaid Bills", 
                    JOptionPane.INFORMATION_MESSAGE);
                return;
            }
            
            String[] billOptions = unpaidBills.stream()
                .map(b -> String.format("%s - $%.2f (Booking: %s)", 
                    b.getBillId(), b.getTotalAmount(), b.getBookingId()))
                .toArray(String[]::new);
            
            String selected = (String) JOptionPane.showInputDialog(this,
                "Select bill to mark as paid:", "Mark Bill as Paid",
                JOptionPane.QUESTION_MESSAGE, null, billOptions, billOptions[0]);
            
            if (selected != null) {
                String billId = selected.split(" - ")[0];
                
                int confirm = JOptionPane.showConfirmDialog(this,
                    "Mark bill " + billId + " as paid?",
                    "Confirm Payment",
                    JOptionPane.YES_NO_OPTION);
                
                if (confirm == JOptionPane.YES_OPTION) {
                    DatabaseManager.updateBillStatus(billId, true);
                    JOptionPane.showMessageDialog(this, 
                        "Bill marked as paid successfully!", 
                        "Payment Recorded", 
                        JOptionPane.INFORMATION_MESSAGE);
                }
            }
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, 
                "Error: " + e.getMessage(), 
                "Update Failed", 
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void viewUnpaidBills() {
        try {
            List<Bill> bills = DatabaseManager.getAllBills();
            List<Bill> unpaidBills = bills.stream()
                .filter(bill -> !bill.isPaid())
                .toList();
            
            if (unpaidBills.isEmpty()) {
                JOptionPane.showMessageDialog(this, 
                    "✅ All bills are paid!", 
                    "No Unpaid Bills", 
                    JOptionPane.INFORMATION_MESSAGE);
                return;
            }
            
            String[] columns = {"Bill ID", "Booking ID", "Amount", "Generated Date"};
            Object[][] data = new Object[unpaidBills.size()][4];
            
            double totalAmount = 0;
            for (int i = 0; i < unpaidBills.size(); i++) {
                Bill bill = unpaidBills.get(i);
                data[i][0] = bill.getBillId();
                data[i][1] = bill.getBookingId();
                data[i][2] = String.format("$%.2f", bill.getTotalAmount());
                data[i][3] = bill.getGeneratedDate().toLocalDate().toString();
                totalAmount += bill.getTotalAmount();
            }
            
            DefaultTableModel model = new DefaultTableModel(data, columns);
            JTable table = new JTable(model);
            
            JPanel summaryPanel = new JPanel(new GridLayout(1, 2, 10, 5));
            summaryPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
            summaryPanel.add(new JLabel("Total Unpaid Bills:"));
            summaryPanel.add(new JLabel(String.valueOf(unpaidBills.size())));
            summaryPanel.add(new JLabel("Total Amount Due:"));
            summaryPanel.add(new JLabel(String.format("$%.2f", totalAmount)));
            
            JDialog dialog = new JDialog(this, "Unpaid Bills", true);
            dialog.setSize(700, 400);
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