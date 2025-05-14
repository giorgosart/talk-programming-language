package talk;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class ErrorFormatterTest {
    @Test
    void testFormatWithLineAndHint() {
        Exception e = new Exception("Something went wrong");
        String msg = ErrorFormatter.format(e, 5, "Check your syntax");
        assertTrue(msg.contains("[Error] (line 5): Something went wrong"));
        assertTrue(msg.contains("Hint: Check your syntax"));
    }

    @Test
    void testFormatWithoutLine() {
        Exception e = new Exception("Oops");
        String msg = ErrorFormatter.format(e, -1, "");
        assertTrue(msg.contains("[Error]: Oops"));
    }

    @Test
    void testFormatWithNullHint() {
        Exception e = new Exception("Null pointer");
        String msg = ErrorFormatter.format(e, 7, null);
        assertTrue(msg.contains("[Error] (line 7): Null pointer"));
        assertFalse(msg.contains("Hint:"));
    }

    @Test
    void testFormatWithEmptyHintAndLine() {
        Exception e = new Exception("Empty hint");
        String msg = ErrorFormatter.format(e, 3, "");
        assertTrue(msg.contains("[Error] (line 3): Empty hint"));
        assertFalse(msg.contains("Hint:"));
    }

    @Test
    void testFormatWithZeroLineNumber() {
        Exception e = new Exception("Zero line");
        String msg = ErrorFormatter.format(e, 0, "Some hint");
        assertTrue(msg.contains("[Error]: Zero line"));
        assertTrue(msg.contains("Hint: Some hint"));
        assertFalse(msg.contains("(line "));
    }
}
