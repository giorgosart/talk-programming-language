package talk;

import java.util.regex.Pattern;

public class ExpressionResolver {
    private final RuntimeContext context;

    public ExpressionResolver(RuntimeContext context) {
        this.context = context;
    }

    public Object resolve(String expr) {
        expr = expr.trim();
        // Try to resolve as integer
        if (expr.matches("-?\\d+")) {
            return Integer.parseInt(expr);
        }
        // Try to resolve as variable
        if (context.hasVariable(expr)) {
            return context.getVariable(expr);
        }
        // Try to resolve as arithmetic: x + 1, 2 * y, etc. (MVP: only single op)
        String[] ops = {"+", "-", "*", "/"};
        for (String op : ops) {
            int idx = expr.indexOf(op);
            if (idx > 0) {
                Object left = resolve(expr.substring(0, idx));
                Object right = resolve(expr.substring(idx + 1));
                if (left instanceof Integer && right instanceof Integer) {
                    switch (op) {
                        case "+": return (Integer) left + (Integer) right;
                        case "-": return (Integer) left - (Integer) right;
                        case "*": return (Integer) left * (Integer) right;
                        case "/": return (Integer) left / (Integer) right;
                    }
                }
            }
        }
        // Try to resolve as string literal
        if (expr.startsWith("\"") && expr.endsWith("\"")) {
            return expr.substring(1, expr.length() - 1);
        }
        // Try to resolve as boolean comparison: x is greater than 10
        if (expr.contains("is greater than")) {
            String[] parts = expr.split("is greater than");
            Object left = resolve(parts[0]);
            Object right = resolve(parts[1]);
            return toInt(left) > toInt(right);
        }
        if (expr.contains("is smaller than")) {
            String[] parts = expr.split("is smaller than");
            Object left = resolve(parts[0]);
            Object right = resolve(parts[1]);
            return toInt(left) < toInt(right);
        }
        if (expr.contains("is equal to")) {
            String[] parts = expr.split("is equal to");
            Object left = resolve(parts[0]);
            Object right = resolve(parts[1]);
            return left.equals(right);
        }
        if (expr.contains("is not equal to")) {
            String[] parts = expr.split("is not equal to");
            Object left = resolve(parts[0]);
            Object right = resolve(parts[1]);
            return !left.equals(right);
        }
        // Fallback: return as string
        return expr;
    }

    private int toInt(Object o) {
        if (o instanceof Integer) return (Integer) o;
        if (o instanceof String && ((String) o).matches("-?\\d+")) return Integer.parseInt((String) o);
        throw new RuntimeException("Cannot convert to int: " + o);
    }
}
