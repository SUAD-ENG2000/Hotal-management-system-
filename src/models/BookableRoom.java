package models;

/**
 * A room that can be booked, implementing the IBookable interface.
 */
public class BookableRoom extends Room implements IBookable {
    public BookableRoom(String roomNumber, String roomType, double pricePerNight, boolean isAvailable) {
        super(roomNumber, roomType, pricePerNight, isAvailable);
    }

    @Override
    public boolean checkAvailability() {
        return isAvailable();
    }

    @Override
    public void markAsBooked() {
        setAvailable(false);
    }

    @Override
    public void markAsAvailable() {
        setAvailable(true);
    }
    
    @Override
    public String toString() {
        return "Bookable" + super.toString();
    }
}