package com.nhl.dflib.series;

import com.nhl.dflib.Printers;
import com.nhl.dflib.Series;

import java.util.Objects;

public class HeadSeries<T> implements Series<T> {

    private Series<T> source;
    private int len;

    public HeadSeries(Series<T> source, int len) {

        // callers should use factory method if they expect the length to be out of bounds.
        if (len > source.size()) {
            throw new IllegalArgumentException("Head length (" + len + ") exceeds source size (" + source.size() + ")");
        }

        this.source = source;
        this.len = len;
    }

    public static <T> Series<T> forSeries(Series<T> source, int len) {
        Objects.requireNonNull(source);
        return len >= source.size() ? source : new HeadSeries<>(source, len);
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

        return source.get(index);
    }

    @Override
    public void copyTo(Object[] to, int fromOffset, int toOffset, int len) {
        if (fromOffset + len > this.len) {
            throw new ArrayIndexOutOfBoundsException(fromOffset + len);
        }

        source.copyTo(to, fromOffset, toOffset, len);
    }

    @Override
    public Series<T> materialize() {
        Object[] head = new Object[len];
        source.copyTo(head, 0, 0, len);
        return new ArraySeries<>((T[]) head);
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
        return Printers.inline.print(new StringBuilder("HeadSeries ["), this).append("]").toString();
    }
}
