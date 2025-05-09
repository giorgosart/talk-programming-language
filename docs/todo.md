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
