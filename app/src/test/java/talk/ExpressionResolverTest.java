package talk;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class ExpressionResolverTest {
    static class DummyContext extends RuntimeContext {
        public DummyContext() { super(); }
        @Override public boolean hasVariable(String name) { return "x".equals(name) || "y".equals(name) || "z".equals(name); }
        @Override public Object getVariable(String name) {
            switch (name) {
                case "x": return 1;
                case "y": return 0;
                case "z": return true;
                default: return null;
            }
        }
    }

    @Test
    void testAndOrNotLogic() {
        ExpressionResolver r = new ExpressionResolver(new DummyContext());
        // x = 1, y = 0, z = true
        assertEquals(false, r.resolve("x is equal to 1 AND y is equal to 1"));
        assertEquals(true, r.resolve("x is equal to 1 OR y is equal to 1"));
        assertEquals(false, r.resolve("NOT x is equal to 1"));
        assertEquals(true, r.resolve("NOT y is equal to 1"));
        assertEquals(true, r.resolve("z AND x is equal to 1"));
        assertEquals(true, r.resolve("z OR y is equal to 1"));
        assertEquals(false, r.resolve("NOT z"));
    }

    @Test
    void testShortCircuit() {
        ExpressionResolver r = new ExpressionResolver(new DummyContext());
        // If left of AND is false, right is not evaluated (should not throw)
        assertDoesNotThrow(() -> r.resolve("y is equal to 1 AND not_a_var is equal to 1"));
        // If left of OR is true, right is not evaluated (should not throw)
        assertDoesNotThrow(() -> r.resolve("x is equal to 1 OR not_a_var is equal to 1"));
    }
}
