package talk.exception;

/**
 * Exception thrown for semantic errors in the Talk programming language.
 * This includes errors like undefined variables, redefined variables, 
 * missing functions, argument mismatches, etc.
 */
public class TalkSemanticException extends TalkException {
    
    /**
     * Creates a new semantic exception with a message and line number.
     * 
     * @param message The error message
     * @param lineNumber The line number where the semantic error occurred
     */
    public TalkSemanticException(String message, int lineNumber) {
        super(message, lineNumber);
    }
    
    /**
     * Creates a new semantic exception with a message, line number, and cause.
     * 
     * @param message The error message
     * @param lineNumber The line number where the semantic error occurred
     * @param cause The underlying cause of the exception
     */
    public TalkSemanticException(String message, int lineNumber, Throwable cause) {
        super(message, lineNumber, cause);
    }
}
