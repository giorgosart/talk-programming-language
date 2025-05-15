package talk.exception;

/**
 * Base exception class for all Talk language exceptions.
 * This class provides common functionality for all Talk-specific exceptions.
 */
public class TalkException extends RuntimeException {
    private final int lineNumber;
    
    /**
     * Creates a new TalkException with a message and line number.
     * 
     * @param message The error message
     * @param lineNumber The line number where the error occurred
     */
    public TalkException(String message, int lineNumber) {
        super(formatMessage(message, lineNumber));
        this.lineNumber = lineNumber;
    }
    
    /**
     * Creates a new TalkException with a message, line number, and cause.
     * 
     * @param message The error message
     * @param lineNumber The line number where the error occurred
     * @param cause The underlying cause of the exception
     */
    public TalkException(String message, int lineNumber, Throwable cause) {
        super(formatMessage(message, lineNumber), cause);
        this.lineNumber = lineNumber;
    }
    
    /**
     * Creates a new TalkException with just a message.
     * This constructor should be used when the line number is unknown.
     * 
     * @param message The error message
     */
    public TalkException(String message) {
        super(message);
        this.lineNumber = -1;
    }
    
    /**
     * Creates a new TalkException with a message and cause.
     * This constructor should be used when the line number is unknown.
     * 
     * @param message The error message
     * @param cause The underlying cause of the exception
     */
    public TalkException(String message, Throwable cause) {
        super(message, cause);
        this.lineNumber = -1;
    }
    
    /**
     * Gets the line number where the error occurred.
     * 
     * @return The line number, or -1 if not available
     */
    public int getLineNumber() {
        return lineNumber;
    }
    
    /**
     * Formats an error message to include the line number if available.
     * 
     * @param message The base error message
     * @param lineNumber The line number where the error occurred
     * @return A formatted message including line number information
     */
    private static String formatMessage(String message, int lineNumber) {
        return (lineNumber > 0) ? message + " (line " + lineNumber + ")" : message;
    }
}
