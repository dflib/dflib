package org.dflib.echarts.render.option.series;

import org.dflib.echarts.render.option.Distance;

public class CenterModel {

    private final Distance horizontal;
    private final Distance vertical;

    public CenterModel(Distance horizontal, Distance vertical) {
        this.horizontal = horizontal;
        this.vertical = vertical;
    }

    public String getCenter() {
        return '[' + horizontal.asString() + ',' + vertical.asString() + ']';
    }
}
