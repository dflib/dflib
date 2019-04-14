package com.nhl.dflib.csv;

import com.nhl.dflib.Series;
import com.nhl.dflib.collection.IntMutableList;
import com.nhl.dflib.map.IntValueMapper;

class IntSeriesBuilder implements SeriesBuilder<Integer> {

    private IntMutableList accumulator;
    private IntValueMapper<String> mapper;

    public IntSeriesBuilder() {
        this.accumulator = new IntMutableList();
        this.mapper = IntValueMapper.stringToInt();
    }

    @Override
    public void append(String csvValue) {
        accumulator.add(mapper.map(csvValue));
    }

    @Override
    public Series<Integer> toSeries() {
        return accumulator.toIntSeries();
    }
}
