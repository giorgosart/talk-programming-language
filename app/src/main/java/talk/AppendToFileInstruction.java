package talk;

public class AppendToFileInstruction implements Instruction {
    private final String text;
    private final String fileName;
    private final int lineNumber;

    public AppendToFileInstruction(String text, String fileName, int lineNumber) {
        this.text = text;
        this.fileName = fileName;
        this.lineNumber = lineNumber;
    }

    public String getText() {
        return text;
    }

    public String getFileName() {
        return fileName;
    }

    @Override
    public int getLineNumber() {
        return lineNumber;
    }
}
