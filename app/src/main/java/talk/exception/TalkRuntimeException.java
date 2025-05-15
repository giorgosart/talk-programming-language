package talk.exception;

/**
 * Exception thrown for runtime errors in the Talk programming language.
 * This includes errors like I/O errors, invalid inputs, execution failures, etc.
 */
public class TalkRuntimeException extends TalkException {
    
    /**
     * Creates a new runtime exception with a message and line number.
     * 
     * @param message The error message
     * @param lineNumber The line number where the runtime error occurred
     */
    public TalkRuntimeException(String message, int lineNumber) {
        super(message, lineNumber);
    }
    
    /**
     * Creates a new runtime exception with a message, line number, and cause.
     * 
     * @param message The error message
     * @param lineNumber The line number where the runtime error occurred
     * @param cause The underlying cause of the exception
     */
    public TalkRuntimeException(String message, int lineNumber, Throwable cause) {
        super(message, lineNumber, cause);
    }
    
    /**
     * Creates a new runtime exception with just a message.
     * This constructor should be used when the line number is unknown.
     * 
     * @param message The error message
     */
    public TalkRuntimeException(String message) {
        super(message);
    }
    
    /**
     * Creates a new runtime exception with a message and cause.
     * This constructor should be used when the line number is unknown.
     * 
     * @param message The error message
     * @param cause The underlying cause of the exception
     */
    public TalkRuntimeException(String message, Throwable cause) {
        super(message, cause);
    }
}
