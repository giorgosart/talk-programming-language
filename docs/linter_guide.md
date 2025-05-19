# Talk Language Linter

## Overview

The Talk Language Linter is a static code analysis tool that examines Talk scripts without executing them. It identifies potential errors and bad coding practices, providing feedback to help developers write more reliable and maintainable code.

## Features

The linter checks for several categories of issues:

1. **Syntax Validation**
   - Unknown instructions
   - Missing required keywords (e.g., 'then' after 'if')
   - Invalid tokens and expressions

2. **Structure Validation**
   - Improper nesting of code blocks
   - Unclosed code blocks

3. **Semantic Analysis**
   - Usage of reserved words as variable names
   - Unused variables
   - Invalid expressions (arithmetic and logical)

## Using the Linter

### Command Line

Use the `lint` command with the Talk CLI:

```bash
talk lint path/to/script.talk
```

This command will analyze the script and report any issues found, with line numbers and descriptions.

### Exit Codes

- `0`: Linting completed without errors (warnings may be present)
- `1`: Linting found at least one error

## Linting Rules

### UnknownInstructionRule

Identifies commands that are not part of the Talk language vocabulary.

```
# Will trigger the rule
unknown_command "hello"
```

### MissingThenRule

Ensures that all `if` statements include the required `then` keyword.

```
# Will trigger the rule
if x is equal to 5
    write "Missing 'then'"
    
# Correct usage
if x is equal to 5 then
    write "Correct"
```

### ImproperNestingRule

Checks for correct indentation after block-starting commands.

```
# Will trigger the rule
if x is equal to 5 then
write "This should be indented"

# Correct usage
if x is equal to 5 then
    write "Properly indented"
```

### UnclosedBlockRule

Identifies code blocks that are opened but not properly closed with dedentation.

```
# Will trigger the rule
if x is greater than 0 then
    write "This block is not closed"
    if y is equal to 10 then
        write "Nested block"
# Missing dedentation for both blocks

# Correct usage
if x is greater than 0 then
    write "Properly closed block"
    if y is equal to 10 then
        write "Nested block"
    # Proper dedentation
# Proper dedentation
```

### InvalidTokenRule

Identifies invalid tokens, such as improperly formatted variable names or literals.

```
# Will trigger the rule
set 123invalid to "Cannot start with a number"

# Correct usage
set valid_name to "Valid variable name"
```

### ReservedWordRule

Prevents the use of language keywords as variable names.

```
# Will trigger the rule
set if to 42

# Correct usage
set condition to 42
```

### UnusedVariableRule

Identifies variables that are declared but never used.

```
# Will trigger the rule
variable unused equals "Never used"

# Correct usage
variable used equals "Used below"
write used
```

### InvalidExpressionRule

Checks for malformed arithmetic or logical expressions.

```
# Will trigger the rule
set result to 5 plus  # Missing right operand

# Correct usage
set result to 5 plus 10
```

## Integration

The linter is fully integrated with the Talk CLI and can be run as a standalone tool or as part of the build/validation process.
