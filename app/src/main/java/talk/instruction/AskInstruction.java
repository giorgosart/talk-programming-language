package talk.instruction;

import talk.core.Instruction;

public class AskInstruction implements Instruction {
    private final String prompt;
    private final String variableName;
    private final int lineNumber;

    public AskInstruction(String prompt, String variableName, int lineNumber) {
        this.prompt = prompt;
        this.variableName = variableName;
        this.lineNumber = lineNumber;
    }

    public String getPrompt() { return prompt; }
    public String getVariableName() { return variableName; }
    @Override
    public int getLineNumber() { return lineNumber; }
}
