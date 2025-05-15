package talk.io;

import java.io.IOException;

/**
 * Defines the logging interface for the Talk programming language.
 * This interface enables dependency injection and better testability
 * by abstracting away the actual logging implementation.
 */
public interface Logger {
    /**
     * Logs a message
     * @param message The message to log
     * @throws IOException If an I/O error occurs during logging
     */
    void log(String message) throws IOException;
    
    /**
     * Logs a message with an associated line number for debugging
     * @param message The message to log
     * @param lineNumber The line number where the message originated
     * @throws IOException If an I/O error occurs during logging
     */
    void log(String message, int lineNumber) throws IOException;
    
    /**
     * Logs an error message
     * @param message The error message to log
     * @throws IOException If an I/O error occurs during logging
     */
    void error(String message) throws IOException;
    
    /**
     * Logs an error message with an associated line number for debugging
     * @param message The error message to log
     * @param lineNumber The line number where the error occurred
     * @throws IOException If an I/O error occurs during logging
     */
    void error(String message, int lineNumber) throws IOException;
    
    /**
     * Logs a debug message - may be suppressed depending on log level
     * @param message The debug message to log
     * @throws IOException If an I/O error occurs during logging
     */
    void debug(String message) throws IOException;
}
