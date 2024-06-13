package org.dflib.echarts;

import org.dflib.echarts.render.option.EncodeModel;
import org.dflib.echarts.render.option.SeriesModel;

/**
 * @since 1.0.0-M22
 */
public class ScatterSeriesOpts extends SeriesOpts<ScatterSeriesOpts> {

    protected SeriesModel resolve(String name, EncodeModel encodeModel, String seriesLayoutBy, boolean last) {
        return new SeriesModel(
                name,
                ChartType.scatter.name(),
                encodeModel,
                label != null ? label.resolve() : null,
                seriesLayoutBy,
                false,
                false,
                false,
                false,
                yAxisIndex,
                last
        );
    }
}
