package database;

import models.*;
import exceptions.HotelManagementException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Handles all database operations for the hotel management system.
 * Uses MySQL database with connection pooling.
 */
public class DatabaseManager {
    private static final String URL = "jdbc:mysql://localhost:3306/hotel_management";
    private static final String USERNAME = "root";
    private static final String PASSWORD = "suad@262503SUAD";

    // Load MySQL driver when class is loaded
    static {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            System.err.println("❌ MySQL JDBC Driver not found!");
            throw new RuntimeException("MySQL Driver not found", e);
        }
    }

    /**
     * Creates a new database connection.
     * @return Connection object
     * @throws SQLException if connection fails
     */
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USERNAME, PASSWORD);
    }

    /**
     * Authenticates a user.
     * @param userId User ID
     * @param password Password
     * @return User object if authentication successful
     * @throws HotelManagementException if authentication fails
     */
    public static User authenticateUser(String userId, String password) throws HotelManagementException {
        String sql = "SELECT * FROM users WHERE user_id = ? AND password = ?";
        
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, userId);
            stmt.setString(2, password);
            
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                String role = rs.getString("role");
                if ("Manager".equals(role)) {
                    return new Manager(userId, password);
                } else if ("Receptionist".equals(role)) {
                    return new Receptionist(userId, password);
                }
            }
            throw new HotelManagementException("Invalid user ID or password");
            
        } catch (SQLException e) {
            throw new HotelManagementException("Database error during authentication: " + e.getMessage());
        }
    }

    /**
     * Adds a new room to the database.
     * @param room Room object to add
     * @throws HotelManagementException if room cannot be added
     */
    public static void addRoom(Room room) throws HotelManagementException {
        String sql = "INSERT INTO rooms (room_number, room_type, price_per_night, is_available) VALUES (?, ?, ?, ?)";
        
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, room.getRoomNumber());
            stmt.setString(2, room.getRoomType());
            stmt.setDouble(3, room.getPricePerNight());
            stmt.setBoolean(4, room.isAvailable());
            
            stmt.executeUpdate();
            
        } catch (SQLException e) {
            if (e.getErrorCode() == 1062) { // Duplicate entry error code
                throw new HotelManagementException("Room number already exists");
            }
            throw new HotelManagementException("Error adding room: " + e.getMessage());
        }
    }

    /**
     * Removes a room from the database.
     * @param roomNumber Room number to remove
     * @throws HotelManagementException if room cannot be removed
     */
    public static void removeRoom(String roomNumber) throws HotelManagementException {
        String sql = "DELETE FROM rooms WHERE room_number = ?";
        
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, roomNumber);
            int affectedRows = stmt.executeUpdate();
            
            if (affectedRows == 0) {
                throw new HotelManagementException("Room not found");
            }
            
        } catch (SQLException e) {
            throw new HotelManagementException("Error removing room: " + e.getMessage());
        }
    }

    /**
     * Retrieves all rooms from the database.
     * @return List of all rooms
     * @throws HotelManagementException if rooms cannot be retrieved
     */
    public static List<Room> getAllRooms() throws HotelManagementException {
        List<Room> rooms = new ArrayList<>();
        String sql = "SELECT * FROM rooms ORDER BY room_number";
        
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                Room room = new Room(
                    rs.getString("room_number"),
                    rs.getString("room_type"),
                    rs.getDouble("price_per_night"),
                    rs.getBoolean("is_available")
                );
                rooms.add(room);
            }
            
        } catch (SQLException e) {
            throw new HotelManagementException("Error retrieving rooms: " + e.getMessage());
        }
        return rooms;
    }

    /**
     * Finds an available room by type.
     * @param roomType Type of room to find
     * @return Room object if found, null otherwise
     * @throws HotelManagementException if search fails
     */
    public static Room findAvailableRoom(String roomType) throws HotelManagementException {
        String sql = "SELECT * FROM rooms WHERE room_type = ? AND is_available = true LIMIT 1";
        
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, roomType);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return new Room(
                    rs.getString("room_number"),
                    rs.getString("room_type"),
                    rs.getDouble("price_per_night"),
                    true
                );
            }
            return null;
            
        } catch (SQLException e) {
            throw new HotelManagementException("Error finding available room: " + e.getMessage());
        }
    }

    /**
     * Creates a new booking in the database.
     * @param booking Booking object to create
     * @throws HotelManagementException if booking cannot be created
     */
    public static void createBooking(Booking booking) throws HotelManagementException {
        String sql = "INSERT INTO bookings (booking_id, customer_name, room_number, check_in_date, check_out_date, is_active) VALUES (?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, booking.getBookingId());
            stmt.setString(2, booking.getCustomerName());
            stmt.setString(3, booking.getRoomNumber());
            stmt.setDate(4, Date.valueOf(booking.getCheckInDate()));
            stmt.setDate(5, Date.valueOf(booking.getCheckOutDate()));
            stmt.setBoolean(6, booking.isActive());
            
            stmt.executeUpdate();
            
            // Update room availability
            updateRoomAvailability(booking.getRoomNumber(), false);
            
        } catch (SQLException e) {
            throw new HotelManagementException("Error creating booking: " + e.getMessage());
        }
    }

    /**
     * Retrieves all bookings from the database.
     * @return List of all bookings
     * @throws HotelManagementException if bookings cannot be retrieved
     */
    public static List<Booking> getAllBookings() throws HotelManagementException {
        List<Booking> bookings = new ArrayList<>();
        String sql = "SELECT * FROM bookings ORDER BY check_in_date DESC";
        
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                Booking booking = new Booking(
                    rs.getString("booking_id"),
                    rs.getString("customer_name"),
                    rs.getString("room_number"),
                    rs.getDate("check_in_date").toLocalDate(),
                    rs.getDate("check_out_date").toLocalDate()
                );
                booking.setActive(rs.getBoolean("is_active"));
                bookings.add(booking);
            }
            
        } catch (SQLException e) {
            throw new HotelManagementException("Error retrieving bookings: " + e.getMessage());
        }
        return bookings;
    }

    /**
     * Generates a bill for a booking.
     * @param booking Booking to generate bill for
     * @return Generated Bill object
     * @throws HotelManagementException if bill cannot be generated
     */
    public static Bill generateBill(Booking booking) throws HotelManagementException {
        try {
            // Get room price
            Room room = getRoomByNumber(booking.getRoomNumber());
            if (room == null) {
                throw new HotelManagementException("Room not found: " + booking.getRoomNumber());
            }
            
            // Calculate total amount
            long nights = booking.calculateNights();
            double totalAmount = nights * room.getPricePerNight();
            
            // Create bill ID
            String billId = "BILL_" + System.currentTimeMillis();
            Bill bill = new Bill(billId, booking.getBookingId(), totalAmount);
            
            // Insert into database
            String sql = "INSERT INTO bills (bill_id, booking_id, total_amount, generated_date, is_paid) VALUES (?, ?, ?, ?, ?)";
            
            try (Connection conn = getConnection();
                 PreparedStatement stmt = conn.prepareStatement(sql)) {
                
                stmt.setString(1, bill.getBillId());
                stmt.setString(2, bill.getBookingId());
                stmt.setDouble(3, bill.getTotalAmount());
                stmt.setTimestamp(4, Timestamp.valueOf(bill.getGeneratedDate()));
                stmt.setBoolean(5, bill.isPaid());
                
                stmt.executeUpdate();
            }
            
            return bill;
            
        } catch (SQLException e) {
            throw new HotelManagementException("Error generating bill: " + e.getMessage());
        }
    }

    /**
     * Updates the status of a bill.
     * @param billId Bill ID to update
     * @param isPaid New payment status
     * @throws HotelManagementException if update fails
     */
    public static void updateBillStatus(String billId, boolean isPaid) throws HotelManagementException {
        String sql = "UPDATE bills SET is_paid = ? WHERE bill_id = ?";
        
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setBoolean(1, isPaid);
            stmt.setString(2, billId);
            
            int affectedRows = stmt.executeUpdate();
            if (affectedRows == 0) {
                throw new HotelManagementException("Bill not found");
            }
            
        } catch (SQLException e) {
            throw new HotelManagementException("Error updating bill status: " + e.getMessage());
        }
    }

    /**
     * Retrieves all bills from the database.
     * @return List of all bills
     * @throws HotelManagementException if bills cannot be retrieved
     */
    public static List<Bill> getAllBills() throws HotelManagementException {
        List<Bill> bills = new ArrayList<>();
        String sql = "SELECT * FROM bills ORDER BY generated_date DESC";
        
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                Bill bill = new Bill(
                    rs.getString("bill_id"),
                    rs.getString("booking_id"),
                    rs.getDouble("total_amount")
                );
                bill.setPaid(rs.getBoolean("is_paid"));
                bills.add(bill);
            }
            
        } catch (SQLException e) {
            throw new HotelManagementException("Error retrieving bills: " + e.getMessage());
        }
        return bills;
    }

    /**
     * Gets a room by room number.
     * @param roomNumber Room number to find
     * @return Room object if found
     * @throws SQLException if database error occurs
     * @throws HotelManagementException if room not found
     */
    public static Room getRoomByNumber(String roomNumber) throws SQLException, HotelManagementException {
        String sql = "SELECT * FROM rooms WHERE room_number = ?";
        
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, roomNumber);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return new Room(
                    rs.getString("room_number"),
                    rs.getString("room_type"),
                    rs.getDouble("price_per_night"),
                    rs.getBoolean("is_available")
                );
            }
            throw new HotelManagementException("Room not found");
        }
    }

    /**
     * Updates room availability.
     * @param roomNumber Room number to update
     * @param available New availability status
     * @throws SQLException if database error occurs
     */
    private static void updateRoomAvailability(String roomNumber, boolean available) throws SQLException {
        String sql = "UPDATE rooms SET is_available = ? WHERE room_number = ?";
        
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setBoolean(1, available);
            stmt.setString(2, roomNumber);
            stmt.executeUpdate();
        }
    }
}