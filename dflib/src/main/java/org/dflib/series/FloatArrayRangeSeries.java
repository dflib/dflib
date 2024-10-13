package org.dflib.series;

import org.dflib.DoubleSeries;
import org.dflib.FloatSeries;
import org.dflib.agg.PrimitiveSeriesAvg;
import org.dflib.agg.PrimitiveSeriesMedian;
import org.dflib.agg.PrimitiveSeriesMinMax;
import org.dflib.agg.PrimitiveSeriesSum;

/**
 * A specialized FloatSeries that maps to a slice of an array. Calculating offsets during every operation has some
 * performance overhead, so this Series is somewhat slower than {@link FloatArraySeries}.
 *
 * @since 1.1.0
 */
public class FloatArrayRangeSeries extends FloatBaseSeries {

    private final float[] data;
    private final int offset;
    private final int size;

    public FloatArrayRangeSeries(float[] data, int offset, int size) {
        this.data = data;
        this.offset = offset;
        this.size = size;
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public FloatSeries add(FloatSeries s) {
        if (!(s instanceof FloatArrayRangeSeries)) {
            return super.add(s);
        }

        int len = size();
        if (len != s.size()) {
            throw new IllegalArgumentException("Another Series size " + s.size() + " is not the same as this size " + len);
        }

        FloatArrayRangeSeries as = (FloatArrayRangeSeries) s;

        // storing ivars in the local vars for performance
        float[] l = this.data;
        float[] r = as.data;

        int lo = this.offset;
        int ro = as.offset;

        float[] data = new float[len];

        if (lo > 0 || ro > 0) {
            for (int i = 0; i < len; i++) {
                data[i] = l[lo + i] + r[ro + i];
            }
        } else {
            // not having to calculate offset (the most common case) results in a performance boost
            // (due to HotSpot vectorization?)
            for (int i = 0; i < len; i++) {
                data[i] = l[i] + r[i];
            }
        }

        return new FloatArraySeries(data);
    }

    @Override
    public FloatSeries sub(FloatSeries s) {
        if (!(s instanceof FloatArrayRangeSeries)) {
            return super.add(s);
        }

        int len = size();
        if (len != s.size()) {
            throw new IllegalArgumentException("Another Series size " + s.size() + " is not the same as this size " + len);
        }

        FloatArrayRangeSeries as = (FloatArrayRangeSeries) s;

        // storing ivars in the local vars for performance
        float[] l = this.data;
        float[] r = as.data;

        int lo = this.offset;
        int ro = as.offset;

        float[] data = new float[len];

        if (lo > 0 || ro > 0) {
            for (int i = 0; i < len; i++) {
                data[i] = l[lo + i] - r[ro + i];
            }
        } else {
            // not having to calculate offset (the most common case) results in a performance boost
            // (due to HotSpot vectorization?)
            for (int i = 0; i < len; i++) {
                data[i] = l[i] - r[i];
            }
        }

        return new FloatArraySeries(data);
    }

    @Override
    public FloatSeries mul(FloatSeries s) {
        if (!(s instanceof FloatArrayRangeSeries)) {
            return super.add(s);
        }

        int len = size();
        if (len != s.size()) {
            throw new IllegalArgumentException("Another Series size " + s.size() + " is not the same as this size " + len);
        }

        FloatArrayRangeSeries as = (FloatArrayRangeSeries) s;

        // storing ivars in the local vars for performance
        float[] l = this.data;
        float[] r = as.data;

        int lo = this.offset;
        int ro = as.offset;

        float[] data = new float[len];

        if (lo > 0 || ro > 0) {
            for (int i = 0; i < len; i++) {
                data[i] = l[lo + i] * r[ro + i];
            }
        } else {
            // not having to calculate offset (the most common case) results in a performance boost
            // (due to HotSpot vectorization?)
            for (int i = 0; i < len; i++) {
                data[i] = l[i] * r[i];
            }
        }

        return new FloatArraySeries(data);
    }

    @Override
    public FloatSeries div(FloatSeries s) {
        if (!(s instanceof FloatArrayRangeSeries)) {
            return super.add(s);
        }

        int len = size();
        if (len != s.size()) {
            throw new IllegalArgumentException("Another Series size " + s.size() + " is not the same as this size " + len);
        }

        FloatArrayRangeSeries as = (FloatArrayRangeSeries) s;

        // storing ivars in the local vars for performance
        float[] l = this.data;
        float[] r = as.data;

        int lo = this.offset;
        int ro = as.offset;

        float[] data = new float[len];

        if (lo > 0 || ro > 0) {
            for (int i = 0; i < len; i++) {
                data[i] = l[lo + i] / r[ro + i];
            }
        } else {
            // not having to calculate offset (the most common case) results in a performance boost
            // (due to HotSpot vectorization?)
            for (int i = 0; i < len; i++) {
                data[i] = l[i] / r[i];
            }
        }

        return new FloatArraySeries(data);
    }

    @Override
    public FloatSeries mod(FloatSeries s) {
        if (!(s instanceof FloatArrayRangeSeries)) {
            return super.add(s);
        }

        int len = size();
        if (len != s.size()) {
            throw new IllegalArgumentException("Another Series size " + s.size() + " is not the same as this size " + len);
        }

        FloatArrayRangeSeries as = (FloatArrayRangeSeries) s;

        // storing ivars in the local vars for performance
        float[] l = this.data;
        float[] r = as.data;

        int lo = this.offset;
        int ro = as.offset;

        float[] data = new float[len];

        if (lo > 0 || ro > 0) {
            for (int i = 0; i < len; i++) {
                data[i] = l[lo + i] % r[ro + i];
            }
        } else {
            // not having to calculate offset (the most common case) results in a performance boost
            // (due to HotSpot vectorization?)
            for (int i = 0; i < len; i++) {
                data[i] = l[i] % r[i];
            }
        }

        return new FloatArraySeries(data);
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
    public FloatSeries rangeFloat(int fromInclusive, int toExclusive) {
        return fromInclusive == 0 && toExclusive == size()
                ? this
                : new FloatArrayRangeSeries(data, offset + fromInclusive, toExclusive - fromInclusive);
    }

    @Override
    public FloatSeries materialize() {
        if (offset == 0 && size == data.length) {
            return new FloatArraySeries(data);
        }

        float[] data = new float[size];
        copyToFloat(data, 0, 0, size);
        return new FloatArraySeries(data);
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
    public double sum() {
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

    @Override
    public DoubleSeries cumSum() {
        double[] cumSum = PrimitiveSeriesSum.cumSumOfArray(data, offset, size);
        return new DoubleArraySeries(cumSum);
    }
}
