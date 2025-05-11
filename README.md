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
  ```
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
  ```
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

---

## 📝 Examples

### Hello World
```
write "Hello from .talk!" in hello.txt
```

### Variables & Condition
```
variable x equal 5
if x is greater than 3 then
  write "x is big" in result.txt
otherwise
  write "x is small" in result.txt
```

### File Operations
```
create file as "data.txt"
write "42" in data.txt
```

### Input & Fallback
```
ask "Enter a number:" and store in num must be a number retry if invalid
attempt
  write num in output.txt
if that fails
  write "Invalid input!" in output.txt
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
