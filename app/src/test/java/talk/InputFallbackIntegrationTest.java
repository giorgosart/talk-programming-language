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
        File outFile = new File("output.txt");
        try {
            ByteArrayInputStream in = new ByteArrayInputStream("r\nr\nr\n".getBytes());
            System.setIn(in);
            // Remove output.txt if exists
            if (outFile.exists()) outFile.delete();
            // Run script
            Tokenizer tokenizer = new Tokenizer();
            var tokens = tokenizer.tokenize(java.util.Arrays.asList(lines));
            System.out.println("[TOKENS]");
            for (var t : tokens) {
                System.out.println(t.value + " (line " + t.lineNumber + ")");
            }
            Parser parser = new Parser(tokens);
            var instructions = parser.parse();
            System.out.println("[TEST DEBUG] Parsed instructions: " + instructions);
            for (Instruction instr : instructions) {
                if (instr instanceof AttemptInstruction) {
                    AttemptInstruction ai = (AttemptInstruction) instr;
                    System.out.println("[TEST DEBUG] Try block:");
                    for (Instruction t : ai.getTryBlock()) {
                        System.out.println("  " + t.getClass().getSimpleName() + ": " + t);
                    }
                    System.out.println("[TEST DEBUG] Catch block:");
                    for (Instruction c : ai.getCatchBlock()) {
                        System.out.println("  " + c.getClass().getSimpleName() + ": " + c);
                    }
                }
            }
            RuntimeContext ctx = new RuntimeContext();
            InstructionExecutor exec = new InstructionExecutor(ctx, in);
            for (Instruction instr : instructions) {
                exec.execute(instr);
            }
            // Check output.txt
            assertTrue(outFile.exists(), "output.txt should exist after script execution");
            String content = Files.readString(outFile.toPath());
            if (!content.contains("Invalid input!")) {
                System.out.println("[TEST DEBUG] output.txt contents: '" + content + "'");
            }
            assertTrue(content.contains("Invalid input!"), "Output should contain 'Invalid input!' but was: " + content);
        } finally {
            System.setIn(originalIn);
            System.setOut(originalOut);
            if (outFile.exists()) outFile.delete();
        }
    }
}
