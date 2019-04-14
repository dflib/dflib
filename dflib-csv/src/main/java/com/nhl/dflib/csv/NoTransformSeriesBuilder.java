package com.nhl.dflib.csv;

import com.nhl.dflib.Series;
import com.nhl.dflib.collection.MutableList;

class NoTransformSeriesBuilder implements SeriesBuilder<String> {

    private MutableList<String> accummulator;

    public NoTransformSeriesBuilder() {
        this.accummulator = new MutableList<>();
    }

    @Override
    public void append(String csvValue) {
        accummulator.add(csvValue);
    }

    @Override
    public Series<String> toSeries() {
        return accummulator.toSeries();
    }
}
