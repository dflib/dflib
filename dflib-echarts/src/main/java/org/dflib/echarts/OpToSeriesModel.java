package org.dflib.echarts;

import org.dflib.echarts.render.option.EncodeModel;
import org.dflib.echarts.render.option.SeriesModel;
import org.dflib.echarts.render.option.series.CenterModel;
import org.dflib.echarts.render.option.series.RadiusModel;

class OpToSeriesModel {

    SeriesModel resolve(OptionSeriesBuilder<?> sb) {

        switch (sb.opts.getType()) {
            case line:
                return lineModel((OptionSeriesBuilder<LineSeriesOpts>) sb);
            case bar:
                return barModel((OptionSeriesBuilder<BarSeriesOpts>) sb);
            case scatter:
                return scatterModel((OptionSeriesBuilder<ScatterSeriesOpts>) sb);
            case pie:
                return pieModel((OptionSeriesBuilder<PieSeriesOpts>) sb);
            default:
                throw new UnsupportedOperationException("Unexpected ChartType: " + this);
        }
    }

    private SeriesModel barModel(OptionSeriesBuilder<BarSeriesOpts> sb) {
        return new SeriesModel(
                sb.name,
                sb.opts.getType().name(),
                new EncodeModel(sb.xDimension, sb.yDimensions, null, null),
                sb.opts.label != null ? sb.opts.label.resolve() : null,
                sb.datasetSeriesLayoutBy,
                null,
                null,
                sb.opts.stack,
                null,
                sb.opts.xAxisIndex,
                sb.opts.yAxisIndex,
                null,
                null,
                null,
                null,
                null
        );
    }

    private SeriesModel lineModel(OptionSeriesBuilder<LineSeriesOpts> sb) {
        return new SeriesModel(
                sb.name,
                sb.opts.getType().name(),
                new EncodeModel(sb.xDimension, sb.yDimensions, null, null),
                sb.opts.label != null ? sb.opts.label.resolve() : null,
                sb.datasetSeriesLayoutBy,
                sb.opts.areaStyle,
                sb.opts.showSymbol,
                sb.opts.stack,
                sb.opts.smooth,
                sb.opts.xAxisIndex,
                sb.opts.yAxisIndex,
                null,
                null,
                null,
                null,
                null);
    }


    private SeriesModel scatterModel(OptionSeriesBuilder<ScatterSeriesOpts> sb) {
        return new SeriesModel(
                sb.name,
                sb.opts.getType().name(),
                new EncodeModel(sb.xDimension, sb.yDimensions, null, null),
                sb.opts.label != null ? sb.opts.label.resolve() : null,
                sb.datasetSeriesLayoutBy,
                null,
                null,
                null,
                null,
                sb.opts.xAxisIndex,
                sb.opts.yAxisIndex,
                null,
                null,
                null,
                null,
                null
        );
    }

    private SeriesModel pieModel(OptionSeriesBuilder<PieSeriesOpts> sb) {

        // TODO: multiple series in a pie chart?
        Integer dataDim = sb.yDimensions != null && !sb.yDimensions.isEmpty() ? sb.yDimensions.get(0) : null;

        return new SeriesModel(
                sb.name,
                sb.opts.getType().name(),

                new EncodeModel(null, null, sb.pieLabelsDimension, dataDim),

                sb.opts.label != null && sb.opts.label.label != null ? sb.opts.label.label.resolve() : null,
                sb.datasetSeriesLayoutBy,
                null,
                null,
                null,
                null,
                null,
                null,
                sb.opts.radius != null ? new RadiusModel(sb.opts.radius) : null,
                sb.opts.center != null ? new CenterModel(sb.opts.center[0], sb.opts.center[1]) : null,
                sb.opts.startAngle,
                sb.opts.endAngle,
                sb.opts.roseType != null ? sb.opts.roseType.name() : null
        );
    }
}
