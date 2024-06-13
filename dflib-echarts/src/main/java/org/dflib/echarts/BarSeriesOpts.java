package org.dflib.echarts;

import org.dflib.echarts.render.option.EncodeModel;
import org.dflib.echarts.render.option.SeriesModel;

/**
 * @since 1.0.0-M22
 */
public class BarSeriesOpts extends SeriesOpts<BarSeriesOpts> {

    private boolean stack;

    public BarSeriesOpts() {
        // ECharts defaults
        this.stack = false;
    }

    public BarSeriesOpts stack() {
        this.stack = true;
        return this;
    }

    protected SeriesModel resolve(String name, EncodeModel encodeModel, String seriesLayoutBy, boolean last) {
        return new SeriesModel(
                name,
                ChartType.bar.name(),
                encodeModel,
                label != null ? label.resolve() : null,
                seriesLayoutBy,
                false,
                false,
                stack,
                false,
                yAxisIndex,
                last
        );
    }
}
