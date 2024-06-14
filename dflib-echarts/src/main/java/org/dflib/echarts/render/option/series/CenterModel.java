package org.dflib.echarts.render.option.series;

import org.dflib.echarts.render.util.Renderer;

public class CenterModel {

    private final Object horizontal;
    private final Object vertical;

    public CenterModel(Object horizontal, Object vertical) {
        this.horizontal = horizontal;
        this.vertical = vertical;
    }

    public String getCenter() {
        return '[' + Renderer.quotedValue(horizontal) + ',' + Renderer.quotedValue(vertical) + ']';
    }
}
