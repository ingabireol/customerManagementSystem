package dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import model.User;
import util.SecurityUtil;
import util.LogUtil;

/**
 * Data Access Object for User operations.
 */
public class UserDao {
    private String db_url = "jdbc:postgresql://localhost:5432/business_db";
    private String db_username = "postgres";
    private String db_passwd = "078868";
    
    /**
     * Creates a new user in the database
     * 
     * @param user The user to create
     * @return true if the user was created successfully
     */
    public boolean createUser(User user) {
        try {
            // Create connection
            Connection con = DriverManager.getConnection(db_url, db_username, db_passwd);
            
            // Hash the password
            String salt = SecurityUtil.generateSaltString();
            String hashedPassword = SecurityUtil.hashPasswordString(user.getPassword(), salt);
            
            // Prepare statement
            String sql = "INSERT INTO users (username, password, salt, full_name, email, role, active, created_at) " +
                         "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
            PreparedStatement pst = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            
            pst.setString(1, user.getUsername());
            pst.setString(2, hashedPassword);
            pst.setString(3, salt);
            pst.setString(4, user.getFullName());
            pst.setString(5, user.getEmail());
            pst.setString(6, user.getRole());
            pst.setBoolean(7, user.isActive());
            pst.setTimestamp(8, Timestamp.valueOf(LocalDateTime.now()));
            
            // Execute statement
            int rowsAffected = pst.executeUpdate();
            
            // Get generated ID
            if (rowsAffected > 0) {
                ResultSet rs = pst.getGeneratedKeys();
                if (rs.next()) {
                    user.setId(rs.getInt(1));
                }
                rs.close();
                con.close();
                return true;
            }
            
            con.close();
            return false;
            
        } catch (Exception ex) {
            LogUtil.error("Failed to create user: " + ex.getMessage(), ex);
            return false;
        }
    }
    
    /**
     * Updates an existing user in the database
     * 
     * @param user The user to update
     * @return true if the user was updated successfully
     */
    public boolean updateUser(User user) {
        try {
            // Create connection
            Connection con = DriverManager.getConnection(db_url, db_username, db_passwd);
            
            // Prepare statement
            String sql = "UPDATE users SET full_name = ?, email = ?, role = ?, active = ? WHERE id = ?";
            PreparedStatement pst = con.prepareStatement(sql);
            
            pst.setString(1, user.getFullName());
            pst.setString(2, user.getEmail());
            pst.setString(3, user.getRole());
            pst.setBoolean(4, user.isActive());
            pst.setInt(5, user.getId());
            
            // Execute statement
            int rowsAffected = pst.executeUpdate();
            
            con.close();
            return rowsAffected > 0;
            
        } catch (Exception ex) {
            LogUtil.error("Failed to update user: " + ex.getMessage(), ex);
            return false;
        }
    }
    
    /**
     * Updates a user's password
     * 
     * @param userId The ID of the user
     * @param newPassword The new password (plaintext)
     * @return true if the password was updated successfully
     */
    public boolean updatePassword(int userId, String newPassword) {
        try {
            // Create connection
            Connection con = DriverManager.getConnection(db_url, db_username, db_passwd);
            
            // Generate new salt and hash
            String salt = SecurityUtil.generateSaltString();
            String hashedPassword = SecurityUtil.hashPasswordString(newPassword, salt);
            
            // Prepare statement
            String sql = "UPDATE users SET password = ?, salt = ? WHERE id = ?";
            PreparedStatement pst = con.prepareStatement(sql);
            
            pst.setString(1, hashedPassword);
            pst.setString(2, salt);
            pst.setInt(3, userId);
            
            // Execute statement
            int rowsAffected = pst.executeUpdate();
            
            con.close();
            return rowsAffected > 0;
            
        } catch (Exception ex) {
            LogUtil.error("Failed to update password: " + ex.getMessage(), ex);
            return false;
        }
    }
    
    /**
     * Updates a user's last login time
     * 
     * @param userId The ID of the user
     * @return true if the last login time was updated successfully
     */
    public boolean updateLastLogin(int userId) {
        try {
            // Create connection
            Connection con = DriverManager.getConnection(db_url, db_username, db_passwd);
            
            // Prepare statement
            String sql = "UPDATE users SET last_login = ? WHERE id = ?";
            PreparedStatement pst = con.prepareStatement(sql);
            
            pst.setTimestamp(1, Timestamp.valueOf(LocalDateTime.now()));
            pst.setInt(2, userId);
            
            // Execute statement
            int rowsAffected = pst.executeUpdate();
            
            con.close();
            return rowsAffected > 0;
            
        } catch (Exception ex) {
            LogUtil.error("Failed to update last login: " + ex.getMessage(), ex);
            return false;
        }
    }
    
    /**
     * Finds a user by ID
     * 
     * @param id The user ID to search for
     * @return The user if found, null otherwise
     */
    public User findUserById(int id) {
        try {
            Connection con = DriverManager.getConnection(db_url, db_username, db_passwd);
            String sql = "SELECT * FROM users WHERE id = ?";
            PreparedStatement pst = con.prepareStatement(sql);
            pst.setInt(1, id);
            
            ResultSet rs = pst.executeQuery();
            User user = null;
            
            if (rs.next()) {
                user = mapResultSetToUser(rs);
            }
            
            rs.close();
            con.close();
            return user;
            
        } catch (Exception ex) {
            LogUtil.error("Failed to find user by ID: " + ex.getMessage(), ex);
            return null;
        }
    }
    
    /**
     * Finds a user by username
     * 
     * @param username The username to search for
     * @return The user if found, null otherwise
     */
    public User findUserByUsername(String username) {
        try {
            Connection con = DriverManager.getConnection(db_url, db_username, db_passwd);
            String sql = "SELECT * FROM users WHERE username = ?";
            PreparedStatement pst = con.prepareStatement(sql);
            pst.setString(1, username);
            
            ResultSet rs = pst.executeQuery();
            User user = null;
            
            if (rs.next()) {
                user = mapResultSetToUser(rs);
            }
            
            rs.close();
            con.close();
            return user;
            
        } catch (Exception ex) {
            LogUtil.error("Failed to find user by username: " + ex.getMessage(), ex);
            return null;
        }
    }
    
    /**
     * Finds a user by email
     * 
     * @param email The email to search for
     * @return The user if found, null otherwise
     */
    public User findUserByEmail(String email) {
        try {
            Connection con = DriverManager.getConnection(db_url, db_username, db_passwd);
            String sql = "SELECT * FROM users WHERE email = ?";
            PreparedStatement pst = con.prepareStatement(sql);
            pst.setString(1, email);
            
            ResultSet rs = pst.executeQuery();
            User user = null;
            
            if (rs.next()) {
                user = mapResultSetToUser(rs);
            }
            
            rs.close();
            con.close();
            return user;
            
        } catch (Exception ex) {
            LogUtil.error("Failed to find user by email: " + ex.getMessage(), ex);
            return null;
        }
    }
    
    /**
     * Gets all users
     * 
     * @return List of all users
     */
    public List<User> findAllUsers() {
        try {
            Connection con = DriverManager.getConnection(db_url, db_username, db_passwd);
            String sql = "SELECT * FROM users ORDER BY username";
            PreparedStatement pst = con.prepareStatement(sql);
            
            ResultSet rs = pst.executeQuery();
            List<User> userList = new ArrayList<>();
            
            while (rs.next()) {
                User user = mapResultSetToUser(rs);
                userList.add(user);
            }
            
            rs.close();
            con.close();
            return userList;
            
        } catch (Exception ex) {
            LogUtil.error("Failed to find all users: " + ex.getMessage(), ex);
            return Collections.emptyList();
        }
    }
    
    /**
     * Finds users by role
     * 
     * @param role The role to search for
     * @return List of matching users
     */
    public List<User> findUsersByRole(String role) {
        try {
            Connection con = DriverManager.getConnection(db_url, db_username, db_passwd);
            String sql = "SELECT * FROM users WHERE role = ? ORDER BY username";
            PreparedStatement pst = con.prepareStatement(sql);
            pst.setString(1, role);
            
            ResultSet rs = pst.executeQuery();
            List<User> userList = new ArrayList<>();
            
            while (rs.next()) {
                User user = mapResultSetToUser(rs);
                userList.add(user);
            }
            
            rs.close();
            con.close();
            return userList;
            
        } catch (Exception ex) {
            LogUtil.error("Failed to find users by role: " + ex.getMessage(), ex);
            return Collections.emptyList();
        }
    }
    
    /**
     * Authenticates a user with username and password
     * 
     * @param username The username
     * @param password The password (plaintext)
     * @return The authenticated user if successful, null otherwise
     */
    public User authenticateUser(String username, String password) {
        try {
            // Find the user by username
            User user = findUserByUsername(username);
            if (user == null) {
                return null;
            }
            
            // Check if user is active
            if (!user.isActive()) {
                return null;
            }
            
            // Verify the password
            if (SecurityUtil.verifyPasswordString(password, user.getPassword(), user.getSalt())) {
                // Update last login time
                updateLastLogin(user.getId());
                user.updateLastLogin();
                return user;
            }
            
            return null;
            
        } catch (Exception ex) {
            LogUtil.error("Authentication failed: " + ex.getMessage(), ex);
            return null;
        }
    }
    
    /**
     * Deletes a user from the database
     * 
     * @param userId The ID of the user to delete
     * @return true if the user was deleted successfully
     */
    public boolean deleteUser(int userId) {
        try {
            Connection con = DriverManager.getConnection(db_url, db_username, db_passwd);
            String sql = "DELETE FROM users WHERE id = ?";
            PreparedStatement pst = con.prepareStatement(sql);
            pst.setInt(1, userId);
            
            int rowsAffected = pst.executeUpdate();
            
            con.close();
            return rowsAffected > 0;
            
        } catch (Exception ex) {
            LogUtil.error("Failed to delete user: " + ex.getMessage(), ex);
            return false;
        }
    }
    
    /**
     * Helper method to map a ResultSet to a User object
     * 
     * @param rs The ResultSet
     * @return The User object
     * @throws Exception If mapping fails
     */
    private User mapResultSetToUser(ResultSet rs) throws Exception {
        User user = new User();
        user.setId(rs.getInt("id"));
        user.setUsername(rs.getString("username"));
        user.setPassword(rs.getString("password"));
        user.setSalt(rs.getString("salt"));
        user.setFullName(rs.getString("full_name"));
        user.setEmail(rs.getString("email"));
        user.setRole(rs.getString("role"));
        user.setActive(rs.getBoolean("active"));
        
        Timestamp lastLogin = rs.getTimestamp("last_login");
        if (lastLogin != null) {
            user.setLastLogin(lastLogin.toLocalDateTime());
        }
        
        Timestamp createdAt = rs.getTimestamp("created_at");
        if (createdAt != null) {
            user.setCreatedAt(createdAt.toLocalDateTime());
        }
        
        return user;
    }
    
    /**
     * Creates the users table if it doesn't exist
     * 
     * @return true if the table was created or already exists
     */
    public boolean createUsersTable() {
        try {
            Connection con = DriverManager.getConnection(db_url, db_username, db_passwd);
            
            // Check if table exists
            boolean tableExists = false;
            ResultSet rs = con.getMetaData().getTables(null, null, "users", null);
            if (rs.next()) {
                tableExists = true;
            }
            rs.close();
            
            // Create table if it doesn't exist
            if (!tableExists) {
                String sql = "CREATE TABLE users (" +
                             "id SERIAL PRIMARY KEY, " +
                             "username VARCHAR(50) UNIQUE NOT NULL, " +
                             "password VARCHAR(255) NOT NULL, " +
                             "salt VARCHAR(255) NOT NULL, " +
                             "full_name VARCHAR(100) NOT NULL, " +
                             "email VARCHAR(100) UNIQUE NOT NULL, " +
                             "role VARCHAR(20) NOT NULL, " +
                             "active BOOLEAN NOT NULL DEFAULT TRUE, " +
                             "last_login TIMESTAMP, " +
                             "created_at TIMESTAMP NOT NULL" +
                             ")";
                
                Statement stmt = con.createStatement();
                stmt.execute(sql);
                stmt.close();
                
                LogUtil.info("Users table created successfully");
            }
            
            con.close();
            return true;
            
        } catch (Exception ex) {
            LogUtil.error("Failed to create users table: " + ex.getMessage(), ex);
            return false;
        }
    }
    
    /**
     * Creates a default admin user if no users exist
     * 
     * @return true if the default user was created or already exists
     */
    public boolean createDefaultAdmin() {
        try {
            // Check if there are any users
            List<User> users = findAllUsers();
            if (!users.isEmpty()) {
                // Users already exist
                return true;
            }
            
            // Create default admin user
            User admin = new User();
            admin.setUsername("admin");
            admin.setPassword("admin123"); // This will be hashed
            admin.setFullName("System Administrator");
            admin.setEmail("admin@example.com");
            admin.setRole(User.ROLE_ADMIN);
            admin.setActive(true);
            
            boolean result = createUser(admin);
            if (result) {
                LogUtil.info("Default admin user created successfully");
            }
            
            return result;
            
        } catch (Exception ex) {
            LogUtil.error("Failed to create default admin: " + ex.getMessage(), ex);
            return false;
        }
    }
}