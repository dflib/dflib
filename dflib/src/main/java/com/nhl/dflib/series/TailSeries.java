package com.nhl.dflib.series;

import com.nhl.dflib.Printers;
import com.nhl.dflib.Series;

import java.util.Objects;

public class TailSeries<T> extends ObjectSeries<T> {
    private Series<T> source;
    private int offset;
    private int len;

    public TailSeries(Series<T> source, int len) {

        // callers should use factory method if they expect the length to be out of bounds.
        if (len > source.size()) {
            throw new IllegalArgumentException("Tail length (" + len + ") exceeds source size (" + source.size() + ")");
        }

        this.source = source;
        this.offset = source.size() - len;
        this.len = len;
    }

    public static <T> Series<T> forSeries(Series<T> source, int len) {
        Objects.requireNonNull(source);
        int s = source.size();
        return len >= s ? source : new TailSeries<>(source, len);
    }

    @Override
    public int size() {
        return len;
    }

    @Override
    public T get(int index) {

        if (index >= len) {
            throw new ArrayIndexOutOfBoundsException(index);
        }

        return source.get(offset + index);
    }

    @Override
    public void copyTo(Object[] to, int fromOffset, int toOffset, int len) {
        if (fromOffset + len > this.len) {
            throw new ArrayIndexOutOfBoundsException(fromOffset + len);
        }

        source.copyTo(to, offset + fromOffset, toOffset, len);
    }

    @Override
    public Series<T> materialize() {
        Object[] tail = new Object[len];
        source.copyTo(tail, offset, 0, len);
        return new ArraySeries<>((T[]) tail);
    }

    @Override
    public Series<T> fillNulls(T value) {
        // TODO: optimize data copy - do materialize and null checking over the same array
        return materialize().fillNulls(value);
    }

    @Override
    public Series<T> fillNullsBackwards() {
        // TODO: optimize data copy - do materialize and null checking over the same array
        return materialize().fillNullsBackwards();
    }

    @Override
    public Series<T> fillNullsForward() {
        // TODO: optimize data copy - do materialize and null checking over the same array
        return materialize().fillNullsForward();
    }

    @Override
    public String toString() {
        return Printers.inline.print(new StringBuilder("TailSeries ["), this).append("]").toString();
    }
}
