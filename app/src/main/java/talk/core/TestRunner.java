package talk.core;

import java.io.File;
import java.util.List;

import talk.Parser;
import talk.runtime.InstructionExecutor;

/**
 * A command-line interface for running Talk language test files.
 */
public class TestRunner {
    
    /**
     * Run tests from a single Talk file
     * 
     * @param filePath The path to the Talk file containing tests
     * @return True if all tests passed, false otherwise
     */
    public static boolean runTests(String filePath) {
        try {
            File file = new File(filePath);
            if (!file.exists() || !file.isFile()) {
                System.err.println("Error: File not found: " + filePath);
                return false;
            }
            
            System.out.println("Running tests from " + filePath);
            
            Parser parser = new Parser();
            List<Instruction> instructions = parser.parse(file);
            
            RuntimeContext context = new RuntimeContext();
            InstructionExecutor executor = new InstructionExecutor(context);
            
            // Execute all instructions in the file
            for (Instruction instruction : instructions) {
                executor.execute(instruction);
            }
            
            // Print test results summary
            List<TestResult> results = context.getTestResults();
            context.printTestSummary();
            
            // Return true if all tests passed
            int failCount = 0;
            for (TestResult result : results) {
                if (!result.isPassed()) {
                    failCount++;
                }
            }
            
            return failCount == 0;
            
        } catch (Exception e) {
            System.err.println("Error running tests: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
}
