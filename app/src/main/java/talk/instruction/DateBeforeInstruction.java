package talk.instruction;

import talk.core.Instruction;

/**
 * Instruction to handle date comparison: 'if <date1> is before <date2>'
 */
public class DateBeforeInstruction implements Instruction {
    private final String firstDateExpression;
    private final String secondDateExpression;
    private final int lineNumber;

    public DateBeforeInstruction(String firstDateExpression, String secondDateExpression, int lineNumber) {
        this.firstDateExpression = firstDateExpression;
        this.secondDateExpression = secondDateExpression;
        this.lineNumber = lineNumber;
    }

    public String getFirstDateExpression() {
        return firstDateExpression;
    }

    public String getSecondDateExpression() {
        return secondDateExpression;
    }

    @Override
    public int getLineNumber() {
        return lineNumber;
    }
}
