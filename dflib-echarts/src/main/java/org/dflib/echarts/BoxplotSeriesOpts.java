package org.dflib.echarts;

public class BoxplotSeriesOpts extends CartesianSeriesOpts<BoxplotSeriesOpts> {

    @Override
    public ChartType getType() {
        return ChartType.boxplot;
    }
}
