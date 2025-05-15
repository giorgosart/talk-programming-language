package talk.instruction;

import talk.core.Instruction;

/**
 * Instruction to handle getting the day of week from a date: 'day of week of <date>'
 */
public class DayOfWeekInstruction implements Instruction {
    private final String dateExpression;
    private final String variableName;
    private final int lineNumber;

    public DayOfWeekInstruction(String dateExpression, String variableName, int lineNumber) {
        this.dateExpression = dateExpression;
        this.variableName = variableName;
        this.lineNumber = lineNumber;
    }

    public String getDateExpression() {
        return dateExpression;
    }

    public String getVariableName() {
        return variableName;
    }

    @Override
    public int getLineNumber() {
        return lineNumber;
    }
}
