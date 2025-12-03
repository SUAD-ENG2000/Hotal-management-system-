package models;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class Booking {
    private String bookingId;
    private String customerName;
    private String roomNumber;
    private LocalDate checkInDate;
    private LocalDate checkOutDate;
    private boolean isActive;

    public Booking(String bookingId, String customerName, String roomNumber, 
                   LocalDate checkInDate, LocalDate checkOutDate) {
        this.bookingId = bookingId;
        this.customerName = customerName;
        this.roomNumber = roomNumber;
        this.checkInDate = checkInDate;
        this.checkOutDate = checkOutDate;
        this.isActive = true;
    }

    public long calculateNights() {
        return ChronoUnit.DAYS.between(checkInDate, checkOutDate);
    }

    // Getters and Setters
    public String getBookingId() { return bookingId; }
    public String getCustomerName() { return customerName; }
    public String getRoomNumber() { return roomNumber; }
    public LocalDate getCheckInDate() { return checkInDate; }
    public LocalDate getCheckOutDate() { return checkOutDate; }
    public boolean isActive() { return isActive; }
    public void setActive(boolean active) { isActive = active; }
}