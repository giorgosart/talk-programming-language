package talk.plugins;

/**
 * Interface for plugin handlers in the Talk language.
 * Implementations of this interface can be registered with the PluginRegistry
 * to extend the Talk language with custom functionality.
 */
@FunctionalInterface
public interface PluginHandler {
    /**
     * Execute the plugin with the given arguments
     * @param args The arguments passed to the plugin
     * @return The result of the plugin execution
     * @throws Exception if the plugin execution fails
     */
    Object execute(Object... args) throws Exception;
}
