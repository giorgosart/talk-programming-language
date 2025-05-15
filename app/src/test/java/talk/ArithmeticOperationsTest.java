package talk;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.*;
import talk.core.*;
import talk.expression.*;
import talk.exception.*;

public class ArithmeticOperationsTest {
    private RuntimeContext ctx;
    private ExpressionResolver resolver;

    @BeforeEach
    void setUp() {
        ctx = new RuntimeContext();
        resolver = new ExpressionResolver(ctx);
        ctx.setVariable("x", 10);
        ctx.setVariable("y", 5);
        ctx.setVariable("z", 2.5);
    }

    @Test
    void testAddition() {
        Object result = resolver.resolve("x plus y");
        assertTrue(result instanceof Integer);
        assertEquals(15, result);
        
        result = resolver.resolve("x plus z");
        assertTrue(result instanceof Double);
        assertEquals(12.5, result);
    }

    @Test
    void testSubtraction() {
        Object result = resolver.resolve("x minus y");
        assertTrue(result instanceof Integer);
        assertEquals(5, result);
        
        result = resolver.resolve("y minus x");
        assertTrue(result instanceof Integer);
        assertEquals(-5, result);
    }

    @Test
    void testMultiplication() {
        Object result = resolver.resolve("x times y");
        assertTrue(result instanceof Integer);
        assertEquals(50, result);
        
        result = resolver.resolve("y times z");
        assertTrue(result instanceof Double);
        assertEquals(12.5, result);
    }

    @Test
    void testDivision() {
        Object result = resolver.resolve("x divided by y");
        assertTrue(result instanceof Integer);
        assertEquals(2, result);
        
        result = resolver.resolve("x divided by z");
        assertTrue(result instanceof Double);
        assertEquals(4.0, result);
        
        // Test division by zero
        assertThrows(TalkValueException.class, () -> {
            resolver.resolve("x divided by 0");
        });
    }

    @Test
    void testModulo() {
        Object result = resolver.resolve("x modulo 3");
        assertTrue(result instanceof Integer);
        assertEquals(1, result);
        
        result = resolver.resolve("10 modulo 2.5");
        assertTrue(result instanceof Double);
        assertEquals(0.0, result);
        
        // Test modulo by zero
        assertThrows(TalkValueException.class, () -> {
            resolver.resolve("x modulo 0");
        });
    }

    @Test
    void testNegation() {
        Object result = resolver.resolve("negative of x");
        assertTrue(result instanceof Double);
        assertEquals(-10.0, result);
        
        result = resolver.resolve("negative of negative of x");
        assertTrue(result instanceof Double);
        assertEquals(10.0, result);
    }

    @Test
    void testPower() {
        Object result = resolver.resolve("x to the power of 2");
        assertTrue(result instanceof Double);
        assertEquals(100.0, result);
        
        result = resolver.resolve("2 to the power of 3");
        assertTrue(result instanceof Double);
        assertEquals(8.0, result);
    }

    @Test
    void testAbsolute() {
        ctx.setVariable("negative", -15);
        Object result = resolver.resolve("absolute of negative");
        assertTrue(result instanceof Double);
        assertEquals(15.0, result);
        
        result = resolver.resolve("absolute of -7.5");
        assertTrue(result instanceof Double);
        assertEquals(7.5, result);
    }

    @Test
    void testRound() {
        Object result = resolver.resolve("round 4.7");
        assertTrue(result instanceof Long);
        assertEquals(5L, result);
        
        result = resolver.resolve("round 4.2");
        assertTrue(result instanceof Long);
        assertEquals(4L, result);
    }

    @Test
    void testFloor() {
        Object result = resolver.resolve("floor 4.7");
        assertTrue(result instanceof Double);
        assertEquals(4.0, result);
        
        result = resolver.resolve("floor -2.3");
        assertTrue(result instanceof Double);
        assertEquals(-3.0, result);
    }

    @Test
    void testCeil() {
        Object result = resolver.resolve("ceil 4.2");
        assertTrue(result instanceof Double);
        assertEquals(5.0, result);
        
        result = resolver.resolve("ceil -2.8");
        assertTrue(result instanceof Double);
        assertEquals(-2.0, result);
    }

    @Test
    void testTypeCoercion() {
        ctx.setVariable("numStr", "20");
        Object result = resolver.resolve("numStr plus 5");
        assertTrue(result instanceof Double);
        assertEquals(25.0, result);
    }

    @Test
    void testInvalidOperands() {
        ctx.setVariable("text", "hello");
        
        assertThrows(TalkValueException.class, () -> {
            resolver.resolve("text plus 5");
        });
        
        assertThrows(TalkValueException.class, () -> {
            resolver.resolve("x divided by text");
        });
    }

    @Test
    void testComplexExpressions() {
        ctx.setVariable("a", 2);
        ctx.setVariable("b", 3);
        ctx.setVariable("c", 4);
        
        // Calculate: a^2 + (b * c) - 5
        Object result = resolver.resolve("a to the power of 2 plus b times c minus 5");
        assertTrue(result instanceof Double);
        assertEquals(4.0 + 12.0 - 5.0, (Double) result, 0.001);
    }
}
