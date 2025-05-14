# Refactoring TODOs for Conversational Programming Language Java Project

## Classes Violating Single Responsibility Principle (SRP)
- [x] Refactor `Tokenizer` to extract logic for each special-case instruction (e.g., list assignment, function definition, file operations) into dedicated handler classes or methods.
- [x] Extract indentation tracking and INDENT/DEDENT token logic from `Tokenizer` into a separate `IndentationManager` class.
- [x] Refactor `Parser` to move instruction-specific parsing (e.g., function, loop, file ops) into dedicated `InstructionParser` classes or methods.
- [x] Move error formatting logic out of `TalkRunner` and into a dedicated `ErrorHandler` or `ErrorFormatter` utility (if not already done).

## Code Duplication
- [x] Consolidate repeated logic for parsing indented blocks in `Parser` (e.g., for `if`, `repeat`, `attempt`, `define`) into a shared utility method.
- [x] Unify argument and parameter parsing for function calls and definitions in `Parser` to avoid duplicate code paths.
- [x] Extract repeated file operation logic (e.g., file existence checks, reading, writing) from instruction classes into a shared `FileUtils` helper.

## Open/Closed Principle (OCP) & Extensibility
- [x] Introduce an `InstructionFactory` to decouple instruction creation from the `Parser`, enabling easier addition of new instructions.
- [x] Define an `Instruction` interface with clear extension points for new instruction types.
- [x] Use a registry or map for instruction keyword-to-class mapping in the parser, rather than hardcoding each instruction.

## Dependency Inversion & Interface Segregation
- [ ] Inject dependencies (e.g., file system, logger) into instruction classes via interfaces, not direct instantiation, to improve testability and flexibility.
- [ ] Define interfaces for file operations and logging, and depend on those abstractions in instruction classes.

## Error Handling & Robustness
- [ ] Standardize exception types for syntax, semantic, and runtime errors throughout the parser and executor.
- [ ] Ensure all error messages include line numbers and context for easier debugging.
- [ ] Add more granular error handling in `InstructionExecutor` for each instruction type.

## Test Structure & DRY Principle
- [ ] Refactor unit tests to use parameterized tests for similar instruction types (e.g., file ops, variable assignment).
- [ ] Extract common test setup code in `TokenizerTest`, `ParserTest`, and `InstructionExecutorTest` into shared utility methods or a base class.
- [ ] Add integration tests for error scenarios and edge cases not currently covered (e.g., deeply nested blocks, invalid indentation, argument mismatch).

## Miscellaneous
- [ ] Remove or gate all `[DEBUG]` print statements in production code; use a logger with configurable log levels instead.
- [ ] Document all public classes and methods with Javadoc for maintainability.
- [ ] Review and enforce consistent naming conventions for variables, methods, and classes across the codebase.
