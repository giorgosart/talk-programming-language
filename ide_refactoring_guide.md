# Package Refactoring with IntelliJ IDEA

## Preparation
1. Open the Talk Programming Language project in IntelliJ IDEA
2. Make sure you have a clean working directory in Git (commit any pending changes)
3. Consider creating a new branch for this refactoring

## Steps for Each File

For each file, follow these steps:

1. Open the file in the editor
2. Right-click on the file name in the Project view
3. Select "Refactor" > "Move..."
4. In the dialog, enter the new package name (e.g., `talk.core`, `talk.instruction`, etc.)
5. Click "Refactor" or "Preview" (if you want to see the changes first)
6. IntelliJ will automatically:
   - Move the file to the new package directory
   - Update the package declaration
   - Update all import statements in other files
   - Resolve conflicts if any

## Migration Order
Follow this order to minimize dependency issues:

1. talk.util (ErrorFormatter)
2. talk.io (FileSystem, DefaultFileSystem, etc.)
3. talk.expression (ExpressionResolver, ListValue, etc.)
4. talk.core (Parser, Tokenizer, etc.)
5. talk.instruction (all instruction classes)
6. talk.runtime (InstructionExecutor)

## Verification
After moving all files:

1. Build the project (Build > Build Project)
2. Run all tests to ensure everything still works
3. Fix any remaining issues

## Note
This refactoring can be done incrementally. You can commit after each package is migrated to make it easier to track changes or roll back if needed.
