package org.dflib.series;

import org.dflib.Series;
import org.dflib.ValueMapper;

public class ColumnMappedSeries<S, T> extends ObjectSeries<T> {

    private Series<S> source;
    private ValueMapper<S, T> mapper;
    private Series<T> materialized;

    public ColumnMappedSeries(Series<S> source, ValueMapper<S, T> mapper) {
        // TODO: we can't infer the target type from the ValueMapper, so using Object
        super(Object.class);
        this.source = source;
        this.mapper = mapper;
    }

    @Override
    public int size() {
        return source != null ? source.size() : materialized.size();
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

    protected ArraySeries<T> doMaterialize() {
        // TODO: see the constructor note.. They type of array is always Object
        T[] data = (T[]) new Object[size()];

        for (int i = 0; i < data.length; i++) {
            data[i] = mapper.map(source.get(i));
        }

        // reset source reference, allowing to free up memory..
        source = null;
        mapper = null;

        return new ArraySeries<>(data);
    }

    @Override
    public Series<T> fillNulls(T value) {
        return materialize().fillNulls(value);
    }

    @Override
    public Series<T> fillNullsFromSeries(Series<? extends T> values) {
        return materialize().fillNullsFromSeries(values);
    }

    @Override
    public Series<T> fillNullsBackwards() {
        return materialize().fillNullsBackwards();
    }

    @Override
    public Series<T> fillNullsForward() {
        return materialize().fillNullsForward();
    }
}
