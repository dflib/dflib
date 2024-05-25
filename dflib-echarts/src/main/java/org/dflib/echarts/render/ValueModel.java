package org.dflib.echarts.render;

/**
 * An abstract list value model that stores a value and the "last" position indicator.
 *
 * @since 1.0.0-M21
 */
public class ValueModel {

    private final Object value;
    private final boolean last;

    public ValueModel(Object value, boolean last) {
        this.value = value;
        this.last = last;
    }

    public Object getValue() {
        return value;
    }

    public Object getQuotedValue() {
        return shouldQuote(value) ? "'" + value + "'" : value;
    }

    public boolean isLast() {
        return last;
    }

    static boolean shouldQuote(Object o) {
        return !(o instanceof Number) && !(o instanceof Boolean);
    }
}
