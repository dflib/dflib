package org.dflib.echarts;

import org.dflib.echarts.render.option.EncodeModel;
import org.dflib.echarts.render.option.SeriesModel;

import java.util.Objects;

/**
 * A configuration of a single data series.
 *
 * @since 1.0.0-M21
 */
public class SeriesOpts {

    private final ChartType type;

    private Label label;
    private boolean areaStyle;
    private boolean showSymbol;
    private boolean smooth;
    private Integer yAxisIndex;
    private boolean stack;

    /**
     * @since 1.0.0-M22
     */
    public static SeriesOpts of(ChartType type) {
        return new SeriesOpts(type);
    }

    /**
     * Starts a builder for a line series options object.
     *
     * @since 1.0.0-M22
     */
    public static SeriesOpts ofLine() {
        return new SeriesOpts(ChartType.line);
    }

    /**
     * @deprecated in favor of {@link #ofLine()}
     */
    @Deprecated(since = "1.0.0-M22", forRemoval = true)
    public static SeriesOpts line() {
        return new SeriesOpts(ChartType.line);
    }

    /**
     * Starts a builder for a bar series options object.
     *
     * @since 1.0.0-M22
     */
    public static SeriesOpts ofBar() {
        return new SeriesOpts(ChartType.bar);
    }

    /**
     * @deprecated in favor of {@link #ofBar()}
     */
    @Deprecated(since = "1.0.0-M22", forRemoval = true)
    public static SeriesOpts bar() {
        return new SeriesOpts(ChartType.bar);
    }

    /**
     * Starts a builder for a scatter series options object.
     *
     * @since 1.0.0-M22
     */
    public static SeriesOpts ofScatter() {
        return new SeriesOpts(ChartType.scatter);
    }

    /**
     * @deprecated in favor of {@link #ofScatter()}
     */
    @Deprecated(since = "1.0.0-M22", forRemoval = true)
    public static SeriesOpts scatter() {
        return new SeriesOpts(ChartType.scatter);
    }

    protected SeriesOpts(ChartType type) {
        this.type = Objects.requireNonNull(type);

        // set ECharts defaults
        this.areaStyle = false;
        this.showSymbol = true;
        this.smooth = false;
        this.stack = false;
    }

    /**
     * Sets an index of Y axis to use for this Series. There can be one or more Y axes, so this method allows to
     * pick one. If not set, 0 is assumed.
     *
     * @since 1.0.0-M22
     */
    public SeriesOpts yAxisIndex(int index) {
        this.yAxisIndex = index;
        return this;
    }

    /**
     * @since 1.0.0-M22
     */
    public SeriesOpts label(LabelPosition position) {
        this.label = Label.of(position);
        return this;
    }

    /**
     * @since 1.0.0-M22
     */
    public SeriesOpts label(Label label) {
        this.label = label;
        return this;
    }

    public SeriesOpts areaStyle() {
        this.areaStyle = true;
        return this;
    }

    public SeriesOpts smooth() {
        this.smooth = true;
        return this;
    }

    public SeriesOpts stack() {
        this.stack = true;
        return this;
    }

    /**
     * @since 1.0.0-M22
     */
    public SeriesOpts showSymbol(boolean showSymbol) {
        this.showSymbol = showSymbol;
        return this;
    }

    protected SeriesModel resolve(String name, EncodeModel encodeModel, String seriesLayoutBy, boolean last) {
        return new SeriesModel(
                name,
                type.name(),
                encodeModel,
                label != null ? label.resolve() : null,
                seriesLayoutBy,
                areaStyle,
                showSymbol,
                stack,
                smooth,
                yAxisIndex,
                last
        );
    }
}
