package talk.instruction;

import java.util.List;

import talk.core.Instruction;

public class IfInstruction implements Instruction {
    private final String condition;
    private final List<Instruction> thenInstructions;
    private final List<Instruction> elseInstructions;
    private final int lineNumber;

    public IfInstruction(String condition, List<Instruction> thenInstructions, List<Instruction> elseInstructions, int lineNumber) {
        this.condition = condition;
        this.thenInstructions = thenInstructions;
        this.elseInstructions = elseInstructions;
        this.lineNumber = lineNumber;
    }

    public String getCondition() { return condition; }
    public List<Instruction> getThenInstructions() { return thenInstructions; }
    public List<Instruction> getElseInstructions() { return elseInstructions; }
    @Override
    public int getLineNumber() { return lineNumber; }
}
