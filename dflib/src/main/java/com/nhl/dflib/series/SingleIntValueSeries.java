package com.nhl.dflib.series;

import com.nhl.dflib.IntSeries;
import com.nhl.dflib.Series;

public class SingleIntValueSeries extends IntBaseSeries {

    private final int value;
    private final int size;

    public SingleIntValueSeries(int value, int size) {
        this.value = value;
        this.size = size;
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public Integer get(int index) {
        if (index >= size) {
            throw new ArrayIndexOutOfBoundsException(index);
        }
        return value;
    }

    @Override
    public void copyTo(Object[] to, int fromOffset, int toOffset, int len) {

    }

    @Override
    public Series<Integer> materialize() {
        return this;
    }

    @Override
    public Series<Integer> rangeOpenClosed(int fromInclusive, int toExclusive) {
       return this;
    }

    @Override
    public Series<Integer> fillNulls(Integer value) {
        return this;
    }

    @Override
    public Series<Integer> fillNullsFromSeries(Series<? extends Integer> values) {
        return this;
    }

    @Override
    public Series<Integer> fillNullsBackwards() {
        return this;
    }

    @Override
    public Series<Integer> fillNullsForward() {
        return this;
    }


    @Override
    public int getInt(int index) {
        if (index >= size) {
            throw new ArrayIndexOutOfBoundsException(index);
        }
        return value;
    }

    @Override
    public void copyToInt(int[] to, int fromOffset, int toOffset, int len) {

    }

    @Override
    public IntSeries materializeInt() {
        return this;
    }

    @Override
    public IntSeries rangeOpenClosedInt(int fromInclusive, int toExclusive) {
        return this;
    }

    @Override
    public IntSeries headInt(int len) {
        return this;
    }

    @Override
    public IntSeries tailInt(int len) {
        return this;
    }

    @Override
    public int max() {
        return value;
    }

    @Override
    public int min() {
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
