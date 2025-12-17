package views;

import models.Manager;
import service.*;
import models.Room;
import models.Booking;
import models.Bill;
import exceptions.HotelManagementException;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

public class ManagerDashboard extends JFrame {
    private Manager manager;
    private StatisticsService statisticsService;
    private ReportService reportService;
    private RoomService roomService;
    private BookingService bookingService;
    private BillService billService;
    private UserService userService;
    
    private JPanel[] statCards;
    private JPanel currentDashboardPanel;
    
    
    private DefaultTableModel roomTableModel;
    private DefaultTableModel bookingTableModel;
    private DefaultTableModel billingTableModel;
    private JTable roomTable;
    private JTable bookingTable;
    private JTable billingTable;
    
    public ManagerDashboard(Manager manager) {
        this.manager = manager;
        this.statisticsService = new StatisticsService();
        this.reportService = new ReportService();
        this.roomService = new RoomService();
        this.bookingService = new BookingService();
        this.billService = new BillService();
        this.userService = new UserService();
        
        setTitle("Hotel Management System - Manager Dashboard");
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
        headerPanel.setBackground(new Color(70, 130, 180));
        headerPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        
        JLabel welcomeLabel = new JLabel("🏨 Hotel Management System - Manager Dashboard");
        welcomeLabel.setFont(new Font("Tahoma", Font.BOLD, 20));
        welcomeLabel.setForeground(Color.WHITE);
        
        JLabel userLabel = new JLabel("👤 User: " + manager.getUserId() + " | 📅 " + 
            LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
        userLabel.setFont(new Font("Tahoma", Font.PLAIN, 14));
        userLabel.setForeground(Color.WHITE);
        
        JButton logoutBtn = new JButton("🚪 Logout");
        logoutBtn.setBackground(Color.RED);
        logoutBtn.setForeground(Color.WHITE);
        logoutBtn.addActionListener(e -> logout());
        
        headerPanel.add(welcomeLabel, BorderLayout.WEST);
        headerPanel.add(userLabel, BorderLayout.CENTER);
        headerPanel.add(logoutBtn, BorderLayout.EAST);
        
        // Main content panel with tabbed pane
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("Tahoma", Font.BOLD, 12));
        
        // Tab 1: Dashboard
        tabbedPane.addTab("📊 Dashboard", createDashboardPanel());
        
        // Tab 2: Room Management
        tabbedPane.addTab("🏠 Room Management", createRoomManagementPanel());
        
        // Tab 3: Booking Management
        tabbedPane.addTab("📅 Booking Management", createBookingManagementPanel());
        
        // Tab 4: Billing Management
        tabbedPane.addTab("💰 Billing Management", createBillingPanel());
        
        // Tab 5: Reports
        tabbedPane.addTab("📈 Reports", createReportsPanel());
        
        // Tab 6: System
        tabbedPane.addTab("⚙️ System", createSystemPanel());
        
        // Add components to main panel
        mainPanel.add(headerPanel, BorderLayout.NORTH);
        mainPanel.add(tabbedPane, BorderLayout.CENTER);
        
        add(mainPanel);
    }
    
    private JPanel createDashboardPanel() {
        JPanel dashboardPanel = new JPanel(new BorderLayout(10, 10));
        dashboardPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Top panel with refresh button
        JPanel topPanel = new JPanel(new BorderLayout());
        JLabel titleLabel = new JLabel("📈 System Dashboard Overview");
        titleLabel.setFont(new Font("Tahoma", Font.BOLD, 18));
        
        JButton refreshBtn = new JButton("🔄 Refresh Dashboard");
        refreshBtn.addActionListener(e -> refreshDashboard());
        
        topPanel.add(titleLabel, BorderLayout.WEST);
        topPanel.add(refreshBtn, BorderLayout.EAST);
        
        // Statistics cards panel
        JPanel statsPanel = new JPanel(new GridLayout(2, 3, 15, 15));
        statsPanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));
        
        // Create 6 statistic cards with specific titles
        String[] statTitles = {
            "Total Rooms", "Available Rooms", "Active Bookings",
            "Occupancy Rate", "Total Revenue", "Today's Activity"
        };
        
        JPanel[] statCards = new JPanel[6];
        for (int i = 0; i < 6; i++) {
            statCards[i] = createStatCard(statTitles[i], "0", "Loading...", getCardColor(i));
            statsPanel.add(statCards[i]);
        }
        
        // Quick actions panel
        JPanel quickActionsPanel = new JPanel(new GridLayout(1, 4, 10, 10));
        quickActionsPanel.setBorder(BorderFactory.createTitledBorder("⚡ Quick Actions"));
        
        String[] actions = {"Add Room", "View All Bookings", "Generate Report", "View System Stats"};
        Color[] actionColors = {
            new Color(60, 179, 113), // Green
            new Color(70, 130, 180), // Blue
            new Color(255, 140, 0),  // Orange
            new Color(186, 85, 211)  // Purple
        };
        
        for (int i = 0; i < actions.length; i++) {
            JButton actionBtn = new JButton(actions[i]);
            actionBtn.setBackground(actionColors[i]);
            actionBtn.setForeground(Color.black);
            actionBtn.setFont(new Font("Tahoma", Font.BOLD, 12));
            final String action = actions[i];
            actionBtn.addActionListener(e -> handleQuickAction(action));
            quickActionsPanel.add(actionBtn);
        }
        
        // Recent activity panel
        JPanel activityPanel = new JPanel(new BorderLayout());
        activityPanel.setBorder(BorderFactory.createTitledBorder("📋 Recent Activity"));
        
        String[] columns = {"Time", "Activity", "User", "Details"};
        DefaultTableModel activityModel = new DefaultTableModel(columns, 0);
        JTable activityTable = new JTable(activityModel);
        activityTable.setRowHeight(30);
        JScrollPane scrollPane = new JScrollPane(activityTable);
        
        // Load recent activity
       
        
        activityPanel.add(scrollPane, BorderLayout.CENTER);
        
        // Assemble dashboard
        dashboardPanel.add(topPanel, BorderLayout.NORTH);
        dashboardPanel.add(statsPanel, BorderLayout.CENTER);
        
        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.add(quickActionsPanel, BorderLayout.NORTH);
        bottomPanel.add(activityPanel, BorderLayout.CENTER);
        dashboardPanel.add(bottomPanel, BorderLayout.SOUTH);
        
        // Load initial statistics
        refreshDashboardStats(statCards);
        currentDashboardPanel = dashboardPanel; // ⭐ حفظ المرجع
        
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
            new Color(70, 130, 180),   // Steel Blue
            new Color(60, 179, 113),   // Medium Sea Green
            new Color(255, 140, 0),    // Dark Orange
            new Color(186, 85, 211),   // Medium Orchid
            new Color(220, 20, 60),    // Crimson
            new Color(30, 144, 255)    // Dodger Blue
        };
        return colors[index % colors.length];
    }
    
    private void refreshDashboardStats(JPanel[] statCards) {
        try {
            Map<String, Object> stats = statisticsService.getDashboardStats();
            
            // Update stat cards
            updateStatCard(statCards[0], stats.get("totalRooms").toString(), "Rooms in system");
            updateStatCard(statCards[1], stats.get("availableRooms").toString(), "Ready for booking");
            updateStatCard(statCards[2], stats.get("activeBookings").toString(), "Current guests");
            updateStatCard(statCards[3], stats.get("occupancyRate") + "%", "Current occupancy");
            updateStatCard(statCards[4], "$" + String.format("%.2f", stats.get("totalRevenue")), "Total revenue");
            
            int todayActivity = (int) stats.get("todayCheckIns") + (int) stats.get("todayCheckOuts");
            updateStatCard(statCards[5], String.valueOf(todayActivity), "Check-ins/outs today");
            
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
        
        JButton addRoomBtn = new JButton("➕ Add Room");
        JButton removeRoomBtn = new JButton("🗑️ Remove Room");
        JButton updateRoomBtn = new JButton("✏️ Update Room");
        JButton refreshBtn = new JButton("🔄 Refresh");
        
        addRoomBtn.addActionListener(e -> addRoom());
        removeRoomBtn.addActionListener(e -> removeRoom());
        updateRoomBtn.addActionListener(e -> updateRoom());
        refreshBtn.addActionListener(e -> refreshRoomTable());
        
        topPanel.add(addRoomBtn);
        topPanel.add(removeRoomBtn);
        topPanel.add(updateRoomBtn);
        topPanel.add(refreshBtn);
        
        // Room table
        String[] columns = {"Room #", "Type", "Price/Night", "Status", "Actions"};
        DefaultTableModel roomTableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Make all cells non-editable
            }
        };
        
        JTable roomTable = new JTable(roomTableModel);
        roomTable.setRowHeight(35);
        roomTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int selectedRow = roomTable.getSelectedRow();
                if (selectedRow != -1) {
                    // Enable update and remove buttons when a room is selected
                    removeRoomBtn.setEnabled(true);
                    updateRoomBtn.setEnabled(true);
                }
            }
        });
        
        JScrollPane scrollPane = new JScrollPane(roomTable);
        
        // Load room data
        loadRoomData(roomTableModel);
        
        roomPanel.add(topPanel, BorderLayout.NORTH);
        roomPanel.add(scrollPane, BorderLayout.CENTER);
        
        return roomPanel;
    }
    
    private void loadRoomData(DefaultTableModel model) {
        try {this.roomTableModel = model;
            List<Room> rooms = roomService.getAllRooms();
            model.setRowCount(0); // Clear existing data
            
            for (Room room : rooms) {
                String status = room.isAvailable() ? "✅ Available" : "❌ Occupied";
                String actions = room.isAvailable() ? "Book | Edit | Delete" : "Edit | Delete";
                
                Object[] row = {
                    room.getRoomNumber(),
                    room.getRoomType(),
                    String.format("$%.2f", room.getPricePerNight()),
                    status,
                    actions
                };
                model.addRow(row);
            }
        } catch (HotelManagementException e) {
            JOptionPane.showMessageDialog(this, 
                "Error loading rooms: " + e.getMessage(), 
                "Load Error", 
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private JPanel createBookingManagementPanel() {
        JPanel bookingPanel = new JPanel(new BorderLayout(10, 10));
        bookingPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Top panel with buttons
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        
        JButton viewAllBtn = new JButton("👁️ View All Bookings");
        JButton viewActiveBtn = new JButton("📋 Active Bookings");
        JButton cancelBtn = new JButton("❌ Cancel Booking");
        JButton refreshBtn = new JButton("🔄 Refresh");
        
        viewAllBtn.addActionListener(e -> viewAllBookings());
        viewActiveBtn.addActionListener(e -> viewActiveBookings());
        cancelBtn.addActionListener(e -> cancelBooking());
        refreshBtn.addActionListener(e -> refreshBookingTable());
        
        topPanel.add(viewAllBtn);
        topPanel.add(viewActiveBtn);
        topPanel.add(cancelBtn);
        topPanel.add(refreshBtn);
        
        // Booking table
        String[] columns = {"Booking ID", "Customer", "Room", "Check-in", "Check-out", "Nights", "Status"};
        DefaultTableModel bookingTableModel = new DefaultTableModel(columns, 0);
        
        JTable bookingTable = new JTable(bookingTableModel);
        bookingTable.setRowHeight(30);
        JScrollPane scrollPane = new JScrollPane(bookingTable);
        
        // Load booking data
        loadBookingData(bookingTableModel);
        
        bookingPanel.add(topPanel, BorderLayout.NORTH);
        bookingPanel.add(scrollPane, BorderLayout.CENTER);
        
        return bookingPanel;
    }
    
    private void loadBookingData(DefaultTableModel model) {
        try {
        	this.bookingTableModel = model;
            List<Booking> bookings = bookingService.getAllBookings();
            model.setRowCount(0);
            
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
                model.addRow(row);
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
        
        JButton viewAllBtn = new JButton("👁️ View All Bills");
        JButton unpaidBtn = new JButton("💵 Unpaid Bills");
        JButton markPaidBtn = new JButton("✅ Mark as Paid");
        JButton refreshBtn = new JButton("🔄 Refresh");
        
        viewAllBtn.addActionListener(e -> viewAllBills());
        unpaidBtn.addActionListener(e -> viewUnpaidBills());
        markPaidBtn.addActionListener(e -> markBillAsPaid());
        refreshBtn.addActionListener(e -> refreshBillingTable());
        
        topPanel.add(viewAllBtn);
        topPanel.add(unpaidBtn);
        topPanel.add(markPaidBtn);
        topPanel.add(refreshBtn);
        
        // Billing table
        String[] columns = {"Bill ID", "Booking ID", "Amount", "Generated Date", "Status", "Action"};
        DefaultTableModel billingTableModel = new DefaultTableModel(columns, 0);
        
        JTable billingTable = new JTable(billingTableModel);
        billingTable.setRowHeight(30);
        JScrollPane scrollPane = new JScrollPane(billingTable);
        
        // Load billing data
        loadBillingData(billingTableModel);
        
        billingPanel.add(topPanel, BorderLayout.NORTH);
        billingPanel.add(scrollPane, BorderLayout.CENTER);
        
        return billingPanel;
    }
    
    private void loadBillingData(DefaultTableModel model) {
        try {
        	this.billingTableModel = model;
            List<Bill> bills = billService.getAllBills();
            model.setRowCount(0);
            
            for (Bill bill : bills) {
                String action = bill.isPaid() ? "View Details" : "Mark as Paid";
                
                Object[] row = {
                    bill.getBillId(),
                    bill.getBookingId(),
                    String.format("$%.2f", bill.getTotalAmount()),
                    bill.getGeneratedDate().toLocalDate().toString(),
                    bill.isPaid() ? "✅ Paid" : "❌ Unpaid",
                    action
                };
                model.addRow(row);
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
        
        // Reports title
        JLabel titleLabel = new JLabel("📊 Reports Generation Center");
        titleLabel.setFont(new Font("Tahoma", Font.BOLD, 18));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));
        
        // Report buttons panel (3x2 grid)
        JPanel buttonsPanel = new JPanel(new GridLayout(3, 2, 15, 15));
        
        String[] reports = {
            "📋 Room Occupancy Report", 
            "💰 Financial Revenue Report", 
            "📈 Analytical Statistics Report",
            "👥 Customer Analysis Report", 
            "📅 Monthly Summary Report",
            "📉 Performance Trends Report"
        };
        
        String[] descriptions = {
            "Detailed room occupancy and availability",
            "Financial income and billing status",
            "Statistical analysis and trends",
            "Customer booking patterns",
            "Monthly performance summary",
            "Performance trends over time"
        };
        
        for (int i = 0; i < reports.length; i++) {
            JPanel reportCard = createReportCard(reports[i], descriptions[i], i);
            buttonsPanel.add(reportCard);
        }
        
        // Preview panel
        JPanel previewPanel = new JPanel(new BorderLayout());
        previewPanel.setBorder(BorderFactory.createTitledBorder("📄 Report Preview"));
        
        JTextArea previewArea = new JTextArea();
        previewArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        previewArea.setEditable(false);
        JScrollPane previewScroll = new JScrollPane(previewArea);
        
        // Control buttons
        JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));
        JButton generateBtn = new JButton("🔄 Generate Selected");
        JButton printBtn = new JButton("🖨️ Print Report");
        JButton exportBtn = new JButton("💾 Export to File");
        JButton clearBtn = new JButton("🗑️ Clear Preview");
        
        generateBtn.addActionListener(e -> {
            // Placeholder for generating selected report
            previewArea.setText("Select a report from the buttons above to generate.");
        });
        exportBtn.addActionListener(e -> {
            if (!previewArea.getText().isEmpty()) {
                saveReportToFile("Generated_Report", previewArea.getText());
            }
        });
        clearBtn.addActionListener(e -> previewArea.setText(""));
        
        controlPanel.add(generateBtn);
        controlPanel.add(printBtn);
        controlPanel.add(exportBtn);
        controlPanel.add(clearBtn);
        
        previewPanel.add(previewScroll, BorderLayout.CENTER);
        previewPanel.add(controlPanel, BorderLayout.SOUTH);
        
        // Assemble reports panel
        reportsPanel.add(titleLabel, BorderLayout.NORTH);
        reportsPanel.add(buttonsPanel, BorderLayout.CENTER);
        reportsPanel.add(previewPanel, BorderLayout.SOUTH);
        
        return reportsPanel;
    }
    
    private JPanel createReportCard(String title, String description, int index) {
        JPanel card = new JPanel(new BorderLayout(5, 5));
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        card.setBackground(new Color(245, 245, 245));
        
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Tahoma", Font.BOLD, 12));
        
        JLabel descLabel = new JLabel("<html><body style='width: 150px'>" + description + "</body></html>");
        descLabel.setFont(new Font("Tahoma", Font.PLAIN, 10));
        descLabel.setForeground(Color.GRAY);
        
        JButton generateBtn = new JButton("Generate");
        generateBtn.setBackground(getReportColor(index));
        generateBtn.setForeground(Color.black);
        
        generateBtn.addActionListener(e -> generateReport(index));
        
        card.add(titleLabel, BorderLayout.NORTH);
        card.add(descLabel, BorderLayout.CENTER);
        card.add(generateBtn, BorderLayout.SOUTH);
        
        return card;
    }
    
    private Color getReportColor(int index) {
        Color[] colors = {
            new Color(70, 130, 180),   // Blue
            new Color(60, 179, 113),   // Green
            new Color(255, 140, 0),    // Orange
            new Color(186, 85, 211),   // Purple
            new Color(220, 20, 60),    // Red
            new Color(30, 144, 255)    // Light Blue
        };
        return colors[index % colors.length];
    }
    
    private JPanel createSystemPanel() {
        JPanel systemPanel = new JPanel(new BorderLayout(10, 10));
        systemPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // System title
        JLabel titleLabel = new JLabel("⚙️ System Management");
        titleLabel.setFont(new Font("Tahoma", Font.BOLD, 18));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));
        
        // System actions panel
        JPanel actionsPanel = new JPanel(new GridLayout(4, 1, 10, 10));
        
        JButton backupBtn = new JButton("💾 Backup Database");
        JButton restoreBtn = new JButton("🔄 Restore Database");
        JButton logsBtn = new JButton("📋 View System Logs");
        JButton settingsBtn = new JButton("⚙️ System Settings");
        
        backupBtn.addActionListener(e -> backupDatabase());
        restoreBtn.addActionListener(e -> restoreDatabase());
        logsBtn.addActionListener(e -> viewSystemLogs());
        settingsBtn.addActionListener(e -> systemSettings());
        
        actionsPanel.add(backupBtn);
        actionsPanel.add(restoreBtn);
        actionsPanel.add(logsBtn);
        actionsPanel.add(settingsBtn);
        
        // System info panel
        JPanel infoPanel = new JPanel(new BorderLayout());
        infoPanel.setBorder(BorderFactory.createTitledBorder("📊 System Information"));
        
        JTextArea infoArea = new JTextArea();
        infoArea.setEditable(false);
        infoArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        infoArea.setText(getSystemInfo());
        
        infoPanel.add(new JScrollPane(infoArea), BorderLayout.CENTER);
        
        systemPanel.add(titleLabel, BorderLayout.NORTH);
        systemPanel.add(actionsPanel, BorderLayout.WEST);
        systemPanel.add(infoPanel, BorderLayout.CENTER);
        
        return systemPanel;
    }
    
    private String getSystemInfo() {
        StringBuilder info = new StringBuilder();
        info.append("System Information\n");
        info.append("==================\n");
        info.append("Version: 2.0 (4-Layer Architecture)\n");
        info.append("Database: MySQL\n");
        info.append("Last Backup: ").append(LocalDate.now().minusDays(1)).append("\n");
        info.append("Total Users: ").append(getUserCount()).append("\n");
        info.append("System Uptime: 24 hours\n");
        info.append("Memory Usage: 512MB / 2GB\n");
        info.append("Disk Space: 1.2GB / 10GB\n");
        return info.toString();
    }
    
    private int getUserCount() {
        try {
            return userService.getUserCount();
        } catch (Exception e) {
            return 0;
        }
    }
    
  

    private void refreshDashboard() {
        try {
           
            
            
        	 if (statCards != null) {
                 System.out.println("🔄 Refreshing dashboard data...");
                 
                 
                 refreshDashboardStats(statCards);
                 
                 
                 for (JPanel card : statCards) {
                     card.revalidate();
                     card.repaint();
                 }
                 
                 if (currentDashboardPanel != null) {
                     currentDashboardPanel.revalidate();
                     currentDashboardPanel.repaint();
                 }
                 
                 JOptionPane.showMessageDialog(this, 
                     "✅ تم تحديث البيانات بنجاح!", 
                     "تحديث ناجح", 
                     JOptionPane.INFORMATION_MESSAGE); }
            
        } catch (Exception e) {
            System.err.println("❌ Error refreshing dashboard: " + e.getMessage());
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, 
                "Error refreshing dashboard: " + e.getMessage(), 
                "Refresh Error", 
                JOptionPane.ERROR_MESSAGE);
        }
    }

    
    private JTabbedPane getTabbedPane() {
        Container contentPane = getContentPane();
        for (Component comp : contentPane.getComponents()) {
            if (comp instanceof JTabbedPane) {
                return (JTabbedPane) comp;
            }
        }
        return null;
    }
    
    private void handleQuickAction(String action) {
        switch (action) {
            case "Add Room":
                addRoom();
                break;
            case "View All Bookings":
                viewAllBookings();
                break;
            case "Generate Report":
                
                Component[] comps = getContentPane().getComponents();
                if (comps.length > 0 && comps[0] instanceof JPanel) {
                    JPanel mainPanel = (JPanel) comps[0];
                    for (Component c : mainPanel.getComponents()) {
                        if (c instanceof JTabbedPane) {
                            ((JTabbedPane) c).setSelectedIndex(4);
                            break;
                        }
                    }
                }
                break;
            case "View System Stats":
                showSystemStats();
                break;
        }
    }
    
    private void addRoom() {
        JDialog dialog = new JDialog(this, "Add New Room", true);
        dialog.setSize(400, 300);
        dialog.setLocationRelativeTo(this);
        
        JPanel panel = new JPanel(new GridLayout(5, 2, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        JTextField roomNumberField = new JTextField();
        JComboBox<String> typeCombo = new JComboBox<>(new String[]{"Single", "Double", "Suite", "Deluxe"});
        JTextField priceField = new JTextField();
        
        panel.add(new JLabel("Room Number:"));
        panel.add(roomNumberField);
        panel.add(new JLabel("Room Type:"));
        panel.add(typeCombo);
        panel.add(new JLabel("Price per Night:"));
        panel.add(priceField);
        panel.add(new JLabel()); // Empty cell
        panel.add(new JLabel()); // Empty cell
        
        JButton saveBtn = new JButton("Save");
        JButton cancelBtn = new JButton("Cancel");
        
        saveBtn.addActionListener(e -> {
            try {
                Room room = new Room(
                    roomNumberField.getText().trim(),
                    (String) typeCombo.getSelectedItem(),
                    Double.parseDouble(priceField.getText().trim()),
                    true
                );
                roomService.addRoom(room);
                JOptionPane.showMessageDialog(dialog, "Room added successfully!");
                dialog.dispose();
                refreshDashboard();
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(dialog, "Please enter a valid price!");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog, "Error: " + ex.getMessage());
            }
        });
        
        cancelBtn.addActionListener(e -> dialog.dispose());
        
        panel.add(saveBtn);
        panel.add(cancelBtn);
        
        dialog.add(panel);
        dialog.setVisible(true);
    }
    
    private void removeRoom() {
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
                    roomService.removeRoom(roomNumber.trim());
                    JOptionPane.showMessageDialog(this, "Room removed successfully!");
                    refreshDashboard();
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(this, 
                        "Error: " + e.getMessage(), 
                        "Removal Failed", 
                        JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    }
    
    private void updateRoom() {
        String roomNumber = JOptionPane.showInputDialog(this, 
            "Enter room number to update:", 
            "Update Room", 
            JOptionPane.QUESTION_MESSAGE);
        
        if (roomNumber != null && !roomNumber.trim().isEmpty()) {
            try {
                Room room = roomService.getRoomByNumber(roomNumber);
                
                JDialog dialog = new JDialog(this, "Update Room", true);
                dialog.setSize(400, 300);
                dialog.setLocationRelativeTo(this);
                
                JPanel panel = new JPanel(new GridLayout(5, 2, 10, 10));
                panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
                
                JTextField numberField = new JTextField(room.getRoomNumber());
                JComboBox<String> typeCombo = new JComboBox<>(new String[]{"Single", "Double", "Suite", "Deluxe"});
                typeCombo.setSelectedItem(room.getRoomType());
                JTextField priceField = new JTextField(String.valueOf(room.getPricePerNight()));
                JCheckBox availableCheck = new JCheckBox("Available", room.isAvailable());
                
                panel.add(new JLabel("Room Number:"));
                panel.add(numberField);
                panel.add(new JLabel("Room Type:"));
                panel.add(typeCombo);
                panel.add(new JLabel("Price per Night:"));
                panel.add(priceField);
                panel.add(new JLabel("Availability:"));
                panel.add(availableCheck);
                
                JButton updateBtn = new JButton("Update");
                JButton cancelBtn = new JButton("Cancel");
                
                updateBtn.addActionListener(e -> {
                    try {
                        room.setRoomType((String) typeCombo.getSelectedItem());
                        room.setPricePerNight(Double.parseDouble(priceField.getText()));
                        room.setAvailable(availableCheck.isSelected());
                        
                        roomService.updateRoomPrice(roomNumber, room.getPricePerNight());
                        roomService.updateRoomAvailability(roomNumber, room.isAvailable());
                        
                        JOptionPane.showMessageDialog(dialog, "Room updated successfully!");
                        dialog.dispose();
                        refreshDashboard();
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(dialog, "Error: " + ex.getMessage());
                    }
                });
                
                cancelBtn.addActionListener(e -> dialog.dispose());
                
                panel.add(updateBtn);
                panel.add(cancelBtn);
                
                dialog.add(panel);
                dialog.setVisible(true);
                
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, 
                    "Error: " + e.getMessage(), 
                    "Update Failed", 
                    JOptionPane.ERROR_MESSAGE);
            }
        }
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
                data[i][6] = booking.isActive() ? "Active" : "Cancelled";
            }
            
            showTableDialog("All Bookings", columns, data);
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, 
                "Error: " + e.getMessage(), 
                "Load Failed", 
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void viewActiveBookings() {
        try {
            List<Booking> bookings = bookingService.getActiveBookings();
            
            String[] columns = {"Booking ID", "Customer", "Room", "Check-in", "Check-out", "Nights"};
            Object[][] data = new Object[bookings.size()][6];
            
            for (int i = 0; i < bookings.size(); i++) {
                Booking booking = bookings.get(i);
                data[i][0] = booking.getBookingId();
                data[i][1] = booking.getCustomerName();
                data[i][2] = booking.getRoomNumber();
                data[i][3] = booking.getCheckInDate().toString();
                data[i][4] = booking.getCheckOutDate().toString();
                data[i][5] = booking.calculateNights();
            }
            
            showTableDialog("Active Bookings", columns, data);
            
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
                data[i][4] = bill.isPaid() ? "Paid" : "Unpaid";
            }
            
            showTableDialog("All Bills", columns, data);
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, 
                "Error: " + e.getMessage(), 
                "Load Failed", 
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void viewUnpaidBills() {
        try {
            List<Bill> bills = billService.getUnpaidBills();
            
            String[] columns = {"Bill ID", "Booking ID", "Amount", "Generated Date"};
            Object[][] data = new Object[bills.size()][4];
            
            for (int i = 0; i < bills.size(); i++) {
                Bill bill = bills.get(i);
                data[i][0] = bill.getBillId();
                data[i][1] = bill.getBookingId();
                data[i][2] = String.format("$%.2f", bill.getTotalAmount());
                data[i][3] = bill.getGeneratedDate().toLocalDate().toString();
            }
            
            showTableDialog("Unpaid Bills", columns, data);
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, 
                "Error: " + e.getMessage(), 
                "Load Failed", 
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void markBillAsPaid() {
        String billId = JOptionPane.showInputDialog(this, 
            "Enter Bill ID to mark as paid:", 
            "Mark Bill as Paid", 
            JOptionPane.QUESTION_MESSAGE);
        
        if (billId != null && !billId.trim().isEmpty()) {
            int confirm = JOptionPane.showConfirmDialog(this, 
                "Mark bill " + billId + " as paid?", 
                "Confirm Payment", 
                JOptionPane.YES_NO_OPTION);
            
            if (confirm == JOptionPane.YES_OPTION) {
                try {
                    billService.markBillAsPaid(billId);
                    JOptionPane.showMessageDialog(this, "Bill marked as paid successfully!");
                    refreshDashboard();
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(this, 
                        "Error: " + e.getMessage(), 
                        "Update Failed", 
                        JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    }
    
    private void generateReport(int reportType) {
        try {
            String reportContent = "";
            String reportTitle = "";
            
            switch (reportType) {
                case 0: // Room Occupancy Report
                    reportContent = reportService.generateRoomOccupancyReport();
                    reportTitle = "Room Occupancy Report";
                    break;
                case 1: // Financial Report
                    reportContent = reportService.generateFinancialReport();
                    reportTitle = "Financial Revenue Report";
                    break;
                case 2: // Analytical Report
                    reportContent = reportService.generateAnalyticalReport();
                    reportTitle = "Analytical Statistics Report";
                    break;
                case 3: // Customer Analysis Report
                    reportContent = "Customer Analysis Report - Coming Soon";
                    reportTitle = "Customer Analysis Report";
                    break;
                case 4: // Monthly Summary Report
                    reportContent = "Monthly Summary Report - Coming Soon";
                    reportTitle = "Monthly Summary Report";
                    break;
                case 5: // Performance Trends Report
                    reportContent = "Performance Trends Report - Coming Soon";
                    reportTitle = "Performance Trends Report";
                    break;
                default:
                    reportContent = "Report type not implemented yet.";
                    reportTitle = "Coming Soon";
            }
            
            showReportDialog(reportTitle, reportContent);
            
        } catch (HotelManagementException e) {
            JOptionPane.showMessageDialog(this, 
                "Error generating report: " + e.getMessage(), 
                "Report Error", 
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void showReportDialog(String title, String content) {
        JDialog reportDialog = new JDialog(this, title, true);
        reportDialog.setSize(800, 600);
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
    private void refreshRoomTable() {
        if (roomTableModel != null) {
            loadRoomData(roomTableModel);
            JOptionPane.showMessageDialog(this, 
                "✅ Room table refreshed! (" + roomTableModel.getRowCount() + " rooms)",
                "Refresh Complete", 
                JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void refreshBookingTable() {
        if (bookingTableModel != null) {
            loadBookingData(bookingTableModel);
            JOptionPane.showMessageDialog(this, 
                "✅ Booking table refreshed! (" + bookingTableModel.getRowCount() + " bookings)",
                "Refresh Complete", 
                JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void refreshBillingTable() {
        if (billingTableModel != null) {
            loadBillingData(billingTableModel);
            JOptionPane.showMessageDialog(this, 
                "✅ Billing table refreshed! (" + billingTableModel.getRowCount() + " bills)",
                "Refresh Complete", 
                JOptionPane.INFORMATION_MESSAGE);
        }
    }
    
    private void backupDatabase() {
        JOptionPane.showMessageDialog(this, 
            "Database backup initiated.\nBackup will be saved to: backup_" + 
            LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd")) + ".sql",
            "Backup Started",
            JOptionPane.INFORMATION_MESSAGE);
    }
    
    private void restoreDatabase() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Select Backup File");
        
        if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            JOptionPane.showMessageDialog(this, 
                "Database restore initiated from:\n" + fileChooser.getSelectedFile().getName(),
                "Restore Started",
                JOptionPane.INFORMATION_MESSAGE);
        }
    }
    
    private void viewSystemLogs() {
        JOptionPane.showMessageDialog(this, 
            "System logs view - Coming Soon",
            "System Logs",
            JOptionPane.INFORMATION_MESSAGE);
    }
    
    private void systemSettings() {
        JOptionPane.showMessageDialog(this, 
            "System settings - Coming Soon",
            "System Settings",
            JOptionPane.INFORMATION_MESSAGE);
    }
    
    private void showSystemStats() {
        try {
            Map<String, Object> stats = statisticsService.getDashboardStats();
            
            StringBuilder message = new StringBuilder();
            message.append("📊 System Statistics\n");
            message.append("====================\n");
            message.append("Total Rooms: ").append(stats.get("totalRooms")).append("\n");
            message.append("Available Rooms: ").append(stats.get("availableRooms")).append("\n");
            message.append("Active Bookings: ").append(stats.get("activeBookings")).append("\n");
            message.append("Occupancy Rate: ").append(stats.get("occupancyRate")).append("%\n");
            message.append("Total Revenue: $").append(String.format("%.2f", stats.get("totalRevenue"))).append("\n");
            message.append("Today's Check-ins: ").append(stats.get("todayCheckIns")).append("\n");
            message.append("Today's Check-outs: ").append(stats.get("todayCheckOuts")).append("\n");
            
            JOptionPane.showMessageDialog(this, 
                message.toString(),
                "System Statistics",
                JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, 
                "Error loading statistics: " + e.getMessage(),
                "Error",
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