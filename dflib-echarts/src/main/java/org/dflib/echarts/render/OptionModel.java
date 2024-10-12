package org.dflib.echarts.render;

import org.dflib.echarts.render.option.dataset.DatasetModel;
import org.dflib.echarts.render.option.GridModel;
import org.dflib.echarts.render.option.SeriesModel;
import org.dflib.echarts.render.option.axis.AxisModel;
import org.dflib.echarts.render.option.toolbox.ToolboxModel;
import org.dflib.echarts.render.option.tooltip.TooltipModel;

import java.util.List;

public class OptionModel {

    private final DatasetModel dataset;
    private final boolean legend;
    private final List<GridModel> grid;
    private final List<SeriesModel> series;
    private final String title;
    private final ToolboxModel toolbox;
    private final TooltipModel tooltip;
    private final List<AxisModel> xAxes;
    private final List<AxisModel> yAxes;

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

        this.title = title;
        this.toolbox = toolbox;
        this.tooltip = tooltip;
        this.dataset = dataset;
        this.xAxes = xAxes;
        this.yAxes = yAxes;
        this.grid = grid;
        this.series = series;
        this.legend = legend;
    }

    public String getTitle() {
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

    public List<SeriesModel> getSeries() {
        return series;
    }

    public boolean isLegend() {
        return legend;
    }
}
