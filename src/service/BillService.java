package service;
import models.*;
import models.Bill;
import models.Booking;
import database.BillDAO;
import database.BookingDAO;
import database.RoomDAO;
import exceptions.HotelManagementException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public class BillService {
    private BillDAO billDAO;
    private BookingDAO bookingDAO;
    private RoomDAO roomDAO;
    
    public BillService() {
        this.billDAO = new BillDAO();
        this.bookingDAO = new BookingDAO();
        this.roomDAO = new RoomDAO();
    }
    
    public Bill generateBill(Booking booking) throws HotelManagementException {
        // Check if bill already exists for this booking
        if (billDAO.billExistsForBooking(booking.getBookingId())) {
            throw new HotelManagementException("A bill already exists for this booking");
        }
        
        // Get room price
        Room room = roomDAO.getRoomByNumber(booking.getRoomNumber());
        
        // Calculate total amount
        long nights = booking.calculateNights();
        double totalAmount = nights * room.getPricePerNight();
        
        // Create bill
        String billId = "BILL_" + System.currentTimeMillis();
        Bill bill = new Bill(billId, booking.getBookingId(), totalAmount);
        
        // Save to database
        billDAO.createBill(bill);
        
        return bill;
    }
    
    public List<Bill> getAllBills() throws HotelManagementException {
        return billDAO.getAllBills();
    }
    
    public Bill getBillById(String billId) throws HotelManagementException {
        return billDAO.getBillById(billId);
    }
    
    public void markBillAsPaid(String billId) throws HotelManagementException {
        billDAO.updateBillStatus(billId, true);
    }
    
    public List<Bill> getUnpaidBills() throws HotelManagementException {
        return billDAO.getUnpaidBills();
    }
    
    public List<Bill> getBillsByDateRange(LocalDate startDate, LocalDate endDate) throws HotelManagementException {
        return billDAO.getBillsByDateRange(startDate, endDate);
    }
    
    public double getTotalRevenue() throws HotelManagementException {
        return billDAO.getTotalRevenue();
    }
    
    public double getTotalUnpaidAmount() throws HotelManagementException {
        return billDAO.getTotalUnpaidAmount();
    }
    
    public double getMonthlyRevenue(int year, int month) throws HotelManagementException {
        LocalDate startDate = LocalDate.of(year, month, 1);
        LocalDate endDate = startDate.withDayOfMonth(startDate.lengthOfMonth());
        
        List<Bill> monthlyBills = billDAO.getBillsByDateRange(startDate, endDate);
        double revenue = 0;
        for (Bill bill : monthlyBills) {
            if (bill.isPaid()) {
                revenue += bill.getTotalAmount();
            }
        }
        return revenue;
    }
    
    public List<Bill> getTodaysBills() throws HotelManagementException {
        LocalDate today = LocalDate.now();
        return billDAO.getBillsByDateRange(today, today);
    }
    
    public int getBillCount() throws HotelManagementException {
        return billDAO.getAllBills().size();
    }
    
    public int getPaidBillCount() throws HotelManagementException {
        List<Bill> bills = billDAO.getAllBills();
        int paidCount = 0;
        for (Bill bill : bills) {
            if (bill.isPaid()) {
                paidCount++;
            }
        }
        return paidCount;
    }
    
    public String generateFinancialReport(LocalDate startDate, LocalDate endDate) throws HotelManagementException {
        List<Bill> bills = billDAO.getBillsByDateRange(startDate, endDate);
        
        StringBuilder report = new StringBuilder();
        report.append("FINANCIAL REPORT\n");
        report.append("Period: ").append(startDate).append(" to ").append(endDate).append("\n");
        report.append("Generated on: ").append(LocalDate.now()).append("\n\n");
        
        double totalPaid = 0;
        double totalUnpaid = 0;
        int paidCount = 0;
        int unpaidCount = 0;
        
        for (Bill bill : bills) {
            if (bill.isPaid()) {
                totalPaid += bill.getTotalAmount();
                paidCount++;
            } else {
                totalUnpaid += bill.getTotalAmount();
                unpaidCount++;
            }
        }
        
        report.append("Total Bills: ").append(bills.size()).append("\n");
        report.append("Paid Bills: ").append(paidCount).append(" ($").append(String.format("%.2f", totalPaid)).append(")\n");
        report.append("Unpaid Bills: ").append(unpaidCount).append(" ($").append(String.format("%.2f", totalUnpaid)).append(")\n");
        report.append("Total Amount: $").append(String.format("%.2f", totalPaid + totalUnpaid)).append("\n");
        report.append("Collection Rate: ").append(String.format("%.1f%%", 
            (totalPaid * 100.0) / (totalPaid + totalUnpaid))).append("\n");
        
        return report.toString();
    }
}