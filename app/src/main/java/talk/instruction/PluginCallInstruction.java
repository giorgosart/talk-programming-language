package talk.instruction;

import java.util.List;
import talk.core.Instruction;

/**
 * Instruction for calling a plugin in the Talk language.
 * This represents a call to a plugin with a specific alias and arguments.
 */
public class PluginCallInstruction implements Instruction {
    private final String pluginAlias;
    private final List<String> arguments;
    private final String intoVariable;
    private final int lineNumber;
    
    /**
     * Create a new PluginCallInstruction
     * @param pluginAlias The alias of the plugin to call
     * @param arguments List of arguments to pass to the plugin
     * @param intoVariable Optional variable name to store the result in (can be null)
     * @param lineNumber The line number in the source code
     */
    public PluginCallInstruction(String pluginAlias, List<String> arguments, String intoVariable, int lineNumber) {
        this.pluginAlias = pluginAlias;
        this.arguments = arguments;
        this.intoVariable = intoVariable;
        this.lineNumber = lineNumber;
    }
    
    /**
     * Get the plugin alias
     * @return The plugin alias
     */
    public String getPluginAlias() {
        return pluginAlias;
    }
    
    /**
     * Get the list of arguments
     * @return The arguments to pass to the plugin
     */
    public List<String> getArguments() {
        return arguments;
    }
    
    /**
     * Get the variable name to store the result in
     * @return The variable name, or null if not storing the result
     */
    public String getIntoVariable() {
        return intoVariable;
    }
    
    @Override
    public int getLineNumber() {
        return lineNumber;
    }
    
    @Override
    public String toString() {
        return "PluginCall(" + pluginAlias + ", args=" + arguments + 
               (intoVariable != null ? ", into=" + intoVariable : "") + 
               ")";
    }
}
