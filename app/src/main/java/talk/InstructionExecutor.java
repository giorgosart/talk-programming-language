package talk;

import java.util.List;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Scanner;

public class InstructionExecutor {
    private final RuntimeContext context;
    private final ExpressionResolver resolver;
    private static final Scanner scanner = new java.util.Scanner(System.in);

    public InstructionExecutor(RuntimeContext context) {
        this.context = context;
        this.resolver = new ExpressionResolver(context);
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
            if (!context.hasVariable(ai.getVariableName())) {
                throw new RuntimeException("Variable '" + ai.getVariableName() + "' not declared (line " + ai.getLineNumber() + ")");
            }
            context.setVariable(ai.getVariableName(), ai.getValue());
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
            context.setVariable(ai.getVariableName(), input);
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
        } else {
            throw new UnsupportedOperationException("Instruction type not supported in this phase");
        }
    }
}
