package com.nhl.dflib.series;

import com.nhl.dflib.LongSeries;
import com.nhl.dflib.Series;

public class LongSingleValueSeries extends LongBaseSeries {

    private final long value;
    private final int size;

    public LongSingleValueSeries(long value, int size) {
        this.value = value;
        this.size = size;
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public Long get(int index) {
        if (index >= size) {
            throw new ArrayIndexOutOfBoundsException(index);
        }
        return value;
    }

    @Override
    public Series<Long> materialize() {
        return this;
    }

    @Override
    public Series<Long> fillNulls(Long value) {
        // primitive series has no nulls
        return this;
    }

    @Override
    public Series<Long> fillNullsFromSeries(Series<? extends Long> values) {
        // primitive series has no nulls
        return this;
    }

    @Override
    public Series<Long> fillNullsBackwards() {
        // primitive series has no nulls
        return this;
    }

    @Override
    public Series<Long> fillNullsForward() {
        // primitive series has no nulls
        return this;
    }

    @Override
    public long getLong(int index) {
        if (index >= size) {
            throw new ArrayIndexOutOfBoundsException(index);
        }
        return value;
    }

    @Override
    public void copyToLong(long[] to, int fromOffset, int toOffset, int len) {
        if (fromOffset + len > size) {
            throw new ArrayIndexOutOfBoundsException(fromOffset + len);
        }
        int targetIdx = toOffset;
        for (int i = fromOffset; i < len; i++) {
            to[targetIdx++] = get(i);
        }
    }

    @Override
    public LongSeries materializeLong() {
        return this;
    }

    @Override
    public LongSeries rangeOpenClosedLong(int fromInclusive, int toExclusive) {
        return fromInclusive == 0 && toExclusive == size()
                ? this
                : new LongSingleValueSeries(value, toExclusive - fromInclusive);
    }

    @Override
    public LongSeries headLong(int len) {
        return len < size ? new LongSingleValueSeries(value, len) : this;
    }

    @Override
    public LongSeries tailLong(int len) {
        return len < size ? new LongSingleValueSeries(value, len) : this;
    }

    @Override
    public long max() {
        return value;
    }

    @Override
    public long min() {
        return value;
    }

    @Override
    public long sum() {
        return value * size;
    }

    @Override
    public double avg() {
        return value;
    }

    @Override
    public double median() {
        return value;
    }
}
