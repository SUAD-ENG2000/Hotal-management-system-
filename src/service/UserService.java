package service;

import models.User;
import database.UserDAO;
import exceptions.HotelManagementException;

public class UserService {
    private UserDAO userDAO;
    
    public UserService() {
        this.userDAO = new UserDAO();
    }
    
    public User authenticateUser(String userId, String password) throws HotelManagementException {
        return userDAO.authenticateUser(userId, password);
    }
    
    public User getUserById(String userId) throws HotelManagementException {
        return userDAO.getUserById(userId);
    }
    
    public void addUser(String userId, String password, String role) throws HotelManagementException {
        // Validate role
        if (!"Manager".equals(role) && !"Receptionist".equals(role)) {
            throw new HotelManagementException("Invalid role. Must be 'Manager' or 'Receptionist'");
        }
        
        // Check if user already exists
        if (userDAO.userExists(userId)) {
            throw new HotelManagementException("User already exists");
        }
        
        // Add user
        userDAO.addUser(userId, password, role);
    }
    
    public void updateUserPassword(String userId, String newPassword) throws HotelManagementException {
        userDAO.updateUserPassword(userId, newPassword);
    }
    
    public void deleteUser(String userId) throws HotelManagementException {
        userDAO.deleteUser(userId);
    }
    
    public boolean userExists(String userId) throws HotelManagementException {
        return userDAO.userExists(userId);
    }
    
    public int getUserCount() throws HotelManagementException {
        return userDAO.getUserCount();
    }
    
    public String getUserRole(String userId) throws HotelManagementException {
        User user = userDAO.getUserById(userId);
        return user.getRole();
    }
    
    public void changeUserRole(String userId, String newRole) throws HotelManagementException {
        // Note: This requires additional database structure or update
        throw new HotelManagementException("Changing user role not implemented yet");
    }
}