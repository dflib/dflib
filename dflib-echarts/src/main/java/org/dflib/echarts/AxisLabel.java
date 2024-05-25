package org.dflib.echarts;

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

    public String getFormatter() {
        return formatter;
    }

    public AxisLabel formatter(String formatter) {
        this.formatter = formatter;
        return this;
    }
}
