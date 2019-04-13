package com.nhl.dflib.series;

import com.nhl.dflib.Series;
import com.nhl.dflib.concat.SeriesConcat;

import static java.util.Arrays.asList;

public class IntSeries implements Series<Integer> {

    // data.length can be >= size
    private int[] data;
    private int offset;
    private int size;

    public IntSeries(int... data) {
        this(data, 0, data.length);
    }

    public IntSeries(int[] data, int offset, int size) {
        this.data = data;
        this.offset = offset;
        this.size = size;
    }

    @Override
    public int size() {
        return size;
    }

    public int getInt(int index) {
        if (offset + index >= size) {
            throw new ArrayIndexOutOfBoundsException(index);
        }

        return data[offset + index];
    }

    public void copyToInt(int[] to, int fromOffset, int toOffset, int len) {
        if (fromOffset + len > size) {
            throw new ArrayIndexOutOfBoundsException(fromOffset + len);
        }

        System.arraycopy(data, offset + fromOffset, to, toOffset, len);
    }

    public IntSeries concatInt(IntSeries... other) {
        if (other.length == 0) {
            return this;
        }

        int h = size;
        for (IntSeries s : other) {
            h += s.size;
        }

        int[] data = new int[h];
        copyToInt(data, 0, 0, size);

        int offset = size;
        for (IntSeries s : other) {
            int len = s.size();
            s.copyToInt(data, 0, offset, len);
            offset += len;
        }

        return new IntSeries(data);
    }

    public IntSeries headInt(int len) {
        return len < size ? new IntSeries(data, offset, len) : this;
    }

    public IntSeries tailInt(int len) {
        return len < size ? new IntSeries(data, offset + size - len, len) : this;
    }

    @Override
    public Integer get(int index) {
        return getInt(index);
    }

    @Override
    public void copyTo(Object[] to, int fromOffset, int toOffset, int len) {
        for (int i = offset + fromOffset; i < len; i++) {
            to[toOffset + i] = this.data[i];
        }
    }

    @Override
    public Series<Integer> materialize() {
        if (offset > 0 || size + offset < this.data.length) {
            int[] data = new int[size];
            copyToInt(data, 0, 0, size);
            return new IntSeries(data);
        }

        return this;
    }

    @Override
    public Series<Integer> fillNulls(Integer value) {
        // TODO: should we replace zeros?
        return this;
    }

    @Override
    public Series<Integer> fillNullsBackwards() {
        // TODO: should we replace zeros?
        return this;
    }

    @Override
    public Series<Integer> fillNullsForward() {
        // TODO: should we replace zeros?
        return this;
    }

    @Override
    public Series<Integer> rangeOpenClosed(int fromInclusive, int toExclusive) {
        return fromInclusive == 0 && toExclusive == size()
                ? this
                : new IntSeries(data, offset + fromInclusive, toExclusive - fromInclusive);
    }

    @Override
    public Series<Integer> concat(Series<? extends Integer>... other) {
        // concatenating as Integer... to concat as IntSeries, "concatInt" should be used
        if (other.length == 0) {
            return this;
        }

        Series<Integer>[] combined = new Series[other.length + 1];
        combined[0] = this;
        System.arraycopy(other, 0, combined, 1, other.length);

        return SeriesConcat.concat(asList(combined));
    }

    @Override
    public Series<Integer> head(int len) {
        return headInt(len);
    }

    @Override
    public Series<Integer> tail(int len) {
        return tailInt(len);
    }
}
