package talk.expression;

import java.util.List;
import java.util.Objects;
import talk.exception.*;

public class ListValue /* implements Value */ {
    private final List<String> items;

    public ListValue(List<String> items) {
        this.items = List.copyOf(items);
    }

    public String get(int index) {
        if (index < 1 || index > items.size()) {
            throw new TalkValueException("List index out of bounds: " + index);
        }
        return items.get(index - 1); // 1-based indexing
    }

    public int size() {
        return items.size();
    }

    public boolean includes(String value) {
        return items.contains(value);
    }

    public List<String> getItems() {
        return items;
    }

    /**
     * Returns the underlying list of items (for test and integration use).
     */
    public List<String> asList() {
        return items;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ListValue listValue = (ListValue) o;
        return items.equals(listValue.items);
    }

    @Override
    public int hashCode() {
        return Objects.hash(items);
    }

    @Override
    public String toString() {
        return items.toString();
    }
}
