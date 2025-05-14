package talk;

/**
 * Handles indentation tracking and INDENT/DEDENT logic for the Tokenizer.
 */
public class IndentationManager {
    private final java.util.List<Integer> indentStack = new java.util.ArrayList<>();

    public IndentationManager() {
        indentStack.add(0);
    }

    public int getCurrentIndent() {
        return indentStack.get(indentStack.size() - 1);
    }

    /**
     * Handles indentation change and returns:
     *   -1 for DEDENT, 1 for INDENT, 0 for no change.
     * Updates the stack accordingly.
     */
    public int handleIndent(int newIndent) {
        int prevIndent = getCurrentIndent();
        if (newIndent > prevIndent) {
            indentStack.add(newIndent);
            return 1;
        } else if (newIndent < prevIndent) {
            while (newIndent < getCurrentIndent() && indentStack.size() > 1) {
                indentStack.remove(indentStack.size() - 1);
            }
            return -1;
        }
        return 0;
    }

    public boolean hasUnclosedIndents() {
        return indentStack.size() > 1;
    }

    public void closeIndent() {
        if (indentStack.size() > 1) {
            indentStack.remove(indentStack.size() - 1);
        }
    }
}
