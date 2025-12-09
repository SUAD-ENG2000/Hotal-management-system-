// IBookable.java (Interface)
package models;

/**
 * Interface for bookable items (like rooms).
 */
public interface IBookable {
    boolean checkAvailability();
    void markAsBooked();
    void markAsAvailable();
}