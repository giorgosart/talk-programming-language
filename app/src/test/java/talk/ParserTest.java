package talk;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.AfterAll;
import java.util.Arrays;
import java.util.List;
import java.io.InputStream;

import talk.core.Instruction;
import talk.Parser;
import talk.core.Tokenizer;
import talk.exception.TalkSyntaxException;
import talk.expression.ListValue;
import talk.instruction.AppendToFileInstruction;
import talk.instruction.AskInstruction;
import talk.instruction.AssignmentInstruction;
import talk.instruction.AttemptInstruction;
import talk.instruction.DeleteFileInstruction;
import talk.instruction.FunctionCallInstruction;
import talk.instruction.FunctionDefinitionInstruction;
import talk.instruction.IfInstruction;
import talk.instruction.ReadFileInstruction;
import talk.instruction.RepeatInstruction;
import talk.instruction.ReturnInstruction;
import talk.instruction.VariableInstruction;
import talk.instruction.WriteInstruction;

import static org.junit.jupiter.api.Assertions.*;
public class ParserTest {

    private static InputStream originalIn;

    @BeforeAll
    static void mockSystemIn() {
        originalIn = System.in;
        System.setIn(new java.io.ByteArrayInputStream("dummy\ndummy\ndummy\n".getBytes()));
    }

    @AfterAll
    static void restoreSystemIn() {
        System.setIn(originalIn);
    }

    private void printInstructions(List<Instruction> instructions, String indent) {
        for (Instruction instr : instructions) {
            System.out.println(indent + instr.getClass().getSimpleName() + ": " + instr);
            if (instr instanceof IfInstruction) {
                IfInstruction ifInstr = (IfInstruction) instr;
                System.out.println(indent + "  THEN:");
                printInstructions(ifInstr.getThenInstructions(), indent + "    ");
                System.out.println(indent + "  ELSE:");
                printInstructions(ifInstr.getElseInstructions(), indent + "    ");
            }
        }
    }

    @Test
    void testVariableDeclaration() {
        List<Tokenizer.Token> tokens = Arrays.asList(
            new Tokenizer.Token("variable", 1),
            new Tokenizer.Token("x", 1)
        );
        Parser parser = new Parser(tokens);
        List<Instruction> instructions = parser.parse();
        assertEquals(1, instructions.size());
        assertTrue(instructions.get(0) instanceof VariableInstruction);
        assertEquals("x", ((VariableInstruction) instructions.get(0)).getName());
    }

    @Test
    void testAssignment() {
        List<Tokenizer.Token> tokens = Arrays.asList(
            new Tokenizer.Token("set", 2),
            new Tokenizer.Token("x", 2),
            new Tokenizer.Token("to", 2),
            new Tokenizer.Token("10", 2)
        );
        Parser parser = new Parser(tokens);
        List<Instruction> instructions = parser.parse();
        assertEquals(1, instructions.size());
        assertTrue(instructions.get(0) instanceof AssignmentInstruction);
        assertEquals("x", ((AssignmentInstruction) instructions.get(0)).getVariableName());
        assertEquals("10", ((AssignmentInstruction) instructions.get(0)).getValue());
    }

    @Test
    void testIfInstruction() {
        List<Tokenizer.Token> tokens = Arrays.asList(
            new Tokenizer.Token("if", 3),
            new Tokenizer.Token("x is greater than 10", 3),
            new Tokenizer.Token("then", 3)
        );
        Parser parser = new Parser(tokens);
        List<Instruction> instructions = parser.parse();
        assertEquals(1, instructions.size());
        assertTrue(instructions.get(0) instanceof IfInstruction);
        assertTrue(((IfInstruction) instructions.get(0)).getCondition().contains("greater"));
    }

    @Test
    void testAskInstruction() {
        List<Tokenizer.Token> tokens = Arrays.asList(
            new Tokenizer.Token("ask", 4),
            new Tokenizer.Token("What is your name?", 4),
            new Tokenizer.Token("and", 4),
            new Tokenizer.Token("store", 4),
            new Tokenizer.Token("in", 4),
            new Tokenizer.Token("name", 4)
        );
        Parser parser = new Parser(tokens);
        List<Instruction> instructions = parser.parse();
        assertEquals(1, instructions.size());
        assertTrue(instructions.get(0) instanceof AskInstruction);
        assertEquals("name", ((AskInstruction) instructions.get(0)).getVariableName());
    }

    @Test
    void testSyntaxError() {
        List<Tokenizer.Token> tokens = Arrays.asList(
            new Tokenizer.Token("foo", 5)
        );
        Parser parser = new Parser(tokens);
        Exception exception = assertThrows(TalkSyntaxException.class, parser::parse);
        assertTrue(exception.getMessage().contains("line 5"));
    }

    @Test
    void testParseRepeatLoop() {
        Tokenizer tokenizer = new Tokenizer();
        List<String> lines = Arrays.asList(
            "repeat 3 times",
            "    write _index in log.txt"
        );
        List<Tokenizer.Token> tokens = tokenizer.tokenize(lines);
        Parser parser = new Parser(tokens);
        List<Instruction> instructions = parser.parse();
        assertEquals(1, instructions.size());
        assertTrue(instructions.get(0) instanceof RepeatInstruction);
        RepeatInstruction repeat = (RepeatInstruction) instructions.get(0);
        assertEquals("3", repeat.getCountExpr());
        assertEquals(1, repeat.getBody().size());
        assertTrue(repeat.getBody().get(0) instanceof WriteInstruction);
    }

    @Test
    void testIfWithAndOrLogic() {
        List<Tokenizer.Token> tokens = Arrays.asList(
            new Tokenizer.Token("if", 1),
            new Tokenizer.Token("x is equal to 1", 1),
            new Tokenizer.Token("AND", 1),
            new Tokenizer.Token("y is equal to 2", 1),
            new Tokenizer.Token("OR", 1),
            new Tokenizer.Token("z is equal to 3", 1),
            new Tokenizer.Token("then", 1)
        );
        Parser parser = new Parser(tokens);
        List<Instruction> instructions = parser.parse();
        assertEquals(1, instructions.size());
        assertTrue(instructions.get(0) instanceof IfInstruction);
        String cond = ((IfInstruction) instructions.get(0)).getCondition();
        assertTrue(cond.contains("AND"));
        assertTrue(cond.contains("OR"));
        assertTrue(cond.contains("x is equal to 1"));
        assertTrue(cond.contains("y is equal to 2"));
        assertTrue(cond.contains("z is equal to 3"));
    }

    @Test
    void testIfWithNotPrefix() {
        List<Tokenizer.Token> tokens = Arrays.asList(
            new Tokenizer.Token("if", 1),
            new Tokenizer.Token("NOT", 1),
            new Tokenizer.Token("x is equal to 1", 1),
            new Tokenizer.Token("then", 1)
        );
        Parser parser = new Parser(tokens);
        List<Instruction> instructions = parser.parse();
        assertEquals(1, instructions.size());
        assertTrue(instructions.get(0) instanceof IfInstruction);
        String cond = ((IfInstruction) instructions.get(0)).getCondition();
        assertTrue(cond.startsWith("NOT"));
        assertTrue(cond.contains("x is equal to 1"));
    }

    @Test
    void testMalformedIfCondition() {
        List<Tokenizer.Token> tokens = Arrays.asList(
            new Tokenizer.Token("if", 1),
            new Tokenizer.Token("AND", 1), // Malformed: AND with no left operand
            new Tokenizer.Token("then", 1)
        );
        Parser parser = new Parser(tokens);
        List<Instruction> instructions = parser.parse();
        assertEquals(1, instructions.size());
        assertTrue(instructions.get(0) instanceof IfInstruction);
        String cond = ((IfInstruction) instructions.get(0)).getCondition();
        // The parser does not throw, but the condition string will be malformed
        assertTrue(cond.startsWith("AND"));
    }

    @Test
    void testFunctionDefinitionAndCall() {
        List<Tokenizer.Token> tokens = Arrays.asList(
            new Tokenizer.Token("DEFINE", 1),
            new Tokenizer.Token("myFunc", 1),
            new Tokenizer.Token("INDENT", 2),
            new Tokenizer.Token("write", 2),
            new Tokenizer.Token("\"Hello\"", 2),
            new Tokenizer.Token("in", 2),
            new Tokenizer.Token("log.txt", 2),
            new Tokenizer.Token("DEDENT", 3),
            new Tokenizer.Token("CALL", 4),
            new Tokenizer.Token("myFunc", 4)
        );
        Parser parser = new Parser(tokens);
        List<Instruction> instructions = parser.parse();
        assertEquals(2, instructions.size());
        assertTrue(instructions.get(0) instanceof FunctionDefinitionInstruction);
        FunctionDefinitionInstruction def = (FunctionDefinitionInstruction) instructions.get(0);
        assertEquals("myFunc", def.getFunctionName());
        assertEquals(1, def.getBody().size());
        assertTrue(def.getBody().get(0) instanceof WriteInstruction);
        assertTrue(instructions.get(1) instanceof FunctionCallInstruction);
        FunctionCallInstruction call = (FunctionCallInstruction) instructions.get(1);
        assertEquals("myFunc", call.getFunctionName());
    }

    @Test
    void testAssignmentWithListValue() {
        Tokenizer tokenizer = new Tokenizer();
        List<String> lines = List.of(
            "variable items equals apple, banana and cherry",
            "set items to orange, lemon and lime"
        );
        List<Tokenizer.Token> tokens = tokenizer.tokenize(lines);
        Parser parser = new Parser(tokens);
        List<Instruction> instructions = parser.parse();
        assertEquals(2, instructions.size());
        assertTrue(instructions.get(0) instanceof VariableInstruction);
        Object val = ((VariableInstruction) instructions.get(0)).getValue();
        assertTrue(val instanceof ListValue);
        ListValue list = (ListValue) val;
        assertEquals(List.of("apple", "banana", "cherry"), list.getItems());
        assertTrue(instructions.get(1) instanceof AssignmentInstruction);
        Object val2 = ((AssignmentInstruction) instructions.get(1)).getValue();
        assertTrue(val2 instanceof ListValue);
        ListValue list2 = (ListValue) val2;
        assertEquals(List.of("orange", "lemon", "lime"), list2.getItems());
    }

    @Test
    void testRepeatForEachList() {
        Tokenizer tokenizer = new Tokenizer();
        List<String> lines = List.of(
            "repeat for each fruit in fruits",
            "    write fruit in log.txt"
        );
        List<Tokenizer.Token> tokens = tokenizer.tokenize(lines);
        Parser parser = new Parser(tokens);
        List<Instruction> instructions = parser.parse();
        assertEquals(1, instructions.size());
        assertTrue(instructions.get(0) instanceof RepeatInstruction);
        RepeatInstruction ri = (RepeatInstruction) instructions.get(0);
        assertEquals("fruit", ri.getItemVar());
        assertEquals("fruits", ri.getListVar());
        assertEquals(1, ri.getBody().size());
        assertTrue(ri.getBody().get(0) instanceof WriteInstruction);
    }

    @Test
    void testReadFileInstruction() {
        List<Tokenizer.Token> tokens = Arrays.asList(
            new Tokenizer.Token("read", 1),
            new Tokenizer.Token("file", 1),
            new Tokenizer.Token("data.txt", 1),
            new Tokenizer.Token("into", 1),
            new Tokenizer.Token("content", 1)
        );
        Parser parser = new Parser(tokens);
        List<Instruction> instructions = parser.parse();
        assertEquals(1, instructions.size());
        assertTrue(instructions.get(0) instanceof ReadFileInstruction);
        ReadFileInstruction instr = (ReadFileInstruction) instructions.get(0);
        assertEquals("data.txt", instr.getFileName());
        assertEquals("content", instr.getVariableName());
    }

    @Test
    void testAppendToFileInstruction() {
        List<Tokenizer.Token> tokens = Arrays.asList(
            new Tokenizer.Token("append", 1),
            new Tokenizer.Token("hello world", 1), // Now expects unquoted value
            new Tokenizer.Token("to", 1),
            new Tokenizer.Token("log.txt", 1)
        );
        Parser parser = new Parser(tokens);
        List<Instruction> instructions = parser.parse();
        assertEquals(1, instructions.size());
        assertTrue(instructions.get(0) instanceof AppendToFileInstruction);
        AppendToFileInstruction instr = (AppendToFileInstruction) instructions.get(0);
        assertEquals("hello world", instr.getText());
        assertEquals("log.txt", instr.getFileName());
    }

    @Test
    void testDeleteFileInstruction() {
        List<Tokenizer.Token> tokens = Arrays.asList(
            new Tokenizer.Token("delete", 1),
            new Tokenizer.Token("file", 1),
            new Tokenizer.Token("temp.txt", 1)
        );
        Parser parser = new Parser(tokens);
        List<Instruction> instructions = parser.parse();
        assertEquals(1, instructions.size());
        assertTrue(instructions.get(0) instanceof DeleteFileInstruction);
        DeleteFileInstruction instr = (DeleteFileInstruction) instructions.get(0);
        assertEquals("temp.txt", instr.getFileName());
    }

    @Test
    void testParameterizedFunctionDefinition() {
        List<Tokenizer.Token> tokens = Arrays.asList(
            new Tokenizer.Token("DEFINE", 1),
            new Tokenizer.Token("sum", 1),
            new Tokenizer.Token("a", 1),
            new Tokenizer.Token("b", 1),
            new Tokenizer.Token("INDENT", 2),
            new Tokenizer.Token("set", 2),
            new Tokenizer.Token("result", 2),
            new Tokenizer.Token("to", 2),
            new Tokenizer.Token("a + b", 2),
            new Tokenizer.Token("DEDENT", 3)
        );
        Parser parser = new Parser(tokens);
        List<Instruction> instructions = parser.parse();
        assertEquals(1, instructions.size());
        assertTrue(instructions.get(0) instanceof FunctionDefinitionInstruction);
        FunctionDefinitionInstruction def = (FunctionDefinitionInstruction) instructions.get(0);
        assertEquals("sum", def.getFunctionName());
        assertEquals(List.of("a", "b"), def.getParameters());
        assertEquals(1, def.getBody().size());
        assertTrue(def.getBody().get(0) instanceof AssignmentInstruction);
    }

    @Test
    void testFunctionCallWithArguments() {
        List<Tokenizer.Token> tokens = Arrays.asList(
            new Tokenizer.Token("CALL", 1),
            new Tokenizer.Token("sum", 1),
            new Tokenizer.Token("with", 1),
            new Tokenizer.Token("5", 1),
            new Tokenizer.Token("10", 1)
        );
        Parser parser = new Parser(tokens);
        List<Instruction> instructions = parser.parse();
        assertEquals(1, instructions.size());
        assertTrue(instructions.get(0) instanceof FunctionCallInstruction);
        FunctionCallInstruction call = (FunctionCallInstruction) instructions.get(0);
        assertEquals("sum", call.getFunctionName());
        assertEquals(List.of("5", "10"), call.getArguments());
    }

    @Test
    void testFunctionCallWithInto() {
        List<Tokenizer.Token> tokens = Arrays.asList(
            new Tokenizer.Token("call", 1),
            new Tokenizer.Token("myfunc", 1),
            new Tokenizer.Token("with", 1),
            new Tokenizer.Token("5", 1),
            new Tokenizer.Token("into", 1),
            new Tokenizer.Token("result", 1)
        );
        Parser parser = new Parser(tokens);
        List<Instruction> instructions = parser.parse();
        assertEquals(1, instructions.size());
        assertTrue(instructions.get(0) instanceof FunctionCallInstruction);
        FunctionCallInstruction fci = (FunctionCallInstruction) instructions.get(0);
        assertEquals("myfunc", fci.getFunctionName());
        assertEquals(1, fci.getArguments().size());
        assertEquals("5", fci.getArguments().get(0));
        assertEquals("result", fci.getIntoVariable());
    }

    @Test
    void testReturnInstructionParsing() {
        List<Tokenizer.Token> tokens = Arrays.asList(
            new Tokenizer.Token("return", 1),
            new Tokenizer.Token("x + 1", 1)
        );
        Parser parser = new Parser(tokens);
        List<Instruction> instructions = parser.parse();
        assertEquals(1, instructions.size());
        assertTrue(instructions.get(0) instanceof ReturnInstruction);
        ReturnInstruction ret = (ReturnInstruction) instructions.get(0);
        assertEquals("x + 1", ret.getExpression());
    }

    @Test
    void testAttemptWithIfThatFailsIndentedCatch() {
        Tokenizer tokenizer = new Tokenizer();
        List<String> lines = List.of(
            "attempt",
            "    write 'try' in log.txt",
            "if that fails",
            "    write 'catch' in log.txt"
        );
        List<Tokenizer.Token> tokens = tokenizer.tokenize(lines);
        Parser parser = new Parser(tokens);
        List<Instruction> instructions = parser.parse();
        assertEquals(1, instructions.size());
        assertTrue(instructions.get(0) instanceof AttemptInstruction);
        AttemptInstruction ai = (AttemptInstruction) instructions.get(0);
        assertEquals(1, ai.getTryBlock().size());
        assertEquals(1, ai.getCatchBlock().size());
        assertTrue(ai.getTryBlock().get(0) instanceof WriteInstruction);
        assertTrue(ai.getCatchBlock().get(0) instanceof WriteInstruction);
    }

    @Test
    void testAttemptWithIfThatFailsNonIndentedCatch() {
        Tokenizer tokenizer = new Tokenizer();
        List<String> lines = List.of(
            "attempt",
            "    write 'try' in log.txt",
            "if that fails",
            "write 'catch' in log.txt"
        );
        List<Tokenizer.Token> tokens = tokenizer.tokenize(lines);
        Parser parser = new Parser(tokens);
        List<Instruction> instructions = parser.parse();
        assertEquals(1, instructions.size());
        assertTrue(instructions.get(0) instanceof AttemptInstruction);
        AttemptInstruction ai = (AttemptInstruction) instructions.get(0);
        assertEquals(1, ai.getTryBlock().size());
        assertEquals(1, ai.getCatchBlock().size());
        assertTrue(ai.getTryBlock().get(0) instanceof WriteInstruction);
        assertTrue(ai.getCatchBlock().get(0) instanceof WriteInstruction);
    }

    @Test
    void testOtherwiseBlockBoundary() {
        Tokenizer tokenizer = new Tokenizer();
        List<String> lines = List.of(
            "if x then",
            "    write 'yes' in log.txt",
            "otherwise",
            "    write 'no' in log.txt"
        );
        List<Tokenizer.Token> tokens = tokenizer.tokenize(lines);
        System.out.println("[TEST DEBUG] Tokens:");
        for (int i = 0; i < tokens.size(); i++) {
            System.out.println(i + ": " + tokens.get(i).value + " (line " + tokens.get(i).lineNumber + ")");
        }
        Parser parser = new Parser(tokens);
        List<Instruction> instructions = parser.parse();
        
        // Print for debugging
        System.out.println("[TEST DEBUG] Number of instructions: " + instructions.size());
        for (int i = 0; i < instructions.size(); i++) {
            System.out.println("[TEST DEBUG] Instruction #" + i + ": " + instructions.get(i).getClass().getSimpleName());
        }
        
        assertEquals(1, instructions.size());
        assertTrue(instructions.get(0) instanceof IfInstruction);
        IfInstruction ifi = (IfInstruction) instructions.get(0);
        assertEquals(1, ifi.getThenInstructions().size());
        assertEquals(1, ifi.getElseInstructions().size());
        assertTrue(ifi.getElseInstructions().get(0) instanceof WriteInstruction);
    }

    @Test
    void testBlockBoundaryAtDedent() {
        List<Tokenizer.Token> tokens = List.of(
            new Tokenizer.Token("INDENT", 1),
            new Tokenizer.Token("DEDENT", 2)
        );
        Parser parser = new Parser(tokens);
        List<Instruction> instructions = parser.parse();
        assertTrue(instructions.isEmpty());
    }

    @Test
    void testFunctionCallWithOnlyInto() {
        List<Tokenizer.Token> tokens = List.of(
            new Tokenizer.Token("call", 1),
            new Tokenizer.Token("myfunc", 1),
            new Tokenizer.Token("into", 1),
            new Tokenizer.Token("result", 1)
        );
        Parser parser = new Parser(tokens);
        List<Instruction> instructions = parser.parse();
        assertEquals(1, instructions.size());
        assertTrue(instructions.get(0) instanceof FunctionCallInstruction);
        FunctionCallInstruction fci = (FunctionCallInstruction) instructions.get(0);
        assertEquals("myfunc", fci.getFunctionName());
        assertEquals("result", fci.getIntoVariable());
        assertTrue(fci.getArguments().isEmpty());
    }

    @Test
    void testReturnWithNoExpression() {
        List<Tokenizer.Token> tokens = List.of(
            new Tokenizer.Token("return", 1)
        );
        Parser parser = new Parser(tokens);
        List<Instruction> instructions = parser.parse();
        assertEquals(1, instructions.size());
        assertTrue(instructions.get(0) instanceof ReturnInstruction);
        ReturnInstruction ret = (ReturnInstruction) instructions.get(0);
        assertEquals("", ret.getExpression());
    }

    @Test
    void testParseInstructionMissingThenThrows() {
        List<Tokenizer.Token> tokens = List.of(
            new Tokenizer.Token("if", 1),
            new Tokenizer.Token("x", 1)
        );
        Parser parser = new Parser(tokens);
        Exception ex = assertThrows(RuntimeException.class, parser::parse);
        assertTrue(ex.getMessage().contains("Expected 'then'"));
    }

    @Test
    void testParseRepeatMissingTimesThrows() {
        List<Tokenizer.Token> tokens = List.of(
            new Tokenizer.Token("repeat", 1),
            new Tokenizer.Token("3", 1)
        );
        Parser parser = new Parser(tokens);
        Exception ex = assertThrows(RuntimeException.class, parser::parse);
        assertTrue(ex.getMessage().contains("Expected 'times'"));
    }

    @Test
    void testParseReadFileMissingIntoThrows() {
        List<Tokenizer.Token> tokens = List.of(
            new Tokenizer.Token("read", 1),
            new Tokenizer.Token("file", 1),
            new Tokenizer.Token("data.txt", 1)
        );
        Parser parser = new Parser(tokens);
        Exception ex = assertThrows(RuntimeException.class, parser::parse);
        assertTrue(ex.getMessage().contains("Expected 'into'"));
    }
}
