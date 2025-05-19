# Quickstart Guide: Conversational Programming Language (`.talk`)

Welcome! This guide will help you get started with the `.talk` language in just a few minutes.

---

## 1. Prerequisites
- Java 17 or newer installed (`java -version`)
- Gradle (optional, for building from source)

---

## 2. Build the CLI
If you have the source code:
```sh
./gradlew shadowJar
```
This creates a runnable JAR at `app/build/libs/talk.jar`.

---

## 3. Run Your First Script

1. **Try a sample script:**
   ```sh
   java -jar app/build/libs/talk.jar run app/examples/hello_world.talk
   ```
   This will create `hello.txt` with the message `Hello from .talk!`.

2. **Explore more examples:**
   - `app/examples/variable_condition.talk`
   - `app/examples/file_operations.talk`
   - `app/examples/input_fallback.talk`

---

## 4. Interactive REPL

The Talk language includes an interactive REPL (Read-Eval-Print Loop) that allows you to experiment with Talk commands one line at a time.

1. **Start the REPL:**
   ```sh
   java -jar app/build/libs/talk.jar repl
   ```

2. **Use the standalone REPL** (for quick testing without building the entire project):
   ```sh
   ./repl.sh
   ```

3. **Use the enhanced REPL** (with arrow key navigation, history search, and tab completion):
   ```sh
   ./enhanced_repl.sh
   ```

4. **Basic REPL commands:**
   ```
   > write "Hello, World!"
   Hello, World!
   
   > variable x = 10
   
   > x + 5
   15
   
   > help
   (shows available commands and examples)
   
   > exit
   (exits the REPL session)
   ```

5. **Enhanced REPL features:**
   - Use up/down arrow keys to navigate through command history
   - Press Tab to autocomplete keywords and variable names
   - Use Ctrl+R to search through command history
   - History is saved between sessions

6. **For more detailed examples and documentation:**
   - See `docs/repl_guide.md` and `docs/repl_examples.md`

---

## 5. Write Your Own Script
Create a file, e.g. `my_script.talk`:
```
variable name
ask "What is your name?" and store in name
write "Hello, " + name in greeting.txt
```
Run it:
```sh
java -jar app/build/libs/talk.jar run my_script.talk
```

---

## 6. Language Basics
- See `README.md` for syntax, examples, and error handling.
- See `docs/specs.md` for full language specification.

---

## 7. Troubleshooting
- If you see an error, check the line number and hint in the message.
- Make sure your script uses the correct syntax (see README examples).
- For help: `java -jar app/build/libs/talk.jar --help`

---

Happy scripting! 🚀
