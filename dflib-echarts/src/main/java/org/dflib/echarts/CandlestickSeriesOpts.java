package org.dflib.echarts;

/**
 * @since 1.0.0-M24
 */
public class CandlestickSeriesOpts extends CartesianSeriesOpts<CandlestickSeriesOpts> {

    @Override
    public ChartType getType() {
        return ChartType.candlestick;
    }
}
