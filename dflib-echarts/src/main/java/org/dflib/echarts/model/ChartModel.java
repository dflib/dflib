package org.dflib.echarts.model;

import org.dflib.Series;

import java.util.ArrayList;
import java.util.List;

/**
 * @since 1.0.0-M21
 */
public class ChartModel {

    private final String id;
    private final Series<?> xAxis;
    private final Series[] yAxes;
    private final boolean darkMode;

    public ChartModel(
            String id,
            Series<?> xAxis,
            Series[] yAxes,
            boolean darkMode) {
        this.id = id;
        this.xAxis = xAxis;
        this.yAxes = yAxes;
        this.darkMode = darkMode;
    }

    public String getId() {
        return id;
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

    public boolean isDarkMode() {
        return darkMode;
    }
}
