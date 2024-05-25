package org.dflib.echarts;

import java.util.Objects;

/**
 * @since 1.0.0-M21
 */
public class Axis {

    private final AxisType type;
    private AxisLabel label;
    private boolean boundaryGap;

    public static Axis defaultX() {
        return of(AxisType.category);
    }

    public static Axis defaultY() {
        return of(AxisType.value);
    }

    public static Axis time() {
        return of(AxisType.time);
    }

    public static Axis value() {
        return of(AxisType.value);
    }

    public static Axis category() {
        return of(AxisType.category);
    }

    protected static Axis of(AxisType type) {
        return new Axis(type);
    }

    protected Axis(AxisType type) {
        this.type = Objects.requireNonNull(type);
        // "true" is the same default as ECharts
        this.boundaryGap = true;
    }

    public boolean isBoundaryGap() {
        return boundaryGap;
    }

    public Axis boundaryGap(boolean gap) {
        this.boundaryGap = gap;
        return this;
    }

    public AxisLabel getLabel() {
        return label;
    }

    public Axis label(AxisLabel axisLabel) {
        this.label = axisLabel;
        return this;
    }

    public AxisType getType() {
        return type;
    }
}
