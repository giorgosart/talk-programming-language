package talk;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public class InstructionFactory {
    private final Map<String, Function<InstructionContext, Instruction>> registry = new HashMap<>();

    public InstructionFactory() {
        // Register built-in instructions
        register("variable", ctx -> new VariableInstruction(ctx.getIdentifier(), ctx.getValue(), ctx.getLine()));
        register("set", ctx -> new AssignmentInstruction(ctx.getIdentifier(), ctx.getValue(), ctx.getLine()));
        register("write", ctx -> new WriteInstruction((String)ctx.getValue(), ctx.getIdentifier(), ctx.getLine()));
        register("ask", ctx -> new AskInstruction((String)ctx.getValue(), ctx.getIdentifier(), ctx.getLine()));
        register("create", ctx -> new CreateFileInstruction(ctx.getIdentifier(), ctx.getLine()));
        register("append", ctx -> new AppendToFileInstruction((String)ctx.getValue(), ctx.getIdentifier(), ctx.getLine()));
        register("delete", ctx -> new DeleteFileInstruction(ctx.getIdentifier(), ctx.getLine()));
        register("log", ctx -> new LogInstruction((String)ctx.getValue(), ctx.getLine()));
        // Add more as needed
    }

    public void register(String keyword, Function<InstructionContext, Instruction> creator) {
        registry.put(keyword, creator);
    }

    public boolean isRegistered(String keyword) {
        return registry.containsKey(keyword);
    }

    public Instruction create(String keyword, InstructionContext ctx) {
        Function<InstructionContext, Instruction> creator = registry.get(keyword);
        if (creator == null) throw new RuntimeException("Unknown instruction keyword: " + keyword);
        return creator.apply(ctx);
    }
}
