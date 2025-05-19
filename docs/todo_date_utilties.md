## 🧠 Phase 17: Date and Time Utilities

### **36.0 Date and Time Expressions**

- [x] **36.1** Tokenizer support for: `now`, `today`, `format date`, `add <n> days to`, `subtract <n> days from` (Dep: 2.1)
- [x] **36.2** Implement:
  - [x] `now` and `today`
  - [x] `format date <value> as <pattern>`
  - [x] `add <n> days to <date>`
  - [x] `subtract <n> days from <date>`

- [x] **36.3** Tokenizer and parser for:
  - [x] `difference in days between <a> and <b>`
  - [x] `day of week of <date>`
  - [x] `parse date <string>`

- [x] **36.4** Tokenizer for conditions:
  - [x] `if <a> is before <b>`
  - [x] `if <a> is after <b>`

### **37.0 Date Runtime and Tests**

- [x] **37.1** Implement evaluators for all expressions (Dep: 36.2, 36.3, 36.4)
- [x] **37.2** Validate format correctness and error handling
- [x] **37.3** Unit tests for:
  - [x] Arithmetic, formatting, and parsing
  - [x] Logical date comparisons

### **38.0 Integration and Docs**

- [x] **38.1** Integration tests for scripts using date utilities (Dep: 37.3)
- [x] **38.2** Add `.talk` examples and README section (Dep: 38.1)
```
