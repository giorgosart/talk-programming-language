package talk;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import talk.core.RuntimeContext;
import talk.instruction.CreateFileInstruction;
import talk.runtime.InstructionExecutor;

import static org.junit.jupiter.api.Assertions.*;

public class CreateFileInstructionExecutorTest {
    private static java.io.InputStream dummyIn = new java.io.ByteArrayInputStream("dummy\ndummy\ndummy\n".getBytes());
    private MockFileSystem mockFileSystem;
    private MockLogger mockLogger;
    
    @BeforeEach
    void setUp() {
        mockFileSystem = new MockFileSystem();
        mockLogger = new MockLogger();
    }

    @Test
    void testCreateFile() {
        String fileName = "test_create_file.txt";
        RuntimeContext ctx = new RuntimeContext();
        InstructionExecutor exec = new InstructionExecutor(ctx, dummyIn, mockFileSystem, mockLogger);
        CreateFileInstruction cfi = new CreateFileInstruction(fileName, 1);
        exec.execute(cfi);
        
        // Verify file was created
        assertTrue(mockFileSystem.getFiles().containsKey(fileName));
        assertTrue(mockFileSystem.getOperations().contains("fileExists:" + fileName));
        assertTrue(mockFileSystem.getOperations().contains("writeFile:" + fileName));
    }
}
