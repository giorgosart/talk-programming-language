package talk.util;

public class ErrorFormatter {
    public static String format(Exception e, int lineNumber, String hint) {
        StringBuilder sb = new StringBuilder();
        sb.append("[Error]");
        if (lineNumber > 0) {
            sb.append(" (line ").append(lineNumber).append(")");
        }
        sb.append(": ").append(e.getMessage());
        if (hint != null && !hint.isEmpty()) {
            sb.append("\nHint: ").append(hint);
        }
        return sb.toString();
    }
}
