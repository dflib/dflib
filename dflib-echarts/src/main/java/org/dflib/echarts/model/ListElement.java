package org.dflib.echarts.model;

/**
 * @since 1.0.0-M21
 */
public class ListElement<T> {

    private final T value;
    private final boolean last;

    public ListElement(T value, boolean last) {
        this.value = value;
        this.last = last;
    }

    public T getValue() {
        return value;
    }

    public boolean isLast() {
        return last;
    }
}
