package talk.instruction;

import talk.core.Instruction;

/**
 * Instruction to handle adding days to a date: 'add <n> days to <date>'
 */
public class AddDaysInstruction implements Instruction {
    private final String days;
    private final String dateExpression;
    private final String variableName;
    private final int lineNumber;

    public AddDaysInstruction(String days, String dateExpression, String variableName, int lineNumber) {
        this.days = days;
        this.dateExpression = dateExpression;
        this.variableName = variableName;
        this.lineNumber = lineNumber;
    }

    public String getDays() {
        return days;
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
