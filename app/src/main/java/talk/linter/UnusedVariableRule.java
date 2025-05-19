package talk.linter;

import talk.core.Tokenizer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Rule that checks for unused variables in Talk code.
 */
public class UnusedVariableRule implements LintRule {

    @Override
    public List<LintIssue> check(LintContext context) {
        List<LintIssue> issues = new ArrayList<>();
        List<Tokenizer.Token> tokens = context.getTokens();
        
        if (tokens == null || tokens.isEmpty()) {
            return issues;
        }
        
        // Map to track variable declarations (name -> line number)
        Map<String, Integer> declaredVariables = new HashMap<>();
        
        // Set to track variables that are used
        Set<String> usedVariables = new HashSet<>();
        
        // First pass: find all variable declarations
        for (int i = 0; i < tokens.size() - 1; i++) {
            Tokenizer.Token token = tokens.get(i);
            
            // Check for variable declaration patterns
            if ("variable".equals(token.value) && i + 1 < tokens.size()) {
                String variableName = tokens.get(i + 1).value;
                declaredVariables.put(variableName, token.lineNumber);
            } else if ("set".equals(token.value) && i + 2 < tokens.size() && "to".equals(tokens.get(i + 2).value)) {
                String variableName = tokens.get(i + 1).value;
                if (!declaredVariables.containsKey(variableName)) {
                    declaredVariables.put(variableName, token.lineNumber);
                }
            }
        }
        
        // Second pass: find variable usages
        for (int i = 0; i < tokens.size(); i++) {
            Tokenizer.Token token = tokens.get(i);
            
            // Skip tokens that are part of a variable declaration
            if (i > 0 && ("variable".equals(tokens.get(i - 1).value) || 
                           ("set".equals(tokens.get(i - 1).value) && 
                            i < tokens.size() - 1 && "to".equals(tokens.get(i + 1).value)))) {
                continue;
            }
            
            // Skip keywords and special tokens
            if (isReservedWord(token.value) || 
                "INDENT".equals(token.value) || "DEDENT".equals(token.value) ||
                "LIST_START".equals(token.value) || "LIST_END".equals(token.value)) {
                continue;
            }
            
            // If the token is a declared variable, mark it as used
            if (declaredVariables.containsKey(token.value)) {
                usedVariables.add(token.value);
            }
        }
        
        // Report variables that were declared but never used
        for (Map.Entry<String, Integer> entry : declaredVariables.entrySet()) {
            if (!usedVariables.contains(entry.getKey())) {
                issues.add(new LintIssue(
                    LintIssue.Severity.WARNING,
                    entry.getValue(),
                    "Unused variable: '" + entry.getKey() + "' is defined but never used",
                    context.getSourceLine(entry.getValue())
                ));
            }
        }
        
        return issues;
    }
    
    /**
     * Check if a token is a Talk language reserved word
     */
    private boolean isReservedWord(String token) {
        // Only check for reserved words that might be confused with variables
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
               token.equals("use") || token.equals("plugin") || token.equals("with");
    }
}
