package talk;

import java.util.ArrayList;
import java.util.List;

public class Parser {
    private final List<Tokenizer.Token> tokens;
    private int pos = 0;
    private final InstructionFactory instructionFactory = new InstructionFactory();

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

    // Helper to look ahead for 'if that fails' sequence
    private boolean peekIfThatFails() {
        return peek("if") && (pos + 2 < tokens.size()) &&
            tokens.get(pos + 1).value.equals("that") &&
            tokens.get(pos + 2).value.equals("fails");
    }

    private Instruction parseInstructionWithIndent() {
        if (pos >= tokens.size()) return null;
        Tokenizer.Token token = tokens.get(pos);
        String value = token.value;
        int line = token.lineNumber;
        if ("INDENT".equals(value)) {
            pos++;
            List<Instruction> block = parseIndentedBlockWithParentIndent(getIndentLevel(pos > 0 ? pos - 1 : 0));
            return new BlockInstruction(block, line);
        }
        if ("DEDENT".equals(value)) {
            pos++;
            return null;
        }
        if ("otherwise".equals(value)) {
            // Do not consume 'otherwise'; let parseIfInstruction handle it as the else block
            return null;
        }
        if ("if".equals(value)) {
            pos++;
            return parseIfInstruction(line);
        }
        if ("attempt".equals(value)) {
            pos++;
            List<Instruction> tryBlock = parseIndentedBlockWithParentIndent(getIndentLevel(pos > 0 ? pos - 1 : 0));
            if (peek("DEDENT")) pos++;
            List<Instruction> catchBlock = new ArrayList<>();
            if (peekIfThatFails()) {
                pos += 3; // skip 'if', 'that', 'fails'
                int fallbackIndent = getIndentLevel(pos > 0 ? pos - 1 : 0);
                System.out.println("[PARSER DEBUG] Parsing fallback/catch block after 'if that fails' at pos=" + pos + ", fallbackIndent=" + fallbackIndent);
                if (peek("INDENT")) {
                    pos++;
                    catchBlock = parseIndentedBlockWithParentIndent(fallbackIndent);
                } else {
                    while (pos < tokens.size() && !peek("DEDENT") && !isBlockBoundaryAtIndent(fallbackIndent)) {
                        Instruction instr = parseInstructionWithIndent();
                        if (instr != null) catchBlock.add(instr);
                    }
                    if (peek("DEDENT")) pos++;
                }
                System.out.println("[PARSER DEBUG] Finished parsing catch block, size: " + catchBlock.size());
                for (Instruction instr : catchBlock) {
                    System.out.println("[PARSER DEBUG] Catch block instruction: " + instr.getClass().getSimpleName() + ": " + instr);
                }
            }
            System.out.println("[PARSER DEBUG] Returning AttemptInstruction: tryBlock size=" + tryBlock.size() + ", catchBlock size=" + catchBlock.size());
            return new AttemptInstruction(tryBlock, catchBlock, line);
        }
        if ("repeat".equals(value)) {
            return parseRepeatInstruction(line);
        }
        if ("DEFINE".equals(value)) {
            return parseFunctionDefinition(line);
        }
        if ("call".equalsIgnoreCase(value)) {
            return parseFunctionCall(line);
        }
        if ("return".equals(value)) {
            return parseReturn(line);
        }
        // fallback to original parseInstruction for all other cases
        return parseInstruction();
    }

    private List<Instruction> parseIndentedBlock() {
        return parseIndentedBlockWithParentIndent(getIndentLevel(pos > 0 ? pos - 1 : 0));
    }

    // Enhanced: Accepts parentIndent to handle 'otherwise' and block keywords only at correct indentation
    private List<Instruction> parseIndentedBlockWithParentIndent(int parentIndent) {
        List<Instruction> block = new ArrayList<>();
        if (peek("INDENT")) {
            pos++;
            while (pos < tokens.size() && !peek("DEDENT")) {
                // Break if a block boundary keyword appears at the parent indentation level
                if (isBlockBoundaryAtIndent(parentIndent) || peekIfThatFails()) {
                    System.out.println("[PARSER DEBUG] Breaking block at boundary keyword '" + tokens.get(pos).value + "' at indent " + getIndentLevel(pos > 0 ? pos - 1 : 0) + " (parent: " + parentIndent + ") pos=" + pos);
                    if (block.isEmpty()) {
                        pos++;
                    }
                    break;
                }
                Instruction instr = parseInstructionWithIndent();
                if (instr != null) block.add(instr);
            }
            if (peek("DEDENT")) pos++;
        } else {
            while (pos < tokens.size() && !peek("DEDENT")) {
                if (isBlockBoundaryAtIndent(parentIndent) || peekIfThatFails()) {
                    System.out.println("[PARSER DEBUG] Breaking block at boundary keyword '" + tokens.get(pos).value + "' at indent " + getIndentLevel(pos > 0 ? pos - 1 : 0) + " (parent: " + parentIndent + ") pos=" + pos);
                    if (block.isEmpty()) {
                        pos++;
                    }
                    break;
                }
                Instruction instr = parseInstructionWithIndent();
                if (instr != null) block.add(instr);
            }
        }
        System.out.println("[PARSER DEBUG] Finished block at indent " + parentIndent + ", block size: " + block.size());
        return block;
    }

    // Helper: returns true if the current token is a block boundary keyword at the given indentation level
    private boolean isBlockBoundaryAtIndent(int indentLevel) {
        if (pos >= tokens.size()) return false;
        String val = tokens.get(pos).value;
        // List of block boundary keywords
        if (val.equals("otherwise") || val.equals("if") || val.equals("attempt") || val.equals("repeat") || val.equals("DEFINE") || val.equals("call") || val.equals("return")) {
            int tokenIndent = getIndentLevel(pos > 0 ? pos - 1 : 0);
            boolean isBoundary = tokenIndent == indentLevel;
            if (isBoundary) {
                System.out.println("[PARSER DEBUG] Block boundary detected: '" + val + "' at indent " + tokenIndent + " (parent: " + indentLevel + ") pos=" + pos);
            }
            return isBoundary;
        }
        // Also treat DEDENT as a block boundary at the parent indent
        if (val.equals("DEDENT")) {
            int tokenIndent = getIndentLevel(pos > 0 ? pos - 1 : 0);
            if (tokenIndent <= indentLevel) {
                System.out.println("[PARSER DEBUG] DEDENT block boundary at indent " + tokenIndent + " (parent: " + indentLevel + ") pos=" + pos);
                return true;
            }
        }
        return false;
    }

    private Instruction parseIfInstruction(int line) {
        int ifIndent = (pos > 0) ? getIndentLevel(pos - 1) : 0;
        StringBuilder cond = new StringBuilder();
        while (pos < tokens.size() && !"then".equals(tokens.get(pos).value)) {
            cond.append(tokens.get(pos).value).append(" ");
            pos++;
        }
        if (peek("then")) pos++;
        List<Instruction> thenInstrs = parseIndentedBlockWithParentIndent(ifIndent);
        while (peek("DEDENT")) pos++;
        List<Instruction> elseInstrs = new ArrayList<>();
        if (peek("otherwise")) {
            int otherwiseIndent = (pos > 0) ? getIndentLevel(pos - 1) : 0;
            if (otherwiseIndent == ifIndent) {
                pos++;
                elseInstrs = parseIndentedBlockWithParentIndent(ifIndent);
            }
        }
        return new IfInstruction(cond.toString().trim(), thenInstrs, elseInstrs, line);
    }

    /**
     * Utility to parse a list of identifiers or values until a stop token.
     * Used for both function parameters and arguments.
     */
    private List<String> parseIdentifiersOrValuesUntil(String... stopTokens) {
        List<String> result = new ArrayList<>();
        outer: while (pos < tokens.size()) {
            for (String stop : stopTokens) {
                if (peek(stop)) break outer;
            }
            Tokenizer.Token token = tokens.get(pos);
            // Accept identifiers, quoted strings, or numbers
            if (token.value.matches("[a-zA-Z_][a-zA-Z0-9_]*") || token.value.matches("\".*\"") || token.value.matches("-?\\d+(\\.\\d+)?")) {
                result.add(token.value);
                pos++;
            } else {
                break;
            }
        }
        return result;
    }

    // Helper to get indentation for a given token index by scanning backwards for INDENT/DEDENT
    private int getIndentLevel(int tokenIndex) {
        int indent = 0;
        for (int i = 0; i <= tokenIndex; i++) {
            String val = tokens.get(i).value;
            if ("INDENT".equals(val)) indent++;
            else if ("DEDENT".equals(val)) indent--;
        }
        return indent;
    }

    // Handler for parsing repeat-instruction
    private Instruction parseRepeatInstruction(int line) {
        pos++;
        if (peek("for")) {
            pos++;
            expect("each");
            String itemVar = expectIdentifier();
            expect("in");
            String listVar = expectIdentifier();
            List<Instruction> body = parseIndentedBlockWithParentIndent(getIndentLevel(pos > 0 ? pos - 1 : 0));
            return new RepeatInstruction(itemVar, listVar, body, line);
        }
        StringBuilder countExpr = new StringBuilder();
        while (pos < tokens.size() && !"times".equals(tokens.get(pos).value)) {
            countExpr.append(tokens.get(pos).value).append(" ");
            pos++;
        }
        if (!peek("times")) {
            throw new RuntimeException("Syntax error at line " + line + ": Expected 'times' after repeat count");
        }
        pos++; // skip 'times'
        List<Instruction> body = parseIndentedBlockWithParentIndent(getIndentLevel(pos > 0 ? pos - 1 : 0));
        return new RepeatInstruction(countExpr.toString().trim(), body, line);
    }

    // Handler for parsing function definition
    private Instruction parseFunctionDefinition(int line) {
        pos++;
        String functionName = expectIdentifier();
        List<String> parameters = parseIdentifiersOrValuesUntil("INDENT", "DEDENT", "NEWLINE");
        List<Instruction> body = parseIndentedBlockWithParentIndent(getIndentLevel(pos > 0 ? pos - 1 : 0));
        if (body.isEmpty()) {
            throw new RuntimeException("Syntax error at line " + line + ": Function definition requires an indented block");
        }
        return new FunctionDefinitionInstruction(functionName, parameters, body, line);
    }

    // Handler for parsing function call
    private Instruction parseFunctionCall(int line) {
        pos++;
        String functionName = expectIdentifier();
        List<String> arguments = new ArrayList<>();
        if (peek("with")) {
            pos++;
            arguments = parseIdentifiersOrValuesUntil("INDENT", "DEDENT", "NEWLINE", "into");
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

    // Handler for parsing return
    private Instruction parseReturn(int line) {
        pos++;
        String expr = "";
        if (pos < tokens.size() && !tokens.get(pos).value.equals("INDENT") && !tokens.get(pos).value.equals("DEDENT") && !tokens.get(pos).value.equals("NEWLINE")) {
            expr = tokens.get(pos).value;
            pos++;
        }
        return new ReturnInstruction(expr, line);
    }

    private Instruction parseInstruction() {
        if (pos >= tokens.size()) return null;
        Tokenizer.Token token = tokens.get(pos);
        String value = token.value;
        int line = token.lineNumber;
        // Use registry/factory for all mapped instructions
        if (instructionFactory.isRegistered(value)) {
            // Gather arguments for the instruction context
            String identifier = null;
            Object val = null;
            switch (value) {
                case "variable":
                    pos++;
                    identifier = expectIdentifier();
                    if (peek("equal") || peek("equals")) {
                        pos++;
                        if (peek("LIST_START")) {
                            pos++;
                            List<String> items = new ArrayList<>();
                            while (!peek("LIST_END")) items.add(expectValue());
                            pos++; // skip LIST_END
                            val = new ListValue(items);
                        } else {
                            val = expectValue();
                        }
                    }
                    break;
                case "set":
                    pos++;
                    identifier = expectIdentifier();
                    expect("to");
                    if (peek("LIST_START")) {
                        pos++;
                        List<String> items = new ArrayList<>();
                        while (!peek("LIST_END")) items.add(expectValue());
                        pos++; // skip LIST_END
                        val = new ListValue(items);
                    } else {
                        val = expectValue();
                    }
                    break;
                case "write":
                    pos++;
                    val = expectValue();
                    expect("in");
                    identifier = expectIdentifier();
                    break;
                case "ask":
                    pos++;
                    val = expectValue();
                    expect("and");
                    expect("store");
                    expect("in");
                    identifier = expectIdentifier();
                    break;
                case "create":
                    pos++;
                    expect("file");
                    expect("as");
                    identifier = expectValue();
                    break;
                case "append":
                    pos++;
                    val = expectValue();
                    expect("to");
                    identifier = expectIdentifier();
                    break;
                case "delete":
                    if (peekNext("file")) {
                        pos++;
                        pos++;
                        identifier = expectValue();
                    }
                    break;
                case "log":
                    pos++;
                    val = expectValue();
                    break;
            }
            return instructionFactory.create(value, new InstructionContext(identifier, val, line));
        }
        if ("read".equals(value) && peekNext("file")) {
            pos++;
            pos++;
            String fileName = expectValue();
            expect("into");
            String variableName = expectIdentifier();
            return new ReadFileInstruction(fileName, variableName, line);
        }
        throw new RuntimeException("Syntax error at line " + line + ": Unknown instruction '" + value + "'");
    }

    // Helper to parse a value or a list value (e.g. a, b and c)
    private Object parseValueOrList() {
        List<String> items = new ArrayList<>();
        while (pos < tokens.size()) {
            String val = tokens.get(pos).value;
            if (val.equals(",")) {
                pos++;
                continue;
            }
            if (val.equals("and")) {
                pos++;
                if (pos < tokens.size()) {
                    items.add(tokens.get(pos).value);
                    pos++;
                }
                break;
            }
            // Stop at block or next instruction
            if (val.equals("INDENT") || val.equals("DEDENT") || val.equals("NEWLINE") || val.equals("to") || val.equals("in") || val.equals("as") || val.equals("file")) {
                break;
            }
            items.add(val);
            pos++;
        }
        if (items.size() == 1) return items.get(0);
        return new ListValue(items);
    }

    private boolean peek(String expected) {
        return pos < tokens.size() && tokens.get(pos).value.equals(expected);
    }

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
