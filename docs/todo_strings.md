```markdown
---

## 🧠 Phase 15: Extended String Utilities

### **28.0 Core String Operations**

- [x] **28.1** Tokenizer support for `uppercase of`, `lowercase of`, `trim of`, `length of` (Dep: 2.1)
- [x] **28.2** Add expression evaluators for:
  - [x] `uppercase of <value>`
  - [x] `lowercase of <value>`
  - [x] `trim of <value>`
  - [x] `length of <value>` (string version)
- [x] **28.3** Type validation and tests (Dep: 28.2)

### **29.0 Substring and Replace**

- [x] **29.1** Tokenizer support for `substring of <value> from <start> to <end>` (Dep: 2.1)
- [x] **29.2** Tokenizer support for `replace <a> with <b> in <value>` (Dep: 2.1)
- [x] **29.3** Add evaluators:
  - [x] `substring of ...`
  - [x] `replace ... with ... in ...`
- [x] **29.4** Validation, slicing logic, and error handling (Dep: 29.3)

### **30.0 Matching Conditions**

- [x] **30.1** Tokenizer support for `contains`, `starts with`, `ends with` (Dep: 2.1)
- [x] **30.2** Add boolean evaluators for:
  - [x] `if X contains Y`
  - [x] `if X starts with Y`
  - [x] `if X ends with Y`
- [x] **30.3** Type safety and fallback handling (Dep: 30.2)

### **31.0 Split Support**

- [x] **31.1** Tokenizer support for `split <value> by <delimiter>` (Dep: 2.1)
- [x] **31.2** Evaluate `split` and return list value (Dep: 13.2)
- [x] **31.3** Validate string input and generate list output (Dep: 31.2)

### **32.0 Documentation and Examples**

- [x] **32.1** Add `.talk` examples for all string utilities (Dep: 31.3)
- [x] **32.2** Update user guide with supported expressions and usage rules
```
