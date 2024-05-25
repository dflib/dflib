package org.dflib.echarts.model;

import org.dflib.Series;

import java.util.ArrayList;
import java.util.List;

/**
 * @since 1.0.0-M21
 */
public class AxisModel {

    private final String type;
    private final AxisLabelModel axisLabel;
    private final boolean boundaryGap;
    private final Series<?> data;

    public AxisModel(String type, AxisLabelModel axisLabel, boolean boundaryGap, Series<?> data) {
        this.type = type;
        this.axisLabel = axisLabel;
        this.boundaryGap = boundaryGap;
        this.data = data;
    }

    public String getType() {
        return type;
    }

    public AxisLabelModel getAxisLabel() {
        return axisLabel;
    }

    public boolean isBoundaryGap() {
        return boundaryGap;
    }

    public boolean isNoBoundaryGap() {
        return !boundaryGap;
    }

    public List<ListElementModel> getData() {

        int len = data.size();
        List<ListElementModel> elements = new ArrayList<>(len);

        for (int i = 0; i < len; i++) {
            elements.add(new ListElementModel(data.get(i), i + 1 == len));
        }

        return elements;
    }

}
