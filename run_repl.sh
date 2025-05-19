#!/bin/bash

# Script to build and launch the Talk REPL standalone
# This bypasses issues with other components of the project
# and allows users to use the REPL while development continues

# Set the base directory
BASE_DIR="$(pwd)"
SRC_DIR="$BASE_DIR/app/src/main/java"
BUILD_DIR="$BASE_DIR/app/build/repl"
LIBS_DIR="$BASE_DIR/app/build/libs"

# Create build directory
mkdir -p $BUILD_DIR

# Clean previous build if it exists
rm -rf $BUILD_DIR/*

echo "Compiling REPL components..."

# We need to create a minimal Parser class for the REPL
mkdir -p "$BUILD_DIR/talk"
cat > "$BUILD_DIR/talk/Parser.java" << 'EOF'
package talk;

import java.util.ArrayList;
import java.util.List;
import talk.core.Instruction;
import talk.core.Tokenizer;

public class Parser {
    private List<Tokenizer.Token> tokens;
    
    public Parser(List<Tokenizer.Token> tokens) {
        this.tokens = tokens;
    }
    
    public List<Instruction> parse() {
        // A minimal implementation for the REPL
        return new ArrayList<>();
    }
}
EOF

# Compile the exception components first (these have no dependencies)
echo "Compiling exception classes..."
javac -d "$BUILD_DIR" $(find "$SRC_DIR/talk/exception" -name "*.java") 2>/dev/null
if [ $? -ne 0 ]; then
    echo "Warning: Some exceptions failed to compile, but we'll continue"
fi

# Compile the minimal Parser
echo "Compiling minimal Parser..."
javac -d "$BUILD_DIR" "$BUILD_DIR/talk/Parser.java"
if [ $? -ne 0 ]; then
    echo "Compilation failed for minimal Parser"
    exit 1
fi

# Compile the core components needed for the REPL
echo "Compiling core components..."
javac -d "$BUILD_DIR" -cp "$BUILD_DIR" "$SRC_DIR/talk/core/MinimalTalkRepl.java" 2>/dev/null
if [ $? -ne 0 ]; then
    echo "Warning: MinimalTalkRepl failed to compile, but we'll continue"
fi

# Compile the expression components
echo "Compiling expression components..."
javac -d "$BUILD_DIR" -cp "$BUILD_DIR" $(find "$SRC_DIR/talk/expression" -name "*.java" | grep -v "DateUtil.java") 2>/dev/null
if [ $? -ne 0 ]; then
    echo "Warning: Some expression classes failed to compile, but we'll continue"
fi

# Compile the runtime components
echo "Compiling runtime components..."
javac -d "$BUILD_DIR" -cp "$BUILD_DIR" "$SRC_DIR/talk/runtime/InstructionExecutor.java" 2>/dev/null
if [ $? -ne 0 ]; then
    echo "Warning: InstructionExecutor failed to compile, but we'll continue"
fi

# Compile the ReplLauncher
echo "Compiling REPL launcher..."
javac -d "$BUILD_DIR" -cp "$BUILD_DIR" "$SRC_DIR/talk/ReplLauncher.java"
if [ $? -ne 0 ]; then
    echo "Compilation failed for ReplLauncher"
    exit 1
fi

echo "Compilation completed. Launching REPL..."

# Create a wrapper class to bypass possible missing dependencies
cat > "$BUILD_DIR/talk/MockExpression.java" << 'EOF'
package talk;

/**
 * A mock class to help with REPL standalone mode
 */
public class MockExpression {
    public static final Object NULL_VALUE = new Object();
}
EOF

javac -d "$BUILD_DIR" "$BUILD_DIR/talk/MockExpression.java"

# Run the REPL
echo "Starting Talk REPL..."
echo "Note: This is a standalone REPL with limited functionality."
echo "Some features may not work due to missing dependencies."
echo "========================================================"
java -cp "$BUILD_DIR" talk.ReplLauncher
