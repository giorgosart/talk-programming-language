package talk.plugins;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * Registry for Talk language plugins.
 * This class maintains a mapping between Talk-friendly command aliases
 * and the Java methods they should execute.
 */
public class PluginRegistry {
    // Singleton instance
    private static final PluginRegistry instance = new PluginRegistry();
    
    // Map from Talk command aliases to plugin handlers
    private final Map<String, PluginHandler> plugins = new HashMap<>();
    
    // Private constructor for singleton pattern
    private PluginRegistry() {}
    
    /**
     * Get the singleton instance of the PluginRegistry
     * @return The PluginRegistry instance
     */
    public static PluginRegistry getInstance() {
        return instance;
    }
    
    /**
     * Register a plugin that takes no arguments
     * @param alias The Talk-friendly command name
     * @param handler The function that will handle the command
     */
    public void register(String alias, PluginHandler handler) {
        plugins.put(alias.toLowerCase(), handler);
    }
    
    /**
     * Check if a plugin with the given alias exists
     * @param alias The Talk-friendly command name
     * @return true if the plugin exists, false otherwise
     */
    public boolean hasPlugin(String alias) {
        return plugins.containsKey(alias.toLowerCase());
    }
    
    /**
     * Execute a plugin with the given alias and arguments
     * @param alias The Talk-friendly command name
     * @param args The arguments to pass to the plugin
     * @return The result of the plugin execution
     * @throws Exception if the plugin execution fails
     */
    public Object execute(String alias, Object... args) throws Exception {
        if (!hasPlugin(alias)) {
            throw new IllegalArgumentException("Plugin not found: " + alias);
        }
        
        PluginHandler handler = plugins.get(alias.toLowerCase());
        return handler.execute(args);
    }
    
    /**
     * Get all registered plugins
     * @return A map of plugin aliases to handlers
     */
    public Map<String, PluginHandler> getAllPlugins() {
        return new HashMap<>(plugins);
    }
}
