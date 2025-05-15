package talk.io;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Default implementation of the Logger interface.
 * Logs messages to a file and/or console based on configuration.
 */
public class DefaultLogger implements Logger {
    private final String logFile;
    private final boolean logToConsole;
    private final boolean debugEnabled;
    private final FileSystem fileSystem;
    
    private static final DateTimeFormatter TIMESTAMP_FORMAT = 
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    
    /**
     * Constructor with all settings
     * @param logFile The file to log to
     * @param logToConsole Whether to log to console
     * @param debugEnabled Whether debug messages should be logged
     * @param fileSystem The file system to use for writing logs
     */
    public DefaultLogger(String logFile, boolean logToConsole, boolean debugEnabled, FileSystem fileSystem) {
        this.logFile = logFile;
        this.logToConsole = logToConsole;
        this.debugEnabled = debugEnabled;
        this.fileSystem = fileSystem;
    }
    
    /**
     * Simplified constructor with defaults
     * @param fileSystem The file system to use for writing logs
     */
    public DefaultLogger(FileSystem fileSystem) {
        this("debug.log", true, false, fileSystem);
    }
    
    @Override
    public void log(String message) throws IOException {
        String formattedMessage = formatLogMessage("INFO", message);
        logMessage(formattedMessage);
    }
    
    @Override
    public void log(String message, int lineNumber) throws IOException {
        String formattedMessage = formatLogMessage("INFO", message + " (line " + lineNumber + ")");
        logMessage(formattedMessage);
    }
    
    @Override
    public void error(String message) throws IOException {
        String formattedMessage = formatLogMessage("ERROR", message);
        logMessage(formattedMessage);
    }
    
    @Override
    public void error(String message, int lineNumber) throws IOException {
        String formattedMessage = formatLogMessage("ERROR", message + " (line " + lineNumber + ")");
        logMessage(formattedMessage);
    }
    
    @Override
    public void debug(String message) throws IOException {
        if (debugEnabled) {
            String formattedMessage = formatLogMessage("DEBUG", message);
            logMessage(formattedMessage);
        }
    }
    
    private String formatLogMessage(String level, String message) {
        LocalDateTime now = LocalDateTime.now();
        return String.format("[%s] [%s] %s", now.format(TIMESTAMP_FORMAT), level, message);
    }
    
    private void logMessage(String message) throws IOException {
        if (logToConsole) {
            System.out.println(message);
        }
        
        fileSystem.appendToFile(logFile, message + System.lineSeparator());
    }
}
