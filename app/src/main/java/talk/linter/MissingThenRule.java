package talk.linter;

import talk.core.Tokenizer;

import java.util.ArrayList;
import java.util.List;

/**
 * Rule that checks for missing 'then' keywords after 'if' statements.
 */
public class MissingThenRule implements LintRule {

    @Override
    public List<LintIssue> check(LintContext context) {
        List<LintIssue> issues = new ArrayList<>();
        List<Tokenizer.Token> tokens = context.getTokens();
        
        if (tokens == null || tokens.isEmpty()) {
            return issues;
        }

        for (int i = 0; i < tokens.size(); i++) {
            Tokenizer.Token token = tokens.get(i);
            
            // Check if this is an 'if' token at the start of a line
            if ("if".equals(token.value) && isLineStart(tokens, i)) {
                // Look for a 'then' token before the end of the line or before any control flow tokens
                boolean foundThen = false;
                int currentLine = token.lineNumber;
                int j = i + 1;
                
                while (j < tokens.size() && 
                       tokens.get(j).lineNumber == currentLine && 
                       !isBlockStartKeyword(tokens.get(j).value)) {
                    if ("then".equals(tokens.get(j).value)) {
                        foundThen = true;
                        break;
                    }
                    j++;
                }
                
                if (!foundThen) {
                    issues.add(new LintIssue(
                        LintIssue.Severity.ERROR,
                        token.lineNumber,
                        "Missing 'then' after 'if' condition",
                        context.getSourceLine(token.lineNumber)
                    ));
                }
            }
        }
        
        return issues;
    }
    
    /**
     * Check if a token is a block start keyword that would terminate condition parsing
     */
    private boolean isBlockStartKeyword(String value) {
        return "if".equals(value) || "otherwise".equals(value) || 
               "repeat".equals(value) || "attempt".equals(value);
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
}
