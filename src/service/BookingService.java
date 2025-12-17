package service;

import models.Booking;
import database.BookingDAO;
import database.RoomDAO;
import exceptions.HotelManagementException;
import java.time.LocalDate;
import java.util.List;

public class BookingService {
    private BookingDAO bookingDAO;
    private RoomDAO roomDAO;
    
    public BookingService() {
        this.bookingDAO = new BookingDAO();
        this.roomDAO = new RoomDAO();
    }
    
    public void createBooking(Booking booking) throws HotelManagementException {
        // Validate dates
        if (!booking.areDatesValid()) {
            throw new HotelManagementException("Invalid booking dates. Check-out must be after check-in.");
        }
        
        // Check room availability
        try {
            var room = roomDAO.getRoomByNumber(booking.getRoomNumber());
            if (!room.isAvailable()) {
                throw new HotelManagementException("Room " + booking.getRoomNumber() + " is not available");
            }
        } catch (HotelManagementException e) {
            throw new HotelManagementException("Room not found: " + booking.getRoomNumber());
        }
        
        // Create booking
        bookingDAO.createBooking(booking);
    }
    
    public List<Booking> getAllBookings() throws HotelManagementException {
        return bookingDAO.getAllBookings();
    }
    
    public List<Booking> getActiveBookings() throws HotelManagementException {
        return bookingDAO.getActiveBookings();
    }
    
    public List<Booking> getBookingsByDateRange(LocalDate startDate, LocalDate endDate) throws HotelManagementException {
        return bookingDAO.getBookingsByDateRange(startDate, endDate);
    }
    
    public Booking getBookingById(String bookingId) throws HotelManagementException {
        return bookingDAO.getBookingById(bookingId);
    }
    
    public void cancelBooking(String bookingId) throws HotelManagementException {
        bookingDAO.cancelBooking(bookingId);
    }
    
    public int getTotalBookings() throws HotelManagementException {
        return bookingDAO.getBookingCount();
    }
    
    public int getActiveBookingCount() throws HotelManagementException {
        return bookingDAO.getActiveBookings().size();
    }
    
    public List<Booking> getTodayCheckIns() throws HotelManagementException {
        LocalDate today = LocalDate.now();
        return bookingDAO.getBookingsByDateRange(today, today);
    }
    
    public List<Booking> getUpcomingCheckIns(int daysAhead) throws HotelManagementException {
        LocalDate today = LocalDate.now();
        LocalDate futureDate = today.plusDays(daysAhead);
        return bookingDAO.getBookingsByDateRange(today, futureDate);
    }
    
    public double calculateTotalNights() throws HotelManagementException {
        List<Booking> bookings = bookingDAO.getAllBookings();
        double totalNights = 0;
        for (Booking booking : bookings) {
            if (booking.isActive()) {
                totalNights += booking.calculateNights();
            }
        }
        return totalNights;
    }
    
    public String generateBookingReport(LocalDate startDate, LocalDate endDate) throws HotelManagementException {
        List<Booking> bookings = bookingDAO.getBookingsByDateRange(startDate, endDate);
        
        StringBuilder report = new StringBuilder();
        report.append("BOOKING REPORT\n");
        report.append("Period: ").append(startDate).append(" to ").append(endDate).append("\n");
        report.append("Generated on: ").append(LocalDate.now()).append("\n\n");
        
        int active = 0;
        int cancelled = 0;
        double totalRevenue = 0;
        
        for (Booking booking : bookings) {
            if (booking.isActive()) {
                active++;
                // Calculate revenue (simplified)
                try {
                    var room = roomDAO.getRoomByNumber(booking.getRoomNumber());
                    totalRevenue += booking.calculateNights() * room.getPricePerNight();
                } catch (Exception e) {
                    // Room not found, skip revenue calculation
                }
            } else {
                cancelled++;
            }
        }
        
        report.append("Total Bookings: ").append(bookings.size()).append("\n");
        report.append("Active Bookings: ").append(active).append("\n");
        report.append("Cancelled Bookings: ").append(cancelled).append("\n");
        report.append("Cancellation Rate: ").append(String.format("%.1f%%", 
            (cancelled * 100.0) / bookings.size())).append("\n");
        report.append("Estimated Revenue: $").append(String.format("%.2f", totalRevenue)).append("\n");
        
        return report.toString();
    }
}