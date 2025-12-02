package models;

public class Receptionist extends User {
    public Receptionist(String userId, String password) {
        super(userId, password, "Receptionist");
    }

    @Override
    public void showMenu() {
        System.out.println("Receptionist Menu: View Rooms, Find Available Room, Create Booking, Generate Bill");
    }

    
}