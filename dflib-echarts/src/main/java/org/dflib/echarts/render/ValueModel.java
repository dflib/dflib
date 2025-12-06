package org.dflib.echarts.render;

import org.dflib.echarts.render.util.Renderer;

/**
 * A wrapper for a collection item that stores a value and the "last" position indicator.
 */
public record ValueModel<T>(T value, boolean last) {

    public Object getQuotedValue() {
        return Renderer.quotedValue(value);
    }
}
