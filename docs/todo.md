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

- [ ] **12.7** Update README and Examples (Dep: 12.6)  
  - [ ] Syntax reference  
  - [ ] Usage scenarios  
  - [ ] Common mistakes

➡️ **Next:** Update README and add example scripts to document function syntax, usage, and common mistakes.