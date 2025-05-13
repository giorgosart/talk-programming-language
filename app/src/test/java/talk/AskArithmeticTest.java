package talk;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class AskArithmeticTest {
    @Test
    void testAskAndArithmetic() {
        // Simulate: ask "Enter a number:" and store in num
        // set num2 to num + 1
        RuntimeContext ctx = new RuntimeContext();
        // Simulate user input: 7
        ctx.setVariable("num", 7); // Should be stored as Integer
        ExpressionResolver resolver = new ExpressionResolver(ctx);
        Object result = resolver.resolve("num + 1");
        assertTrue(result instanceof Integer);
        assertEquals(8, result);
    }
}
