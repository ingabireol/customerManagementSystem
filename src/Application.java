import controller.AuthController;
import dao.UserDao;
import ui.UIFactory;
import util.LogUtil;

import javax.swing.*;
import java.awt.*;

/**
 * Main application class for the Business Management System.
 * Initializes the application and starts the login screen.
 */
public class Application {
    
    /**
     * Application entry point
     * 
     * @param args Command-line arguments
     */
    public static void main(String[] args) {
        // Initialize logging
        LogUtil.initialize();
        // Set application properties
        System.setProperty("awt.useSystemAAFontSettings", "on");
         
        System.setProperty("swing.aatext", "true");
        
        // Display splash screen while initializing
        SplashScreen splash = SplashScreen.getSplashScreen();
        
        // Initialize the database in a background thread
        SwingWorker<Boolean, Void> worker = new SwingWorker<Boolean, Void>() {
            @Override
            protected Boolean doInBackground() throws Exception {
                // Initialize tables
                UserDao userDao = new UserDao();
                return userDao.createUsersTable() && userDao.createDefaultAdmin();
                
            }
            
            @Override
            protected void done() {
                try {
                    // Set up the UI look and feel
                    setupLookAndFeel();
                    
                    // Close splash screen if it exists
                    if (splash != null) {
                        splash.close();
                    }
                    
                    // Show the login screen
                    SwingUtilities.invokeLater(() -> {
                        AuthController authController = new AuthController(null);
                        authController.showLoginView();
                    });
                } catch (Exception ex) {
                    // Log error and show message
                    LogUtil.error("Failed to initialize application: " + ex.getMessage(), ex);
                    JOptionPane.showMessageDialog(
                        null,
                        "Error initializing application: " + ex.getMessage(),
                        "Application Error",
                        JOptionPane.ERROR_MESSAGE
                    );
                    System.exit(1);
                }
            }
        };
        
        worker.execute();
    }
    
    /**
     * Sets up the UI look and feel
     */
    private static void setupLookAndFeel() {
        try {
            // Set the look and feel to the system look and feel
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            
            // Set global font settings
            Font defaultFont = new Font("Segoe UI", Font.PLAIN, 12);
            Font boldFont = new Font("Segoe UI", Font.BOLD, 12);
            Font titleFont = new Font("Segoe UI", Font.BOLD, 18);
            
            UIManager.put("Label.font", defaultFont);
            UIManager.put("Button.font", defaultFont);
            UIManager.put("TextField.font", defaultFont);
            UIManager.put("TextArea.font", defaultFont);
            UIManager.put("ComboBox.font", defaultFont);
            UIManager.put("CheckBox.font", defaultFont);
            UIManager.put("RadioButton.font", defaultFont);
            UIManager.put("Table.font", defaultFont);
            UIManager.put("TableHeader.font", boldFont);
            UIManager.put("TabbedPane.font", defaultFont);
            UIManager.put("MenuBar.font", defaultFont);
            UIManager.put("Menu.font", defaultFont);
            UIManager.put("MenuItem.font", defaultFont);
            UIManager.put("OptionPane.messageFont", defaultFont);
            UIManager.put("OptionPane.buttonFont", defaultFont);
            
            // Set primary color for highlighted components
            UIManager.put("Panel.background", Color.WHITE);
            UIManager.put("OptionPane.background", Color.WHITE);
            UIManager.put("Button.background", UIFactory.PRIMARY_COLOR);
            UIManager.put("Button.foreground", Color.WHITE);
            UIManager.put("Button.select", new Color(0x1565C0)); // Darker shade
            
            LogUtil.info("UI Look and Feel initialized");
        } catch (Exception ex) {
            LogUtil.warning("Failed to set look and feel: " + ex.getMessage(), ex);
            // Continue with default look and feel
        }
    }
}