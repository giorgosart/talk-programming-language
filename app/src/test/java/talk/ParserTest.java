package talk;

import org.junit.jupiter.api.Test;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

import talk.RepeatInstruction;

public class ParserTest {
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
        Exception exception = assertThrows(RuntimeException.class, parser::parse);
        assertTrue(exception.getMessage().contains("Syntax error at line 5"));
    }

    @Test
    void testParseNestedIfWithIndentBlocks() {
        Tokenizer tokenizer = new Tokenizer();
        List<String> lines = Arrays.asList(
            "if x is greater than 10 then",
            "    write \"big\" in log.txt",
            "    if x is greater than 100 then",
            "        write \"huge\" in log.txt",
            "otherwise",
            "    write \"small\" in log.txt"
        );
        List<Tokenizer.Token> tokens = tokenizer.tokenize(lines);
        Parser parser = new Parser(tokens);
        List<Instruction> instructions = parser.parse();
        assertEquals(1, instructions.size());
        assertTrue(instructions.get(0) instanceof IfInstruction);
        IfInstruction outerIf = (IfInstruction) instructions.get(0);
        assertEquals(2, outerIf.getThenInstructions().size());
        assertTrue(outerIf.getThenInstructions().get(0) instanceof WriteInstruction);
        assertTrue(outerIf.getThenInstructions().get(1) instanceof IfInstruction);
        IfInstruction innerIf = (IfInstruction) outerIf.getThenInstructions().get(1);
        assertEquals(1, innerIf.getThenInstructions().size());
        assertTrue(innerIf.getThenInstructions().get(0) instanceof WriteInstruction);
        assertEquals(1, outerIf.getElseInstructions().size());
        assertTrue(outerIf.getElseInstructions().get(0) instanceof WriteInstruction);
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
}
