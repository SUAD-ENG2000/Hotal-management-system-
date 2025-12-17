package database;

import models.User;
import models.Manager;
import models.Receptionist;
import exceptions.HotelManagementException;
import database.DatabaseManager;
import java.sql.*;

public class UserDAO {
    private DatabaseManager dbManager;
    
    public UserDAO() {
        this.dbManager = DatabaseManager.getInstance();
    }
    
    public User authenticateUser(String userId, String password) throws HotelManagementException {
        String sql = "SELECT * FROM users WHERE user_id = ? AND password = ?";
        
        try (Connection conn = dbManager.getConnection();
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
    
    public User getUserById(String userId) throws HotelManagementException {
        String sql = "SELECT * FROM users WHERE user_id = ?";
        
        try (Connection conn = dbManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, userId);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                String role = rs.getString("role");
                String password = rs.getString("password");
                if ("Manager".equals(role)) {
                    return new Manager(userId, password);
                } else if ("Receptionist".equals(role)) {
                    return new Receptionist(userId, password);
                }
            }
            throw new HotelManagementException("User not found: " + userId);
            
        } catch (SQLException e) {
            throw new HotelManagementException("Error retrieving user: " + e.getMessage());
        }
    }
    
    public void addUser(String userId, String password, String role) throws HotelManagementException {
        String sql = "INSERT INTO users (user_id, password, role) VALUES (?, ?, ?)";
        
        try (Connection conn = dbManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, userId);
            stmt.setString(2, password);
            stmt.setString(3, role);
            
            stmt.executeUpdate();
            
        } catch (SQLException e) {
            throw new HotelManagementException("Error adding user: " + e.getMessage());
        }
    }
    
    public void updateUserPassword(String userId, String newPassword) throws HotelManagementException {
        String sql = "UPDATE users SET password = ? WHERE user_id = ?";
        
        try (Connection conn = dbManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, newPassword);
            stmt.setString(2, userId);
            
            int affectedRows = stmt.executeUpdate();
            if (affectedRows == 0) {
                throw new HotelManagementException("User not found");
            }
            
        } catch (SQLException e) {
            throw new HotelManagementException("Error updating password: " + e.getMessage());
        }
    }
    
    public void deleteUser(String userId) throws HotelManagementException {
        String sql = "DELETE FROM users WHERE user_id = ?";
        
        try (Connection conn = dbManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, userId);
            
            int affectedRows = stmt.executeUpdate();
            if (affectedRows == 0) {
                throw new HotelManagementException("User not found");
            }
            
        } catch (SQLException e) {
            throw new HotelManagementException("Error deleting user: " + e.getMessage());
        }
    }
    
    public boolean userExists(String userId) throws HotelManagementException {
        String sql = "SELECT COUNT(*) as count FROM users WHERE user_id = ?";
        
        try (Connection conn = dbManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, userId);
            ResultSet rs = stmt.executeQuery();
            
            return rs.next() && rs.getInt("count") > 0;
            
        } catch (SQLException e) {
            throw new HotelManagementException("Error checking user existence: " + e.getMessage());
        }
    }
    
    public int getUserCount() throws HotelManagementException {
        String sql = "SELECT COUNT(*) as count FROM users";
        
        try (Connection conn = dbManager.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            return rs.next() ? rs.getInt("count") : 0;
            
        } catch (SQLException e) {
            throw new HotelManagementException("Error counting users: " + e.getMessage());
        }
    }
}