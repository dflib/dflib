package org.dflib.echarts;

import org.dflib.echarts.render.option.axis.AxisLabelModel;

/**
 * @since 1.0.0-M21
 */
public class AxisLabel {

    private String formatter;

    /**
     * @since 1.0.0-M22
     */
    public static AxisLabel of() {
        return new AxisLabel(null);
    }

    /**
     * @deprecated in favor of {@link #of()}
     */
    @Deprecated(since = "1.0.0-M22", forRemoval = true)
    public static AxisLabel create() {
        return of();
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
