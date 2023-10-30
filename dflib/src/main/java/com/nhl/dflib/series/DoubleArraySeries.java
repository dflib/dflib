package com.nhl.dflib.series;

import com.nhl.dflib.DoubleSeries;
import com.nhl.dflib.agg.PrimitiveSeriesAvg;
import com.nhl.dflib.agg.PrimitiveSeriesMedian;
import com.nhl.dflib.agg.PrimitiveSeriesMinMax;
import com.nhl.dflib.agg.PrimitiveSeriesSum;

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
    public DoubleSeries add(DoubleSeries s) {
        if (!(s instanceof DoubleArraySeries)) {
            return super.add(s);
        }

        int len = size();
        if (len != s.size()) {
            throw new IllegalArgumentException("Another Series size " + s.size() + " is not the same as this size " + len);
        }

        DoubleArraySeries as = (DoubleArraySeries) s;

        // storing ivars in the local vars for performance
        double[] l = this.data;
        double[] r = as.data;

        int lo = this.offset;
        int ro = as.offset;

        double[] data = new double[len];

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

        return new DoubleArraySeries(data);
    }

    @Override
    public DoubleSeries sub(DoubleSeries s) {
        if (!(s instanceof DoubleArraySeries)) {
            return super.add(s);
        }

        int len = size();
        if (len != s.size()) {
            throw new IllegalArgumentException("Another Series size " + s.size() + " is not the same as this size " + len);
        }

        DoubleArraySeries as = (DoubleArraySeries) s;

        // storing ivars in the local vars for performance
        double[] l = this.data;
        double[] r = as.data;

        int lo = this.offset;
        int ro = as.offset;

        double[] data = new double[len];

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

        return new DoubleArraySeries(data);
    }

    @Override
    public DoubleSeries mul(DoubleSeries s) {
        if (!(s instanceof DoubleArraySeries)) {
            return super.add(s);
        }

        int len = size();
        if (len != s.size()) {
            throw new IllegalArgumentException("Another Series size " + s.size() + " is not the same as this size " + len);
        }

        DoubleArraySeries as = (DoubleArraySeries) s;

        // storing ivars in the local vars for performance
        double[] l = this.data;
        double[] r = as.data;

        int lo = this.offset;
        int ro = as.offset;

        double[] data = new double[len];

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

        return new DoubleArraySeries(data);
    }

    @Override
    public DoubleSeries div(DoubleSeries s) {
        if (!(s instanceof DoubleArraySeries)) {
            return super.add(s);
        }

        int len = size();
        if (len != s.size()) {
            throw new IllegalArgumentException("Another Series size " + s.size() + " is not the same as this size " + len);
        }

        DoubleArraySeries as = (DoubleArraySeries) s;

        // storing ivars in the local vars for performance
        double[] l = this.data;
        double[] r = as.data;

        int lo = this.offset;
        int ro = as.offset;

        double[] data = new double[len];

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

        return new DoubleArraySeries(data);
    }

    @Override
    public DoubleSeries mod(DoubleSeries s) {
        if (!(s instanceof DoubleArraySeries)) {
            return super.add(s);
        }

        int len = size();
        if (len != s.size()) {
            throw new IllegalArgumentException("Another Series size " + s.size() + " is not the same as this size " + len);
        }

        DoubleArraySeries as = (DoubleArraySeries) s;

        // storing ivars in the local vars for performance
        double[] l = this.data;
        double[] r = as.data;

        int lo = this.offset;
        int ro = as.offset;

        double[] data = new double[len];

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

        return new DoubleArraySeries(data);
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
    public DoubleSeries head(int len) {

        if (Math.abs(len) >= size) {
            return this;
        }

        return len < 0 ? tail(size + len) : new DoubleArraySeries(data, offset, len);
    }

    @Override
    public DoubleSeries tail(int len) {

        if (Math.abs(len) >= size) {
            return this;
        }

        return len < 0 ? head(size + len) : new DoubleArraySeries(data, offset + size - len, len);
    }

    @Override
    public DoubleSeries rangeOpenClosedDouble(int fromInclusive, int toExclusive) {
        return fromInclusive == 0 && toExclusive == size()
                ? this
                : new DoubleArraySeries(data, offset + fromInclusive, toExclusive - fromInclusive);
    }

    @Override
    public DoubleSeries materialize() {
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
    public double avg() {
        return PrimitiveSeriesAvg.avgOfArray(data, offset, size);
    }

    @Override
    public double median() {
        return PrimitiveSeriesMedian.medianOfArray(data, offset, size);
    }

    @Override
    public DoubleSeries cumSum() {
        double[] cumSum = PrimitiveSeriesSum.cumSumOfArray(data, offset, size);
        return new DoubleArraySeries(cumSum);
    }
}
