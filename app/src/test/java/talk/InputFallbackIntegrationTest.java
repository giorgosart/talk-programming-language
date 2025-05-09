package talk;

import org.junit.jupiter.api.*;
import java.io.*;
import java.nio.file.Files;
import static org.junit.jupiter.api.Assertions.*;

public class InputFallbackIntegrationTest {
    @Test
    void testInputFallbackWithInvalidInput() throws Exception {
        // Prepare script lines matching input_fallback.talk, with ask inside attempt
        String[] lines = {
            "attempt",
            "  ask \"Enter a number:\" and store in num",
            "  write num in output.txt",
            "if that fails",
            "  write \"Invalid input!\" in output.txt"
        };
        // Simulate user entering 'r' three times
        InputStream originalIn = System.in;
        PrintStream originalOut = System.out;
        try {
            ByteArrayInputStream in = new ByteArrayInputStream("r\nr\nr\n".getBytes());
            System.setIn(in);
            // Remove output.txt if exists
            File outFile = new File("output.txt");
            if (outFile.exists()) outFile.delete();
            // Run script
            Tokenizer tokenizer = new Tokenizer();
            var tokens = tokenizer.tokenize(java.util.Arrays.asList(lines));
            Parser parser = new Parser(tokens);
            var instructions = parser.parse();
            RuntimeContext ctx = new RuntimeContext();
            InstructionExecutor exec = new InstructionExecutor(ctx);
            for (Instruction instr : instructions) {
                exec.execute(instr);
            }
            // Check output.txt
            String content = Files.readString(outFile.toPath());
            assertTrue(content.contains("Invalid input!"), "Output should contain 'Invalid input!' but was: " + content);
        } finally {
            System.setIn(originalIn);
            System.setOut(originalOut);
        }
    }
}
