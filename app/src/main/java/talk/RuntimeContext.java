package talk;

import java.util.HashMap;
import java.util.Map;

public class RuntimeContext {
    private final Map<String, Object> variables = new HashMap<>();
    private int instructionPointer = 0;
    private boolean running = true;

    public Object getVariable(String name) {
        return variables.get(name);
    }

    public void setVariable(String name, Object value) {
        variables.put(name, value);
    }

    public boolean hasVariable(String name) {
        return variables.containsKey(name);
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
}
