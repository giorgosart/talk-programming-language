package talk.instruction;

import talk.core.Instruction;

public class WriteInstruction implements Instruction {
    private final String content;
    private final String fileName;
    private final int lineNumber;

    public WriteInstruction(String content, String fileName, int lineNumber) {
        this.content = content;
        this.fileName = fileName;
        this.lineNumber = lineNumber;
    }

    public String getContent() { return content; }
    public String getFileName() { return fileName; }
    @Override
    public int getLineNumber() { return lineNumber; }
}
