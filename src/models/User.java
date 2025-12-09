// User.java (abstract base class)
package models;

/**
 * Abstract base class for all users in the system.
 */
public abstract class User {
    private String userId;
    private String password;
    private String role;

    public User(String userId, String role) {
        this.userId = userId;
        this.role = role;
    }

    // Getters
    public String getUserId() { return userId; }
    public String getPassword() { return password; }
    public String getRole() { return role; }

    // Abstract method to be implemented by subclasses
    public abstract void showMenu();
}