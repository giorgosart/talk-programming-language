package talk;

import org.junit.jupiter.api.Test;
import java.io.File;
import java.nio.file.Files;
import static org.junit.jupiter.api.Assertions.*;

public class WriteInstructionExecutorTest {
    @Test
    void testWriteLiteralToFile() throws Exception {
        String fileName = "test_output.txt";
        File file = new File(fileName);
        if (file.exists()) file.delete();
        RuntimeContext ctx = new RuntimeContext();
        InstructionExecutor exec = new InstructionExecutor(ctx);
        WriteInstruction wi = new WriteInstruction("Hello", fileName, 1);
        exec.execute(wi);
        String content = Files.readString(file.toPath());
        assertTrue(content.contains("Hello"));
        file.delete();
    }

    @Test
    void testWriteVariableToFile() throws Exception {
        String fileName = "test_output_var.txt";
        File file = new File(fileName);
        if (file.exists()) file.delete();
        RuntimeContext ctx = new RuntimeContext();
        ctx.setVariable("x", "World");
        InstructionExecutor exec = new InstructionExecutor(ctx);
        WriteInstruction wi = new WriteInstruction("x", fileName, 2);
        exec.execute(wi);
        String content = Files.readString(file.toPath());
        assertTrue(content.contains("World"));
        file.delete();
    }
}
