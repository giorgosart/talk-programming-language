package talk;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.Arrays;
import java.util.Collections;

import talk.core.RuntimeContext;
import talk.instruction.PluginCallInstruction;
import talk.plugins.PluginRegistry;
import talk.runtime.InstructionExecutor;

import static org.junit.jupiter.api.Assertions.*;

public class PluginCallInstructionTest {
    
    private RuntimeContext ctx;
    private InstructionExecutor exec;
    private PluginRegistry registry;
    
    @BeforeEach
    void setUp() {
        ctx = new RuntimeContext();
        exec = new InstructionExecutor(ctx);
        registry = PluginRegistry.getInstance();
        
        // Register a test plugin
        registry.register("add numbers", (Object... args) -> {
            if (args.length < 2) {
                throw new IllegalArgumentException("Need at least two numbers");
            }
            double sum = 0;
            for (Object arg : args) {
                if (arg instanceof Number) {
                    sum += ((Number) arg).doubleValue();
                } else {
                    sum += Double.parseDouble(arg.toString());
                }
            }
            return sum;
        });
        
        // Register a plugin that returns a string
        registry.register("join words", (Object... args) -> {
            StringBuilder sb = new StringBuilder();
            for (Object arg : args) {
                sb.append(arg.toString()).append(" ");
            }
            return sb.toString().trim();
        });
        
        // Register a plugin that returns null
        registry.register("return nothing", (Object... args) -> null);
    }
    
    @Test
    void testPluginCallWithNoArguments() {
        // Calling a plugin that expects arguments with none should throw an exception
        PluginCallInstruction noArgs = new PluginCallInstruction("add numbers", Collections.emptyList(), "result", 1);
        Exception e = assertThrows(RuntimeException.class, () -> exec.execute(noArgs));
        assertTrue(e.getMessage().contains("Need at least two numbers"));
    }
    
    @Test
    void testPluginCallWithArguments() {
        // Call the add numbers plugin with two arguments
        PluginCallInstruction addCall = new PluginCallInstruction("add numbers", 
                Arrays.asList("5", "10"), "result", 1);
        exec.execute(addCall);
        
        // Verify the result is stored in the variable
        assertTrue(ctx.hasVariable("result"));
        assertEquals(15.0, ctx.getVariable("result"));
    }
    
    @Test
    void testPluginCallWithoutResultVariable() {
        // Call a plugin without storing the result
        PluginCallInstruction addCall = new PluginCallInstruction("add numbers", 
                Arrays.asList("5", "10"), null, 1);
        exec.execute(addCall);
        
        // No variable should be created
        assertFalse(ctx.hasVariable("result"));
    }
    
    @Test
    void testPluginCallWithStringResult() {
        // Call the join words plugin
        PluginCallInstruction joinCall = new PluginCallInstruction("join words", 
                Arrays.asList("hello", "world", "today"), "message", 1);
        exec.execute(joinCall);
        
        // Verify the result is stored in the variable
        assertTrue(ctx.hasVariable("message"));
        assertEquals("hello world today", ctx.getVariable("message"));
    }
    
    @Test
    void testNonExistentPlugin() {
        // Call a plugin that doesn't exist
        PluginCallInstruction badCall = new PluginCallInstruction("nonexistent plugin", 
                Collections.emptyList(), "result", 1);
        Exception e = assertThrows(RuntimeException.class, () -> exec.execute(badCall));
        assertTrue(e.getMessage().contains("not found"));
    }
    
    @Test
    void testPluginWithNullResult() {
        // Call a plugin that returns null
        PluginCallInstruction nullCall = new PluginCallInstruction("return nothing", 
                Collections.emptyList(), "result", 1);
        exec.execute(nullCall);
        
        // Variable should exist but be null
        assertTrue(ctx.hasVariable("result"));
        assertNull(ctx.getVariable("result"));
    }
}
