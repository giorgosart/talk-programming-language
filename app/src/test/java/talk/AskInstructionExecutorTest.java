package talk;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class AskInstructionExecutorTest {
    // Note: Interactive input can't be tested in a standard unit test without mocking System.in.
    // This is a placeholder to show where such a test would go.
    @Test
    void testAskStoresValue() {
        RuntimeContext ctx = new RuntimeContext();
        // Provide input for the ask instruction
        java.io.InputStream in = new java.io.ByteArrayInputStream("Alice\n".getBytes());
        InstructionExecutor exec = new InstructionExecutor(ctx, in);
        AskInstruction ask = new AskInstruction("What is your name?", "name", 1);
        exec.execute(ask);
        assertEquals("Alice", ctx.getVariable("name"));
    }
}
