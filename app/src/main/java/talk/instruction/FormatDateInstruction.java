package talk.instruction;

import talk.core.Instruction;

/**
 * Instruction to handle date formatting: 'format date <date> as <pattern>'
 */
public class FormatDateInstruction implements Instruction {
    private final String dateExpression;
    private final String pattern;
    private final String variableName;
    private final int lineNumber;

    public FormatDateInstruction(String dateExpression, String pattern, String variableName, int lineNumber) {
        this.dateExpression = dateExpression;
        this.pattern = pattern;
        this.variableName = variableName;
        this.lineNumber = lineNumber;
    }

    public String getDateExpression() {
        return dateExpression;
    }

    public String getPattern() {
        return pattern;
    }

    public String getVariableName() {
        return variableName;
    }

    @Override
    public int getLineNumber() {
        return lineNumber;
    }
}
