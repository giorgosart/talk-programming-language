package talk;

import java.util.List;

public class FunctionDefinitionInstruction implements Instruction {
    private final String functionName;
    private final List<Instruction> body;
    private final int lineNumber;

    public FunctionDefinitionInstruction(String functionName, List<Instruction> body, int lineNumber) {
        this.functionName = functionName;
        this.body = body;
        this.lineNumber = lineNumber;
    }

    public String getFunctionName() { return functionName; }
    public List<Instruction> getBody() { return body; }
    @Override
    public int getLineNumber() { return lineNumber; }
}
