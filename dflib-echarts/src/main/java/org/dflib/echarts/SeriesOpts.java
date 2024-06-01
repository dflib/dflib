package org.dflib.echarts;

import org.dflib.echarts.render.option.EncodeModel;
import org.dflib.echarts.render.option.SeriesModel;

import java.util.Objects;

/**
 * A partial configuration of a single data series options. The full configuration is obtained
 *
 * @since 1.0.0-M21
 */
public class SeriesOpts {

    private final ChartType type;

    private boolean areaStyle;
    private boolean showSymbol;
    private boolean smooth;
    private boolean stack;

    /**
     * Starts a builder for a line series options object.
     */
    public static SeriesOpts line() {
        return new SeriesOpts(ChartType.line);
    }

    /**
     * Starts a builder for a bar series options object.
     */
    public static SeriesOpts bar() {
        return new SeriesOpts(ChartType.bar);
    }

    /**
     * Starts a builder for a scatter series options object.
     */
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
                seriesLayoutBy,
                areaStyle,
                showSymbol,
                stack,
                smooth,
                last
        );
    }
}
