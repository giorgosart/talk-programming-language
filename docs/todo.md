# ✅ Conversational Programming Language `.talk` — Step-by-Step Implementation Plan

---

## 🧱 Phase 1: Project Setup and Foundation

### **1.0 Project Bootstrap**
> Set up the base project and prepare development environment.

- [x] **1.1** Create a new Java project using Gradle (Dep: None)  
  - [x] Set Java version to 17+  
  - [x] Create basic directory structure (`src/main/java`, `src/test/java`)  
  - [x] Add `Application.java` as entry point  
  - [x] Add `TalkRunner.java` class for CLI

- [x] **1.2** Configure Gradle build system (Dep: 1.1)  
  - [x] Add JUnit 5 for testing  
  - [x] Add command-line argument parser (e.g. `picocli` or simple args parser)

- [x] **1.3** Implement CLI wrapper to run `.talk` files (Dep: 1.2)  
  - [x] `talk run <script.talk>` should read the file  
  - [x] Validate `.talk` extension  
  - [x] Print basic output: "Running script..."

---

## 🧠 Phase 2: Parsing and Tokenizing

### **2.0 Tokenizer**
> Convert script lines into structured tokens.

- [x] **2.1** Create `Tokenizer` class (Dep: 1.3)  
  - [x] Tokenize by keywords, strings, variables  
  - [x] Preserve line numbers for error tracking

- [x] **2.2** Add support for comment skipping (lines starting with `#`) (Dep: 2.1)

- [x] **2.3** Write tokenizer tests for common cases (Dep: 2.1, 2.2)  
  - [x] Tokenize simple assignment  
  - [x] Tokenize `if`, `otherwise`  
  - [x] Tokenize `ask`, `attempt`

---

### **3.0 Parser**
> Build syntax tree or instruction list from tokens.

- [x] **3.1** Define `Instruction` interface and subclasses (Dep: 2.3)  
  - [x] Create `VariableInstruction`, `AssignmentInstruction`, `IfInstruction`, `AskInstruction`, etc.

- [x] **3.2** Build `Parser` class to convert tokens into instructions (Dep: 3.1)  
  - [x] Support one instruction per line (MVP)  
  - [x] Add error reporting with line number on syntax issues

- [x] **3.3** Write parser tests for all instruction types (Dep: 3.2)

---

## 🏃‍♂️ Phase 3: Runtime and Execution Engine

### **4.0 Runtime Context**
> Manage variable state and script execution context.

- [x] **4.1** Create `RuntimeContext` class (Dep: 3.3)  
  - [x] Map for variable values  
  - [x] Track execution state  
  - [x] Support current instruction line pointer

- [x] **4.2** Add variable declaration + assignment execution (Dep: 4.1)  
  - [x] `variable x`  
  - [x] `set x to 10`  
  - [x] `variable x equal 10`  
  - [x] Validate existence when assigning

- [x] **4.3** Add expression resolver (Dep: 4.2)  
  - [x] Support literals, variables, and simple arithmetic  
  - [x] Support `+`, `-`, `*`, `/`  
  - [x] Evaluate comparisons

---

### **5.0 Instruction Execution**
> Implement actual behavior of each instruction.

- [x] **5.1** Implement `IfInstruction` (Dep: 4.3)  
  - [x] Execute conditional branch  
  - [x] Support `otherwise if` and `otherwise`

- [x] **5.2** Implement `WriteInstruction` (Dep: 4.3)  
  - [x] `write "Hello" in file.txt`  
  - [x] Support writing variables to file  
  - [x] Create file if it doesn’t exist

- [x] **5.3** Implement `CreateFileInstruction` (Dep: 5.2)  
  - [x] `create file as "filename.txt"`  
  - [x] Validate paths and permissions

- [x] **5.4** Implement `AskInstruction` (Dep: 4.3)  
  - [x] Prompt user for input  
  - [x] Store in variable  
  - [x] Support `must be a number`  
  - [x] Support `must be one of`  
  - [x] Support `retry if invalid`

- [x] **5.5** Implement `AttemptInstruction` (Dep: 5.1, 5.2, 5.4)  
  - [x] Execute block  
  - [x] Catch any error  
  - [x] Jump to fallback block on failure

---

## 🧪 Phase 4: Testing and Error Handling

### **6.0 Testing Coverage**
> Ensure all core features are covered.

- [x] **6.1** Unit test all instruction types (Dep: 5.5)  
  - [x] `VariableInstruction`  
  - [x] `IfInstruction`  
  - [x] `WriteInstruction`, etc.

- [x] **6.2** Add integration tests for script execution (Dep: 6.1)  
  - [x] Full `.talk` script tests  
  - [x] File creation  
  - [x] Input validation  
  - [x] Error fallback

- [x] **6.3** Implement error formatter (Dep: 6.1)  
  - [x] Show friendly error message  
  - [x] Include line number and hint

---

## 📄 Phase 5: CLI & Packaging

### **7.0 CLI Enhancements**
> Make the `.talk` runner script-friendly and user-facing.

- [x] **7.1** Enhance CLI `talk run <file>` (Dep: 5.5)  
  - [x] Add help messages  
  - [x] Handle file not found  
  - [x] Output success or error summary

- [x] **7.2** Package CLI as runnable JAR (Dep: 7.1)  
  - [x] Create shadow JAR  
  - [x] `java -jar talk.jar script.talk`

---

## 📘 Phase 6: Documentation and Samples

### **8.0 Docs and Examples**
> Educate users and prepare for demo.

- [x] **8.1** Create README with language reference (Dep: 7.2)  
  - [x] Syntax rules  
  - [x] Examples  
  - [x] Error handling

- [x] **8.2** Add sample `.talk` scripts (Dep: 8.1)  
  - [x] Hello world  
  - [x] Variable + condition  
  - [x] File operations  
  - [x] Input and fallback

- [x] **8.3** Prepare quickstart guide for new users (Dep: 8.2)

## 🧱 Phase 7: Indentation-Based Block Scoping

### **9.0 Indentation Support**
> Allow nesting of control flow using consistent indentation.

- [x] **9.1** Update Tokenizer to support indentation tracking (Dep: 2.1)  
  - [x] Count leading spaces on each line  
  - [x] Generate `INDENT` and `DEDENT` tokens  
  - [x] Track line indentation stack  
  - [x] Throw syntax error on inconsistent indentation

- [x] **9.2** Update Parser to group blocks (Dep: 9.1)  
  - [x] Modify `IfInstruction`, `AttemptInstruction` to accept block children  
  - [x] Handle `INDENT` by entering recursive parse mode  
  - [x] Handle `DEDENT` by ending current block scope  
  - [x] Ensure `otherwise` matches the indentation of its parent `if`

- [x] **9.3** Update Execution Engine (Dep: 9.2)  
  - [x] Execute instruction blocks recursively  
  - [x] Attach child blocks to parent instruction objects  
  - [x] Preserve runtime context and variable scope across nested blocks

- [x] **9.4** Add Unit Tests for nested logic (Dep: 9.3)  
  - [x] Nested `if` inside `if`  
  - [x] `attempt` block with nested condition  
  - [x] Incorrect indentation edge cases

- [x] **9.5** Add Integration Tests for `.talk` scripts with indentation (Dep: 9.4)  
  - [x] Deep nesting (3+ levels)  
  - [x] Mixed blocks: `if` + `attempt`  
  - [x] Misaligned `otherwise` → error

- [x] **9.6** Update README and Examples (Dep: 9.5)  
  - [x] Add example of nested `if`  
  - [x] Document indentation rules  
  - [x] Warn against mixing tabs and spaces

## 🔁 Phase 8: Looping Constructs

### **10.0 Add Support for `repeat` Loop**

- [x] **10.1** Update Tokenizer to recognize `repeat` and `times` keywords (Dep: 2.1)  
  - [x] Recognize `repeat <number> times` as a loop declaration  
  - [x] Track indentation tokens inside loop blocks  
  - [x] Store numeric literal for loop count

- [x] **10.2** Define `RepeatInstruction` class (Dep: 3.1)  
  - [x] Hold `times` integer  
  - [x] Contain a list of instructions as the loop body  
  - [x] Expose implicit `_index` variable in each iteration

- [x] **10.3** Update Parser to support `repeat ... times` blocks (Dep: 10.1, 10.2)  
  - [x] Parse line into a `RepeatInstruction`  
  - [x] Capture nested block instructions using indentation  
  - [x] Raise syntax errors for malformed loops (e.g., missing number, no block)

- [x] **10.4** Update Runtime to execute `RepeatInstruction` (Dep: 10.3)  
  - [x] Evaluate loop count expression  
  - [x] Execute loop body `n` times  
  - [x] Inject `_index` as a local variable  
  - [x] Reset `_index` after loop

- [x] **10.5** Unit Tests (Dep: 10.4)  
  - [x] Loop runs exact number of times  
  - [x] `_index` is correct for each iteration  
  - [x] Nested loops work properly  
  - [x] Syntax errors: missing `times`, negative numbers, invalid block

- [x] **10.6** Integration Tests (Dep: 10.5)  
  - [x] `repeat 3 times` with simple write  
  - [x] Loop inside `if`, and vice versa  
  - [x] Loop with `attempt` block inside  
  - [x] `write _index` in output

- [x] **10.7** Update README and Examples (Dep: 10.6)  
  - [x] Document loop syntax and `_index`  
  - [x] Add sample `.talk` script with loop  
  - [x] Mention future support for `while` and `for each`

## 🧠 Phase 9: Logical Operators

### **11.0 Add Logical Operators to Conditions**

- [x] **11.1** Update Tokenizer to recognize `and`, `or`, `not` (Dep: 2.1)  
  - [x] Ensure these tokens are treated as part of expressions  

- [x] **11.2** Update Expression Resolver to support logic trees (Dep: 4.3, 11.1)  
  - [x] Build logical expression nodes (`AndNode`, `OrNode`, `NotNode`)  
  - [x] Evaluate short-circuiting for `and`/`or`

- [x] **11.3** Update Parser to handle chained conditions (Dep: 11.2)  
  - [x] Nest conditions under `IfInstruction`  
  - [x] Add support for `not` prefix handling

- [x] **11.4** Unit Tests (Dep: 11.3)  
  - [x] Combined `and`/`or` logic  
  - [x] `not` usage  
  - [x] Errors on malformed expressions

- [x] **11.5** Integration Tests (Dep: 11.4)  
  - [x] Complex `if` statements  
  - [x] Nested logical blocks  
  - [x] Combined with file conditions or user input

- [x] **11.6** Update README with examples (Dep: 11.5)

---

## 🧠 Phase 10: Functions

### **12.0 Add Support for Function Definition and Invocation**

- [x] **12.1** Update Tokenizer to recognize `define` and `call` (Dep: 2.1)

- [x] **12.2** Add `FunctionDefinitionInstruction` and `FunctionCallInstruction` (Dep: 3.1)  
  - [x] Store block of instructions under a named function  
  - [x] Allow invoking stored instructions later

- [x] **12.3** Extend Parser to build function map (Dep: 12.2)  
  - [x] Parse `define <name>` and capture indented block  
  - [x] Add to function registry in the context  
  - [x] Validate `call <name>` instructions

- [x] **12.4** Update Runtime to support function call stack (Dep: 12.3)  
  - [x] Lookup function definition  
  - [x] Push and pop execution context

- [x] **12.5** Unit Tests (Dep: 12.4)  
  - [x] Define + call simple function  
  - [x] Nested calls  
  - [x] Call before definition → error

- [x] **12.6** Integration Tests (Dep: 12.5)  
  - [x] Scripts with multiple functions  
  - [x] Function inside loop or if block  
  - [x] Error for undefined function

- [x] **12.7** Update README and Examples (Dep: 12.6)  
  - [x] Syntax reference  
  - [x] Usage scenarios  
  - [x] Common mistakes

## 🧠 Phase 11: List Support

### **13.0 Implement List Features**

- [x] **13.1** Update Tokenizer to support list syntax (Dep: 2.1)  
  - [x] Parse statements like `variable items equals apple, banana and cherry`  
  - [x] Tokenize comma-and separated words as a list literal  
  - [x] Preserve quoted multi-word values

- [x] **13.2** Add `ListValue` class (Dep: 4.1)  
  - [x] Extend `Value` interface  
  - [x] Store items as string list  
  - [x] Provide `get(index)`, `size()`, and `includes()` methods

- [x] **13.3** Update `AssignmentInstruction` to support list values (Dep: 13.1, 13.2)  
  - [x] Recognize list-style variable initialization  
  - [x] Assign `ListValue` to variable context

- [x] **13.4** Add expression support for list access (Dep: 13.2)  
  - [x] Syntax: `item 1 in items`  
  - [x] Parse as expression node with list and index  
  - [x] Validate 1-based index is within bounds

- [x] **13.5** Add `includes` condition to expression evaluator (Dep: 4.3, 13.2)  
  - [x] Handle `if items includes apple`  
  - [x] Validate type is a list  
  - [x] Return boolean result

- [x] **13.6** Update `RepeatInstruction` to support list iteration (Dep: 10.3, 13.2)  
  - [x] Parse `repeat for each item in items`  
  - [x] Loop through list contents  
  - [x] Inject named loop variable (e.g., `item`)

- [x] **13.7** Expose `position` keyword during iteration (Dep: 13.6)  
  - [x] Automatically define `position` inside loop scope  
  - [x] Value starts at 1 and increments per iteration  
  - [x] Disallow reassignment of `position`

- [x] **13.8** Unit Tests (Dep: 13.7)  
  - [x] List assignment and resolution  
  - [x] `item N in list` access  
  - [x] `includes` checks  
  - [x] `position` accuracy inside loops

- [x] **13.9** Integration Tests (Dep: 13.8)  
  - [x] Declare list + access by index  
  - [x] Loop over list with `position`  
  - [x] Write indexed output to file  
  - [x] Use membership test in `if` condition

- [x] **13.10** Update README and Examples (Dep: 13.9)  
  - [x] Document list declaration  
  - [x] Explain `position` keyword  
  - [x] Provide examples with loops and indexing

## 💾 Phase 12: Enhanced File and Logging Operations

### **14.0 File Reading**
- [x] **14.1** Update Tokenizer to recognize `read file <file> into <variable>` (Dep: 2.1)
- [x] **14.2** Add `ReadFileInstruction` class (Dep: 3.1)
- [x] **14.3** Implement runtime logic to read file content and assign to variable (Dep: 14.2)
- [x] **14.4** Unit and integration tests (Dep: 14.3)

### **15.0 File Appending**
- [x] **15.1** Update Tokenizer to recognize `append <text> to <file>` (Dep: 2.1)
- [x] **15.2** Add `AppendToFileInstruction` class (Dep: 3.1)
- [x] **15.3** Implement logic to append to file (Dep: 15.2)
- [x] **15.4** Unit and integration tests (Dep: 15.3)

### **16.0 File Deletion**
- [x] **16.1** Update Tokenizer to recognize `delete file <file>` (Dep: 2.1)
- [x] **16.2** Add `DeleteFileInstruction` class (Dep: 3.1)
- [x] **16.3** Implement logic to delete file (Dep: 16.2)
- [x] **16.4** Unit and integration tests (Dep: 16.3)

### **17.0 Directory Listing**
- [x] **17.1** Update Tokenizer to recognize `list files in <directory> into <variable>` (Dep: 2.1)
- [x] **17.2** Add `ListDirectoryInstruction` class (Dep: 3.1)
- [x] **17.3** Implement logic to fetch file names from a directory and assign as list (Dep: 17.2)
- [x] **17.4** Unit and integration tests (Dep: 17.3)

### **18.0 Logging**
- [x] **18.1** Update Tokenizer to recognize `log <message>` (Dep: 2.1)
- [x] **18.2** Add `LogInstruction` class (Dep: 3.1)
- [x] **18.3** Implement logic to write logs to a default log file (Dep: 18.2)
- [x] **18.4** Unit and integration tests (Dep: 18.3)

### **19.0 File Copying**
- [x] **19.1** Update Tokenizer to recognize `copy file <source> to <destination>` (Dep: 2.1)
- [x] **19.2** Add `CopyFileInstruction` class (Dep: 3.1)
- [x] **19.3** Implement logic to copy contents from source to destination (Dep: 19.2)
- [x] **19.4** Unit and integration tests (Dep: 19.3)

## 🧠 Phase 13: Function Parameters and Return Values

### **20.0 Function Parameters**

- [x] **20.1** Update Tokenizer to parse parameterized function definitions (Dep: 2.1)  
  - [x] Handle `define <name> <param1> <param2> ...`  
  - [x] Tokenize parameters cleanly

- [x] **20.2** Extend `FunctionDefinitionInstruction` to store parameter list (Dep: 12.2)  
  - [x] Allow access to parameters by name within function body

- [x] **20.3** Extend `FunctionCallInstruction` to support `call <name> with <args>` (Dep: 12.2)  
  - [x] Validate argument count matches  
  - [x] Bind arguments to parameters

- [x] **20.4** Pass parameters into execution context during function call (Dep: 20.3)  
  - [x] Inject as scoped variables  
  - [x] Avoid polluting global context

### **21.0 Function Return Values**

- [x] **21.1** Update Tokenizer to recognize `return <expression>` (Dep: 2.1)  
  - [x] Must be inside a function

- [x] **21.2** Add `ReturnInstruction` (Dep: 3.1)  
  - [x] Evaluate and return value  
  - [x] Signal early function exit

- [x] **21.3** Extend `FunctionCallInstruction` to accept `into <variable>` (Dep: 20.3, 21.2)  
  - [x] Store returned value into provided variable

- [x] **21.4** Handle return state in runtime (Dep: 21.2)  
  - [x] Track whether a return was executed  
  - [x] Exit function block accordingly

### **22.0 Testing and Documentation**

- [x] **22.1** Unit Tests (Dep: 21.4)  
  - [x] Functions with/without parameters  
  - [x] Return behavior and argument mismatch handling

- [x] **22.2** Integration Tests (Dep: 22.1)  
  - [x] Nested functions  
  - [x] Functions with return values  
  - [x] Reassignment of return value

- [x] **22.3** Update README and Examples (Dep: 22.2)  
  - [x] Document `define`, `call with`, `return`, `into`  
  - [x] Show sample use cases
