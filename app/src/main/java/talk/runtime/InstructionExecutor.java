package talk.runtime;

import java.util.List;
import java.io.IOException;
import java.util.Scanner;
import java.io.InputStream;

import talk.core.Instruction;
import talk.Parser;
import talk.core.RuntimeContext;
import talk.exception.*;
import talk.expression.ExpressionResolver;
import talk.expression.ListValue;
import talk.instruction.*;
import talk.io.DefaultFileSystem;
import talk.io.DefaultLogger;
import talk.io.FileSystem;
import talk.io.Logger;
import talk.expression.DateUtil;
import talk.plugins.PluginRegistry;

public class InstructionExecutor {
    private final RuntimeContext context;
    private final ExpressionResolver resolver;
    private final Scanner scanner;
    private final FileSystem fileSystem;
    private final Logger logger;

    // Constructor with full dependency injection
    public InstructionExecutor(RuntimeContext context, InputStream in, FileSystem fileSystem, Logger logger) {
        this.context = context;
        this.resolver = new ExpressionResolver(context);
        this.scanner = new Scanner(in);
        this.fileSystem = fileSystem;
        this.logger = logger;
    }

    // Backward compatibility constructors
    public InstructionExecutor(RuntimeContext context) {
        this(context, System.in);
    }

    public InstructionExecutor(RuntimeContext context, InputStream in) {
        this(context, in, new DefaultFileSystem(), new DefaultLogger(new DefaultFileSystem()));
    }

    public Object executeWithReturn(Instruction instruction) {
        try {
            execute(instruction);
            return null;
        } catch (talk.exception.FunctionReturn fr) {
            return fr.getValue();
        }
    }

    public void execute(Instruction instruction) {
        if (instruction instanceof VariableInstruction) {
            VariableInstruction vi = (VariableInstruction) instruction;
            if (context.hasVariable(vi.getName())) {
                throw new TalkSemanticException("Variable '" + vi.getName() + "' already declared", vi.getLineNumber());
            }
            // Variable declarations should always go in the current scope
            context.setVariable(vi.getName(), vi.getValue());
        } else if (instruction instanceof AssignmentInstruction) {
            AssignmentInstruction ai = (AssignmentInstruction) instruction;
            
            // Resolving behavior for 'set' instruction:
            // 1. If the variable exists in any scope, update it in its own scope (using setVariableScoped)
            // 2. If the variable doesn't exist, create it in the current scope
            
            if (!context.hasVariable(ai.getVariableName())) {
                // Create new variable in the current scope
                context.setVariable(ai.getVariableName(), ai.getValue());
            } else {
                // Update existing variable
                // Set to true to allow modifying parent scope variables from child scopes
                boolean allowOuterScopeModification = true; 
                context.setVariableScoped(ai.getVariableName(), ai.getValue(), allowOuterScopeModification);
            }
        } else if (instruction instanceof IfInstruction) {
            IfInstruction ii = (IfInstruction) instruction;
            Object condResult = resolver.resolve(ii.getCondition());
            boolean cond = condResult instanceof Boolean ? (Boolean) condResult : false;
            List<Instruction> branch = cond ? ii.getThenInstructions() : ii.getElseInstructions();
            
            // Create a new scope for the if/else block
            context.pushScope();
            
            try {
                for (Instruction instr : branch) {
                    execute(instr);
                }
            } finally {
                // Always pop the scope after executing the block
                context.popScope();
            }
        } else if (instruction instanceof WriteInstruction) {
            WriteInstruction wi = (WriteInstruction) instruction;
            Object content = resolver.resolve(wi.getContent());
            String fileName = wi.getFileName();
            
            // Special case for console output
            if ("console".equals(fileName)) {
                // Try writing directly to console using System.err
                System.err.println("CONSOLE OUTPUT: " + String.valueOf(content));
            } else {
                // Write to file
                try {
                    if (fileSystem.fileExists(fileName)) {
                        fileSystem.appendToFile(fileName, String.valueOf(content) + System.lineSeparator());
                    } else {
                        fileSystem.writeFile(fileName, String.valueOf(content) + System.lineSeparator());
                    }
                } catch (IOException e) {
                    try {
                        logger.error("Failed to write to file '" + fileName + "'", wi.getLineNumber());
                    } catch (IOException logEx) {
                        // Ignore logging errors
                    }
                    throw new TalkRuntimeException("Failed to write to file '" + fileName + "'", wi.getLineNumber(), e);
                }
            }
        } else if (instruction instanceof CreateFileInstruction) {
            CreateFileInstruction cfi = (CreateFileInstruction) instruction;
            String fileName = cfi.getFileName();
            
            // Debug: Print current working directory
            System.out.println("[DEBUG] Creating file: " + fileName);
            System.out.println("[DEBUG] Current working directory: " + System.getProperty("user.dir"));
            
            try {
                if (!fileSystem.fileExists(fileName)) {
                    fileSystem.writeFile(fileName, "");
                }
                // No explicit isWritable check; rely on IOException
            } catch (IOException e) {
                try {
                    logger.error("Failed to create file '" + fileName + "'", cfi.getLineNumber());
                } catch (IOException logEx) {
                    // Ignore logging errors
                }
                throw new TalkRuntimeException("Failed to create file '" + fileName + "'", cfi.getLineNumber(), e);
            }
        } else if (instruction instanceof AskInstruction) {
            AskInstruction ai = (AskInstruction) instruction;
            String input;
            boolean valid = false;
            int attempts = 0;
            do {
                System.out.print(ai.getPrompt() + " ");
                input = scanner.nextLine();
                // MVP: Only accept non-empty, numeric input
                valid = input != null && !input.trim().isEmpty();
                // If the variable name is 'num' or similar, require a number
                if (input != null && ai.getVariableName().toLowerCase().contains("num")) {
                    valid = valid && input.matches("-?\\d+");
                }
                if (!valid) {
                    System.out.println("Invalid input. Please try again.");
                }
                attempts++;
            } while (!valid && attempts < 3);
            if (!valid) {
                throw new TalkRuntimeException("Invalid input after 3 attempts");
            }
            // Store as Integer if numeric, else as String
            if (input != null && ai.getVariableName().toLowerCase().contains("num") && input.matches("-?\\d+")) {
                context.setVariable(ai.getVariableName(), Integer.parseInt(input));
            } else {
                context.setVariable(ai.getVariableName(), input);
            }
        } else if (instruction instanceof AttemptInstruction) {
            AttemptInstruction ai = (AttemptInstruction) instruction;
            boolean failed = false;
            try {
                for (Instruction instr : ai.getTryBlock()) {
                    execute(instr);
                }
            } catch (Exception e) {
                failed = true;
            }
            if (failed) {
                for (Instruction instr : ai.getCatchBlock()) {
                    execute(instr);
                }
            }
        } else if (instruction instanceof Parser.BlockInstruction) {
            Parser.BlockInstruction block = (Parser.BlockInstruction) instruction;
            for (Instruction instr : block.getBlock()) {
                execute(instr);
            }
        } else if (instruction instanceof Parser.OtherwiseBlockInstruction) {
            Parser.OtherwiseBlockInstruction block = (Parser.OtherwiseBlockInstruction) instruction;
            for (Instruction instr : block.getBlock()) {
                execute(instr);
            }
        } else if (instruction instanceof RepeatInstruction) {
            RepeatInstruction ri = (RepeatInstruction) instruction;
            if (ri.getItemVar() != null && ri.getListVar() != null) {
                // List iteration mode
                Object listObj = context.getVariable(ri.getListVar());
                if (!(listObj instanceof ListValue)) {
                    throw new TalkValueException("Variable '" + ri.getListVar() + "' is not a list", ri.getLineNumber());
                }
                ListValue list = (ListValue) listObj;
                for (int i = 0; i < list.size(); i++) {
                    // Create a new scope for each iteration to isolate variables
                    context.pushScope();
                    
                    // Set loop-specific variables in the new scope
                    context.setVariable(ri.getItemVar(), list.get(i + 1)); // 1-based
                    context.setVariable("_index", i);
                    context.setVariable("position", i + 1); // 1-based position
                    
                    for (Instruction instr : ri.getBody()) {
                        // Disallow reassignment of 'position' inside loop
                        if (instr instanceof AssignmentInstruction) {
                            AssignmentInstruction ai = (AssignmentInstruction) instr;
                            if ("position".equals(ai.getVariableName())) {
                                throw new TalkSemanticException("Cannot reassign 'position' inside list iteration", ai.getLineNumber());
                            }
                        }
                        execute(instr);
                    }
                    
                    // Pop the scope at the end of each iteration
                    context.popScope();
                }
                // No need to remove loop variables as they're isolated in their own scopes
            } else {
                int count = 0;
                try {
                    Object resolved = resolver.resolve(ri.getCountExpr());
                    if (resolved instanceof Number) {
                        count = ((Number) resolved).intValue();
                    } else {
                        count = Integer.parseInt(resolved.toString());
                    }
                } catch (Exception e) {
                    throw new TalkValueException("Invalid repeat count", ri.getLineNumber());
                }
                if (count < 0) throw new TalkValueException("Repeat count must be non-negative", ri.getLineNumber());
                
                Object prevIndex = context.getVariable("_index");
                for (int i = 0; i < count; i++) {
                    // Create a new scope for each iteration
                    context.pushScope();
                    
                    // Set the index in the loop's scope
                    context.setVariable("_index", i);
                    
                    // Execute loop body
                    for (Instruction instr : ri.getBody()) {
                        execute(instr);
                    }
                    
                    // Pop the scope at the end of each iteration
                    context.popScope();
                }
                if (prevIndex != null) {
                    context.setVariable("_index", prevIndex);
                } else {
                    // Remove _index if it was not previously set
                    try {
                        java.lang.reflect.Method m = context.getClass().getDeclaredMethod("removeVariable", String.class);
                        m.setAccessible(true);
                        m.invoke(context, "_index");
                    } catch (Exception e) {
                        // fallback: set to null
                        context.setVariable("_index", null);
                    }
                }
            }
        } else if (instruction instanceof FunctionDefinitionInstruction) {
            FunctionDefinitionInstruction fdi = (FunctionDefinitionInstruction) instruction;
            context.registerFunction(fdi.getFunctionName(), fdi);
        } else if (instruction instanceof FunctionCallInstruction) {
            FunctionCallInstruction fci = (FunctionCallInstruction) instruction;
            if (!context.hasFunction(fci.getFunctionName())) {
                throw new TalkSemanticException("Function '" + fci.getFunctionName() + "' is not defined", fci.getLineNumber());
            }
            FunctionDefinitionInstruction def = context.getFunction(fci.getFunctionName());
            List<String> params = def.getParameters();
            List<String> args = fci.getArguments();
            if (params.size() != args.size()) {
                throw new TalkSemanticException("Function '" + fci.getFunctionName() + "' expects " + params.size() + " arguments but got " + args.size(), fci.getLineNumber());
            }
            context.pushScope();
            try {
                for (int i = 0; i < params.size(); i++) {
                    context.setVariable(params.get(i), resolver.resolve(args.get(i)));
                }
                Object returnValue = null;
                try {
                    for (Instruction instr : def.getBody()) {
                        execute(instr);
                    }
                } catch (talk.exception.FunctionReturn fr) {
                    returnValue = fr.getValue();
                }
                // If 'into' is specified, assign return value to variable in caller's scope
                if (fci.getIntoVariable() != null) {
                    context.popScope(); // pop function scope to assign in caller's scope
                    context.setVariable(fci.getIntoVariable(), returnValue);
                    context.pushScope(); // restore function scope for finally
                } else if (returnValue != null) {
                    // If not captured, propagate return for executeWithReturn
                    throw new talk.exception.FunctionReturn(returnValue);
                }
            } finally {
                context.popScope();
            }
        } else if (instruction instanceof ReturnInstruction) {
            ReturnInstruction ri = (ReturnInstruction) instruction;
            Object value = resolver.resolve(ri.getExpression());
            throw new talk.exception.FunctionReturn(value, ri.getLineNumber());
        } else if (instruction instanceof ReadFileInstruction) {
            ReadFileInstruction rfi = (ReadFileInstruction) instruction;
            String fileName = rfi.getFileName();
            String variableName = rfi.getVariableName();
            try {
                String content = fileSystem.readFile(fileName);
                context.setVariable(variableName, content);
            } catch (IOException e) {
                try {
                    logger.error("Failed to read file '" + fileName + "'", rfi.getLineNumber());
                } catch (IOException logEx) {
                    // Ignore logging errors
                }
                throw new TalkRuntimeException("Failed to read file '" + fileName + "'", rfi.getLineNumber(), e);
            }
        } else if (instruction instanceof AppendToFileInstruction) {
            AppendToFileInstruction afi = (AppendToFileInstruction) instruction;
            Object content = resolver.resolve(afi.getText());
            String fileName = afi.getFileName();
            try {
                fileSystem.appendToFile(fileName, String.valueOf(content) + System.lineSeparator());
            } catch (IOException e) {
                try {
                    logger.error("Failed to append to file '" + fileName + "'", afi.getLineNumber());
                } catch (IOException logEx) {
                    // Ignore logging errors
                }
                throw new TalkRuntimeException("Failed to append to file '" + fileName + "'", afi.getLineNumber(), e);
            }
        } else if (instruction instanceof DeleteFileInstruction) {
            DeleteFileInstruction dfi = (DeleteFileInstruction) instruction;
            String fileName = dfi.getFileName();
            try {
                fileSystem.deleteFile(fileName);
            } catch (IOException e) {
                try {
                    logger.error("Failed to delete file '" + fileName + "'", dfi.getLineNumber());
                } catch (IOException logEx) {
                    // Ignore logging errors
                }
                throw new TalkRuntimeException("Failed to delete file '" + fileName + "'", dfi.getLineNumber(), e);
            }
        } else if (instruction instanceof ListDirectoryInstruction) {
            ListDirectoryInstruction ldi = (ListDirectoryInstruction) instruction;
            String directory = ldi.getDirectory();
            String variableName = ldi.getVariableName();
            try {
                String[] files = fileSystem.listDirectory(directory);
                ListValue fileList = new ListValue(java.util.Arrays.asList(files));
                context.setVariable(variableName, fileList);
            } catch (IOException e) {
                try {
                    logger.error("Failed to list directory '" + directory + "'", ldi.getLineNumber());
                } catch (IOException logEx) {
                    // Ignore logging errors
                }
                throw new TalkRuntimeException("Directory '" + directory + "' does not exist or is not a directory", ldi.getLineNumber(), e);
            }
        } else if (instruction instanceof LogInstruction) {
            LogInstruction li = (LogInstruction) instruction;
            try {
                logger.log(li.getMessage(), li.getLineNumber());
            } catch (IOException e) {
                // Even if logging fails, we don't want to crash the program
                System.err.println("Warning: Failed to log message (line " + li.getLineNumber() + "): " + e.getMessage());
            }
        } else if (instruction instanceof CopyFileInstruction) {
            CopyFileInstruction cfi = (CopyFileInstruction) instruction;
            String src = cfi.getSource();
            String dest = cfi.getDestination();
            try {
                fileSystem.copyFile(src, dest);
            } catch (IOException e) {
                try {
                    logger.error("Failed to copy file from '" + src + "' to '" + dest + "'", cfi.getLineNumber());
                } catch (IOException logEx) {
                    // Ignore logging errors
                }
                throw new TalkRuntimeException("Failed to copy file from '" + src + "' to '" + dest + "'", cfi.getLineNumber(), e);
            }
        } else if (instruction instanceof DateExpressionInstruction) {
            DateExpressionInstruction dei = (DateExpressionInstruction) instruction;
            String result;
            
            // Handle different date expressions
            if ("now".equals(dei.getExpression())) {
                result = DateUtil.now();
            } else if ("today".equals(dei.getExpression())) {
                result = DateUtil.today();
            } else {
                throw new RuntimeException("Unknown date expression: " + dei.getExpression() + " (line " + dei.getLineNumber() + ")");
            }
            
            context.setVariable(dei.getVariableName(), result);
        } else if (instruction instanceof FormatDateInstruction) {
            FormatDateInstruction fdi = (FormatDateInstruction) instruction;
            String dateExpr = fdi.getDateExpression();
            String pattern = fdi.getPattern();
            
            // Resolve the date expression if it's a variable
            Object dateValue = resolver.resolve(dateExpr);
            String dateStr = dateValue.toString();
            
            try {
                String formattedDate = DateUtil.formatDate(dateStr, pattern);
                context.setVariable(fdi.getVariableName(), formattedDate);
            } catch (Exception e) {
                throw new RuntimeException("Failed to format date: " + e.getMessage() + " (line " + fdi.getLineNumber() + ")");
            }
        } else if (instruction instanceof AddDaysInstruction) {
            AddDaysInstruction adi = (AddDaysInstruction) instruction;
            String daysStr = adi.getDays();
            String dateExpr = adi.getDateExpression();
            
            try {
                // Handle potential variable expressions in daysStr
                Object daysValue = resolver.resolve(daysStr);
                Object dateValue = resolver.resolve(dateExpr);
                
                String newDate = DateUtil.addDays(daysValue.toString(), dateValue.toString());
                context.setVariable(adi.getVariableName(), newDate);
            } catch (Exception e) {
                throw new RuntimeException("Failed to add days: " + e.getMessage() + " (line " + adi.getLineNumber() + ")");
            }
        } else if (instruction instanceof SubtractDaysInstruction) {
            SubtractDaysInstruction sdi = (SubtractDaysInstruction) instruction;
            String daysStr = sdi.getDays();
            String dateExpr = sdi.getDateExpression();
            
            try {
                // Handle potential variable expressions in daysStr
                Object daysValue = resolver.resolve(daysStr);
                Object dateValue = resolver.resolve(dateExpr);
                
                String newDate = DateUtil.subtractDays(daysValue.toString(), dateValue.toString());
                context.setVariable(sdi.getVariableName(), newDate);
            } catch (Exception e) {
                throw new RuntimeException("Failed to subtract days: " + e.getMessage() + " (line " + sdi.getLineNumber() + ")");
            }
        } else if (instruction instanceof DaysDifferenceInstruction) {
            DaysDifferenceInstruction ddi = (DaysDifferenceInstruction) instruction;
            String date1Expr = ddi.getFirstDateExpression();
            String date2Expr = ddi.getSecondDateExpression();
            
            // Resolve the date expressions if they are variables
            Object date1Value = resolver.resolve(date1Expr);
            Object date2Value = resolver.resolve(date2Expr);
            String date1Str = date1Value.toString();
            String date2Str = date2Value.toString();
            
            try {
                long daysDiff = DateUtil.differenceInDays(date1Str, date2Str);
                context.setVariable(ddi.getVariableName(), daysDiff);
            } catch (Exception e) {
                throw new RuntimeException("Failed to calculate days difference: " + e.getMessage() + " (line " + ddi.getLineNumber() + ")");
            }
        } else if (instruction instanceof DayOfWeekInstruction) {
            DayOfWeekInstruction dowi = (DayOfWeekInstruction) instruction;
            String dateExpr = dowi.getDateExpression();
            
            // Resolve the date expression if it's a variable
            Object dateValue = resolver.resolve(dateExpr);
            String dateStr = dateValue.toString();
            
            try {
                String dayOfWeek = DateUtil.dayOfWeek(dateStr);
                context.setVariable(dowi.getVariableName(), dayOfWeek);
            } catch (Exception e) {
                throw new RuntimeException("Failed to get day of week: " + e.getMessage() + " (line " + dowi.getLineNumber() + ")");
            }
        } else if (instruction instanceof ParseDateInstruction) {
            ParseDateInstruction pdi = (ParseDateInstruction) instruction;
            String dateStr = pdi.getDateString();
            
            try {
                String parsedDate = DateUtil.parseDateToString(dateStr);
                context.setVariable(pdi.getVariableName(), parsedDate);
            } catch (Exception e) {
                throw new RuntimeException("Failed to parse date: " + e.getMessage() + " (line " + pdi.getLineNumber() + ")");
            }
        } else if (instruction instanceof DateBeforeInstruction) {
            DateBeforeInstruction dbi = (DateBeforeInstruction) instruction;
            String date1Expr = dbi.getFirstDateExpression();
            String date2Expr = dbi.getSecondDateExpression();
            
            // Resolve the date expressions if they are variables
            Object date1Value = resolver.resolve(date1Expr);
            Object date2Value = resolver.resolve(date2Expr);
            String date1Str = date1Value.toString();
            String date2Str = date2Value.toString();
            
            try {
                boolean result = DateUtil.isBefore(date1Str, date2Str);
                // Execute the then block if the condition is true
                if (result) {
                    // In a full implementation, we would execute the "then" block here
                    System.out.println("[DEBUG] Date condition is true: " + date1Str + " is before " + date2Str);
                }
            } catch (Exception e) {
                throw new RuntimeException("Failed to compare dates: " + e.getMessage() + " (line " + dbi.getLineNumber() + ")");
            }
        } else if (instruction instanceof DateAfterInstruction) {
            DateAfterInstruction dai = (DateAfterInstruction) instruction;
            String date1Expr = dai.getFirstDateExpression();
            String date2Expr = dai.getSecondDateExpression();
            
            // Resolve the date expressions if they are variables
            Object date1Value = resolver.resolve(date1Expr);
            Object date2Value = resolver.resolve(date2Expr);
            String date1Str = date1Value.toString();
            String date2Str = date2Value.toString();
            
            try {
                boolean result = DateUtil.isAfter(date1Str, date2Str);
                // Execute the then block if the condition is true
                if (result) {
                    // In a full implementation, we would execute the "then" block here
                    System.out.println("[DEBUG] Date condition is true: " + date1Str + " is after " + date2Str);
                }
            } catch (Exception e) {
                throw new RuntimeException("Failed to compare dates: " + e.getMessage() + " (line " + dai.getLineNumber() + ")");
            }
        } else if (instruction instanceof ImportInstruction) {
            ImportInstruction importInst = (ImportInstruction) instruction;
            String filePath = importInst.getFilePath();
            
            try {
                // Check if file exists
                if (!fileSystem.fileExists(filePath)) {
                    // Try adding .talk extension if not already present
                    if (!filePath.endsWith(".talk")) {
                        filePath += ".talk";
                    }
                    
                    if (!fileSystem.fileExists(filePath)) {
                        throw new IOException("Import file not found: " + filePath);
                    }
                }
                
                // Read the file content
                String fileContent = fileSystem.readFile(filePath);
                String[] lines = fileContent.split("\n");
                
                // Convert to List<String>
                java.util.List<String> linesList = new java.util.ArrayList<>();
                for (String line : lines) {
                    linesList.add(line);
                }
                
                // Parse and execute
                talk.core.Tokenizer tokenizer = new talk.core.Tokenizer();
                List<talk.core.Tokenizer.Token> tokens = tokenizer.tokenize(linesList);
                talk.Parser parser = new talk.Parser(tokens);
                List<Instruction> importedInstructions = parser.parse();
                
                // Execute each instruction from the imported file
                for (Instruction instr : importedInstructions) {
                    execute(instr);
                }
                
                System.out.println("[DEBUG] Successfully imported file: " + filePath);
            } catch (IOException e) {
                throw new TalkRuntimeException("Failed to import file '" + filePath + "': " + e.getMessage(), importInst.getLineNumber(), e);
            }
        } else if (instruction instanceof PluginCallInstruction) {
            PluginCallInstruction pci = (PluginCallInstruction) instruction;
            String pluginAlias = pci.getPluginAlias();
            List<String> arguments = pci.getArguments();
            String intoVariable = pci.getIntoVariable();
            
            // Check if the plugin exists
            PluginRegistry registry = PluginRegistry.getInstance();
            if (!registry.hasPlugin(pluginAlias)) {
                throw new TalkRuntimeException("Plugin '" + pluginAlias + "' not found", pci.getLineNumber());
            }
            
            // Convert arguments by resolving any variable references
            Object[] args = new Object[arguments.size()];
            for (int i = 0; i < arguments.size(); i++) {
                args[i] = resolver.resolve(arguments.get(i));
            }
            
            try {
                // Execute the plugin
                Object result = registry.execute(pluginAlias, args);
                
                // Store result if there's an 'into' variable
                if (intoVariable != null) {
                    context.setVariable(intoVariable, result);
                }
            } catch (Exception e) {
                throw new TalkRuntimeException("Error executing plugin '" + pluginAlias + "': " + e.getMessage(), pci.getLineNumber(), e);
            }
        } 
        else if (InstructionExecutorTestExtensions.isTestInstruction(instruction)) {
            // Handle test framework instructions using the extension methods
            InstructionExecutorTestExtensions.executeTestInstruction(this, context, resolver, instruction);
        } else {
            throw new TalkRuntimeException("Instruction type not supported in this phase");
        }
    }
}
