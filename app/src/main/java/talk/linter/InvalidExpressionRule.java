package talk.linter;

import talk.core.Tokenizer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Rule that checks for invalid expressions in Talk code.
 */
public class InvalidExpressionRule implements LintRule {
    // Arithmetic operators
    private static final Set<String> ARITHMETIC_OPERATORS = new HashSet<>(Arrays.asList(
        "plus", "minus", "times", "divided", "modulo", "power", "negative", "absolute", 
        "round", "floor", "ceil"
    ));
    
    // Comparison operators
    private static final Set<String> COMPARISON_OPERATORS = new HashSet<>(Arrays.asList(
        "is equal to", "is not equal to", "is greater than", "is smaller than", 
        "is before", "is after"
    ));
    
    // Logical operators
    private static final Set<String> LOGICAL_OPERATORS = new HashSet<>(Arrays.asList(
        "and", "or", "not"
    ));
    
    @Override
    public List<LintIssue> check(LintContext context) {
        List<LintIssue> issues = new ArrayList<>();
        List<Tokenizer.Token> tokens = context.getTokens();
        List<String> lines = context.getSourceLines();
        
        if (tokens == null || tokens.isEmpty() || lines == null || lines.isEmpty()) {
            return issues;
        }
        
        // Check arithmetic expressions
        checkArithmeticExpressions(context, issues, tokens);
        
        // Check logical expressions
        checkLogicalExpressions(context, issues, tokens);
        
        return issues;
    }
    
    /**
     * Check for invalid arithmetic expressions
     */
    private void checkArithmeticExpressions(LintContext context, List<LintIssue> issues, List<Tokenizer.Token> tokens) {
        for (int i = 0; i < tokens.size() - 2; i++) {
            Tokenizer.Token token = tokens.get(i);
            
            // Check for arithmetic operations in variable assignments
            if ("set".equals(token.value) && i + 3 < tokens.size() && "to".equals(tokens.get(i + 2).value)) {
                String varName = tokens.get(i + 1).value;
                // Starting from the token after 'to'
                int exprStartIdx = i + 3;
                
                // Check if the expression contains arithmetic operators
                for (int j = exprStartIdx; j < tokens.size() && tokens.get(j).lineNumber == token.lineNumber; j++) {
                    Tokenizer.Token exprToken = tokens.get(j);
                    
                    // Check for potential malformed arithmetic expressions
                    if (ARITHMETIC_OPERATORS.contains(exprToken.value)) {
                        // Basic check - must have something before and after the operator
                        if (j == exprStartIdx) {
                            issues.add(new LintIssue(
                                LintIssue.Severity.ERROR,
                                token.lineNumber,
                                "Invalid arithmetic expression: missing left operand for '" + exprToken.value + "'",
                                context.getSourceLine(token.lineNumber)
                            ));
                        } else if (j == tokens.size() - 1 || tokens.get(j + 1).lineNumber != token.lineNumber) {
                            issues.add(new LintIssue(
                                LintIssue.Severity.ERROR,
                                token.lineNumber,
                                "Invalid arithmetic expression: missing right operand for '" + exprToken.value + "'",
                                context.getSourceLine(token.lineNumber)
                            ));
                        }
                        
                        // Special check for 'divided by' to ensure it's not just 'divided'
                        if ("divided".equals(exprToken.value)) {
                            if (j + 1 >= tokens.size() || !tokens.get(j + 1).value.equals("by")) {
                                issues.add(new LintIssue(
                                    LintIssue.Severity.ERROR,
                                    token.lineNumber,
                                    "Invalid arithmetic expression: 'divided' must be followed by 'by'",
                                    context.getSourceLine(token.lineNumber)
                                ));
                            }
                        }
                        
                        // Special check for 'to the power of' to ensure complete phrase
                        if ("power".equals(exprToken.value)) {
                            if (j < 2 || !tokens.get(j - 2).value.equals("to") || !tokens.get(j - 1).value.equals("the") || 
                                j + 1 >= tokens.size() || !tokens.get(j + 1).value.equals("of")) {
                                issues.add(new LintIssue(
                                    LintIssue.Severity.ERROR,
                                    token.lineNumber,
                                    "Invalid arithmetic expression: power operation must use 'to the power of'",
                                    context.getSourceLine(token.lineNumber)
                                ));
                            }
                        }
                    }
                }
            }
        }
    }
    
    /**
     * Check for invalid logical expressions
     */
    private void checkLogicalExpressions(LintContext context, List<LintIssue> issues, List<Tokenizer.Token> tokens) {
        for (int i = 0; i < tokens.size() - 1; i++) {
            Tokenizer.Token token = tokens.get(i);
            
            // Check for logical operators in if conditions
            if ("if".equals(token.value)) {
                int thenIdx = -1;
                // Find the 'then' token
                for (int j = i + 1; j < tokens.size() && tokens.get(j).lineNumber == token.lineNumber; j++) {
                    if ("then".equals(tokens.get(j).value)) {
                        thenIdx = j;
                        break;
                    }
                }
                
                if (thenIdx == -1) {
                    // This will be caught by the MissingThenRule, so we don't report it here
                    continue;
                }
                
                // Check the condition between 'if' and 'then'
                boolean foundComparison = false;
                boolean foundLogicalOperator = false;
                
                for (int j = i + 1; j < thenIdx; j++) {
                    Tokenizer.Token condToken = tokens.get(j);
                    
                    // Check for comparison operators
                    if (j + 2 < thenIdx && 
                        "is".equals(condToken.value) && 
                        ("equal".equals(tokens.get(j + 1).value) || "not".equals(tokens.get(j + 1).value) || 
                         "greater".equals(tokens.get(j + 1).value) || "smaller".equals(tokens.get(j + 1).value))) {
                        foundComparison = true;
                    }
                    
                    // Check logical operators
                    if ("and".equals(condToken.value) || "or".equals(condToken.value)) {
                        foundLogicalOperator = true;
                        
                        // Check if logical operator has operands on both sides
                        if (j == i + 1 || j == thenIdx - 1) {
                            issues.add(new LintIssue(
                                LintIssue.Severity.ERROR,
                                token.lineNumber,
                                "Invalid logical expression: logical operator '" + condToken.value + "' must have expressions on both sides",
                                context.getSourceLine(token.lineNumber)
                            ));
                        }
                    }
                    
                    // Check not operator
                    if ("not".equals(condToken.value) && (j == thenIdx - 1 || j + 1 >= tokens.size())) {
                        issues.add(new LintIssue(
                            LintIssue.Severity.ERROR,
                            token.lineNumber,
                            "Invalid logical expression: 'not' operator must be followed by an expression",
                            context.getSourceLine(token.lineNumber)
                        ));
                    }
                }
                
                // If we have a logical operator but no comparison, it might be an issue
                if (foundLogicalOperator && !foundComparison && thenIdx - i > 3) {
                    issues.add(new LintIssue(
                        LintIssue.Severity.WARNING,
                        token.lineNumber,
                        "Possibly invalid logical expression: logical operator without clear comparison",
                        context.getSourceLine(token.lineNumber)
                    ));
                }
            }
        }
    }
}
