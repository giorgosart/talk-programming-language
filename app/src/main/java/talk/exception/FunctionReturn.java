package talk.exception;

/**
 * Special exception used to signal an early return from a function.
 * This is not a true exception, but uses the exception mechanism for flow control.
 */
public class FunctionReturn extends TalkException {
    private final Object value;
    
    /**
     * Creates a new function return with the return value.
     * 
     * @param value The value being returned from the function
     */
    public FunctionReturn(Object value) {
        super("Function return");
        this.value = value;
    }
    
    /**
     * Creates a new function return with the return value and line number.
     * 
     * @param value The value being returned from the function
     * @param lineNumber The line number where the return occurs
     */
    public FunctionReturn(Object value, int lineNumber) {
        super("Function return", lineNumber);
        this.value = value;
    }
    
    /**
     * Gets the return value from the function.
     * 
     * @return The return value
     */
    public Object getValue() {
        return value;
    }
}
