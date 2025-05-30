package model;

import java.time.LocalDateTime;

/**
 * Represents a user in the business management system.
 * Contains user authentication and permission details.
 */
public class User {
    private int id;
    private String username;
    private String password; // Stored as hash
    private String salt; // For password hashing
    private String fullName;
    private String email;
    private String role; // ADMIN, MANAGER, STAFF, etc.
    private boolean active;
    private LocalDateTime lastLogin;
    private LocalDateTime createdAt;
    
    // Role constants
    public static final String ROLE_ADMIN = "ADMIN";
    public static final String ROLE_MANAGER = "MANAGER";
    public static final String ROLE_STAFF = "STAFF";
    
    /**
     * Default constructor
     */
    public User() {
        this.active = true;
        this.createdAt = LocalDateTime.now();
    }
    
    /**
     * Constructor with essential fields
     * 
     * @param username Username
     * @param password Password (will be hashed)
     * @param fullName User's full name
     * @param email User's email
     * @param role User's role
     */
    public User(String username, String password, String fullName, String email, String role) {
        this();
        this.username = username;
        this.password = password; // Note: This should be hashed before setting
        this.fullName = fullName;
        this.email = email;
        this.role = role;
    }

    /**
     * Full constructor 
     * 
     * @param id Database ID
     * @param username Username
     * @param password Hashed password
     * @param salt Password salt
     * @param fullName User's full name
     * @param email User's email
     * @param role User's role
     * @param active Whether the account is active
     * @param lastLogin Last login timestamp
     * @param createdAt Creation timestamp
     */
    public User(int id, String username, String password, String salt, 
                String fullName, String email, String role, boolean active,
                LocalDateTime lastLogin, LocalDateTime createdAt) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.salt = salt;
        this.fullName = fullName;
        this.email = email;
        this.role = role;
        this.active = active;
        this.lastLogin = lastLogin;
        this.createdAt = createdAt;
    }

    // Getters and Setters
    
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getSalt() {
        return salt;
    }

    public void setSalt(String salt) {
        this.salt = salt;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public LocalDateTime getLastLogin() {
        return lastLogin;
    }

    public void setLastLogin(LocalDateTime lastLogin) {
        this.lastLogin = lastLogin;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    /**
     * Checks if the user has admin role
     * 
     * @return true if the user is an admin
     */
    public boolean isAdmin() {
        return ROLE_ADMIN.equals(role);
    }
    
    /**
     * Checks if the user has manager role
     * 
     * @return true if the user is a manager
     */
    public boolean isManager() {
        return ROLE_MANAGER.equals(role);
    }
    
    /**
     * Updates the last login time to the current time
     */
    public void updateLastLogin() {
        this.lastLogin = LocalDateTime.now();
    }
    
    @Override
    public String toString() {
        return "User [id=" + id + ", username=" + username + ", fullName=" + fullName + 
               ", email=" + email + ", role=" + role + "]";
    }
}