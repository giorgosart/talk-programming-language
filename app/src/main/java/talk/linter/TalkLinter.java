package talk.linter;

import talk.core.InstructionFactory;
import talk.core.Tokenizer;

import java.util.*;
import java.nio.file.*;
import java.io.IOException;

/**
 * A linter for Talk programming language scripts.
 * Analyzes code without executing it and reports errors, warnings, and suggestions.
 */
public class TalkLinter {
    
    private final InstructionFactory instructionFactory;
    private final List<LintRule> rules;
    private final List<LintIssue> issues;
    
    /**
     * Creates a new linter with default rules
     */
    public TalkLinter() {
        this.instructionFactory = new InstructionFactory();
        this.rules = new ArrayList<>();
        this.issues = new ArrayList<>();
        
        // Register default rules
        registerDefaultRules();
    }
    
    /**
     * Register all default linting rules
     */
    private void registerDefaultRules() {
        // Core rules for syntax checking
        rules.add(new UnknownInstructionRule());
        rules.add(new ImproperNestingRule());
        rules.add(new MissingThenRule());
        rules.add(new UnclosedBlockRule());
        rules.add(new InvalidTokenRule());
        rules.add(new ReservedWordRule());
        rules.add(new UnusedVariableRule());
        rules.add(new InvalidExpressionRule());
    }
    
    /**
     * Add a custom rule to the linter
     * @param rule The rule to add
     */
    public void addRule(LintRule rule) {
        rules.add(rule);
    }
    
    /**
     * Lint a Talk script file
     * @param filePath Path to the script file
     * @return A list of lint issues found
     * @throws IOException If the file cannot be read
     */
    public List<LintIssue> lint(String filePath) throws IOException {
        issues.clear();
        
        // Read the file content
        List<String> lines = Files.readAllLines(Paths.get(filePath));
        
        // First pass: tokenize to check for syntax errors
        Tokenizer tokenizer = new Tokenizer();
        List<Tokenizer.Token> tokens;
        
        try {
            tokens = tokenizer.tokenize(lines);
        } catch (Exception e) {
            // Handle tokenizer errors by adding them as lint issues
            issues.add(new LintIssue(
                LintIssue.Severity.ERROR,
                extractLineNumber(e.getMessage()),
                "Syntax error: " + e.getMessage(),
                extractLineFromMessage(e.getMessage(), lines)
            ));
            return issues;
        }
        
        // Create context object with all the information rules might need
        LintContext context = new LintContext(lines, tokens, instructionFactory);
        
        // Apply each rule
        for (LintRule rule : rules) {
            List<LintIssue> ruleIssues = rule.check(context);
            if (ruleIssues != null) {
                issues.addAll(ruleIssues);
            }
        }
        
        // Sort issues by line number
        Collections.sort(issues, Comparator.comparingInt(LintIssue::getLine));
        
        return issues;
    }
    
    /**
     * Print lint issues to the console
     */
    public void printIssues() {
        int errorCount = 0;
        int warningCount = 0;
        
        for (LintIssue issue : issues) {
            System.out.println("Line " + issue.getLine() + ": " + issue.getMessage());
            System.out.println("  " + issue.getSourceLine());
            
            if (issue.getSeverity() == LintIssue.Severity.ERROR) {
                errorCount++;
            } else if (issue.getSeverity() == LintIssue.Severity.WARNING) {
                warningCount++;
            }
        }
        
        System.out.println("\nLinting completed: " + errorCount + " error(s), " + warningCount + " warning(s)");
    }
    
    /**
     * Get the number of errors found
     * @return The count of error issues
     */
    public int getErrorCount() {
        int count = 0;
        for (LintIssue issue : issues) {
            if (issue.getSeverity() == LintIssue.Severity.ERROR) {
                count++;
            }
        }
        return count;
    }
    
    /**
     * Get the number of warnings found
     * @return The count of warning issues
     */
    public int getWarningCount() {
        int count = 0;
        for (LintIssue issue : issues) {
            if (issue.getSeverity() == LintIssue.Severity.WARNING) {
                count++;
            }
        }
        return count;
    }
    
    /**
     * Extract line number from tokenizer error message
     */
    private int extractLineNumber(String message) {
        // Example: "Error at line 3: ..."
        try {
            if (message.contains("line")) {
                String[] parts = message.split("line");
                if (parts.length > 1) {
                    String lineNumStr = parts[1].trim().split("\\D+")[0];
                    return Integer.parseInt(lineNumStr);
                }
            }
        } catch (Exception e) {
            // Fall back to line 1 if we can't extract
        }
        return 1;
    }
    
    /**
     * Extract the relevant line of code from an error message
     */
    private String extractLineFromMessage(String message, List<String> lines) {
        int lineNumber = extractLineNumber(message);
        if (lineNumber > 0 && lineNumber <= lines.size()) {
            return lines.get(lineNumber - 1);
        }
        return "";
    }
}
