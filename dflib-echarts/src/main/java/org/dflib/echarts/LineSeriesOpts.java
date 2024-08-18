package org.dflib.echarts;

/**
 * @since 1.0.0-M22
 */
public class LineSeriesOpts extends CartesianSeriesOpts<LineSeriesOpts> {

    Boolean areaStyle;
    Boolean showSymbol;
    Boolean smooth;
    Boolean stack;

    @Override
    public ChartType getType() {
        return ChartType.line;
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
}
