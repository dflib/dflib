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
    List<XAxisBuilder> xAxes;
    List<YAxis> yAxes;

    final List<SeriesBuilder<?>> series;

    public static Option of() {
        return new Option();
    }

    protected Option() {
        this.series = new ArrayList<>();
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
        Objects.requireNonNull(dataColumn);

        if (xAxes == null) {
            xAxes = new ArrayList<>(3);
        }

        xAxes.add(new XAxisBuilder(dataColumn, axis));
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
     * Specifies a DataFrame column to be plotted as individual series and configuration for series display.
     */
    public Option series(SeriesOpts opts, Index dataColumns) {
        series.add(new SeriesBuilder(opts, dataColumns));
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
        return new OptionModelMaker(this, df).resolve();
    }

}
