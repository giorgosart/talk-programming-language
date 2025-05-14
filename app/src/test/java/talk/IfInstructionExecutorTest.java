package talk;

import org.junit.jupiter.api.Test;
import java.util.Collections;
import static org.junit.jupiter.api.Assertions.*;

public class IfInstructionExecutorTest {
    private static java.io.InputStream dummyIn = new java.io.ByteArrayInputStream("dummy\ndummy\ndummy\n".getBytes());

    @Test
    void testIfTrueBranch() {
        RuntimeContext ctx = new RuntimeContext();
        InstructionExecutor exec = new InstructionExecutor(ctx, dummyIn);
        VariableInstruction vi = new VariableInstruction("x", 10, 1);
        exec.execute(vi);
        IfInstruction ifInstr = new IfInstruction("x is greater than 5", Collections.singletonList(new AssignmentInstruction("x", 42, 2)), Collections.emptyList(), 2);
        exec.execute(ifInstr);
        assertEquals(42, ctx.getVariable("x"));
    }

    @Test
    void testIfFalseBranch() {
        RuntimeContext ctx = new RuntimeContext();
        InstructionExecutor exec = new InstructionExecutor(ctx, dummyIn);
        VariableInstruction vi = new VariableInstruction("x", 2, 1);
        exec.execute(vi);
        IfInstruction ifInstr = new IfInstruction("x is greater than 5", Collections.emptyList(), Collections.singletonList(new AssignmentInstruction("x", 99, 2)), 2);
        exec.execute(ifInstr);
        assertEquals(99, ctx.getVariable("x"));
    }
}
