package talk;

import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Parameters;
import picocli.CommandLine.Option;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.concurrent.Callable;

@Command(name = "talk", mixinStandardHelpOptions = true, version = "talk 0.1",
        description = "Conversational Programming Language CLI")
public class TalkRunner implements Callable<Integer> {
    @Option(names = {"-h", "--help"}, usageHelp = true, description = "Display help message")
    boolean helpRequested;

    @Parameters(index = "0", description = "The command to execute (e.g. run)")
    private String command;

    @Parameters(index = "1", description = "The .talk script file to run", arity = "0..1")
    private String scriptFile;

    @Override
    public Integer call() throws Exception {
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
        } else {
            System.err.println("Unknown command: " + command);
            CommandLine.usage(this, System.out);
            return 1;
        }
        return 0;
    }

    public static void main(String[] args) {
        int exitCode = new CommandLine(new TalkRunner()).execute(args);
        System.exit(exitCode);
    }
}
