package talk;

public class CopyFileInstruction implements Instruction {
    private final String source;
    private final String destination;
    private final int lineNumber;

    public CopyFileInstruction(String source, String destination, int lineNumber) {
        this.source = source;
        this.destination = destination;
        this.lineNumber = lineNumber;
    }

    public String getSource() {
        return source;
    }

    public String getDestination() {
        return destination;
    }

    @Override
    public int getLineNumber() {
        return lineNumber;
    }
}
