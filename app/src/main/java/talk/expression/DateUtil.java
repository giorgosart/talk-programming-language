package talk.expression;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;

/**
 * Utility class to handle date operations for the Talk language.
 */
public class DateUtil {
    private static final DateTimeFormatter ISO_DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    
    /**
     * Returns the current date and time as a string
     * @return Current datetime as string
     */
    public static String now() {
        return LocalDateTime.now().toString();
    }
    
    /**
     * Returns the current date as a string
     * @return Current date as string in ISO format (yyyy-MM-dd)
     */
    public static String today() {
        return LocalDate.now().format(ISO_DATE_FORMAT);
    }
    
    /**
     * Formats a date with the given pattern
     * @param dateStr The date string to format
     * @param pattern The formatting pattern
     * @return Formatted date string
     * @throws DateTimeParseException if the date is invalid
     */
    public static String formatDate(String dateStr, String pattern) {
        try {
            // Try parsing as LocalDateTime first
            LocalDateTime dateTime = LocalDateTime.parse(dateStr);
            return dateTime.format(DateTimeFormatter.ofPattern(pattern));
        } catch (DateTimeParseException e) {
            // If that fails, try parsing as LocalDate
            try {
                LocalDate date = parseDate(dateStr);
                return date.format(DateTimeFormatter.ofPattern(pattern));
            } catch (DateTimeParseException ex) {
                throw new IllegalArgumentException("Invalid date: " + dateStr);
            }
        }
    }
    
    /**
     * Adds a number of days to a date
     * @param days Number of days to add
     * @param dateStr The date to add days to
     * @return New date as string
     */
    public static String addDays(int days, String dateStr) {
        LocalDate date = parseDate(dateStr);
        return date.plusDays(days).format(ISO_DATE_FORMAT);
    }
    
    /**
     * Adds a number of days to a date, handling floating point values
     * by rounding to the nearest integer.
     * @param daysValue A string representing the number of days, which can be a decimal
     * @param dateStr The date to add days to
     * @return New date as string
     */
    public static String addDays(String daysValue, String dateStr) {
        try {
            double days = Double.parseDouble(daysValue);
            return addDays((int)Math.round(days), dateStr);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid number format: " + daysValue);
        }
    }
    
    /**
     * Subtracts a number of days from a date
     * @param days Number of days to subtract
     * @param dateStr The date to subtract days from
     * @return New date as string
     */
    public static String subtractDays(int days, String dateStr) {
        LocalDate date = parseDate(dateStr);
        return date.minusDays(days).format(ISO_DATE_FORMAT);
    }
    
    /**
     * Subtracts a number of days from a date, handling floating point values
     * by rounding to the nearest integer.
     * @param daysValue A string representing the number of days, which can be a decimal
     * @param dateStr The date to subtract days from
     * @return New date as string
     */
    public static String subtractDays(String daysValue, String dateStr) {
        try {
            double days = Double.parseDouble(daysValue);
            return subtractDays((int)Math.round(days), dateStr);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid number format: " + daysValue);
        }
    }
    
    /**
     * Calculates the difference in days between two dates
     * @param dateStr1 First date
     * @param dateStr2 Second date
     * @return Number of days between the dates
     */
    public static long differenceInDays(String dateStr1, String dateStr2) {
        LocalDate date1 = parseDate(dateStr1);
        LocalDate date2 = parseDate(dateStr2);
        return Math.abs(ChronoUnit.DAYS.between(date1, date2));
    }
    
    /**
     * Returns the day of week for a given date
     * @param dateStr The date to get day of week from
     * @return Day of week as string
     */
    public static String dayOfWeek(String dateStr) {
        LocalDate date = parseDate(dateStr);
        return date.getDayOfWeek().toString();
    }
    
    /**
     * Checks if the first date is before the second date
     * @param dateStr1 First date
     * @param dateStr2 Second date
     * @return true if the first date is before the second
     */
    public static boolean isBefore(String dateStr1, String dateStr2) {
        LocalDate date1 = parseDate(dateStr1);
        LocalDate date2 = parseDate(dateStr2);
        return date1.isBefore(date2);
    }
    
    /**
     * Checks if the first date is after the second date
     * @param dateStr1 First date
     * @param dateStr2 Second date
     * @return true if the first date is after the second
     */
    public static boolean isAfter(String dateStr1, String dateStr2) {
        LocalDate date1 = parseDate(dateStr1);
        LocalDate date2 = parseDate(dateStr2);
        return date1.isAfter(date2);
    }
    
    /**
     * Parses a string into a date object
     * @param dateStr Date string to parse
     * @return Parsed LocalDate object
     * @throws DateTimeParseException if the string cannot be parsed
     */
    public static LocalDate parseDate(String dateStr) {
        try {
            return LocalDate.parse(dateStr);
        } catch (DateTimeParseException e) {
            // Try with ISO format
            try {
                return LocalDate.parse(dateStr, ISO_DATE_FORMAT);
            } catch (DateTimeParseException ex) {
                // If all parsing attempts fail, throw exception
                throw new IllegalArgumentException("Invalid date format: " + dateStr);
            }
        }
    }
    
    /**
     * Parses a string into a date and returns it in ISO format
     * @param dateStr Date string to parse
     * @return Date in ISO format
     */
    public static String parseDateToString(String dateStr) {
        LocalDate date = parseDate(dateStr);
        return date.format(ISO_DATE_FORMAT);
    }
}
