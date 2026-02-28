package org.dflib.echarts;

import org.dflib.DataFrame;
import org.dflib.Index;
import org.dflib.echarts.render.OptionModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * A builder of the EChart "option" object - the main chart configuration.
 */
public class Option {

    Title title;
    Legend legend;
    Toolbox toolbox;
    Tooltip tooltip;
    List<Grid> grids;
    List<VisualMap> visualMaps;
    List<ColumnLinkedCalendarCoords> calendars;
    List<ColumnLinkedXAxis> xAxes;
    List<YAxis> yAxes;
    Geo geo;
    List<ColumnLinkedSingleAxis> singleAxes;

    // these two lists are populated together (for each SeriesOpts there is an Index)
    final List<SeriesOpts<?>> seriesOpts;
    final List<Index> seriesDataColumns;

    public static Option of() {
        return new Option();
    }

    protected Option() {
        this.seriesOpts = new ArrayList<>();
        this.seriesDataColumns = new ArrayList<>();
    }

    public Option toolbox(Toolbox toolbox) {
        this.toolbox = toolbox;
        return this;
    }

    public Option tooltip(Tooltip tooltip) {
        this.tooltip = tooltip;
        return this;
    }

    /**
     * Adds a Grid to the chart. Grids are used to plot multiple charts in cartesian coordinates. Axis objects can
     * optionally have grid references.
     */
    public Option grid(Grid grid) {
        Objects.requireNonNull(grid);

        if (this.grids == null) {
            this.grids = new ArrayList<>();
        }

        this.grids.add(grid);
        return this;
    }

    /**
     * @since 2.0.0
     */
    public Option visualMap(VisualMap visualMap) {
        if (visualMaps == null) {
            visualMaps = new ArrayList<>(3);
        }

        visualMaps.add(visualMap);
        return this;
    }

    /**
     * @since 2.0.0
     */
    public Option geo(Geo geo) {
        this.geo = geo;
        return this;
    }

    /**
     * Adds a calendar coordinate system to the chart, that will use the specified DataFrame column to plot dates.
     * Since we don't specify a date range for the calendar in this method, the period of the last 12 months back
     * from the current date is assumed. Use {@link #calendar(String, CalendarCoords)} to set the exact range.
     *
     * @since 2.0.0
     */
    public Option calendar(String dataColumn) {
        return calendar(dataColumn, CalendarCoords.ofLast12Months());
    }

    /**
     * Adds a calendar coordinate system to the chart, that will use the specified DataFrame column to plot dates.
     *
     * @since 2.0.0
     */
    public Option calendar(String dataColumn, CalendarCoords calendar) {
        if (calendars == null) {
            calendars = new ArrayList<>(3);
        }

        calendars.add(new ColumnLinkedCalendarCoords(dataColumn, calendar));
        return this;
    }

    /**
     * Adds an X axis to the chart, that will use the specified DataFrame column as axis labels. If no X axis is set,
     * series element indices will be used for X.
     */
    public Option xAxis(String dataColumn) {
        return xAxis(dataColumn, XAxis.ofDefault());
    }

    public Option xAxis(XAxis axis) {
        return xAxis(null, axis);
    }

    public Option xAxis(String dataColumn, XAxis axis) {

        if (xAxes == null) {
            xAxes = new ArrayList<>(3);
        }

        xAxes.add(new ColumnLinkedXAxis(dataColumn, axis));
        return this;
    }

    /**
     * Adds one or more Y axes to the chart.
     */
    public Option yAxes(YAxis... axes) {
        for (YAxis a : axes) {
            yAxis(a);
        }

        return this;
    }

    /**
     * Adds a Y axis to the chart.
     */
    public Option yAxis(YAxis axis) {
        Objects.requireNonNull(axis);

        if (this.yAxes == null) {
            this.yAxes = new ArrayList<>(3);
        }

        this.yAxes.add(axis);
        return this;
    }

    /**
     * Adds a "single" axis to the chart that will be bound to the provided data column.
     *
     * @since 2.0.0
     */
    public Option singleAxis(String dataColumn, SingleAxis axis) {
        Objects.requireNonNull(axis);

        if (this.singleAxes == null) {
            this.singleAxes = new ArrayList<>(3);
        }

        this.singleAxes.add(new ColumnLinkedSingleAxis(dataColumn, axis));
        return this;
    }

    /**
     * Adds a "single" axis to the chart.
     *
     * @since 2.0.0
     */
    public Option singleAxis(SingleAxis axis) {
        return singleAxis(null, axis);
    }

    /**
     * Adds a categorical "single" axis to the chart that will be bound to the provided data column.
     *
     * @since 2.0.0
     */
    public Option singleAxis(String dataColumn) {
        return singleAxis(dataColumn, SingleAxis.ofCategory());
    }

    /**
     * Specifies a DataFrame column to be plotted as individual series and configuration for series display.
     */
    public Option series(SeriesOpts<?> opts, Index dataColumns) {
        seriesOpts.add(opts);
        seriesDataColumns.add(dataColumns);
        return this;
    }

    public Option title(String title) {
        return title(Title.of(title));
    }

    /**
     * @since 2.0.0
     */
    public Option title(Title title) {
        this.title = title;
        return this;
    }

    public Option legend() {
        return legend(Legend.ofPlain());
    }

    /**
     * @since 2.0.0
     */
    public Option legend(Legend legend) {
        this.legend = legend;
        return this;
    }

    protected OptionModel resolve(DataFrame df) {
        resolveDefaults();
        return new OptionModelBuilder(this, df).resolve();
    }

    private void resolveDefaults() {
        boolean cartesianDefaults = useCartesianDefaults();

        if (xAxes == null && cartesianDefaults) {
            xAxes = List.of(new ColumnLinkedXAxis(null, XAxis.ofDefault()));
        }

        if (yAxes == null && cartesianDefaults) {
            yAxes = List.of(YAxis.ofDefault());
        }

        if (singleAxes == null && useSingleAxisDefaults()) {
            singleAxes = List.of(new ColumnLinkedSingleAxis(null, SingleAxis.ofValue()));
        }

        if (calendars == null && useCalendarDefaults()) {
            calendars = List.of(new ColumnLinkedCalendarCoords(null, CalendarCoords.ofLast12Months()));
        }
    }

    private boolean useCartesianDefaults() {
        // TODO: geo-related check is a hack. How do we make it more generic?
        return (seriesOpts.isEmpty() && geo == null)
                || seriesOpts.stream().anyMatch(s -> s.getCoordinateSystemType().isCartesian());
    }

    private boolean useSingleAxisDefaults() {
        return seriesOpts.stream().anyMatch(s -> s.getCoordinateSystemType().isSingleAxis());
    }

    private boolean useCalendarDefaults() {
        return seriesOpts.stream().anyMatch(s -> s.getCoordinateSystemType().isCalendar());
    }
}
