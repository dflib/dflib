package org.dflib.echarts.model;

import java.util.List;
import java.util.Objects;

/**
 * A model for rendering EChart script
 *
 * @since 1.0.0-M21
 */
public class ScriptModel {

    private final String id;
    private final String title;
    private final XAxisModel xAxis;
    private final List<SeriesModel> series;
    private final String theme;
    private final boolean legend;

    public ScriptModel(
            String id,
            String title,
            XAxisModel xAxis,
            List<SeriesModel> series,
            String theme,
            boolean legend) {

        this.id = Objects.requireNonNull(id);
        this.title = title;
        this.xAxis = xAxis;
        this.series = series;
        this.theme = theme;
        this.legend = legend;
    }

    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public XAxisModel getXAxis() {
        return xAxis;
    }

    public List<SeriesModel> getSeries() {
        return series;
    }

    public String getTheme() {
        return theme;
    }

    public boolean isLegend() {
        return legend;
    }
}
