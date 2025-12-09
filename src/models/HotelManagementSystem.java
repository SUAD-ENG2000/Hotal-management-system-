package models;

import database.DatabaseManager;
import exceptions.HotelManagementException;
import java.util.Scanner;

/**
 * Main entry point for the Hotel Management System application.
 * Handles user authentication and delegates to appropriate user menus.
 */
public class HotelManagementSystem {
    // Shared scanner for reading user input throughout the application
    private static Scanner scanner = new Scanner(System.in);
    
    // Currently logged-in user (Manager or Receptionist)
    private static User currentUser;

    /**
     * Application starting point.
     * @param args Command line arguments (not used)
     */
    public static void main(String[] args) {
        System.out.println("=== Hotel Management System ===");
        
        try {
            // Attempt user login
            boolean loggedIn = login();
            if (loggedIn) {
                showMainMenu();
            }
        } catch (HotelManagementException e) {
            // Handle any login or database errors
            System.out.println("Error: " + e.getMessage());
            System.out.println("Please check your database connection!");
        } finally {
            // Always close scanner to prevent resource leak
            scanner.close();
        }
    }

    /**
     * Authenticates user credentials and sets current user.
     * @return true if login successful, false otherwise
     * @throws HotelManagementException if authentication fails
     */
    private static boolean login() throws HotelManagementException {
        // Prompt for credentials
        System.out.print("User ID: ");
        String userId = scanner.nextLine();
        
        System.out.print("Password: ");
        String password = scanner.nextLine();

        // Validate against database
        currentUser = DatabaseManager.authenticateUser(userId, password);
        System.out.println("Login successful! Welcome " + currentUser.getRole());
        return true;
    }

    /**
     * Routes to appropriate menu based on user role.
     * @throws HotelManagementException if menu operation fails
     */
    private static void showMainMenu() throws HotelManagementException {
        // Dynamic dispatch based on user type
        if (currentUser instanceof Manager) {
            showManagerMenu();
        } else if (currentUser instanceof Receptionist) {
            showReceptionistMenu();
        }
    }

    /**
     * Displays and handles Manager-specific operations.
     * Delegates actual operations to Manager class methods.
     * @throws HotelManagementException if any database operation fails
     */
    private static void showManagerMenu() throws HotelManagementException {
        // Cast to Manager to access Manager-specific methods
        Manager manager = (Manager) currentUser;
        while (true) {
            System.out.println("\n=== Manager Menu ===");
            System.out.println("1. Add Room");
            System.out.println("2. Remove Room");
            System.out.println("3. View All Rooms");
            System.out.println("4. View All Bookings");
            System.out.println("5. View All Bills");
            System.out.println("6. Exit");
            System.out.print("Choose option: ");

            String input = scanner.nextLine();
            
            try {
                int choice = Integer.parseInt(input);
                
                switch (choice) {
                    case 1:
                        // Add a new room to inventory
                        manager.addRoom();
                        break;
                    case 2:
                        // Remove a room from inventory
                        manager.removeRoom();
                        break;
                    case 3:
                        // Display all rooms with status
                        manager.viewAllRooms();
                        break;
                    case 4:
                        // Show all current and past bookings
                        manager.viewAllBookings();
                        break;
                    case 5:
                        // Display all generated bills
                        manager.viewAllBills();
                        break;
                    case 6:
                        // Exit the application
                        System.out.println("Goodbye!");
                        return;
                    default:
                        System.out.println("Invalid option!");
                }
            } catch (NumberFormatException e) {
                // Handle non-numeric input
                System.out.println("Please enter a valid number!");
            }
        }
    }

    /**
     * Displays and handles Receptionist-specific operations.
     * Delegates actual operations to Receptionist class methods.
     * @throws HotelManagementException if any database operation fails
     */
    private static void showReceptionistMenu() throws HotelManagementException {
        // Cast to Receptionist to access Receptionist-specific methods
        Receptionist receptionist = (Receptionist) currentUser;
        while (true) {
            System.out.println("\n=== Receptionist Menu ===");
            System.out.println("1. View All Rooms");
            System.out.println("2. Find Available Room");
            System.out.println("3. Create Booking");
            System.out.println("4. Generate Bill");
            System.out.println("5. Mark Bill as Paid");
            System.out.println("6. View Unpaid Bills");
            System.out.println("7. Exit");
            System.out.print("Choose option: ");

            String input = scanner.nextLine();
            
            try {
                int choice = Integer.parseInt(input);
                
                switch (choice) {
                    case 1:
                        // View room inventory
                        receptionist.viewAllRooms();
                        break;
                    case 2:
                        // Search for available rooms by type
                        receptionist.findAvailableRoom();
                        break;
                    case 3:
                        // Create new guest booking
                        receptionist.createBooking();
                        break;
                    case 4:
                        // Generate invoice for completed stay
                        receptionist.generateBill();
                        break;
                    case 5:
                        // Update bill payment status
                        receptionist.markBillAsPaid();
                        break;
                    case 6:
                        // List all outstanding payments
                        receptionist.viewUnpaidBills();
                        break;
                    case 7:
                        // Exit the application
                        System.out.println("Goodbye!");
                        return;
                    default:
                        System.out.println("Invalid option!");
                }
            } catch (NumberFormatException e) {
                // Handle non-numeric input
                System.out.println("Please enter a valid number!");
            }
        }
    }
}