package talk.exception;

/**
 * Exception thrown for syntax errors in the Talk programming language.
 * This includes errors like invalid tokens, missing keywords, unexpected end of input, etc.
 */
public class TalkSyntaxException extends TalkException {
    
    /**
     * Creates a new syntax exception with a message and line number.
     * 
     * @param message The error message
     * @param lineNumber The line number where the syntax error occurred
     */
    public TalkSyntaxException(String message, int lineNumber) {
        super(message, lineNumber);
    }
    
    /**
     * Creates a new syntax exception with a message, line number, and cause.
     * 
     * @param message The error message
     * @param lineNumber The line number where the syntax error occurred
     * @param cause The underlying cause of the exception
     */
    public TalkSyntaxException(String message, int lineNumber, Throwable cause) {
        super(message, lineNumber, cause);
    }
}
