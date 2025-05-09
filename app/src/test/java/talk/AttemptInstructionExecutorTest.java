package talk;

import org.junit.jupiter.api.Test;
import java.util.Arrays;
import static org.junit.jupiter.api.Assertions.*;

public class AttemptInstructionExecutorTest {
    @Test
    void testAttemptSuccess() {
        RuntimeContext ctx = new RuntimeContext();
        InstructionExecutor exec = new InstructionExecutor(ctx);
        VariableInstruction vi = new VariableInstruction("x", 1, 1);
        AssignmentInstruction ai = new AssignmentInstruction("x", 2, 2);
        AttemptInstruction attempt = new AttemptInstruction(
            Arrays.asList(vi, ai),
            Arrays.asList(new AssignmentInstruction("x", 99, 3)),
            1
        );
        exec.execute(attempt);
        assertEquals(2, ctx.getVariable("x"));
    }

    @Test
    void testAttemptFailure() {
        RuntimeContext ctx = new RuntimeContext();
        InstructionExecutor exec = new InstructionExecutor(ctx);
        // This will fail: assignment to undeclared variable
        AssignmentInstruction ai = new AssignmentInstruction("y", 2, 2);
        AttemptInstruction attempt = new AttemptInstruction(
            Arrays.asList(ai),
            Arrays.asList(new VariableInstruction("y", 42, 3)),
            1
        );
        exec.execute(attempt);
        assertEquals(42, ctx.getVariable("y"));
    }
}
