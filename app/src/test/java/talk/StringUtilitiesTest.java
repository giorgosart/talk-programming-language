package talk;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.*;
import talk.core.*;
import talk.expression.*;
import talk.exception.*;
import java.util.List;

public class StringUtilitiesTest {
    private RuntimeContext ctx;
    private ExpressionResolver resolver;

    @BeforeEach
    void setUp() {
        ctx = new RuntimeContext();
        resolver = new ExpressionResolver(ctx);
        ctx.setVariable("hello", "Hello, World!");
        ctx.setVariable("spaces", "  trim me  ");
        ctx.setVariable("mixed", "MiXeD CaSe");
    }

    @Test
    void testUppercaseOperation() {
        Object result = resolver.resolve("uppercase of hello");
        assertTrue(result instanceof String);
        assertEquals("HELLO, WORLD!", result);
    }

    @Test
    void testLowercaseOperation() {
        Object result = resolver.resolve("lowercase of mixed");
        assertTrue(result instanceof String);
        assertEquals("mixed case", result);
    }

    @Test
    void testTrimOperation() {
        Object result = resolver.resolve("trim of spaces");
        assertTrue(result instanceof String);
        assertEquals("trim me", result);
    }

    @Test
    void testLengthOperation() {
        Object result = resolver.resolve("length of hello");
        assertTrue(result instanceof Integer);
        assertEquals(13, result);
    }

    @Test
    void testSubstringOperation() {
        ctx.setVariable("start", 1);
        ctx.setVariable("end", 5);
        Object result = resolver.resolve("substring of hello from start to end");
        assertTrue(result instanceof String);
        assertEquals("Hello", result);
    }

    @Test
    void testReplaceOperation() {
        ctx.setVariable("old", "World");
        ctx.setVariable("new", "Talk");
        Object result = resolver.resolve("replace old with new in hello");
        assertTrue(result instanceof String);
        assertEquals("Hello, Talk!", result);
    }

    @Test
    void testContainsOperation() {
        Object result = resolver.resolve("hello contains \"World\"");
        assertTrue(result instanceof Boolean);
        assertTrue((Boolean) result);
        
        result = resolver.resolve("hello contains \"Java\"");
        assertFalse((Boolean) result);
    }

    @Test
    void testStartsWithOperation() {
        Object result = resolver.resolve("hello starts with \"Hello\"");
        assertTrue(result instanceof Boolean);
        assertTrue((Boolean) result);
        
        result = resolver.resolve("hello starts with \"World\"");
        assertFalse((Boolean) result);
    }

    @Test
    void testEndsWithOperation() {
        Object result = resolver.resolve("hello ends with \"!\"");
        assertTrue(result instanceof Boolean);
        assertTrue((Boolean) result);
        
        result = resolver.resolve("hello ends with \"?\"");
        assertFalse((Boolean) result);
    }

    @Test
    void testSplitOperation() {
        Object result = resolver.resolve("split hello by \", \"");
        assertTrue(result instanceof ListValue);
        ListValue list = (ListValue) result;
        assertEquals(2, list.size());
        assertEquals("Hello", list.get(1));
        assertEquals("World!", list.get(2));
    }
    
    @Test
    void testNestedOperations() {
        ctx.setVariable("text", "  HELLO, WORLD!  ");
        Object result = resolver.resolve("lowercase of trim of text");
        assertTrue(result instanceof String);
        assertEquals("hello, world!", result);
    }
    
    @Test
    void testInvalidInputs() {
        ctx.setVariable("number", 42);
        
        // Test that operations fail gracefully with non-string inputs
        Throwable exception = assertThrows(TalkValueException.class, () -> {
            resolver.resolve("uppercase of number");
        });
        assertTrue(exception.getMessage().contains("Expected string"));
        
        exception = assertThrows(TalkValueException.class, () -> {
            resolver.resolve("substring of number from 1 to 3");
        });
        assertTrue(exception.getMessage().contains("Expected string"));
    }
}
