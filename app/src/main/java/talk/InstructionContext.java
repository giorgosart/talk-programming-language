package talk;

/**
 * Context object for passing information to the InstructionFactory when creating instructions.
 */
public class InstructionContext {
    private final String identifier;
    private final Object value;
    private final int line;
    public InstructionContext(String identifier, Object value, int line) {
        this.identifier = identifier;
        this.value = value;
        this.line = line;
    }
    public String getIdentifier() { return identifier; }
    public Object getValue() { return value; }
    public int getLine() { return line; }
}
