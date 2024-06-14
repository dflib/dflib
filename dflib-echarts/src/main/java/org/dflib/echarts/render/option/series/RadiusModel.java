package org.dflib.echarts.render.option.series;

import org.dflib.echarts.render.util.Renderer;

public class RadiusModel {

    private final Object[] radius;

    public RadiusModel(Object[] radius) {
        this.radius = radius;
    }

    public String getRadius() {
        switch (radius.length) {
            case 0:
                return null;
            case 1:
                return Renderer.quotedValue(radius[0]);
            default:
                return '[' + Renderer.quotedValue(radius[0]) + ',' + Renderer.quotedValue(radius[1]) + ']';
        }
    }
}
