package talk.linter;

import talk.core.Tokenizer;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Rule that checks for invalid token syntax in Talk code.
 */
public class InvalidTokenRule implements LintRule {
    // Pattern for valid variable names
    private static final Pattern VALID_VARIABLE_NAME = Pattern.compile("^[a-zA-Z][a-zA-Z0-9_]*$");
    
    // Pattern for valid string literals (with proper quoting)
    private static final Pattern VALID_STRING_LITERAL = Pattern.compile("^\"[^\"]*\"$|^'[^']*'$");
    
    // Pattern for valid numeric literals
    private static final Pattern VALID_NUMBER_LITERAL = Pattern.compile("^-?\\d+(\\.\\d+)?$");

    @Override
    public List<LintIssue> check(LintContext context) {
        List<LintIssue> issues = new ArrayList<>();
        List<Tokenizer.Token> tokens = context.getTokens();
        
        if (tokens == null || tokens.isEmpty()) {
            return issues;
        }

        for (int i = 0; i < tokens.size(); i++) {
            Tokenizer.Token token = tokens.get(i);
            
            // Skip special tokens
            if ("INDENT".equals(token.value) || "DEDENT".equals(token.value) || 
                "LIST_START".equals(token.value) || "LIST_END".equals(token.value)) {
                continue;
            }
            
            // Skip keywords
            if (isKeyword(token.value)) {
                continue;
            }
            
            // In variable assignment context, check variable name
            if (i > 0 && ("variable".equals(tokens.get(i-1).value) || 
                           ("set".equals(tokens.get(i-1).value) && i < tokens.size() - 1 && "to".equals(tokens.get(i+1).value)))) {
                if (!VALID_VARIABLE_NAME.matcher(token.value).matches()) {
                    issues.add(new LintIssue(
                        LintIssue.Severity.ERROR,
                        token.lineNumber,
                        "Invalid variable name: '" + token.value + "'. Variable names must start with a letter and contain only letters, numbers, and underscores.",
                        context.getSourceLine(token.lineNumber)
                    ));
                }
                continue;
            }
            
            // Check if it's a valid literal (string or number) or variable reference
            if (!VALID_VARIABLE_NAME.matcher(token.value).matches() && 
                !VALID_STRING_LITERAL.matcher(token.value).matches() && 
                !VALID_NUMBER_LITERAL.matcher(token.value).matches() &&
                !token.value.contains(" ")) {  // Multi-word literals are handled by the tokenizer
                
                // Check for unbalanced quotes that might indicate a tokenization issue
                if (token.value.contains("\"") || token.value.contains("'")) {
                    issues.add(new LintIssue(
                        LintIssue.Severity.ERROR,
                        token.lineNumber,
                        "Invalid string literal: '" + token.value + "'. String literals must be enclosed in matching quotes.",
                        context.getSourceLine(token.lineNumber)
                    ));
                }
            }
        }
        
        return issues;
    }
    
    /**
     * Check if a token is a Talk language keyword
     */
    private boolean isKeyword(String token) {
        return token.equals("write") || token.equals("variable") || token.equals("set") || 
               token.equals("to") || token.equals("ask") || token.equals("and") || 
               token.equals("store") || token.equals("in") || token.equals("if") || 
               token.equals("then") || token.equals("otherwise") || token.equals("repeat") || 
               token.equals("until") || token.equals("attempt") || token.equals("test") || 
               token.equals("before") || token.equals("after") || token.equals("each") || 
               token.equals("expect") || token.equals("result") || token.equals("of") || 
               token.equals("be") || token.equals("read") || token.equals("file") || 
               token.equals("into") || token.equals("append") || token.equals("delete") || 
               token.equals("copy") || token.equals("list") || token.equals("files") || 
               token.equals("define") || token.equals("return") || token.equals("import") || 
               token.equals("use") || token.equals("plugin") || token.equals("with") || 
               token.equals("uppercase") || token.equals("lowercase") || token.equals("trim") || 
               token.equals("length") || token.equals("substring") || token.equals("from") || 
               token.equals("replace") || token.equals("split") || token.equals("by") || 
               token.equals("log") || token.equals("call") || token.equals("is") || 
               token.equals("not") || token.equals("equal") || token.equals("greater") || 
               token.equals("smaller") || token.equals("than") || token.equals("or") || 
               token.equals("format") || token.equals("date") || token.equals("as") || 
               token.equals("now") || token.equals("today") || token.equals("add") || 
               token.equals("subtract") || token.equals("days") || token.equals("parse") || 
               token.equals("day") || token.equals("week") || token.equals("that") || 
               token.equals("fails") || token.equals("equals") || token.equals("plus") ||
               token.equals("minus") || token.equals("times") || token.equals("divided") ||
               token.equals("modulo") || token.equals("power") || token.equals("negative") ||
               token.equals("absolute") || token.equals("round") || token.equals("floor") ||
               token.equals("ceil");
    }
}
