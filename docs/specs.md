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

### 7. Block Scoping with Indentation
- Indentation (4 spaces) defines a block of instructions.
- Used for `if`, `otherwise`, `attempt`, and future loop support.
- Allows nested blocks using consistent indentation depth.
- No need for braces or explicit `end` statements.

**Example:**
```plaintext
if x is greater than 10 then
    write "big" in "log.txt"
    if x is greater than 100 then
        write "huge" in "log.txt"
otherwise
    write "small" in "log.txt"
```

#### Rules:
- Block starts when indentation increases after a control keyword.
- Block ends when indentation decreases.
- Mixed spaces/tabs are disallowed (parser error).

### 8. Looping Constructs

Loops allow repeated execution of a block of instructions. The following loop types are supported:

#### 1. Fixed-count loops
```plaintext
repeat 5 times
    write "Hello" in "log.txt"
```
- Repeats the block 5 times.
- The loop variable _index is implicitly available (starts from 0).

#### 2. Condition-based loops (future)
```
repeat while x is smaller than 10
    set x to x + 1
```
- Repeats the block while the condition is true.
- Note: Only fixed-count loops are supported in the current MVP.

#### 3. List-based loops (future)
```
repeat for each item in items
    write item in "output.txt"
```
- Iterates over a list.
- Planned for future releases.

#### Rules:
- Loop body is defined by indentation.
- Loop variables persist within the block but not globally.
- Loops can be nested inside each other or inside if blocks.


### 9. Logical Operators

Logical operators extend conditional expressions:

**Supported operators:**
- `and`
- `or`
- `not`

**Examples:**
```plaintext
if x is greater than 5 and y is equal to 3 then
    write "Valid" in "result.txt"
if not x is equal to 10 then
    write "x is not 10" in "result.txt"
```

#### Rules:
-Logical operators must be used with valid boolean expressions.
-Operator precedence: not > and > or (left to right within the same precedence level).

10. Functions
Functions allow you to reuse a block of instructions:

Definition Syntax:
```
define greeting
    write "Hello" in "greeting.txt"
```

Call Syntax:
```
call greeting
```
#### Rules:
- Functions must be defined before use.
- Parameters are not supported in MVP (planned for future).
- Function names must be unique and declared using define.

### 11. Lists

Lists allow users to store multiple values in a single variable and work with them through indexing, looping, and membership tests.

#### 1. Declaring a List
```plaintext
variable items equals apple, banana and cherry
```

- Creates a list variable items with the given values.
- List values are automatically treated as strings.
- Use and or comma and to separate items.
- Values with spaces must be quoted (e.g., "green apple").

#### 2. Accessing a List Item
```
set x to item 1 in items
```
- Uses 1-based indexing: item 1 refers to the first item.
- Future versions may support: first item, last item.

#### 3. Looping Over a List
```
repeat for each item in items
    write position and item in "log.txt"
```
- Iterates through each item in the list.
- The loop variable (item above) is available inside the block.
- The current index is exposed as `position` (starting from 1).
- `position` is read-only and only exists inside the loop.

#### 4. Membership Checks
```
if items includes apple then
    write "Found it!" in "log.txt"
```
- Returns true if the list contains the given value.
- Only the list includes value form is supported in MVP.

#### Notes:
- Lists cannot be modified in MVP (no append/remove).
- No nested lists or dictionaries.
- Values must be quoted if they contain multiple words.

### 12. Enhanced File and Logging Operations

These features extend file I/O beyond writing and creation.

#### 1. Reading a File
```plaintext
read file report.txt into content
```
- Reads the contents of `report.txt` into the variable `content`
- Content is stored as a single string (newline characters preserved)

#### 2. Appending to a File
```plaintext
append "Hello again" to report.txt
```
- Adds the specified text to the end of the file
- Creates the file if it doesn’t exist

#### 3. Deleting a File
```plaintext
delete file report.txt
```
- Deletes the file if it exists
- Throws an error if the file is missing (unless in `attempt`)

#### 4. Listing Files in a Directory
```plaintext
list files in "data" into filenames
```
- Stores a list of filenames from the directory `data` into variable `filenames`

#### 5. Logging
```plaintext
log "System started"
```
- Writes the message to a default log file (e.g., `talk.log`)
- Optionally extended later: `log "message" to "filename.log"`


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