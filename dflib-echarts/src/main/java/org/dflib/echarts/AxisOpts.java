package org.dflib.echarts;

import java.util.Objects;

/**
 * @since 1.0.0-M21
 */
public class AxisOpts {

    private final AxisType type;
    private AxisLabelOpts label;
    private boolean boundaryGap;

    public static AxisOpts defaultX() {
        return of(AxisType.category);
    }

    public static AxisOpts defaultY() {
        return of(AxisType.value);
    }

    public static AxisOpts of(AxisType type) {
        return new AxisOpts(type);
    }

    protected AxisOpts(AxisType type) {
        this.type = Objects.requireNonNull(type);
        // "true" is the same default as ECharts
        this.boundaryGap = true;
    }

    public boolean isBoundaryGap() {
        return boundaryGap;
    }

    public AxisOpts boundaryGap(boolean gap) {
        this.boundaryGap = gap;
        return this;
    }

    public AxisLabelOpts getLabel() {
        return label;
    }

    public AxisOpts label(AxisLabelOpts axisLabel) {
        this.label = axisLabel;
        return this;
    }

    public AxisType getType() {
        return type;
    }
}
