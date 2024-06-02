package org.dflib.echarts.render;

import org.dflib.echarts.render.option.AxisModel;
import org.dflib.echarts.render.option.DataSetModel;
import org.dflib.echarts.render.option.SeriesModel;
import org.dflib.echarts.render.option.ToolboxModel;
import org.dflib.echarts.render.option.tooltip.TooltipModel;

import java.util.List;

/**
 * @since 1.0.0-M21
 */
public class OptionModel {

    private final DataSetModel dataset;
    private final boolean legend;
    private final List<SeriesModel> series;
    private final String title;
    private final ToolboxModel toolbox;
    private final TooltipModel tooltip;
    private final AxisModel xAxis;
    private final AxisModel yAxis;

    public OptionModel(
            DataSetModel dataset,
            boolean legend,
            List<SeriesModel> series,
            String title,
            ToolboxModel toolbox,
            TooltipModel tooltip,
            AxisModel xAxis,
            AxisModel yAxis) {

        this.title = title;
        this.toolbox = toolbox;
        this.tooltip = tooltip;
        this.dataset = dataset;
        this.xAxis = xAxis;
        this.yAxis = yAxis;
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

    public DataSetModel getDataset() {
        return dataset;
    }

    public AxisModel getXAxis() {
        return xAxis;
    }

    public AxisModel getYAxis() {
        return yAxis;
    }

    public List<SeriesModel> getSeries() {
        return series;
    }

    public boolean isLegend() {
        return legend;
    }
}
