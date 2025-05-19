package talk;

import talk.core.JLineEnabledRepl;

/**
 * A launcher for the enhanced Talk REPL with JLine support
 * This provides arrow key navigation, history search, and tab completion.
 */
public class EnhancedReplLauncher {

    /**
     * Main entry point for the Enhanced Talk REPL
     */
    public static void main(String[] args) {
        System.out.println("Starting Talk Enhanced REPL...");
        try {
            JLineEnabledRepl repl = new JLineEnabledRepl();
            repl.start();
        } catch (Exception e) {
            System.err.println("Enhanced REPL launch failed: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
        System.exit(0);
    }
}
