package org.dflib.echarts;

import org.dflib.echarts.render.option.EncodeModel;
import org.dflib.echarts.render.option.SeriesModel;

/**
 * @since 1.0.0-M22
 */
public class BarSeriesOpts extends CartesianSeriesOpts<BarSeriesOpts> {

    private boolean stack;

    public BarSeriesOpts() {
        // ECharts defaults
        this.stack = false;
    }

    public BarSeriesOpts stack() {
        this.stack = true;
        return this;
    }

    @Override
    protected SeriesModel resolve(String name, int labelsPos, int seriesPos, String seriesLayoutBy) {
        return new SeriesModel(
                name,
                ChartType.bar.name(),
                new EncodeModel(labelsPos, seriesPos, null, null),
                label != null ? label.resolve() : null,
                seriesLayoutBy,
                null,
                null,
                null,
                null,
                yAxisIndex,
                null,
                null,
                null
        );
    }
}
