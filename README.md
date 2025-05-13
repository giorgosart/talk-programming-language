# Conversational Programming Language (`.talk`)

A simple, English-like scripting language for automating tasks and learning programming basics. Powered by Java.

---

## 🚀 Quick Start

1. **Build the JAR:**

   ```sh
   ./gradlew shadowJar
   ```

2. **Run a script:**

   ```sh
   java -jar app/build/libs/talk.jar run path/to/script.talk
   ```

---

## 📖 Language Reference

### 1. Variables

- Declare: `variable x`
- Declare & set: `variable x equal 10`
- Assign: `set x to 42`

### 2. Expressions

- Use numbers, variables, and arithmetic: `set y to x + 5`
- Comparisons: `if x is greater than 10 then ...`

### 3. Control Flow

- If/else:

  ```talk
  if x is greater than 10 then
    # instructions
  otherwise
    # fallback
  ```

### 4. File Operations

- Create file: `create file as "output.txt"`
- Write to file: `write "Hello!" in output.txt`

### 5. User Input

- Ask: `ask "What is your name?" and store in name`
- With validation: `ask "Age?" and store in age must be a number`

### 6. Error Handling

- Attempt/fallback:

  ```talk
  attempt
    # risky instructions
  if that fails
    # fallback instructions
  ```

### 7. Comments

- Any line starting with `#` is ignored.

### 8. Logical Operators & Complex Conditions

- Use `AND`, `OR`, `NOT` (case-insensitive) to combine conditions in `if` statements.
- Parentheses for grouping are supported.

#### Examples

```talk
variable x equal 1
variable y equal 0
variable z equal 1

if x is equal to 1 AND y is equal to 1 then
    write "both true" in logic.txt
otherwise
    write "at least one is false" in logic.txt

if x is equal to 1 OR y is equal to 1 then
    write "at least one is true" in logic.txt
otherwise
    write "both false" in logic.txt

if NOT y is equal to 1 then
    write "y is not 1" in logic.txt

if x is equal to 1 AND (y is equal to 1 OR z is equal to 1) then
    write "nested logic true" in logic.txt
otherwise
    write "nested logic false" in logic.txt
```

### 9. Functions

- Define a function:

  ```talk
  define <function_name>
      # instructions (indented)
  ```

- Call a function:

  ```talk
  call <function_name>
  ```

- Functions must be defined before they are called.
- Function names must be unique (no duplicates).
- Parameters are not supported in this version.

#### Example

```talk
variable x equal 0

define set42
    set x to 42

define printHello
    write "Hello from function!" in func.txt

call set42
call printHello

define outer
    call printHello
    set x to 99
call outer
```

#### Common Mistakes

- **Calling before definition:**

  ```talk
  call notDefined  # Error: function not defined
  ```

- **Duplicate function names:**

  ```talk
  define foo
      write "hi" in out.txt
  define foo  # Error: function 'foo' already defined
      write "bye" in out.txt
  ```

- **Incorrect indentation:**

  ```talk
  define myFunc
  write "bad indent" in out.txt  # Error: expected indented block
  ```

### 10. Lists

Lists allow you to store and work with multiple values in a single variable. You can declare lists, access items by index, loop over them, and check for membership.

- **Declare a list:**

  ```talk
  variable fruits equals apple, banana and cherry
  variable colors equals "red apple", "green apple", orange
  ```
  - Use commas and/or `and` to separate items.
  - Items with spaces must be quoted.

- **Access an item by index:**

  ```talk
  set x to item 2 in fruits  # x = "banana"
  ```
  - Indexing is 1-based: `item 1` is the first item.

- **Loop over a list:**

  ```talk
  repeat for each fruit in fruits
      write position and fruit in "fruits.txt"
  ```
  - The loop variable (here, `fruit`) is set to each item in turn.
  - The special variable `position` is available inside the loop (starts at 1, read-only).

- **Check if a list includes a value:**

  ```talk
  if fruits includes apple then
      write "Found apple!" in "result.txt"
  otherwise
      write "No apple found." in "result.txt"
  ```

#### Examples

```talk
variable items equals 10, 20, 30
set first to item 1 in items
set last to item 3 in items

repeat for each number in items
    write position and number in "numbers.txt"

if items includes 20 then
    write "20 is present" in "numbers.txt"
```

---

## 📝 Examples

### Hello World

```talk
write "Hello from .talk!" in hello.txt
```

### Variables & Condition

```talk
variable x equal 5
if x is greater than 3 then
  write "x is big" in result.txt
otherwise
  write "x is small" in result.txt
```

### File Operations

```talk
create file as "data.txt"
write "42" in data.txt
```

### Input & Fallback

```talk
ask "Enter a number:" and store in num must be a number retry if invalid
attempt
  write num in output.txt
if that fails
  write "Invalid input!" in output.txt
```

### Functions

```talk
define greet
    write "Hello!" in greet.txt
call greet
```

---

## ⚠️ Error Handling

- Errors show a friendly message, line number, and a hint.
- Example:

  ```
  [Error]: Syntax error at line 2: Expected 'in'
  Hint: Script failed to run. See above for details.
  ```

---

## 📚 More

- See `docs/specs.md` for full language specs.
- See `app/hello.talk` and other `.talk` files for examples.
