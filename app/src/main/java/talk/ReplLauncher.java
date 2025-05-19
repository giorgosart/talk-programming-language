package talk;

import talk.core.MinimalTalkRepl;

/**
 * A standalone launcher for the Talk REPL
 * This provides a way to run the REPL without depending on
 * all the components that may have compilation errors.
 */
public class ReplLauncher {

    /**
     * Main entry point for the Talk REPL
     */
    public static void main(String[] args) {
        System.out.println("Starting Talk REPL in standalone mode...");
        try {
            MinimalTalkRepl repl = new MinimalTalkRepl();
            repl.start();
        } catch (Exception e) {
            System.err.println("REPL launch failed: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
        System.exit(0);
    }
}
