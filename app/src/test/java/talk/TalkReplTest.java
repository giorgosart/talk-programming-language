package talk;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import talk.core.Instruction;
import talk.core.RuntimeContext;
import talk.core.TalkRepl;
import talk.core.Tokenizer;
import talk.expression.ExpressionResolver;
import talk.runtime.InstructionExecutor;

@ExtendWith(MockitoExtension.class)
public class TalkReplTest {

    private TalkRepl repl;
    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private final ByteArrayOutputStream errContent = new ByteArrayOutputStream();
    private final PrintStream originalOut = System.out;
    private final PrintStream originalErr = System.err;

    @Mock
    private BufferedReader mockReader;
    
    @Mock
    private RuntimeContext mockContext;
    
    @Mock
    private InstructionExecutor mockExecutor;
    
    @Mock
    private ExpressionResolver mockResolver;

    @BeforeEach
    public void setUpStreams() {
        System.setOut(new PrintStream(outContent));
        System.setErr(new PrintStream(errContent));
    }

    @AfterEach
    public void restoreStreams() {
        System.setOut(originalOut);
        System.setErr(originalErr);
    }
    
    @Test
    public void testStartAndQuit() throws IOException {
        // Setup mock reader to simulate user input "quit"
        when(mockReader.readLine()).thenReturn("quit");
        
        // Create a repl instance with the mock
        TalkRepl repl = new TalkRepl();
        repl.setReaderForTesting(mockReader);
        
        // Run the REPL
        repl.start();
        
        // Verify the output contains the welcome message and goodbye message
        String output = outContent.toString();
        assertTrue(output.contains("Talk REPL v0.1"), "Welcome message should be displayed");
        assertTrue(output.contains("Goodbye!"), "Goodbye message should be displayed when quitting");
    }

    @Test
    public void testHelp() throws IOException {
        // Setup mock reader to simulate "help" command followed by "quit"
        when(mockReader.readLine()).thenReturn("help", "quit");
        
        // Create a repl instance with the mock
        TalkRepl repl = new TalkRepl();
        repl.setReaderForTesting(mockReader);
        
        // Run the REPL
        repl.start();
        
        // Verify the output contains help information
        String output = outContent.toString();
        assertTrue(output.contains("Talk REPL Help:"), "Help information should be displayed");
        assertTrue(output.contains("Examples:"), "Help should contain examples");
    }
    
    @Test
    public void testHistoryCommand() throws IOException {
        // Setup mock reader to simulate commands and then history display
        when(mockReader.readLine()).thenReturn(
            "variable x = 10", 
            "set y = 20", 
            "history", 
            "quit"
        );
        
        // Create a repl instance with the mock
        TalkRepl repl = new TalkRepl();
        repl.setReaderForTesting(mockReader);
        
        // Run the REPL
        repl.start();
        
        // Verify history command shows the executed commands
        String output = outContent.toString();
        assertTrue(output.contains("Command History:"), "History command should display history header");
        assertTrue(output.contains("variable x = 10"), "History should contain first command");
        assertTrue(output.contains("set y = 20"), "History should contain second command");
    }
    
    @Test
    public void testClearCommand() throws IOException {
        // Setup mock reader to simulate commands, then clear, then history
        when(mockReader.readLine()).thenReturn(
            "variable x = 10", 
            "clear", 
            "history", 
            "quit"
        );
        
        // Create a repl instance with the mock
        TalkRepl repl = new TalkRepl();
        repl.setReaderForTesting(mockReader);
        
        // Run the REPL
        repl.start();
        
        // Verify history was cleared
        String output = outContent.toString();
        assertTrue(output.contains("History cleared."), "Clear message should be shown");
        assertTrue(output.contains("History is empty."), "History should be empty after clear");
    }
    
    @Test
    public void testExpressionEvaluation() throws IOException {
        // Setup mock resolver to return a result for an expression
        TalkRepl repl = new TalkRepl();
        repl.setResolverForTesting((expr) -> 15);  // Mock resolver that always returns 15
        
        // Simulate evaluating an expression
        repl.evaluateAndPrintForTesting("5 + 10");
        
        // Verify the result
        String output = outContent.toString();
        assertTrue(output.contains("15"), "Expression result should be displayed");
    }
    
    @Test
    public void testInstructionExecution() throws IOException {
        // Setup mock for instruction execution
        TalkRepl repl = new TalkRepl();
        repl.setExecutorForTesting((instr) -> "Hello World");  // Mock executor that returns "Hello World"
        
        // Simulate evaluating an instruction
        repl.evaluateAndPrintForTesting("write \"Hello World\"");
        
        // Verify the result
        String output = outContent.toString();
        assertTrue(output.contains("\"Hello World\""), "Instruction result should be displayed");
    }
    
    @Test
    public void testErrorHandling() throws IOException {
        // Setup mock resolver to throw an exception
        TalkRepl repl = new TalkRepl();
        repl.setResolverForTesting((expr) -> { 
            throw new RuntimeException("Test error message");
        });
        
        // Simulate evaluating an expression that will cause an error
        repl.evaluateAndPrintForTesting("x + y");
        
        // Verify error output
        String errorOutput = errContent.toString();
        assertTrue(errorOutput.contains("Expression evaluation error"), "Error type should be shown");
        assertTrue(errorOutput.contains("Test error message"), "Error message should be shown");
    }
}
