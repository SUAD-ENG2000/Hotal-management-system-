package service;

import models.Room;
import database.RoomDAO;
import exceptions.HotelManagementException;
import java.util.List;

public class RoomService {
    private RoomDAO roomDAO;
    
    public RoomService() {
        this.roomDAO = new RoomDAO();
    }
    
    public void addRoom(Room room) throws HotelManagementException {
        // Validate room data
        if (room.getRoomNumber() == null || room.getRoomNumber().trim().isEmpty()) {
            throw new HotelManagementException("Room number cannot be empty");
        }
        if (room.getRoomType() == null || room.getRoomType().trim().isEmpty()) {
            throw new HotelManagementException("Room type cannot be empty");
        }
        if (room.getPricePerNight() <= 0) {
            throw new HotelManagementException("Price must be greater than 0");
        }
        
        roomDAO.addRoom(room);
    }
    
    public void removeRoom(String roomNumber) throws HotelManagementException {
        roomDAO.removeRoom(roomNumber);
    }
    
    public List<Room> getAllRooms() throws HotelManagementException {
        return roomDAO.getAllRooms();
    }
    
    public Room getRoomByNumber(String roomNumber) throws HotelManagementException {
        return roomDAO.getRoomByNumber(roomNumber);
    }
    
    public Room findAvailableRoom(String roomType) throws HotelManagementException {
        return roomDAO.findAvailableRoom(roomType);
    }
    
    public List<Room> getAvailableRooms() throws HotelManagementException {
        return roomDAO.getAvailableRooms();
    }
    
    public List<Room> getRoomsByType(String roomType) throws HotelManagementException {
        return roomDAO.getRoomsByType(roomType);
    }
    
    public void updateRoomAvailability(String roomNumber, boolean available) throws HotelManagementException {
        roomDAO.updateRoomAvailability(roomNumber, available);
    }
    
    public void updateRoomPrice(String roomNumber, double newPrice) throws HotelManagementException {
        if (newPrice <= 0) {
            throw new HotelManagementException("Price must be greater than 0");
        }
        roomDAO.updateRoomPrice(roomNumber, newPrice);
    }
    
    public int getTotalRooms() throws HotelManagementException {
        return roomDAO.getRoomCount();
    }
    
    public int getAvailableRoomsCount() throws HotelManagementException {
        return roomDAO.getAvailableRoomCount();
    }
    
    public int getOccupiedRoomsCount() throws HotelManagementException {
        return roomDAO.getRoomCount() - roomDAO.getAvailableRoomCount();
    }
    
    public double getAverageRoomPrice() throws HotelManagementException {
        List<Room> rooms = roomDAO.getAllRooms();
        if (rooms.isEmpty()) {
            return 0.0;
        }
        
        double total = 0;
        for (Room room : rooms) {
            total += room.getPricePerNight();
        }
        return total / rooms.size();
    }
    
    public String getRoomStatistics() throws HotelManagementException {
        List<Room> rooms = roomDAO.getAllRooms();
        int singleCount = 0, doubleCount = 0, suiteCount = 0, deluxeCount = 0;
        double singleRevenue = 0, doubleRevenue = 0, suiteRevenue = 0, deluxeRevenue = 0;
        
        for (Room room : rooms) {
            switch (room.getRoomType()) {
                case "Single":
                    singleCount++;
                    singleRevenue += room.getPricePerNight() * 30; // Monthly estimate
                    break;
                case "Double":
                    doubleCount++;
                    doubleRevenue += room.getPricePerNight() * 30;
                    break;
                case "Suite":
                    suiteCount++;
                    suiteRevenue += room.getPricePerNight() * 30;
                    break;
                case "Deluxe":
                    deluxeCount++;
                    deluxeRevenue += room.getPricePerNight() * 30;
                    break;
            }
        }
        
        StringBuilder stats = new StringBuilder();
        stats.append("ROOM STATISTICS\n");
        stats.append("Total Rooms: ").append(rooms.size()).append("\n");
        stats.append("Single Rooms: ").append(singleCount).append(" ($").append(String.format("%.2f", singleRevenue)).append(" monthly)\n");
        stats.append("Double Rooms: ").append(doubleCount).append(" ($").append(String.format("%.2f", doubleRevenue)).append(" monthly)\n");
        stats.append("Suite Rooms: ").append(suiteCount).append(" ($").append(String.format("%.2f", suiteRevenue)).append(" monthly)\n");
        stats.append("Deluxe Rooms: ").append(deluxeCount).append(" ($").append(String.format("%.2f", deluxeRevenue)).append(" monthly)\n");
        
        return stats.toString();
    }
}