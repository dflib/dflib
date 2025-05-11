package org.dflib.series;

import org.dflib.LongSeries;
import org.dflib.agg.Max;
import org.dflib.agg.Min;
import org.dflib.agg.Percentiles;
import org.dflib.agg.PrimitiveSeriesAvg;
import org.dflib.agg.PrimitiveSeriesSum;

public class LongArraySeries extends LongBaseSeries {

    private final long[] data;

    public LongArraySeries(long... data) {
        this.data = data;
    }

    @Override
    public int size() {
        return data.length;
    }

    @Override
    public LongSeries add(LongSeries s) {
        if (!(s instanceof LongArraySeries)) {
            return super.add(s);
        }

        int len = size();
        if (len != s.size()) {
            throw new IllegalArgumentException("Another Series size " + s.size() + " is not the same as this size " + len);
        }

        LongArraySeries as = (LongArraySeries) s;

        // storing ivars in the local vars for performance
        long[] l = this.data;
        long[] r = as.data;

        long[] data = new long[len];
        for (int i = 0; i < len; i++) {
            data[i] = l[i] + r[i];
        }

        return new LongArraySeries(data);
    }

    @Override
    public LongSeries sub(LongSeries s) {
        if (!(s instanceof LongArraySeries)) {
            return super.add(s);
        }

        int len = size();
        if (len != s.size()) {
            throw new IllegalArgumentException("Another Series size " + s.size() + " is not the same as this size " + len);
        }

        LongArraySeries as = (LongArraySeries) s;

        // storing ivars in the local vars for performance
        long[] l = this.data;
        long[] r = as.data;

        long[] data = new long[len];
        for (int i = 0; i < len; i++) {
            data[i] = l[i] - r[i];
        }

        return new LongArraySeries(data);
    }

    @Override
    public LongSeries mul(LongSeries s) {
        if (!(s instanceof LongArraySeries)) {
            return super.add(s);
        }

        int len = size();
        if (len != s.size()) {
            throw new IllegalArgumentException("Another Series size " + s.size() + " is not the same as this size " + len);
        }

        LongArraySeries as = (LongArraySeries) s;

        // storing ivars in the local vars for performance
        long[] l = this.data;
        long[] r = as.data;

        long[] data = new long[len];
        for (int i = 0; i < len; i++) {
            data[i] = l[i] * r[i];
        }

        return new LongArraySeries(data);
    }

    @Override
    public LongSeries div(LongSeries s) {
        if (!(s instanceof LongArraySeries)) {
            return super.add(s);
        }

        int len = size();
        if (len != s.size()) {
            throw new IllegalArgumentException("Another Series size " + s.size() + " is not the same as this size " + len);
        }

        LongArraySeries as = (LongArraySeries) s;

        // storing ivars in the local vars for performance
        long[] l = this.data;
        long[] r = as.data;

        long[] data = new long[len];
        for (int i = 0; i < len; i++) {
            data[i] = l[i] / r[i];
        }

        return new LongArraySeries(data);
    }

    @Override
    public LongSeries mod(LongSeries s) {
        if (!(s instanceof LongArraySeries)) {
            return super.add(s);
        }

        int len = size();
        if (len != s.size()) {
            throw new IllegalArgumentException("Another Series size " + s.size() + " is not the same as this size " + len);
        }

        LongArraySeries as = (LongArraySeries) s;

        // storing ivars in the local vars for performance
        long[] l = this.data;
        long[] r = as.data;

        long[] data = new long[len];
        for (int i = 0; i < len; i++) {
            data[i] = l[i] % r[i];
        }

        return new LongArraySeries(data);
    }

    @Override
    public long getLong(int index) {
        return data[index];
    }

    @Override
    public void copyToLong(long[] to, int fromOffset, int toOffset, int len) {
        if (fromOffset + len > size()) {
            throw new ArrayIndexOutOfBoundsException(fromOffset + len);
        }

        System.arraycopy(data, fromOffset, to, toOffset, len);
    }

    @Override
    public LongSeries rangeLong(int fromInclusive, int toExclusive) {
        return fromInclusive == 0 && toExclusive == size()
                ? this
                : new LongArrayRangeSeries(data, fromInclusive, toExclusive - fromInclusive);
    }

    @Override
    public LongSeries materialize() {
        return this;
    }


    @Override
    public long max() {
        return Max.ofArray(data, 0, size());
    }

    @Override
    public long min() {
        return Min.ofArray(data, 0, size());
    }

    @Override
    public long sum() {
        return PrimitiveSeriesSum.sumOfArray(data, 0, size());
    }

    @Override
    public double avg() {
        return PrimitiveSeriesAvg.avgOfArray(data, 0, size());
    }

    @Override
    public LongSeries cumSum() {
        long[] cumSum = PrimitiveSeriesSum.cumSumOfArray(data, 0, size());
        return new LongArraySeries(cumSum);
    }

    @Override
    public double quantile(double q) {
        return Percentiles.ofArray(data, 0, size(), q);
    }
}
