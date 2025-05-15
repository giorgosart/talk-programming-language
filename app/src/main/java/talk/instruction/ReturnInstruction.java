package talk.instruction;

import talk.core.Instruction;

public class ReturnInstruction implements Instruction {
    private final String expression;
    private final int lineNumber;

    public ReturnInstruction(String expression, int lineNumber) {
        this.expression = expression;
        this.lineNumber = lineNumber;
    }

    public String getExpression() { return expression; }
    @Override
    public int getLineNumber() { return lineNumber; }
}
