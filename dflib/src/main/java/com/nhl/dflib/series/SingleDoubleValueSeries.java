package com.nhl.dflib.series;

import com.nhl.dflib.DoubleSeries;
import com.nhl.dflib.Series;

public class SingleDoubleValueSeries extends DoubleBaseSeries {

    private final double value;
    private final int size;

    public SingleDoubleValueSeries(double value, int size) {
        this.value = value;
        this.size = size;
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public Double get(int index) {
        if (index >= size) {
            throw new ArrayIndexOutOfBoundsException(index);
        }
        return value;
    }

    @Override
    public void copyTo(Object[] to, int fromOffset, int toOffset, int len) {

    }

    @Override
    public Series<Double> materialize() {
        return this;
    }

    @Override
    public Series<Double> rangeOpenClosed(int fromInclusive, int toExclusive) {
        return this;
    }

    @Override
    public Series<Double> fillNulls(Double value) {
        return this;
    }

    @Override
    public Series<Double> fillNullsFromSeries(Series<? extends Double> values) {
        return this;
    }

    @Override
    public Series<Double> fillNullsBackwards() {
        return this;
    }

    @Override
    public Series<Double> fillNullsForward() {
        return this;
    }


    @Override
    public double getDouble(int index) {
        if (index >= size) {
            throw new ArrayIndexOutOfBoundsException(index);
        }
        return value;
    }

    @Override
    public void copyToDouble(double[] to, int fromOffset, int toOffset, int len) {

    }

    @Override
    public DoubleSeries materializeDouble() {
        return this;
    }

    @Override
    public DoubleSeries rangeOpenClosedDouble(int fromInclusive, int toExclusive) {
        return this;
    }

    @Override
    public DoubleSeries headDouble(int len) {
        return this;
    }

    @Override
    public DoubleSeries tailDouble(int len) {
        return this;
    }

    @Override
    public double max() {
        return value;
    }

    @Override
    public double min() {
        return value;
    }

    @Override
    public double sum() {
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
