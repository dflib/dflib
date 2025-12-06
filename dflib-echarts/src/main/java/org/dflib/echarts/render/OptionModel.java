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

/**
 * @since 2.0.0
 */
public record OptionModel(
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

    public boolean isGridPresent() {
        return grid != null;
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
    @Override
    public List<CalendarModel> calendars() {
        return calendars;
    }

    /**
     * @since 2.0.0
     */
    public boolean isVisualMapsPresent() {
        return visualMaps != null;
    }

    public boolean isXAxesPresent() {
        return xAxes != null;
    }

    public boolean isYAxesPresent() {
        return yAxes != null;
    }

    public boolean isSingleAxesPresent() {
        return singleAxes != null;
    }

    @Deprecated(since = "2.0.0", forRemoval = true)
    public boolean isLegend() {
        return legend != null;
    }
}
