package com.nhl.dflib.avro.loader;

import com.nhl.dflib.Series;
import com.nhl.dflib.ValueMapper;
import com.nhl.dflib.accumulator.Accumulator;
import com.nhl.dflib.accumulator.ObjectAccumulator;

// TODO: ugly generics switchover from "Object" for input to "T" for output.
//  We either need something like CSV ColumnBuilder, or make base Accumulator parameterized by both input and output
public class ConvertingAccumulator<T> implements Accumulator<Object> {

    private final Accumulator delegate;
    private final ValueMapper<Object, T> converter;

    public ConvertingAccumulator(ValueMapper<Object, T> converter) {
        this.delegate = new ObjectAccumulator<>();
        this.converter = converter;
    }

    public void add(Object v) {
        T t = v != null ? converter.map(v) : null;
        delegate.add(t);
    }

    public void set(int pos, Object v) {
        T t = v != null ? converter.map(v) : null;
        delegate.set(pos, t);
    }

    @Override
    public Series<Object> toSeries() {
        return delegate.toSeries();
    }
}
