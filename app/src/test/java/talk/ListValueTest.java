package talk;

import org.junit.jupiter.api.Test;
import java.util.List;
import talk.exception.TalkValueException;
import talk.expression.ListValue;

import static org.junit.jupiter.api.Assertions.*;

public class ListValueTest {
    @Test
    void testGetValidIndex() {
        ListValue lv = new ListValue(List.of("a", "b", "c"));
        assertEquals("a", lv.get(1));
        assertEquals("b", lv.get(2));
        assertEquals("c", lv.get(3));
    }

    @Test
    void testGetInvalidIndexLow() {
        ListValue lv = new ListValue(List.of("x", "y"));
        Exception e = assertThrows(TalkValueException.class, () -> lv.get(0));
        assertTrue(e.getMessage().contains("List index out of bounds"));
    }

    @Test
    void testGetInvalidIndexHigh() {
        ListValue lv = new ListValue(List.of("x", "y"));
        Exception e = assertThrows(TalkValueException.class, () -> lv.get(3));
        assertTrue(e.getMessage().contains("List index out of bounds"));
    }

    @Test
    void testSize() {
        ListValue lv = new ListValue(List.of("a", "b"));
        assertEquals(2, lv.size());
    }

    @Test
    void testIncludes() {
        ListValue lv = new ListValue(List.of("foo", "bar"));
        assertTrue(lv.includes("foo"));
        assertFalse(lv.includes("baz"));
    }

    @Test
    void testGetItemsAndAsList() {
        List<String> items = List.of("1", "2");
        ListValue lv = new ListValue(items);
        assertEquals(items, lv.getItems());
        assertEquals(items, lv.asList());
    }

    @Test
    void testEqualsAndHashCode() {
        ListValue lv1 = new ListValue(List.of("a", "b"));
        ListValue lv2 = new ListValue(List.of("a", "b"));
        ListValue lv3 = new ListValue(List.of("b", "a"));
        assertEquals(lv1, lv2);
        assertEquals(lv1.hashCode(), lv2.hashCode());
        assertNotEquals(lv1, lv3);
        assertNotEquals(lv1, null);
        assertNotEquals(lv1, "not a ListValue");
        assertEquals(lv1, lv1); // self-equality
    }

    @Test
    void testToString() {
        ListValue lv = new ListValue(List.of("x", "y"));
        assertEquals(List.of("x", "y").toString(), lv.toString());
    }
}
