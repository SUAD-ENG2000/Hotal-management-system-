package database;

import models.Bill;
import exceptions.HotelManagementException;
import database.DatabaseManager;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class BillDAO {
    private DatabaseManager dbManager;
    
    public BillDAO() {
        this.dbManager = DatabaseManager.getInstance();
    }
    
    public void createBill(Bill bill) throws HotelManagementException {
        String sql = "INSERT INTO bills (bill_id, booking_id, total_amount, generated_date, is_paid) VALUES (?, ?, ?, ?, ?)";
        
        try (Connection conn = dbManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, bill.getBillId());
            stmt.setString(2, bill.getBookingId());
            stmt.setDouble(3, bill.getTotalAmount());
            stmt.setTimestamp(4, Timestamp.valueOf(bill.getGeneratedDate()));
            stmt.setBoolean(5, bill.isPaid());
            
            stmt.executeUpdate();
            
        } catch (SQLException e) {
            throw new HotelManagementException("Error creating bill: " + e.getMessage());
        }
    }
    
    public List<Bill> getAllBills() throws HotelManagementException {
        List<Bill> bills = new ArrayList<>();
        String sql = "SELECT * FROM bills ORDER BY generated_date DESC";
        
        try (Connection conn = dbManager.getConnection();
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
    
    public Bill getBillById(String billId) throws HotelManagementException {
        String sql = "SELECT * FROM bills WHERE bill_id = ?";
        
        try (Connection conn = dbManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, billId);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                Bill bill = new Bill(
                    rs.getString("bill_id"),
                    rs.getString("booking_id"),
                    rs.getDouble("total_amount")
                );
                bill.setPaid(rs.getBoolean("is_paid"));
                return bill;
            }
            throw new HotelManagementException("Bill not found: " + billId);
            
        } catch (SQLException e) {
            throw new HotelManagementException("Error retrieving bill: " + e.getMessage());
        }
    }
    
    public void updateBillStatus(String billId, boolean isPaid) throws HotelManagementException {
        String sql = "UPDATE bills SET is_paid = ?, paid_date = ? WHERE bill_id = ?";
        
        try (Connection conn = dbManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setBoolean(1, isPaid);
            stmt.setTimestamp(2, isPaid ? new Timestamp(System.currentTimeMillis()) : null);
            stmt.setString(3, billId);
            
            int affectedRows = stmt.executeUpdate();
            if (affectedRows == 0) {
                throw new HotelManagementException("Bill not found");
            }
            
        } catch (SQLException e) {
            throw new HotelManagementException("Error updating bill status: " + e.getMessage());
        }
    }
    
    public List<Bill> getUnpaidBills() throws HotelManagementException {
        String sql = "SELECT * FROM bills WHERE is_paid = false ORDER BY generated_date";
        
        try (Connection conn = dbManager.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            List<Bill> bills = new ArrayList<>();
            while (rs.next()) {
                Bill bill = new Bill(
                    rs.getString("bill_id"),
                    rs.getString("booking_id"),
                    rs.getDouble("total_amount")
                );
                bill.setPaid(false);
                bills.add(bill);
            }
            return bills;
            
        } catch (SQLException e) {
            throw new HotelManagementException("Error retrieving unpaid bills: " + e.getMessage());
        }
    }
    
    public List<Bill> getBillsByDateRange(LocalDate startDate, LocalDate endDate) throws HotelManagementException {
        String sql = "SELECT * FROM bills WHERE DATE(generated_date) >= ? AND DATE(generated_date) <= ? ORDER BY generated_date";
        
        try (Connection conn = dbManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setDate(1, Date.valueOf(startDate));
            stmt.setDate(2, Date.valueOf(endDate));
            ResultSet rs = stmt.executeQuery();
            
            List<Bill> bills = new ArrayList<>();
            while (rs.next()) {
                Bill bill = new Bill(
                    rs.getString("bill_id"),
                    rs.getString("booking_id"),
                    rs.getDouble("total_amount")
                );
                bill.setPaid(rs.getBoolean("is_paid"));
                bills.add(bill);
            }
            return bills;
            
        } catch (SQLException e) {
            throw new HotelManagementException("Error retrieving bills by date range: " + e.getMessage());
        }
    }
    
    public double getTotalRevenue() throws HotelManagementException {
        String sql = "SELECT SUM(total_amount) as total FROM bills WHERE is_paid = true";
        
        try (Connection conn = dbManager.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            return rs.next() ? rs.getDouble("total") : 0.0;
            
        } catch (SQLException e) {
            throw new HotelManagementException("Error calculating total revenue: " + e.getMessage());
        }
    }
    
    public double getTotalUnpaidAmount() throws HotelManagementException {
        String sql = "SELECT SUM(total_amount) as total FROM bills WHERE is_paid = false";
        
        try (Connection conn = dbManager.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            return rs.next() ? rs.getDouble("total") : 0.0;
            
        } catch (SQLException e) {
            throw new HotelManagementException("Error calculating unpaid amount: " + e.getMessage());
        }
    }
    
    public boolean billExistsForBooking(String bookingId) throws HotelManagementException {
        String sql = "SELECT COUNT(*) as count FROM bills WHERE booking_id = ?";
        
        try (Connection conn = dbManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, bookingId);
            ResultSet rs = stmt.executeQuery();
            
            return rs.next() && rs.getInt("count") > 0;
            
        } catch (SQLException e) {
            throw new HotelManagementException("Error checking bill existence: " + e.getMessage());
        }
    }
}