#!/bin/bash

# Simple script to compile and run the standalone REPL

echo "Compiling standalone REPL..."
mkdir -p ./app/build/standalone
javac -d ./app/build/standalone ./app/src/main/java/talk/StandaloneRepl.java

if [ $? -eq 0 ]; then
    echo "Compilation successful. Starting Talk REPL..."
    echo "=================================================="
    java -cp ./app/build/standalone talk.StandaloneRepl
else
    echo "Compilation failed."
    exit 1
fi
