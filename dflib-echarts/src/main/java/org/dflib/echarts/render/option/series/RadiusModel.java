package org.dflib.echarts.render.option.series;

import org.dflib.echarts.render.option.Distance;

public class RadiusModel {

    private final Distance[] radius;

    public RadiusModel(Distance[] radius) {
        this.radius = radius;
    }

    public String getRadius() {
        switch (radius.length) {
            case 0:
                return null;
            case 1:
                return radius[0].asString();
            default:
                return '[' + radius[0].asString() + ',' + radius[1].asString() + ']';
        }
    }
}
