package talk.plugins;

import java.time.LocalDateTime;
import java.util.UUID;
import java.util.Map;
import java.util.Properties;

/**
 * Register built-in plugins for common operations.
 * This class is responsible for registering plugins that are available by default
 * in the Talk programming language.
 */
public class BuiltInPlugins {
    
    /**
     * Register all built-in plugins
     */
    public static void registerAll() {
        PluginRegistry registry = PluginRegistry.getInstance();
        
        // UUID generation
        registry.register("generate uuid", args -> UUID.randomUUID().toString());
        
        // System information
        registry.register("system property", args -> {
            if (args.length == 0) {
                throw new IllegalArgumentException("Property name is required");
            }
            return System.getProperty(args[0].toString());
        });
        
        registry.register("get environment variable", args -> {
            if (args.length == 0) {
                throw new IllegalArgumentException("Environment variable name is required");
            }
            return System.getenv(args[0].toString());
        });
        
        registry.register("system info", args -> {
            Properties props = System.getProperties();
            StringBuilder sb = new StringBuilder();
            sb.append("OS: ").append(props.getProperty("os.name")).append(" ").append(props.getProperty("os.version"))
              .append("\nJava: ").append(props.getProperty("java.version"))
              .append("\nUser: ").append(props.getProperty("user.name"));
            return sb.toString();
        });
        
        // Date and time utilities
        registry.register("current timestamp", args -> System.currentTimeMillis());
        
        registry.register("format timestamp", args -> {
            if (args.length < 1) {
                throw new IllegalArgumentException("Timestamp is required");
            }
            
            java.text.SimpleDateFormat formatter;
            if (args.length >= 2) {
                formatter = new java.text.SimpleDateFormat(args[1].toString());
            } else {
                formatter = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            }
            
            try {
                long timestamp = Long.parseLong(args[0].toString());
                return formatter.format(new java.util.Date(timestamp));
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("Invalid timestamp format");
            }
        });
        
        // Math operations
        registry.register("random number", args -> {
            if (args.length >= 2) {
                int min = Integer.parseInt(args[0].toString());
                int max = Integer.parseInt(args[1].toString());
                return min + (int)(Math.random() * ((max - min) + 1));
            } else if (args.length == 1) {
                int max = Integer.parseInt(args[0].toString());
                return (int)(Math.random() * max);
            } else {
                return Math.random();
            }
        });
        
        // String operations
        registry.register("to uppercase", args -> {
            if (args.length == 0) {
                throw new IllegalArgumentException("String is required");
            }
            return args[0].toString().toUpperCase();
        });
        
        registry.register("to lowercase", args -> {
            if (args.length == 0) {
                throw new IllegalArgumentException("String is required");
            }
            return args[0].toString().toLowerCase();
        });
        
        // JSON utilities
        registry.register("parse json", args -> {
            if (args.length == 0) {
                throw new IllegalArgumentException("JSON string is required");
            }
            try {
                javax.json.JsonReader jsonReader = javax.json.Json.createReader(
                    new java.io.StringReader(args[0].toString()));
                return jsonReader.readObject();
            } catch (Exception e) {
                throw new IllegalArgumentException("Invalid JSON: " + e.getMessage());
            }
        });
    }
}
