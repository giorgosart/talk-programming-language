package talk;

import org.junit.jupiter.api.Test;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

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
}
