package talk.exception;

/**
 * Exception thrown for value-related errors in the Talk programming language.
 * This includes errors like type conversion errors, index out of bounds, 
 * invalid operations on values, etc.
 */
public class TalkValueException extends TalkException {
    
    /**
     * Creates a new value exception with a message and line number.
     * 
     * @param message The error message
     * @param lineNumber The line number where the value error occurred
     */
    public TalkValueException(String message, int lineNumber) {
        super(message, lineNumber);
    }
    
    /**
     * Creates a new value exception with a message, line number, and cause.
     * 
     * @param message The error message
     * @param lineNumber The line number where the value error occurred
     * @param cause The underlying cause of the exception
     */
    public TalkValueException(String message, int lineNumber, Throwable cause) {
        super(message, lineNumber, cause);
    }
    
    /**
     * Creates a new value exception with just a message.
     * This constructor should be used when the line number is unknown.
     * 
     * @param message The error message
     */
    public TalkValueException(String message) {
        super(message);
    }
    
    /**
     * Creates a new value exception with a message and cause.
     * This constructor should be used when the line number is unknown.
     * 
     * @param message The error message
     * @param cause The underlying cause of the exception
     */
    public TalkValueException(String message, Throwable cause) {
        super(message, cause);
    }
}
