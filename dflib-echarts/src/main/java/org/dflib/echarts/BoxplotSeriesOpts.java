package org.dflib.echarts;

/**
 * @since 1.0.0-RC1
 */
public class BoxplotSeriesOpts extends CartesianSeriesOpts<BoxplotSeriesOpts> {

    @Override
    public ChartType getType() {
        return ChartType.boxplot;
    }
}
