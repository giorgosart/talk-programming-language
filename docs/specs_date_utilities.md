```markdown
### 17. Date and Time Utilities

`.talk` includes human-readable ways to work with dates and times using built-in expressions.

#### Supported Date Utilities

| Feature              | Description                                       | Example Syntax |
|----------------------|---------------------------------------------------|----------------|
| `now`                | Current date and time as string                   | `set start to now` |
| `today`              | Current date (without time)                       | `set today to today` |
| `format date`        | Formats a date using a pattern                    | `set output to format date now as "yyyy-MM-dd"` |
| `add days`           | Adds days to a date                               | `set next to add 3 days to today` |
| `subtract days`      | Subtracts days from a date                        | `set previous to subtract 7 days from today` |
| `difference in days` | Returns number of days between two dates         | `set gap to difference in days between start and end` |
| `day of week`        | Gets the day name from a date                     | `set weekday to day of week of today` |
| `is before`          | Checks if one date is earlier than another        | `if deadline is before today then` |
| `is after`           | Checks if one date is after another               | `if today is after startDate then` |
| `parse date`         | Converts string to date object                    | `set date to parse date "2024-01-01"` |

#### Notes:
- Dates should follow ISO `yyyy-MM-dd` format unless formatted or parsed.
- Errors (e.g., invalid date input) raise runtime exceptions unless inside `attempt`.
```
