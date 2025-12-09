package models;

import database.DatabaseManager;
import exceptions.HotelManagementException;
import java.util.List;
import java.util.Scanner;

/**
 * Represents a Manager user with administrative privileges.
 * Can manage room inventory and view all system data.
 */
public class Manager extends User {
    // Scanner for reading user input specific to Manager operations
    private Scanner scanner;

    /**
     * Creates a new Manager user.
     * @param userId Unique username for login
     * @param password Password for authentication
     */
    public Manager(String userId, String password) {
        super(userId, "Manager");
        this.scanner = new Scanner(System.in);
    }

    /**
     * Displays brief description of Manager capabilities.
     */
    @Override
    public void showMenu() {
        System.out.println("Manager Menu: Add Room, Remove Room, View All Bookings, View All Bills");
    }

    /**
     * Adds a new room to the hotel inventory.
     * Collects room details from user and persists to database.
     * @throws HotelManagementException if room cannot be added
     */
    public void addRoom() throws HotelManagementException {
        System.out.println("\n--- Add New Room ---");
        System.out.print("Room Number: ");
        String roomNumber = scanner.nextLine();
        
        System.out.print("Room Type (Single/Double/Suite/Deluxe): ");
        String roomType = scanner.nextLine();
        
        System.out.print("Price per Night: ");
        double price = scanner.nextDouble();
        scanner.nextLine(); // consume newline left by nextDouble()

        // Create room object with provided details
        Room room = new Room(roomNumber, roomType, price, true);
        
        // Persist to database
        DatabaseManager.addRoom(room);
        System.out.println("✅ Room added successfully!");
    }

    /**
     * Removes a room from the hotel inventory.
     * Requires confirmation before deletion.
     * @throws HotelManagementException if room cannot be found or removed
     */
    public void removeRoom() throws HotelManagementException {
        System.out.println("\n--- Remove Room ---");
        System.out.print("Enter room number to remove: ");
        String roomNumber = scanner.nextLine();
        
        // Safety check: confirm deletion
        System.out.print("Are you sure you want to delete room " + roomNumber + "? (yes/no): ");
        String confirm = scanner.nextLine();
        
        if (confirm.equalsIgnoreCase("yes")) {
            // Delete from database
            DatabaseManager.removeRoom(roomNumber);
            System.out.println("✅ Room removed successfully!");
        } else {
            System.out.println("Room deletion cancelled.");
        }
    }

    /**
     * Displays all rooms in the hotel with current status.
     * Shows formatted table with room details and availability.
     * @throws HotelManagementException if rooms cannot be retrieved
     */
    public void viewAllRooms() throws HotelManagementException {
        // Fetch all rooms from database
        List<Room> rooms = DatabaseManager.getAllRooms();
        
        // Display header
        System.out.println("\n=== All Rooms (" + rooms.size() + " rooms) ===");
        System.out.println("No. | Room | Type | Price/Night | Available");
        System.out.println("----|------|------|-------------|----------");
        
        // Display each room in formatted table
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
     * Displays all bookings in the system.
     * Shows detailed information for each booking including status.
     * @throws HotelManagementException if bookings cannot be retrieved
     */
    public void viewAllBookings() throws HotelManagementException {
        // Fetch all bookings from database
        List<Booking> bookings = DatabaseManager.getAllBookings();
        
        // Display header with count
        System.out.println("\n=== All Bookings (" + bookings.size() + " bookings) ===");
        
        // Display detailed info for each booking
        for (Booking booking : bookings) {
            System.out.println("\n--- Booking Details ---");
            System.out.println("Booking ID: " + booking.getBookingId());
            System.out.println("Customer: " + booking.getCustomerName());
            System.out.println("Room: " + booking.getRoomNumber());
            System.out.println("Check-in: " + booking.getCheckInDate());
            System.out.println("Check-out: " + booking.getCheckOutDate());
            System.out.println("Nights: " + booking.calculateNights());
            System.out.println("Status: " + (booking.isActive() ? "✅ Active" : "❌ Inactive"));
        }
    }

    /**
     * Displays all bills generated in the system.
     * Shows payment status and details for each invoice.
     * @throws HotelManagementException if bills cannot be retrieved
     */
    public void viewAllBills() throws HotelManagementException {
        // Fetch all bills from database
        List<Bill> bills = DatabaseManager.getAllBills();
        
        // Display header with count
        System.out.println("\n=== All Bills (" + bills.size() + " bills) ===");
        
        // Display detailed info for each bill
        for (Bill bill : bills) {
            System.out.println("\n--- Bill Details ---");
            System.out.println("Bill ID: " + bill.getBillId());
            System.out.println("Booking ID: " + bill.getBookingId());
            System.out.println("Amount: $" + bill.getTotalAmount());
            System.out.println("Generated: " + bill.getGeneratedDate());
            System.out.println("Status: " + (bill.isPaid() ? "✅ Paid" : "❌ Unpaid"));
        }
    }
}