package talk;

public class ListAccessExpression {
    private final String listName;
    private final int index;

    public ListAccessExpression(String listName, int index) {
        this.listName = listName;
        this.index = index;
    }

    public String getListName() {
        return listName;
    }

    public int getIndex() {
        return index;
    }
}
