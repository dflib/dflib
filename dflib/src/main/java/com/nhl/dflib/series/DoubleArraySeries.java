package com.nhl.dflib.series;

import com.nhl.dflib.DoubleSeries;
import com.nhl.dflib.aggregate.PrimitiveSeriesAverage;
import com.nhl.dflib.aggregate.PrimitiveSeriesMedian;
import com.nhl.dflib.aggregate.PrimitiveSeriesMinMax;
import com.nhl.dflib.aggregate.PrimitiveSeriesSum;

/**
 * @since 0.6
 */
// TODO: should we split that into a fast DoubleArraySeries that matches exactly data[], and a slower DoubleArrayRangeSeries,
//  that has offset and size? Need to measure the performance gain of not having to calculate offset
public class DoubleArraySeries extends DoubleBaseSeries {

    private final double[] data;
    private final int offset;
    private final int size;

    public DoubleArraySeries(double... data) {
        this(data, 0, data.length);
    }

    public DoubleArraySeries(double[] data, int offset, int size) {
        this.data = data;
        this.offset = offset;
        this.size = size;
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public double getDouble(int index) {
        if (index >= size) {
            throw new ArrayIndexOutOfBoundsException(index);
        }

        return data[offset + index];
    }

    @Override
    public void copyToDouble(double[] to, int fromOffset, int toOffset, int len) {
        if (fromOffset + len > size) {
            throw new ArrayIndexOutOfBoundsException(fromOffset + len);
        }

        System.arraycopy(data, offset + fromOffset, to, toOffset, len);
    }

    @Override
    public DoubleSeries headDouble(int len) {
        return len < size ? new DoubleArraySeries(data, offset, len) : this;
    }

    @Override
    public DoubleSeries tailDouble(int len) {
        return len < size ? new DoubleArraySeries(data, offset + size - len, len) : this;
    }

    @Override
    public DoubleSeries rangeOpenClosedDouble(int fromInclusive, int toExclusive) {
        return fromInclusive == 0 && toExclusive == size()
                ? this
                : new DoubleArraySeries(data, offset + fromInclusive, toExclusive - fromInclusive);
    }

    @Override
    public DoubleSeries materializeDouble() {
        if (offset > 0 || size + offset < this.data.length) {
            double[] data = new double[size];
            copyToDouble(data, 0, 0, size);
            return new DoubleArraySeries(data);
        }

        return this;
    }

    @Override
    public double max() {
        return PrimitiveSeriesMinMax.maxOfArray(data, offset, size);
    }

    @Override
    public double min() {
        return PrimitiveSeriesMinMax.minOfArray(data, offset, size);
    }

    @Override
    public double sum() {
        return PrimitiveSeriesSum.sumOfArray(data, offset, size);
    }

    @Override
    public double average() {
        return PrimitiveSeriesAverage.averageOfArray(data, offset, size);
    }

    @Override
    public double median() {
        return PrimitiveSeriesMedian.medianOfArray(data, offset, size);
    }
}
