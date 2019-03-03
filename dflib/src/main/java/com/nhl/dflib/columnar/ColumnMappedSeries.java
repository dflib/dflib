package com.nhl.dflib.columnar;

import com.nhl.dflib.map.ValueMapper;

public class ColumnMappedSeries<S, T> implements Series<T> {

    private Series<S> source;
    private ValueMapper<S, T> mapper;
    private Series<T> materialized;

    public ColumnMappedSeries(Series<S> source, ValueMapper<S, T> mapper) {
        this.source = source;
        this.mapper = mapper;
    }

    @Override
    public int size() {
        return source.size();
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
        Object[] data = new Object[size()];

        for(int i = 0; i < data.length; i++) {
            data[i] = mapper.map(source.get(i));
        }

        return new ArraySeries(data);
    }
}
