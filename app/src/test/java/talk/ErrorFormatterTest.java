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
}
