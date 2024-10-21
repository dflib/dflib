package org.dflib.echarts;

public class CandlestickSeriesOpts extends CartesianSeriesOpts<CandlestickSeriesOpts> {

    CandlestickItemStyle itemStyle;

    @Override
    public ChartType getType() {
        return ChartType.candlestick;
    }

    /**
     * @since 1.1.0
     */
    public CandlestickSeriesOpts itemStyle(CandlestickItemStyle itemStyle) {
        this.itemStyle = itemStyle;
        return this;
    }
}
