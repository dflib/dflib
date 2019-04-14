package com.nhl.dflib.csv;

import com.nhl.dflib.Series;
import com.nhl.dflib.collection.MutableList;
import com.nhl.dflib.map.ValueMapper;

class TransformingSeriesBuilder<T> implements SeriesBuilder<T> {

    private ValueMapper<String, T> mapper;
    private MutableList<T> accummulator;

    public TransformingSeriesBuilder(ValueMapper<String, T> mapper) {
        this.mapper = mapper;
        this.accummulator = new MutableList<>();
    }

    @Override
    public void append(String csvValue) {
        accummulator.add(mapper.map(csvValue));
    }

    @Override
    public Series<T> toSeries() {
        return accummulator.toSeries();
    }
}
