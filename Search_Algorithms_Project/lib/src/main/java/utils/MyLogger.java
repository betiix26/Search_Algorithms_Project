package utils;

import java.util.logging.Level;
import java.util.logging.Logger;

public class MyLogger {
	private static final Logger LOGGER = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);

	private MyLogger() {
		throw new UnsupportedOperationException("Utility class");
	}

	public static void log(Level level, String message) {
		LOGGER.log(level, message);
	}
}
