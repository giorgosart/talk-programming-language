# 📝 Product Requirements Document: Conversational Programming Language MVP
## 🚀 Project Overview
This project aims to develop a conversational programming language that wraps around Java, allowing non-technical users to write general-purpose scripts using natural, English-like syntax. The language is designed to lower the barrier to entry for programming by using simple, readable statements with strict keyword rules.

The MVP will focus on supporting basic logic, file operations, variable management, user input, and simple control flow.

## ✅ Core Requirements
- Users can write scripts in natural English using a strict set of keywords.
- Scripts are interpreted line-by-line (like a shell script).
- The system translates .talk scripts into executable Java code.
- Errors are handled gracefully and stop execution unless explicitly handled.
- Parsing should be extensible to allow richer phrasing in future versions.

## ✨ Core Features
### 1. Variable Handling
- `variable x` — declare variable
- `variable x equal 10` — declare and set
- `set x to y` — set existing variable

### 2. Control Flow
- `if x is greater than 10 then ... otherwise ...`
- Supports: `is greater than`, `is smaller than`, `is equal to`, `is not equal to`

### 3. File Interaction
- `create file as 'report.txt'`
- `write "Hello" in report.txt`
- `if file 'report.txt' is empty`
- `if file 'report.txt' does not exist`

### 4. Error Handling
- `attempt ... if that fails ...`
- Fail-fast behavior by default

### 5. User Input
- `ask "What is your name?" and store in name`
- Optional validation: `must be a number`, `must be one of yes, no`
- Optional retry: `retry if invalid`

### 6. Comments
- Any line starting with `#` is treated as a comment

## 🧩 Core Components
### 1. Parser
- Reads `.talk` files line-by-line
- Tokenizes and interprets based on strict keyword syntax

### 2. Interpreter/Compiler
- Maps parsed instructions to Java equivalents
- Handles variable scopes, flow logic, and runtime context

### 3. Runtime
- Executes the generated Java code or uses a Java backend directly
- Handles I/O, file access, condition checking, and error reporting

### 4. Error Handler
- Provides useful, human-readable error messages
- Supports optional `attempt` block handling

## 🔁 App/User Flow
- User writes a script in a `.talk` file
- The interpreter parses the file top-down
- Variables are declared and tracked in context
- Conditions and actions are evaluated line by line
- Errors stop execution unless wrapped in `attempt`
- Outputs are written to console or files as defined

## 🧱 Tech Stack
|Component	|Technology|
|------| ------|
|Language runtime|	Java|
|CLI Tool	|Java-based parser + executor|
|File format	|.talk|
|Build & compile	|Gradle|
|Future tooling	| VS Code extension (planned), Playground (planned)|

## 🛠 Implementation Plan
### Phase 1: Foundations
- [x] Define grammar and keywords
- [x] Set up Java project with parsing infrastructure
- [x] Create TalkParser and TalkRuntimeContext

### Phase 2: Core Feature Support
- [ ] Implement variable handling and context
- [ ] Implement control flow parser + evaluator
- [ ] Add file I/O operations (create, write, exists, empty)
- [ ] Implement basic arithmetic and comparisons
- [ ] Build support for user input and validation
- [ ] Implement attempt / if that fails error blocks

### Phase 3: CLI Tool
- [ ] Build command-line runner: talk run script.talk
- [ ] Add helpful error output and debugging

### Phase 4: Testing and Iteration
- [ ] Add unit tests for each feature
- [ ] Gather early feedback and refine syntax

### Phase 5: Documentation & Examples
- [ ] Write user guide
- [ ] Provide sample .talk scripts
- [ ] Prepare demo video