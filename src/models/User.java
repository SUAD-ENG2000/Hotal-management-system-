package models;

public abstract class User {
    private String userId;
    private String password;
    private String role;

    public User(String userId, String password, String role) {
        this.userId = userId;
        this.password = password;
        this.role = role;
    }

    // Getters and Setters
    public String getUserId() { return userId; }
    public String getPassword() { return password; }
    public String getRole() { return role; }

    public abstract void showMenu();
}