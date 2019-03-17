package com.nhl.dflib.series;

import com.nhl.dflib.Series;

import java.util.Objects;

public class IndexedSeries<T> implements Series<T> {

    private Series<T> source;
    private Series<Integer> includePositions;

    private Series<T> materialized;

    public IndexedSeries(Series<T> source, Series<Integer> includePositions) {
        this.source = Objects.requireNonNull(source);
        this.includePositions = Objects.requireNonNull(includePositions);
    }

    @Override
    public int size() {
        return includePositions.size();
    }

    @Override
    public T get(int index) {
        return getMaterialized().get(index);
    }

    protected Series<T> getMaterialized() {
        if (materialized == null) {
            synchronized (this) {
                if (materialized == null) {
                    materialized = doMaterialize();
                }
            }
        }

        return materialized;
    }

    protected ArraySeries doMaterialize() {

        int fl = includePositions.size();

        Object[] data = new Object[fl];

        for (int i = 0; i < fl; i++) {
            data[i] = source.get(includePositions.get(i));
        }

        // reset source reference, allowing to free up memory..
        source = null;
        includePositions = null;

        return new ArraySeries(data);
    }
}
