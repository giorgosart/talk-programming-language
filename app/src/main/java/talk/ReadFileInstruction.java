package talk;

public class ReadFileInstruction implements Instruction {
    private final String fileName;
    private final String variableName;
    private final int lineNumber;

    public ReadFileInstruction(String fileName, String variableName, int lineNumber) {
        this.fileName = fileName;
        this.variableName = variableName;
        this.lineNumber = lineNumber;
    }

    public String getFileName() {
        return fileName;
    }

    public String getVariableName() {
        return variableName;
    }

    @Override
    public int getLineNumber() {
        return lineNumber;
    }
}
