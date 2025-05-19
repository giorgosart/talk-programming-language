# Talk Language REPL - Example Guide

This guide provides examples for using the Talk Programming Language REPL (Read-Eval-Print Loop), which allows you to interactively experiment with the language one command at a time.

## Getting Started

Start the REPL by running:

```
talk repl
```

You'll see a welcome message and a prompt (`>`) where you can start typing commands.

## Basic Examples

### Simple Expressions

Try evaluating some expressions:

```
> 2 + 3
5
> 10 - 4
6
> 5 * 3
15
> 20 / 4
5
```

### String Operations

Work with strings:

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

### Variables

Create and manipulate variables:

```
> variable name = "Alice"
> write "Hello, " + name
"Hello, Alice"
> set name = "Bob"
> write "Hello, " + name
"Hello, Bob"
```

### Mathematical Operations

Try more advanced math:

```
> 5 * (3 + 2)
25
> 10 % 3
1
> 2 ^ 3
8
```

## Using Lists

Create and manipulate lists:

```
> variable fruits = ["apple", "banana", "orange"]
> fruits[1]
"banana"
> length of fruits
3
> fruits + ["grape"]
["apple", "banana", "orange", "grape"]
```

## Control Structures

### If Statements

```
> variable age = 25
> if age > 18 then write "Adult" else write "Minor"
"Adult"
```

### Repeat Loops

```
> variable sum = 0
> repeat 5 times with i set sum = sum + i
> sum
10
```

## Function Definition and Calling

Define and use functions:

```
> define greet with name as write "Hello, " + name
> greet with "Jane"
"Hello, Jane"
```

## Working with Files

Read and write files:

```
> write "Hello, file world!" to "hello.txt"
> read file "hello.txt"
"Hello, file world!"
> append "More content" to "hello.txt"
> read file "hello.txt"
"Hello, file world!More content"
```

## Error Handling

See how errors are reported:

```
> x + 5
Error: Variable 'x' not found
Value error. Check that your variables are defined and have the correct type.
```

Try catching errors:

```
> attempt read file "nonexistent.txt" catch with error write "File not found: " + error
"File not found: Error reading file: nonexistent.txt (No such file or directory)"
```

## Complex Examples

### Working with Maps

```
> variable person = {"name": "Alice", "age": 30, "city": "New York"}
> person["name"]
"Alice"
> set person["age"] = 31
> person["age"]
31
```

### Nested Control Structures

```
> variable x = 1
> if x > 0 then
>   if x < 10 then
>     write "x is between 0 and 10"
>   else
>     write "x is greater than or equal to 10"
>   end
> else
>   write "x is less than or equal to 0"
> end
"x is between 0 and 10"
```

### Date Utilities

```
> current date
"2025-05-19"
> format date "2025-05-19" as "dd/MM/yyyy"
"19/05/2025"
```

## REPL Special Commands

The REPL includes several special commands:

```
> help
...displays help information...

> history
...shows command history...

> clear
History cleared.

> exit
Goodbye!
```

## Tips & Tricks

1. Use the up arrow key to recall previous commands (coming soon)
2. Type `help` to see available commands and examples
3. Use expressions directly without `write` to see their values
4. The REPL maintains context, so variables defined in one command are available in subsequent commands
5. Press Ctrl+C to force exit the REPL if needed

Enjoy exploring the Talk Programming Language through the interactive REPL!
