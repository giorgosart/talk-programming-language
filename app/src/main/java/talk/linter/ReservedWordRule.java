package talk.linter;

import talk.core.Tokenizer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Rule that checks for usage of reserved words as variable names.
 */
public class ReservedWordRule implements LintRule {
    // Set of all Talk language keywords that are reserved and shouldn't be used as variable names
    private static final Set<String> RESERVED_WORDS = new HashSet<>(Arrays.asList(
        "write", "variable", "set", "ask", "and", "store", "in", "if", "then", "otherwise", 
        "repeat", "until", "attempt", "test", "before", "after", "each", "expect", "result", 
        "of", "be", "read", "file", "into", "append", "to", "delete", "copy", "list", "files", 
        "define", "return", "import", "use", "plugin", "with", "uppercase", "lowercase", "trim", 
        "length", "substring", "from", "replace", "split", "by", "log", "call", "is", "not", 
        "equal", "greater", "smaller", "than", "or", "format", "date", "as", "now", "today", 
        "add", "subtract", "days", "parse", "day", "week", "that", "fails", "equals", "plus", 
        "minus", "times", "divided", "by", "modulo", "to", "the", "power", "of", "negative", 
        "absolute", "round", "floor", "ceil"
    ));

    @Override
    public List<LintIssue> check(LintContext context) {
        List<LintIssue> issues = new ArrayList<>();
        List<Tokenizer.Token> tokens = context.getTokens();
        
        if (tokens == null || tokens.isEmpty()) {
            return issues;
        }

        for (int i = 1; i < tokens.size(); i++) {
            Tokenizer.Token token = tokens.get(i);
            Tokenizer.Token prevToken = tokens.get(i - 1);
            
            // Check variable declarations
            if (("variable".equals(prevToken.value) && prevToken.lineNumber == token.lineNumber) || 
                ("set".equals(prevToken.value) && i < tokens.size() - 1 && "to".equals(tokens.get(i+1).value))) {
                
                // Check if the variable name is a reserved word
                if (RESERVED_WORDS.contains(token.value.toLowerCase())) {
                    issues.add(new LintIssue(
                        LintIssue.Severity.ERROR,
                        token.lineNumber,
                        "Variable name '" + token.value + "' is a reserved word and cannot be used as a variable name",
                        context.getSourceLine(token.lineNumber)
                    ));
                }
            }
        }
        
        return issues;
    }
}
