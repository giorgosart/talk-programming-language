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
            boolean handledList = false;
            // Special handling for list-style assignment: variable items equals ...
            if (trimmed.startsWith("variable ") && trimmed.contains(" equals ")) {
                int eqIdx = trimmed.indexOf(" equals ");
                String before = trimmed.substring(0, eqIdx);
                String after = trimmed.substring(eqIdx + 8).trim();
                String[] beforeParts = before.split(" ");
                if (beforeParts.length == 2) {
                    tokens.add(new Token("variable", i + 1));
                    tokens.add(new Token(beforeParts[1], i + 1));
                    tokens.add(new Token("equals", i + 1));
                    // Parse list items from 'after'
                    List<String> listItems = new ArrayList<>();
                    StringBuilder itemBuilder = new StringBuilder();
                    boolean inQuotes = false;
                    for (int j = 0; j < after.length(); j++) {
                        char c = after.charAt(j);
                        if (c == '"') {
                            inQuotes = !inQuotes;
                            continue;
                        }
                        if (!inQuotes && (c == ',' || (j + 3 <= after.length() && after.substring(j, j + 3).equals("and")))) {
                            String item = itemBuilder.toString().trim();
                            if (!item.isEmpty()) listItems.add(item);
                            itemBuilder.setLength(0);
                            if (c == ',') continue;
                            else { j += 2; continue; }
                        }
                        itemBuilder.append(c);
                    }
                    String lastItem = itemBuilder.toString().trim();
                    if (!lastItem.isEmpty()) listItems.add(lastItem);
                    if (!listItems.isEmpty()) {
                        tokens.add(new Token("LIST_START", i + 1));
                        for (String item : listItems) {
                            tokens.add(new Token(item, i + 1));
                        }
                        tokens.add(new Token("LIST_END", i + 1));
                        handledList = true;
                    }
                }
            }
            // Special handling for list-style assignment: set items to ...
            else if (trimmed.startsWith("set ") && trimmed.contains(" to ")) {
                int toIdx = trimmed.indexOf(" to ");
                String before = trimmed.substring(0, toIdx);
                String after = trimmed.substring(toIdx + 4).trim();
                String[] beforeParts = before.split(" ");
                if (beforeParts.length == 2) {
                    tokens.add(new Token("set", i + 1));
                    tokens.add(new Token(beforeParts[1], i + 1));
                    tokens.add(new Token("to", i + 1));
                    // Parse list items from 'after'
                    List<String> listItems = new ArrayList<>();
                    StringBuilder itemBuilder = new StringBuilder();
                    boolean inQuotes = false;
                    for (int j = 0; j < after.length(); j++) {
                        char c = after.charAt(j);
                        if (c == '"') {
                            inQuotes = !inQuotes;
                            continue;
                        }
                        if (!inQuotes && (c == ',' || (j + 3 <= after.length() && after.substring(j, j + 3).equals("and")))) {
                            String item = itemBuilder.toString().trim();
                            if (!item.isEmpty()) listItems.add(item);
                            itemBuilder.setLength(0);
                            if (c == ',') continue;
                            else { j += 2; continue; }
                        }
                        itemBuilder.append(c);
                    }
                    String lastItem = itemBuilder.toString().trim();
                    if (!lastItem.isEmpty()) listItems.add(lastItem);
                    if (!listItems.isEmpty()) {
                        tokens.add(new Token("LIST_START", i + 1));
                        for (String item : listItems) {
                            tokens.add(new Token(item, i + 1));
                        }
                        tokens.add(new Token("LIST_END", i + 1));
                        handledList = true;
                    }
                }
            }
            // Special handling for ask ... and store in ...
            else if (trimmed.startsWith("ask ") && trimmed.contains(" and store in ")) {
                int askIdx = trimmed.indexOf("ask ");
                int andIdx = trimmed.indexOf(" and store in ");
                String promptPart = trimmed.substring(askIdx + 4, andIdx).trim();
                String varPart = trimmed.substring(andIdx + 14).trim();
                if (promptPart.startsWith("\"") && promptPart.endsWith("\"")) {
                    promptPart = promptPart.substring(1, promptPart.length() - 1);
                }
                tokens.add(new Token("ask", i + 1));
                tokens.add(new Token(promptPart, i + 1));
                tokens.add(new Token("and", i + 1));
                tokens.add(new Token("store", i + 1));
                tokens.add(new Token("in", i + 1));
                tokens.add(new Token(varPart, i + 1));
                continue;
            }
            // Special handling for file reading: read file <file> into <variable>
            else if (trimmed.startsWith("read file ") && trimmed.contains(" into ")) {
                int fileIdx = "read file ".length();
                int intoIdx = trimmed.indexOf(" into ");
                String filePart = trimmed.substring(fileIdx, intoIdx).trim();
                String varPart = trimmed.substring(intoIdx + 6).trim();
                tokens.add(new Token("read", i + 1));
                tokens.add(new Token("file", i + 1));
                tokens.add(new Token(filePart, i + 1));
                tokens.add(new Token("into", i + 1));
                tokens.add(new Token(varPart, i + 1));
                continue;
            }
            // Special handling for file appending: append <text> to <file>
            else if (trimmed.startsWith("append ") && trimmed.contains(" to ")) {
                int appendIdx = "append ".length();
                int toIdx = trimmed.indexOf(" to ");
                String textPart = trimmed.substring(appendIdx, toIdx).trim();
                String filePart = trimmed.substring(toIdx + 4).trim();
                // Handle quoted text (single or double quotes)
                if ((textPart.startsWith("\"") && textPart.endsWith("\"")) || (textPart.startsWith("'") && textPart.endsWith("'"))) {
                    textPart = textPart.substring(1, textPart.length() - 1);
                }
                tokens.add(new Token("append", i + 1));
                tokens.add(new Token(textPart, i + 1));
                tokens.add(new Token("to", i + 1));
                tokens.add(new Token(filePart, i + 1));
                continue;
            }
            // Special handling for file deletion: delete file <file>
            else if (trimmed.startsWith("delete file ")) {
                String filePart = trimmed.substring("delete file ".length()).trim();
                tokens.add(new Token("delete", i + 1));
                tokens.add(new Token("file", i + 1));
                tokens.add(new Token(filePart, i + 1));
                continue;
            }
            // Special handling for directory listing: list files in <directory> into <variable>
            else if (trimmed.startsWith("list files in ") && trimmed.contains(" into ")) {
                int dirIdx = "list files in ".length();
                int intoIdx = trimmed.indexOf(" into ");
                String dirPart = trimmed.substring(dirIdx, intoIdx).trim();
                String varPart = trimmed.substring(intoIdx + 6).trim();
                tokens.add(new Token("list", i + 1));
                tokens.add(new Token("files", i + 1));
                tokens.add(new Token("in", i + 1));
                tokens.add(new Token(dirPart, i + 1));
                tokens.add(new Token("into", i + 1));
                tokens.add(new Token(varPart, i + 1));
                continue;
            }
            // Special handling for logging: log <message>
            else if (trimmed.startsWith("log ")) {
                String message = trimmed.substring(4).trim();
                // Handle quoted message (single or double quotes)
                if ((message.startsWith("\"") && message.endsWith("\"")) || (message.startsWith("'") && message.endsWith("'"))) {
                    message = message.substring(1, message.length() - 1);
                }
                tokens.add(new Token("log", i + 1));
                tokens.add(new Token(message, i + 1));
                continue;
            }
            if (handledList) continue;
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
                    // Skip whitespace after quoted string
                    while (idx < line.length() && Character.isWhitespace(line.charAt(idx))) idx++;
                } else {
                    int end = idx;
                    while (end < line.length() && !Character.isWhitespace(line.charAt(end))) end++;
                    String part2 = line.substring(idx, end);
                    String prevToken = tokens.isEmpty() ? "" : tokens.get(tokens.size() - 1).value;
                    String tokenValue;
                    if (part2.equals("and") || part2.equals("or") || part2.equals("not")) {
                        // Only treat as logical operator if not after 'ask' or 'write', otherwise keep as lowercase
                        if (!(prevToken.equals("ask") || prevToken.equals("write"))) {
                            tokenValue = part2.toUpperCase();
                        } else {
                            tokenValue = part2;
                        }
                    } else if (part2.equals("define")) {
                        tokenValue = "DEFINE";
                    } else if (part2.equals("call")) {
                        tokenValue = "CALL";
                    } else {
                        tokenValue = part2;
                    }
                    System.out.println("[DEBUG] Raw part: '" + part2 + "'");
                    tokens.add(new Token(tokenValue, i + 1));
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
