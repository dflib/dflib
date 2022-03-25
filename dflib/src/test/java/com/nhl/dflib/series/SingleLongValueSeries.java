package com.nhl.dflib.series;

import com.nhl.dflib.LongSeries;
import com.nhl.dflib.Series;

public class SingleLongValueSeries extends LongBaseSeries {

    private final long value;
    private final int size;

    public SingleLongValueSeries(long value, int size) {
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
    public void copyTo(Object[] to, int fromOffset, int toOffset, int len) {

    }

    @Override
    public Series<Long> materialize() {
        return this;
    }

    @Override
    public Series<Long> rangeOpenClosed(int fromInclusive, int toExclusive) {
        return this;
    }

    @Override
    public Series<Long> fillNulls(Long value) {
        return this;
    }

    @Override
    public Series<Long> fillNullsFromSeries(Series<? extends Long> values) {
        return this;
    }

    @Override
    public Series<Long> fillNullsBackwards() {
        return this;
    }

    @Override
    public Series<Long> fillNullsForward() {
        return this;
    }

    private Series<Long> alignAndReplace(Series<? extends Long> another) {

       return this;
    }

    @Override
    public long getLong(int index) {
        return value;
    }

    @Override
    public void copyToLong(long[] to, int fromOffset, int toOffset, int len) {

    }

    @Override
    public LongSeries materializeLong() {
        return this;
    }

    @Override
    public LongSeries rangeOpenClosedLong(int fromInclusive, int toExclusive) {
        return this;
    }

    @Override
    public LongSeries headLong(int len) {
        return this;
    }

    @Override
    public LongSeries tailLong(int len) {
        return this;
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
