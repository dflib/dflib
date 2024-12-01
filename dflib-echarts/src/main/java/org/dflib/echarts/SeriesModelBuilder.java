package org.dflib.echarts;

import org.dflib.Index;
import org.dflib.echarts.render.ValueModels;
import org.dflib.echarts.render.option.EncodeModel;
import org.dflib.echarts.render.option.SeriesModel;
import org.dflib.echarts.render.option.series.CenterModel;
import org.dflib.echarts.render.option.series.RadiusModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

class SeriesModelBuilder {

    final SeriesOpts<?> seriesOpts;
    final Index dataColumns;
    String name;

    // dimensions are references to row numbers in the dataset
    Integer xDimension;
    List<Integer> yDimensions;
    // TODO: tooltip dimension

    Integer pieLabelsDimension;

    String datasetSeriesLayoutBy;


    public SeriesModelBuilder(String name, SeriesOpts<?> seriesOpts, Index dataColumns) {
        this.seriesOpts = Objects.requireNonNull(seriesOpts);
        this.dataColumns = Objects.requireNonNull(dataColumns);
        this.name = name;
    }

    public SeriesModelBuilder name(String name) {
        this.name = Objects.requireNonNull(name);
        return this;
    }

    public SeriesModelBuilder xDimension(int dim) {
        this.xDimension = dim;
        return this;
    }

    /**
     * Appends "y" dimension to the list of dimensions. "Dimension" is a position in the dataset.
     */
    public SeriesModelBuilder yDimension(int dim) {
        if (this.yDimensions == null) {
            this.yDimensions = new ArrayList<>();
        }

        this.yDimensions.add(dim);
        return this;
    }

    public SeriesModelBuilder pieLabelsDimension(int dim) {
        this.pieLabelsDimension = dim;
        return this;
    }


    public SeriesModelBuilder datasetSeriesLayoutBy(String layout) {
        this.datasetSeriesLayoutBy = layout;
        return this;
    }

    public SeriesModel build() {

        switch (seriesOpts.getType()) {
            case line:
                return lineModel((LineSeriesOpts) seriesOpts);
            case bar:
                return barModel((BarSeriesOpts) seriesOpts);
            case scatter:
                return scatterModel((ScatterSeriesOpts) seriesOpts);
            case candlestick:
                return candlestickModel((CandlestickSeriesOpts) seriesOpts);
            case boxplot:
                return boxplotModel((BoxplotSeriesOpts) seriesOpts);
            case pie:
                return pieModel((PieSeriesOpts) seriesOpts);
            default:
                throw new UnsupportedOperationException("Unexpected ChartType: " + seriesOpts.getType());
        }
    }

    private SeriesModel barModel(BarSeriesOpts so) {
        return new SeriesModel(
                name,
                so.getType().name(),
                new EncodeModel(xDimension, ValueModels.of(yDimensions), null, null),
                so.label != null ? so.label.resolve() : null,
                datasetSeriesLayoutBy,
                null,
                null,
                so.stack,
                null,
                null,
                so.xAxisIndex,
                so.yAxisIndex,
                null,
                null,
                null,
                null,
                null,
                so.itemStyle != null ? so.itemStyle.resolve() : null,
                null
        );
    }

    private SeriesModel lineModel(LineSeriesOpts so) {
        return new SeriesModel(
                name,
                so.getType().name(),
                new EncodeModel(xDimension, ValueModels.of(yDimensions), null, null),
                so.label != null ? so.label.resolve() : null,
                datasetSeriesLayoutBy,
                so.areaStyle,
                so.showSymbol,
                so.stack,
                so.smooth,
                so.symbolSize,
                so.xAxisIndex,
                so.yAxisIndex,
                null,
                null,
                null,
                null,
                null,
                so.itemStyle != null ? so.itemStyle.resolve() : null,
                so.lineStyle != null ? so.lineStyle.resolve() : null
        );
    }

    private SeriesModel scatterModel(ScatterSeriesOpts so) {
        return new SeriesModel(
                name,
                so.getType().name(),
                new EncodeModel(xDimension, ValueModels.of(yDimensions), null, null),
                so.label != null ? so.label.resolve() : null,
                datasetSeriesLayoutBy,
                null,
                null,
                null,
                null,
                so.symbolSize,
                so.xAxisIndex,
                so.yAxisIndex,
                null,
                null,
                null,
                null,
                null,
                so.itemStyle != null ? so.itemStyle.resolve() : null,
                null
        );
    }

    private SeriesModel candlestickModel(CandlestickSeriesOpts so) {
        return new SeriesModel(
                name,
                so.getType().name(),
                new EncodeModel(xDimension, ValueModels.of(yDimensions), null, null),
                null,
                datasetSeriesLayoutBy,
                null,
                null,
                null,
                null,
                null,
                so.xAxisIndex,
                so.yAxisIndex,
                null,
                null,
                null,
                null,
                null,
                so.itemStyle != null ? so.itemStyle.resolve() : null,
                null
        );
    }

    private SeriesModel boxplotModel(BoxplotSeriesOpts so) {
        return new SeriesModel(
                name,
                so.getType().name(),
                new EncodeModel(xDimension, ValueModels.of(yDimensions), null, null),
                null,
                datasetSeriesLayoutBy,
                null,
                null,
                null,
                null,
                null,
                so.xAxisIndex,
                so.yAxisIndex,
                null,
                null,
                null,
                null,
                null,
                so.itemStyle != null ? so.itemStyle.resolve() : null,
                null
        );
    }

    private SeriesModel pieModel(PieSeriesOpts so) {

        // TODO: multiple series in a pie chart?
        Integer dataDim = yDimensions != null && !yDimensions.isEmpty() ? yDimensions.get(0) : null;

        return new SeriesModel(
                name,
                so.getType().name(),

                new EncodeModel(null, null, pieLabelsDimension, dataDim),

                so.label != null && so.label.label != null ? so.label.label.resolve() : null,
                datasetSeriesLayoutBy,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                so.radius != null ? new RadiusModel(so.radius) : null,
                so.center != null ? new CenterModel(so.center[0], so.center[1]) : null,
                so.startAngle,
                so.endAngle,
                so.roseType != null ? so.roseType.name() : null,
                so.itemStyle != null ? so.itemStyle.resolve() : null,
                null
        );
    }
}
