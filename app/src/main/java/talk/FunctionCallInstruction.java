package talk;

import java.util.List;

public class FunctionCallInstruction implements Instruction {
    private final String functionName;
    private final int lineNumber;
    private final List<String> arguments;
    private final String intoVariable; // null if not used

    public FunctionCallInstruction(String functionName, List<String> arguments, int lineNumber) {
        this(functionName, arguments, null, lineNumber);
    }

    public FunctionCallInstruction(String functionName, List<String> arguments, String intoVariable, int lineNumber) {
        this.functionName = functionName;
        this.arguments = arguments;
        this.intoVariable = intoVariable;
        this.lineNumber = lineNumber;
    }

    // Retain old constructor for backward compatibility (deprecated, to be removed after parser update)
    @Deprecated
    public FunctionCallInstruction(String functionName, int lineNumber) {
        this(functionName, java.util.Collections.emptyList(), lineNumber);
    }

    public String getFunctionName() { return functionName; }
    public List<String> getArguments() { return arguments; }
    public String getIntoVariable() { return intoVariable; }
    @Override
    public int getLineNumber() { return lineNumber; }
}
