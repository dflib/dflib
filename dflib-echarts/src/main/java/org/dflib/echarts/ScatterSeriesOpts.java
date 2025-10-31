package org.dflib.echarts;

public abstract class ScatterSeriesOpts<SO extends ScatterSeriesOpts<SO>> extends SeriesOpts<SO> {


    Label label;
    ScatterItemStyle itemStyle;
    ValOrSeries<Integer> symbolSize;

    @Override
    public ChartType getType() {
        return ChartType.scatter;
    }

    public SO label(LabelPosition position) {
        this.label = Label.of(position);
        return (SO) this;
    }

    public SO label(Label label) {
        this.label = label;
        return (SO) this;
    }

    /**
     * @since 1.1.0
     */
    public SO itemStyle(ScatterItemStyle itemStyle) {
        this.itemStyle = itemStyle;
        return (SO) this;
    }

    /**
     * @since 1.1.0
     */
    public SO symbolSize(int symbolSize) {
        this.symbolSize = ValOrSeries.ofVal(symbolSize);
        return (SO) this;
    }

    /**
     * @deprecated in favor of {@link #symbolSizeData(String)}
     */
    @Deprecated(since = "2.0.0-M5", forRemoval = true)
    public SO symbolSize(String dataColumn) {
        return symbolSizeData(dataColumn);
    }

    /**
     * Will generate graph symbol size using a dynamic value coming from the specified DataFrame column, essentially
     * providing an extra visual dimension.
     *
     * @since 2.0.0
     */
    public SO symbolSizeData(String dataColumn) {
        this.symbolSize = ValOrSeries.ofSeries(dataColumn);
        return (SO) this;
    }
}
