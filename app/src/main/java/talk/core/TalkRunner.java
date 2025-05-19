package talk.core;

import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Parameters;
import picocli.CommandLine.Option;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.concurrent.Callable;
import talk.Parser;
import talk.linter.TalkLinter;
import talk.runtime.InstructionExecutor;
import talk.util.ErrorFormatter;
import talk.plugins.BuiltInPlugins;
import talk.plugins.PluginConfigLoader;

@Command(name = "talk", mixinStandardHelpOptions = true, version = "talk 0.1",
        description = "Conversational Programming Language CLI")
public class TalkRunner implements Callable<Integer> {
    @Option(names = {"-h", "--help"}, usageHelp = true, description = "Display help message")
    boolean helpRequested;

    @Parameters(index = "0", description = "The command to execute (e.g. run)")
    private String command;

    @Parameters(index = "1", description = "The .talk script file to run", arity = "0..1")
    private String scriptFile;
    
    @Option(names = {"-p", "--plugin-config"}, description = "Custom plugin configuration file path")
    private String pluginConfigPath;

    @Override
    public Integer call() throws Exception {
        // Initialize plugin system
        initializePlugins();
        
        if (helpRequested) {
            CommandLine.usage(this, System.out);
            return 0;
        }
        if ("run".equals(command)) {
            if (scriptFile == null || !scriptFile.endsWith(".talk")) {
                System.err.println("Error: Please provide a .talk script file.");
                return 1;
            }
            File file = new File(scriptFile);
            if (!file.exists()) {
                System.err.println("Error: File not found: " + scriptFile);
                return 1;
            }
            System.out.println("Running script...");
            try {
                List<String> lines = Files.readAllLines(Paths.get(scriptFile));
                Tokenizer tokenizer = new Tokenizer();
                List<Tokenizer.Token> tokens = tokenizer.tokenize(lines);
                Parser parser = new Parser(tokens);
                List<Instruction> instructions = parser.parse();
                RuntimeContext ctx = new RuntimeContext();
                InstructionExecutor exec = new InstructionExecutor(ctx);
                for (Instruction instr : instructions) {
                    try {
                        exec.execute(instr);
                    } catch (Exception e) {
                        String msg = ErrorFormatter.format(e, instr.getLineNumber(), "Check your syntax or variable usage.");
                        System.err.println(msg);
                        System.out.println("Script execution failed.");
                        return 1;
                    }
                }
                System.out.println("Script executed successfully.");
            } catch (Exception e) {
                String msg = ErrorFormatter.format(e, -1, "Script failed to run. See above for details.");
                System.err.println(msg);
                System.out.println("Script execution failed.");
                return 1;
            }
        } else if ("test".equals(command)) {
            if (scriptFile == null || !scriptFile.endsWith(".talk")) {
                System.err.println("Error: Please provide a .talk test file.");
                return 1;
            }
            File file = new File(scriptFile);
            if (!file.exists()) {
                System.err.println("Error: File not found: " + scriptFile);
                return 1;
            }
            System.out.println("Running tests...");
            try {
                boolean allPassed = TestRunner.runTests(scriptFile);
                return allPassed ? 0 : 1;
            } catch (Exception e) {
                String msg = ErrorFormatter.format(e, -1, "Tests failed to run. See above for details.");
                System.err.println(msg);
                return 1;
            }
        } else if ("lint".equals(command)) {
            if (scriptFile == null || !scriptFile.endsWith(".talk")) {
                System.err.println("Error: Please provide a .talk script file to lint.");
                return 1;
            }
            File file = new File(scriptFile);
            if (!file.exists()) {
                System.err.println("Error: File not found: " + scriptFile);
                return 1;
            }
            System.out.println("Linting script...");
            try {
                TalkLinter linter = new TalkLinter();
                linter.lint(scriptFile); // No need to store the returned issues
                linter.printIssues();
                
                // Return non-zero exit code if there are errors
                return linter.getErrorCount() > 0 ? 1 : 0;
            } catch (Exception e) {
                String msg = ErrorFormatter.format(e, -1, "Linting failed. See above for details.");
                System.err.println(msg);
                return 1;
            }
        } else if ("repl".equals(command)) {
            System.out.println("Starting Talk interactive REPL...");
            try {
                TalkRepl repl = new TalkRepl();
                repl.start();
                return 0;
            } catch (Exception e) {
                String msg = ErrorFormatter.format(e, -1, "REPL session failed.");
                System.err.println(msg);
                return 1;
            }
        } else {
            System.err.println("Unknown command: " + command);
            System.err.println("Available commands: run, test, lint, repl");
            CommandLine.usage(this, System.out);
            return 1;
        }
        return 0;
    }
    
    /**
     * Initialize the plugin system
     */
    private void initializePlugins() {
        // Register built-in plugins
        BuiltInPlugins.registerAll();
        System.out.println("Built-in plugins registered.");
        
        // Load plugins from configuration if specified
        if (pluginConfigPath != null) {
            boolean loaded = PluginConfigLoader.loadPlugins(pluginConfigPath);
            if (loaded) {
                System.out.println("Custom plugins loaded from: " + pluginConfigPath);
            } else {
                System.err.println("Failed to load plugins from: " + pluginConfigPath);
            }
        } else {
            // Try to load default plugins configuration
            boolean loaded = PluginConfigLoader.loadDefaultPlugins();
            if (loaded) {
                System.out.println("Default plugins loaded.");
            }
        }
    }

    public static void main(String[] args) {
        int exitCode = new CommandLine(new TalkRunner()).execute(args);
        System.exit(exitCode);
    }
}
