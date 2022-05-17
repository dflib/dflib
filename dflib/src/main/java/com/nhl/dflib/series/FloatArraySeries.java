package com.nhl.dflib.series;

import com.nhl.dflib.FloatSeries;
import com.nhl.dflib.agg.PrimitiveSeriesAvg;
import com.nhl.dflib.agg.PrimitiveSeriesMedian;
import com.nhl.dflib.agg.PrimitiveSeriesMinMax;
import com.nhl.dflib.agg.PrimitiveSeriesSum;

/**
 * @since 0.6
 */
// TODO: should we split that into a fast FloatArraySeries that matches exactly data[], and a slower FloatArrayRangeSeries,
//  that has offset and size? Need to measure the performance gain of not having to calculate offset
public class FloatArraySeries extends FloatBaseSeries {

    private final float[] data;
    private final int offset;
    private final int size;

    public FloatArraySeries(float... data) {
        this(data, 0, data.length);
    }

    public FloatArraySeries(float[] data, int offset, int size) {
        this.data = data;
        this.offset = offset;
        this.size = size;
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public float getFloat(int index) {
        if (index >= size) {
            throw new ArrayIndexOutOfBoundsException(index);
        }

        return data[offset + index];
    }

    @Override
    public void copyToFloat(float[] to, int fromOffset, int toOffset, int len) {
        if (fromOffset + len > size) {
            throw new ArrayIndexOutOfBoundsException(fromOffset + len);
        }

        System.arraycopy(data, offset + fromOffset, to, toOffset, len);
    }

    @Override
    public FloatSeries headFloat(int len) {
        return len < size ? new FloatArraySeries(data, offset, len) : this;
    }

    @Override
    public FloatSeries tailFloat(int len) {
        return len < size ? new FloatArraySeries(data, offset + size - len, len) : this;
    }

    @Override
    public FloatSeries rangeOpenClosedFloat(int fromInclusive, int toExclusive) {
        return fromInclusive == 0 && toExclusive == size()
                ? this
                : new FloatArraySeries(data, offset + fromInclusive, toExclusive - fromInclusive);
    }

    @Override
    public FloatSeries materializeFloat() {
        if (offset > 0 || size + offset < this.data.length) {
            float[] data = new float[size];
            copyToFloat(data, 0, 0, size);
            return new FloatArraySeries(data);
        }

        return this;
    }

    @Override
    public float max() {
        return PrimitiveSeriesMinMax.maxOfArray(data, offset, size);
    }

    @Override
    public float min() {
        return PrimitiveSeriesMinMax.minOfArray(data, offset, size);
    }

    @Override
    public float sum() {
        return PrimitiveSeriesSum.sumOfArray(data, offset, size);
    }

    @Override
    public float avg() {
        return PrimitiveSeriesAvg.avgOfArray(data, offset, size);
    }

    @Override
    public float median() {
        return PrimitiveSeriesMedian.medianOfArray(data, offset, size);
    }
}
