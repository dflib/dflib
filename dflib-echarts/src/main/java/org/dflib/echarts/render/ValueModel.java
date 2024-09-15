package org.dflib.echarts.render;

import org.dflib.echarts.render.util.Renderer;

/**
 * A wrapper for a collection item that stores a value and the "last" position indicator.
 *
 * @since 1.0.0-M21
 */
public class ValueModel<T> {

    private final T value;
    private final boolean last;

    public ValueModel(T value, boolean last) {
        this.value = value;
        this.last = last;
    }

    public T getValue() {
        return value;
    }

    public Object getQuotedValue() {
        return Renderer.quotedValue(value);
    }

    public boolean isLast() {
        return last;
    }
}
