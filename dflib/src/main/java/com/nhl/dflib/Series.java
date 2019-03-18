package com.nhl.dflib;

import com.nhl.dflib.series.ArraySeries;

public interface Series<T> {

    static Series<?>[] fromColumnarData(Object[][] columnarData) {

        int w = columnarData.length;

        Series[] series = new Series[w];

        for (int i = 0; i < w; i++) {
            series[i] = new ArraySeries(columnarData[i]);
        }

        return series;
    }

    int size();

    T get(int index);
}
