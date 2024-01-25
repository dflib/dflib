package org.dflib.series;

import org.dflib.IntSeries;
import org.dflib.LongSeries;
import org.dflib.agg.PrimitiveSeriesAvg;
import org.dflib.agg.PrimitiveSeriesMedian;
import org.dflib.agg.PrimitiveSeriesMinMax;
import org.dflib.agg.PrimitiveSeriesSum;

/**
 * @since 0.6
 */
public class IntArraySeries extends IntBaseSeries {

    private final int[] data;

    public IntArraySeries(int... data) {
        this.data = data;
    }

    @Override
    public int size() {
        return data.length;
    }

    // oddly enough, the same optimization of the "eq" method with IntArraySeries cast only shows about 3% speed
    // improvement, so we are ignoring it. While reimplementing "add" gives 33% improvement vs super.

    @Override
    public IntSeries add(IntSeries s) {

        if (!(s instanceof IntArraySeries)) {
            return super.add(s);
        }

        int len = size();
        if (len != s.size()) {
            throw new IllegalArgumentException("Another Series size " + s.size() + " is not the same as this size " + len);
        }

        IntArraySeries as = (IntArraySeries) s;

        // storing ivars in the local vars for performance
        int[] l = this.data;
        int[] r = as.data;

        int[] data = new int[len];

        for (int i = 0; i < len; i++) {
            data[i] = l[i] + r[i];
        }

        return new IntArraySeries(data);
    }

    @Override
    public IntSeries sub(IntSeries s) {
        if (!(s instanceof IntArraySeries)) {
            return super.sub(s);
        }

        int len = size();
        if (len != s.size()) {
            throw new IllegalArgumentException("Another Series size " + s.size() + " is not the same as this size " + len);
        }

        IntArraySeries as = (IntArraySeries) s;

        // storing ivars in the local vars for performance
        int[] l = this.data;
        int[] r = as.data;

        int[] data = new int[len];
        for (int i = 0; i < len; i++) {
            data[i] = l[i] - r[i];
        }

        return new IntArraySeries(data);
    }

    @Override
    public IntSeries mul(IntSeries s) {
        if (!(s instanceof IntArraySeries)) {
            return super.mul(s);
        }

        int len = size();
        if (len != s.size()) {
            throw new IllegalArgumentException("Another Series size " + s.size() + " is not the same as this size " + len);
        }

        IntArraySeries as = (IntArraySeries) s;

        // storing ivars in the local vars for performance
        int[] l = this.data;
        int[] r = as.data;

        int[] data = new int[len];
        for (int i = 0; i < len; i++) {
            data[i] = l[i] * r[i];
        }

        return new IntArraySeries(data);
    }

    @Override
    public IntSeries div(IntSeries s) {
        if (!(s instanceof IntArraySeries)) {
            return super.div(s);
        }

        int len = size();
        if (len != s.size()) {
            throw new IllegalArgumentException("Another Series size " + s.size() + " is not the same as this size " + len);
        }

        IntArraySeries as = (IntArraySeries) s;

        // storing ivars in the local vars for performance
        int[] l = this.data;
        int[] r = as.data;

        int[] data = new int[len];

        for (int i = 0; i < len; i++) {
            data[i] = l[i] / r[i];
        }

        return new IntArraySeries(data);
    }

    @Override
    public IntSeries mod(IntSeries s) {
        if (!(s instanceof IntArraySeries)) {
            return super.mod(s);
        }

        int len = size();
        if (len != s.size()) {
            throw new IllegalArgumentException("Another Series size " + s.size() + " is not the same as this size " + len);
        }

        IntArraySeries as = (IntArraySeries) s;

        // storing ivars in the local vars for performance
        int[] l = this.data;
        int[] r = as.data;

        int[] data = new int[len];
        for (int i = 0; i < len; i++) {
            data[i] = l[i] % r[i];
        }

        return new IntArraySeries(data);
    }

    @Override
    public int getInt(int index) {
        return data[index];
    }

    @Override
    public void copyToInt(int[] to, int fromOffset, int toOffset, int len) {
        if (fromOffset + len > size()) {
            throw new ArrayIndexOutOfBoundsException(fromOffset + len);
        }

        System.arraycopy(data, fromOffset, to, toOffset, len);
    }

    @Override
    public IntSeries rangeInt(int fromInclusive, int toExclusive) {
        return fromInclusive == 0 && toExclusive == size()
                ? this
                : new IntArrayRangeSeries(data, fromInclusive, toExclusive - fromInclusive);
    }

    @Override
    public IntSeries materialize() {
        return this;
    }

    @Override
    public int max() {
        return PrimitiveSeriesMinMax.maxOfArray(data, 0, size());
    }

    @Override
    public int min() {
        return PrimitiveSeriesMinMax.minOfArray(data, 0, size());
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
    public double median() {
        return PrimitiveSeriesMedian.medianOfArray(data, 0, size());
    }

    @Override
    public LongSeries cumSum() {
        long[] cumSum = PrimitiveSeriesSum.cumSumOfArray(data, 0, size());
        return new LongArraySeries(cumSum);
    }
}
