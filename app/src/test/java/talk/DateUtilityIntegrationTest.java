package talk;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.io.TempDir;
import static org.junit.jupiter.api.Assertions.*;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.PrintStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import talk.execution.TalkExecutor;

/**
 * Integration tests for date utility functionality in Talk scripts
 */
public class DateUtilityIntegrationTest {

    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private final PrintStream originalOut = System.out;
    
    @TempDir
    Path tempDir;
    
    @BeforeEach
    public void setUpStreams() {
        System.setOut(new PrintStream(outContent));
    }
    
    @BeforeEach
    public void restoreStreams() {
        System.setOut(originalOut);
    }
    
    @Test
    public void testBasicDateExpressions() throws IOException {
        String script = 
            "# Test basic date expressions\n" +
            "variable today_value\n" +
            "set today_value to today\n" +
            "write \"Today: \" in console.txt\n" +
            "write today_value in console.txt\n" +
            "\n" +
            "variable now_value\n" +
            "set now_value to now\n" +
            "write \"Now: \" in console.txt\n" +
            "write now_value in console.txt\n";
            
        Path scriptFile = tempDir.resolve("basic_date_test.talk");
        Files.writeString(scriptFile, script);
        
        // Run the script
        TalkExecutor executor = new TalkExecutor();
        executor.execute(scriptFile.toString());
        
        // Check console.txt contains today's date
        Path outputFile = tempDir.resolve("console.txt");
        assertTrue(Files.exists(outputFile), "Output file should be created");
        
        String output = Files.readString(outputFile);
        String today = LocalDate.now().toString();
        
        assertTrue(output.contains("Today: "), "Output should contain 'Today: '");
        assertTrue(output.contains(today), "Output should contain today's date");
        assertTrue(output.contains("Now: "), "Output should contain 'Now: '");
    }
    
    @Test
    public void testDateArithmetic() throws IOException {
        String script = 
            "# Test date arithmetic\n" +
            "variable today_value\n" +
            "set today_value to today\n" +
            "\n" +
            "variable tomorrow\n" +
            "set tomorrow to add 1 days to today_value\n" +
            "write \"Tomorrow: \" in console.txt\n" +
            "write tomorrow in console.txt\n" +
            "\n" +
            "variable yesterday\n" +
            "set yesterday to subtract 1 days from today_value\n" +
            "write \"Yesterday: \" in console.txt\n" +
            "write yesterday in console.txt\n" +
            "\n" +
            "variable diff\n" +
            "set diff to difference in days between yesterday and tomorrow\n" +
            "write \"Difference in days: \" in console.txt\n" +
            "write diff in console.txt\n";
            
        Path scriptFile = tempDir.resolve("date_arithmetic_test.talk");
        Files.writeString(scriptFile, script);
        
        // Run the script
        TalkExecutor executor = new TalkExecutor();
        executor.execute(scriptFile.toString());
        
        // Check console.txt
        Path outputFile = tempDir.resolve("console.txt");
        assertTrue(Files.exists(outputFile), "Output file should be created");
        
        String output = Files.readString(outputFile);
        LocalDate today = LocalDate.now();
        String tomorrow = today.plusDays(1).toString();
        String yesterday = today.minusDays(1).toString();
        
        assertTrue(output.contains("Tomorrow: "), "Output should contain 'Tomorrow: '");
        assertTrue(output.contains(tomorrow), "Output should contain tomorrow's date");
        assertTrue(output.contains("Yesterday: "), "Output should contain 'Yesterday: '");
        assertTrue(output.contains(yesterday), "Output should contain yesterday's date");
        assertTrue(output.contains("Difference in days: "), "Output should contain 'Difference in days: '");
        assertTrue(output.contains("2"), "Difference should be 2 days");
    }
    
    @Test
    public void testDateFormatting() throws IOException {
        String script = 
            "# Test date formatting\n" +
            "variable today_value\n" +
            "set today_value to today\n" +
            "\n" +
            "variable formatted\n" +
            "set formatted to format date today_value as \"MM/dd/yyyy\"\n" +
            "write \"Formatted date: \" in console.txt\n" +
            "write formatted in console.txt\n";
            
        Path scriptFile = tempDir.resolve("date_formatting_test.talk");
        Files.writeString(scriptFile, script);
        
        // Run the script
        TalkExecutor executor = new TalkExecutor();
        executor.execute(scriptFile.toString());
        
        // Check console.txt
        Path outputFile = tempDir.resolve("console.txt");
        assertTrue(Files.exists(outputFile), "Output file should be created");
        
        String output = Files.readString(outputFile);
        String formattedToday = LocalDate.now().format(DateTimeFormatter.ofPattern("MM/dd/yyyy"));
        
        assertTrue(output.contains("Formatted date: "), "Output should contain 'Formatted date: '");
        assertTrue(output.contains(formattedToday), "Output should contain formatted date");
    }
    
    @Test
    public void testDayOfWeekAndParsing() throws IOException {
        String script = 
            "# Test day of week and date parsing\n" +
            "variable today_value\n" +
            "set today_value to today\n" +
            "\n" +
            "variable dow\n" +
            "set dow to day of week of today_value\n" +
            "write \"Day of week: \" in console.txt\n" +
            "write dow in console.txt\n" +
            "\n" +
            "variable parsed\n" +
            "set parsed to parse date \"2024-03-15\"\n" +
            "write \"Parsed date: \" in console.txt\n" +
            "write parsed in console.txt\n";
            
        Path scriptFile = tempDir.resolve("day_of_week_test.talk");
        Files.writeString(scriptFile, script);
        
        // Run the script
        TalkExecutor executor = new TalkExecutor();
        executor.execute(scriptFile.toString());
        
        // Check console.txt
        Path outputFile = tempDir.resolve("console.txt");
        assertTrue(Files.exists(outputFile), "Output file should be created");
        
        String output = Files.readString(outputFile);
        String dayOfWeek = LocalDate.now().getDayOfWeek().toString();
        dayOfWeek = dayOfWeek.charAt(0) + dayOfWeek.substring(1).toLowerCase();
        
        assertTrue(output.contains("Day of week: "), "Output should contain 'Day of week: '");
        assertTrue(output.contains("Parsed date: "), "Output should contain 'Parsed date: '");
        assertTrue(output.contains("2024-03-15"), "Output should contain parsed date");
    }
    
    @Test
    public void testDateComparisons() throws IOException {
        String script = 
            "# Test date comparisons\n" +
            "variable today_value\n" +
            "set today_value to today\n" +
            "\n" +
            "variable future\n" +
            "set future to add 7 days to today_value\n" +
            "\n" +
            "variable past\n" +
            "set past to subtract 7 days from today_value\n" +
            "\n" +
            "if past is before today_value then\n" +
            "    write \"Past is before today\" in console.txt\n" +
            "\n" +
            "if future is after today_value then\n" +
            "    write \"Future is after today\" in console.txt\n" +
            "\n" +
            "if today_value is after past then\n" +
            "    write \"Today is after past\" in console.txt\n" +
            "\n" +
            "if today_value is before future then\n" +
            "    write \"Today is before future\" in console.txt\n";
            
        Path scriptFile = tempDir.resolve("date_comparison_test.talk");
        Files.writeString(scriptFile, script);
        
        // Run the script
        TalkExecutor executor = new TalkExecutor();
        executor.execute(scriptFile.toString());
        
        // Check console.txt
        Path outputFile = tempDir.resolve("console.txt");
        assertTrue(Files.exists(outputFile), "Output file should be created");
        
        String output = Files.readString(outputFile);
        
        assertTrue(output.contains("Past is before today"), "Output should contain 'Past is before today'");
        assertTrue(output.contains("Future is after today"), "Output should contain 'Future is after today'");
        assertTrue(output.contains("Today is after past"), "Output should contain 'Today is after past'");
        assertTrue(output.contains("Today is before future"), "Output should contain 'Today is before future'");
    }
}
