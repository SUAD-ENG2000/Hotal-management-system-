package models;

public interface IBookable {
    boolean checkAvailability();
    void markAsBooked();
    void markAsAvailable();
}