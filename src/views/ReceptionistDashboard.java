package views;

import models.Receptionist;
import models.*;
import service.*;
import exceptions.HotelManagementException;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.util.List;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.format.DateTimeFormatter;
import java.util.Map;

public class ReceptionistDashboard extends JFrame {
    private Receptionist receptionist;
    private RoomService roomService;
    private BookingService bookingService;
    private BillService billService;
    private StatisticsService statisticsService;
    private ReportService reportService;
    private UserService userService;
    
    private JPanel[] statCards;
    private JPanel currentDashboardPanel;
    private JButton logoutBtn;
    
    private DefaultTableModel roomTableModel;
    private DefaultTableModel bookingTableModel;
    private DefaultTableModel billingTableModel;
    
    public ReceptionistDashboard(Receptionist receptionist) {
        this.receptionist = receptionist;
        this.roomService = new RoomService();
        this.bookingService = new BookingService();
        this.billService = new BillService();
        this.statisticsService = new StatisticsService();
        this.reportService = new ReportService();
        this.userService = new UserService();
        
        setTitle("Hotel Management System - Receptionist Dashboard");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1200, 800);
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
        
        JLabel welcomeLabel = new JLabel("🏨 Hotel Management System - Receptionist Dashboard");
        welcomeLabel.setFont(new Font("Tahoma", Font.BOLD, 20));
        welcomeLabel.setForeground(Color.WHITE);
        
        JLabel userLabel = new JLabel("👤 User: " + receptionist.getUserId() + " | 📅 " + 
            LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
        userLabel.setFont(new Font("Tahoma", Font.PLAIN, 14));
        userLabel.setForeground(Color.WHITE);
        
        logoutBtn = new JButton("🚪 Logout");
        logoutBtn.setBackground(Color.RED);
        logoutBtn.setForeground(Color.black);
        logoutBtn.addActionListener(e -> logout());
        
        JPanel userPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        userPanel.setOpaque(false);
        userPanel.add(userLabel);
        userPanel.add(Box.createHorizontalStrut(20));
        userPanel.add(logoutBtn);
        
        headerPanel.add(welcomeLabel, BorderLayout.WEST);
        headerPanel.add(userPanel, BorderLayout.EAST);
        
        // Main content panel with tabbed pane
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("Tahoma", Font.BOLD, 12));
        
        // Tab 1: Quick Dashboard
        tabbedPane.addTab("🏠 Quick Dashboard", createDashboardPanel());
        
        // Tab 2: Room Management
        tabbedPane.addTab("🛏️ Room Management", createRoomManagementPanel());
        
        // Tab 3: Booking Management
        tabbedPane.addTab("📅 Booking Management", createBookingPanel());
        
        // Tab 4: Billing
        tabbedPane.addTab("💰 Billing", createBillingPanel());
        
        // Tab 5: Reports
        tabbedPane.addTab("📊 Reports", createReportsPanel());
        
        // Add components to main panel
        mainPanel.add(headerPanel, BorderLayout.NORTH);
        mainPanel.add(tabbedPane, BorderLayout.CENTER);
        
        add(mainPanel);
    }
    
    private JPanel createDashboardPanel() {
        JPanel dashboardPanel = new JPanel(new BorderLayout(10, 10));
        dashboardPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Top panel with welcome message and refresh button
        JPanel topPanel = new JPanel(new BorderLayout());
        JPanel welcomePanel = new JPanel(new BorderLayout());
        
        JLabel welcomeLabel = new JLabel("👋 Welcome, " + receptionist.getUserId() + "!");
        welcomeLabel.setFont(new Font("Tahoma", Font.BOLD, 24));
        welcomeLabel.setForeground(new Color(60, 179, 113));
        
        JLabel roleLabel = new JLabel("Front Desk Receptionist");
        roleLabel.setFont(new Font("Tahoma", Font.PLAIN, 16));
        roleLabel.setForeground(Color.GRAY);
        
        JButton refreshDashboardBtn = new JButton("🔄 Refresh Dashboard");
        refreshDashboardBtn.addActionListener(e -> refreshDashboard());
        
        welcomePanel.add(welcomeLabel, BorderLayout.NORTH);
        welcomePanel.add(roleLabel, BorderLayout.SOUTH);
        
        topPanel.add(welcomePanel, BorderLayout.WEST);
        topPanel.add(refreshDashboardBtn, BorderLayout.EAST);
        
        // Quick stats panel
        JPanel statsPanel = new JPanel(new GridLayout(2, 3, 15, 15));
        statsPanel.setBorder(BorderFactory.createTitledBorder("📈 Quick Stats"));
        
        // Create 6 statistic cards
        statCards = new JPanel[6];
        String[] statTitles = {
            "Available Rooms", "Active Bookings", 
            "Today's Check-ins", "Today's Check-outs",
            "Unpaid Bills", "Total Revenue"
        };
        
        for (int i = 0; i < 6; i++) {
            statCards[i] = createStatCard(statTitles[i], "0", "Loading...", getCardColor(i));
            statsPanel.add(statCards[i]);
        }
        
        // Quick actions panel
      
        
     
        
        // Recent activity panel
        JPanel activityPanel = new JPanel(new BorderLayout());
        activityPanel.setBorder(BorderFactory.createTitledBorder("📋 Recent Activity"));
        
        String[] columns = {"Time", "Activity", "User", "Details"};
        DefaultTableModel activityModel = new DefaultTableModel(columns, 0);
        JTable activityTable = new JTable(activityModel);
        activityTable.setRowHeight(30);
        JScrollPane scrollPane = new JScrollPane(activityTable);
        
        
        // Assemble dashboard
        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.add(statsPanel, BorderLayout.NORTH);
     
        
        dashboardPanel.add(topPanel, BorderLayout.NORTH);
        dashboardPanel.add(centerPanel, BorderLayout.CENTER);
        dashboardPanel.add(activityPanel, BorderLayout.SOUTH);
        
        // Load initial statistics
        refreshDashboardStats();
        
        return dashboardPanel;
    }
    
    private JPanel createStatCard(String title, String value, String subtitle, Color color) {
        JPanel card = new JPanel(new BorderLayout(5, 5));
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(color.darker(), 2),
            BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));
        card.setBackground(new Color(250, 250, 250));
        
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Tahoma", Font.BOLD, 14));
        titleLabel.setForeground(color.darker());
        
        JLabel valueLabel = new JLabel(value);
        valueLabel.setFont(new Font("Tahoma", Font.BOLD, 28));
        valueLabel.setForeground(color);
        
        JLabel subtitleLabel = new JLabel(subtitle);
        subtitleLabel.setFont(new Font("Tahoma", Font.PLAIN, 12));
        subtitleLabel.setForeground(Color.GRAY);
        
        card.add(titleLabel, BorderLayout.NORTH);
        card.add(valueLabel, BorderLayout.CENTER);
        card.add(subtitleLabel, BorderLayout.SOUTH);
        
        return card;
    }
    
    private Color getCardColor(int index) {
        Color[] colors = {
            new Color(60, 179, 113),   // Medium Sea Green
            new Color(70, 130, 180),   // Steel Blue
            new Color(255, 140, 0),    // Dark Orange
            new Color(186, 85, 211),   // Medium Orchid
            new Color(220, 20, 60),    // Crimson
            new Color(30, 144, 255)    // Dodger Blue
        };
        return colors[index % colors.length];
    }
    
    private void refreshDashboardStats() {
        try {
            // Get statistics
            int availableRooms = roomService.getAvailableRoomsCount();
            int activeBookings = bookingService.getActiveBookingCount();
            double totalRevenue = billService.getTotalRevenue();
            double unpaidAmount = billService.getTotalUnpaidAmount();
            
            // Get today's activities
            List<Booking> todayCheckIns = bookingService.getTodayCheckIns();
            int todayCheckInsCount = todayCheckIns != null ? todayCheckIns.size() : 0;
            
            // Get today's check-outs (approximation)
            LocalDate today = LocalDate.now();
            List<Booking> activeBookingsList = bookingService.getActiveBookings();
            int todayCheckOutsCount = 0;
            if (activeBookingsList != null) {
                for (Booking booking : activeBookingsList) {
                    if (booking.getCheckOutDate().equals(today)) {
                        todayCheckOutsCount++;
                    }
                }
            }
            
            // Update stat cards
            updateStatCard(statCards[0], String.valueOf(availableRooms), "Rooms ready for booking");
            updateStatCard(statCards[1], String.valueOf(activeBookings), "Current guests");
            updateStatCard(statCards[2], String.valueOf(todayCheckInsCount), "Guests arriving today");
            updateStatCard(statCards[3], String.valueOf(todayCheckOutsCount), "Guests departing today");
            updateStatCard(statCards[4], "$" + String.format("%.2f", unpaidAmount), "Pending payments");
            updateStatCard(statCards[5], "$" + String.format("%.2f", totalRevenue), "Total collected");
            
        } catch (HotelManagementException e) {
            JOptionPane.showMessageDialog(this, 
                "Error loading dashboard: " + e.getMessage(), 
                "Dashboard Error", 
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void updateStatCard(JPanel card, String value, String subtitle) {
        JLabel valueLabel = (JLabel) ((BorderLayout) card.getLayout()).getLayoutComponent(BorderLayout.CENTER);
        JLabel subtitleLabel = (JLabel) ((BorderLayout) card.getLayout()).getLayoutComponent(BorderLayout.SOUTH);
        
        valueLabel.setText(value);
        subtitleLabel.setText(subtitle);
    }
    
    private JPanel createRoomManagementPanel() {
        JPanel roomPanel = new JPanel(new BorderLayout(10, 10));
        roomPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Top panel with buttons
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        
        JButton viewAllRoomsBtn = new JButton("👀 View All Rooms");
        JButton findRoomBtn = new JButton("🔍 Find Available Room");
        JButton refreshBtn = new JButton("🔄 Refresh");
        
        viewAllRoomsBtn.addActionListener(e -> viewAllRooms());
        findRoomBtn.addActionListener(e -> findAvailableRoom());
        refreshBtn.addActionListener(e -> refreshRoomTable());
        
        topPanel.add(viewAllRoomsBtn);
        topPanel.add(findRoomBtn);
        topPanel.add(refreshBtn);
        
        // Room table
        String[] columns = {"Room #", "Type", "Price/Night", "Status"};
        roomTableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        JTable roomTable = new JTable(roomTableModel);
        roomTable.setRowHeight(35);
        
        JScrollPane scrollPane = new JScrollPane(roomTable);
        
        // Load room data
        loadRoomData();
        
        roomPanel.add(topPanel, BorderLayout.NORTH);
        roomPanel.add(scrollPane, BorderLayout.CENTER);
        
        return roomPanel;
    }
    
    private void loadRoomData() {
        try {
            List<Room> rooms = roomService.getAllRooms();
            roomTableModel.setRowCount(0); // Clear existing data
            
            for (Room room : rooms) {
                String status = room.isAvailable() ? "✅ Available" : "❌ Occupied";
                Object[] row = {
                    room.getRoomNumber(),
                    room.getRoomType(),
                    String.format("$%.2f", room.getPricePerNight()),
                    status
                };
                roomTableModel.addRow(row);
            }
        } catch (HotelManagementException e) {
            JOptionPane.showMessageDialog(this, 
                "Error loading rooms: " + e.getMessage(), 
                "Load Error", 
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private JPanel createBookingPanel() {
        JPanel bookingPanel = new JPanel(new BorderLayout(10, 10));
        bookingPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Top panel with buttons
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        
        JButton createBookingBtn = new JButton("➕ Create New Booking");
        JButton viewBookingsBtn = new JButton("📋 View All Bookings");
        JButton cancelBookingBtn = new JButton("❌ Cancel Booking");
        JButton refreshBtn = new JButton("🔄 Refresh");
        
        createBookingBtn.addActionListener(e -> createBooking());
        viewBookingsBtn.addActionListener(e -> viewAllBookings());
        cancelBookingBtn.addActionListener(e -> cancelBooking());
        refreshBtn.addActionListener(e -> refreshBookingTable());
        
        topPanel.add(createBookingBtn);
        topPanel.add(viewBookingsBtn);
        topPanel.add(cancelBookingBtn);
        topPanel.add(refreshBtn);
        
        // Booking table
        String[] columns = {"Booking ID", "Customer", "Room", "Check-in", "Check-out", "Nights", "Status"};
        bookingTableModel = new DefaultTableModel(columns, 0);
        
        JTable bookingTable = new JTable(bookingTableModel);
        bookingTable.setRowHeight(30);
        JScrollPane scrollPane = new JScrollPane(bookingTable);
        
        // Load booking data
        loadBookingData();
        
        bookingPanel.add(topPanel, BorderLayout.NORTH);
        bookingPanel.add(scrollPane, BorderLayout.CENTER);
        
        return bookingPanel;
    }
    
    private void loadBookingData() {
        try {
            List<Booking> bookings = bookingService.getAllBookings();
            bookingTableModel.setRowCount(0);
            
            for (Booking booking : bookings) {
                Object[] row = {
                    booking.getBookingId(),
                    booking.getCustomerName(),
                    booking.getRoomNumber(),
                    booking.getCheckInDate().toString(),
                    booking.getCheckOutDate().toString(),
                    booking.calculateNights(),
                    booking.isActive() ? "✅ Active" : "❌ Cancelled"
                };
                bookingTableModel.addRow(row);
            }
        } catch (HotelManagementException e) {
            JOptionPane.showMessageDialog(this, 
                "Error loading bookings: " + e.getMessage(), 
                "Load Error", 
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private JPanel createBillingPanel() {
        JPanel billingPanel = new JPanel(new BorderLayout(10, 10));
        billingPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Top panel with buttons
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        
        JButton generateBillBtn = new JButton("🧾 Generate Bill");
        JButton markPaidBtn = new JButton("💵 Mark as Paid");
        JButton viewBillsBtn = new JButton("📋 View All Bills");
        JButton refreshBtn = new JButton("🔄 Refresh");
        
        generateBillBtn.addActionListener(e -> generateBill());
        markPaidBtn.addActionListener(e -> markBillAsPaid());
        viewBillsBtn.addActionListener(e -> viewAllBills());
        refreshBtn.addActionListener(e -> refreshBillingTable());
        
        topPanel.add(generateBillBtn);
        topPanel.add(markPaidBtn);
        topPanel.add(viewBillsBtn);
        topPanel.add(refreshBtn);
        
        // Billing table
        String[] columns = {"Bill ID", "Booking ID", "Amount", "Generated Date", "Status"};
        billingTableModel = new DefaultTableModel(columns, 0);
        
        JTable billingTable = new JTable(billingTableModel);
        billingTable.setRowHeight(30);
        JScrollPane scrollPane = new JScrollPane(billingTable);
        
        // Load billing data
        loadBillingData();
        
        billingPanel.add(topPanel, BorderLayout.NORTH);
        billingPanel.add(scrollPane, BorderLayout.CENTER);
        
        return billingPanel;
    }
    
    private void loadBillingData() {
        try {
            List<Bill> bills = billService.getAllBills();
            billingTableModel.setRowCount(0);
            
            for (Bill bill : bills) {
                Object[] row = {
                    bill.getBillId(),
                    bill.getBookingId(),
                    String.format("$%.2f", bill.getTotalAmount()),
                    bill.getGeneratedDate().toLocalDate().toString(),
                    bill.isPaid() ? "✅ Paid" : "❌ Unpaid"
                };
                billingTableModel.addRow(row);
            }
        } catch (HotelManagementException e) {
            JOptionPane.showMessageDialog(this, 
                "Error loading bills: " + e.getMessage(), 
                "Load Error", 
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private JPanel createReportsPanel() {
        JPanel reportsPanel = new JPanel(new BorderLayout(10, 10));
        reportsPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        JLabel titleLabel = new JLabel("📊 Receptionist Reports Center");
        titleLabel.setFont(new Font("Tahoma", Font.BOLD, 18));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));
        
        // Report buttons panel
        JPanel buttonsPanel = new JPanel(new GridLayout(2, 2, 15, 15));
        
        String[] reports = {
            "📋 Daily Activity Report", 
            "💰 Today's Revenue Report", 
            "🛏️ Room Status Report",
            "👥 Customer Check-in/out Report"
        };
        
        String[] descriptions = {
            "Today's bookings, check-ins, and check-outs",
            "Revenue generated today",
            "Current room availability status",
            "Today's customer movements"
        };
        
        for (int i = 0; i < reports.length; i++) {
            JPanel reportCard = createReportCard(reports[i], descriptions[i], i);
            buttonsPanel.add(reportCard);
        }
        
        reportsPanel.add(titleLabel, BorderLayout.NORTH);
        reportsPanel.add(buttonsPanel, BorderLayout.CENTER);
        
        return reportsPanel;
    }
    
    private JPanel createReportCard(String title, String description, int index) {
        JPanel card = new JPanel(new BorderLayout(5, 5));
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(getCardColor(index), 1),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        card.setBackground(new Color(245, 245, 245));
        
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Tahoma", Font.BOLD, 12));
        
        JLabel descLabel = new JLabel("<html><body style='width: 150px'>" + description + "</body></html>");
        descLabel.setFont(new Font("Tahoma", Font.PLAIN, 10));
        descLabel.setForeground(Color.GRAY);
        
        JButton generateBtn = new JButton("Generate");
        generateBtn.setBackground(getCardColor(index));
        generateBtn.setForeground(Color.black);
        
        generateBtn.addActionListener(e -> {
            switch (index) {
                case 0: generateDailyReport(); break;
                case 1: generateRevenueReport(); break;
                case 2: generateRoomStatusReport(); break;
                case 3: generateCustomerReport(); break;
            }
        });
        
        card.add(titleLabel, BorderLayout.NORTH);
        card.add(descLabel, BorderLayout.CENTER);
        card.add(generateBtn, BorderLayout.SOUTH);
        
        return card;
    }
    
    // ==================== Action Methods ====================
    
    private void refreshDashboard() {
        try {
            if (statCards != null) {
                System.out.println("🔄 Refreshing receptionist dashboard data...");
                
               
                refreshDashboardStats();
                
              
                for (JPanel card : statCards) {
                    card.revalidate();
                    card.repaint();
                }
                
                
            }
            
        } catch (Exception e) {
            System.err.println("❌ Error refreshing dashboard: " + e.getMessage());
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, 
                "Error refreshing dashboard: " + e.getMessage(), 
                "Refresh Error", 
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void handleQuickAction(String action) {
        switch (action) {
            case "Check-in Guest":
                checkInGuest();
                break;
            case "Check-out Guest":
                checkOutGuest();
                break;
            case "Find Room":
                findAvailableRoom();
                break;
            case "Create Bill":
                generateBill();
                break;
        }
    }
    
    private void checkInGuest() {
        try {
            List<Booking> activeBookings = bookingService.getActiveBookings();
            
            if (activeBookings.isEmpty()) {
                JOptionPane.showMessageDialog(this, 
                    "No active bookings found!", 
                    "Check-in Error", 
                    JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            String[] bookingOptions = activeBookings.stream()
                .map(b -> String.format("%s - %s (Room %s)", 
                    b.getBookingId(), b.getCustomerName(), b.getRoomNumber()))
                .toArray(String[]::new);
            
            String selected = (String) JOptionPane.showInputDialog(this,
                "Select booking for check-in:", "Guest Check-in",
                JOptionPane.QUESTION_MESSAGE, null, bookingOptions, bookingOptions[0]);
            
            if (selected != null) {
                String bookingId = selected.split(" - ")[0];
                
                // Update room status
                for (Booking booking : activeBookings) {
                    if (booking.getBookingId().equals(bookingId)) {
                        roomService.updateRoomAvailability(booking.getRoomNumber(), false);
                        break;
                    }
                }
                
                JOptionPane.showMessageDialog(this, 
                    "✅ Guest checked in successfully!\nBooking ID: " + bookingId,
                    "Check-in Complete",
                    JOptionPane.INFORMATION_MESSAGE);
                
                refreshDashboard();
            }
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, 
                "Error during check-in: " + e.getMessage(),
                "Check-in Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void checkOutGuest() {
        try {
            List<Booking> activeBookings = bookingService.getActiveBookings();
            
            if (activeBookings.isEmpty()) {
                JOptionPane.showMessageDialog(this, 
                    "No active bookings found!", 
                    "Check-out Error", 
                    JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            String[] bookingOptions = activeBookings.stream()
                .map(b -> String.format("%s - %s (Room %s)", 
                    b.getBookingId(), b.getCustomerName(), b.getRoomNumber()))
                .toArray(String[]::new);
            
            String selected = (String) JOptionPane.showInputDialog(this,
                "Select booking for check-out:", "Guest Check-out",
                JOptionPane.QUESTION_MESSAGE, null, bookingOptions, bookingOptions[0]);
            
            if (selected != null) {
                String bookingId = selected.split(" - ")[0];
                
                // Update room status
                for (Booking booking : activeBookings) {
                    if (booking.getBookingId().equals(bookingId)) {
                        roomService.updateRoomAvailability(booking.getRoomNumber(), true);
                        break;
                    }
                }
                
                JOptionPane.showMessageDialog(this, 
                    "✅ Guest checked out successfully!\nBooking ID: " + bookingId,
                    "Check-out Complete",
                    JOptionPane.INFORMATION_MESSAGE);
                
                refreshDashboard();
            }
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, 
                "Error during check-out: " + e.getMessage(),
                "Check-out Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void viewAllRooms() {
        try {
            List<Room> rooms = roomService.getAllRooms();
            
            String[] columns = {"Room #", "Type", "Price/Night", "Status"};
            Object[][] data = new Object[rooms.size()][4];
            
            for (int i = 0; i < rooms.size(); i++) {
                Room room = rooms.get(i);
                data[i][0] = room.getRoomNumber();
                data[i][1] = room.getRoomType();
                data[i][2] = String.format("$%.2f", room.getPricePerNight());
                data[i][3] = room.isAvailable() ? "✅ Available" : "❌ Occupied";
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
                Room room = roomService.findAvailableRoom(selectedType);
                
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
    
    private void refreshRoomTable() {
        if (roomTableModel != null) {
            loadRoomData();
            JOptionPane.showMessageDialog(this, 
                "✅ Room table refreshed! (" + roomTableModel.getRowCount() + " rooms)",
                "Refresh Complete", 
                JOptionPane.INFORMATION_MESSAGE);
        }
    }
    
    private void createBooking() {
        JDialog dialog = new JDialog(this, "Create New Booking", true);
        dialog.setSize(500, 350);
        dialog.setLocationRelativeTo(this);
        
        JPanel panel = new JPanel(new GridLayout(6, 2, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        JTextField customerNameField = new JTextField();
        JTextField roomNumberField = new JTextField();
        JTextField checkInField = new JTextField(LocalDate.now().toString());
        JTextField checkOutField = new JTextField(LocalDate.now().plusDays(1).toString());
        
        panel.add(new JLabel("Customer Name:"));
        panel.add(customerNameField);
        panel.add(new JLabel("Room Number:"));
        panel.add(roomNumberField);
        panel.add(new JLabel("Check-in Date (YYYY-MM-DD):"));
        panel.add(checkInField);
        panel.add(new JLabel("Check-out Date (YYYY-MM-DD):"));
        panel.add(checkOutField);
        
        JButton checkAvailabilityBtn = new JButton("Check Room Availability");
        JButton submitBtn = new JButton("✅ Submit Booking");
        submitBtn.setBackground(new Color(60, 179, 113));
        submitBtn.setForeground(Color.WHITE);
        
        checkAvailabilityBtn.addActionListener(e -> {
            try {
                String roomNumber = roomNumberField.getText().trim();
                if (!roomNumber.isEmpty()) {
                    Room room = roomService.getRoomByNumber(roomNumber);
                    if (room.isAvailable()) {
                        JOptionPane.showMessageDialog(dialog, 
                            "✅ Room is available!", 
                            "Availability", 
                            JOptionPane.INFORMATION_MESSAGE);
                    } else {
                        JOptionPane.showMessageDialog(dialog, 
                            "❌ Room is not available!", 
                            "Availability", 
                            JOptionPane.WARNING_MESSAGE);
                    }
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog, 
                    "Error: " + ex.getMessage(), 
                    "Check Error", 
                    JOptionPane.ERROR_MESSAGE);
            }
        });
        
        submitBtn.addActionListener(e -> {
            try {
                String bookingId = "BOOK_" + System.currentTimeMillis();
                Booking booking = new Booking(
                    bookingId,
                    customerNameField.getText().trim(),
                    roomNumberField.getText().trim(),
                    LocalDate.parse(checkInField.getText().trim()),
                    LocalDate.parse(checkOutField.getText().trim())
                );
                
                bookingService.createBooking(booking);
                
                JOptionPane.showMessageDialog(dialog, 
                    "✅ Booking created successfully!\nBooking ID: " + bookingId,
                    "Success",
                    JOptionPane.INFORMATION_MESSAGE);
                
                dialog.dispose();
                refreshDashboard();
                
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog, 
                    "Error creating booking: " + ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            }
        });
        
        panel.add(checkAvailabilityBtn);
        panel.add(submitBtn);
        
        dialog.add(panel);
        dialog.setVisible(true);
    }
    
    private void viewAllBookings() {
        try {
            List<Booking> bookings = bookingService.getAllBookings();
            
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
                data[i][6] = booking.isActive() ? "✅ Active" : "❌ Inactive";
            }
            
            showTableDialog("All Bookings", columns, data);
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, 
                "Error: " + e.getMessage(), 
                "Load Failed", 
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void cancelBooking() {
        String bookingId = JOptionPane.showInputDialog(this, 
            "Enter Booking ID to cancel:", 
            "Cancel Booking", 
            JOptionPane.QUESTION_MESSAGE);
        
        if (bookingId != null && !bookingId.trim().isEmpty()) {
            int confirm = JOptionPane.showConfirmDialog(this, 
                "Are you sure you want to cancel booking " + bookingId + "?", 
                "Confirm Cancellation", 
                JOptionPane.YES_NO_OPTION);
            
            if (confirm == JOptionPane.YES_OPTION) {
                try {
                    bookingService.cancelBooking(bookingId.trim());
                    JOptionPane.showMessageDialog(this, "Booking cancelled successfully!");
                    refreshDashboard();
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(this, 
                        "Error: " + e.getMessage(), 
                        "Cancellation Failed", 
                        JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    }
    
    private void generateBill() {
        try {
            List<Booking> activeBookings = bookingService.getActiveBookings();
            
            if (activeBookings.isEmpty()) {
                JOptionPane.showMessageDialog(this, 
                    "No active bookings found to generate bill!", 
                    "Bill Generation Error", 
                    JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            String[] bookingOptions = activeBookings.stream()
                .map(b -> String.format("%s - %s (Room %s)", 
                    b.getBookingId(), b.getCustomerName(), b.getRoomNumber()))
                .toArray(String[]::new);
            
            String selected = (String) JOptionPane.showInputDialog(this,
                "Select booking for bill generation:", "Generate Bill",
                JOptionPane.QUESTION_MESSAGE, null, bookingOptions, bookingOptions[0]);
            
            if (selected != null) {
                String bookingId = selected.split(" - ")[0];
                
                // Find the booking
                Booking selectedBooking = null;
                for (Booking booking : activeBookings) {
                    if (booking.getBookingId().equals(bookingId)) {
                        selectedBooking = booking;
                        break;
                    }
                }
                
                if (selectedBooking != null) {
                    Bill bill = billService.generateBill(selectedBooking);
                    
                    JOptionPane.showMessageDialog(this, 
                        "✅ Bill generated successfully!\n" +
                        "Bill ID: " + bill.getBillId() + "\n" +
                        "Amount: $" + String.format("%.2f", bill.getTotalAmount()) + "\n" +
                        "Booking: " + bill.getBookingId(),
                        "Bill Generated",
                        JOptionPane.INFORMATION_MESSAGE);
                    
                    refreshDashboard();
                }
            }
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, 
                "Error generating bill: " + e.getMessage(),
                "Bill Generation Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void markBillAsPaid() {
        try {
            List<Bill> bills = billService.getUnpaidBills();
            
            if (bills.isEmpty()) {
                JOptionPane.showMessageDialog(this, 
                    "All bills are already paid.", 
                    "No Unpaid Bills", 
                    JOptionPane.INFORMATION_MESSAGE);
                return;
            }
            
            String[] billOptions = bills.stream()
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
                    billService.markBillAsPaid(billId);
                    JOptionPane.showMessageDialog(this, 
                        "✅ Bill marked as paid successfully!", 
                        "Payment Recorded", 
                        JOptionPane.INFORMATION_MESSAGE);
                    
                    refreshDashboard();
                }
            }
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, 
                "Error: " + e.getMessage(), 
                "Update Failed", 
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void viewAllBills() {
        try {
            List<Bill> bills = billService.getAllBills();
            
            String[] columns = {"Bill ID", "Booking ID", "Amount", "Generated Date", "Status"};
            Object[][] data = new Object[bills.size()][5];
            
            for (int i = 0; i < bills.size(); i++) {
                Bill bill = bills.get(i);
                data[i][0] = bill.getBillId();
                data[i][1] = bill.getBookingId();
                data[i][2] = String.format("$%.2f", bill.getTotalAmount());
                data[i][3] = bill.getGeneratedDate().toLocalDate().toString();
                data[i][4] = bill.isPaid() ? "✅ Paid" : "❌ Unpaid";
            }
            
            showTableDialog("All Bills", columns, data);
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, 
                "Error: " + e.getMessage(), 
                "Load Failed", 
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void refreshBookingTable() {
        if (bookingTableModel != null) {
            loadBookingData();
            JOptionPane.showMessageDialog(this, 
                "✅ Booking table refreshed! (" + bookingTableModel.getRowCount() + " bookings)",
                "Refresh Complete", 
                JOptionPane.INFORMATION_MESSAGE);
        }
    }
    
    private void refreshBillingTable() {
        if (billingTableModel != null) {
            loadBillingData();
            JOptionPane.showMessageDialog(this, 
                "✅ Billing table refreshed! (" + billingTableModel.getRowCount() + " bills)",
                "Refresh Complete", 
                JOptionPane.INFORMATION_MESSAGE);
        }
    }
    
    private void generateDailyReport() {
        try {
            LocalDate today = LocalDate.now();
            StringBuilder report = new StringBuilder();
            
            report.append("=".repeat(50)).append("\n");
            report.append("           DAILY ACTIVITY REPORT           \n");
            report.append("=".repeat(50)).append("\n");
            report.append("Date: ").append(today).append("\n");
            report.append("Generated by: ").append(receptionist.getUserId()).append("\n\n");
            
            // Get today's data
            List<Booking> todayBookings = bookingService.getBookingsByDateRange(today, today);
            List<Bill> todayBills = billService.getBillsByDateRange(today, today);
            
            report.append("📅 Today's Bookings: ").append(todayBookings.size()).append("\n");
            for (Booking booking : todayBookings) {
                report.append("  - ").append(booking.getCustomerName())
                      .append(" (Room ").append(booking.getRoomNumber()).append(")\n");
            }
            
            report.append("\n💰 Today's Bills: ").append(todayBills.size()).append("\n");
            double todayRevenue = 0;
            for (Bill bill : todayBills) {
                if (bill.isPaid()) {
                    todayRevenue += bill.getTotalAmount();
                }
                report.append("  - Bill ").append(bill.getBillId())
                      .append(": $").append(String.format("%.2f", bill.getTotalAmount()))
                      .append(" (").append(bill.isPaid() ? "Paid" : "Unpaid").append(")\n");
            }
            
            report.append("\n📊 Summary:\n");
            report.append("  Today's Revenue: $").append(String.format("%.2f", todayRevenue)).append("\n");
            report.append("  Active Bookings: ").append(bookingService.getActiveBookingCount()).append("\n");
            report.append("  Available Rooms: ").append(roomService.getAvailableRoomsCount()).append("\n");
            report.append("=".repeat(50)).append("\n");
            
            showReportDialog("Daily Activity Report", report.toString());
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, 
                "Error generating report: " + e.getMessage(),
                "Report Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void generateRevenueReport() {
        try {
            LocalDate today = LocalDate.now();
            StringBuilder report = new StringBuilder();
            
            report.append("=".repeat(50)).append("\n");
            report.append("          TODAY'S REVENUE REPORT          \n");
            report.append("=".repeat(50)).append("\n");
            report.append("Date: ").append(today).append("\n\n");
            
            List<Bill> todayBills = billService.getBillsByDateRange(today, today);
            
            double totalRevenue = 0;
            double paidRevenue = 0;
            double unpaidRevenue = 0;
            
            report.append("Bill Details:\n");
            report.append("-".repeat(50)).append("\n");
            
            for (Bill bill : todayBills) {
                totalRevenue += bill.getTotalAmount();
                if (bill.isPaid()) {
                    paidRevenue += bill.getTotalAmount();
                } else {
                    unpaidRevenue += bill.getTotalAmount();
                }
                
                report.append("  Bill ID: ").append(bill.getBillId()).append("\n");
                report.append("  Booking ID: ").append(bill.getBookingId()).append("\n");
                report.append("  Amount: $").append(String.format("%.2f", bill.getTotalAmount())).append("\n");
                report.append("  Status: ").append(bill.isPaid() ? "✅ Paid" : "❌ Unpaid").append("\n");
                report.append("  Generated: ").append(bill.getGeneratedDate().toLocalTime()).append("\n");
                report.append("-".repeat(30)).append("\n");
            }
            
            report.append("\n📊 Financial Summary:\n");
            report.append("  Total Bills Today: ").append(todayBills.size()).append("\n");
            report.append("  Total Amount: $").append(String.format("%.2f", totalRevenue)).append("\n");
            report.append("  Paid Amount: $").append(String.format("%.2f", paidRevenue)).append("\n");
            report.append("  Unpaid Amount: $").append(String.format("%.2f", unpaidRevenue)).append("\n");
            report.append("  Collection Rate: ").append(String.format("%.1f%%", 
                (paidRevenue * 100.0) / totalRevenue)).append("\n");
            report.append("=".repeat(50)).append("\n");
            
            showReportDialog("Today's Revenue Report", report.toString());
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, 
                "Error generating report: " + e.getMessage(),
                "Report Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void generateRoomStatusReport() {
        try {
            StringBuilder report = new StringBuilder();
            
            report.append("=".repeat(50)).append("\n");
            report.append("          ROOM STATUS REPORT          \n");
            report.append("=".repeat(50)).append("\n");
            report.append("Generated: ").append(LocalDate.now()).append("\n\n");
            
            List<Room> allRooms = roomService.getAllRooms();
            List<Room> availableRooms = roomService.getAvailableRooms();
            
            report.append("📊 Room Statistics:\n");
            report.append("  Total Rooms: ").append(allRooms.size()).append("\n");
            report.append("  Available Rooms: ").append(availableRooms.size()).append("\n");
            report.append("  Occupied Rooms: ").append(allRooms.size() - availableRooms.size()).append("\n");
            report.append("  Occupancy Rate: ").append(String.format("%.1f%%", 
                ((allRooms.size() - availableRooms.size()) * 100.0) / allRooms.size())).append("\n\n");
            
            report.append("🛏️ Available Rooms:\n");
            report.append("-".repeat(50)).append("\n");
            for (Room room : availableRooms) {
                report.append("  Room ").append(room.getRoomNumber())
                      .append(" (").append(room.getRoomType()).append(")")
                      .append(" - $").append(String.format("%.2f", room.getPricePerNight()))
                      .append("/night\n");
            }
            
            report.append("\n⭕ Occupied Rooms:\n");
            report.append("-".repeat(50)).append("\n");
            for (Room room : allRooms) {
                if (!room.isAvailable()) {
                    report.append("  Room ").append(room.getRoomNumber())
                          .append(" (").append(room.getRoomType()).append(")")
                          .append(" - $").append(String.format("%.2f", room.getPricePerNight()))
                          .append("/night\n");
                }
            }
            
            report.append("=".repeat(50)).append("\n");
            
            showReportDialog("Room Status Report", report.toString());
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, 
                "Error generating report: " + e.getMessage(),
                "Report Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void generateCustomerReport() {
        try {
            LocalDate today = LocalDate.now();
            StringBuilder report = new StringBuilder();
            
            report.append("=".repeat(50)).append("\n");
            report.append("       CUSTOMER MOVEMENT REPORT       \n");
            report.append("=".repeat(50)).append("\n");
            report.append("Date: ").append(today).append("\n\n");
            
            List<Booking> todayBookings = bookingService.getBookingsByDateRange(today, today);
            
            report.append("👥 Check-ins Today:\n");
            report.append("-".repeat(50)).append("\n");
            int checkInCount = 0;
            for (Booking booking : todayBookings) {
                if (booking.getCheckInDate().equals(today) && booking.isActive()) {
                    checkInCount++;
                    report.append("  ").append(checkInCount).append(". ")
                          .append(booking.getCustomerName())
                          .append(" - Room ").append(booking.getRoomNumber())
                          .append(" (").append(booking.getRoomNumber()).append(")\n");
                }
            }
            
            report.append("\n🚪 Check-outs Today:\n");
            report.append("-".repeat(50)).append("\n");
            int checkOutCount = 0;
            for (Booking booking : todayBookings) {
                if (booking.getCheckOutDate().equals(today) && booking.isActive()) {
                    checkOutCount++;
                    report.append("  ").append(checkOutCount).append(". ")
                          .append(booking.getCustomerName())
                          .append(" - Room ").append(booking.getRoomNumber())
                          .append(" (Booking ID: ").append(booking.getBookingId()).append(")\n");
                }
            }
            
            report.append("\n📊 Summary:\n");
            report.append("  Total Check-ins: ").append(checkInCount).append("\n");
            report.append("  Total Check-outs: ").append(checkOutCount).append("\n");
            report.append("  Net Guest Change: ").append(checkInCount - checkOutCount).append("\n");
            report.append("=".repeat(50)).append("\n");
            
            showReportDialog("Customer Movement Report", report.toString());
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, 
                "Error generating report: " + e.getMessage(),
                "Report Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void showReportDialog(String title, String content) {
        JDialog reportDialog = new JDialog(this, title, true);
        reportDialog.setSize(600, 500);
        reportDialog.setLocationRelativeTo(this);
        
        JTextArea textArea = new JTextArea(content);
        textArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        textArea.setEditable(false);
        
        JScrollPane scrollPane = new JScrollPane(textArea);
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));
        JButton printButton = new JButton("🖨️ Print");
        JButton saveButton = new JButton("💾 Save to File");
        JButton closeButton = new JButton("Close");
        
        saveButton.addActionListener(e -> saveReportToFile(title, content));
        closeButton.addActionListener(e -> reportDialog.dispose());
        
        buttonPanel.add(printButton);
        buttonPanel.add(saveButton);
        buttonPanel.add(closeButton);
        
        reportDialog.setLayout(new BorderLayout());
        reportDialog.add(scrollPane, BorderLayout.CENTER);
        reportDialog.add(buttonPanel, BorderLayout.SOUTH);
        
        reportDialog.setVisible(true);
    }
    
    private void saveReportToFile(String title, String content) {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Save Report");
        fileChooser.setSelectedFile(new File(
            title.replace(" ", "_") + "_" + 
            LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd")) + ".txt"
        ));
        
        if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            try (PrintWriter writer = new PrintWriter(file)) {
                writer.println(content);
                JOptionPane.showMessageDialog(this, 
                    "✅ Report saved successfully to:\n" + file.getAbsolutePath(), 
                    "Success", 
                    JOptionPane.INFORMATION_MESSAGE);
            } catch (IOException e) {
                JOptionPane.showMessageDialog(this, 
                    "Error saving file: " + e.getMessage(), 
                    "Save Error", 
                    JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void showTableDialog(String title, String[] columns, Object[][] data) {
        JDialog dialog = new JDialog(this, title, true);
        dialog.setSize(800, 400);
        dialog.setLocationRelativeTo(this);
        
        DefaultTableModel model = new DefaultTableModel(data, columns);
        JTable table = new JTable(model);
        JScrollPane scrollPane = new JScrollPane(table);
        
        dialog.add(scrollPane);
        dialog.setVisible(true);
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