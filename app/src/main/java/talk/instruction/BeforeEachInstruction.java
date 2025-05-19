package talk.instruction;

import talk.core.Instruction;
import java.util.List;

/**
 * Represents a block of instructions to execute before each test in the Talk language.
 */
public class BeforeEachInstruction implements Instruction {
    private final List<Instruction> instructions;
    private final int lineNumber;
    
    public BeforeEachInstruction(List<Instruction> instructions, int lineNumber) {
        this.instructions = instructions;
        this.lineNumber = lineNumber;
    }
    
    public List<Instruction> getInstructions() {
        return instructions;
    }
    
    @Override
    public int getLineNumber() {
        return lineNumber;
    }
}
