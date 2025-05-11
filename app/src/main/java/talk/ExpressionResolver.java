package talk;

public class ExpressionResolver {
    private final RuntimeContext context;

    public ExpressionResolver(RuntimeContext context) {
        this.context = context;
    }

    // --- LOGICAL NODE CLASSES ---
    interface LogicNode {
        Object eval();
    }
    class ValueNode implements LogicNode {
        private final String expr;
        ValueNode(String expr) { this.expr = expr; }
        public Object eval() { return resolve(expr, false); }
    }
    class AndNode implements LogicNode {
        private final LogicNode left, right;
        AndNode(LogicNode left, LogicNode right) { this.left = left; this.right = right; }
        public Object eval() {
            Object l = left.eval();
            if (l instanceof Boolean && !(Boolean)l) return false; // short-circuit
            return (Boolean)l && (Boolean)right.eval();
        }
    }
    class OrNode implements LogicNode {
        private final LogicNode left, right;
        OrNode(LogicNode left, LogicNode right) { this.left = left; this.right = right; }
        public Object eval() {
            Object l = left.eval();
            if (l instanceof Boolean && (Boolean)l) return true; // short-circuit
            return (Boolean)l || (Boolean)right.eval();
        }
    }
    class NotNode implements LogicNode {
        private final LogicNode node;
        NotNode(LogicNode node) { this.node = node; }
        public Object eval() {
            Object v = node.eval();
            return !(Boolean)v;
        }
    }

    // --- LOGICAL PARSER ---
    public Object resolve(String expr, boolean allowLogic) {
        expr = expr.trim();
        if (allowLogic) {
            LogicNode logicTree = parseLogic(expr);
            if (logicTree != null) return logicTree.eval();
        }
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
                Object left = resolve(expr.substring(0, idx), true);
                Object right = resolve(expr.substring(idx + 1), true);
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
            Object left = resolve(parts[0], true);
            Object right = resolve(parts[1], true);
            return toInt(left) > toInt(right);
        }
        if (expr.contains("is smaller than")) {
            String[] parts = expr.split("is smaller than");
            Object left = resolve(parts[0], true);
            Object right = resolve(parts[1], true);
            return toInt(left) < toInt(right);
        }
        if (expr.contains("is equal to")) {
            String[] parts = expr.split("is equal to");
            Object left = resolve(parts[0], true);
            Object right = resolve(parts[1], true);
            return left.equals(right);
        }
        if (expr.contains("is not equal to")) {
            String[] parts = expr.split("is not equal to");
            Object left = resolve(parts[0], true);
            Object right = resolve(parts[1], true);
            return !left.equals(right);
        }
        // Fallback: return as string
        return expr;
    }

    public Object resolve(String expr) {
        return resolve(expr, true);
    }

    private int toInt(Object o) {
        if (o instanceof Integer) return (Integer) o;
        if (o instanceof String && ((String) o).matches("-?\\d+")) return Integer.parseInt((String) o);
        throw new RuntimeException("Cannot convert to int: " + o);
    }

    private LogicNode parseLogic(String expr) {
        String exprU = expr.trim().toUpperCase();
        // Parse 'or' (lowest precedence)
        int orIdx = findTopLevel(exprU, " OR ");
        if (orIdx > 0) {
            String left = expr.substring(0, orIdx).trim();
            String right = expr.substring(orIdx + 4).trim();
            LogicNode leftNode = parseLogic(left);
            if (leftNode == null) leftNode = new ValueNode(left);
            LogicNode rightNode = parseLogic(right);
            if (rightNode == null) rightNode = new ValueNode(right);
            return new OrNode(leftNode, rightNode);
        }
        // Parse 'and' (next precedence)
        int andIdx = findTopLevel(exprU, " AND ");
        if (andIdx > 0) {
            String left = expr.substring(0, andIdx).trim();
            String right = expr.substring(andIdx + 5).trim();
            LogicNode leftNode = parseLogic(left);
            if (leftNode == null) leftNode = new ValueNode(left);
            LogicNode rightNode = parseLogic(right);
            if (rightNode == null) rightNode = new ValueNode(right);
            return new AndNode(leftNode, rightNode);
        }
        // Parse 'not' (highest precedence, prefix)
        if (exprU.startsWith("NOT ")) {
            String sub = expr.substring(4).trim();
            LogicNode subNode = parseLogic(sub);
            if (subNode == null) subNode = new ValueNode(sub);
            return new NotNode(subNode);
        }
        // Only treat as logic if the expression is a single logical value or variable
        if (exprU.equals("TRUE") || exprU.equals("FALSE")) return new ValueNode(expr);
        if (context.hasVariable(expr.trim()) && context.getVariable(expr.trim()) instanceof Boolean) {
            return new ValueNode(expr.trim());
        }
        // Otherwise, not a logic expression
        return null;
    }

    private int findTopLevel(String expr, String op) {
        // Find op not inside quotes (MVP: no parentheses)
        boolean inQuote = false;
        for (int i = 0; i <= expr.length() - op.length(); i++) {
            char c = expr.charAt(i);
            if (c == '"') inQuote = !inQuote;
            if (!inQuote && expr.startsWith(op, i)) {
                return i;
            }
        }
        return -1;
    }
}
