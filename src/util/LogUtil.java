package util;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.logging.ConsoleHandler;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

/**
 * Utility class for logging operations throughout the application.
 */
public class LogUtil {
    private static final Logger LOGGER = Logger.getLogger("BusinessManagementSystem");
    private static final String LOG_DIRECTORY = "logs";
    private static final String LOG_FILE_PREFIX = "business_system_";
    private static final DateTimeFormatter LOG_DATE_FORMAT = DateTimeFormatter.ofPattern("yyyyMMdd");
    private static boolean initialized = false;
    
    /**
     * Initializes the logging system
     */
    public static synchronized void initialize() {
        if (initialized) {
            return;
        }
        
        try {
            // Create logs directory if it doesn't exist
            File logDir = new File(LOG_DIRECTORY);
            if (!logDir.exists()) {
                logDir.mkdir();
            }
            
            // Configure logger
            LOGGER.setUseParentHandlers(false);
            LOGGER.setLevel(Level.ALL);
            
            // Add console handler
            ConsoleHandler consoleHandler = new ConsoleHandler();
            consoleHandler.setLevel(Level.INFO);
            LOGGER.addHandler(consoleHandler);
            
            // Add file handler
            String logFileName = LOG_DIRECTORY + File.separator + 
                    LOG_FILE_PREFIX + LocalDateTime.now().format(LOG_DATE_FORMAT) + ".log";
            FileHandler fileHandler = new FileHandler(logFileName, true);
            fileHandler.setFormatter(new SimpleFormatter());
            fileHandler.setLevel(Level.ALL);
            LOGGER.addHandler(fileHandler);
            
            initialized = true;
            info("Logging system initialized");
        } catch (IOException e) {
            System.err.println("Failed to initialize logging system: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Logs an informational message
     * 
     * @param message The message to log
     */
    public static void info(String message) {
        ensureInitialized();
        LOGGER.info(message);
    }
    
    /**
     * Logs a warning message
     * 
     * @param message The message to log
     */
    public static void warning(String message) {
        ensureInitialized();
        LOGGER.warning(message);
    }
    
    /**
     * Logs a warning message with an exception
     * 
     * @param message The message to log
     * @param t The exception to log
     */
    public static void warning(String message, Throwable t) {
        ensureInitialized();
        LOGGER.log(Level.WARNING, message, t);
    }
    
    /**
     * Logs an error message
     * 
     * @param message The message to log
     */
    public static void error(String message) {
        ensureInitialized();
        LOGGER.severe(message);
    }
    
    /**
     * Logs an error message with an exception
     * 
     * @param message The message to log
     * @param t The exception to log
     */
    public static void error(String message, Throwable t) {
        ensureInitialized();
        LOGGER.log(Level.SEVERE, message, t);
    }
    
    /**
     * Logs a debug message
     * 
     * @param message The message to log
     */
    public static void debug(String message) {
        ensureInitialized();
        LOGGER.fine(message);
    }
    
    /**
     * Ensures the logging system is initialized
     */
    private static void ensureInitialized() {
        if (!initialized) {
            initialize();
        }
    }
    
    /**
     * Closes all log handlers
     */
    public static void shutdown() {
        if (!initialized) {
            return;
        }
        
        for (java.util.logging.Handler handler : LOGGER.getHandlers()) {
            handler.close();
        }
        
        info("Logging system shutdown");
    }
}