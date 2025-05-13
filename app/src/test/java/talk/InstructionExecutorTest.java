package talk;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import java.util.ArrayList;

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

    @Test
    void testRepeatForEachListExecution() {
        RuntimeContext ctx = new RuntimeContext();
        ctx.setVariable("fruits", new ListValue(List.of("apple", "banana", "cherry")));
        List<String> seen = new ArrayList<>();
        // Custom Instruction to collect the value of 'fruit' in each iteration
        class CollectInstruction implements Instruction {
            @Override public int getLineNumber() { return 2; }
        }
        List<Instruction> body = List.of(new CollectInstruction());
        RepeatInstruction ri = new RepeatInstruction("fruit", "fruits", body, 1);
        InstructionExecutor exec = new InstructionExecutor(ctx) {
            @Override
            public void execute(Instruction instruction) {
                if (instruction instanceof CollectInstruction) {
                    seen.add((String)ctx.getVariable("fruit"));
                } else {
                    super.execute(instruction);
                }
            }
        };
        exec.execute(ri);
        assertEquals(List.of("apple", "banana", "cherry"), seen);
    }

    @Test
    void testPositionKeywordInListIteration() {
        RuntimeContext ctx = new RuntimeContext();
        ctx.setVariable("fruits", new ListValue(List.of("apple", "banana", "cherry")));
        List<Integer> positions = new ArrayList<>();
        // Custom Instruction to collect the value of 'position' in each iteration
        class CollectPositionInstruction implements Instruction {
            @Override public int getLineNumber() { return 2; }
        }
        List<Instruction> body = List.of(new CollectPositionInstruction());
        RepeatInstruction ri = new RepeatInstruction("fruit", "fruits", body, 1);
        InstructionExecutor exec = new InstructionExecutor(ctx) {
            @Override
            public void execute(Instruction instruction) {
                if (instruction instanceof CollectPositionInstruction) {
                    positions.add((Integer)ctx.getVariable("position"));
                } else {
                    super.execute(instruction);
                }
            }
        };
        exec.execute(ri);
        assertEquals(List.of(1,2,3), positions);
        // Test that reassignment of 'position' throws
        List<Instruction> badBody = List.of(new AssignmentInstruction("position", 99, 2));
        RepeatInstruction badRi = new RepeatInstruction("fruit", "fruits", badBody, 1);
        Exception ex = assertThrows(RuntimeException.class, () -> exec.execute(badRi));
        assertTrue(ex.getMessage().contains("Cannot reassign 'position'"));
    }

    @Test
    void testListAssignmentAndResolution() {
        RuntimeContext ctx = new RuntimeContext();
        ListValue list = new ListValue(List.of("a", "b", "c"));
        ctx.setVariable("letters", list);
        assertEquals(list, ctx.getVariable("letters"));
    }

    @Test
    void testItemNInListAccess() {
        RuntimeContext ctx = new RuntimeContext();
        ctx.setVariable("nums", new ListValue(List.of("one", "two", "three")));
        ExpressionResolver resolver = new ExpressionResolver(ctx);
        assertEquals("one", resolver.resolve("item 1 in nums"));
        assertEquals("three", resolver.resolve("item 3 in nums"));
        Exception ex = assertThrows(RuntimeException.class, () -> resolver.resolve("item 0 in nums"));
        assertTrue(ex.getMessage().contains("out of bounds"));
    }

    @Test
    void testIncludesChecks() {
        RuntimeContext ctx = new RuntimeContext();
        ctx.setVariable("colors", new ListValue(List.of("red", "green", "blue")));
        ExpressionResolver resolver = new ExpressionResolver(ctx);
        assertTrue((Boolean)resolver.resolve("colors includes green"));
        assertFalse((Boolean)resolver.resolve("colors includes yellow"));
    }

    @Test
    void testPositionAccuracyInsideLoops() {
        RuntimeContext ctx = new RuntimeContext();
        ctx.setVariable("animals", new ListValue(List.of("cat", "dog", "fox")));
        List<Integer> positions = new ArrayList<>();
        class CollectPositionInstruction implements Instruction {
            @Override public int getLineNumber() { return 2; }
        }
        List<Instruction> body = List.of(new CollectPositionInstruction());
        RepeatInstruction ri = new RepeatInstruction("animal", "animals", body, 1);
        InstructionExecutor exec = new InstructionExecutor(ctx) {
            @Override
            public void execute(Instruction instruction) {
                if (instruction instanceof CollectPositionInstruction) {
                    positions.add((Integer)ctx.getVariable("position"));
                } else {
                    super.execute(instruction);
                }
            }
        };
        exec.execute(ri);
        assertEquals(List.of(1,2,3), positions);
    }

    @Test
    void testReadFileInstruction() throws Exception {
        // Prepare a file with known content
        String fileName = "test_readfile.txt";
        String fileContent = "Hello, file!\nSecond line.";
        java.nio.file.Files.write(java.nio.file.Paths.get(fileName), fileContent.getBytes());
        try {
            RuntimeContext ctx = new RuntimeContext();
            InstructionExecutor exec = new InstructionExecutor(ctx);
            ReadFileInstruction rfi = new ReadFileInstruction(fileName, "result", 1);
            exec.execute(rfi);
            assertTrue(ctx.hasVariable("result"));
            assertEquals(fileContent, ctx.getVariable("result"));
        } finally {
            java.nio.file.Files.deleteIfExists(java.nio.file.Paths.get(fileName));
        }
    }

    @Test
    void testAppendToFileInstruction() throws Exception {
        String fileName = "test_appendfile.txt";
        java.nio.file.Files.deleteIfExists(java.nio.file.Paths.get(fileName));
        RuntimeContext ctx = new RuntimeContext();
        InstructionExecutor exec = new InstructionExecutor(ctx);
        AppendToFileInstruction afi = new AppendToFileInstruction("hello world", fileName, 1);
        exec.execute(afi);
        String content = java.nio.file.Files.readString(java.nio.file.Paths.get(fileName));
        assertTrue(content.contains("hello world"));
        // Append again
        exec.execute(new AppendToFileInstruction("second line", fileName, 2));
        String content2 = java.nio.file.Files.readString(java.nio.file.Paths.get(fileName));
        assertTrue(content2.contains("hello world"));
        assertTrue(content2.contains("second line"));
        java.nio.file.Files.deleteIfExists(java.nio.file.Paths.get(fileName));
    }

    @Test
    void testDeleteFileInstruction() throws Exception {
        String fileName = "test_deletefile.txt";
        java.nio.file.Files.write(java.nio.file.Paths.get(fileName), "delete me".getBytes());
        assertTrue(new java.io.File(fileName).exists());
        RuntimeContext ctx = new RuntimeContext();
        InstructionExecutor exec = new InstructionExecutor(ctx);
        DeleteFileInstruction dfi = new DeleteFileInstruction(fileName, 1);
        exec.execute(dfi);
        assertFalse(new java.io.File(fileName).exists());
    }

    @Test
    void testListDirectoryInstruction() throws Exception {
        String dirName = "test_dir";
        java.nio.file.Files.createDirectories(java.nio.file.Paths.get(dirName));
        String file1 = dirName + "/file1.txt";
        String file2 = dirName + "/file2.txt";
        java.nio.file.Files.write(java.nio.file.Paths.get(file1), "abc".getBytes());
        java.nio.file.Files.write(java.nio.file.Paths.get(file2), "def".getBytes());
        RuntimeContext ctx = new RuntimeContext();
        InstructionExecutor exec = new InstructionExecutor(ctx);
        ListDirectoryInstruction ldi = new ListDirectoryInstruction(dirName, "files", 1);
        exec.execute(ldi);
        assertTrue(ctx.hasVariable("files"));
        Object val = ctx.getVariable("files");
        assertTrue(val instanceof ListValue);
        ListValue list = (ListValue) val;
        List<String> items = list.asList();
        assertTrue(items.contains("file1.txt"));
        assertTrue(items.contains("file2.txt"));
        java.nio.file.Files.deleteIfExists(java.nio.file.Paths.get(file1));
        java.nio.file.Files.deleteIfExists(java.nio.file.Paths.get(file2));
        java.nio.file.Files.deleteIfExists(java.nio.file.Paths.get(dirName));
    }

    @Test
    void testLogInstruction() throws Exception {
        String logFile = "debug.log";
        java.nio.file.Files.deleteIfExists(java.nio.file.Paths.get(logFile));
        RuntimeContext ctx = new RuntimeContext();
        InstructionExecutor exec = new InstructionExecutor(ctx);
        LogInstruction li = new LogInstruction("Hello log!", 1);
        exec.execute(li);
        String content = java.nio.file.Files.readString(java.nio.file.Paths.get(logFile));
        assertTrue(content.contains("Hello log!"));
        // Append again
        exec.execute(new LogInstruction("Second entry", 2));
        String content2 = java.nio.file.Files.readString(java.nio.file.Paths.get(logFile));
        assertTrue(content2.contains("Hello log!"));
        assertTrue(content2.contains("Second entry"));
        java.nio.file.Files.deleteIfExists(java.nio.file.Paths.get(logFile));
    }
}
