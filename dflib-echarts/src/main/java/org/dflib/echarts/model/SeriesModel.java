package org.dflib.echarts.model;

import java.util.List;

/**
 * A model for rendering EChart script "series" element
 *
 * @since 1.0.0-M21
 */
public class SeriesModel {

    private final String name;
    private final List<ValueModel> data;
    private final List<ValuePairModel> dataPairs;
    private final String type;
    private final boolean areaStyle;
    private final boolean stack;
    private final boolean smooth;
    private final boolean last;

    public SeriesModel(
            String name,
            List<ValueModel> data,
            List<ValuePairModel> dataPairs,
            String type,
            boolean areaStyle,
            boolean stack,
            boolean smooth,
            boolean last) {

        this.name = name;
        this.data = data;
        this.dataPairs = dataPairs;
        this.type = type;
        this.areaStyle = areaStyle;
        this.stack = stack;
        this.smooth = smooth;
        this.last = last;
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

    public List<ValueModel> getData() {
        return data;
    }

    public List<ValuePairModel> getDataPairs() {
        return dataPairs;
    }

    public boolean isAreaStyle() {
        return areaStyle;
    }

    public boolean isStack() {
        return stack;
    }

    public boolean isSmooth() {
        return smooth;
    }

    public boolean isLast() {
        return last;
    }
}
