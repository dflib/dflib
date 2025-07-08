package org.dflib.echarts;

public class LineSeriesOpts extends CartesianSeriesOpts<LineSeriesOpts> {

    Label label;
    Boolean areaStyle;
    Boolean showSymbol;
    SymbolSize symbolSize;
    Boolean smooth;
    Boolean stack;
    LineItemStyle itemStyle;
    LineStyle lineStyle;

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

    /**
     * @since 1.1.0
     */
    public LineSeriesOpts symbolSize(int symbolSize) {
        this.symbolSize = SymbolSize.of(symbolSize);
        return this;
    }

    /**
     * Will generate graph symbol size using a dynamic value coming from the specified DataFrame column, essentially
     * providing an extra visual dimension.
     *
     * @since 2.0.0
     */
    public LineSeriesOpts symbolSize(String dataColumn) {
        this.symbolSize = SymbolSize.ofDataColumn(dataColumn);
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

    /**
     * @since 1.1.0
     */
    public LineSeriesOpts itemStyle(LineItemStyle itemStyle) {
        this.itemStyle = itemStyle;
        return this;
    }

    /**
     * @since 1.1.0
     */
    public LineSeriesOpts lineStyle(LineStyle lineStyle) {
        this.lineStyle = lineStyle;
        return this;
    }
}
