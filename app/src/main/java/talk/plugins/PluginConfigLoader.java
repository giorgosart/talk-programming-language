package talk.plugins;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;
import java.lang.reflect.Method;

/**
 * Loads plugin configurations from properties files.
 * This class is responsible for loading plugin definitions
 * from configuration files and registering them with the PluginRegistry.
 */
public class PluginConfigLoader {
    
    private static final String DEFAULT_CONFIG_PATH = "plugins/talk-plugins.properties";
    
    /**
     * Load plugins from the default configuration file
     * @return true if plugins were loaded successfully, false otherwise
     */
    public static boolean loadDefaultPlugins() {
        String userDir = System.getProperty("user.dir");
        Path configPath = Paths.get(userDir, DEFAULT_CONFIG_PATH);
        
        if (Files.exists(configPath)) {
            return loadPlugins(configPath.toString());
        }
        
        // Try to find the config file in the classpath
        try (InputStream in = PluginConfigLoader.class.getClassLoader().getResourceAsStream(DEFAULT_CONFIG_PATH)) {
            if (in != null) {
                Properties props = new Properties();
                props.load(in);
                registerPluginsFromProperties(props);
                return true;
            }
        } catch (IOException e) {
            System.err.println("Error loading default plugin configuration: " + e.getMessage());
        }
        
        return false;
    }
    
    /**
     * Load plugins from a specified configuration file
     * @param configFilePath The path to the plugin configuration file
     * @return true if plugins were loaded successfully, false otherwise
     */
    public static boolean loadPlugins(String configFilePath) {
        try (FileInputStream fis = new FileInputStream(configFilePath)) {
            Properties props = new Properties();
            props.load(fis);
            registerPluginsFromProperties(props);
            return true;
        } catch (IOException e) {
            System.err.println("Error loading plugin configuration from " + configFilePath + ": " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Register plugins from the loaded properties
     * @param props The properties containing plugin definitions
     */
    private static void registerPluginsFromProperties(Properties props) {
        PluginRegistry registry = PluginRegistry.getInstance();
        
        for (String name : props.stringPropertyNames()) {
            if (name.startsWith("plugin.")) {
                String alias = name.substring("plugin.".length());
                String classMethodRef = props.getProperty(name);
                
                try {
                    registerPluginFromReference(registry, alias, classMethodRef);
                } catch (Exception e) {
                    System.err.println("Error registering plugin '" + alias + "': " + e.getMessage());
                }
            }
        }
    }
    
    /**
     * Register a plugin from a class.method reference string
     * Format: "fully.qualified.ClassName#methodName"
     * 
     * @param registry The plugin registry
     * @param alias The alias for the plugin
     * @param classMethodRef The class#method reference string
     * @throws Exception if the plugin cannot be registered
     */
    private static void registerPluginFromReference(PluginRegistry registry, String alias, String classMethodRef) throws Exception {
        int hashIdx = classMethodRef.indexOf('#');
        if (hashIdx == -1) {
            throw new IllegalArgumentException("Invalid class#method reference: " + classMethodRef);
        }
        
        String className = classMethodRef.substring(0, hashIdx);
        String methodName = classMethodRef.substring(hashIdx + 1);
        
        Class<?> clazz = Class.forName(className);
        
        // Find the right method - look for both static and instance methods with Object... params
        Method method = null;
        
        try {
            // Try to find a static method first
            method = clazz.getMethod(methodName, Object[].class);
            if (java.lang.reflect.Modifier.isStatic(method.getModifiers())) {
                // Static method with Object... parameters
                final Method staticMethod = method;
                registry.register(alias, args -> staticMethod.invoke(null, new Object[] { args }));
                return;
            }
        } catch (NoSuchMethodException e) {
            // Method not found, try other signatures
        }
        
        try {
            // Try to find an instance method with Object... parameters
            method = clazz.getMethod(methodName, Object[].class);
            final Method instanceMethod = method;
            final Object instance = clazz.getDeclaredConstructor().newInstance();
            registry.register(alias, args -> instanceMethod.invoke(instance, new Object[] { args }));
            return;
        } catch (NoSuchMethodException e) {
            // Method not found, try other signatures
        }
        
        throw new IllegalArgumentException("No suitable method found for plugin: " + classMethodRef);
    }
}
