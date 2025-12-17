package service;

import database.StatisticsDAO;
import exceptions.HotelManagementException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

public class StatisticsService {
    private StatisticsDAO statisticsDAO;
    
    public StatisticsService() {
        this.statisticsDAO = new StatisticsDAO();
    }
    
    public Map<String, Object> getDashboardStats() throws HotelManagementException {
        Map<String, Object> stats = new HashMap<>();
        try {
            stats.put("totalRooms", statisticsDAO.getTotalRooms());
            stats.put("availableRooms", statisticsDAO.getAvailableRooms());
            stats.put("activeBookings", statisticsDAO.getActiveBookings());
            stats.put("totalRevenue", statisticsDAO.getTotalRevenue());
            stats.put("todayCheckIns", statisticsDAO.getTodayCheckIns());
            stats.put("todayCheckOuts", statisticsDAO.getTodayCheckOuts());
            
            
            int totalRooms = statisticsDAO.getTotalRooms();
            int occupiedRooms = totalRooms - statisticsDAO.getAvailableRooms();
            double occupancyRate = totalRooms > 0 ? (occupiedRooms * 100.0) / totalRooms : 0;
            stats.put("occupancyRate", Math.round(occupancyRate * 10.0) / 10.0);
            
        } catch (SQLException e) {
            throw new HotelManagementException("Error fetching statistics: " + e.getMessage());
        }
        return stats;
    }
    
    public Map<String, Object> getMonthlyReport(int year, int month) throws HotelManagementException {
        Map<String, Object> report = new HashMap<>();
        try {
            report.put("month", month);
            report.put("year", year);
            report.put("monthlyRevenue", statisticsDAO.getMonthlyRevenue(year, month));
            report.put("totalRooms", statisticsDAO.getTotalRooms());
            report.put("availableRooms", statisticsDAO.getAvailableRooms());
            
            
            report.put("newBookings", 0);
            report.put("cancelledBookings", 0);
            
        } catch (SQLException e) {
            throw new HotelManagementException("Error generating monthly report: " + e.getMessage());
        }
        return report;
    }
}