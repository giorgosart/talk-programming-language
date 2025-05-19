package talk.core;

import java.util.HashMap;
import java.util.Map;
import java.util.ArrayList;
import java.util.List;

import talk.instruction.FunctionDefinitionInstruction;
import talk.instruction.BeforeEachInstruction;
import talk.instruction.AfterEachInstruction;

public class RuntimeContext {
    private final Map<String, Object> variables = new HashMap<>();
    private int instructionPointer = 0;
    private boolean running = true;

    private final Map<String, FunctionDefinitionInstruction> functions = new HashMap<>();
    private final java.util.Deque<Map<String, Object>> variableStack = new java.util.ArrayDeque<>();
    
    // Test framework related fields
    private final List<TestResult> testResults = new ArrayList<>();
    private BeforeEachInstruction beforeEachBlock = null;
    private AfterEachInstruction afterEachBlock = null;
    private String currentTestName = null;

    public RuntimeContext() {
        variableStack.push(variables);
    }

    // Test framework methods
    public void addTestResult(TestResult result) {
        testResults.add(result);
    }
    
    public List<TestResult> getTestResults() {
        return testResults;
    }
    
    public void setBeforeEachBlock(BeforeEachInstruction block) {
        this.beforeEachBlock = block;
    }
    
    public BeforeEachInstruction getBeforeEachBlock() {
        return beforeEachBlock;
    }
    
    public void setAfterEachBlock(AfterEachInstruction block) {
        this.afterEachBlock = block;
    }
    
    public AfterEachInstruction getAfterEachBlock() {
        return afterEachBlock;
    }
    
    public void setCurrentTestName(String testName) {
        this.currentTestName = testName;
    }
    
    public String getCurrentTestName() {
        return currentTestName;
    }
    
    public void printTestSummary() {
        System.out.println("\n--- Test Results ---");
        int passCount = 0;
        int failCount = 0;
        
        for (TestResult result : testResults) {
            System.out.println(result);
            if (result.isPassed()) {
                passCount++;
            } else {
                failCount++;
            }
        }
        
        System.out.println("\nSummary: " + passCount + " passed, " + failCount + " failed, " + testResults.size() + " total");
    }

    // Existing methods
    public void registerFunction(String name, FunctionDefinitionInstruction def) {
        functions.put(name, def);
    }

    public FunctionDefinitionInstruction getFunction(String name) {
        return functions.get(name);
    }

    public boolean hasFunction(String name) {
        return functions.containsKey(name);
    }

    public void pushScope() {
        variableStack.push(new HashMap<>());
    }

    public void popScope() {
        if (variableStack.size() > 1) variableStack.pop();
    }

    public Object getVariable(String name) {
        for (Map<String, Object> scope : variableStack) {
            if (scope.containsKey(name)) return scope.get(name);
        }
        return null;
    }

    public void setVariable(String name, Object value) {
        // First check if the variable exists in the current scope
        variableStack.peek().put(name, value);
    }
    
    /**
     * Sets a variable in the current scope, preventing modification of variables in outer scopes
     * by default. This implements proper variable shadowing.
     * 
     * @param name The variable name
     * @param value The value to set
     * @param allowOuterScopeModification Whether to allow modifying variables in outer scopes
     */
    public void setVariableScoped(String name, Object value, boolean allowOuterScopeModification) {
        if (allowOuterScopeModification) {
            // Look for the variable in existing scopes and update it
            for (Map<String, Object> scope : variableStack) {
                if (scope.containsKey(name)) {
                    scope.put(name, value);
                    return;
                }
            }
        }
        // If not found in any scope or if outer scope modification is not allowed,
        // create/update the variable in the current scope
        variableStack.peek().put(name, value);
    }

    public boolean hasVariable(String name) {
        for (Map<String, Object> scope : variableStack) {
            if (scope.containsKey(name)) return true;
        }
        return false;
    }

    public void removeVariable(String name) {
        variableStack.peek().remove(name);
    }

    public int getInstructionPointer() {
        return instructionPointer;
    }

    public void setInstructionPointer(int pointer) {
        this.instructionPointer = pointer;
    }

    public boolean isRunning() {
        return running;
    }

    public void stop() {
        this.running = false;
    }

    // Returns true if currently in a local (non-global) scope
    public boolean isLocalScope() {
        return variableStack.size() > 1;
    }
}
