package database;

import models.Booking;
import exceptions.HotelManagementException;
import database.DatabaseManager;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class BookingDAO {
    private DatabaseManager dbManager;
    
    public BookingDAO() {
        this.dbManager = DatabaseManager.getInstance();
    }
    
    public void createBooking(Booking booking) throws HotelManagementException {
        String sql = "INSERT INTO bookings (booking_id, customer_name, room_number, check_in_date, check_out_date, is_active) VALUES (?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = dbManager.getConnection();
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
    
    public List<Booking> getAllBookings() throws HotelManagementException {
        List<Booking> bookings = new ArrayList<>();
        String sql = "SELECT * FROM bookings ORDER BY check_in_date DESC";
        
        try (Connection conn = dbManager.getConnection();
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
    
    public Booking getBookingById(String bookingId) throws HotelManagementException {
        String sql = "SELECT * FROM bookings WHERE booking_id = ?";
        
        try (Connection conn = dbManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, bookingId);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                Booking booking = new Booking(
                    rs.getString("booking_id"),
                    rs.getString("customer_name"),
                    rs.getString("room_number"),
                    rs.getDate("check_in_date").toLocalDate(),
                    rs.getDate("check_out_date").toLocalDate()
                );
                booking.setActive(rs.getBoolean("is_active"));
                return booking;
            }
            throw new HotelManagementException("Booking not found: " + bookingId);
            
        } catch (SQLException e) {
            throw new HotelManagementException("Error retrieving booking: " + e.getMessage());
        }
    }
    
    public List<Booking> getActiveBookings() throws HotelManagementException {
        String sql = "SELECT * FROM bookings WHERE is_active = true ORDER BY check_in_date";
        
        try (Connection conn = dbManager.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            List<Booking> bookings = new ArrayList<>();
            while (rs.next()) {
                Booking booking = new Booking(
                    rs.getString("booking_id"),
                    rs.getString("customer_name"),
                    rs.getString("room_number"),
                    rs.getDate("check_in_date").toLocalDate(),
                    rs.getDate("check_out_date").toLocalDate()
                );
                booking.setActive(true);
                bookings.add(booking);
            }
            return bookings;
            
        } catch (SQLException e) {
            throw new HotelManagementException("Error retrieving active bookings: " + e.getMessage());
        }
    }
    
    public List<Booking> getBookingsByDateRange(LocalDate startDate, LocalDate endDate) throws HotelManagementException {
        String sql = "SELECT * FROM bookings WHERE check_in_date >= ? AND check_out_date <= ? ORDER BY check_in_date";
        
        try (Connection conn = dbManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setDate(1, Date.valueOf(startDate));
            stmt.setDate(2, Date.valueOf(endDate));
            ResultSet rs = stmt.executeQuery();
            
            List<Booking> bookings = new ArrayList<>();
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
            return bookings;
            
        } catch (SQLException e) {
            throw new HotelManagementException("Error retrieving bookings by date range: " + e.getMessage());
        }
    }
    
    public void cancelBooking(String bookingId) throws HotelManagementException {
        String sql = "UPDATE bookings SET is_active = false WHERE booking_id = ?";
        
        try (Connection conn = dbManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, bookingId);
            int affectedRows = stmt.executeUpdate();
            
            if (affectedRows == 0) {
                throw new HotelManagementException("Booking not found");
            }
            
            // Get room number and update availability
            Booking booking = getBookingById(bookingId);
            updateRoomAvailability(booking.getRoomNumber(), true);
            
        } catch (SQLException e) {
            throw new HotelManagementException("Error canceling booking: " + e.getMessage());
        }
    }
    
    public int getBookingCount() throws HotelManagementException {
        String sql = "SELECT COUNT(*) as count FROM bookings";
        
        try (Connection conn = dbManager.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            return rs.next() ? rs.getInt("count") : 0;
            
        } catch (SQLException e) {
            throw new HotelManagementException("Error counting bookings: " + e.getMessage());
        }
    }
    
    private void updateRoomAvailability(String roomNumber, boolean available) throws HotelManagementException {
        String sql = "UPDATE rooms SET is_available = ? WHERE room_number = ?";
        
        try (Connection conn = dbManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setBoolean(1, available);
            stmt.setString(2, roomNumber);
            stmt.executeUpdate();
            
        } catch (SQLException e) {
            throw new HotelManagementException("Error updating room availability: " + e.getMessage());
        }
    }
}