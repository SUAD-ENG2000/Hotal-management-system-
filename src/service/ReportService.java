package service;

import database.RoomDAO;
import database.BookingDAO;
import database.BillDAO;
import models.Room;
import models.Booking;
import models.Bill;
import exceptions.HotelManagementException;
import java.time.LocalDate;
import java.util.List;

public class ReportService {
    private RoomDAO roomDAO;
    private BookingDAO bookingDAO;
    private BillDAO billDAO;
    
    public ReportService() {
        this.roomDAO = new RoomDAO();
        this.bookingDAO = new BookingDAO();
        this.billDAO = new BillDAO();
    }
    
    
    public String generateRoomOccupancyReport() throws HotelManagementException {
        List<Room> rooms = roomDAO.getAllRooms();
        StringBuilder report = new StringBuilder();
        
        report.append("=".repeat(60)).append("\n");
        report.append("                  ROOM OCCUPANCY REPORT                  \n");
        report.append("=".repeat(60)).append("\n");
        report.append("Generated on: ").append(LocalDate.now()).append("\n\n");
        
        int available = 0;
        int occupied = 0;
        double totalRevenuePotential = 0;
        
        report.append(String.format("%-10s %-12s %-15s %-12s %-15s\n", 
            "Room No", "Type", "Price/Night", "Status", "Monthly Potential"));
        report.append("-".repeat(64)).append("\n");
        
        for (Room room : rooms) {
            String status = room.isAvailable() ? "Available" : "Occupied";
            String statusIcon = room.isAvailable() ? "✅" : "⭕";
            
            if (room.isAvailable()) available++;
            else occupied++;
            
            
            double monthlyPotential = room.getPricePerNight() * 20;
            totalRevenuePotential += monthlyPotential;
            
            report.append(String.format("%-10s %-12s $%-14.2f %-4s %-8s $%-14.2f\n",
                room.getRoomNumber(),
                room.getRoomType(),
                room.getPricePerNight(),
                statusIcon,
                status,
                monthlyPotential));
        }
        
        report.append("\n").append("-".repeat(60)).append("\n");
        report.append("SUMMARY:\n");
        report.append("Total Rooms: ").append(rooms.size()).append("\n");
        report.append("Available Rooms: ").append(available).append("\n");
        report.append("Occupied Rooms: ").append(occupied).append("\n");
        double occupancyRate = rooms.size() > 0 ? (occupied * 100.0) / rooms.size() : 0;
        report.append("Occupancy Rate: ").append(String.format("%.1f%%", occupancyRate)).append("\n");
        report.append("Total Monthly Revenue Potential: $").append(String.format("%.2f", totalRevenuePotential)).append("\n");
        report.append("=".repeat(60)).append("\n");
        
        return report.toString();
    }
    
    
    public String generateFinancialReport() throws HotelManagementException {
        List<Bill> bills = billDAO.getAllBills();
        StringBuilder report = new StringBuilder();
        
        report.append("=".repeat(70)).append("\n");
        report.append("                     FINANCIAL REPORT                     \n");
        report.append("=".repeat(70)).append("\n");
        report.append("Generated on: ").append(LocalDate.now()).append("\n\n");
        
        double totalPaid = 0;
        double totalUnpaid = 0;
        int paidCount = 0;
        int unpaidCount = 0;
        
        report.append("BILL DETAILS:\n");
        report.append(String.format("%-15s %-15s %-12s %-12s %-10s\n", 
            "Bill ID", "Booking ID", "Amount", "Generated", "Status"));
        report.append("-".repeat(64)).append("\n");
        
        for (Bill bill : bills) {
            String status = bill.isPaid() ? "Paid ✅" : "Unpaid ❌";
            String date = bill.getGeneratedDate().toLocalDate().toString();
            
            report.append(String.format("%-15s %-15s $%-11.2f %-12s %-10s\n",
                bill.getBillId(),
                bill.getBookingId(),
                bill.getTotalAmount(),
                date,
                status));
            
            if (bill.isPaid()) {
                totalPaid += bill.getTotalAmount();
                paidCount++;
            } else {
                totalUnpaid += bill.getTotalAmount();
                unpaidCount++;
            }
        }
        
        report.append("\n").append("=".repeat(70)).append("\n");
        report.append("FINANCIAL SUMMARY:\n");
        report.append("-".repeat(70)).append("\n");
        report.append("Total Bills Generated: ").append(bills.size()).append("\n");
        report.append("Paid Bills: ").append(paidCount).append(" ($").append(String.format("%.2f", totalPaid)).append(")\n");
        report.append("Unpaid Bills: ").append(unpaidCount).append(" ($").append(String.format("%.2f", totalUnpaid)).append(")\n");
        report.append("-".repeat(70)).append("\n");
        report.append("Total Revenue: $").append(String.format("%.2f", totalPaid)).append("\n");
        report.append("Outstanding Amount: $").append(String.format("%.2f", totalUnpaid)).append("\n");
        report.append("Total Billed Amount: $").append(String.format("%.2f", totalPaid + totalUnpaid)).append("\n");
        
        double collectionRate = (totalPaid + totalUnpaid) > 0 ? 
            (totalPaid * 100.0) / (totalPaid + totalUnpaid) : 0;
        report.append("Collection Rate: ").append(String.format("%.1f%%", collectionRate)).append("\n");
        report.append("=".repeat(70)).append("\n");
        
        return report.toString();
    }
    
    
    public String generateAnalyticalReport() throws HotelManagementException {
        List<Booking> bookings = bookingDAO.getAllBookings();
        List<Room> rooms = roomDAO.getAllRooms();
        
        StringBuilder report = new StringBuilder();
        
        report.append("=".repeat(60)).append("\n");
        report.append("                ANALYTICAL REPORT                \n");
        report.append("=".repeat(60)).append("\n");
        report.append("Generated on: ").append(LocalDate.now()).append("\n\n");
        
        
        long activeBookings = bookings.stream().filter(Booking::isActive).count();
        long completedBookings = bookings.stream().filter(b -> !b.isActive()).count();
        
        report.append("BOOKING STATISTICS:\n");
        report.append("-".repeat(60)).append("\n");
        report.append("Total Bookings: ").append(bookings.size()).append("\n");
        report.append("Active Bookings: ").append(activeBookings).append("\n");
        report.append("Completed Bookings: ").append(completedBookings).append("\n");
        report.append("Cancellation Rate: ").append("0.0%").append(" (مثال)\n\n");
        
        
        long availableRooms = rooms.stream().filter(Room::isAvailable).count();
        long occupiedRooms = rooms.size() - availableRooms;
        double occupancyRate = rooms.size() > 0 ? (occupiedRooms * 100.0) / rooms.size() : 0;
        
        report.append("ROOM STATISTICS:\n");
        report.append("-".repeat(60)).append("\n");
        report.append("Total Rooms: ").append(rooms.size()).append("\n");
        report.append("Available Rooms: ").append(availableRooms).append("\n");
        report.append("Occupied Rooms: ").append(occupiedRooms).append("\n");
        report.append("Occupancy Rate: ").append(String.format("%.1f%%", occupancyRate)).append("\n\n");
        
        
        report.append("RECOMMENDATIONS:\n");
        report.append("-".repeat(60)).append("\n");
        if (occupancyRate < 50) {
            report.append("⚠️  Low occupancy rate! Consider promotional offers.\n");
        } else if (occupancyRate > 80) {
            report.append("✅ High occupancy rate! Consider adding more rooms.\n");
        } else {
            report.append("📊 Healthy occupancy rate. Maintain current strategy.\n");
        }
        
        if (availableRooms > (rooms.size() * 0.3)) {
            report.append("💡 Many rooms available. Consider last-minute discounts.\n");
        }
        
        report.append("=".repeat(60)).append("\n");
        
        return report.toString();
    }
}