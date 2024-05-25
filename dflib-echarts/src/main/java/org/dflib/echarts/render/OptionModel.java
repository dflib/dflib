package org.dflib.echarts.render;

import org.dflib.echarts.render.option.AxisModel;
import org.dflib.echarts.render.option.DataSetModel;
import org.dflib.echarts.render.option.SeriesModel;
import org.dflib.echarts.render.option.ToolboxModel;

import java.util.List;

/**
 * @since 1.0.0-M21
 */
public class OptionModel {

    private final String title;
    private final ToolboxModel toolbox;
    private final DataSetModel dataset;
    private final AxisModel xAxis;
    private final AxisModel yAxis;
    private final List<SeriesModel> series;
    private final boolean legend;

    public OptionModel(
            String title,
            ToolboxModel toolbox,
            DataSetModel dataset,
            AxisModel xAxis,
            AxisModel yAxis,
            List<SeriesModel> series,
            boolean legend) {

        this.title = title;
        this.toolbox = toolbox;
        this.dataset = dataset;
        this.xAxis = xAxis;
        this.yAxis = yAxis;
        this.series = series;
        this.legend = legend;
    }

    public String getTitle() {
        return title;
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
