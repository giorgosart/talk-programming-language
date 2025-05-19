package talk.core;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * A minimal implementation of the Talk REPL (Read-Eval-Print Loop)
 * that can work even when other components are not fully implemented.
 * This is a standalone version that can be used for demonstration purposes.
 */
public class TalkRepl {
    private static final String PROMPT = "> ";
    private static final String WELCOME_MESSAGE = "Talk REPL v0.1 (Minimal Version)\n" +
            "Type 'exit' or 'quit' to end the session.\n" +
            "Type 'help' to see available commands and examples.";
    
    private final List<String> history;
    
    /**
     * Creates a new minimal REPL instance
     */
    public TalkRepl() {
        this.history = new ArrayList<>();
    }
    
    /**
     * Start the REPL session
     */
    public void start() {
        System.out.println(WELCOME_MESSAGE);
        
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
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
                    System.out.println("History cleared.");
                    continue;
                }
                
                // Add to history
                history.add(trimmedInput);
                
                // Evaluate and Print (minimal implementation)
                evaluateMinimal(trimmedInput);
                
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
        
        System.out.println("\nThis is a minimal implementation with limited functionality.");
        System.out.println("For full functionality, please use the complete Talk implementation.");
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
     * A minimal evaluation implementation
     */
    private void evaluateMinimal(String line) {
        // Echo the line for minimal implementation
        if (line.startsWith("write ")) {
            String content = line.substring(6).trim();
            if (content.startsWith("\"") && content.endsWith("\"")) {
                content = content.substring(1, content.length() - 1);
            }
            System.out.println(content);
        } else if (line.startsWith("variable ")) {
            System.out.println("Variable definition processed (limited functionality in minimal mode)");
        } else if (line.contains("+")) {
            // Simple addition for demo purposes
            String[] parts = line.split("\\+");
            try {
                if (parts.length == 2) {
                    // Try numeric addition
                    double a = Double.parseDouble(parts[0].trim());
                    double b = Double.parseDouble(parts[1].trim());
                    System.out.println(a + b);
                } else {
                    System.out.println("Expression processed (limited functionality in minimal mode)");
                }
            } catch (NumberFormatException e) {
                // Try string concatenation
                boolean stringMode = false;
                for (String part : parts) {
                    if (part.trim().startsWith("\"") || part.trim().endsWith("\"")) {
                        stringMode = true;
                        break;
                    }
                }
                
                if (stringMode) {
                    StringBuilder result = new StringBuilder();
                    for (String part : parts) {
                        String trimmed = part.trim();
                        if (trimmed.startsWith("\"")) {
                            trimmed = trimmed.substring(1);
                        }
                        if (trimmed.endsWith("\"")) {
                            trimmed = trimmed.substring(0, trimmed.length() - 1);
                        }
                        result.append(trimmed);
                    }
                    System.out.println("\"" + result + "\"");
                } else {
                    System.out.println("Expression processed (limited functionality in minimal mode)");
                }
            }
        } else {
            System.out.println("Command processed (limited functionality in minimal mode)");
        }
    }
}
