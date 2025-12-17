package models;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

/**
 * Represents a hotel room booking reservation.
 * Tracks customer details, room assignment, stay dates, and booking status.
 */
public class Booking {
    // Unique identifier for the booking (e.g., BOOK_123456789)
    private String bookingId;
    
    // Full name of the customer making the booking
    private String customerName;
    
    // Room number assigned for this booking
    private String roomNumber;
    
    // Scheduled arrival date (when guest checks in)
    private LocalDate checkInDate;
    
    // Scheduled departure date (when guest checks out)
    private LocalDate checkOutDate;
    
    // Indicates if booking is currently active (not cancelled )
    private boolean isActive;

    /**
     * Creates a new booking instance.
     * @param bookingId Unique identifier for tracking
     * @param customerName Guest's full name
     * @param roomNumber Room number being reserved
     * @param checkInDate Arrival date (must be valid)
     * @param checkOutDate Departure date (must be after check-in)
     */
    public Booking(String bookingId, String customerName, String roomNumber, 
                   LocalDate checkInDate, LocalDate checkOutDate) {
        this.bookingId = bookingId;
        this.customerName = customerName;
        this.roomNumber = roomNumber;
        this.checkInDate = checkInDate;
        this.checkOutDate = checkOutDate;
        this.isActive = true; // New bookings are automatically active
    }

    // Calculate number of nights between check-in and check-out
    // Note: check-out day is not counted as a night (e.g., Jan1-Jan3 = 2 nights)
    public long calculateNights() {
        return ChronoUnit.DAYS.between(checkInDate, checkOutDate);
    }
    
    // Alternative method for calculating nights (same logic, different API)
    public int getNumberOfNights() {
        return (int) checkInDate.until(checkOutDate).getDays();
    }
    
    // Validate dates: check-out must be after check-in and check-in cannot be in the past
    public boolean areDatesValid() {
        return checkOutDate.isAfter(checkInDate) && 
               !checkInDate.isBefore(LocalDate.now());
    }
    
    // Calculate total price for the booking based on nightly rate
    public double calculateTotalPrice(double pricePerNight) {
        return calculateNights() * pricePerNight;
    }

    // Getters and setters
    public String getBookingId() { return bookingId; }
    public void setBookingId(String bookingId) { this.bookingId = bookingId; }
    
    public String getCustomerName() { return customerName; }
    public void setCustomerName(String customerName) { this.customerName = customerName; }
    
    public String getRoomNumber() { return roomNumber; }
    public void setRoomNumber(String roomNumber) { this.roomNumber = roomNumber; }
    
    public LocalDate getCheckInDate() { return checkInDate; }
    public void setCheckInDate(LocalDate checkInDate) { this.checkInDate = checkInDate; }
    
    public LocalDate getCheckOutDate() { return checkOutDate; }
    public void setCheckOutDate(LocalDate checkOutDate) { this.checkOutDate = checkOutDate; }
    
    public boolean isActive() { return isActive; }
    public void setActive(boolean active) { isActive = active; }
    
    @Override
    public String toString() {
        return String.format("Booking[ID: %s, Customer: %s, Room: %s, Check-in: %s, Check-out: %s, Nights: %d, Active: %s]",
            bookingId, customerName, roomNumber, checkInDate, checkOutDate, calculateNights(), isActive ? "Yes" : "No");
    }
}