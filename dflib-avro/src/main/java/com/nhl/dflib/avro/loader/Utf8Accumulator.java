package com.nhl.dflib.avro.loader;

import com.nhl.dflib.Series;
import com.nhl.dflib.accumulator.Accumulator;
import com.nhl.dflib.accumulator.ObjectAccumulator;

/**
 * Handles Avro {@link org.apache.avro.util.Utf8} conversion to String during DataFrame build.
 *
 *  @since 0.11
 */
// TODO: ugly generics switchover from Utf8 for input to String for output
//  We either need something like CSV ColumnBuilder, or make Accumulator<I, O> instead of <T>
public class Utf8Accumulator implements Accumulator<Object> {

    private final Accumulator delegate;

    public Utf8Accumulator() {
        delegate = new ObjectAccumulator<>();
    }

    public void add(Object v) {
        delegate.add(toString(v));
    }

    public void set(int pos, Object v) {
        delegate.set(pos, toString(v));
    }

    @Override
    public Series<Object> toSeries() {
        return delegate.toSeries();
    }

    protected String toString(Object utf8) {
        return utf8 != null ? utf8.toString() : null;
    }

}
