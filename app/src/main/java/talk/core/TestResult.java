package talk.core;

/**
 * Class to track and store test results in the Talk language.
 */
public class TestResult {
    private final String testName;
    private final boolean passed;
    private final String message;
    private final int lineNumber;
    
    public TestResult(String testName, boolean passed, String message, int lineNumber) {
        this.testName = testName;
        this.passed = passed;
        this.message = message;
        this.lineNumber = lineNumber;
    }
    
    public String getTestName() {
        return testName;
    }
    
    public boolean isPassed() {
        return passed;
    }
    
    public String getMessage() {
        return message;
    }
    
    public int getLineNumber() {
        return lineNumber;
    }
    
    @Override
    public String toString() {
        return String.format("%s: %s (line %d) - %s", 
                             passed ? "✓ PASS" : "✗ FAIL", 
                             testName, 
                             lineNumber, 
                             message);
    }
}
