package org.dflib.echarts;

/**
 * @since 1.0.0-M22
 */
public class ScatterSeriesOpts extends CartesianSeriesOpts<ScatterSeriesOpts> {

    @Override
    public ChartType getType() {
        return ChartType.scatter;
    }
}
