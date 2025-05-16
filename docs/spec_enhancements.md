### 18. Language Enhancements and Architectural Features

This document outlines planned enhancements to the `.talk` language covering expressions, scoping, testing, and future ecosystem expansion.

---

#### 1. Comparison and Boolean Logic

| Type       | Description  | Syntax Example |
|------------|------------------------------------|----------------|
| Comparison | Compare numbers, strings, dates    | `x is equal to y`, `x is greater than 5` |
| Boolean    | Logical operations                 | `if a is greater than 0 and b is smaller than 10 then` |
| Negation   | Invert a condition                 | `if not x is equal to y` |

---

#### 2. Scoping and Variable Shadowing

- Variables declared in blocks (`if`, `repeat`, `function`) are scoped to that block
- Outer scope variables cannot be overwritten by default inside inner scopes
- Loop variables and function parameters use isolated context
- Function calls push a new scoped execution context

---

#### 3. Native Testing Support

- Native `.talk` test runner format
- Simple test syntax:
```plaintext
expect result of 5 plus 3 to be 8
expect result of greet with "George" to contain "Hello"
```
- Output should include test counts and pass/fail summary

---

#### 4. Bonus Enhancements

| Feature        | Description                                     | Example |
|----------------|-------------------------------------------------|---------|
| `import`       | Include other `.talk` files                     | `import "lib.talk"` |
| Plugins        | Register Java functions as callable utilities   | `call java.util.UUID.randomUUID as uuid` |
| Linter         | Basic validation for unknown instructions, missing `then`, etc. | — |
| Interactive REPL | Try `.talk` line-by-line                      | `talk > write "Hello"` |