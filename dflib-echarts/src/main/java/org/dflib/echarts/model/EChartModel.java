package org.dflib.echarts.model;

import org.dflib.Series;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * A model for internal EChart rendering.
 *
 * @since 1.0.0-M21
 */
public class EChartModel {

    private final String id;
    private final String echartsJsUrl;
    private final String title;
    private final Series<?> xAxis;
    private final Series[] yAxes;
    private final String theme;
    private final int width;
    private final int height;

    public EChartModel(
            String id,
            String echartsJsUrl,
            String title,
            Series<?> xAxis,
            Series[] yAxes,
            String theme,
            int width,
            int height) {

        this.id = Objects.requireNonNull(id);
        this.echartsJsUrl = Objects.requireNonNull(echartsJsUrl);

        this.title = title;
        this.xAxis = xAxis;
        this.yAxes = yAxes;
        this.theme = theme;
        this.width = width;
        this.height = height;
    }

    public String getId() {
        return id;
    }

    public String getEchartsJsUrl() {
        return echartsJsUrl;
    }

    public String getTitle() {
        return title;
    }

    public List<ListElement<?>> getXAxis() {

        int len = xAxis.size();
        List<ListElement<?>> elements = new ArrayList<>(len);

        for (int i = 0; i < len; i++) {
            elements.add(new ListElement<>(xAxis.get(i), i + 1 == len));
        }

        return elements;
    }

    public List<ListElement<List<ListElement<?>>>> getYAxes() {
        int sLen = yAxes.length;
        List<ListElement<List<ListElement<?>>>> series = new ArrayList<>(sLen);
        for (int i = 0; i < sLen; i++) {

            int eLen = yAxes[i].size();
            List<ListElement<?>> elements = new ArrayList<>(eLen);
            for (int j = 0; j < eLen; j++) {
                elements.add(new ListElement<>(yAxes[i].get(j), j + 1 == eLen));
            }

            series.add(new ListElement<>(elements, i + 1 == sLen));
        }

        return series;
    }

    public String getTheme() {
        return theme;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }
}
