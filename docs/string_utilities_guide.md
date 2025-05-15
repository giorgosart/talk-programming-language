# String Utilities in Talk Programming Language

Talk programming language includes a variety of string manipulation utilities to help you process and transform text. These utilities are designed to be clear, intuitive, and easy to use in everyday programming tasks.

## Core String Operations

### Uppercase

Convert a string to uppercase:

```
set message to "hello world"
set uppercase_message to uppercase of message
# Result: "HELLO WORLD"
```

### Lowercase

Convert a string to lowercase:

```
set message to "HELLO WORLD"
set lowercase_message to lowercase of message
# Result: "hello world"
```

### Trim

Remove whitespace from both ends of a string:

```
set text to "  hello world  "
set trimmed_text to trim of text
# Result: "hello world"
```

### Length

Count the number of characters in a string:

```
set message to "hello world"
set message_length to length of message
# Result: 11
```

## Substring and Replace

### Substring

Extract a portion of a string by position (1-based indexing):

```
set message to "hello world"
set part to substring of message from 1 to 5
# Result: "hello"
```

### Replace

Replace occurrences of a substring with another string:

```
set message to "hello world"
set new_message to replace "world" with "talk" in message
# Result: "hello talk"
```

## Matching Conditions

### Contains

Check if a string contains a specified substring:

```
if message contains "hello" then
    log "Found the greeting!"
```

### Starts With

Check if a string starts with a specified prefix:

```
if filename starts with "data_" then
    log "This is a data file"
```

### Ends With

Check if a string ends with a specified suffix:

```
if filename ends with ".txt" then
    log "This is a text file"
```

## Split Support

### Split

Split a string into a list of parts using a delimiter:

```
set sentence to "hello, world, welcome"
set parts to split sentence by ", "
# Result: a list with ["hello", "world", "welcome"]

# Accessing parts of the split string
log item 1 in parts  # Outputs: "hello"
log item 2 in parts  # Outputs: "world"
log item 3 in parts  # Outputs: "welcome"
```

## Notes

- All string operations fail gracefully if used on non-string values. If you want to handle errors, wrap the operation in an `attempt` block.
- Indexes in substring operations are 1-based (the first character is at position 1).
- All operations except `split` return strings or booleans, while `split` returns a list.

## Examples

Combine multiple string operations:

```
set text to "  MIXED case TEXT  "
set processed to lowercase of trim of text
# Result: "mixed case text"
```

Using variables in string operations:

```
set search_term to "world"
set replacement to "universe"
set message to "hello world"
set new_message to replace search_term with replacement in message
# Result: "hello universe"
```
