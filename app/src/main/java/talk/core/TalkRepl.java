package talk.core;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import talk.Parser;
import talk.exception.FunctionReturn;
import talk.expression.ExpressionResolver;
import talk.runtime.InstructionExecutor;

/**
 * An interactive REPL (Read-Eval-Print Loop) for the Talk programming language.
 * Allows users to enter one instruction at a time and see the results immediately.
 * Supports simple command history and provides error feedback.
 */
public class TalkRepl {
    private static final String PROMPT = "> ";
    private static final String WELCOME_MESSAGE = "Talk REPL v0.1\n" +
            "Type 'exit' or 'quit' to end the session.\n" +
            "Type 'help' to see available commands and examples.";
    
    private RuntimeContext context;
    private InstructionExecutor executor;
    private ExpressionResolver resolver;
    private BufferedReader reader;
    private List<String> history;
    private int historyIndex = -1;
    
    // For testing purposes
    private interface ExpressionEvaluator {
        Object evaluate(String expression) throws Exception;
    }
    
    private interface InstructionExecutable {
        Object execute(Instruction instruction) throws Exception;
    }
    
    private ExpressionEvaluator testExprResolver = null;
    private InstructionExecutable testInstrExecutor = null;
    
    /**
     * Creates a new REPL instance
     */
    public TalkRepl() {
        this.context = new RuntimeContext();
        this.executor = new InstructionExecutor(context);
        this.resolver = new ExpressionResolver(context);
        this.reader = new BufferedReader(new InputStreamReader(System.in));
        this.history = new ArrayList<>();
    }
    
    /**
     * Start the REPL session
     */
    public void start() {
        System.out.println(WELCOME_MESSAGE);
        
        String input;
        while (true) {
            try {
                // Read
                System.out.print(PROMPT);
                input = reader.readLine();
                
                if (input == null) {
                    System.out.println("\nGoodbye!");
                    break;
                }
                
                String trimmedInput = input.trim();
                
                // Handle special commands
                if (trimmedInput.isEmpty()) {
                    continue;
                } else if (trimmedInput.equalsIgnoreCase("exit") || trimmedInput.equalsIgnoreCase("quit")) {
                    System.out.println("Goodbye!");
                    break;
                } else if (trimmedInput.equalsIgnoreCase("help")) {
                    showHelp();
                    continue;
                } else if (trimmedInput.equalsIgnoreCase("history")) {
                    showHistory();
                    continue;
                } else if (trimmedInput.equalsIgnoreCase("clear")) {
                    history.clear();
                    historyIndex = -1;
                    System.out.println("History cleared.");
                    continue;
                }
                
                // Add to history
                history.add(trimmedInput);
                historyIndex = history.size();
                
                // Evaluate and Print
                evaluateAndPrint(trimmedInput);
            } catch (IOException e) {
                System.err.println("Error reading input: " + e.getMessage());
            }
        }
    }
    
    /**
     * Show help information for the REPL
     */
    private void showHelp() {
        System.out.println("\nTalk REPL Help:");
        System.out.println("---------------");
        System.out.println("  exit, quit   - Exit the REPL session");
        System.out.println("  help         - Show this help information");
        System.out.println("  history      - Show command history");
        System.out.println("  clear        - Clear command history");
        
        System.out.println("\nBasic Examples:");
        System.out.println("  variable name = \"John\"       - Define a new variable");
        System.out.println("  write \"Hello, \" + name       - Print a message");
        System.out.println("  set x = 10                   - Set a variable value");
        System.out.println("  x + 5                        - Evaluate an expression");
        
        System.out.println("\nControl Structures:");
        System.out.println("  if x > 5 then write \"Greater\" else write \"Smaller\"");
        System.out.println("  repeat 3 times with i write \"Count: \" + i");
        
        System.out.println("\nFunctions:");
        System.out.println("  define greet with name as write \"Hello, \" + name");
        System.out.println("  greet with \"World\"");
        
        System.out.println("\nFile Operations:");
        System.out.println("  write \"Content\" to \"file.txt\"");
        System.out.println("  read file \"file.txt\"");
        
        System.out.println("\nTip: Enter expressions directly without 'write' to see their values");
        System.out.println("For more examples, see: docs/repl_examples.md");
        System.out.println();
    }
    
    /**
     * Show command history
     */
    private void showHistory() {
        if (history.isEmpty()) {
            System.out.println("History is empty.");
            return;
        }
        
        System.out.println("\nCommand History:");
        for (int i = 0; i < history.size(); i++) {
            System.out.printf("%3d: %s%n", i + 1, history.get(i));
        }
        System.out.println();
    }
    
    /**
     * Evaluate a single line of Talk code and print the result
     * 
     * @param line The line to evaluate
     */
    private void evaluateAndPrint(String line) {
        try {
            // Create a single-line tokenizer and parser
            List<String> lines = List.of(line);
            Tokenizer tokenizer = new Tokenizer();
            List<Tokenizer.Token> tokens = tokenizer.tokenize(lines);
            
            // Extract value for expressions (non-instructions)
            if (isExpression(tokens)) {
                try {
                    Object result;
                    if (testExprResolver != null) {
                        result = testExprResolver.evaluate(line);
                    } else {
                        result = resolver.resolve(line);
                    }
                    printResult(result);
                } catch (Exception e) {
                    printError("Expression evaluation error", e);
                }
                return;
            }
            
            // Regular instruction parsing
            try {
                Parser parser = new Parser(tokens);
                List<Instruction> instructions = parser.parse();
                
                // Execute each instruction
                for (Instruction instruction : instructions) {
                    try {
                        // Special handling for return to capture value
                        Object result;
                        if (testInstrExecutor != null) {
                            result = testInstrExecutor.execute(instruction);
                        } else {
                            result = executor.executeWithReturn(instruction);
                        }
                        
                        if (result != null) {
                            printResult(result);
                        }
                    } catch (FunctionReturn fr) {
                        printResult(fr.getValue());
                    } catch (Exception e) {
                        printError("Execution error", e);
                        break; // Stop after first execution error
                    }
                }
            } catch (Exception e) {
                printError("Parsing error", e);
            }
        } catch (Exception e) {
            printError("Tokenization error", e);
        }
    }
    
    /**
     * Print a more descriptive error message with contextual information
     */
    private void printError(String errorType, Exception e) {
        System.err.println(errorType + ": " + e.getMessage());
        
        // Provide more context for specific error types
        if (e instanceof talk.exception.TalkSyntaxException) {
            System.err.println("Syntax error. Check your syntax and try again.");
            System.err.println("Tip: Make sure all keywords are spelled correctly and expressions are properly formed.");
        } else if (e instanceof talk.exception.TalkValueException) {
            System.err.println("Value error. Check that your variables are defined and have the correct type.");
            System.err.println("Tip: You can define a variable with 'variable name = value' before using it.");
        } else if (e instanceof talk.exception.TalkSemanticException) {
            System.err.println("Semantic error. Check the logic in your code.");
            System.err.println("Tip: Verify that operations are performed on compatible types.");
        } else if (e instanceof talk.exception.FunctionReturn) {
            // This is not an error - handle appropriately
            printResult(((talk.exception.FunctionReturn) e).getValue());
            return;
        } else if (e instanceof IOException) {
            System.err.println("I/O error. There was a problem with file operations.");
            System.err.println("Tip: Check file paths and permissions.");
        } else {
            System.err.println("Tip: Try typing 'help' to see examples of valid Talk commands.");
        }
    }
    
    /**
     * Check if tokens represent a simple expression rather than an instruction
     */
    private boolean isExpression(List<Tokenizer.Token> tokens) {
        // Simple heuristic: if the first token is not a known keyword,
        // treat it as an expression to be evaluated
        if (tokens.isEmpty()) {
            return false;
        }
        
        String firstToken = tokens.get(0).value.toLowerCase();
        return !isInstructionKeyword(firstToken);
    }
    
    /**
     * Check if a token is a known instruction keyword
     */
    private boolean isInstructionKeyword(String token) {
        return token.equals("write") || token.equals("variable") || token.equals("set") ||
               token.equals("ask") || token.equals("if") || token.equals("repeat") || 
               token.equals("attempt") || token.equals("read") || token.equals("append") || 
               token.equals("delete") || token.equals("copy") || token.equals("list") || 
               token.equals("define") || token.equals("return") || token.equals("import") ||
               token.equals("use") || token.equals("log");
    }
    
    /**
     * Format and print a result value
     */
    private void printResult(Object result) {
        if (result == null) {
            return;
        }
        
        if (result instanceof String) {
            System.out.println("\"" + result + "\"");
        } else if (result instanceof List) {
            System.out.println(formatList((List<?>) result));
        } else {
            System.out.println(result.toString());
        }
    }
    
    /**
     * Format a list value for display
     */
    private String formatList(List<?> list) {
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < list.size(); i++) {
            Object item = list.get(i);
            if (item instanceof String) {
                sb.append("\"").append(item).append("\"");
            } else {
                sb.append(item);
            }
            if (i < list.size() - 1) {
                sb.append(", ");
            }
        }
        sb.append("]");
        return sb.toString();
    }
    
    /**
     * Set a custom reader for testing
     */
    void setReaderForTesting(BufferedReader reader) {
        this.reader = reader;
    }
    
    /**
     * Set a custom expression resolver for testing
     */
    void setResolverForTesting(ExpressionEvaluator resolver) {
        this.testExprResolver = resolver;
    }
    
    /**
     * Set a custom instruction executor for testing
     */
    void setExecutorForTesting(InstructionExecutable executor) {
        this.testInstrExecutor = executor;
    }
    
    /**
     * Expose evaluateAndPrint for testing
     */
    void evaluateAndPrintForTesting(String line) {
        evaluateAndPrint(line);
    }
}
