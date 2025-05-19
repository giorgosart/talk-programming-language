# Import System in Talk Programming Language

## Overview

The import system in Talk allows you to organize your code across multiple files, promoting modularity and code reuse. This document explains how to use the import functionality to include code from external files into your Talk scripts.

## Basic Usage

To import code from another file, use the `import` statement followed by the path to the Talk script:

```talk
import "path/to/file.talk"
```

## How Imports Work

When you use an import statement:

1. The specified file is located using the given path (relative to the current working directory)
2. The file's content is read and parsed as Talk code
3. All instructions in the imported file are executed in the current context
4. Any functions, variables, or other definitions from the imported file become available in the importing file

## Examples

### Simple Import

```talk
# main.talk
import "utilities.talk"

# Use a function from utilities.talk
call add with 5 and 10 into result
write "Result: " + result to console
```

```talk
# utilities.talk
function add with a and b
    return a + b
```

### Nested Imports

Files can import other files, creating a chain of imports:

```talk
# main.talk
import "module1.talk"

call greet with "World"
```

```talk
# module1.talk
import "module2.talk"

function greet with name
    call format with "Hello, " and name into message
    write message to console
```

```talk
# module2.talk
function format with greeting and name
    return greeting + name + "!"
```

## Best Practices

1. **Avoid Circular Imports**: Don't create circular dependencies where file A imports file B and file B imports file A.

2. **Use Descriptive Module Names**: Choose file names that clearly indicate the purpose of the module.

3. **Keep Modules Focused**: Each module should have a single responsibility or group related functionality.

4. **Document Your Modules**: Add comments at the top of each file to describe its purpose and the functionality it provides.

5. **Relative Paths**: Consider your project structure when using relative paths in imports.

## Limitations

- The import system does not have namespaces, so all imported definitions are in the same scope.
- There's no way to selectively import specific functions or variables.
- Import paths are relative to the current working directory, not the location of the importing file.

## Example Project Structure

A well-organized Talk project using imports might look like this:

```
my_project/
├── main.talk
├── modules/
│   ├── math_utils.talk
│   ├── string_utils.talk
│   └── file_utils.talk
├── config/
│   └── settings.talk
└── data/
    └── constants.talk
```

Where `main.talk` might contain:

```talk
import "modules/math_utils.talk"
import "modules/string_utils.talk"
import "config/settings.talk"

# Application logic using imported modules
```
