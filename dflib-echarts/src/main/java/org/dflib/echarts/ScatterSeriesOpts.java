package org.dflib.echarts;

public class ScatterSeriesOpts extends CartesianSeriesOpts<ScatterSeriesOpts> {

    Label label;
    ScatterItemStyle itemStyle;
    SymbolSize symbolSize;

    @Override
    public ChartType getType() {
        return ChartType.scatter;
    }

    public ScatterSeriesOpts label(LabelPosition position) {
        this.label = Label.of(position);
        return this;
    }

    public ScatterSeriesOpts label(Label label) {
        this.label = label;
        return this;
    }

    /**
     * @since 1.1.0
     */
    public ScatterSeriesOpts itemStyle(ScatterItemStyle itemStyle) {
        this.itemStyle = itemStyle;
        return this;
    }

    /**
     * @since 1.1.0
     */
    public ScatterSeriesOpts symbolSize(int symbolSize) {
        this.symbolSize = SymbolSize.of(symbolSize);
        return this;
    }

    /**
     * Will generate graph symbol size using a dynamic value coming from the specified DataFrame column, essentially
     * providing an extra visual dimension.
     *
     * @since 2.0.0
     */
    public ScatterSeriesOpts symbolSize(String dataColumn) {
        this.symbolSize = SymbolSize.ofDataColumn(dataColumn);
        return this;
    }
}
