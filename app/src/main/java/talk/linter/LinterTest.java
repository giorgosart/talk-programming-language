package talk.linter;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

/**
 * A test driver for the Talk language linter
 */
public class LinterTest {

    public static void main(String[] args) {
        if (args.length == 0) {
            System.out.println("Usage: java talk.linter.LinterTest <script.talk>");
            System.exit(1);
        }
        
        String scriptPath = args[0];
        Path path = Paths.get(scriptPath);
        
        if (!Files.exists(path)) {
            System.err.println("File not found: " + scriptPath);
            System.exit(1);
        }
        
        if (!scriptPath.endsWith(".talk")) {
            System.err.println("File must be a .talk script");
            System.exit(1);
        }
        
        try {
            System.out.println("Linting: " + scriptPath);
            TalkLinter linter = new TalkLinter();
            List<LintIssue> issues = linter.lint(scriptPath);
            
            linter.printIssues();
            
            int errorCount = linter.getErrorCount();
            int warningCount = linter.getWarningCount();
            
            System.out.println("\nLinting summary:");
            System.out.println("  Errors:   " + errorCount);
            System.out.println("  Warnings: " + warningCount);
            
            System.exit(errorCount > 0 ? 1 : 0);
        } 
        catch (IOException e) {
            System.err.println("Error reading file: " + e.getMessage());
            System.exit(1);
        }
        catch (Exception e) {
            System.err.println("Linting error: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
    }
}
