package talk.linter;

import talk.core.Tokenizer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Stack;

/**
 * Rule that checks for improper nesting of blocks in Talk code.
 */
public class ImproperNestingRule implements LintRule {
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
        int currentIndentLevel = 0;
        
        for (int i = 0; i < tokens.size(); i++) {
            Tokenizer.Token token = tokens.get(i);
            
            if ("INDENT".equals(token.value)) {
                currentIndentLevel++;
                continue;
            } else if ("DEDENT".equals(token.value)) {
                if (!blockStack.isEmpty()) {
                    blockStack.pop();
                }
                currentIndentLevel--;
                continue;
            }
            
            // Check if this token starts a new block
            if (BLOCK_STARTERS.contains(token.value) && isLineStart(tokens, i)) {
                // For if statements, we need to find the "then" token
                if ("if".equals(token.value)) {
                    // Find the "then" token
                    int thenPos = findNextTokenValue("then", tokens, i + 1);
                    if (thenPos != -1) {
                        // Check if the next token after "then" is INDENT
                        if (thenPos + 1 < tokens.size() && !"INDENT".equals(tokens.get(thenPos + 1).value)) {
                            // Next non-whitespace token after "then" should be on the next line (indented)
                            issues.add(new LintIssue(
                                LintIssue.Severity.ERROR,
                                token.lineNumber,
                                "Missing indentation after 'if-then' statement",
                                context.getSourceLine(token.lineNumber)
                            ));
                        }
                    }
                } else {
                    // For other block starters, check if the next token is INDENT
                    if (i + 1 < tokens.size() && !"INDENT".equals(tokens.get(i + 1).value)) {
                        issues.add(new LintIssue(
                            LintIssue.Severity.ERROR,
                            token.lineNumber,
                            "Missing indentation after '" + token.value + "' statement",
                            context.getSourceLine(token.lineNumber)
                        ));
                    }
                }
                
                // Push this block onto the stack
                blockStack.push(new BlockInfo(token.value, token.lineNumber, currentIndentLevel));
            }
            
            // Check "otherwise" without preceding "if"
            if ("otherwise".equals(token.value) && isLineStart(tokens, i)) {
                boolean hasMatchingIf = false;
                for (BlockInfo block : blockStack) {
                    if ("if".equals(block.type)) {
                        hasMatchingIf = true;
                        break;
                    }
                }
                
                if (!hasMatchingIf) {
                    issues.add(new LintIssue(
                        LintIssue.Severity.ERROR,
                        token.lineNumber,
                        "'otherwise' without matching 'if' statement",
                        context.getSourceLine(token.lineNumber)
                    ));
                }
            }
        }
        
        return issues;
    }
    
    /**
     * Find the position of the next token with the specified value
     */
    private int findNextTokenValue(String value, List<Tokenizer.Token> tokens, int startPos) {
        for (int i = startPos; i < tokens.size(); i++) {
            if (value.equals(tokens.get(i).value)) {
                return i;
            }
        }
        return -1;
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
        int indentLevel;
        
        public BlockInfo(String type, int lineNumber, int indentLevel) {
            this.type = type;
            this.lineNumber = lineNumber;
            this.indentLevel = indentLevel;
        }
    }
}
