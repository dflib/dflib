package org.dflib.echarts;

public class ScatterSeriesOpts extends CartesianSeriesOpts<ScatterSeriesOpts> {

    Label label;
    ScatterItemStyle itemStyle;
    ValOrSeries<Integer> symbolSize;

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
        this.symbolSize = ValOrSeries.ofVal(symbolSize);
        return this;
    }

    /**
     * @deprecated in favor of {@link #symbolSizeData(String)}
     */
    @Deprecated(since = "2.0.0-M5", forRemoval = true)
    public ScatterSeriesOpts symbolSize(String dataColumn) {
        return symbolSizeData(dataColumn);
    }

    /**
     * Will generate graph symbol size using a dynamic value coming from the specified DataFrame column, essentially
     * providing an extra visual dimension.
     *
     * @since 2.0.0
     */
    public ScatterSeriesOpts symbolSizeData(String dataColumn) {
        this.symbolSize = ValOrSeries.ofSeries(dataColumn);
        return this;
    }
}
