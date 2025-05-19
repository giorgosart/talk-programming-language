package talk.instruction;

import talk.core.Instruction;

/**
 * Represents a test assertion in the Talk language.
 * This instruction evaluates an expression and compares it with an expected value.
 */
public class ExpectInstruction implements Instruction {
    private final String expression;
    private final String expectedValue;
    private final int lineNumber;
    
    public ExpectInstruction(String expression, String expectedValue, int lineNumber) {
        this.expression = expression;
        this.expectedValue = expectedValue;
        this.lineNumber = lineNumber;
    }
    
    public String getExpression() {
        return expression;
    }
    
    public String getExpectedValue() {
        return expectedValue;
    }
    
    @Override
    public int getLineNumber() {
        return lineNumber;
    }
}
