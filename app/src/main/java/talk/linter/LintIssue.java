package talk.linter;

/**
 * Represents a linting issue found in the code
 */
public class LintIssue {
    /**
     * Severity level of the lint issue
     */
    public enum Severity {
        ERROR,    // Definite problem that will cause runtime errors
        WARNING,  // Potential problem or bad practice
        INFO      // Suggestion or informational
    }
    
    private final Severity severity;
    private final int line;
    private final String message;
    private final String sourceLine;
    
    /**
     * Create a new lint issue
     * @param severity The severity level
     * @param line The line number (1-based)
     * @param message The descriptive message
     * @param sourceLine The source code line
     */
    public LintIssue(Severity severity, int line, String message, String sourceLine) {
        this.severity = severity;
        this.line = line;
        this.message = message;
        this.sourceLine = sourceLine;
    }
    
    /**
     * Get the severity level
     * @return The severity level
     */
    public Severity getSeverity() {
        return severity;
    }
    
    /**
     * Get the line number (1-based)
     * @return The line number
     */
    public int getLine() {
        return line;
    }
    
    /**
     * Get the descriptive message
     * @return The message
     */
    public String getMessage() {
        return message;
    }
    
    /**
     * Get the source code line
     * @return The source line
     */
    public String getSourceLine() {
        return sourceLine;
    }
}
