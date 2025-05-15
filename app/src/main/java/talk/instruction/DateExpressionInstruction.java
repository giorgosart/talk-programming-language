package talk.instruction;

import talk.core.Instruction;

/**
 * Instruction to handle date expressions like 'now' and 'today'
 */
public class DateExpressionInstruction implements Instruction {
    private final String expression;
    private final String variableName;
    private final int lineNumber;

    public DateExpressionInstruction(String expression, String variableName, int lineNumber) {
        this.expression = expression;
        this.variableName = variableName;
        this.lineNumber = lineNumber;
    }

    public String getExpression() {
        return expression;
    }

    public String getVariableName() {
        return variableName;
    }

    @Override
    public int getLineNumber() {
        return lineNumber;
    }
}
