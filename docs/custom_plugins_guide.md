# How to Create Custom Plugins for Talk

This guide explains how to create and register custom plugins for the Talk programming language, allowing you to extend its functionality with your own Java code.

## Overview

Plugins in Talk provide a way to:
- Access Java libraries and APIs from Talk scripts
- Create reusable functionality with natural language aliases
- Extend the language without modifying the interpreter

## Creating a Plugin

There are three ways to create plugins for Talk:

### 1. Using Lambda Expressions

The simplest way to create a plugin is with a lambda expression:

```java
import talk.plugins.PluginRegistry;

public class MyPlugins {
    public static void registerPlugins() {
        PluginRegistry registry = PluginRegistry.getInstance();
        
        // Register a simple plugin using a lambda
        registry.register("reverse text", (Object... args) -> {
            if (args.length == 0) return "";
            String input = args[0].toString();
            return new StringBuilder(input).reverse().toString();
        });
        
        // Math operation plugin
        registry.register("calculate average", (Object... args) -> {
            if (args.length == 0) return 0;
            double sum = 0;
            for (Object arg : args) {
                sum += Double.parseDouble(arg.toString());
            }
            return sum / args.length;
        });
    }
}
```

### 2. Using Method References

You can also use method references:

```java
import talk.plugins.PluginRegistry;

public class MathUtils {
    public static double average(Object... args) {
        if (args.length == 0) return 0;
        double sum = 0;
        for (Object arg : args) {
            sum += Double.parseDouble(arg.toString());
        }
        return sum / args.length;
    }
    
    public static void registerPlugins() {
        PluginRegistry registry = PluginRegistry.getInstance();
        registry.register("calculate average", MathUtils::average);
    }
}
```

### 3. Using Java Classes with Static Methods

Create a class with static methods for your plugins:

```java
package com.example.plugins;

public class StringUtils {
    /**
     * Counts occurrences of a substring in a string
     * @param args First arg is the string, second arg is the substring to count
     * @return Number of occurrences
     */
    public static int countOccurrences(Object... args) {
        if (args.length < 2) {
            throw new IllegalArgumentException("Need string and substring to count");
        }
        
        String str = args[0].toString();
        String subStr = args[1].toString();
        
        // Special case for empty substring
        if (subStr.isEmpty()) {
            return str.length() + 1;
        }
        
        int count = 0;
        int lastIndex = 0;
        
        while (lastIndex != -1) {
            lastIndex = str.indexOf(subStr, lastIndex);
            if (lastIndex != -1) {
                count++;
                lastIndex += subStr.length();
            }
        }
        
        return count;
    }
    
    /**
     * Truncates a string with ellipsis if longer than maxLength
     * @param args First arg is string, second arg is maximum length
     * @return Truncated string with ellipsis if needed
     */
    public static String truncate(Object... args) {
        if (args.length < 2) {
            throw new IllegalArgumentException("Need string and max length");
        }
        
        String str = args[0].toString();
        int maxLength = Integer.parseInt(args[1].toString());
        
        if (str.length() <= maxLength) {
            return str;
        }
        
        return str.substring(0, maxLength - 3) + "...";
    }
}
```

### 4. Using Instance Methods (Stateful Plugins)

For plugins that need to maintain state:

```java
package com.example.plugins;

import java.util.HashMap;
import java.util.Map;

public class CachePlugin {
    private final Map<String, Object> cache = new HashMap<>();
    
    /**
     * Store a value in the cache
     * @param args First arg is key, second is value
     * @return The stored value
     */
    public Object store(Object... args) {
        if (args.length < 2) {
            throw new IllegalArgumentException("Need key and value");
        }
        
        String key = args[0].toString();
        Object value = args[1];
        
        cache.put(key, value);
        return value;
    }
    
    /**
     * Retrieve a value from the cache
     * @param args Key to look up
     * @return The stored value or null if not found
     */
    public Object retrieve(Object... args) {
        if (args.length == 0) {
            throw new IllegalArgumentException("Need key to retrieve");
        }
        
        String key = args[0].toString();
        return cache.get(key);
    }
    
    /**
     * Clear the cache
     * @param args Not used
     * @return Number of entries cleared
     */
    public int clear(Object... args) {
        int size = cache.size();
        cache.clear();
        return size;
    }
}
```

## Registering Your Plugins

### Option 1: Programmatic Registration

To register your plugins programmatically:

```java
import talk.plugins.PluginRegistry;

public class MyPluginRegistration {
    public static void registerAll() {
        PluginRegistry registry = PluginRegistry.getInstance();
        
        // Register static methods
        registry.register("count occurrences", StringUtils::countOccurrences);
        registry.register("truncate text", StringUtils::truncate);
        
        // Register instance methods (stateful)
        CachePlugin cachePlugin = new CachePlugin();
        registry.register("store in cache", cachePlugin::store);
        registry.register("get from cache", cachePlugin::retrieve);
        registry.register("clear cache", cachePlugin::clear);
    }
}
```

Call this from your application startup code:

```java
// In your main app initialization
MyPluginRegistration.registerAll();
```

### Option 2: Using Configuration Files

Create a properties file at `plugins/talk-plugins.properties`:

```properties
# String utility plugins
plugin.count occurrences = com.example.plugins.StringUtils#countOccurrences
plugin.truncate text = com.example.plugins.StringUtils#truncate

# Cache plugins
plugin.store in cache = com.example.plugins.CachePlugin#store
plugin.get from cache = com.example.plugins.CachePlugin#retrieve
plugin.clear cache = com.example.plugins.CachePlugin#clear
```

The Talk programming language will automatically load this file when it starts.

## Using Your Plugins in Talk Scripts

Once registered, you can use your plugins in Talk scripts:

```
# Using string utility plugins
variable text equal "hello hello world"
use plugin count occurrences with text and "hello" into count
write "Found " and count and " occurrences of 'hello'" in console

variable long_text equal "This is a very long text that should be truncated"
use plugin truncate text with long_text and 20 into short_text
write "Truncated: " and short_text in console

# Using cache plugins
variable key equal "user_id"
variable value equal 12345
use plugin store in cache with key and value into result
write "Stored in cache: " and result in console

use plugin get from cache with key into retrieved
write "Retrieved from cache: " and retrieved in console

use plugin clear cache into cleared_count
write "Cleared " and cleared_count and " items from cache" in console
```

## Plugin Design Best Practices

1. **Handle Type Conversions**: The plugin system handles basic type conversions, but your methods should be prepared to handle various input types.

2. **Validate Arguments**: Always check that you received the expected number and types of arguments.

3. **Return Meaningful Results**: Make sure your plugins return values that can be used in Talk scripts.

4. **Use Descriptive Aliases**: Choose natural language aliases that clearly describe what your plugin does.

5. **Document Your Plugins**: Provide clear documentation on how to use your plugins, what arguments they expect, and what they return.

6. **Error Handling**: Throw clear, descriptive exceptions when something goes wrong.

## Debugging Plugins

To debug plugins:

1. Add logging to your plugin methods
2. Run Talk with the `--debug` flag
3. Check the log output for plugin execution details

## Example Plugin Project Structure

```
my-talk-plugins/
├── build.gradle
├── src/
│   └── main/
│       ├── java/
│       │   └── com/
│       │       └── example/
│       │           └── plugins/
│       │               ├── StringUtils.java
│       │               ├── MathUtils.java
│       │               ├── CachePlugin.java
│       │               └── MyPluginRegistration.java
│       └── resources/
│           └── plugins/
│               └── talk-plugins.properties
└── README.md
```

## Packaging and Distribution

To distribute your plugins:

1. Package your code as a JAR file
2. Document the plugin aliases and functionality
3. Instruct users to either:
   - Add the JAR to their classpath
   - Add entries to their `talk-plugins.properties` file
   - Or use programmatic registration in their own code
