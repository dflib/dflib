package org.dflib.echarts;

import org.dflib.echarts.render.option.EncodeModel;
import org.dflib.echarts.render.option.SeriesModel;

/**
 * @since 1.0.0-M22
 */
public class LineSeriesOpts extends SeriesOpts<LineSeriesOpts> {

    private boolean areaStyle;
    private boolean showSymbol;
    private boolean smooth;
    protected boolean stack;

    public LineSeriesOpts() {
        // ECharts defaults
        this.areaStyle = false;
        this.showSymbol = true;
        this.smooth = false;
        this.stack = false;
    }

    public LineSeriesOpts smooth() {
        this.smooth = true;
        return this;
    }

    public LineSeriesOpts areaStyle() {
        this.areaStyle = true;
        return this;
    }

    /**
     * @since 1.0.0-M22
     */
    public LineSeriesOpts showSymbol(boolean showSymbol) {
        this.showSymbol = showSymbol;
        return this;
    }

    public LineSeriesOpts stack() {
        this.stack = true;
        return this;
    }

    @Override
    protected SeriesModel resolve(String name, EncodeModel encodeModel, String seriesLayoutBy, boolean last) {
        return new SeriesModel(
                name,
                ChartType.line.name(),
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
