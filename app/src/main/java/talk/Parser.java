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
            Instruction instr = parseInstructionWithBlocks();
            if (instr != null) {
                instructions.add(instr);
            }
        }
        return instructions;
    }

    // New method to support if ... then ... otherwise ... blocks by line
    private Instruction parseInstructionWithBlocks() {
        if (pos >= tokens.size()) return null;
        Tokenizer.Token token = tokens.get(pos);
        String value = token.value;
        int line = token.lineNumber;
        if ("if".equals(value)) {
            pos++;
            StringBuilder cond = new StringBuilder();
            while (pos < tokens.size() && !"then".equals(tokens.get(pos).value)) {
                cond.append(tokens.get(pos).value).append(" ");
                pos++;
            }
            if (peek("then")) pos++;
            // Collect then-block: all instructions until 'otherwise' at start of a new line or end
            List<Instruction> thenInstrs = new ArrayList<>();
            while (pos < tokens.size() && !(tokens.get(pos).value.equals("otherwise") && tokens.get(pos).lineNumber != line)) {
                thenInstrs.add(parseInstructionWithBlocks());
            }
            // Collect else-block if 'otherwise' is present at start of a new line
            List<Instruction> elseInstrs = new ArrayList<>();
            if (pos < tokens.size() && tokens.get(pos).value.equals("otherwise") && tokens.get(pos).lineNumber != line) {
                int otherwiseLine = tokens.get(pos).lineNumber;
                pos++;
                while (pos < tokens.size() && tokens.get(pos).lineNumber != otherwiseLine) {
                    elseInstrs.add(parseInstructionWithBlocks());
                }
                // Also collect remaining instructions as else block
                while (pos < tokens.size()) {
                    elseInstrs.add(parseInstructionWithBlocks());
                }
            }
            return new IfInstruction(cond.toString().trim(), thenInstrs, elseInstrs, line);
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
}
