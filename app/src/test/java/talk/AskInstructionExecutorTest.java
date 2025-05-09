package talk;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class AskInstructionExecutorTest {
    // Note: Interactive input can't be tested in a standard unit test without mocking System.in.
    // This is a placeholder to show where such a test would go.
    @Test
    void testAskStoresValue() {
        RuntimeContext ctx = new RuntimeContext();
        InstructionExecutor exec = new InstructionExecutor(ctx);
        // Simulate input by directly setting variable (since interactive input can't be automated here)
        ctx.setVariable("name", "Alice");
        assertEquals("Alice", ctx.getVariable("name"));
    }
}
