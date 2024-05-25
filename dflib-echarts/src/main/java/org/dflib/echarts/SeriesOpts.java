package org.dflib.echarts;

import java.util.Objects;

/**
 * Configuration of a single data series of an {@link EChart}.
 *
 * @since 1.0.0-M21
 */
public class SeriesOpts {

    private final ChartType type;

    // using objects instead of primitives to be able to perform merge
    // TODO: areaStyle and smooth are not present in the Bar chart... subclass opts?
    private Boolean areaStyle;
    private Boolean smooth;
    private Boolean stack;

    /**
     * Starts a builder for a line series options object.
     */
    public static SeriesOpts line() {
        return new SeriesOpts(ChartType.line, null, null, null);
    }

    /**
     * Starts a builder for a bar series options object.
     */
    public static SeriesOpts bar() {
        return new SeriesOpts(ChartType.bar, null, null, null);
    }

    /**
     * Starts a builder for a scatter series options object.
     */
    public static SeriesOpts scatter() {
        return new SeriesOpts(ChartType.scatter, null, null, null);
    }

    protected SeriesOpts(
            ChartType type,
            Boolean areaStyle,
            Boolean smooth,
            Boolean stack) {
        this.type = Objects.requireNonNull(type);
        this.stack = stack;
        this.smooth = smooth;
        this.areaStyle = areaStyle;
    }

    public SeriesOpts merge(SeriesOpts other) {
        return new SeriesOpts(
                other.type,
                other.areaStyle != null ? other.areaStyle : this.areaStyle,
                other.smooth != null ? other.smooth : this.smooth,
                other.stack != null ? other.stack : this.stack
        );
    }

    public ChartType getType() {
        return type;
    }

    public boolean isAreaStyle() {
        return areaStyle != null ? areaStyle : false;
    }

    public boolean isSmooth() {
        return smooth != null ? smooth : false;
    }

    public boolean isStack() {
        return stack != null ? stack : false;
    }

    public SeriesOpts areaStyle() {
        this.areaStyle = Boolean.TRUE;
        return this;
    }

    public SeriesOpts smooth() {
        this.smooth = Boolean.TRUE;
        return this;
    }

    public SeriesOpts stack() {
        this.stack = Boolean.TRUE;
        return this;
    }
}
