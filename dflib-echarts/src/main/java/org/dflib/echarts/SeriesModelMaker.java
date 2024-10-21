package org.dflib.echarts;

import org.dflib.echarts.render.ValueModels;
import org.dflib.echarts.render.option.EncodeModel;
import org.dflib.echarts.render.option.SeriesModel;
import org.dflib.echarts.render.option.series.CenterModel;
import org.dflib.echarts.render.option.series.RadiusModel;

class SeriesModelMaker {

    SeriesModel resolve(SeriesBuilder<?> sb) {

        switch (sb.seriesOpts.getType()) {
            case line:
                return lineModel((SeriesBuilder<LineSeriesOpts>) sb);
            case bar:
                return barModel((SeriesBuilder<BarSeriesOpts>) sb);
            case scatter:
                return scatterModel((SeriesBuilder<ScatterSeriesOpts>) sb);
            case candlestick:
                return candlestickModel((SeriesBuilder<CandlestickSeriesOpts>) sb);
            case boxplot:
                return boxplotModel((SeriesBuilder<BoxplotSeriesOpts>) sb);
            case pie:
                return pieModel((SeriesBuilder<PieSeriesOpts>) sb);
            default:
                throw new UnsupportedOperationException("Unexpected ChartType: " + this);
        }
    }

    private SeriesModel barModel(SeriesBuilder<BarSeriesOpts> sb) {
        return new SeriesModel(
                sb.name,
                sb.seriesOpts.getType().name(),
                new EncodeModel(sb.xDimension, ValueModels.of(sb.yDimensions), null, null),
                sb.seriesOpts.label != null ? sb.seriesOpts.label.resolve() : null,
                sb.datasetSeriesLayoutBy,
                null,
                null,
                sb.seriesOpts.stack,
                null,
                sb.seriesOpts.xAxisIndex,
                sb.seriesOpts.yAxisIndex,
                null,
                null,
                null,
                null,
                null,
                sb.seriesOpts.itemStyle != null ? sb.seriesOpts.itemStyle.resolve() : null
        );
    }

    private SeriesModel lineModel(SeriesBuilder<LineSeriesOpts> sb) {
        return new SeriesModel(
                sb.name,
                sb.seriesOpts.getType().name(),
                new EncodeModel(sb.xDimension, ValueModels.of(sb.yDimensions), null, null),
                sb.seriesOpts.label != null ? sb.seriesOpts.label.resolve() : null,
                sb.datasetSeriesLayoutBy,
                sb.seriesOpts.areaStyle,
                sb.seriesOpts.showSymbol,
                sb.seriesOpts.stack,
                sb.seriesOpts.smooth,
                sb.seriesOpts.xAxisIndex,
                sb.seriesOpts.yAxisIndex,
                null,
                null,
                null,
                null,
                null,
                null);
    }


    private SeriesModel scatterModel(SeriesBuilder<ScatterSeriesOpts> sb) {
        return new SeriesModel(
                sb.name,
                sb.seriesOpts.getType().name(),
                new EncodeModel(sb.xDimension, ValueModels.of(sb.yDimensions), null, null),
                sb.seriesOpts.label != null ? sb.seriesOpts.label.resolve() : null,
                sb.datasetSeriesLayoutBy,
                null,
                null,
                null,
                null,
                sb.seriesOpts.xAxisIndex,
                sb.seriesOpts.yAxisIndex,
                null,
                null,
                null,
                null,
                null,
                sb.seriesOpts.itemStyle != null ? sb.seriesOpts.itemStyle.resolve() : null
        );
    }

    private SeriesModel candlestickModel(SeriesBuilder<CandlestickSeriesOpts> sb) {
        return new SeriesModel(
                sb.name,
                sb.seriesOpts.getType().name(),
                new EncodeModel(sb.xDimension, ValueModels.of(sb.yDimensions), null, null),
                null,
                sb.datasetSeriesLayoutBy,
                null,
                null,
                null,
                null,
                sb.seriesOpts.xAxisIndex,
                sb.seriesOpts.yAxisIndex,
                null,
                null,
                null,
                null,
                null,
                sb.seriesOpts.itemStyle != null ? sb.seriesOpts.itemStyle.resolve() : null
        );
    }

    private SeriesModel boxplotModel(SeriesBuilder<BoxplotSeriesOpts> sb) {
        return new SeriesModel(
                sb.name,
                sb.seriesOpts.getType().name(),
                new EncodeModel(sb.xDimension, ValueModels.of(sb.yDimensions), null, null),
                null,
                sb.datasetSeriesLayoutBy,
                null,
                null,
                null,
                null,
                sb.seriesOpts.xAxisIndex,
                sb.seriesOpts.yAxisIndex,
                null,
                null,
                null,
                null,
                null,
                null
        );
    }

    private SeriesModel pieModel(SeriesBuilder<PieSeriesOpts> sb) {

        // TODO: multiple series in a pie chart?
        Integer dataDim = sb.yDimensions != null && !sb.yDimensions.isEmpty() ? sb.yDimensions.get(0) : null;

        return new SeriesModel(
                sb.name,
                sb.seriesOpts.getType().name(),

                new EncodeModel(null, null, sb.pieLabelsDimension, dataDim),

                sb.seriesOpts.label != null && sb.seriesOpts.label.label != null ? sb.seriesOpts.label.label.resolve() : null,
                sb.datasetSeriesLayoutBy,
                null,
                null,
                null,
                null,
                null,
                null,
                sb.seriesOpts.radius != null ? new RadiusModel(sb.seriesOpts.radius) : null,
                sb.seriesOpts.center != null ? new CenterModel(sb.seriesOpts.center[0], sb.seriesOpts.center[1]) : null,
                sb.seriesOpts.startAngle,
                sb.seriesOpts.endAngle,
                sb.seriesOpts.roseType != null ? sb.seriesOpts.roseType.name() : null,
                null
        );
    }
}
