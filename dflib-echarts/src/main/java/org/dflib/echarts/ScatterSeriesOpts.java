package org.dflib.echarts;

public abstract class ScatterSeriesOpts<SO extends ScatterSeriesOpts<SO>> extends SeriesOpts<SO> implements
        SeriesOptsNamedItems,
        SeriesOptsItemStyleColor,
        SeriesOptsItemSymbolSize {

    ColumnLinkedLabel label;
    ScatterItemStyle itemStyle;
    Symbol symbol;
    ValOrSeries<Integer> symbolSize;

    @Override
    public ChartType getType() {
        return ChartType.scatter;
    }

    @Override
    public String getItemNameSeries() {
        return label != null ? label.columnName : null;
    }

    @Override
    public String getItemStyleColorSeries() {
        return itemStyle != null && itemStyle.color != null && itemStyle.color.isSeries() ? itemStyle.color.seriesName : null;
    }

    @Override
    public String getItemSymbolSizeSeries() {
        return symbolSize != null && symbolSize.isSeries() ? symbolSize.seriesName : null;
    }

    /**
     * @deprecated redundant, use {@link #label(Label)} instead.
     */
    @Deprecated(since = "2.0.0", forRemoval = true)
    public SO label(LabelPosition position) {
        return label(Label.of(position));
    }

    /**
     * @since 2.0.0
     */
    public SO label(String labelColumn) {
        return label(labelColumn, null);
    }

    /**
     * @since 2.0.0
     */
    public SO label(String labelColumn, Label label) {
        this.label = new ColumnLinkedLabel(labelColumn, label);
        return (SO) this;
    }

    public SO label(Label label) {
        this.label = new ColumnLinkedLabel(null, label);
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
     * Sets an alternative chart symbol.
     *
     * @since 2.0.0
     */
    public SO symbol(Symbol symbol) {
        this.symbol = symbol;
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
