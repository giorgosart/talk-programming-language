package talk;

import java.util.ArrayList;
import java.util.List;
import talk.ReadFileInstruction;
import talk.AppendToFileInstruction;
import talk.DeleteFileInstruction;
import talk.LogInstruction;
import talk.CopyFileInstruction;

public class Parser {
    private final List<Tokenizer.Token> tokens;
    private int pos = 0;

    public Parser(List<Tokenizer.Token> tokens) {
        this.tokens = tokens;
    }

    public List<Instruction> parse() {
        List<Instruction> instructions = new ArrayList<>();
        while (pos < tokens.size()) {
            Instruction instr = parseInstructionWithIndent();
            if (instr != null) {
                instructions.add(instr);
            }
        }
        return instructions;
    }

    private Instruction parseInstructionWithIndent() {
        if (pos >= tokens.size()) return null;
        Tokenizer.Token token = tokens.get(pos);
        String value = token.value;
        int line = token.lineNumber;
        if ("INDENT".equals(value)) {
            pos++;
            // Parse a block of instructions until DEDENT
            List<Instruction> block = new ArrayList<>();
            while (pos < tokens.size() && !peek("DEDENT")) {
                Instruction instr = parseInstructionWithIndent();
                if (instr != null) block.add(instr);
            }
            if (peek("DEDENT")) pos++;
            // Return a special BlockInstruction or flatten in parent
            // For now, flatten: return null, parent will collect block
            return new BlockInstruction(block, line);
        }
        if ("DEDENT".equals(value)) {
            // Should be handled by block logic
            pos++;
            return null;
        }
        if ("otherwise".equals(value)) {
            pos++;
            // Otherwise block can be indented or single-line
            List<Instruction> elseInstrs = new ArrayList<>();
            if (peek("INDENT")) {
                pos++;
                while (pos < tokens.size() && !peek("DEDENT")) {
                    Instruction instr = parseInstructionWithIndent();
                    if (instr != null) elseInstrs.add(instr);
                }
                if (peek("DEDENT")) pos++;
            } else {
                Instruction instr = parseInstructionWithIndent();
                if (instr != null) elseInstrs.add(instr);
            }
            return new OtherwiseBlockInstruction(elseInstrs, line);
        }
        if ("if".equals(value)) {
            pos++;
            // Parse the full logical condition, including chained 'and', 'or', and 'not' operators.
            // The entire condition string is passed to ExpressionResolver, which builds the logic tree.
            StringBuilder cond = new StringBuilder();
            while (pos < tokens.size() && !"then".equals(tokens.get(pos).value)) {
                cond.append(tokens.get(pos).value).append(" ");
                pos++;
            }
            if (peek("then")) pos++;
            List<Instruction> thenInstrs = new ArrayList<>();
            if (peek("INDENT")) {
                pos++;
                while (pos < tokens.size() && !peek("DEDENT") && !peek("otherwise")) {
                    Instruction instr = parseInstructionWithIndent();
                    if (instr != null) thenInstrs.add(instr);
                }
                if (peek("DEDENT")) pos++;
            } else {
                Instruction instr = parseInstructionWithIndent();
                if (instr != null) thenInstrs.add(instr);
            }
            List<Instruction> elseInstrs = new ArrayList<>();
            if (peek("otherwise")) {
                pos++;
                if (peek("INDENT")) {
                    pos++;
                    while (pos < tokens.size() && !peek("DEDENT")) {
                        Instruction instr = parseInstructionWithIndent();
                        if (instr != null) elseInstrs.add(instr);
                    }
                    if (peek("DEDENT")) pos++;
                } else {
                    Instruction instr = parseInstructionWithIndent();
                    if (instr != null) elseInstrs.add(instr);
                }
            }
            return new IfInstruction(cond.toString().trim(), thenInstrs, elseInstrs, line);
        }
        if ("attempt".equals(value)) {
            pos++;
            List<Instruction> tryBlock = new ArrayList<>();
            List<Instruction> catchBlock = new ArrayList<>();
            if (peek("INDENT")) {
                pos++;
                while (pos < tokens.size() && !peek("DEDENT") && !peek("if")) {
                    Instruction instr = parseInstructionWithIndent();
                    if (instr != null) tryBlock.add(instr);
                }
                if (peek("DEDENT")) pos++;
            } else {
                Instruction instr = parseInstructionWithIndent();
                if (instr != null) tryBlock.add(instr);
            }
            if (peek("if")) {
                // 'if that fails' or similar fallback
                pos++;
                if (peek("that")) pos++;
                if (peek("fails")) pos++;
                if (peek("INDENT")) {
                    pos++;
                    while (pos < tokens.size() && !peek("DEDENT")) {
                        Instruction instr = parseInstructionWithIndent();
                        if (instr != null) catchBlock.add(instr);
                    }
                    if (peek("DEDENT")) pos++;
                } else {
                    Instruction instr = parseInstructionWithIndent();
                    if (instr != null) catchBlock.add(instr);
                }
            }
            return new AttemptInstruction(tryBlock, catchBlock, line);
        }
        if ("repeat".equals(value)) {
            pos++;
            // Check for list iteration: repeat for each item in items
            if (peek("for")) {
                pos++;
                expect("each");
                String itemVar = expectIdentifier();
                expect("in");
                String listVar = expectIdentifier();
                List<Instruction> body = new ArrayList<>();
                if (peek("INDENT")) {
                    pos++;
                    while (pos < tokens.size() && !peek("DEDENT")) {
                        Instruction instr = parseInstructionWithIndent();
                        if (instr != null) body.add(instr);
                    }
                    if (peek("DEDENT")) pos++;
                } else {
                    Instruction instr = parseInstructionWithIndent();
                    if (instr != null) body.add(instr);
                }
                return new RepeatInstruction(itemVar, listVar, body, line);
            }
            // Parse count expression (repeat N times)
            StringBuilder countExpr = new StringBuilder();
            while (pos < tokens.size() && !"times".equals(tokens.get(pos).value)) {
                countExpr.append(tokens.get(pos).value).append(" ");
                pos++;
            }
            if (!peek("times")) {
                throw new RuntimeException("Syntax error at line " + line + ": Expected 'times' after repeat count");
            }
            pos++; // skip 'times'
            List<Instruction> body = new ArrayList<>();
            if (peek("INDENT")) {
                pos++;
                while (pos < tokens.size() && !peek("DEDENT")) {
                    Instruction instr = parseInstructionWithIndent();
                    if (instr != null) body.add(instr);
                }
                if (peek("DEDENT")) pos++;
            } else {
                Instruction instr = parseInstructionWithIndent();
                if (instr != null) body.add(instr);
            }
            return new RepeatInstruction(countExpr.toString().trim(), body, line);
        }
        if ("DEFINE".equals(value)) {
            pos++;
            String functionName = expectIdentifier();
            // Parse parameter names (all identifiers until INDENT)
            List<String> parameters = new ArrayList<>();
            while (pos < tokens.size() && !peek("INDENT") && !peek("DEDENT") && !peek("NEWLINE")) {
                Tokenizer.Token paramToken = tokens.get(pos);
                // Only accept identifiers (not keywords or symbols)
                if (paramToken.value.matches("[a-zA-Z_][a-zA-Z0-9_]*")) {
                    parameters.add(paramToken.value);
                    pos++;
                } else {
                    break;
                }
            }
            List<Instruction> body = new ArrayList<>();
            if (peek("INDENT")) {
                pos++;
                while (pos < tokens.size() && !peek("DEDENT")) {
                    Instruction instr = parseInstructionWithIndent();
                    if (instr != null) body.add(instr);
                }
                if (peek("DEDENT")) pos++;
            } else {
                throw new RuntimeException("Syntax error at line " + line + ": Function definition requires an indented block");
            }
            return new FunctionDefinitionInstruction(functionName, parameters, body, line);
        }
        if ("call".equalsIgnoreCase(value)) {
            pos++;
            String functionName = expectIdentifier();
            List<String> arguments = new ArrayList<>();
            if (peek("with")) {
                pos++;
                // Parse arguments (all values until INDENT, DEDENT, NEWLINE, or 'into')
                while (pos < tokens.size() && !peek("INDENT") && !peek("DEDENT") && !peek("NEWLINE") && !peek("into")) {
                    Tokenizer.Token argToken = tokens.get(pos);
                    // Accept identifiers, numbers, or quoted strings as arguments
                    if (argToken.value.matches("[a-zA-Z_][a-zA-Z0-9_]*") || argToken.value.matches("\".*\"") || argToken.value.matches("-?\\d+(\\.\\d+)?")) {
                        arguments.add(argToken.value);
                        pos++;
                    } else {
                        break;
                    }
                }
            }
            String intoVariable = null;
            if (peek("into")) {
                pos++;
                if (pos < tokens.size() && tokens.get(pos).value.matches("[a-zA-Z_][a-zA-Z0-9_]*")) {
                    intoVariable = tokens.get(pos).value;
                    pos++;
                } else {
                    throw new RuntimeException("Syntax error at line " + line + ": Expected variable name after 'into'");
                }
            }
            return new FunctionCallInstruction(functionName, arguments, intoVariable, line);
        }
        if ("return".equals(value)) {
            pos++;
            String expr = "";
            if (pos < tokens.size() && !tokens.get(pos).value.equals("INDENT") && !tokens.get(pos).value.equals("DEDENT") && !tokens.get(pos).value.equals("NEWLINE")) {
                expr = tokens.get(pos).value;
                pos++;
            }
            return new ReturnInstruction(expr, line);
        }
        // fallback to original parseInstruction for all other cases
        return parseInstruction();
    }

    private Instruction parseInstruction() {
        if (pos >= tokens.size()) return null;
        Tokenizer.Token token = tokens.get(pos);
        String value = token.value;
        int line = token.lineNumber;
        // Variable declaration: variable x
        if ("variable".equals(value)) {
            pos++;
            String name = expectIdentifier();
            if (peek("equal") || peek("equals")) {
                pos++;
                // Check for list literal
                if (peek("LIST_START")) {
                    pos++;
                    List<String> items = new ArrayList<>();
                    while (!peek("LIST_END")) {
                        items.add(expectValue());
                    }
                    expect("LIST_END");
                    return new VariableInstruction(name, new ListValue(items), line);
                } else {
                    String val = expectValue();
                    return new VariableInstruction(name, val, line);
                }
            }
            return new VariableInstruction(name, null, line);
        }
        // Assignment: set x to y
        if ("set".equals(value)) {
            pos++;
            String name = expectIdentifier();
            expect("to");
            // Check for list literal
            if (peek("LIST_START")) {
                pos++;
                List<String> items = new ArrayList<>();
                while (!peek("LIST_END")) {
                    items.add(expectValue());
                }
                expect("LIST_END");
                return new AssignmentInstruction(name, new ListValue(items), line);
            } else {
                String val = expectValue();
                return new AssignmentInstruction(name, val, line);
            }
        }
        // If: if x is greater than 10 then ... otherwise ...
        if ("if".equals(value)) {
            pos++;
            StringBuilder cond = new StringBuilder();
            // Gather condition until 'then'
            while (pos < tokens.size() && !"then".equals(tokens.get(pos).value)) {
                cond.append(tokens.get(pos).value).append(" ");
                pos++;
            }
            if (peek("then")) pos++;
            // Parse then-instructions (all lines until 'otherwise' or end)
            List<Instruction> thenInstrs = new ArrayList<>();
            while (pos < tokens.size() && !"otherwise".equals(tokens.get(pos).value)) {
                thenInstrs.add(parseInstruction());
            }
            // Parse else-instructions if 'otherwise' is present
            List<Instruction> elseInstrs = new ArrayList<>();
            if (peek("otherwise")) {
                pos++;
                while (pos < tokens.size()) {
                    elseInstrs.add(parseInstruction());
                }
            }
            return new IfInstruction(cond.toString().trim(), thenInstrs, elseInstrs, line);
        }
        // Ask: ask "..." and store in name
        if ("ask".equals(value)) {
            pos++;
            String prompt = expectValue();
            expect("and");
            expect("store");
            expect("in");
            String var = expectIdentifier();
            return new AskInstruction(prompt, var, line);
        }
        // Write: write "Hello" in file.txt
        if ("write".equals(value)) {
            pos++;
            String content = expectValue();
            expect("in");
            String file = expectIdentifier();
            return new WriteInstruction(content, file, line);
        }
        // Create file: create file as "filename.txt"
        if ("create".equals(value)) {
            pos++;
            expect("file");
            expect("as");
            String file = expectValue();
            return new CreateFileInstruction(file, line);
        }
        // Read file: read file <file> into <variable>
        if ("read".equals(value) && peekNext("file")) {
            pos++; // read
            pos++; // file
            String fileName = expectValue();
            expect("into");
            String variableName = expectIdentifier();
            return new ReadFileInstruction(fileName, variableName, line);
        }
        // Append to file: append <text> to <file>
        if ("append".equals(value)) {
            pos++;
            String text = expectValue();
            expect("to");
            String fileName = expectIdentifier();
            return new AppendToFileInstruction(text, fileName, line);
        }
        // Delete file: delete file <file>
        if ("delete".equals(value) && peekNext("file")) {
            pos++; // delete
            pos++; // file
            String fileName = expectValue();
            return new DeleteFileInstruction(fileName, line);
        }
        // List files in directory: list files in <directory> into <variable>
        if ("list".equals(value) && peekNext("files")) {
            pos++; // list
            pos++; // files
            expect("in");
            String directory = expectValue();
            expect("into");
            String variableName = expectIdentifier();
            return new ListDirectoryInstruction(directory, variableName, line);
        }
        // Attempt: attempt ... if that fails ...
        if ("attempt".equals(value)) {
            pos++;
            // For MVP, no block parsing, just stub
            return new AttemptInstruction(new ArrayList<>(), new ArrayList<>(), line);
        }
        // Logging: log <message>
        if ("log".equals(value)) {
            pos++;
            String message = expectValue();
            return new LogInstruction(message, line);
        }
        // File copying: copy file <source> to <destination>
        if ("copy".equals(value) && peekNext("file")) {
            pos++; // copy
            pos++; // file
            String source = expectValue();
            expect("to");
            String destination = expectValue();
            return new CopyFileInstruction(source, destination, line);
        }
        if ("return".equals(value)) {
            pos++;
            String expr = "";
            if (pos < tokens.size() && !tokens.get(pos).value.equals("INDENT") && !tokens.get(pos).value.equals("DEDENT") && !tokens.get(pos).value.equals("NEWLINE")) {
                expr = tokens.get(pos).value;
                pos++;
            }
            return new ReturnInstruction(expr, line);
        }
        throw new RuntimeException("Syntax error at line " + line + ": Unknown instruction '" + value + "'");
    }

    private boolean peek(String expected) {
        return pos < tokens.size() && tokens.get(pos).value.equals(expected);
    }

    // Utility: peek next token value
    private boolean peekNext(String expected) {
        return (pos + 1) < tokens.size() && tokens.get(pos + 1).value.equals(expected);
    }

    private void expect(String expected) {
        if (!peek(expected)) {
            int line = pos < tokens.size() ? tokens.get(pos).lineNumber : -1;
            throw new RuntimeException("Syntax error at line " + line + ": Expected '" + expected + "'");
        }
        pos++;
    }
    private String expectIdentifier() {
        if (pos >= tokens.size()) throw new RuntimeException("Unexpected end of input");
        String val = tokens.get(pos).value;
        pos++;
        return val;
    }
    private String expectValue() {
        if (pos >= tokens.size()) throw new RuntimeException("Unexpected end of input");
        String val = tokens.get(pos).value;
        pos++;
        return val;
    }

    // Add BlockInstruction and OtherwiseBlockInstruction for block flattening
    static class BlockInstruction implements Instruction {
        private final List<Instruction> block;
        private final int lineNumber;
        BlockInstruction(List<Instruction> block, int lineNumber) {
            this.block = block;
            this.lineNumber = lineNumber;
        }
        public List<Instruction> getBlock() { return block; }
        public int getLineNumber() { return lineNumber; }
    }
    static class OtherwiseBlockInstruction implements Instruction {
        private final List<Instruction> block;
        private final int lineNumber;
        OtherwiseBlockInstruction(List<Instruction> block, int lineNumber) {
            this.block = block;
            this.lineNumber = lineNumber;
        }
        public List<Instruction> getBlock() { return block; }
        public int getLineNumber() { return lineNumber; }
    }
}
