package talk.instruction;

import talk.core.Instruction;

public class AssignmentInstruction implements Instruction {
    private final String variableName;
    private final Object value;
    private final int lineNumber;

    public AssignmentInstruction(String variableName, Object value, int lineNumber) {
        this.variableName = variableName;
        this.value = value;
        this.lineNumber = lineNumber;
    }

    public String getVariableName() { return variableName; }
    public Object getValue() { return value; }
    @Override
    public int getLineNumber() { return lineNumber; }
}
