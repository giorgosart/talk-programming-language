package talk;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import talk.core.RuntimeContext;
import talk.instruction.WriteInstruction;
import talk.runtime.InstructionExecutor;

import static org.junit.jupiter.api.Assertions.*;

public class WriteInstructionExecutorTest {
    private static java.io.InputStream dummyIn = new java.io.ByteArrayInputStream("dummy\ndummy\ndummy\n".getBytes());
    private MockFileSystem mockFileSystem;
    private MockLogger mockLogger;
    
    @BeforeEach
    void setUp() {
        mockFileSystem = new MockFileSystem();
        mockLogger = new MockLogger();
    }

    @Test
    void testWriteLiteralToFile() throws Exception {
        String fileName = "test_output.txt";
        RuntimeContext ctx = new RuntimeContext();
        InstructionExecutor exec = new InstructionExecutor(ctx, dummyIn, mockFileSystem, mockLogger);
        WriteInstruction wi = new WriteInstruction("Hello", fileName, 1);
        exec.execute(wi);
        
        assertTrue(mockFileSystem.getFiles().containsKey(fileName));
        String content = mockFileSystem.getFiles().get(fileName);
        assertTrue(content.contains("Hello"));
        assertEquals(2, mockFileSystem.getOperations().size());
        assertTrue(mockFileSystem.getOperations().contains("fileExists:" + fileName));
        assertTrue(mockFileSystem.getOperations().contains("writeFile:" + fileName));
    }

    @Test
    void testWriteVariableToFile() throws Exception {
        String fileName = "test_output_var.txt";
        RuntimeContext ctx = new RuntimeContext();
        ctx.setVariable("x", "World");
        InstructionExecutor exec = new InstructionExecutor(ctx, dummyIn, mockFileSystem, mockLogger);
        WriteInstruction wi = new WriteInstruction("x", fileName, 2);
        exec.execute(wi);
        
        assertTrue(mockFileSystem.getFiles().containsKey(fileName));
        String content = mockFileSystem.getFiles().get(fileName);
        assertTrue(content.contains("World"));
    }
}
