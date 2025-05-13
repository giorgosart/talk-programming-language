package talk;

public class ListDirectoryInstruction implements Instruction {
    private final String directory;
    private final String variableName;
    private final int lineNumber;

    public ListDirectoryInstruction(String directory, String variableName, int lineNumber) {
        this.directory = directory;
        this.variableName = variableName;
        this.lineNumber = lineNumber;
    }

    public String getDirectory() {
        return directory;
    }

    public String getVariableName() {
        return variableName;
    }

    public int getLineNumber() {
        return lineNumber;
    }
}
