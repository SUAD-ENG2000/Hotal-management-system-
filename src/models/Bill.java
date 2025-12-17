package models;

import java.time.LocalDateTime;

/**
 * Represents a bill for a hotel booking.
 * Contains information about the booking and payment status.
 */
public class Bill {
    private String billId;
    private String bookingId;
    private double totalAmount;
    private LocalDateTime generatedDate;
    private boolean isPaid;

    /**
     * Constructor to create a new bill.
     * @param billId Unique identifier for the bill
     * @param bookingId ID of the associated booking
     * @param totalAmount Total amount to be paid
     */
    public Bill(String billId, String bookingId, double totalAmount) {
        this.billId = billId;
        this.bookingId = bookingId;
        this.totalAmount = totalAmount;
        this.generatedDate = LocalDateTime.now();
        this.isPaid = false; // New bills are unpaid by default
    }

    // Getters and Setters
    public String getBillId() { return billId; }
    public void setBillId(String billId) { this.billId = billId; }
    
    public String getBookingId() { return bookingId; }
    public void setBookingId(String bookingId) { this.bookingId = bookingId; }
    
    public double getTotalAmount() { return totalAmount; }
    public void setTotalAmount(double totalAmount) { this.totalAmount = totalAmount; }
    
    public LocalDateTime getGeneratedDate() { return generatedDate; }
    public void setGeneratedDate(LocalDateTime generatedDate) { this.generatedDate = generatedDate; }
    
    public boolean isPaid() { return isPaid; }
    public void setPaid(boolean paid) { isPaid = paid; }
    
    /**
     * Returns a string representation of the bill.
     * @return Formatted string with bill details
     */
    @Override
    public String toString() {
        return String.format("Bill[ID: %s, Booking: %s, Amount: $%.2f, Generated: %s, Paid: %s]",
            billId, bookingId, totalAmount, generatedDate, isPaid ? "Yes" : "No");
    }
}