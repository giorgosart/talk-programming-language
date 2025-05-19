package talk.runtime;

import talk.core.Instruction;
import talk.core.RuntimeContext;
import talk.core.TestResult;
import talk.exception.TalkRuntimeException;
import talk.expression.ExpressionResolver;
import talk.instruction.AfterEachInstruction;
import talk.instruction.BeforeEachInstruction;
import talk.instruction.ExpectInstruction;
import talk.instruction.TestBlockInstruction;

/**
 * Extension methods for the InstructionExecutor class to handle test instructions.
 * This is used to avoid modifying the core InstructionExecutor class directly.
 */
public class InstructionExecutorTestExtensions {
    
    /**
     * Execute an ExpectInstruction
     */
    public static void executeExpectInstruction(RuntimeContext context, ExpressionResolver resolver, ExpectInstruction instruction) {
        Object actualResult = resolver.resolve(instruction.getExpression());
        Object expectedResult = resolver.resolve(instruction.getExpectedValue());
        
        boolean passed = false;
        String message = "";
        
        // Compare the actual and expected results
        if (actualResult == null && expectedResult == null) {
            passed = true;
        } else if (actualResult != null && expectedResult != null) {
            if (actualResult instanceof Number && expectedResult instanceof Number) {
                // Convert to double for numeric comparison
                double actualNum = ((Number) actualResult).doubleValue();
                double expectedNum = ((Number) expectedResult).doubleValue();
                passed = Math.abs(actualNum - expectedNum) < 0.0001;  // Allow for small floating point differences
            } else {
                passed = actualResult.equals(expectedResult);
            }
        }
        
        if (!passed) {
            message = "Expected: " + expectedResult + ", but got: " + actualResult;
        }
        
        // Create and add the test result
        TestResult result = new TestResult(
            context.getCurrentTestName(),
            passed,
            passed ? "Test passed" : message,
            instruction.getLineNumber()
        );
        
        context.addTestResult(result);
    }
    
    /**
     * Execute a TestBlockInstruction
     */
    public static void executeTestBlockInstruction(InstructionExecutor executor, RuntimeContext context, TestBlockInstruction instruction) {
        String testName = instruction.getDescription();
        
        // Set the current test name for later assertions
        context.setCurrentTestName(testName);
        
        try {
            // Run the "before each" hook if it exists
            BeforeEachInstruction beforeEach = context.getBeforeEachBlock();
            if (beforeEach != null) {
                context.pushScope(); // Create scope for before each
                try {
                    for (Instruction instr : beforeEach.getInstructions()) {
                        executor.execute(instr);
                    }
                } finally {
                    context.popScope();
                }
            }
            
            // Run the test block
            context.pushScope(); // Create scope for the test itself
            try {
                for (Instruction instr : instruction.getInstructions()) {
                    executor.execute(instr);
                }
            } finally {
                context.popScope();
            }
            
            // Run the "after each" hook if it exists
            AfterEachInstruction afterEach = context.getAfterEachBlock();
            if (afterEach != null) {
                context.pushScope(); // Create scope for after each
                try {
                    for (Instruction instr : afterEach.getInstructions()) {
                        executor.execute(instr);
                    }
                } finally {
                    context.popScope();
                }
            }
        } finally {
            // Reset the current test name
            context.setCurrentTestName(null);
        }
    }
    
    /**
     * Execute a BeforeEachInstruction
     */
    public static void executeBeforeEachInstruction(RuntimeContext context, BeforeEachInstruction instruction) {
        context.setBeforeEachBlock(instruction);
    }
    
    /**
     * Execute an AfterEachInstruction
     */
    public static void executeAfterEachInstruction(RuntimeContext context, AfterEachInstruction instruction) {
        context.setAfterEachBlock(instruction);
    }
    
    /**
     * Determine if the given instruction is a test instruction
     */
    public static boolean isTestInstruction(Instruction instruction) {
        return instruction instanceof ExpectInstruction 
            || instruction instanceof TestBlockInstruction
            || instruction instanceof BeforeEachInstruction
            || instruction instanceof AfterEachInstruction;
    }
    
    /**
     * Execute a test instruction
     */
    public static void executeTestInstruction(InstructionExecutor executor, RuntimeContext context, ExpressionResolver resolver, Instruction instruction) {
        if (instruction instanceof ExpectInstruction) {
            executeExpectInstruction(context, resolver, (ExpectInstruction) instruction);
        } else if (instruction instanceof TestBlockInstruction) {
            executeTestBlockInstruction(executor, context, (TestBlockInstruction) instruction);
        } else if (instruction instanceof BeforeEachInstruction) {
            executeBeforeEachInstruction(context, (BeforeEachInstruction) instruction);
        } else if (instruction instanceof AfterEachInstruction) {
            executeAfterEachInstruction(context, (AfterEachInstruction) instruction);
        } else {
            throw new TalkRuntimeException("Unsupported test instruction type: " + instruction.getClass().getSimpleName(), instruction.getLineNumber());
        }
    }
}
