package org.dflib.echarts;

import org.dflib.echarts.render.option.EncodeModel;
import org.dflib.echarts.render.option.SeriesModel;

/**
 * @since 1.0.0-M22
 */
public class ScatterSeriesOpts extends CartesianSeriesOpts<ScatterSeriesOpts> {

    @Override
    protected SeriesModel resolve(String name, int labelsPos, int seriesPos, String seriesLayoutBy) {
        return new SeriesModel(
                name,
                ChartType.scatter.name(),
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
                null,
                null
        );
    }
}
