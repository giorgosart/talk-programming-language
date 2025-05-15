package talk.instruction;

import talk.core.Instruction;

/**
 * Instruction to handle parsing dates: 'parse date <string>'
 */
public class ParseDateInstruction implements Instruction {
    private final String dateString;
    private final String variableName;
    private final int lineNumber;

    public ParseDateInstruction(String dateString, String variableName, int lineNumber) {
        this.dateString = dateString;
        this.variableName = variableName;
        this.lineNumber = lineNumber;
    }

    public String getDateString() {
        return dateString;
    }

    public String getVariableName() {
        return variableName;
    }

    @Override
    public int getLineNumber() {
        return lineNumber;
    }
}
