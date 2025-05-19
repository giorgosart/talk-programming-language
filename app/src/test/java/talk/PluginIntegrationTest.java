package talk;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import talk.core.Instruction;
import talk.core.RuntimeContext;
import talk.core.Tokenizer;
import talk.plugins.BuiltInPlugins;
import talk.plugins.PluginRegistry;
import talk.runtime.InstructionExecutor;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration test for the Talk plugin system.
 * This test verifies that plugins can be called from Talk scripts
 * and the results are properly stored in variables.
 */
public class PluginIntegrationTest {
    private static final InputStream dummyIn = new ByteArrayInputStream("dummy\ndummy\ndummy\n".getBytes());
    private MockFileSystem mockFileSystem;
    private MockLogger mockLogger;
    
    @BeforeEach
    void setUp() {
        mockFileSystem = new MockFileSystem();
        mockLogger = new MockLogger();
        
        // Initialize built-in plugins
        BuiltInPlugins.registerAll();
        
        // Register a test plugin
        PluginRegistry.getInstance().register("add", (Object... args) -> {
            double sum = 0;
            for (Object arg : args) {
                sum += Double.parseDouble(arg.toString());
            }
            return sum;
        });
    }
    
    @Test
    void testPluginInScript() throws Exception {
        // Create a Talk script that uses plugins
        List<String> lines = Arrays.asList(
            "variable x equal 10",
            "variable y equal 20",
            "use plugin add with x and y into result",
            "variable formatted equal \"Sum: \"",
            "use plugin to uppercase with formatted into upper_format",
            "variable output equal upper_format and result"
        );
        
        // Parse and execute the script
        Tokenizer tokenizer = new Tokenizer();
        List<Tokenizer.Token> tokens = tokenizer.tokenize(lines);
        Parser parser = new Parser(tokens);
        List<Instruction> instructions = parser.parse();
        
        RuntimeContext ctx = new RuntimeContext();
        InstructionExecutor exec = new InstructionExecutor(ctx, dummyIn, mockFileSystem, mockLogger);
        
        for (Instruction instr : instructions) {
            exec.execute(instr);
        }
        
        // Verify the plugin was called and the results are correct
        assertTrue(ctx.hasVariable("result"));
        assertEquals(30.0, ctx.getVariable("result"));
        assertTrue(ctx.hasVariable("upper_format"));
        assertEquals("SUM: ", ctx.getVariable("upper_format"));
        assertTrue(ctx.hasVariable("output"));
        assertEquals("SUM: 30.0", ctx.getVariable("output").toString());
    }
    
    @Test
    void testBuiltInPlugins() throws Exception {
        // Create a Talk script using built-in plugins
        List<String> lines = Arrays.asList(
            "use plugin random number with 1 and 100 into rand",
            "use plugin system property with \"user.name\" into username",
            "use plugin to lowercase with \"HELLO WORLD\" into hello"
        );
        
        // Parse and execute the script
        Tokenizer tokenizer = new Tokenizer();
        List<Tokenizer.Token> tokens = tokenizer.tokenize(lines);
        Parser parser = new Parser(tokens);
        List<Instruction> instructions = parser.parse();
        
        RuntimeContext ctx = new RuntimeContext();
        InstructionExecutor exec = new InstructionExecutor(ctx, dummyIn, mockFileSystem, mockLogger);
        
        for (Instruction instr : instructions) {
            exec.execute(instr);
        }
        
        // Verify results
        assertTrue(ctx.hasVariable("rand"));
        Object rand = ctx.getVariable("rand");
        assertTrue(rand instanceof Integer || rand instanceof Double);
        if (rand instanceof Integer) {
            int randInt = (Integer)rand;
            assertTrue(randInt >= 1 && randInt <= 100);
        } else {
            double randDouble = (Double)rand;
            assertTrue(randDouble >= 1 && randDouble <= 100);
        }
        
        assertTrue(ctx.hasVariable("username"));
        assertNotNull(ctx.getVariable("username"));
        
        assertTrue(ctx.hasVariable("hello"));
        assertEquals("hello world", ctx.getVariable("hello"));
    }
    
    @Test
    void testPluginWithoutIntoClause() throws Exception {
        // Create a Talk script with a plugin call that doesn't store its result
        List<String> lines = Arrays.asList(
            "variable x equal 5",
            "variable y equal 10",
            "use plugin add with x and y", // No 'into' clause
            "variable final equal \"The plugin was called\""
        );
        
        // Parse and execute the script
        Tokenizer tokenizer = new Tokenizer();
        List<Tokenizer.Token> tokens = tokenizer.tokenize(lines);
        Parser parser = new Parser(tokens);
        List<Instruction> instructions = parser.parse();
        
        RuntimeContext ctx = new RuntimeContext();
        InstructionExecutor exec = new InstructionExecutor(ctx, dummyIn, mockFileSystem, mockLogger);
        
        for (Instruction instr : instructions) {
            exec.execute(instr);
        }
        
        // Verify the script ran without errors
        assertEquals(5, ctx.getVariable("x"));
        assertEquals(10, ctx.getVariable("y"));
        assertEquals("The plugin was called", ctx.getVariable("final"));
    }
}
