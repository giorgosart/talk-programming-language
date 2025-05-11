package talk;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class InstructionExecutorTest {
    @Test
    void testVariableDeclaration() {
        RuntimeContext ctx = new RuntimeContext();
        InstructionExecutor exec = new InstructionExecutor(ctx);
        VariableInstruction vi = new VariableInstruction("x", null, 1);
        exec.execute(vi);
        assertTrue(ctx.hasVariable("x"));
        assertNull(ctx.getVariable("x"));
    }

    @Test
    void testVariableDeclarationWithValue() {
        RuntimeContext ctx = new RuntimeContext();
        InstructionExecutor exec = new InstructionExecutor(ctx);
        VariableInstruction vi = new VariableInstruction("x", "10", 2);
        exec.execute(vi);
        assertEquals("10", ctx.getVariable("x"));
    }

    @Test
    void testAssignment() {
        RuntimeContext ctx = new RuntimeContext();
        InstructionExecutor exec = new InstructionExecutor(ctx);
        exec.execute(new VariableInstruction("x", null, 1));
        exec.execute(new AssignmentInstruction("x", "42", 2));
        assertEquals("42", ctx.getVariable("x"));
    }

    @Test
    void testAssignmentToUndeclaredVariable() {
        RuntimeContext ctx = new RuntimeContext();
        InstructionExecutor exec = new InstructionExecutor(ctx);
        AssignmentInstruction ai = new AssignmentInstruction("y", "5", 3);
        Exception ex = assertThrows(RuntimeException.class, () -> exec.execute(ai));
        assertTrue(ex.getMessage().contains("not declared"));
    }

    @Test
    void testDuplicateVariableDeclaration() {
        RuntimeContext ctx = new RuntimeContext();
        InstructionExecutor exec = new InstructionExecutor(ctx);
        exec.execute(new VariableInstruction("x", null, 1));
        Exception ex = assertThrows(RuntimeException.class, () -> exec.execute(new VariableInstruction("x", null, 2)));
        assertTrue(ex.getMessage().contains("already declared"));
    }

    @Test
    void testFunctionDefinitionAndCallExecution() {
        RuntimeContext ctx = new RuntimeContext();
        InstructionExecutor exec = new InstructionExecutor(ctx);
        // Define function
        FunctionDefinitionInstruction def = new FunctionDefinitionInstruction(
            "myFunc",
            java.util.List.of(new AssignmentInstruction("x", "42", 2)),
            1
        );
        exec.execute(new VariableInstruction("x", "0", 1));
        exec.execute(def);
        // Call function
        FunctionCallInstruction call = new FunctionCallInstruction("myFunc", 3);
        exec.execute(call);
        // x should be updated in the local scope, but after popScope, global x remains
        assertEquals("0", ctx.getVariable("x"));
    }

    @Test
    void testFunctionCallBeforeDefinitionThrows() {
        RuntimeContext ctx = new RuntimeContext();
        InstructionExecutor exec = new InstructionExecutor(ctx);
        FunctionCallInstruction call = new FunctionCallInstruction("notDefined", 1);
        Exception ex = assertThrows(RuntimeException.class, () -> exec.execute(call));
        assertTrue(ex.getMessage().contains("not defined"));
    }
}
