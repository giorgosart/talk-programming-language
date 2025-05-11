package talk;

import java.util.ArrayList;
import java.util.List;

public class Tokenizer {
    public static class Token {
        public final String value;
        public final int lineNumber;
        public Token(String value, int lineNumber) {
            this.value = value;
            this.lineNumber = lineNumber;
        }
    }

    public List<Token> tokenize(List<String> lines) {
        List<Token> tokens = new ArrayList<>();
        int prevIndent = 0;
        List<Integer> indentStack = new ArrayList<>();
        indentStack.add(0);
        for (int i = 0; i < lines.size(); i++) {
            String rawLine = lines.get(i);
            String line = rawLine.replaceAll("\t", "    "); // treat tabs as 4 spaces
            int indent = 0;
            while (indent < line.length() && line.charAt(indent) == ' ') indent++;
            if (line.trim().isEmpty() || line.trim().startsWith("#")) continue; // skip comments/empty
            if (indent > prevIndent) {
                tokens.add(new Token("INDENT", i + 1));
                indentStack.add(indent);
            } else if (indent < prevIndent) {
                while (indent < prevIndent && indentStack.size() > 1) {
                    tokens.add(new Token("DEDENT", i + 1));
                    indentStack.remove(indentStack.size() - 1);
                    prevIndent = indentStack.get(indentStack.size() - 1);
                }
            }
            prevIndent = indent;
            String trimmed = line.trim();
            System.out.println("[DEBUG] Line: '" + trimmed + "'");
            int idx = indent;
            while (idx < line.length()) {
                if (Character.isWhitespace(line.charAt(idx))) {
                    idx++;
                    continue;
                }
                if (line.charAt(idx) == '"') {
                    int end = line.indexOf('"', idx + 1);
                    if (end == -1) end = line.length();
                    String quoted = line.substring(idx + 1, end);
                    System.out.println("[DEBUG] Quoted token: '" + quoted + "'");
                    tokens.add(new Token(quoted, i + 1));
                    idx = end + 1;
                } else {
                    int end = idx;
                    while (end < line.length() && !Character.isWhitespace(line.charAt(end))) end++;
                    String part = line.substring(idx, end);
                    System.out.println("[DEBUG] Raw part: '" + part + "'");
                    tokens.add(new Token(part, i + 1));
                    idx = end;
                }
            }
        }
        // Close any remaining indents
        while (indentStack.size() > 1) {
            tokens.add(new Token("DEDENT", lines.size()));
            indentStack.remove(indentStack.size() - 1);
        }
        return tokens;
    }
}
