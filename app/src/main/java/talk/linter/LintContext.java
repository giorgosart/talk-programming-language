package talk.linter;

import talk.core.Tokenizer;

import java.util.List;

/**
 * Context object containing all information needed for linting rules
 */
public class LintContext {
    private final List<String> sourceLines;
    private final List<Tokenizer.Token> tokens;
    private final talk.core.InstructionFactory instructionFactory;
    
    /**
     * Create a new lint context
     * @param sourceLines The source code lines
     * @param tokens The tokenized code
     * @param instructionFactory Factory for validating instructions
     */
    public LintContext(List<String> sourceLines, List<Tokenizer.Token> tokens, talk.core.InstructionFactory instructionFactory) {
        this.sourceLines = sourceLines;
        this.tokens = tokens;
        this.instructionFactory = instructionFactory;
    }
    
    /**
     * Get the source code lines
     * @return List of source code lines
     */
    public List<String> getSourceLines() {
        return sourceLines;
    }
    
    /**
     * Get the tokens
     * @return List of tokens
     */
    public List<Tokenizer.Token> getTokens() {
        return tokens;
    }
    
    /**
     * Get the instruction factory
     * @return The instruction factory
     */
    public talk.core.InstructionFactory getInstructionFactory() {
        return instructionFactory;
    }
    
    /**
     * Get a source line by line number (1-based)
     * @param lineNumber The line number (1-based)
     * @return The source line or empty string if out of bounds
     */
    public String getSourceLine(int lineNumber) {
        if (lineNumber > 0 && lineNumber <= sourceLines.size()) {
            return sourceLines.get(lineNumber - 1);
        }
        return "";
    }
}
