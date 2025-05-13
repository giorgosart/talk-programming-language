package talk;

import java.util.List;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Scanner;
import talk.LogInstruction;
import talk.CopyFileInstruction;

public class InstructionExecutor {
    private final RuntimeContext context;
    private final ExpressionResolver resolver;
    private static final Scanner scanner = new java.util.Scanner(System.in);

    // Custom exception to signal early return from a function
    public static class FunctionReturn extends RuntimeException {
        private final Object value;
        public FunctionReturn(Object value) { this.value = value; }
        public Object getValue() { return value; }
    }

    public InstructionExecutor(RuntimeContext context) {
        this.context = context;
        this.resolver = new ExpressionResolver(context);
    }

    public Object executeWithReturn(Instruction instruction) {
        try {
            execute(instruction);
            return null;
        } catch (FunctionReturn fr) {
            return fr.getValue();
        }
    }

    public void execute(Instruction instruction) {
        if (instruction instanceof VariableInstruction) {
            VariableInstruction vi = (VariableInstruction) instruction;
            if (context.hasVariable(vi.getName())) {
                throw new RuntimeException("Variable '" + vi.getName() + "' already declared (line " + vi.getLineNumber() + ")");
            }
            context.setVariable(vi.getName(), vi.getValue());
        } else if (instruction instanceof AssignmentInstruction) {
            AssignmentInstruction ai = (AssignmentInstruction) instruction;
            // Only auto-declare in function/local scope (not global)
            boolean isLocalScope = context.isLocalScope();
            if (!context.hasVariable(ai.getVariableName())) {
                if (isLocalScope) {
                    context.setVariable(ai.getVariableName(), ai.getValue());
                } else {
                    throw new RuntimeException("Variable '" + ai.getVariableName() + "' not declared (line " + ai.getLineNumber() + ")");
                }
            } else {
                context.setVariable(ai.getVariableName(), ai.getValue());
            }
        } else if (instruction instanceof IfInstruction) {
            IfInstruction ii = (IfInstruction) instruction;
            Object condResult = resolver.resolve(ii.getCondition());
            boolean cond = condResult instanceof Boolean ? (Boolean) condResult : false;
            List<Instruction> branch = cond ? ii.getThenInstructions() : ii.getElseInstructions();
            for (Instruction instr : branch) {
                execute(instr);
            }
        } else if (instruction instanceof WriteInstruction) {
            WriteInstruction wi = (WriteInstruction) instruction;
            Object content = resolver.resolve(wi.getContent());
            String fileName = wi.getFileName();
            File file = new File(fileName);
            try {
                if (!file.exists()) {
                    file.createNewFile();
                }
                try (FileWriter writer = new FileWriter(file, true)) {
                    writer.write(String.valueOf(content));
                    writer.write(System.lineSeparator());
                }
            } catch (IOException e) {
                throw new RuntimeException("Failed to write to file '" + fileName + "' (line " + wi.getLineNumber() + ")", e);
            }
        } else if (instruction instanceof CreateFileInstruction) {
            CreateFileInstruction cfi = (CreateFileInstruction) instruction;
            String fileName = cfi.getFileName();
            Path path = Paths.get(fileName);
            try {
                if (!Files.exists(path)) {
                    Files.createFile(path);
                }
                if (!Files.isWritable(path)) {
                    throw new RuntimeException("File '" + fileName + "' is not writable (line " + cfi.getLineNumber() + ")");
                }
            } catch (Exception e) {
                throw new RuntimeException("Failed to create file '" + fileName + "' (line " + cfi.getLineNumber() + ")", e);
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
                throw new RuntimeException("Invalid input after 3 attempts");
            }
            // Store as Integer if numeric, else as String
            if (input != null && ai.getVariableName().toLowerCase().contains("num") && input.matches("-?\\d+")) {
                context.setVariable(ai.getVariableName(), Integer.parseInt(input));
                System.out.println("[DEBUG] Stored variable '" + ai.getVariableName() + "' as Integer: " + input);
            } else {
                context.setVariable(ai.getVariableName(), input);
                System.out.println("[DEBUG] Stored variable '" + ai.getVariableName() + "' as String: " + input);
            }
        } else if (instruction instanceof AttemptInstruction) {
            AttemptInstruction ai = (AttemptInstruction) instruction;
            try {
                for (Instruction instr : ai.getTryBlock()) {
                    execute(instr);
                }
            } catch (Exception e) {
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
                    throw new RuntimeException("Variable '" + ri.getListVar() + "' is not a list (line " + ri.getLineNumber() + ")");
                }
                ListValue list = (ListValue) listObj;
                for (int i = 0; i < list.size(); i++) {
                    context.setVariable(ri.getItemVar(), list.get(i + 1)); // 1-based
                    context.setVariable("_index", i);
                    context.setVariable("position", i + 1); // 1-based position
                    for (Instruction instr : ri.getBody()) {
                        // Disallow reassignment of 'position' inside loop
                        if (instr instanceof AssignmentInstruction) {
                            AssignmentInstruction ai = (AssignmentInstruction) instr;
                            if ("position".equals(ai.getVariableName())) {
                                throw new RuntimeException("Cannot reassign 'position' inside list iteration (line " + ai.getLineNumber() + ")");
                            }
                        }
                        execute(instr);
                    }
                }
                // Remove loop variable, _index, and position after loop
                try {
                    java.lang.reflect.Method m = context.getClass().getDeclaredMethod("removeVariable", String.class);
                    m.setAccessible(true);
                    m.invoke(context, ri.getItemVar());
                    m.invoke(context, "_index");
                    m.invoke(context, "position");
                } catch (Exception e) {
                    context.setVariable(ri.getItemVar(), null);
                    context.setVariable("_index", null);
                    context.setVariable("position", null);
                }
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
                    throw new RuntimeException("Invalid repeat count at line " + ri.getLineNumber());
                }
                if (count < 0) throw new RuntimeException("Repeat count must be non-negative (line " + ri.getLineNumber() + ")");
                Object prevIndex = context.getVariable("_index");
                for (int i = 0; i < count; i++) {
                    context.setVariable("_index", i);
                    for (Instruction instr : ri.getBody()) {
                        execute(instr);
                    }
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
                throw new RuntimeException("Function '" + fci.getFunctionName() + "' is not defined (line " + fci.getLineNumber() + ")");
            }
            FunctionDefinitionInstruction def = context.getFunction(fci.getFunctionName());
            List<String> params = def.getParameters();
            List<String> args = fci.getArguments();
            if (params.size() != args.size()) {
                throw new RuntimeException("Function '" + fci.getFunctionName() + "' expects " + params.size() + " arguments but got " + args.size() + " (line " + fci.getLineNumber() + ")");
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
                } catch (FunctionReturn fr) {
                    returnValue = fr.getValue();
                }
                // If 'into' is specified, assign return value to variable in caller's scope
                if (fci.getIntoVariable() != null) {
                    context.popScope(); // pop function scope to assign in caller's scope
                    context.setVariable(fci.getIntoVariable(), returnValue);
                    context.pushScope(); // restore function scope for finally
                } else if (returnValue != null) {
                    // If not captured, propagate return for executeWithReturn
                    throw new FunctionReturn(returnValue);
                }
            } finally {
                context.popScope();
            }
        } else if (instruction instanceof ReturnInstruction) {
            ReturnInstruction ri = (ReturnInstruction) instruction;
            Object value = resolver.resolve(ri.getExpression());
            throw new FunctionReturn(value);
        } else if (instruction instanceof ReadFileInstruction) {
            ReadFileInstruction rfi = (ReadFileInstruction) instruction;
            String fileName = rfi.getFileName();
            String variableName = rfi.getVariableName();
            try {
                String content = Files.readString(Paths.get(fileName));
                context.setVariable(variableName, content);
            } catch (IOException e) {
                throw new RuntimeException("Failed to read file '" + fileName + "' (line " + rfi.getLineNumber() + ")", e);
            }
        } else if (instruction instanceof AppendToFileInstruction) {
            AppendToFileInstruction afi = (AppendToFileInstruction) instruction;
            Object content = resolver.resolve(afi.getText());
            String fileName = afi.getFileName();
            File file = new File(fileName);
            System.out.println("[DEBUG] Appending to file: " + file.getAbsolutePath());
            try {
                if (!file.exists()) {
                    file.createNewFile();
                }
                try (FileWriter writer = new FileWriter(file, true)) {
                    writer.write(String.valueOf(content));
                    writer.write(System.lineSeparator());
                }
            } catch (IOException e) {
                throw new RuntimeException("Failed to append to file '" + fileName + "' (line " + afi.getLineNumber() + ")", e);
            }
        } else if (instruction instanceof DeleteFileInstruction) {
            DeleteFileInstruction dfi = (DeleteFileInstruction) instruction;
            String fileName = dfi.getFileName();
            File file = new File(fileName);
            if (file.exists()) {
                if (!file.delete()) {
                    throw new RuntimeException("Failed to delete file '" + fileName + "' (line " + dfi.getLineNumber() + ")");
                }
            } else {
                throw new RuntimeException("File '" + fileName + "' does not exist (line " + dfi.getLineNumber() + ")");
            }
        } else if (instruction instanceof ListDirectoryInstruction) {
            ListDirectoryInstruction ldi = (ListDirectoryInstruction) instruction;
            String directory = ldi.getDirectory();
            String variableName = ldi.getVariableName();
            File dir = new File(directory);
            if (!dir.exists() || !dir.isDirectory()) {
                throw new RuntimeException("Directory '" + directory + "' does not exist or is not a directory (line " + ldi.getLineNumber() + ")");
            }
            String[] files = dir.list();
            if (files == null) files = new String[0];
            ListValue fileList = new ListValue(java.util.Arrays.asList(files));
            context.setVariable(variableName, fileList);
        } else if (instruction instanceof LogInstruction) {
            LogInstruction li = (LogInstruction) instruction;
            String message = li.getMessage();
            // Write to default log file (append mode)
            String logFile = "debug.log";
            try (FileWriter writer = new FileWriter(logFile, true)) {
                writer.write(message);
                writer.write(System.lineSeparator());
            } catch (IOException e) {
                throw new RuntimeException("Failed to write to log file '" + logFile + "' (line " + li.getLineNumber() + ")", e);
            }
        } else if (instruction instanceof CopyFileInstruction) {
            CopyFileInstruction cfi = (CopyFileInstruction) instruction;
            java.nio.file.Path src = java.nio.file.Paths.get(cfi.getSource());
            java.nio.file.Path dest = java.nio.file.Paths.get(cfi.getDestination());
            try {
                java.nio.file.Files.copy(src, dest, java.nio.file.StandardCopyOption.REPLACE_EXISTING);
            } catch (java.io.IOException e) {
                throw new RuntimeException("Failed to copy file from '" + cfi.getSource() + "' to '" + cfi.getDestination() + "' (line " + cfi.getLineNumber() + ")");
            }
        } else {
            throw new UnsupportedOperationException("Instruction type not supported in this phase");
        }
    }
}
