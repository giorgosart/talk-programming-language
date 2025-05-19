package talk.linter;

import talk.core.Tokenizer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Rule that checks for unknown instructions in Talk code.
 */
public class UnknownInstructionRule implements LintRule {
    // Set of all valid Talk instruction keywords
    private static final Set<String> VALID_INSTRUCTIONS = new HashSet<>(Arrays.asList(
        "write", "variable", "set", "ask", "if", "otherwise", "repeat", "until", 
        "attempt", "test", "before", "after", "expect", "read", "append", "delete", 
        "copy", "list", "define", "return", "import", "use", "uppercase", "lowercase",
        "trim", "length", "substring", "replace", "split", "log", "call", "format"
    ));

    @Override
    public List<LintIssue> check(LintContext context) {
        List<LintIssue> issues = new ArrayList<>();
        List<Tokenizer.Token> tokens = context.getTokens();
        
        // Skip empty files
        if (tokens == null || tokens.isEmpty()) {
            return issues;
        }

        // We'll check each line's first non-whitespace token
        for (int i = 0; i < tokens.size(); i++) {
            Tokenizer.Token token = tokens.get(i);
            
            // Skip tokens that can't be the start of an instruction
            if ("INDENT".equals(token.value) || "DEDENT".equals(token.value) || 
                "then".equals(token.value) || "and".equals(token.value) || 
                "or".equals(token.value) || "not".equals(token.value) ||
                "to".equals(token.value) || "with".equals(token.value) || 
                "of".equals(token.value) || "by".equals(token.value) ||
                "from".equals(token.value) || "into".equals(token.value) ||
                "in".equals(token.value)) {
                continue;
            }
            
            // If it's a potential instruction start and not recognized, report it
            if (isLineStart(tokens, i) && !VALID_INSTRUCTIONS.contains(token.value.toLowerCase())) {
                issues.add(new LintIssue(
                    LintIssue.Severity.ERROR,
                    token.lineNumber,
                    "Unknown instruction: '" + token.value + "'",
                    context.getSourceLine(token.lineNumber)
                ));
            }
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
}
