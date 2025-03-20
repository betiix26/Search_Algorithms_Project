package utils;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Utility class for logging messages with different severity levels.
 * Uses Java's built-in logging framework.
 */
public class MyLogger {
    
    private static final Logger LOGGER = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);

    /**
     * Private constructor to prevent instantiation of utility class.
     */
    private MyLogger() {
        throw new UnsupportedOperationException("Utility class");
    }

    /**
     * Logs a message at the specified level.
     * 
     * @param level   The logging level (e.g., Level.INFO, Level.SEVERE).
     * @param message The message to log.
     */
    public static void log(Level level, String message) {
        LOGGER.log(level, message);
    }
}
