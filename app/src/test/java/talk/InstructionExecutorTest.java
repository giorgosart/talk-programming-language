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
            java.util.Collections.emptyList(),
            java.util.List.of(new AssignmentInstruction("x", "42", 2)),
            1
        );
        exec.execute(new VariableInstruction("x", "0", 1));
        exec.execute(def);
        // Call function
        FunctionCallInstruction call = new FunctionCallInstruction("myFunc", java.util.Collections.emptyList(), 3);
        exec.execute(call);
        // x should be updated in the local scope, but after popScope, global x remains
        assertEquals("0", ctx.getVariable("x"));
    }

    @Test
    void testFunctionCallBeforeDefinitionThrows() {
        RuntimeContext ctx = new RuntimeContext();
        InstructionExecutor exec = new InstructionExecutor(ctx);
        FunctionCallInstruction call = new FunctionCallInstruction("notDefined", java.util.Collections.emptyList(), 1);
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

    @Test
    void testCopyFileInstruction() throws Exception {
        String srcFile = "test_src.txt";
        String destFile = "test_dest.txt";
        java.nio.file.Files.write(java.nio.file.Paths.get(srcFile), "copy me".getBytes());
        java.nio.file.Files.deleteIfExists(java.nio.file.Paths.get(destFile));
        RuntimeContext ctx = new RuntimeContext();
        InstructionExecutor exec = new InstructionExecutor(ctx);
        CopyFileInstruction cfi = new CopyFileInstruction(srcFile, destFile, 1);
        exec.execute(cfi);
        String content = java.nio.file.Files.readString(java.nio.file.Paths.get(destFile));
        assertEquals("copy me", content);
        java.nio.file.Files.deleteIfExists(java.nio.file.Paths.get(srcFile));
        java.nio.file.Files.deleteIfExists(java.nio.file.Paths.get(destFile));
    }

    @Test
    void testFunctionCallWithParametersAndArguments() {
        RuntimeContext ctx = new RuntimeContext();
        InstructionExecutor exec = new InstructionExecutor(ctx);
        // Define function with parameters a, b
        FunctionDefinitionInstruction def = new FunctionDefinitionInstruction(
            "sum",
            java.util.List.of("a", "b"),
            java.util.List.of(new AssignmentInstruction("result", "a + b", 2)),
            1
        );
        exec.execute(def);
        // Call function with arguments 5, 10
        FunctionCallInstruction call = new FunctionCallInstruction("sum", java.util.List.of("5", "10"), 3);
        exec.execute(call);
        // result should be set in the function scope, not global
        assertNull(ctx.getVariable("result"));
    }

    @Test
    void testFunctionCallArgumentCountMismatchThrows() {
        RuntimeContext ctx = new RuntimeContext();
        InstructionExecutor exec = new InstructionExecutor(ctx);
        FunctionDefinitionInstruction def = new FunctionDefinitionInstruction(
            "sum",
            java.util.List.of("a", "b"),
            java.util.List.of(new AssignmentInstruction("result", "a + b", 2)),
            1
        );
        exec.execute(def);
        // Call with only one argument
        FunctionCallInstruction call = new FunctionCallInstruction("sum", java.util.List.of("5"), 3);
        Exception ex = assertThrows(RuntimeException.class, () -> exec.execute(call));
        assertTrue(ex.getMessage().contains("expects 2 arguments but got 1"));
    }

    @Test
    void testFunctionReturnInstruction() {
        RuntimeContext ctx = new RuntimeContext();
        InstructionExecutor exec = new InstructionExecutor(ctx);
        // Function: return x + 1
        ReturnInstruction ret = new ReturnInstruction("x + 1", 2);
        // Should throw FunctionReturn with correct value
        Exception ex = assertThrows(InstructionExecutor.FunctionReturn.class, () -> exec.execute(ret));
        assertTrue(ex instanceof InstructionExecutor.FunctionReturn);
    }

    @Test
    void testFunctionCallWithReturnValue() {
        RuntimeContext ctx = new RuntimeContext();
        InstructionExecutor exec = new InstructionExecutor(ctx);
        // Define function that returns a value
        FunctionDefinitionInstruction def = new FunctionDefinitionInstruction(
            "addOne",
            java.util.List.of("x"),
            java.util.List.of(new ReturnInstruction("x + 1", 2)),
            1
        );
        exec.execute(def);
        // Call function and capture return value
        FunctionCallInstruction call = new FunctionCallInstruction("addOne", java.util.List.of("5"), 3);
        Object result = null;
        try {
            result = exec.executeWithReturn(call);
        } catch (InstructionExecutor.FunctionReturn fr) {
            result = fr.getValue();
        }
        assertEquals(6, result); // Should be 5 + 1 = 6
    }

    @Test
    void testFunctionCallWithReturnValueIntoVariable() {
        RuntimeContext ctx = new RuntimeContext();
        InstructionExecutor exec = new InstructionExecutor(ctx);
        // Define function that returns a value
        FunctionDefinitionInstruction def = new FunctionDefinitionInstruction(
            "addOne",
            java.util.List.of("x"),
            java.util.List.of(new ReturnInstruction("x + 1", 2)),
            1
        );
        exec.execute(def);
        // Call function and capture return value into variable
        FunctionCallInstruction call = new FunctionCallInstruction("addOne", java.util.List.of("5"), "result", 3);
        exec.execute(call);
        // The result variable in the caller's scope should be set
        assertEquals(6, ctx.getVariable("result"));
    }

    @Test
    void testFunctionWithNoParameters() {
        RuntimeContext ctx = new RuntimeContext();
        InstructionExecutor exec = new InstructionExecutor(ctx);
        FunctionDefinitionInstruction def = new FunctionDefinitionInstruction(
            "noParams",
            java.util.Collections.emptyList(),
            java.util.List.of(new ReturnInstruction("42", 2)),
            1
        );
        exec.execute(def);
        FunctionCallInstruction call = new FunctionCallInstruction("noParams", java.util.Collections.emptyList(), 3);
        Object result = exec.executeWithReturn(call);
        assertEquals(42, result);
    }

    @Test
    void testFunctionWithParameters() {
        RuntimeContext ctx = new RuntimeContext();
        InstructionExecutor exec = new InstructionExecutor(ctx);
        FunctionDefinitionInstruction def = new FunctionDefinitionInstruction(
            "add",
            java.util.List.of("a", "b"),
            java.util.List.of(new ReturnInstruction("a + b", 2)),
            1
        );
        exec.execute(def);
        FunctionCallInstruction call = new FunctionCallInstruction("add", java.util.List.of("3", "4"), 3);
        Object result = exec.executeWithReturn(call);
        assertEquals(7, result);
    }

    @Test
    void testFunctionArgumentMismatchThrows() {
        RuntimeContext ctx = new RuntimeContext();
        InstructionExecutor exec = new InstructionExecutor(ctx);
        FunctionDefinitionInstruction def = new FunctionDefinitionInstruction(
            "oneParam",
            java.util.List.of("x"),
            java.util.List.of(new ReturnInstruction("x", 2)),
            1
        );
        exec.execute(def);
        FunctionCallInstruction call = new FunctionCallInstruction("oneParam", java.util.Collections.emptyList(), 3);
        Exception ex = assertThrows(RuntimeException.class, () -> exec.executeWithReturn(call));
        assertTrue(ex.getMessage().contains("expects 1 arguments"));
    }

    @Test
    void testFunctionReturnEarlyExit() {
        RuntimeContext ctx = new RuntimeContext();
        InstructionExecutor exec = new InstructionExecutor(ctx);
        FunctionDefinitionInstruction def = new FunctionDefinitionInstruction(
            "earlyReturn",
            java.util.List.of("x"),
            java.util.List.of(
                new AssignmentInstruction("y", "1", 2),
                new ReturnInstruction("x", 3),
                new AssignmentInstruction("y", "2", 4)
            ),
            1
        );
        exec.execute(def);
        FunctionCallInstruction call = new FunctionCallInstruction("earlyReturn", java.util.List.of("99"), 5);
        Object result = exec.executeWithReturn(call);
        assertEquals(99, result);
        // y should be 1, not 2, because of early return
        assertNull(ctx.getVariable("y"));
    }
}
