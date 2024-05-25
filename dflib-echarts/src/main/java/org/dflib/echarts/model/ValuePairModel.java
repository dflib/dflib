package org.dflib.echarts.model;

/**
 * An abstract list value model that stores a pair of values and the "last" position indicator.
 *
 * @since 1.0.0-M21
 */
public class ValuePairModel {

    private final Object value1;
    private final Object value2;
    private final boolean last;

    public ValuePairModel(Object value1, Object value2, boolean last) {
        this.value1 = value1;
        this.value2 = value2;
        this.last = last;
    }

    public Object getValue1() {
        return value1;
    }

    public Object getValue2() {
        return value2;
    }

    public Object getQuotedValue1() {
        return ValueModel.shouldQuote(value1) ? "'" + value1 + "'" : value1;
    }

    public Object getQuotedValue2() {
        return ValueModel.shouldQuote(value2) ? "'" + value2 + "'" : value2;
    }

    public boolean isLast() {
        return last;
    }
}
