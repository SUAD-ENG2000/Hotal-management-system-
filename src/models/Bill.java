package models;

import java.time.LocalDateTime;

public class Bill {
    private String billId;
    private String bookingId;
    private double totalAmount;
    private LocalDateTime generatedDate;
    private boolean isPaid;

    public Bill(String billId, String bookingId, double totalAmount) {
        this.billId = billId;
        this.bookingId = bookingId;
        this.totalAmount = totalAmount;
        this.generatedDate = LocalDateTime.now();
        this.isPaid = false;
    }

    // Getters and Setters
    public String getBillId() { return billId; }
    public String getBookingId() { return bookingId; }
    public double getTotalAmount() { return totalAmount; }
    public LocalDateTime getGeneratedDate() { return generatedDate; }
    public boolean isPaid() { return isPaid; }
    public void setPaid(boolean paid) { isPaid = paid; }
}