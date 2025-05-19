package talk.instruction;

import talk.core.Instruction;

/**
 * Instruction for importing code from other Talk script files.
 * This allows for modularizing Talk programs by separating code into multiple files.
 */
public class ImportInstruction implements Instruction {
    private final String filePath;
    private final int lineNumber;
    
    public ImportInstruction(String filePath, int lineNumber) {
        this.filePath = filePath;
        this.lineNumber = lineNumber;
    }
    
    /**
     * Get the path of the file to import.
     * 
     * @return The file path as a string
     */
    public String getFilePath() {
        return filePath;
    }
    
    @Override
    public int getLineNumber() {
        return lineNumber;
    }
    
    @Override
    public String toString() {
        return "Import(\"" + filePath + "\")";
    }
}
