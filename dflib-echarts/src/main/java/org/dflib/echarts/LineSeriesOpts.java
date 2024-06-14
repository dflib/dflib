package org.dflib.echarts;

import org.dflib.echarts.render.option.EncodeModel;
import org.dflib.echarts.render.option.SeriesModel;

/**
 * @since 1.0.0-M22
 */
public class LineSeriesOpts extends CartesianSeriesOpts<LineSeriesOpts> {

    private Boolean areaStyle;
    private Boolean showSymbol;
    private Boolean smooth;
    private Boolean stack;

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
    protected SeriesModel resolve(String name, int labelsPos, int seriesPos, String seriesLayoutBy) {
        return new SeriesModel(
                name,
                ChartType.line.name(),
                new EncodeModel(labelsPos, seriesPos, null, null),
                label != null ? label.resolve() : null,
                seriesLayoutBy,
                areaStyle,
                showSymbol,
                stack,
                smooth,
                yAxisIndex,
                null,
                null,
                null
        );
    }
}
