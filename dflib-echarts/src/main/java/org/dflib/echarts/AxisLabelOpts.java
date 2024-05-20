package org.dflib.echarts;

/**
 * @since 1.0.0-M21
 */
public class AxisLabelOpts {

    private String formatter;

    public static AxisLabelOpts create() {
        return new AxisLabelOpts(null);
    }

    protected AxisLabelOpts(String formatter) {
        this.formatter = formatter;
    }

    public String getFormatter() {
        return formatter;
    }

    public AxisLabelOpts formatter(String formatter) {
        this.formatter = formatter;
        return this;
    }
}
