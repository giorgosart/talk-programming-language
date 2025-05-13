package talk;

import java.util.HashMap;
import java.util.Map;

public class RuntimeContext {
    private final Map<String, Object> variables = new HashMap<>();
    private int instructionPointer = 0;
    private boolean running = true;

    private final Map<String, FunctionDefinitionInstruction> functions = new HashMap<>();
    private final java.util.Deque<Map<String, Object>> variableStack = new java.util.ArrayDeque<>();

    public RuntimeContext() {
        variableStack.push(variables);
    }

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
