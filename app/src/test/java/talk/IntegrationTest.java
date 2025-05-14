package talk;

import org.junit.jupiter.api.Test;
import java.io.File;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class IntegrationTest {
    private static java.io.InputStream dummyIn = new java.io.ByteArrayInputStream("dummy\ndummy\ndummy\n".getBytes());

    @Test
    void testFullScriptExecution() throws Exception {
        // Simulate a .talk script: variable x equal 5; if x is greater than 3 then set x to 10; write x in out.txt
        List<String> lines = Arrays.asList(
            "variable x equal 5",
            "if x is greater than 3 then set x to 10",
            "write x in out.txt"
        );
        Tokenizer tokenizer = new Tokenizer();
        List<Tokenizer.Token> tokens = tokenizer.tokenize(lines);
        Parser parser = new Parser(tokens);
        List<Instruction> instructions = parser.parse();
        RuntimeContext ctx = new RuntimeContext();
        InstructionExecutor exec = new InstructionExecutor(ctx, dummyIn);
        for (Instruction instr : instructions) {
            exec.execute(instr);
        }
        assertEquals("10", ctx.getVariable("x").toString());
        File file = new File("out.txt");
        assertTrue(file.exists());
        String content = Files.readString(file.toPath());
        assertTrue(content.contains("10"));
        file.delete();
    }

    @Test
    void testErrorFallback() {
        // Simulate: attempt set y to 1 if that fails set y to 42
        AssignmentInstruction failAssign = new AssignmentInstruction("y", 1, 1);
        VariableInstruction fallback = new VariableInstruction("y", 42, 2);
        AttemptInstruction attempt = new AttemptInstruction(
            Arrays.asList(failAssign),
            Arrays.asList(fallback),
            1
        );
        RuntimeContext ctx = new RuntimeContext();
        InstructionExecutor exec = new InstructionExecutor(ctx, dummyIn);
        exec.execute(attempt);
        assertEquals(42, ctx.getVariable("y"));
    }
}
