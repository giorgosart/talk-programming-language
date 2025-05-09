package talk;

import org.junit.jupiter.api.Test;
import java.io.File;
import static org.junit.jupiter.api.Assertions.*;

public class CreateFileInstructionExecutorTest {
    @Test
    void testCreateFile() {
        String fileName = "test_create_file.txt";
        File file = new File(fileName);
        if (file.exists()) file.delete();
        RuntimeContext ctx = new RuntimeContext();
        InstructionExecutor exec = new InstructionExecutor(ctx);
        CreateFileInstruction cfi = new CreateFileInstruction(fileName, 1);
        exec.execute(cfi);
        assertTrue(file.exists());
        assertTrue(file.canWrite());
        file.delete();
    }
}
