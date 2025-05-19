package talk.instruction;

import talk.core.Instruction;
import java.util.List;

/**
 * Represents a test block in the Talk language.
 * A test block contains a description and a list of instructions to execute.
 */
public class TestBlockInstruction implements Instruction {
    private final String description;
    private final List<Instruction> instructions;
    private final int lineNumber;
    
    public TestBlockInstruction(String description, List<Instruction> instructions, int lineNumber) {
        this.description = description;
        this.instructions = instructions;
        this.lineNumber = lineNumber;
    }
    
    public String getDescription() {
        return description;
    }
    
    public List<Instruction> getInstructions() {
        return instructions;
    }
    
    @Override
    public int getLineNumber() {
        return lineNumber;
    }
}
