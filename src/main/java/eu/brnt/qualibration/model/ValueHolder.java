package eu.brnt.qualibration.model;

public class ValueHolder<T> {

    private T value;

    public ValueHolder() {
        this(null);
    }

    public ValueHolder(T value) {
        this.value = value;
    }

    public T getValue() {
        return value;
    }

    public void setValue(T value) {
        this.value = value;
    }
}
