package talk;

public class VariableInstruction implements Instruction {
    private final String name;
    private final Object value; // Can be null if just declaration
    private final int lineNumber;

    public VariableInstruction(String name, Object value, int lineNumber) {
        this.name = name;
        this.value = value;
        this.lineNumber = lineNumber;
    }

    public String getName() { return name; }
    public Object getValue() { return value; }
    @Override
    public int getLineNumber() { return lineNumber; }
}
