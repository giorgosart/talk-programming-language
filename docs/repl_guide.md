# Talk Programming Language REPL

The Talk Programming Language includes an interactive REPL (Read-Eval-Print Loop) 
that allows you to experiment with the language by executing one instruction at a time.

## Features

- Execute Talk instructions and expressions interactively
- Maintain variables and context between commands
- Command history for reviewing past entries
- Helpful error messages with context
- Built-in help system with examples
- Support for direct expression evaluation
- Arrow key navigation through command history (Enhanced REPL)
- Tab completion for keywords and variables (Enhanced REPL)
- Persistent history across sessions (Enhanced REPL)

## Using the REPL

There are two versions of the REPL available:

### Standard REPL

To start the standard REPL, run:

```bash
talk repl
```

### Enhanced REPL

To use the enhanced version with arrow key navigation and tab completion, run:

```bash
./enhanced_repl.sh
```

This will start the interactive session where you can enter Talk commands.

### Available Commands

The REPL includes special built-in commands:

- `exit`, `quit`: Exit the REPL session
- `help`: Show help information and examples
- `history`: Display command history
- `clear`: Clear command history

### Executing Instructions

You can enter any valid Talk instruction and it will be executed immediately:

```
> variable name = "John"
> write "Hello, " + name
"Hello, John"
```

### Evaluating Expressions

You can also enter expressions directly to evaluate them without an instruction keyword:

```
> 10 + 20
30
> "Hello" + " world"
"Hello world"
```

### Working with Variables

Variables persist between commands in a session:

```
> variable counter = 0
> set counter = counter + 1
> counter
1
> set counter = counter + 1
> counter
2
```

### Control Structures

You can use any Talk control structures:

```
> if 10 > 5 then write "Ten is greater than five"
"Ten is greater than five"
```

### Error Handling

The REPL provides helpful error messages with contextual tips:

```
> write undefinedVariable
Error: Variable 'undefinedVariable' not found
Value error. Check that your variables are defined and have the correct type.
Tip: You can define a variable with 'variable name = value' before using it.
```

## Examples

Here are some examples of what you can do in the REPL:

### Basic Arithmetic

```
> 5 + 10
15
> 20 - 8
12
> 4 * 7
28
> 100 / 4
25
```

### Working with Strings

```
> "Hello" + " World"
"Hello World"
> uppercase of "hello"
"HELLO"
> lowercase of "HELLO"
"hello"
> length of "hello world"
11
```

### Variables and Lists

```
> variable colors = ["red", "green", "blue"]
> colors[1]
"green"
> variable person = {"name": "Alice", "age": 30}
> person["name"]
"Alice"
```

### Control Flow

```
> variable x = 10
> if x > 5 then write "x is greater than 5" else write "x is not greater than 5"
"x is greater than 5"
> repeat 3 times with i write "Iteration " + i
"Iteration 0"
"Iteration 1"
"Iteration 2"
```

## Additional Resources

For more detailed information:

- See [repl_examples.md](repl_examples.md) for a comprehensive list of examples
- See [repl_developer_guide.md](repl_developer_guide.md) for implementation details

## Future Enhancements

- Better handling of multiline input
- Saving and loading REPL sessions
- Support for debugging features
- Integration with IDE plugins

The following enhancements have been implemented in the Enhanced REPL version:
- ✓ Tab completion for commands and variables
- ✓ Enhanced history navigation using arrow keys
- ✓ Persistent history between sessions
