package talk.core;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.mockito.Mockito;
import static org.mockito.Mockito.*;

import org.jline.reader.*;
import org.jline.terminal.Terminal;

/**
 * Unit tests for the JLineEnabledRepl class
 * Note: These tests mock the JLine components to test the functionality without
 * requiring a real terminal.
 */
public class JLineEnabledReplTest {
    
    private JLineEnabledRepl repl;
    private LineReader mockLineReader;
    private Terminal mockTerminal;
    
    @BeforeEach
    public void setup() throws Exception {
        // Create mocks for JLine components
        mockLineReader = Mockito.mock(LineReader.class);
        mockTerminal = Mockito.mock(Terminal.class);
        
        // Create an instance of the REPL
        repl = new JLineEnabledRepl();
        
        // Use reflection to inject our mocks
        java.lang.reflect.Field lineReaderField = JLineEnabledRepl.class.getDeclaredField("lineReader");
        lineReaderField.setAccessible(true);
        lineReaderField.set(repl, mockLineReader);
        
        java.lang.reflect.Field terminalField = JLineEnabledRepl.class.getDeclaredField("terminal");
        terminalField.setAccessible(true);
        terminalField.set(repl, mockTerminal);
    }
    
    @Test
    @DisplayName("Test basic REPL interaction with JLine")
    public void testBasicInteraction() throws Exception {
        // Setup input behavior
        when(mockLineReader.readLine(anyString()))
            .thenReturn("write \"Hello, World\"")  // First command
            .thenReturn("exit");                  // Exit command
        
        // Run the REPL (it will exit after the second command)
        repl.start();
        
        // Verify that readLine was called twice
        verify(mockLineReader, times(2)).readLine(anyString());
    }
    
    @Test
    @DisplayName("Test handling of null input (Ctrl+D)")
    public void testNullInput() throws Exception {
        // Setup input behavior to simulate Ctrl+D
        when(mockLineReader.readLine(anyString()))
            .thenReturn(null);
        
        // Run the REPL (it should exit with null input)
        repl.start();
        
        // Verify readLine was called once
        verify(mockLineReader, times(1)).readLine(anyString());
    }
    
    @Test
    @DisplayName("Test handling of UserInterruptException (Ctrl+C)")
    public void testUserInterrupt() throws Exception {
        // Setup input to throw UserInterruptException
        when(mockLineReader.readLine(anyString()))
            .thenThrow(new UserInterruptException("Test interrupt"))
            .thenReturn("exit");
        
        // Run the REPL (it should handle the exception and continue)
        repl.start();
        
        // Verify readLine was called twice
        verify(mockLineReader, times(2)).readLine(anyString());
    }
}
