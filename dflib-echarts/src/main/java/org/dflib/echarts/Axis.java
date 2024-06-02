package org.dflib.echarts;

import org.dflib.echarts.render.option.axis.AxisModel;

import java.util.Objects;

/**
 * @since 1.0.0-M21
 */
public abstract class Axis<T extends Axis<T>> {

    protected String name;
    protected Integer offset;
    protected AxisType type;
    protected AxisLabel label;
    protected AxisLine line;
    protected boolean boundaryGap;
    protected Boolean alignTicks;

    /**
     * @deprecated in favor of {@link XAxis#ofDefault()}
     */
    @Deprecated(since = "1.0.0-M22", forRemoval = true)
    public static XAxis defaultX() {
        return XAxis.ofDefault();
    }

    /**
     * @deprecated in favor of {@link YAxis#ofDefault()}
     */
    @Deprecated(since = "1.0.0-M22", forRemoval = true)
    public static YAxis defaultY() {
        return YAxis.ofDefault();
    }

    protected Axis(AxisType type) {
        this.type = Objects.requireNonNull(type);
        // "true" is the same default as ECharts
        this.boundaryGap = true;
    }

    public T boundaryGap(boolean gap) {
        this.boundaryGap = gap;
        return (T) this;
    }

    /**
     * @since 1.0.0-M22
     */
    public T alignTicks(boolean alignTicks) {
        this.alignTicks = alignTicks;
        return (T) this;
    }

    /**
     * Sets the "axisLabel" property of the Axis object.
     */
    public T label(AxisLabel label) {
        this.label = label;
        return (T) this;
    }

    /**
     * Sets the "axisLine" property of the Axis object.
     */
    public T line(AxisLine line) {
        this.line = line;
        return (T) this;
    }

    public T name(String name) {
        this.name = name;
        return (T) this;
    }

    public T offset(int offset) {
        this.offset = offset;
        return (T) this;
    }

    protected abstract AxisModel resolve();
}
