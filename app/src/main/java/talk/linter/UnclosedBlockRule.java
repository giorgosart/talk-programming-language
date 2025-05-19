package talk.linter;

import talk.core.Tokenizer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Stack;

/**
 * Rule that checks for unclosed code blocks in Talk code.
 */
public class UnclosedBlockRule implements LintRule {
    // Instructions that should be followed by indented blocks
    private static final Set<String> BLOCK_STARTERS = new HashSet<>(Arrays.asList(
        "if", "otherwise", "repeat", "attempt", "test", "before", "after", "define"
    ));

    @Override
    public List<LintIssue> check(LintContext context) {
        List<LintIssue> issues = new ArrayList<>();
        List<Tokenizer.Token> tokens = context.getTokens();
        
        if (tokens == null || tokens.isEmpty()) {
            return issues;
        }

        Stack<BlockInfo> blockStack = new Stack<>();
        
        for (int i = 0; i < tokens.size(); i++) {
            Tokenizer.Token token = tokens.get(i);
            
            if ("INDENT".equals(token.value)) {
                // There should be a block starter before this indent
                if (blockStack.isEmpty()) {
                    issues.add(new LintIssue(
                        LintIssue.Severity.ERROR,
                        token.lineNumber,
                        "Unexpected indentation without a block starter",
                        context.getSourceLine(token.lineNumber)
                    ));
                }
                continue;
            } else if ("DEDENT".equals(token.value)) {
                if (!blockStack.isEmpty()) {
                    blockStack.pop();
                } else {
                    issues.add(new LintIssue(
                        LintIssue.Severity.ERROR,
                        token.lineNumber,
                        "Unexpected dedentation without matching indentation",
                        context.getSourceLine(token.lineNumber)
                    ));
                }
                continue;
            }
            
            // Check if this token starts a new block
            if (BLOCK_STARTERS.contains(token.value) && isLineStart(tokens, i)) {
                // Push this block onto the stack
                blockStack.push(new BlockInfo(token.value, token.lineNumber));
            }
        }
        
        // Check if there are any unclosed blocks left
        while (!blockStack.isEmpty()) {
            BlockInfo block = blockStack.pop();
            issues.add(new LintIssue(
                LintIssue.Severity.ERROR,
                block.lineNumber,
                "Unclosed block: '" + block.type + "' block started but not properly closed with dedentation",
                context.getSourceLine(block.lineNumber)
            ));
        }
        
        return issues;
    }
    
    /**
     * Check if a token position represents the beginning of a line/instruction
     */
    private boolean isLineStart(List<Tokenizer.Token> tokens, int position) {
        if (position == 0) {
            return true;
        }
        
        // Check if previous token is on a different line or is a DEDENT
        Tokenizer.Token prevToken = tokens.get(position - 1);
        Tokenizer.Token currentToken = tokens.get(position);
        
        return prevToken.lineNumber != currentToken.lineNumber || "DEDENT".equals(prevToken.value);
    }
    
    /**
     * Helper class to track block information
     */
    private static class BlockInfo {
        String type;
        int lineNumber;
        
        public BlockInfo(String type, int lineNumber) {
            this.type = type;
            this.lineNumber = lineNumber;
        }
    }
}
