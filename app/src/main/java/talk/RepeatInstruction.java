package talk;

import java.util.List;

public class RepeatInstruction implements Instruction {
    private final String countExpr;
    private final List<Instruction> body;
    private final int lineNumber;
    public RepeatInstruction(String countExpr, List<Instruction> body, int lineNumber) {
        this.countExpr = countExpr;
        this.body = body;
        this.lineNumber = lineNumber;
    }
    public String getCountExpr() { return countExpr; }
    public List<Instruction> getBody() { return body; }
    @Override
    public int getLineNumber() { return lineNumber; }
}
