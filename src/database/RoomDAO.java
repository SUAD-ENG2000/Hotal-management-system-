package database;

import models.Room;
import exceptions.HotelManagementException;
import database.DatabaseManager;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class RoomDAO {
    private DatabaseManager dbManager;
    
    public RoomDAO() {
        this.dbManager = DatabaseManager.getInstance();
    }
    
    public void addRoom(Room room) throws HotelManagementException {
        String sql = "INSERT INTO rooms (room_number, room_type, price_per_night, is_available) VALUES (?, ?, ?, ?)";
        
        try (Connection conn = dbManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, room.getRoomNumber());
            stmt.setString(2, room.getRoomType());
            stmt.setDouble(3, room.getPricePerNight());
            stmt.setBoolean(4, room.isAvailable());
            
            stmt.executeUpdate();
            
        } catch (SQLException e) {
            if (e.getErrorCode() == 1062) { // Duplicate entry
                throw new HotelManagementException("Room number already exists");
            }
            throw new HotelManagementException("Error adding room: " + e.getMessage());
        }
    }
    
    public void removeRoom(String roomNumber) throws HotelManagementException {
        String sql = "DELETE FROM rooms WHERE room_number = ?";
        
        try (Connection conn = dbManager.getConnection();
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
    
    public List<Room> getAllRooms() throws HotelManagementException {
        List<Room> rooms = new ArrayList<>();
        String sql = "SELECT * FROM rooms ORDER BY room_number";
        
        try (Connection conn = dbManager.getConnection();
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
    
    public Room getRoomByNumber(String roomNumber) throws HotelManagementException {
        String sql = "SELECT * FROM rooms WHERE room_number = ?";
        
        try (Connection conn = dbManager.getConnection();
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
            
        } catch (SQLException e) {
            throw new HotelManagementException("Error retrieving room: " + e.getMessage());
        }
    }
    
    public Room findAvailableRoom(String roomType) throws HotelManagementException {
        String sql = "SELECT * FROM rooms WHERE room_type = ? AND is_available = true LIMIT 1";
        
        try (Connection conn = dbManager.getConnection();
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
    
    public List<Room> getAvailableRooms() throws HotelManagementException {
        String sql = "SELECT * FROM rooms WHERE is_available = true ORDER BY room_number";
        
        try (Connection conn = dbManager.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            List<Room> rooms = new ArrayList<>();
            while (rs.next()) {
                Room room = new Room(
                    rs.getString("room_number"),
                    rs.getString("room_type"),
                    rs.getDouble("price_per_night"),
                    true
                );
                rooms.add(room);
            }
            return rooms;
            
        } catch (SQLException e) {
            throw new HotelManagementException("Error retrieving available rooms: " + e.getMessage());
        }
    }
    
    public List<Room> getRoomsByType(String roomType) throws HotelManagementException {
        String sql = "SELECT * FROM rooms WHERE room_type = ? ORDER BY room_number";
        
        try (Connection conn = dbManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, roomType);
            ResultSet rs = stmt.executeQuery();
            
            List<Room> rooms = new ArrayList<>();
            while (rs.next()) {
                Room room = new Room(
                    rs.getString("room_number"),
                    rs.getString("room_type"),
                    rs.getDouble("price_per_night"),
                    rs.getBoolean("is_available")
                );
                rooms.add(room);
            }
            return rooms;
            
        } catch (SQLException e) {
            throw new HotelManagementException("Error retrieving rooms by type: " + e.getMessage());
        }
    }
    
    public void updateRoomAvailability(String roomNumber, boolean available) throws HotelManagementException {
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
    
    public void updateRoomPrice(String roomNumber, double newPrice) throws HotelManagementException {
        String sql = "UPDATE rooms SET price_per_night = ? WHERE room_number = ?";
        
        try (Connection conn = dbManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setDouble(1, newPrice);
            stmt.setString(2, roomNumber);
            
            int affectedRows = stmt.executeUpdate();
            if (affectedRows == 0) {
                throw new HotelManagementException("Room not found");
            }
            
        } catch (SQLException e) {
            throw new HotelManagementException("Error updating room price: " + e.getMessage());
        }
    }
    
    public int getRoomCount() throws HotelManagementException {
        String sql = "SELECT COUNT(*) as count FROM rooms";
        
        try (Connection conn = dbManager.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            return rs.next() ? rs.getInt("count") : 0;
            
        } catch (SQLException e) {
            throw new HotelManagementException("Error counting rooms: " + e.getMessage());
        }
    }
    
    public int getAvailableRoomCount() throws HotelManagementException {
        String sql = "SELECT COUNT(*) as count FROM rooms WHERE is_available = true";
        
        try (Connection conn = dbManager.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            return rs.next() ? rs.getInt("count") : 0;
            
        } catch (SQLException e) {
            throw new HotelManagementException("Error counting available rooms: " + e.getMessage());
        }
    }
}