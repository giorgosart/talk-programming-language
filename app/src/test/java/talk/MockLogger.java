package talk;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import talk.io.Logger;

/**
 * A mock implementation of Logger for testing purposes.
 * This allows tests to verify logging behavior without writing to actual files.
 */
public class MockLogger implements Logger {
    private final List<String> logMessages = new ArrayList<>();
    private final List<String> errorMessages = new ArrayList<>();
    private final List<String> debugMessages = new ArrayList<>();
    
    /**
     * Get all logged messages for testing verification
     */
    public List<String> getLogMessages() {
        return logMessages;
    }
    
    /**
     * Get all error messages for testing verification
     */
    public List<String> getErrorMessages() {
        return errorMessages;
    }
    
    /**
     * Get all debug messages for testing verification
     */
    public List<String> getDebugMessages() {
        return debugMessages;
    }
    
    /**
     * Clear all recorded messages
     */
    public void reset() {
        logMessages.clear();
        errorMessages.clear();
        debugMessages.clear();
    }
    
    @Override
    public void log(String message) throws IOException {
        logMessages.add(message);
    }
    
    @Override
    public void log(String message, int lineNumber) throws IOException {
        logMessages.add(message + " (line " + lineNumber + ")");
    }
    
    @Override
    public void error(String message) throws IOException {
        errorMessages.add(message);
    }
    
    @Override
    public void error(String message, int lineNumber) throws IOException {
        errorMessages.add(message + " (line " + lineNumber + ")");
    }
    
    @Override
    public void debug(String message) throws IOException {
        debugMessages.add(message);
    }
}
