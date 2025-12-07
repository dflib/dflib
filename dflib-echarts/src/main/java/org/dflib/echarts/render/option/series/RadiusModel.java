package org.dflib.echarts.render.option.series;

import org.dflib.echarts.render.option.Distance;

public class RadiusModel {

    private final Distance[] radius;

    public RadiusModel(Distance[] radius) {
        this.radius = radius;
    }

    public String getRadius() {
        return switch (radius.length) {
            case 0 -> null;
            case 1 -> radius[0].asString();
            default -> '[' + radius[0].asString() + ',' + radius[1].asString() + ']';
        };
    }
}
