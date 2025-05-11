package talk;

public class FunctionCallInstruction implements Instruction {
    private final String functionName;
    private final int lineNumber;

    public FunctionCallInstruction(String functionName, int lineNumber) {
        this.functionName = functionName;
        this.lineNumber = lineNumber;
    }

    public String getFunctionName() { return functionName; }
    @Override
    public int getLineNumber() { return lineNumber; }
}
