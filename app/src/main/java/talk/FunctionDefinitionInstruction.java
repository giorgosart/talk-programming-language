package talk;

import java.util.List;

public class FunctionDefinitionInstruction implements Instruction {
    private final String functionName;
    private final List<String> parameters;
    private final List<Instruction> body;
    private final int lineNumber;

    public FunctionDefinitionInstruction(String functionName, List<String> parameters, List<Instruction> body, int lineNumber) {
        this.functionName = functionName;
        this.parameters = parameters;
        this.body = body;
        this.lineNumber = lineNumber;
    }

    // Retain the old constructor for backward compatibility (deprecated, to be removed after parser update)
    @Deprecated
    public FunctionDefinitionInstruction(String functionName, List<Instruction> body, int lineNumber) {
        this(functionName, java.util.Collections.emptyList(), body, lineNumber);
    }

    public String getFunctionName() { return functionName; }
    public List<String> getParameters() { return parameters; }
    public List<Instruction> getBody() { return body; }
    @Override
    public int getLineNumber() { return lineNumber; }
}
