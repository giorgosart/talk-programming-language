# Plugin System for Talk Programming Language

The Talk programming language includes a plugin system that allows extending its functionality by registering Java classes or methods that can be invoked from Talk scripts using plain-English aliases.

## Overview

The plugin system allows users to:

1. Call Java methods and classes from within Talk scripts
2. Use natural language aliases to make the API more conversational
3. Add new functionality without changing the Talk interpreter
4. Load plugins from configuration files

## Using Plugins in Talk Scripts

Plugins are invoked using the following syntax patterns:

```
use plugin <plugin-alias> into <variable>
use plugin <plugin-alias> with <arg1> and <arg2> ... into <variable>
use plugin <plugin-alias> with <arg1>
```

### Examples:

```
# Generate a UUID and store it in the 'id' variable
use plugin generate uuid into id

# Get system information 
use plugin system info into sys_info
write sys_info in console

# Generate a random number between 1 and 100
use plugin random number with 1 and 100 into num
```

## Built-in Plugins

The Talk programming language comes with several built-in plugins:

| Plugin Alias | Description | Arguments | Example |
|-------------|-------------|-----------|---------|
| `generate uuid` | Generates a UUID | None | `use plugin generate uuid into id` |
| `system property` | Gets a Java system property | Property name | `use plugin system property with "user.name" into username` |
| `get environment variable` | Gets an environment variable | Variable name | `use plugin get environment variable with "PATH" into path` |
| `system info` | Gets system info (OS, Java version) | None | `use plugin system info into info` |
| `current timestamp` | Gets current timestamp in ms | None | `use plugin current timestamp into now` |
| `format timestamp` | Formats a timestamp | Timestamp, format (optional) | `use plugin format timestamp with now into time` |
| `random number` | Generates a random number | Max or min/max | `use plugin random number with 10 into num` |
| `to uppercase` | Converts string to uppercase | String | `use plugin to uppercase with text into upper` |
| `to lowercase` | Converts string to lowercase | String | `use plugin to lowercase with text into lower` |

## Creating Custom Plugins

### Option 1: Java Class with Static Methods

Create a Java class with static methods:

```java
package myapp.plugins;

public class MyPlugins {
    public static String reverse(Object... args) {
        if (args.length == 0) return "";
        String input = args[0].toString();
        return new StringBuilder(input).reverse().toString();
    }
}
```

### Option 2: Java Class with Instance Methods

Create a class with instance methods:

```java
package myapp.plugins;

public class DatabasePlugin {
    private final Connection conn;
    
    public DatabasePlugin() {
        // Initialize connection
        this.conn = DriverManager.getConnection("jdbc:mysql://localhost/mydb");
    }
    
    public ResultSet query(Object... args) {
        String sql = args[0].toString();
        return conn.prepareStatement(sql).executeQuery();
    }
}
```

### Registering Plugins in Code

You can register plugins programmatically:

```java
PluginRegistry registry = PluginRegistry.getInstance();
registry.register("reverse text", MyPlugins::reverse);
registry.register("query database", new DatabasePlugin()::query);
```

### Registering Plugins via Configuration

Create a properties file (e.g., `talk-plugins.properties`):

```properties
plugin.reverse text = myapp.plugins.MyPlugins#reverse
plugin.query database = myapp.plugins.DatabasePlugin#query
```

Load the configuration:

```
java -jar talk.jar run script.talk --plugin-config=/path/to/talk-plugins.properties
```

Or place it in the default location (`plugins/talk-plugins.properties`).

## Type Conversion

The plugin system handles type conversion automatically when passing arguments between Talk scripts and Java methods:

- Numbers in Talk are converted to appropriate Java number types
- Strings in Talk are passed as Java Strings
- Lists in Talk are converted to Java Lists
- Boolean values are converted between Talk and Java

## Error Handling

Errors in plugin execution are caught and reported with line numbers in the Talk script:

```
Error executing plugin 'query database': SQLException: Table 'users' doesn't exist (line 5)
```

## Advanced Features

- Plugin discovery via classpath scanning (future enhancement)
- Hot reloading of plugins (future enhancement)
- Plugin dependencies and versioning (future enhancement)
