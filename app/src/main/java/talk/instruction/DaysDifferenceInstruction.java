package talk.instruction;

import talk.core.Instruction;

/**
 * Instruction to handle getting the difference in days between dates:
 * 'difference in days between <date1> and <date2>'
 */
public class DaysDifferenceInstruction implements Instruction {
    private final String firstDateExpression;
    private final String secondDateExpression;
    private final String variableName;
    private final int lineNumber;

    public DaysDifferenceInstruction(String firstDateExpression, String secondDateExpression, 
                                   String variableName, int lineNumber) {
        this.firstDateExpression = firstDateExpression;
        this.secondDateExpression = secondDateExpression;
        this.variableName = variableName;
        this.lineNumber = lineNumber;
    }

    public String getFirstDateExpression() {
        return firstDateExpression;
    }

    public String getSecondDateExpression() {
        return secondDateExpression;
    }

    public String getVariableName() {
        return variableName;
    }

    @Override
    public int getLineNumber() {
        return lineNumber;
    }
}
