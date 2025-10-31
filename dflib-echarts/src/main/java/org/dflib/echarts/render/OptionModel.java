package org.dflib.echarts.render;

import org.dflib.echarts.render.option.GridModel;
import org.dflib.echarts.render.option.LegendModel;
import org.dflib.echarts.render.option.SeriesModel;
import org.dflib.echarts.render.option.TitleModel;
import org.dflib.echarts.render.option.axis.AxisModel;
import org.dflib.echarts.render.option.axis.SingleAxisModel;
import org.dflib.echarts.render.option.calendar.CalendarModel;
import org.dflib.echarts.render.option.dataset.DatasetModel;
import org.dflib.echarts.render.option.toolbox.ToolboxModel;
import org.dflib.echarts.render.option.tooltip.TooltipModel;
import org.dflib.echarts.render.option.visualmap.VisualMapModel;

import java.util.List;

public class OptionModel {

    private final DatasetModel dataset;
    private final LegendModel legend;
    private final List<GridModel> grid;
    private final List<SeriesModel> series;
    private final TitleModel title;
    private final ToolboxModel toolbox;
    private final TooltipModel tooltip;
    private final List<AxisModel> xAxes;
    private final List<AxisModel> yAxes;
    private final List<SingleAxisModel> singleAxes;
    private final List<CalendarModel> calendars;
    private final List<VisualMapModel> visualMaps;

    /**
     * @since 2.0.0
     */
    public OptionModel(
            DatasetModel dataset,
            LegendModel legend,
            List<GridModel> grid,
            List<SeriesModel> series,
            TitleModel title,
            ToolboxModel toolbox,
            TooltipModel tooltip,
            List<AxisModel> xAxes,
            List<AxisModel> yAxes,
            List<SingleAxisModel> singleAxes,
            List<CalendarModel> calendars,
            List<VisualMapModel> visualMaps) {

        this.title = title;
        this.toolbox = toolbox;
        this.tooltip = tooltip;
        this.dataset = dataset;
        this.visualMaps = visualMaps;
        this.calendars = calendars;
        this.xAxes = xAxes;
        this.yAxes = yAxes;
        this.singleAxes = singleAxes;
        this.grid = grid;
        this.series = series;
        this.legend = legend;
    }

    @Deprecated(since = "2.0.0", forRemoval = true)
    public OptionModel(
            DatasetModel dataset,
            boolean legend,
            List<GridModel> grid,
            List<SeriesModel> series,
            String title,
            ToolboxModel toolbox,
            TooltipModel tooltip,
            List<AxisModel> xAxes,
            List<AxisModel> yAxes) {
        this(
                dataset,
                legend ? new LegendModel(null, "plain", null, null, null, null, null, null) : null,
                grid,
                series,
                title != null ? new TitleModel(title, null, null, null, null, null) : null,
                toolbox,
                tooltip,
                xAxes,
                yAxes,
                null,
                null,
                null);
    }

    public TitleModel getTitle() {
        return title;
    }

    public TooltipModel getTooltip() {
        return tooltip;
    }

    public ToolboxModel getToolbox() {
        return toolbox;
    }

    public boolean isGridPresent() {
        return grid != null;
    }

    public List<GridModel> getGrid() {
        return grid;
    }

    public DatasetModel getDataset() {
        return dataset;
    }

    /**
     * @since 2.0.0
     */
    public boolean isCalendarsPresent() {
        return calendars != null;
    }

    /**
     * @since 2.0.0
     */
    public List<CalendarModel> getCalendars() {
        return calendars;
    }

    /**
     * @since 2.0.0
     */
    public boolean isVisualMapsPresent() {
        return visualMaps != null;
    }

    /**
     * @since 2.0.0
     */
    public List<VisualMapModel> getVisualMaps() {
        return visualMaps;
    }

    public boolean isXAxesPresent() {
        return xAxes != null;
    }

    public List<AxisModel> getXAxes() {
        return xAxes;
    }

    public boolean isYAxesPresent() {
        return yAxes != null;
    }

    public List<AxisModel> getYAxes() {
        return yAxes;
    }

    public boolean isSingleAxesPresent() {
        return singleAxes != null;
    }

    public List<SingleAxisModel> getSingleAxes() {
        return singleAxes;
    }

    public List<SeriesModel> getSeries() {
        return series;
    }

    @Deprecated(since = "2.0.0", forRemoval = true)
    public boolean isLegend() {
        return legend != null;
    }

    /**
     * @since 2.0.0
     */
    public LegendModel getLegend() {
        return legend;
    }
}
