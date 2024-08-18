package org.dflib.echarts;

import org.dflib.echarts.render.option.EncodeModel;
import org.dflib.echarts.render.option.SeriesModel;
import org.dflib.echarts.render.option.series.CenterModel;
import org.dflib.echarts.render.option.series.RadiusModel;

import java.util.Map;

/**
 * Resolves a sequence of {@link SeriesOpts} to {@link SeriesModel} that references data in the dataset.
 */
class OpToSeriesModel {

    private final Map<Integer, Integer> xAxisIndices;
    private final String dataSetSeriesLayoutBy;
    private int dataSetSeriesPos;

    OpToSeriesModel(
            Map<Integer, Integer> xAxisIndices,
            String dataSetSeriesLayoutBy,
            int dataSetSeriesPosOffset) {

        this.xAxisIndices = xAxisIndices;
        this.dataSetSeriesLayoutBy = dataSetSeriesLayoutBy;
        this.dataSetSeriesPos = dataSetSeriesPosOffset;
    }

    SeriesModel resolve(SeriesOpts<?> opts, String name) {

        switch (opts.getType()) {
            case line:
                return lineModel((LineSeriesOpts) opts, name);
            case bar:
                return barModel((BarSeriesOpts) opts, name);
            case scatter:
                return scatterModel((ScatterSeriesOpts) opts, name);
            case pie:
                return pieModel((PieSeriesOpts) opts, name);
            default:
                throw new UnsupportedOperationException("Unexpected ChartType: " + this);
        }
    }

    private int getLabelPos(Integer xAxisIndex) {
        return xAxisIndex != null ? xAxisIndices.get(xAxisIndex) : 0;
    }

    private SeriesModel barModel(BarSeriesOpts o, String name) {
        return new SeriesModel(
                name,
                o.getType().name(),
                new EncodeModel(getLabelPos(o.xAxisIndex), dataSetSeriesPos++, null, null),
                o.label != null ? o.label.resolve() : null,
                dataSetSeriesLayoutBy,
                null,
                null,
                o.stack,
                null,
                o.xAxisIndex,
                o.yAxisIndex,
                null,
                null,
                null,
                null,
                null
        );
    }

    private SeriesModel lineModel(LineSeriesOpts o, String name) {
        return new SeriesModel(
                name,
                o.getType().name(),
                new EncodeModel(getLabelPos(o.xAxisIndex), dataSetSeriesPos++, null, null),
                o.label != null ? o.label.resolve() : null,
                dataSetSeriesLayoutBy,
                o.areaStyle,
                o.showSymbol,
                o.stack,
                o.smooth,
                o.xAxisIndex,
                o.yAxisIndex,
                null,
                null,
                null,
                null,
                null);
    }


    private SeriesModel scatterModel(ScatterSeriesOpts o, String name) {
        return new SeriesModel(
                name,
                ChartType.scatter.name(),
                new EncodeModel(getLabelPos(o.xAxisIndex), dataSetSeriesPos++, null, null),
                o.label != null ? o.label.resolve() : null,
                dataSetSeriesLayoutBy,
                null,
                null,
                null,
                null,
                o.xAxisIndex,
                o.yAxisIndex,
                null,
                null,
                null,
                null,
                null
        );
    }


    private SeriesModel pieModel(PieSeriesOpts o, String name) {
        return new SeriesModel(
                name,
                ChartType.pie.name(),

                // TODO: implement PieChart label to column resolution (other than 0 would not work)
                new EncodeModel(null, null, 0, dataSetSeriesPos++),

                o.label != null && o.label.label != null ? o.label.label.resolve() : null,
                dataSetSeriesLayoutBy,
                null,
                null,
                null,
                null,
                null,
                null,
                o.radius != null ? new RadiusModel(o.radius) : null,
                o.center != null ? new CenterModel(o.center[0], o.center[1]) : null,
                o.startAngle,
                o.endAngle,
                o.roseType != null ? o.roseType.name() : null
        );
    }
}
