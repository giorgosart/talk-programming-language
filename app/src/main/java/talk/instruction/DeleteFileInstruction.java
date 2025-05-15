package talk.instruction;

import talk.core.Instruction;

public class DeleteFileInstruction implements Instruction {
    private final String fileName;
    private final int lineNumber;

    public DeleteFileInstruction(String fileName, int lineNumber) {
        this.fileName = fileName;
        this.lineNumber = lineNumber;
    }

    public String getFileName() {
        return fileName;
    }

    @Override
    public int getLineNumber() {
        return lineNumber;
    }
}
