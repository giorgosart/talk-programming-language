# Talk Language Test Runner Guide

## Overview

The Talk programming language includes a built-in testing framework that allows you to write tests in the same natural language style as the rest of your Talk code. This guide explains how to write and run tests using the Talk test framework.

## Writing Tests

### Basic Test Structure

Tests in Talk are organized into test blocks with descriptive names. Each test block can contain:
- Setup code (variable assignments, function calls, etc.)
- Assertions using the `expect` syntax
- Cleanup code

A simple test looks like this:

```talk
test "Adding two numbers"
    set x to 5
    set y to 10
    expect result of x + y to be 15
```

### Test Assertions

The primary way to make assertions in Talk is with the `expect result of` syntax:

```talk
# Basic equality assertion
expect result of expression to be expected_value

# Examples:
expect result of 5 + 5 to be 10
expect result of "hello" + " world" to be "hello world"
expect result of my_function with arg1 to be "expected output"
```

### Test Organization

You can organize your tests into multiple test blocks:

```talk
test "First test group"
    set x to 10
    expect result of x * 2 to be 20
    
test "Second test group"
    set y to "hello"
    expect result of y + " world" to be "hello world"
```

### Test Setup and Teardown

For tests that need setup and cleanup code, you can use the `before each test` and `after each test` blocks:

```talk
before each test
    # This code runs before every test
    create file "test_data.txt"
    set counter to 0
    
test "File operations test"
    write "test content" to "test_data.txt"
    read "test_data.txt" into file_content
    expect result of file_content to be "test content"
    
after each test
    # This code runs after every test
    delete file "test_data.txt"
```

## Running Tests

### Command Line Interface

To run your tests, use the `test` command with the Talk CLI:

```bash
talk test path/to/your_test_file.talk
```

### Test Output

When you run tests, you'll see output like this:

```
Running tests from path/to/your_test_file.talk

--- Test Results ---
✓ PASS: Adding two numbers (line 2) - Test passed
✓ PASS: First test group (line 6) - Test passed
✗ FAIL: Second test group (line 10) - Expected: "hello world", but got: "helloworld"

Summary: 2 passed, 1 failed, 3 total
```

### Exit Codes

The test runner will exit with:
- `0` if all tests pass
- `1` if any test fails or an error occurs

This makes it easy to integrate with CI/CD pipelines and automated scripts.

## Best Practices

1. **Descriptive Test Names**: Give your test blocks clear, descriptive names
2. **One Assertion Per Test**: When possible, keep tests focused on a single behavior
3. **Independent Tests**: Make sure tests don't depend on each other's state
4. **Clean Up Resources**: Use `after each test` to clean up any files or resources created during tests
5. **Group Related Tests**: Organize related tests into separate files

## Example Test File

Here's a complete example test file that shows various testing features:

```talk
# Example test file: calculator_tests.talk

before each test
    # Setup for all tests
    set calculator_ready to true
    
test "Addition functionality"
    set result to 5 + 3
    expect result of result to be 8
    
test "Subtraction functionality"
    set result to 10 - 4
    expect result of result to be 6
    
test "Multiplication functionality"
    set result to 3 * 7
    expect result of result to be 21
    
test "Division functionality"
    set result to 20 / 5
    expect result of result to be 4
    
after each test
    # Clean up after each test
    set calculator_ready to false
```

Run this test file with:
```bash
talk test calculator_tests.talk
```
