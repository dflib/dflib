package com.nhl.dflib.series;

import com.nhl.dflib.DataFrame;
import com.nhl.dflib.Series;
import com.nhl.dflib.map.RowToValueMapper;
import com.nhl.dflib.row.RowProxy;

public class RowMappedSeries<T> implements Series<T> {

    private DataFrame source;
    private RowToValueMapper<T> mapper;
    private Series<T> materialized;

    public RowMappedSeries(DataFrame source, RowToValueMapper<T> mapper) {
        this.source = source;
        this.mapper = mapper;
    }

    @Override
    public int size() {
        return source != null ? source.height() : materialize().size();
    }

    @Override
    public T get(int index) {
        return materialize().get(index);
    }

    @Override
    public void copyTo(Object[] to, int fromOffset, int toOffset, int len) {
        materialize().copyTo(to, fromOffset, toOffset, len);
    }

    @Override
    public Series<T> materialize() {
        if (materialized == null) {
            synchronized (this) {
                if (materialized == null) {
                    materialized = doMaterialize();
                }
            }
        }

        return materialized;
    }

    protected Series<T> doMaterialize() {
        Object[] data = new Object[source.height()];

        int i = 0;
        for (RowProxy row : source) {
            data[i++] = mapper.map(row);
        }

        // reset source reference, allowing to free up memory..
        source = null;
        mapper = null;

        return new ArraySeries(data);
    }

    @Override
    public Series<T> fillNulls(T value) {
        return materialize().fillNulls(value);
    }

    @Override
    public Series<T> backFillNulls() {
        return materialize().backFillNulls();
    }

    @Override
    public Series<T> forwardFillNulls() {
        return materialize().forwardFillNulls();
    }
}
