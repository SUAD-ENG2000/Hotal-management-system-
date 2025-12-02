package models;

public class Manager extends User {
    public Manager(String userId, String password) {
        super(userId, password, "Manager");
    }

    @Override
    public void showMenu() {
        System.out.println("Manager Menu: Add Room, Remove Room, View All Bookings, View All Bills");
    }

    
}