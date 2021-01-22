package com.nhl.dflib.avro.loader;

import com.nhl.dflib.Series;
import com.nhl.dflib.accumulator.Accumulator;
import com.nhl.dflib.accumulator.ObjectAccumulator;

import java.nio.ByteBuffer;

/**
 * Handles {@link ByteBuffer} conversion preferred by Avro to a byte[] during DataFrame build.
 *
 * @since 0.11
 */
// TODO: ugly generics switchover from ByteBuffer for input to byte[] for output
//  We either need something like CSV ColumnBuilder, or make Accumulator<I, O> instead of <T>
public class ByteBufferAccumulator implements Accumulator<Object> {

    private final Accumulator delegate;

    public ByteBufferAccumulator() {
        delegate = new ObjectAccumulator<>();
    }

    public void add(Object v) {
        delegate.add(toByteArray(v));
    }

    public void set(int pos, Object v) {
        delegate.set(pos, toByteArray(v));
    }

    @Override
    public Series<Object> toSeries() {
        return delegate.toSeries();
    }

    protected byte[] toByteArray(Object v) {
        if (v == null) {
            return null;
        }

        ByteBuffer buffer = (ByteBuffer) v;
        int pos = buffer.position();
        int limit = buffer.limit();
        byte[] bytes = new byte[limit - pos];

        buffer.get(bytes);

        return bytes;
    }
}
