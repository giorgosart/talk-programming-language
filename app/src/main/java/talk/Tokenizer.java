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

    // Handler for list-style variable assignment
    private void handleListVariableAssignment(String trimmed, int lineNumber, List<Token> tokens) {
        int eqIdx = trimmed.indexOf(" equals ");
        String before = trimmed.substring(0, eqIdx);
        String after = trimmed.substring(eqIdx + 8).trim();
        String[] beforeParts = before.split(" ");
        if (beforeParts.length == 2) {
            tokens.add(new Token("variable", lineNumber));
            tokens.add(new Token(beforeParts[1], lineNumber));
            tokens.add(new Token("equals", lineNumber));
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
                tokens.add(new Token("LIST_START", lineNumber));
                for (String item : listItems) {
                    tokens.add(new Token(item, lineNumber));
                }
                tokens.add(new Token("LIST_END", lineNumber));
            }
        }
    }

    // Handler for list-style assignment via set
    private void handleListSetAssignment(String trimmed, int lineNumber, List<Token> tokens) {
        int toIdx = trimmed.indexOf(" to ");
        String before = trimmed.substring(0, toIdx);
        String after = trimmed.substring(toIdx + 4).trim();
        String[] beforeParts = before.split(" ");
        if (beforeParts.length == 2) {
            tokens.add(new Token("set", lineNumber));
            tokens.add(new Token(beforeParts[1], lineNumber));
            tokens.add(new Token("to", lineNumber));
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
                tokens.add(new Token("LIST_START", lineNumber));
                for (String item : listItems) {
                    tokens.add(new Token(item, lineNumber));
                }
                tokens.add(new Token("LIST_END", lineNumber));
            }
        }
    }

    // Handler for ask ... and store in ...
    private void handleAskAndStore(String trimmed, int lineNumber, List<Token> tokens) {
        int askIdx = trimmed.indexOf("ask ");
        int andIdx = trimmed.indexOf(" and store in ");
        String promptPart = trimmed.substring(askIdx + 4, andIdx).trim();
        String varPart = trimmed.substring(andIdx + 14).trim();
        if (promptPart.startsWith("\"") && promptPart.endsWith("\"")) {
            promptPart = promptPart.substring(1, promptPart.length() - 1);
        }
        tokens.add(new Token("ask", lineNumber));
        tokens.add(new Token(promptPart, lineNumber));
        tokens.add(new Token("and", lineNumber));
        tokens.add(new Token("store", lineNumber));
        tokens.add(new Token("in", lineNumber));
        tokens.add(new Token(varPart, lineNumber));
    }

    // Handler for file reading
    private void handleReadFileInto(String trimmed, int lineNumber, List<Token> tokens) {
        int fileIdx = "read file ".length();
        int intoIdx = trimmed.indexOf(" into ");
        String filePart = trimmed.substring(fileIdx, intoIdx).trim();
        String varPart = trimmed.substring(intoIdx + 6).trim();
        tokens.add(new Token("read", lineNumber));
        tokens.add(new Token("file", lineNumber));
        tokens.add(new Token(filePart, lineNumber));
        tokens.add(new Token("into", lineNumber));
        tokens.add(new Token(varPart, lineNumber));
    }

    // Handler for file appending
    private void handleAppendToFile(String trimmed, int lineNumber, List<Token> tokens) {
        int appendIdx = "append ".length();
        int toIdx = trimmed.indexOf(" to ");
        String textPart = trimmed.substring(appendIdx, toIdx).trim();
        String filePart = trimmed.substring(toIdx + 4).trim();
        if ((textPart.startsWith("\"") && textPart.endsWith("\"")) || (textPart.startsWith("'") && textPart.endsWith("'"))) {
            textPart = textPart.substring(1, textPart.length() - 1);
        }
        tokens.add(new Token("append", lineNumber));
        tokens.add(new Token(textPart, lineNumber));
        tokens.add(new Token("to", lineNumber));
        tokens.add(new Token(filePart, lineNumber));
    }

    // Handler for file deletion
    private void handleDeleteFile(String trimmed, int lineNumber, List<Token> tokens) {
        String filePart = trimmed.substring("delete file ".length()).trim();
        tokens.add(new Token("delete", lineNumber));
        tokens.add(new Token("file", lineNumber));
        tokens.add(new Token(filePart, lineNumber));
    }

    // Handler for file copying
    private void handleCopyFile(String trimmed, int lineNumber, List<Token> tokens) {
        int srcIdx = "copy file ".length();
        int toIdx = trimmed.indexOf(" to ");
        String srcPart = trimmed.substring(srcIdx, toIdx).trim();
        String destPart = trimmed.substring(toIdx + 4).trim();
        tokens.add(new Token("copy", lineNumber));
        tokens.add(new Token("file", lineNumber));
        tokens.add(new Token(srcPart, lineNumber));
        tokens.add(new Token("to", lineNumber));
        tokens.add(new Token(destPart, lineNumber));
    }

    // Handler for directory listing
    private void handleListFilesInDir(String trimmed, int lineNumber, List<Token> tokens) {
        int dirIdx = "list files in ".length();
        int intoIdx = trimmed.indexOf(" into ");
        String dirPart = trimmed.substring(dirIdx, intoIdx).trim();
        String varPart = trimmed.substring(intoIdx + 6).trim();
        tokens.add(new Token("list", lineNumber));
        tokens.add(new Token("files", lineNumber));
        tokens.add(new Token("in", lineNumber));
        tokens.add(new Token(dirPart, lineNumber));
        tokens.add(new Token("into", lineNumber));
        tokens.add(new Token(varPart, lineNumber));
    }

    // Handler for logging
    private void handleLog(String trimmed, int lineNumber, List<Token> tokens) {
        String message = trimmed.substring(4).trim();
        if ((message.startsWith("\"") && message.endsWith("\"")) || (message.startsWith("'") && message.endsWith("'"))) {
            message = message.substring(1, message.length() - 1);
        }
        tokens.add(new Token("log", lineNumber));
        tokens.add(new Token(message, lineNumber));
    }

    // Handler for parameterized function definition
    private void handleDefine(String trimmed, int lineNumber, List<Token> tokens) {
        String[] parts = trimmed.split("\\s+");
        tokens.add(new Token("DEFINE", lineNumber));
        if (parts.length > 1) {
            for (int j = 1; j < parts.length; j++) {
                tokens.add(new Token(parts[j], lineNumber));
            }
        }
    }

    // Handler for return
    private void handleReturn(String trimmed, int lineNumber, List<Token> tokens) {
        tokens.add(new Token("return", lineNumber));
        String expr = trimmed.substring(7).trim();
        if (!expr.isEmpty()) {
            tokens.add(new Token(expr, lineNumber));
        }
    }

    public List<Token> tokenize(List<String> lines) {
        List<Token> tokens = new ArrayList<>();
        IndentationManager indentationManager = new IndentationManager();
        int prevIndent = 0;
        boolean afterIfThatFails = false;
        Integer tryBlockBaseIndent = null;
        boolean inAttempt = false;
        for (int i = 0; i < lines.size(); i++) {
            String rawLine = lines.get(i);
            String line = rawLine.replaceAll("\t", "    "); // treat tabs as 4 spaces
            int indent = 0;
            while (indent < line.length() && line.charAt(indent) == ' ') indent++;
            System.out.println("[DEBUG] Processing line " + (i + 1) + ": '" + line + "' (indent=" + indent + ")");
            if (line.trim().isEmpty() || line.trim().startsWith("#")) continue; // skip comments/empty
            String trimmed = line.trim();
            // Track when we enter an attempt block
            if (trimmed.equals("attempt")) {
                inAttempt = true;
                tryBlockBaseIndent = null;
            } else if (inAttempt && tryBlockBaseIndent == null) {
                // First non-empty, non-comment line after 'attempt'
                tryBlockBaseIndent = indent;
                inAttempt = false;
            }
            // Delay the check for INDENT after 'if that fails' until the next non-empty, non-comment line
            if (afterIfThatFails) {
                System.out.println("[DEBUG] tryBlockBaseIndent=" + tryBlockBaseIndent + ", current indent=" + indent);
                if (tryBlockBaseIndent != null && indent > tryBlockBaseIndent) {
                    System.out.println("[DEBUG] Emitting INDENT after 'if that fails' at line " + (i + 1));
                    tokens.add(new Token("INDENT", i + 1));
                }
                afterIfThatFails = false;
                tryBlockBaseIndent = null; // Reset after fallback block
            }
            if (trimmed.equals("if that fails")) {
                // Emit tokens for 'if that fails' so the parser can recognize the fallback block
                tokens.add(new Token("if", i + 1));
                tokens.add(new Token("that", i + 1));
                tokens.add(new Token("fails", i + 1));
                afterIfThatFails = true;
                continue; // Do not process 'if that fails' as a normal line
            }
            int indentChange = indentationManager.handleIndent(indent);
            if (indentChange == 1) {
                System.out.println("[DEBUG] Emitting INDENT at line " + (i + 1));
                tokens.add(new Token("INDENT", i + 1));
            } else if (indentChange == -1) {
                // Before processing the line, if it's a block keyword, emit DEDENT(s) first
                String nextTrimmed = trimmed;
                while (prevIndent > indent && indentationManager.hasUnclosedIndents()) {
                    // Only emit DEDENT before block keywords
                    if (nextTrimmed.equals("otherwise") || nextTrimmed.equals("if that fails") || nextTrimmed.equals("attempt") || nextTrimmed.equals("if") || nextTrimmed.equals("repeat") || nextTrimmed.startsWith("define ") || nextTrimmed.startsWith("call ") || nextTrimmed.startsWith("return ")) {
                        System.out.println("[DEBUG] Emitting DEDENT at line " + (i + 1) + " before block keyword");
                        tokens.add(new Token("DEDENT", i + 1));
                        indentationManager.closeIndent();
                        prevIndent = indentationManager.getCurrentIndent();
                    } else {
                        break;
                    }
                }
                // Now emit any remaining DEDENTs for normal dedent
                while (prevIndent > indent && indentationManager.hasUnclosedIndents()) {
                    System.out.println("[DEBUG] Emitting DEDENT at line " + (i + 1));
                    tokens.add(new Token("DEDENT", i + 1));
                    indentationManager.closeIndent();
                    prevIndent = indentationManager.getCurrentIndent();
                }
            }
            prevIndent = indent;
            // Tokenize the line
            System.out.println("[DEBUG] Line: '" + trimmed + "'");
            int idx = indent;
            boolean handledList = false;
            if (trimmed.startsWith("variable ") && trimmed.contains(" equals ")) {
                handleListVariableAssignment(trimmed, i + 1, tokens);
                handledList = true;
            } else if (trimmed.startsWith("set ") && trimmed.contains(" to ")) {
                handleListSetAssignment(trimmed, i + 1, tokens);
                handledList = true;
            } else if (trimmed.startsWith("ask ") && trimmed.contains(" and store in ")) {
                handleAskAndStore(trimmed, i + 1, tokens);
                continue;
            } else if (trimmed.startsWith("read file ") && trimmed.contains(" into ")) {
                handleReadFileInto(trimmed, i + 1, tokens);
                continue;
            } else if (trimmed.startsWith("append ") && trimmed.contains(" to ")) {
                handleAppendToFile(trimmed, i + 1, tokens);
                continue;
            } else if (trimmed.startsWith("delete file ")) {
                handleDeleteFile(trimmed, i + 1, tokens);
                continue;
            } else if (trimmed.startsWith("copy file ") && trimmed.contains(" to ")) {
                handleCopyFile(trimmed, i + 1, tokens);
                continue;
            } else if (trimmed.startsWith("list files in ") && trimmed.contains(" into ")) {
                handleListFilesInDir(trimmed, i + 1, tokens);
                continue;
            } else if (trimmed.startsWith("log ")) {
                handleLog(trimmed, i + 1, tokens);
                continue;
            } else if (trimmed.startsWith("define ")) {
                handleDefine(trimmed, i + 1, tokens);
                continue;
            } else if (trimmed.startsWith("return ")) {
                handleReturn(trimmed, i + 1, tokens);
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
                    while (idx < line.length() && Character.isWhitespace(line.charAt(idx))) idx++;
                } else {
                    int end = idx;
                    while (end < line.length() && !Character.isWhitespace(line.charAt(end))) end++;
                    String part2 = line.substring(idx, end);
                    String prevToken = tokens.isEmpty() ? "" : tokens.get(tokens.size() - 1).value;
                    String tokenValue;
                    if (part2.equals("and") || part2.equals("or") || part2.equals("not")) {
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
        while (indentationManager.hasUnclosedIndents()) {
            tokens.add(new Token("DEDENT", lines.size()));
            indentationManager.closeIndent();
        }
        return tokens;
    }
}
