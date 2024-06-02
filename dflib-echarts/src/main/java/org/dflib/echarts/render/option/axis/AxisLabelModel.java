package org.dflib.echarts.render.option.axis;

/**
 * @since 1.0.0-M21
 */
public class AxisLabelModel {

    private final String formatter;

    public AxisLabelModel(String formatter) {
        this.formatter = formatter;
    }

    public String getFormatter() {
        return formatter;
    }
}
