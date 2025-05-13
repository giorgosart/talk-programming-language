package talk;

import java.util.List;

public class RepeatInstruction implements Instruction {
    private final String countExpr;
    private final List<Instruction> body;
    private final int lineNumber;
    private final String itemVar;
    private final String listVar;

    // Standard repeat N times
    public RepeatInstruction(String countExpr, List<Instruction> body, int lineNumber) {
        this.countExpr = countExpr;
        this.body = body;
        this.lineNumber = lineNumber;
        this.itemVar = null;
        this.listVar = null;
    }

    // List iteration: repeat for each item in items
    public RepeatInstruction(String itemVar, String listVar, List<Instruction> body, int lineNumber) {
        this.countExpr = null;
        this.body = body;
        this.lineNumber = lineNumber;
        this.itemVar = itemVar;
        this.listVar = listVar;
    }

    public String getCountExpr() { return countExpr; }
    public List<Instruction> getBody() { return body; }
    public String getItemVar() { return itemVar; }
    public String getListVar() { return listVar; }
    @Override
    public int getLineNumber() { return lineNumber; }
}
