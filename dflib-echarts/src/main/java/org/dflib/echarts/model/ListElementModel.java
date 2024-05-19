package org.dflib.echarts.model;

/**
 * An abstract list element model that stores a value and the "last" position indicator.
 *
 * @since 1.0.0-M21
 */
public class ListElementModel {

    private final Object value;
    private final boolean last;

    public ListElementModel(Object value, boolean last) {
        this.value = value;
        this.last = last;
    }

    public Object getValue() {
        return value;
    }

    public boolean isLast() {
        return last;
    }
}
