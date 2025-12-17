package database;
import models.*;
import database.DatabaseManager;
import java.sql.*;

public class StatisticsDAO {
    private DatabaseManager dbManager;
    
    public StatisticsDAO() {
        this.dbManager = DatabaseManager.getInstance();
    }
    
    public int getTotalRooms() throws SQLException {
        String sql = "SELECT COUNT(*) as total FROM rooms";
        try (Connection conn = dbManager.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            return rs.next() ? rs.getInt("total") : 0;
        }
    }
    
    public int getAvailableRooms() throws SQLException {
        String sql = "SELECT COUNT(*) as available FROM rooms WHERE is_available = true";
        try (Connection conn = dbManager.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            return rs.next() ? rs.getInt("available") : 0;
        }
    }
    
    public int getActiveBookings() throws SQLException {
        String sql = "SELECT COUNT(*) as active FROM bookings WHERE is_active = true";
        try (Connection conn = dbManager.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            return rs.next() ? rs.getInt("active") : 0;
        }
    }
    
    public double getTotalRevenue() throws SQLException {
        String sql = "SELECT SUM(total_amount) as revenue FROM bills WHERE is_paid = true";
        try (Connection conn = dbManager.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            return rs.next() ? rs.getDouble("revenue") : 0.0;
        }
    }
    
    public double getMonthlyRevenue(int year, int month) throws SQLException {
        String sql = "SELECT SUM(total_amount) as monthly_revenue FROM bills " +
                    "WHERE is_paid = true AND YEAR(generated_date) = ? AND MONTH(generated_date) = ?";
        try (Connection conn = dbManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, year);
            stmt.setInt(2, month);
            ResultSet rs = stmt.executeQuery();
            return rs.next() ? rs.getDouble("monthly_revenue") : 0.0;
        }
    }
    
    public int getTodayCheckIns() throws SQLException {
        String sql = "SELECT COUNT(*) as today_checkins FROM bookings " +
                    "WHERE DATE(check_in_date) = CURDATE()";
        try (Connection conn = dbManager.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            return rs.next() ? rs.getInt("today_checkins") : 0;
        }
    }
    
    public int getTodayCheckOuts() throws SQLException {
        String sql = "SELECT COUNT(*) as today_checkouts FROM bookings " +
                    "WHERE DATE(check_out_date) = CURDATE()";
        try (Connection conn = dbManager.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            return rs.next() ? rs.getInt("today_checkouts") : 0;
        }
    }
}