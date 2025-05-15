```markdown
### 15. Extended String Utilities

This section expands the `.talk` language with richer, plain-English string manipulation capabilities.

#### Supported String Utilities

| Feature       | Description                                              | Syntax Example |
|---------------|----------------------------------------------------------|----------------|
| `uppercase`   | Converts to uppercase                                    | `set x to uppercase of name` |
| `lowercase`   | Converts to lowercase                                    | `set x to lowercase of title` |
| `trim`        | Removes whitespace from both ends                        | `set name to trim of fullName` |
| `length`      | Counts characters                                        | `set count to length of message` |
| `contains`    | Checks if substring exists                               | `if message contains "error" then ...` |
| `starts with` | Checks beginning of string                               | `if name starts with "Dr" then ...` |
| `ends with`   | Checks ending of string                                  | `if filename ends with ".txt" then ...` |
| `replace`     | Replace substring                                        | `set result to replace "foo" with "bar" in text` |
| `substring`   | Extracts string slice by position                        | `set part to substring of message from 1 to 5` |
| `split`       | Splits string into list by delimiter                     | `set words to split sentence by " "` |

#### Notes:
- Indexes in `substring` are 1-based.
- All operations fail gracefully if used on non-strings (unless wrapped in `attempt`).
- Output types: `split` returns a list, others return strings or booleans.
```
