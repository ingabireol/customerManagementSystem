package controller;

import model.User;
import dao.UserDao;
import ui.MainView;
import ui.auth.LoginView;
import ui.auth.Session;
import ui.DialogFactory;

import javax.swing.*;
import java.awt.*;

/**
 * Controller for authentication operations.
 * Manages the login process and user session.
 */
public class AuthController {
    // Data access
    private UserDao userDao;
    
    // Views
    private LoginView loginView;
    private Component parentComponent;
    
    // Session
    private Session session;
    
    /**
     * Constructor
     * 
     * @param parentComponent Parent component for dialogs
     */
    public AuthController(Component parentComponent) {
        this.userDao = new UserDao();
        this.parentComponent = parentComponent;
        this.session = Session.getInstance();
        
        // Initialize database if needed
        initializeDatabase();
    }
    
    /**
     * Initializes the database tables and default data
     */
    private void initializeDatabase() {
        // Create users table if it doesn't exist
        userDao.createUsersTable();
        
        // Create default admin user if no users exist
        userDao.createDefaultAdmin();
    }
    
    /**
     * Shows the login view
     */
    public void showLoginView() {
        loginView = new LoginView(new LoginView.AuthenticationCallback() {
            @Override
            public void onLoginSuccess(User user) {
                // Set the current user in the session
                session.setCurrentUser(user);
                
                // Close the login view
                loginView.dispose();
                
                // Show the main view
                SwingUtilities.invokeLater(() -> {
                    MainView mainView = new MainView();
                    mainView.setVisible(true);
                });
            }
            
            @Override
            public void onLoginFailure(String reason) {
                // Already handled in LoginView
            }
            
            @Override
            public void onCancel() {
                // Exit the application
                System.exit(0);
            }
            
            @Override
            public void onForgotPassword() {
                showForgotPasswordDialog();
            }
        });
        
        // Show the login view
        loginView.setVisible(true);
    }
    
    /**
     * Shows the forgot password dialog
     */
    private void showForgotPasswordDialog() {
        JPanel panel = new JPanel(new BorderLayout(0, 10));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        JLabel messageLabel = new JLabel("Enter your username to reset your password:");
        messageLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        
        JTextField usernameField = new JTextField(20);
        
        JPanel fieldPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        fieldPanel.setOpaque(false);
        fieldPanel.add(new JLabel("Username:"));
        fieldPanel.add(usernameField);
        
        panel.add(messageLabel, BorderLayout.NORTH);
        panel.add(fieldPanel, BorderLayout.CENTER);
        
        // Show the dialog
        int result = JOptionPane.showConfirmDialog(
            loginView,
            panel,
            "Forgot Password",
            JOptionPane.OK_CANCEL_OPTION,
            JOptionPane.QUESTION_MESSAGE
        );
        
        if (result == JOptionPane.OK_OPTION) {
            String username = usernameField.getText().trim();
            if (!username.isEmpty()) {
                // In a real application, this would send a password reset email
                // For this demo, we'll just show a message
                JOptionPane.showMessageDialog(
                    loginView,
                    "A password reset link has been sent to the email associated with this account.",
                    "Password Reset",
                    JOptionPane.INFORMATION_MESSAGE
                );
            }
        }
    }
    
    /**
     * Logs out the current user
     */
    public void logout() {
        // Clear the session
        session.logout();
        
        // Show the login view
        showLoginView();
    }
    
    /**
     * Shows the change password dialog
     */
    public void showChangePasswordDialog() {
        if (!session.isLoggedIn()) {
            return;
        }
        
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        JLabel currentLabel = new JLabel("Current Password:");
        JPasswordField currentField = new JPasswordField(20);
        
        JLabel newLabel = new JLabel("New Password:");
        JPasswordField newField = new JPasswordField(20);
        
        JLabel confirmLabel = new JLabel("Confirm New Password:");
        JPasswordField confirmField = new JPasswordField(20);
        
        panel.add(currentLabel);
        panel.add(Box.createRigidArea(new Dimension(0, 5)));
        panel.add(currentField);
        panel.add(Box.createRigidArea(new Dimension(0, 15)));
        
        panel.add(newLabel);
        panel.add(Box.createRigidArea(new Dimension(0, 5)));
        panel.add(newField);
        panel.add(Box.createRigidArea(new Dimension(0, 15)));
        
        panel.add(confirmLabel);
        panel.add(Box.createRigidArea(new Dimension(0, 5)));
        panel.add(confirmField);
        
        // Show the dialog
        int result = JOptionPane.showConfirmDialog(
            parentComponent,
            panel,
            "Change Password",
            JOptionPane.OK_CANCEL_OPTION,
            JOptionPane.PLAIN_MESSAGE
        );
        
        if (result == JOptionPane.OK_OPTION) {
            String currentPassword = new String(currentField.getPassword());
            String newPassword = new String(newField.getPassword());
            String confirmPassword = new String(confirmField.getPassword());
            
            // Validate input
            if (currentPassword.isEmpty() || newPassword.isEmpty() || confirmPassword.isEmpty()) {
                JOptionPane.showMessageDialog(
                    parentComponent,
                    "All fields are required.",
                    "Validation Error",
                    JOptionPane.ERROR_MESSAGE
                );
                return;
            }
            
            if (!newPassword.equals(confirmPassword)) {
                JOptionPane.showMessageDialog(
                    parentComponent,
                    "New password and confirmation do not match.",
                    "Validation Error",
                    JOptionPane.ERROR_MESSAGE
                );
                return;
            }
            
            // Verify current password
            User currentUser = session.getCurrentUser();
            User authenticatedUser = userDao.authenticateUser(currentUser.getUsername(), currentPassword);
            
            if (authenticatedUser == null) {
                JOptionPane.showMessageDialog(
                    parentComponent,
                    "Current password is incorrect.",
                    "Authentication Error",
                    JOptionPane.ERROR_MESSAGE
                );
                return;
            }
            
            // Update password
            boolean success = userDao.updatePassword(currentUser.getId(), newPassword);
            
            if (success) {
                JOptionPane.showMessageDialog(
                    parentComponent,
                    "Password updated successfully.",
                    "Success",
                    JOptionPane.INFORMATION_MESSAGE
                );
            } else {
                JOptionPane.showMessageDialog(
                    parentComponent,
                    "Failed to update password. Please try again.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE
                );
            }
        }
    }
    
    /**
     * Gets the current user session
     * 
     * @return The user session
     */
    public Session getSession() {
        return session;
    }
}