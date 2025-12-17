package models;

import service.RoomService;
import service.BookingService;
import service.BillService;
import exceptions.HotelManagementException;
import java.time.LocalDate;
import java.util.List;
import java.util.Scanner;

/**
 * Represents a Receptionist user with front-desk operations.
 * Handles guest bookings, room assignments, and billing.
 */
public class Receptionist extends User {
    // Scanner for reading user input specific to Receptionist operations
    private Scanner scanner;
    private RoomService roomService;
    private BookingService bookingService;
    private BillService billService;

    /**
     * Creates a new Receptionist user.
     * @param userId Unique username for login
     * @param password Password for authentication
     */
    public Receptionist(String userId, String password) {
        super(userId, "Receptionist");
        this.scanner = new Scanner(System.in);
        this.roomService = new RoomService();
        this.bookingService = new BookingService();
        this.billService = new BillService();
    }

    /**
     * Displays brief description of Receptionist capabilities.
     */
    @Override
    public void showMenu() {
        System.out.println("Receptionist Menu: View Rooms, Find Available Room, Create Booking, Generate Bill");
    }

    /**
     * Displays all rooms with current status.
     * Similar to Manager's view but with receptionist context.
     * @throws HotelManagementException if rooms cannot be retrieved
     */
    public void viewAllRooms() throws HotelManagementException {
        // Fetch all rooms from service
        List<Room> rooms = roomService.getAllRooms();
        
        // Display formatted table
        System.out.println("\n=== All Rooms (" + rooms.size() + " rooms) ===");
        System.out.println("No. | Room | Type | Price/Night | Available");
        System.out.println("----|------|------|-------------|----------");
        
        for (int i = 0; i < rooms.size(); i++) {
            Room room = rooms.get(i);
            System.out.printf("%-3d | %-4s | %-6s | $%-10.2f | %s%n",
                i + 1,
                room.getRoomNumber(),
                room.getRoomType(),
                room.getPricePerNight(),
                room.isAvailable() ? "✅ Yes" : "❌ No"
            );
        }
    }

    /**
     * Searches for an available room of specific type.
     * @throws HotelManagementException if search fails
     */
    public void findAvailableRoom() throws HotelManagementException {
        System.out.println("\n--- Find Available Room ---");
        System.out.print("Enter room type (Single/Double/Suite/Deluxe): ");
        String roomType = scanner.nextLine();
        
        // Query service for first available room of specified type
        Room room = roomService.findAvailableRoom(roomType);
        
        if (room != null) {
            System.out.println("\n✅ Available room found!");
            System.out.println("Room Number: " + room.getRoomNumber());
            System.out.println("Room Type: " + room.getRoomType());
            System.out.println("Price per Night: $" + room.getPricePerNight());
        } else {
            System.out.println("❌ No available rooms of type: " + roomType);
        }
    }

    /**
     * Creates a new booking for a guest.
     * Includes validation and room availability checks.
     * @throws HotelManagementException if booking creation fails
     */
    public void createBooking() throws HotelManagementException {
        System.out.println("\n--- Create New Booking ---");
        
        // Display available rooms to assist user
        System.out.println("\nAvailable rooms:");
        List<Room> rooms = roomService.getAvailableRooms();
        for (Room room : rooms) {
            System.out.println("- " + room.getRoomNumber() + " (" + room.getRoomType() + 
                             ") - $" + room.getPricePerNight() + "/night");
        }
        
        // Collect guest information
        System.out.print("\nCustomer Name: ");
        String customerName = scanner.nextLine();
        
        System.out.print("Room Number: ");
        String roomNumber = scanner.nextLine();
        
        // Collect and validate dates
        System.out.print("Check-in Date (YYYY-MM-DD): ");
        LocalDate checkIn = LocalDate.parse(scanner.nextLine());
        
        System.out.print("Check-out Date (YYYY-MM-DD): ");
        LocalDate checkOut = LocalDate.parse(scanner.nextLine());
        
        // Business rule: check-out must be after check-in
        if (checkOut.isBefore(checkIn) || checkOut.isEqual(checkIn)) {
            System.out.println("❌ Error: Check-out date must be after check-in date!");
            return;
        }
        
        // Business rule: cannot book in the past
        if (checkIn.isBefore(LocalDate.now())) {
            System.out.println("❌ Error: Check-in date cannot be in the past!");
            return;
        }

        // Generate unique booking ID using timestamp
        String bookingId = "BOOK_" + System.currentTimeMillis();
        Booking booking = new Booking(bookingId, customerName, roomNumber, checkIn, checkOut);
        
        // Persist booking using service
        bookingService.createBooking(booking);
        
        // Display confirmation
        System.out.println("\n✅ Booking created successfully!");
        System.out.println("Booking ID: " + bookingId);
        System.out.println("Customer: " + customerName);
        System.out.println("Room: " + roomNumber);
        System.out.println("Check-in: " + checkIn);
        System.out.println("Check-out: " + checkOut);
        System.out.println("Total Nights: " + booking.calculateNights());
    }

    /**
     * Generates a bill for a completed booking.
     * Validates that booking exists and is active.
     * @throws HotelManagementException if bill generation fails
     */
    public void generateBill() throws HotelManagementException {
        System.out.println("\n--- Generate Bill ---");
        
        // List active bookings to help user
        System.out.println("\nActive bookings:");
        List<Booking> bookings = bookingService.getActiveBookings();
        boolean hasActiveBookings = false;
        
        for (Booking booking : bookings) {
            hasActiveBookings = true;
            System.out.println("- Booking ID: " + booking.getBookingId());
            System.out.println("  Customer: " + booking.getCustomerName());
            System.out.println("  Room: " + booking.getRoomNumber());
            System.out.println("  Check-in: " + booking.getCheckInDate());
            System.out.println("  Check-out: " + booking.getCheckOutDate());
            System.out.println("  Nights: " + booking.calculateNights());
        }
        
        if (!hasActiveBookings) {
            System.out.println("❌ No active bookings found!");
            return;
        }
        
        // Get booking ID from user
        System.out.print("\nEnter Booking ID: ");
        String bookingId = scanner.nextLine();
        
        // Find the specific booking
        Booking bookingToBill = null;
        for (Booking booking : bookings) {
            if (booking.getBookingId().equals(bookingId)) {
                bookingToBill = booking;
                break;
            }
        }
        
        if (bookingToBill == null) {
            System.out.println("❌ Booking not found or not active!");
            return;
        }
        
        // Generate and save the bill using service
        Bill bill = billService.generateBill(bookingToBill);
        
        // Display bill details
        System.out.println("\n✅ Bill generated successfully!");
        System.out.println("--- Bill Details ---");
        System.out.println("Bill ID: " + bill.getBillId());
        System.out.println("Booking ID: " + bill.getBookingId());
        System.out.println("Total Amount: $" + bill.getTotalAmount());
        System.out.println("Generated Date: " + bill.getGeneratedDate());
        System.out.println("Status: " + (bill.isPaid() ? "Paid" : "Unpaid"));
    }

    /**
     * Updates a bill's status to paid.
     * Validates that bill exists and is currently unpaid.
     * @throws HotelManagementException if status update fails
     */
    public void markBillAsPaid() throws HotelManagementException {
        System.out.println("\n--- Mark Bill as Paid ---");
        
        // List unpaid bills
        System.out.println("\nUnpaid bills:");
        List<Bill> bills = billService.getUnpaidBills();
        boolean hasUnpaidBills = false;
        
        for (Bill bill : bills) {
            hasUnpaidBills = true;
            System.out.println("- Bill ID: " + bill.getBillId());
            System.out.println("  Booking ID: " + bill.getBookingId());
            System.out.println("  Amount: $" + bill.getTotalAmount());
            System.out.println("  Generated: " + bill.getGeneratedDate());
        }
        
        if (!hasUnpaidBills) {
            System.out.println("✅ All bills are already paid!");
            return;
        }
        
        // Get bill ID from user
        System.out.print("\nEnter Bill ID to mark as paid: ");
        String billId = scanner.nextLine();
        
        // Find and update the bill
        boolean found = false;
        for (Bill bill : bills) {
            if (bill.getBillId().equals(billId)) {
                // Update bill status using service
                billService.markBillAsPaid(billId);
                System.out.println("✅ Bill marked as paid successfully!");
                found = true;
                break;
            }
        }
        
        if (!found) {
            System.out.println("❌ Bill not found!");
        }
    }

    /**
     * Displays all unpaid bills with summary information.
     * Shows total outstanding amount and individual bill details.
     * @throws HotelManagementException if bills cannot be retrieved
     */
    public void viewUnpaidBills() throws HotelManagementException {
        System.out.println("\n=== Unpaid Bills ===");
        
        List<Bill> bills = billService.getUnpaidBills();
        int unpaidCount = 0;
        double totalUnpaid = 0;
        
        // Process each bill
        for (Bill bill : bills) {
            unpaidCount++;
            totalUnpaid += bill.getTotalAmount();
            
            // Display bill details
            System.out.println("\n--- Unpaid Bill ---");
            System.out.println("Bill ID: " + bill.getBillId());
            System.out.println("Booking ID: " + bill.getBookingId());
            System.out.println("Amount: $" + bill.getTotalAmount());
            System.out.println("Generated: " + bill.getGeneratedDate());
            
            // Calculate how old the bill is
            System.out.println("Days since generated: " + 
                java.time.temporal.ChronoUnit.DAYS.between(
                    bill.getGeneratedDate().toLocalDate(), 
                    java.time.LocalDate.now()
                ));
        }
        
        // Display summary
        if (unpaidCount == 0) {
            System.out.println("✅ All bills are paid!");
        } else {
            System.out.println("\n📊 Summary:");
            System.out.println("Total unpaid bills: " + unpaidCount);
            System.out.println("Total amount due: $" + totalUnpaid);
        }
    }
    
    /**
     * Gets the room service for external access
     */
    public RoomService getRoomService() {
        return roomService;
    }
    
    /**
     * Gets the booking service for external access
     */
    public BookingService getBookingService() {
        return bookingService;
    }
    
    /**
     * Gets the bill service for external access
     */
    public BillService getBillService() {
        return billService;
    }
}