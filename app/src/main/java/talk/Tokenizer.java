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
        for (int i = 0; i < lines.size(); i++) {
            String line = lines.get(i).trim();
            if (line.isEmpty() || line.startsWith("#")) continue; // skip comments/empty
            System.out.println("[DEBUG] Line: '" + line + "'");
            int idx = 0;
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
        return tokens;
    }
}
