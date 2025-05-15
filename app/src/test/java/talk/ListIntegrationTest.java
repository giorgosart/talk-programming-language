package talk;

import org.junit.jupiter.api.*;

import talk.core.Instruction;
import talk.core.RuntimeContext;
import talk.expression.ExpressionResolver;
import talk.expression.ListValue;
import talk.instruction.RepeatInstruction;
import talk.instruction.WriteInstruction;
import talk.runtime.InstructionExecutor;

import java.io.*;
import java.nio.file.*;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;

public class ListIntegrationTest {
    private static java.io.InputStream dummyIn = new java.io.ByteArrayInputStream("dummy\ndummy\ndummy\n".getBytes());

    @Test
    void testDeclareListAndAccessByIndex() {
        RuntimeContext ctx = new RuntimeContext();
        ctx.setVariable("fruits", new ListValue(List.of("apple", "banana", "cherry")));
        ExpressionResolver resolver = new ExpressionResolver(ctx);
        assertEquals("banana", resolver.resolve("item 2 in fruits"));
    }

    @Test
    void testLoopOverListWithPosition() {
        RuntimeContext ctx = new RuntimeContext();
        ctx.setVariable("nums", new ListValue(List.of("one", "two", "three")));
        List<String> seen = new ArrayList<>();
        List<Integer> positions = new ArrayList<>();
        class CollectInstruction implements Instruction { public int getLineNumber() { return 2; } }
        List<Instruction> body = List.of(new CollectInstruction());
        RepeatInstruction ri = new RepeatInstruction("num", "nums", body, 1);
        InstructionExecutor exec = new InstructionExecutor(ctx, dummyIn) {
            @Override
            public void execute(Instruction instruction) {
                if (instruction instanceof CollectInstruction) {
                    seen.add((String)ctx.getVariable("num"));
                    positions.add((Integer)ctx.getVariable("position"));
                } else {
                    super.execute(instruction);
                }
            }
        };
        exec.execute(ri);
        assertEquals(List.of("one", "two", "three"), seen);
        assertEquals(List.of(1,2,3), positions);
    }

    @Test
    void testWriteIndexedOutputToFile() throws Exception {
        String fileName = "test_list_output.txt";
        File file = new File(fileName);
        if (file.exists()) file.delete();
        RuntimeContext ctx = new RuntimeContext();
        ctx.setVariable("letters", new ListValue(List.of("A", "B", "C")));
        List<Instruction> body = List.of(
            new WriteInstruction("position", fileName, 2),
            new WriteInstruction("letter", fileName, 3)
        );
        RepeatInstruction ri = new RepeatInstruction("letter", "letters", body, 1);
        InstructionExecutor exec = new InstructionExecutor(ctx, dummyIn);
        exec.execute(ri);
        String content = Files.readString(file.toPath());
        // Check that all expected values are present
        assertTrue(content.contains("1"), "File should contain '1'");
        assertTrue(content.contains("A"), "File should contain 'A'");
        assertTrue(content.contains("2"), "File should contain '2'");
        assertTrue(content.contains("B"), "File should contain 'B'");
        assertTrue(content.contains("3"), "File should contain '3'");
        assertTrue(content.contains("C"), "File should contain 'C'");
        file.delete();
    }

    @Test
    void testMembershipTestInIfCondition() {
        RuntimeContext ctx = new RuntimeContext();
        ctx.setVariable("colors", new ListValue(List.of("red", "green", "blue")));
        ExpressionResolver resolver = new ExpressionResolver(ctx);
        assertTrue((Boolean)resolver.resolve("colors includes green"));
        assertFalse((Boolean)resolver.resolve("colors includes yellow"));
    }
}
