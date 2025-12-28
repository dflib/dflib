package org.dflib.echarts;

public class LineSeriesOpts extends SeriesOpts<LineSeriesOpts> implements
        SeriesOptsCoordsCartesian2D,
        SeriesOptsNamedItems,
        SeriesOptsItemStyleColor,
        SeriesOptsItemSymbolSize {

    Integer xAxisIndex;
    Integer yAxisIndex;
    ColumnLinkedLabel label;
    Boolean areaStyle;
    Symbol symbol;
    Boolean showSymbol;
    ValOrSeries<Integer> symbolSize;
    Boolean smooth;
    Boolean stack;
    LineItemStyle itemStyle;
    LineStyle lineStyle;

    @Override
    public ChartType getType() {
        return ChartType.line;
    }

    @Override
    public CoordinateSystemType getCoordinateSystemType() {
        return CoordinateSystemType.cartesian2d;
    }

    @Override
    public Integer getXAxisIndex() {
        return xAxisIndex;
    }

    @Override
    public Integer getYAxisIndex() {
        return yAxisIndex;
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
     * Sets an index of X axis to use for this Series. There can be one or more X axes, so this method allows to
     * pick one. If not set, 0 is assumed.
     */
    public LineSeriesOpts xAxisIndex(int index) {
        this.xAxisIndex = index;
        return this;
    }

    /**
     * Sets an index of Y axis to use for this Series. There can be one or more Y axes, so this method allows to
     * pick one. If not set, 0 is assumed.
     */
    public LineSeriesOpts yAxisIndex(int index) {
        this.yAxisIndex = index;
        return this;
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
     * Sets line chart symbol. The default is {@link Symbol#emptyCircle}.
     *
     * @since 2.0.0
     */
    public LineSeriesOpts symbol(Symbol symbol) {
        this.symbol = symbol;
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
        this.symbolSize = ValOrSeries.ofVal(symbolSize);
        return this;
    }

    /**
     * @deprecated in favor of {@link #symbolSizeData(String)}
     */
    @Deprecated(since = "2.0.0-M5", forRemoval = true)
    public LineSeriesOpts symbolSize(String dataColumn) {
        return symbolSizeData(dataColumn);
    }

    /**
     * Will generate graph symbol size using a dynamic value coming from the specified DataFrame column, essentially
     * providing an extra visual dimension.
     *
     * @since 2.0.0
     */
    public LineSeriesOpts symbolSizeData(String dataColumn) {
        this.symbolSize = ValOrSeries.ofSeries(dataColumn);
        return this;
    }

    public LineSeriesOpts stack() {
        this.stack = true;
        return this;
    }

    /**
     * @deprecated redundant, use {@link #label(Label)} instead.
     */
    @Deprecated(since = "2.0.0", forRemoval = true)
    public LineSeriesOpts label(LabelPosition position) {
        return label(Label.of(position));
    }

    public LineSeriesOpts label(Label label) {
        this.label = new ColumnLinkedLabel(null, label);
        return this;
    }

    /**
     * @since 2.0.0
     */
    public LineSeriesOpts label(String labelColumn) {
        return label(labelColumn, null);
    }

    /**
     * @since 2.0.0
     */
    public LineSeriesOpts label(String labelColumn, Label label) {
        this.label = new ColumnLinkedLabel(labelColumn, label);
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
