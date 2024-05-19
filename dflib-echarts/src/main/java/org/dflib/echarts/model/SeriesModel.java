package org.dflib.echarts.model;

import java.util.List;

/**
 * A model for rendering EChart script "series" element
 *
 * @since 1.0.0-M21
 */
public class SeriesModel {

    private final String name;
    private final List<ListElementModel> data;
    private final String type;
    private final boolean last;

    public SeriesModel(String name, String type, List<ListElementModel> data, boolean last) {
        this.data = data;
        this.name = name;
        this.type = type;
        this.last = last;
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

    public List<ListElementModel> getData() {
        return data;
    }

    public boolean isLast() {
        return last;
    }
}
