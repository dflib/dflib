package org.dflib.echarts;

import org.dflib.echarts.render.option.AxisLabelModel;

/**
 * @since 1.0.0-M21
 */
public class AxisLabel {

    private String formatter;

    public static AxisLabel create() {
        return new AxisLabel(null);
    }

    protected AxisLabel(String formatter) {
        this.formatter = formatter;
    }

    public AxisLabel formatter(String formatter) {
        this.formatter = formatter;
        return this;
    }

    protected AxisLabelModel resolve() {
        return new AxisLabelModel(formatter);
    }
}
