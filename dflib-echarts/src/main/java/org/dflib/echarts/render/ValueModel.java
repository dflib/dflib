package org.dflib.echarts.render;

import org.dflib.echarts.render.util.Renderer;

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
        return Renderer.quotedValue(value);
    }

    public boolean isLast() {
        return last;
    }



}
