package com.nhl.dflib.series;

import com.nhl.dflib.Series;

public class StringSingleValueSeries extends ObjectSeries<String> {
    private final String value;
    private final int size;

    public StringSingleValueSeries(String value, int size) {
        super(String.class);
        this.value = value;
        this.size = size;
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public String get(int index) {
        return value;
    }

    @Override
    public void copyTo(Object[] to, int fromOffset, int toOffset, int len) {

    }

    @Override
    public Series<String> materialize() {
        return this;
    }

    @Override
    public Series<String> fillNulls(String value) {
        return this;
    }

    @Override
    public Series<String> fillNullsFromSeries(Series<? extends String> values) {
        return this;
    }

    @Override
    public Series<String> fillNullsBackwards() {
        return this;
    }

    @Override
    public Series<String> fillNullsForward() {
        return this;
    }
}
