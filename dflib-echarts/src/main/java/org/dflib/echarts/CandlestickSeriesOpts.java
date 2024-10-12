package org.dflib.echarts;

public class CandlestickSeriesOpts extends CartesianSeriesOpts<CandlestickSeriesOpts> {

    @Override
    public ChartType getType() {
        return ChartType.candlestick;
    }
}
