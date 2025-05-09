package talk;

import java.util.List;

public class AttemptInstruction implements Instruction {
    private final List<Instruction> tryBlock;
    private final List<Instruction> catchBlock;
    private final int lineNumber;

    public AttemptInstruction(List<Instruction> tryBlock, List<Instruction> catchBlock, int lineNumber) {
        this.tryBlock = tryBlock;
        this.catchBlock = catchBlock;
        this.lineNumber = lineNumber;
    }

    public List<Instruction> getTryBlock() { return tryBlock; }
    public List<Instruction> getCatchBlock() { return catchBlock; }
    @Override
    public int getLineNumber() { return lineNumber; }
}
