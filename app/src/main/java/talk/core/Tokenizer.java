package talk.core;

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
    
    // Handler for import statements
    private void handleImport(String trimmed, int lineNumber, List<Token> tokens) {
        String filePath = trimmed.substring("import ".length()).trim();
        
        // Remove quotes if present
        if ((filePath.startsWith("\"") && filePath.endsWith("\"")) || 
            (filePath.startsWith("'") && filePath.endsWith("'"))) {
            filePath = filePath.substring(1, filePath.length() - 1);
        }
        
        tokens.add(new Token("import", lineNumber));
        tokens.add(new Token(filePath, lineNumber));
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

    // Handler for string uppercase operation
    private void handleUppercaseOf(String trimmed, int lineNumber, List<Token> tokens) {
        if (trimmed.startsWith("uppercase of ")) {
            String value = trimmed.substring(12).trim();
            tokens.add(new Token("uppercase", lineNumber));
            tokens.add(new Token("of", lineNumber));
            tokens.add(new Token(value, lineNumber));
        }
    }

    // Handler for string lowercase operation
    private void handleLowercaseOf(String trimmed, int lineNumber, List<Token> tokens) {
        if (trimmed.startsWith("lowercase of ")) {
            String value = trimmed.substring(12).trim();
            tokens.add(new Token("lowercase", lineNumber));
            tokens.add(new Token("of", lineNumber));
            tokens.add(new Token(value, lineNumber));
        }
    }

    // Handler for string trim operation
    private void handleTrimOf(String trimmed, int lineNumber, List<Token> tokens) {
        if (trimmed.startsWith("trim of ")) {
            String value = trimmed.substring(8).trim();
            tokens.add(new Token("trim", lineNumber));
            tokens.add(new Token("of", lineNumber));
            tokens.add(new Token(value, lineNumber));
        }
    }

    // Handler for string length operation
    private void handleLengthOf(String trimmed, int lineNumber, List<Token> tokens) {
        if (trimmed.startsWith("length of ")) {
            String value = trimmed.substring(10).trim();
            tokens.add(new Token("length", lineNumber));
            tokens.add(new Token("of", lineNumber));
            tokens.add(new Token(value, lineNumber));
        }
    }

    // Handler for substring operation
    private void handleSubstring(String trimmed, int lineNumber, List<Token> tokens) {
        if (trimmed.startsWith("substring of ") && trimmed.contains(" from ") && trimmed.contains(" to ")) {
            int fromIdx = trimmed.indexOf(" from ");
            int toIdx = trimmed.indexOf(" to ", fromIdx);
            
            String valuePart = trimmed.substring(12, fromIdx).trim();
            String startPart = trimmed.substring(fromIdx + 6, toIdx).trim();
            String endPart = trimmed.substring(toIdx + 4).trim();
            
            tokens.add(new Token("substring", lineNumber));
            tokens.add(new Token("of", lineNumber));
            tokens.add(new Token(valuePart, lineNumber));
            tokens.add(new Token("from", lineNumber));
            tokens.add(new Token(startPart, lineNumber));
            tokens.add(new Token("to", lineNumber));
            tokens.add(new Token(endPart, lineNumber));
        }
    }

    // Handler for replace operation
    private void handleReplace(String trimmed, int lineNumber, List<Token> tokens) {
        if (trimmed.startsWith("replace ") && trimmed.contains(" with ") && trimmed.contains(" in ")) {
            int withIdx = trimmed.indexOf(" with ");
            int inIdx = trimmed.indexOf(" in ", withIdx);
            
            String oldPart = trimmed.substring(8, withIdx).trim();
            String newPart = trimmed.substring(withIdx + 6, inIdx).trim();
            String valuePart = trimmed.substring(inIdx + 4).trim();
            
            tokens.add(new Token("replace", lineNumber));
            tokens.add(new Token(oldPart, lineNumber));
            tokens.add(new Token("with", lineNumber));
            tokens.add(new Token(newPart, lineNumber));
            tokens.add(new Token("in", lineNumber));
            tokens.add(new Token(valuePart, lineNumber));
        }
    }

    // Handler for split operation
    private void handleSplit(String trimmed, int lineNumber, List<Token> tokens) {
        if (trimmed.startsWith("split ") && trimmed.contains(" by ")) {
            int byIdx = trimmed.indexOf(" by ");
            
            String valuePart = trimmed.substring(6, byIdx).trim();
            String delimiterPart = trimmed.substring(byIdx + 4).trim();
            
            tokens.add(new Token("split", lineNumber));
            tokens.add(new Token(valuePart, lineNumber));
            tokens.add(new Token("by", lineNumber));
            tokens.add(new Token(delimiterPart, lineNumber));
        }
    }
    
    // Handler for test assertions
    private void handleTestAssertion(String trimmed, int lineNumber, List<Token> tokens) {
        if (trimmed.startsWith("expect result of ") && trimmed.contains(" to be ")) {
            int toBeIdx = trimmed.indexOf(" to be ");
            
            String expressionPart = trimmed.substring(16, toBeIdx).trim();
            String expectedValuePart = trimmed.substring(toBeIdx + 7).trim();
            
            tokens.add(new Token("expect", lineNumber));
            tokens.add(new Token("result", lineNumber));
            tokens.add(new Token("of", lineNumber));
            tokens.add(new Token(expressionPart, lineNumber));
            tokens.add(new Token("to", lineNumber));
            tokens.add(new Token("be", lineNumber));
            tokens.add(new Token(expectedValuePart, lineNumber));
        }
    }
    
    // Handler for test blocks
    private void handleTestBlock(String trimmed, int lineNumber, List<Token> tokens) {
        if (trimmed.startsWith("test ")) {
            String description = trimmed.substring(5).trim();
            
            // Check if the description is quoted
            if ((description.startsWith("\"") && description.endsWith("\"")) || 
                (description.startsWith("'") && description.endsWith("'"))) {
                // Remove the quotes
                description = description.substring(1, description.length() - 1);
            }
            
            tokens.add(new Token("test", lineNumber));
            tokens.add(new Token(description, lineNumber));
        } else if (trimmed.equals("before each test")) {
            tokens.add(new Token("before", lineNumber));
            tokens.add(new Token("each", lineNumber));
            tokens.add(new Token("test", lineNumber));
        } else if (trimmed.equals("after each test")) {
            tokens.add(new Token("after", lineNumber));
            tokens.add(new Token("each", lineNumber)); 
            tokens.add(new Token("test", lineNumber));
        }
    }

    // Handler for arithmetic operations
    private void handleArithmeticExpression(String trimmed, int lineNumber, List<Token> tokens) {
        // Check if the line contains an arithmetic operation
        if (trimmed.contains(" plus ") || 
            trimmed.contains(" minus ") || 
            trimmed.contains(" times ") || 
            trimmed.contains(" divided by ") || 
            trimmed.contains(" modulo ") ||
            trimmed.contains(" to the power of ") ||
            trimmed.startsWith("negative of ") ||
            trimmed.startsWith("absolute of ") ||
            trimmed.startsWith("round ") ||
            trimmed.startsWith("floor ") ||
            trimmed.startsWith("ceil ")) {
            
            // If line starts with "set", handle variable assignment
            if (trimmed.startsWith("set ") && trimmed.contains(" to ")) {
                int toIdx = trimmed.indexOf(" to ");
                String varName = trimmed.substring(4, toIdx).trim();
                String expr = trimmed.substring(toIdx + 4).trim();
                
                tokens.add(new Token("set", lineNumber));
                tokens.add(new Token(varName, lineNumber));
                tokens.add(new Token("to", lineNumber));
                tokens.add(new Token(expr, lineNumber));
            } else {
                // Direct expression
                tokens.add(new Token(trimmed, lineNumber));
            }
        }
    }

    // Handler for 'now' date expression
    private void handleNowExpression(String trimmed, int lineNumber, List<Token> tokens) {
        // For "set x to now" pattern
        if (trimmed.startsWith("set ") && trimmed.contains(" to now")) {
            int toIdx = trimmed.indexOf(" to now");
            String varName = trimmed.substring(4, toIdx).trim();
            
            tokens.add(new Token("set", lineNumber));
            tokens.add(new Token(varName, lineNumber));
            tokens.add(new Token("to", lineNumber));
            tokens.add(new Token("now", lineNumber));
        }
    }
    
    // Handler for 'today' date expression
    private void handleTodayExpression(String trimmed, int lineNumber, List<Token> tokens) {
        // For "set x to today" pattern
        if (trimmed.startsWith("set ") && trimmed.contains(" to today")) {
            int toIdx = trimmed.indexOf(" to today");
            String varName = trimmed.substring(4, toIdx).trim();
            
            tokens.add(new Token("set", lineNumber));
            tokens.add(new Token(varName, lineNumber));
            tokens.add(new Token("to", lineNumber));
            tokens.add(new Token("today", lineNumber));
        }
    }
    
    // Handler for 'format date' expression
    private void handleFormatDate(String trimmed, int lineNumber, List<Token> tokens) {
        if (trimmed.startsWith("set ") && trimmed.contains(" to format date ")) {
            int toFormatIdx = trimmed.indexOf(" to format date ");
            int asIdx = trimmed.indexOf(" as ", toFormatIdx);
            
            if (asIdx > 0) {
                String varName = trimmed.substring(4, toFormatIdx).trim();
                String dateExpr = trimmed.substring(toFormatIdx + " to format date ".length(), asIdx).trim();
                String pattern = trimmed.substring(asIdx + " as ".length()).trim();
                
                // Remove quotes from pattern if present
                if ((pattern.startsWith("\"") && pattern.endsWith("\"")) || 
                    (pattern.startsWith("'") && pattern.endsWith("'"))) {
                    pattern = pattern.substring(1, pattern.length() - 1);
                }
                
                tokens.add(new Token("set", lineNumber));
                tokens.add(new Token(varName, lineNumber));
                tokens.add(new Token("to", lineNumber));
                tokens.add(new Token("format", lineNumber));
                tokens.add(new Token("date", lineNumber));
                tokens.add(new Token(dateExpr, lineNumber));
                tokens.add(new Token("as", lineNumber));
                tokens.add(new Token(pattern, lineNumber));
            }
        }
    }
    
    // Handler for 'add days' expression
    private void handleAddDays(String trimmed, int lineNumber, List<Token> tokens) {
        if (trimmed.startsWith("set ") && trimmed.contains(" to add ") && trimmed.contains(" days to ")) {
            int toAddIdx = trimmed.indexOf(" to add ");
            int daysToIdx = trimmed.indexOf(" days to ", toAddIdx);
            
            if (daysToIdx > 0) {
                String varName = trimmed.substring(4, toAddIdx).trim();
                String daysStr = trimmed.substring(toAddIdx + " to add ".length(), daysToIdx).trim();
                String dateExpr = trimmed.substring(daysToIdx + " days to ".length()).trim();
                
                tokens.add(new Token("set", lineNumber));
                tokens.add(new Token(varName, lineNumber));
                tokens.add(new Token("to", lineNumber));
                tokens.add(new Token("add", lineNumber));
                tokens.add(new Token(daysStr, lineNumber));
                tokens.add(new Token("days", lineNumber));
                tokens.add(new Token("to", lineNumber));
                tokens.add(new Token(dateExpr, lineNumber));
            }
        }
    }
    
    // Handler for 'subtract days' expression
    private void handleSubtractDays(String trimmed, int lineNumber, List<Token> tokens) {
        if (trimmed.startsWith("set ") && trimmed.contains(" to subtract ") && trimmed.contains(" days from ")) {
            int toSubtractIdx = trimmed.indexOf(" to subtract ");
            int daysFromIdx = trimmed.indexOf(" days from ", toSubtractIdx);
            
            if (daysFromIdx > 0) {
                String varName = trimmed.substring(4, toSubtractIdx).trim();
                String daysStr = trimmed.substring(toSubtractIdx + " to subtract ".length(), daysFromIdx).trim();
                String dateExpr = trimmed.substring(daysFromIdx + " days from ".length()).trim();
                
                tokens.add(new Token("set", lineNumber));
                tokens.add(new Token(varName, lineNumber));
                tokens.add(new Token("to", lineNumber));
                tokens.add(new Token("subtract", lineNumber));
                tokens.add(new Token(daysStr, lineNumber));
                tokens.add(new Token("days", lineNumber));
                tokens.add(new Token("from", lineNumber));
                tokens.add(new Token(dateExpr, lineNumber));
            }
        }
    }
    
    // Handler for 'difference in days' expression
    private void handleDifferenceInDays(String trimmed, int lineNumber, List<Token> tokens) {
        if (trimmed.startsWith("set ") && trimmed.contains(" to difference in days between ") && trimmed.contains(" and ")) {
            int toDiffIdx = trimmed.indexOf(" to difference in days between ");
            int andIdx = trimmed.indexOf(" and ", toDiffIdx);
            
            if (andIdx > 0) {
                String varName = trimmed.substring(4, toDiffIdx).trim();
                String date1 = trimmed.substring(toDiffIdx + " to difference in days between ".length(), andIdx).trim();
                String date2 = trimmed.substring(andIdx + " and ".length()).trim();
                
                tokens.add(new Token("set", lineNumber));
                tokens.add(new Token(varName, lineNumber));
                tokens.add(new Token("to", lineNumber));
                tokens.add(new Token("difference", lineNumber));
                tokens.add(new Token("in", lineNumber));
                tokens.add(new Token("days", lineNumber));
                tokens.add(new Token("between", lineNumber));
                tokens.add(new Token(date1, lineNumber));
                tokens.add(new Token("and", lineNumber));
                tokens.add(new Token(date2, lineNumber));
            }
        }
    }
    
    // Handler for 'day of week' expression
    private void handleDayOfWeek(String trimmed, int lineNumber, List<Token> tokens) {
        if (trimmed.startsWith("set ") && trimmed.contains(" to day of week of ")) {
            int toDayOfWeekIdx = trimmed.indexOf(" to day of week of ");
            
            String varName = trimmed.substring(4, toDayOfWeekIdx).trim();
            String dateExpr = trimmed.substring(toDayOfWeekIdx + " to day of week of ".length()).trim();
            
            tokens.add(new Token("set", lineNumber));
            tokens.add(new Token(varName, lineNumber));
            tokens.add(new Token("to", lineNumber));
            tokens.add(new Token("day", lineNumber));
            tokens.add(new Token("of", lineNumber));
            tokens.add(new Token("week", lineNumber));
            tokens.add(new Token("of", lineNumber));
            tokens.add(new Token(dateExpr, lineNumber));
        }
    }
    
    // Handler for 'parse date' expression
    private void handleParseDate(String trimmed, int lineNumber, List<Token> tokens) {
        if (trimmed.startsWith("set ") && trimmed.contains(" to parse date ")) {
            int toParseDateIdx = trimmed.indexOf(" to parse date ");
            
            String varName = trimmed.substring(4, toParseDateIdx).trim();
            String dateStr = trimmed.substring(toParseDateIdx + " to parse date ".length()).trim();
            
            // Remove quotes if present
            if ((dateStr.startsWith("\"") && dateStr.endsWith("\"")) || 
                (dateStr.startsWith("'") && dateStr.endsWith("'"))) {
                dateStr = dateStr.substring(1, dateStr.length() - 1);
            }
            
            tokens.add(new Token("set", lineNumber));
            tokens.add(new Token(varName, lineNumber));
            tokens.add(new Token("to", lineNumber));
            tokens.add(new Token("parse", lineNumber));
            tokens.add(new Token("date", lineNumber));
            tokens.add(new Token(dateStr, lineNumber));
        }
    }
    
    // Handler for date comparison ('is before' and 'is after')
    private void handleDateComparison(String trimmed, int lineNumber, List<Token> tokens) {
        if (trimmed.startsWith("if ")) {
            // Handle 'is before' comparison
            int isBeforeIdx = trimmed.indexOf(" is before ");
            if (isBeforeIdx > 0) {
                String date1 = trimmed.substring(3, isBeforeIdx).trim();
                
                int thenIdx = trimmed.indexOf(" then", isBeforeIdx);
                String date2 = thenIdx > 0 
                    ? trimmed.substring(isBeforeIdx + " is before ".length(), thenIdx).trim()
                    : trimmed.substring(isBeforeIdx + " is before ".length()).trim();
                
                tokens.add(new Token("if", lineNumber));
                tokens.add(new Token(date1, lineNumber));
                tokens.add(new Token("is", lineNumber));
                tokens.add(new Token("before", lineNumber));
                tokens.add(new Token(date2, lineNumber));
                
                if (thenIdx > 0) {
                    tokens.add(new Token("then", lineNumber));
                }
                return;
            }
            
            // Handle 'is after' comparison
            int isAfterIdx = trimmed.indexOf(" is after ");
            if (isAfterIdx > 0) {
                String date1 = trimmed.substring(3, isAfterIdx).trim();
                
                int thenIdx = trimmed.indexOf(" then", isAfterIdx);
                String date2 = thenIdx > 0 
                    ? trimmed.substring(isAfterIdx + " is after ".length(), thenIdx).trim()
                    : trimmed.substring(isAfterIdx + " is after ".length()).trim();
                
                tokens.add(new Token("if", lineNumber));
                tokens.add(new Token(date1, lineNumber));
                tokens.add(new Token("is", lineNumber));
                tokens.add(new Token("after", lineNumber));
                tokens.add(new Token(date2, lineNumber));
                
                if (thenIdx > 0) {
                    tokens.add(new Token("then", lineNumber));
                }
            }
        }
    }

    // Handler for logical operators (and, or, not)
    private void handleLogicalOperators(String trimmed, int lineNumber, List<Token> tokens) {
        // First, standard tokenize the statement
        String[] parts = trimmed.split("\\s+");
        for (int i = 0; i < parts.length; i++) {
            String part = parts[i];
            
            // Convert the case-insensitive logical operators to standard form
            if (part.equalsIgnoreCase("and")) {
                tokens.add(new Token("and", lineNumber));
            } else if (part.equalsIgnoreCase("or")) {
                tokens.add(new Token("or", lineNumber));
            } else if (part.equalsIgnoreCase("not")) {
                tokens.add(new Token("not", lineNumber));
            } else {
                // For non-logical operators, add the token as is
                tokens.add(new Token(part, lineNumber));
            }
        }
    }
    
    // Handler for comparison operators (is equal to, is not equal to, is greater than, is smaller than)
    // and logical operators (and, or, not)
    private void handleComparisonOperators(String trimmed, int lineNumber, List<Token> tokens) {
        if (trimmed.startsWith("if ")) {
            // Extract the 'if' token
            tokens.add(new Token("if", lineNumber));
            
            // Process the rest of the condition
            String condition = trimmed.substring(3).trim();
            int thenIdx = condition.indexOf(" then");
            String conditionWithoutThen = thenIdx > 0 ? condition.substring(0, thenIdx) : condition;
            
            System.out.println("[TOKENIZER DEBUG] Processing condition: " + conditionWithoutThen);
            
            // Split by potential logical operators to handle complex expressions
            List<String> expressions = splitByLogicalOperators(conditionWithoutThen);
            
            System.out.println("[TOKENIZER DEBUG] Split into " + expressions.size() + " parts: " + expressions);
            
            for (int i = 0; i < expressions.size(); i++) {
                String expr = expressions.get(i).trim();
                
                System.out.println("[TOKENIZER DEBUG] Processing part " + i + ": " + expr);
                
                // Check for logical operators first
                if (expr.equalsIgnoreCase("and")) {
                    tokens.add(new Token("and", lineNumber));
                } else if (expr.equalsIgnoreCase("or")) {
                    tokens.add(new Token("or", lineNumber));
                } else if (expr.equalsIgnoreCase("not")) {
                    tokens.add(new Token("not", lineNumber));
                }
                // Check for each comparison type and tokenize accordingly
                else if (expr.contains(" is equal to ")) {
                    tokenizeComparison(expr, " is equal to ", "is equal to", lineNumber, tokens);
                } else if (expr.contains(" is not equal to ")) {
                    tokenizeComparison(expr, " is not equal to ", "is not equal to", lineNumber, tokens);
                } else if (expr.contains(" is greater than ")) {
                    tokenizeComparison(expr, " is greater than ", "is greater than", lineNumber, tokens);
                } else if (expr.contains(" is smaller than ")) {
                    tokenizeComparison(expr, " is smaller than ", "is smaller than", lineNumber, tokens);
                } else {
                    // Add the expression as is if no comparison or logical operator found
                    tokens.add(new Token(expr, lineNumber));
                }
            }
            
            // Add 'then' token if present
            if (thenIdx > 0) {
                tokens.add(new Token("then", lineNumber));
            }
        }
    }
    
    // Handler for plugin calls
    private void handlePluginCall(String trimmed, int lineNumber, List<Token> tokens) {
        // Import ArrayList
        java.util.List<String> args = new java.util.ArrayList<>();
        // Format: "use plugin <pluginAlias> with <arg1> and <arg2> and <arg3> ... into <variable>"
        // or: "use plugin <pluginAlias> into <variable>"
        // or: "use plugin <pluginAlias> with <arg1>"
        
        String restOfLine = trimmed.substring("use plugin ".length());
        String pluginAlias;
        String argsStr = null;
        String intoVar = null;
        
        // Check if there's an "into" clause
        int intoIdx = restOfLine.indexOf(" into ");
        if (intoIdx >= 0) {
            intoVar = restOfLine.substring(intoIdx + 6).trim();
            restOfLine = restOfLine.substring(0, intoIdx);
        }
        
        // Check if there's a "with" clause
        int withIdx = restOfLine.indexOf(" with ");
        if (withIdx >= 0) {
            pluginAlias = restOfLine.substring(0, withIdx).trim();
            argsStr = restOfLine.substring(withIdx + 6).trim();
        } else {
            pluginAlias = restOfLine.trim();
        }
        
        // Add tokens
        tokens.add(new Token("use", lineNumber));
        tokens.add(new Token("plugin", lineNumber));
        tokens.add(new Token(pluginAlias, lineNumber));
        
        if (argsStr != null) {
            tokens.add(new Token("with", lineNumber));
            
            // Parse the arguments
            List<String> args = new ArrayList<>();
            StringBuilder argBuilder = new StringBuilder();
            boolean inQuotes = false;
            
            for (int i = 0; i < argsStr.length(); i++) {
                char c = argsStr.charAt(i);
                if (c == '"') {
                    inQuotes = !inQuotes;
                    argBuilder.append(c);
                } else if (!inQuotes && (i + 4 <= argsStr.length() && argsStr.substring(i, i + 5).equals(" and "))) {
                    // Split on " and " but only if not inside quotes
                    args.add(argBuilder.toString().trim());
                    argBuilder.setLength(0);
                    i += 4; // Skip " and "
                } else {
                    argBuilder.append(c);
                }
            }
            
            // Add the last argument
            if (argBuilder.length() > 0) {
                args.add(argBuilder.toString().trim());
            }
            
            // Add all arguments as tokens
            for (String arg : args) {
                tokens.add(new Token(arg, lineNumber));
                if (args.indexOf(arg) < args.size() - 1) {
                    tokens.add(new Token("and", lineNumber));
                }
            }
        }
        
        if (intoVar != null) {
            tokens.add(new Token("into", lineNumber));
            tokens.add(new Token(intoVar, lineNumber));
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
                // Check for arithmetic operations
                if (trimmed.contains(" to ") && (
                    trimmed.contains(" plus ") || 
                    trimmed.contains(" minus ") || 
                    trimmed.contains(" times ") || 
                    trimmed.contains(" divided by ") || 
                    trimmed.contains(" modulo ") ||
                    trimmed.contains(" to the power of ") ||
                    trimmed.contains(" negative of ") ||
                    trimmed.contains(" absolute of ") ||
                    trimmed.contains(" round ") ||
                    trimmed.contains(" floor ") ||
                    trimmed.contains(" ceil "))) {
                    
                    int toIdx = trimmed.indexOf(" to ");
                    String varName = trimmed.substring(4, toIdx).trim();
                    String expr = trimmed.substring(toIdx + 4).trim();
                    
                    tokens.add(new Token("set", i + 1));
                    tokens.add(new Token(varName, i + 1));
                    tokens.add(new Token("to", i + 1));
                    tokens.add(new Token(expr, i + 1));
                    continue;
                }
                // Check for string operations first
                if (trimmed.contains(" to uppercase of ")) {
                    int toIdx = trimmed.indexOf(" to ");
                    String varName = trimmed.substring(4, toIdx).trim();
                    String expr = trimmed.substring(toIdx + 4).trim();
                    
                    tokens.add(new Token("set", i + 1));
                    tokens.add(new Token(varName, i + 1));
                    tokens.add(new Token("to", i + 1));
                    tokens.add(new Token(expr, i + 1));
                    continue;
                } else if (trimmed.contains(" to lowercase of ")) {
                    int toIdx = trimmed.indexOf(" to ");
                    String varName = trimmed.substring(4, toIdx).trim();
                    String expr = trimmed.substring(toIdx + 4).trim();
                    
                    tokens.add(new Token("set", i + 1));
                    tokens.add(new Token(varName, i + 1));
                    tokens.add(new Token("to", i + 1));
                    tokens.add(new Token(expr, i + 1));
                    continue;
                } else if (trimmed.contains(" to trim of ")) {
                    int toIdx = trimmed.indexOf(" to ");
                    String varName = trimmed.substring(4, toIdx).trim();
                    String expr = trimmed.substring(toIdx + 4).trim();
                    
                    tokens.add(new Token("set", i + 1));
                    tokens.add(new Token(varName, i + 1));
                    tokens.add(new Token("to", i + 1));
                    tokens.add(new Token(expr, i + 1));
                    continue;
                } else if (trimmed.contains(" to length of ")) {
                    int toIdx = trimmed.indexOf(" to ");
                    String varName = trimmed.substring(4, toIdx).trim();
                    String expr = trimmed.substring(toIdx + 4).trim();
                    
                    tokens.add(new Token("set", i + 1));
                    tokens.add(new Token(varName, i + 1));
                    tokens.add(new Token("to", i + 1));
                    tokens.add(new Token(expr, i + 1));
                    continue;
                } else if (trimmed.contains(" to substring of ")) {
                    int toIdx = trimmed.indexOf(" to ");
                    String varName = trimmed.substring(4, toIdx).trim();
                    String expr = trimmed.substring(toIdx + 4).trim();
                    
                    tokens.add(new Token("set", i + 1));
                    tokens.add(new Token(varName, i + 1));
                    tokens.add(new Token("to", i + 1));
                    tokens.add(new Token(expr, i + 1));
                    continue;
                } else if (trimmed.contains(" to replace ")) {
                    int toIdx = trimmed.indexOf(" to ");
                    String varName = trimmed.substring(4, toIdx).trim();
                    String expr = trimmed.substring(toIdx + 4).trim();
                    
                    tokens.add(new Token("set", i + 1));
                    tokens.add(new Token(varName, i + 1));
                    tokens.add(new Token("to", i + 1));
                    tokens.add(new Token(expr, i + 1));
                    continue;
                } else if (trimmed.contains(" to split ")) {
                    int toIdx = trimmed.indexOf(" to ");
                    String varName = trimmed.substring(4, toIdx).trim();
                    String expr = trimmed.substring(toIdx + 4).trim();
                    
                    tokens.add(new Token("set", i + 1));
                    tokens.add(new Token(varName, i + 1));
                    tokens.add(new Token("to", i + 1));
                    tokens.add(new Token(expr, i + 1));
                    continue;
                } 
                // Check for date expressions
                if (trimmed.contains(" to now")) {
                    handleNowExpression(trimmed, i + 1, tokens);
                    continue;
                } else if (trimmed.contains(" to today")) {
                    handleTodayExpression(trimmed, i + 1, tokens);
                    continue;
                } else if (trimmed.contains(" to format date ")) {
                    handleFormatDate(trimmed, i + 1, tokens);
                    continue;
                } else if (trimmed.contains(" to add ") && trimmed.contains(" days to ")) {
                    handleAddDays(trimmed, i + 1, tokens);
                    continue;
                } else if (trimmed.contains(" to subtract ") && trimmed.contains(" days from ")) {
                    handleSubtractDays(trimmed, i + 1, tokens);
                    continue;
                } else if (trimmed.contains(" to difference in days between ") && trimmed.contains(" and ")) {
                    handleDifferenceInDays(trimmed, i + 1, tokens);
                    continue;
                } else if (trimmed.contains(" to day of week of ")) {
                    handleDayOfWeek(trimmed, i + 1, tokens);
                    continue;
                } else if (trimmed.contains(" to parse date ")) {
                    handleParseDate(trimmed, i + 1, tokens);
                    continue;
                } else {
                    handleListSetAssignment(trimmed, i + 1, tokens);
                    handledList = true;
                }
            } else if (trimmed.startsWith("uppercase of ")) {
                handleUppercaseOf(trimmed, i + 1, tokens);
                continue;
            } else if (trimmed.startsWith("lowercase of ")) {
                handleLowercaseOf(trimmed, i + 1, tokens);
                continue;
            } else if (trimmed.startsWith("trim of ")) {
                handleTrimOf(trimmed, i + 1, tokens);
                continue;
            } else if (trimmed.startsWith("length of ")) {
                handleLengthOf(trimmed, i + 1, tokens);
                continue;
            } else if (trimmed.startsWith("substring of ")) {
                handleSubstring(trimmed, i + 1, tokens);
                continue;
            } else if (trimmed.startsWith("replace ")) {
                handleReplace(trimmed, i + 1, tokens);
                continue;
            } else if (trimmed.startsWith("split ")) {
                handleSplit(trimmed, i + 1, tokens);
                continue;
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
            } else if (trimmed.startsWith("copy file ")) {
                handleCopyFile(trimmed, i + 1, tokens);
                continue;
            } else if (trimmed.startsWith("list directory ")) {
                handleListDirectory(trimmed, i + 1, tokens);
                continue;
            } else if (trimmed.startsWith("if ")) {
                handleIfStatement(trimmed, i + 1, tokens);
                continue;
            } else if (trimmed.equals("otherwise")) {
                tokens.add(new Token("otherwise", i + 1));
                continue;
            } else if (trimmed.startsWith("repeat ")) {
                handleRepeatStatement(trimmed, i + 1, tokens);
                continue;
            } else if (trimmed.startsWith("import ")) {
                handleImport(trimmed, i + 1, tokens);
                continue;
            } else if (trimmed.startsWith("use plugin ")) {
                handlePluginCall(trimmed, i + 1, tokens);
                continue;
            } else if (trimmed.equals("attempt")) {
                tokens.add(new Token("attempt", i + 1));
                continue;
            } else {
                handleArithmeticExpression(trimmed, i + 1, tokens);
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
