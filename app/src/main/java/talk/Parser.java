package talk;

import java.util.ArrayList;
import java.util.List;

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
            // Parse count expression
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
            return new FunctionDefinitionInstruction(functionName, body, line);
        }
        if ("CALL".equals(value)) {
            pos++;
            String functionName = expectIdentifier();
            return new FunctionCallInstruction(functionName, line);
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
            if (peek("equal")) {
                pos++;
                String val = expectValue();
                return new VariableInstruction(name, val, line);
            }
            return new VariableInstruction(name, null, line);
        }
        // Assignment: set x to y
        if ("set".equals(value)) {
            pos++;
            String name = expectIdentifier();
            expect("to");
            String val = expectValue();
            return new AssignmentInstruction(name, val, line);
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
        // Attempt: attempt ... if that fails ...
        if ("attempt".equals(value)) {
            pos++;
            // For MVP, no block parsing, just stub
            return new AttemptInstruction(new ArrayList<>(), new ArrayList<>(), line);
        }
        throw new RuntimeException("Syntax error at line " + line + ": Unknown instruction '" + value + "'");
    }

    private boolean peek(String expected) {
        return pos < tokens.size() && tokens.get(pos).value.equals(expected);
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
