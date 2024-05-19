package org.dflib.echarts.model;

import org.dflib.Series;

import java.util.ArrayList;
import java.util.List;

public class XAxisModel {

    private final boolean noBoundaryGap;
    private final Series<?> data;

    public XAxisModel(boolean noBoundaryGap, Series<?> data) {
        this.noBoundaryGap = noBoundaryGap;
        this.data = data;
    }

    public boolean isNoBoundaryGap() {
        return noBoundaryGap;
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
