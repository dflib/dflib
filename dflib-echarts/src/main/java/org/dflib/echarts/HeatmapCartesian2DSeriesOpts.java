package org.dflib.echarts;

/**
 * @since 2.0.0
 */
public class HeatmapCartesian2DSeriesOpts extends CartesianSeriesOpts<HeatmapCartesian2DSeriesOpts> {

    @Override
    public ChartType getType() {
        return ChartType.heatmap;
    }
}
