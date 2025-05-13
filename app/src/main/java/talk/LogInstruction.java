package talk;

public class LogInstruction implements Instruction {
    private final String message;
    private final int lineNumber;

    public LogInstruction(String message, int lineNumber) {
        this.message = message;
        this.lineNumber = lineNumber;
    }

    public String getMessage() {
        return message;
    }

    @Override
    public int getLineNumber() {
        return lineNumber;
    }
}
