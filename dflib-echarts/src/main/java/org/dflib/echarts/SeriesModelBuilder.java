package org.dflib.echarts;

import org.dflib.DataFrame;
import org.dflib.echarts.render.ValueModels;
import org.dflib.echarts.render.option.EncodeModel;
import org.dflib.echarts.render.option.SeriesModel;
import org.dflib.echarts.render.option.data.DataModel;
import org.dflib.echarts.render.option.series.CenterModel;
import org.dflib.echarts.render.option.series.RadiusModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

class SeriesModelBuilder {

    final Option opt;
    final DataFrame dataFrame;
    final int seriesPosInOpt;


    String name;

    // dimensions are references to row numbers in the dataset
    Integer xDimension;
    List<Integer> yDimensions;
    Integer pieLabelsDimension;
    Integer symbolSizeDimension;
    // TODO: tooltip dimension, etc.

    String datasetSeriesLayoutBy;

    public SeriesModelBuilder(String name, Option opt, DataFrame dataFrame, int seriesPosInOpt) {
        this.name = name;
        this.seriesPosInOpt = seriesPosInOpt;
        this.dataFrame = Objects.requireNonNull(dataFrame);
        this.opt = opt;
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

    public SeriesModelBuilder symbolSizeDimension(int dim) {
        this.symbolSizeDimension = dim;
        return this;
    }

    public SeriesModelBuilder datasetSeriesLayoutBy(String layout) {
        this.datasetSeriesLayoutBy = layout;
        return this;
    }

    public SeriesOpts<?> seriesOpts() {
        return opt.seriesOpts.get(seriesPosInOpt);
    }

    public SeriesModel resolve() {

        SeriesOpts<?> seriesOpts = seriesOpts();

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
            case heatmap:
                return heatmapModel(seriesOpts);
            default:
                throw new UnsupportedOperationException("Unexpected ChartType: " + seriesOpts.getType());
        }
    }

    private SeriesModel barModel(BarSeriesOpts so) {
        return new SeriesModel(
                name,
                so.getType().name(),
                null,
                new EncodeModel(xDimension, ValueModels.of(yDimensions), null, null),
                so.label != null ? so.label.resolve() : null,
                datasetSeriesLayoutBy,
                null,
                null,
                null,
                so.stack,
                so.barWidth != null ? so.barWidth.asString() : null,
                null,
                null,
                so.xAxisIndex,
                so.yAxisIndex,
                null,
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
                null,
                new EncodeModel(xDimension, ValueModels.of(yDimensions), null, null),
                so.label != null ? so.label.resolve() : null,
                datasetSeriesLayoutBy,
                null,
                so.areaStyle,
                so.showSymbol,
                so.stack,
                null,
                so.smooth,
                // TODO: ugly due to possible dimension linking
                symbolSizeDimension != null ? SymbolSize.resolveAsFunction(symbolSizeDimension)
                        : (so.symbolSize != null ? SymbolSize.resolveAsFixedSize(so.symbolSize.symbolSize) : null),
                so.xAxisIndex,
                so.yAxisIndex,
                null,
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
                null,
                new EncodeModel(xDimension, ValueModels.of(yDimensions), null, null),
                so.label != null ? so.label.resolve() : null,
                datasetSeriesLayoutBy,
                null,
                null,
                null,
                null,
                null,
                null,
                // TODO: ugly due to possible dimension linking
                symbolSizeDimension != null ? SymbolSize.resolveAsFunction(symbolSizeDimension)
                        : (so.symbolSize != null ? SymbolSize.resolveAsFixedSize(so.symbolSize.symbolSize) : null),
                so.xAxisIndex,
                so.yAxisIndex,
                null,
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
                null,
                new EncodeModel(xDimension, ValueModels.of(yDimensions), null, null),
                null,
                datasetSeriesLayoutBy,
                null,
                null,
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
                null,
                so.itemStyle != null ? so.itemStyle.resolve() : null,
                null
        );
    }

    private SeriesModel boxplotModel(BoxplotSeriesOpts so) {
        return new SeriesModel(
                name,
                so.getType().name(),
                null,
                new EncodeModel(xDimension, ValueModels.of(yDimensions), null, null),
                null,
                datasetSeriesLayoutBy,
                null,
                null,
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
                null,
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

    private SeriesModel heatmapModel(SeriesOpts<?> so) {
        return so instanceof HeatmapCartesian2DSeriesOpts
                ? heatmapCartesian2dModel((HeatmapCartesian2DSeriesOpts) so)
                : heatmapCalendarModel((HeatmapCalendarSeriesOpts) so);
    }

    private SeriesModel heatmapCartesian2dModel(HeatmapCartesian2DSeriesOpts so) {
        return new SeriesModel(
                name,
                so.getType().name(),
                heatmapCartesian2dDataModel(),
                null,
                null,
                datasetSeriesLayoutBy,
                CoordinateSystemType.cartesian2d.name(),
                null,
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
                null,
                null,
                null
        );
    }

    private SeriesModel heatmapCalendarModel(HeatmapCalendarSeriesOpts so) {
        return new SeriesModel(
                name,
                so.getType().name(),
                heatmapCalendarDataModel(),
                null,
                null,
                datasetSeriesLayoutBy,
                CoordinateSystemType.calendar.name(),
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                so.calendarIndex,
                null,
                null,
                null,
                null,
                null,
                null,
                null
        );
    }

    private DataModel heatmapCartesian2dDataModel() {
        InlineDataBuilder db = InlineDataBuilder.of(dataFrame);
        opt.seriesDataColumns.get(seriesPosInOpt).forEach(db::appendCol);
        return db.dataModel();
    }

    private DataModel heatmapCalendarDataModel() {
        InlineDataBuilder db = InlineDataBuilder.of(dataFrame);

        if (opt.calendars != null) {
            int len = opt.calendars.size();
            for (int i = 0; i < len; i++) {
                CalendarCoordsBuilder ab = opt.calendars.get(i);
                if (ab.columnName != null) {
                    db.appendCol(dataFrame.getColumn(ab.columnName));
                }
                // else: doesn't seem like there's a reasonable default for the calendar column.
            }
        }

        opt.seriesDataColumns.get(seriesPosInOpt).forEach(db::appendCol);
        return db.dataModel();
    }
}
