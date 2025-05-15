package talk.expression;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Unit tests for DateUtil functionality
 */
public class DateUtilTest {

    @Test
    public void testNow() {
        String now = DateUtil.now();
        // Check format matches ISO_LOCAL_DATE_TIME
        assertTrue(now.matches("\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2}(\\.\\d{1,9})?"));
    }

    @Test
    public void testToday() {
        String today = DateUtil.today();
        // Check format matches ISO_LOCAL_DATE
        assertTrue(today.matches("\\d{4}-\\d{2}-\\d{2}"));
        assertEquals(LocalDate.now().toString(), today);
    }

    @Test
    public void testFormatDate() {
        String date = "2024-03-15";
        String pattern = "MM/dd/yyyy";
        String formatted = DateUtil.formatDate(date, pattern);
        assertEquals("03/15/2024", formatted);
    }

    @Test
    public void testFormatDateTime() {
        String dateTime = "2024-03-15T14:30:45";
        String pattern = "MM/dd/yyyy HH:mm";
        String formatted = DateUtil.formatDate(dateTime, pattern);
        assertEquals("03/15/2024 14:30", formatted);
    }

    @Test
    public void testAddDays() {
        String date = "2024-03-15";
        String newDate = DateUtil.addDays(5, date);
        assertEquals("2024-03-20", newDate);
    }

    @Test
    public void testSubtractDays() {
        String date = "2024-03-15";
        String newDate = DateUtil.subtractDays(5, date);
        assertEquals("2024-03-10", newDate);
    }

    @Test
    public void testDifferenceInDays() {
        String date1 = "2024-03-10";
        String date2 = "2024-03-15";
        long diff = DateUtil.differenceInDays(date1, date2);
        assertEquals(5, diff);
        
        // Test negative difference
        diff = DateUtil.differenceInDays(date2, date1);
        assertEquals(-5, diff);
    }

    @Test
    public void testDayOfWeek() {
        String date = "2024-03-15"; // March 15, 2024 is a Friday
        String dayOfWeek = DateUtil.dayOfWeek(date);
        assertEquals("Friday", dayOfWeek);
    }

    @Test
    public void testParseDate() {
        String dateStr = "2024-03-15";
        String parsed = DateUtil.parseDateToString(dateStr);
        assertEquals(dateStr, parsed);
        
        // Test parsing with different formats
        dateStr = "03/15/2024";
        parsed = DateUtil.parseDateToString(dateStr);
        assertEquals("2024-03-15", parsed);
    }

    @Test
    public void testIsBefore() {
        String date1 = "2024-03-10";
        String date2 = "2024-03-15";
        
        assertTrue(DateUtil.isBefore(date1, date2));
        assertFalse(DateUtil.isBefore(date2, date1));
        assertFalse(DateUtil.isBefore(date1, date1)); // Same date
    }

    @Test
    public void testIsAfter() {
        String date1 = "2024-03-10";
        String date2 = "2024-03-15";
        
        assertTrue(DateUtil.isAfter(date2, date1));
        assertFalse(DateUtil.isAfter(date1, date2));
        assertFalse(DateUtil.isAfter(date1, date1)); // Same date
    }

    @Test
    public void testInvalidDateFormat() {
        String invalidDate = "invalid-date";
        
        Exception exception = assertThrows(RuntimeException.class, () -> {
            DateUtil.parseDateToString(invalidDate);
        });
        
        assertTrue(exception.getMessage().contains("Failed to parse date"));
    }
}
