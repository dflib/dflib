package org.dflib.echarts;

public class LineSeriesOpts extends CartesianSeriesOpts<LineSeriesOpts> {

    Label label;
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

    public LineSeriesOpts showSymbol(boolean showSymbol) {
        this.showSymbol = showSymbol;
        return this;
    }

    public LineSeriesOpts stack() {
        this.stack = true;
        return this;
    }

    public LineSeriesOpts label(LabelPosition position) {
        this.label = Label.of(position);
        return this;
    }

    public LineSeriesOpts label(Label label) {
        this.label = label;
        return this;
    }

}
