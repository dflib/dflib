package org.dflib.series;

import org.dflib.DoubleSeries;
import org.dflib.FloatSeries;
import org.dflib.agg.PrimitiveSeriesAvg;
import org.dflib.agg.PrimitiveSeriesMinMax;
import org.dflib.agg.Percentiles;
import org.dflib.agg.PrimitiveSeriesSum;

/**
 * @since 1.1.0
 */
public class FloatArraySeries extends FloatBaseSeries {

    private final float[] data;

    public FloatArraySeries(float... data) {
        this.data = data;
    }

    @Override
    public int size() {
        return data.length;
    }

    @Override
    public FloatSeries add(FloatSeries s) {
        if (!(s instanceof FloatArraySeries)) {
            return super.add(s);
        }

        int len = size();
        if (len != s.size()) {
            throw new IllegalArgumentException("Another Series size " + s.size() + " is not the same as this size " + len);
        }

        FloatArraySeries as = (FloatArraySeries) s;

        // storing ivars in the local vars for performance
        float[] l = this.data;
        float[] r = as.data;

        float[] data = new float[len];
        for (int i = 0; i < len; i++) {
            data[i] = l[i] + r[i];
        }

        return new FloatArraySeries(data);
    }

    @Override
    public FloatSeries sub(FloatSeries s) {
        if (!(s instanceof FloatArraySeries)) {
            return super.add(s);
        }

        int len = size();
        if (len != s.size()) {
            throw new IllegalArgumentException("Another Series size " + s.size() + " is not the same as this size " + len);
        }

        FloatArraySeries as = (FloatArraySeries) s;

        // storing ivars in the local vars for performance
        float[] l = this.data;
        float[] r = as.data;

        float[] data = new float[len];
        for (int i = 0; i < len; i++) {
            data[i] = l[i] - r[i];
        }

        return new FloatArraySeries(data);
    }

    @Override
    public FloatSeries mul(FloatSeries s) {
        if (!(s instanceof FloatArraySeries)) {
            return super.add(s);
        }

        int len = size();
        if (len != s.size()) {
            throw new IllegalArgumentException("Another Series size " + s.size() + " is not the same as this size " + len);
        }

        FloatArraySeries as = (FloatArraySeries) s;

        // storing ivars in the local vars for performance
        float[] l = this.data;
        float[] r = as.data;

        float[] data = new float[len];
        for (int i = 0; i < len; i++) {
            data[i] = l[i] * r[i];
        }

        return new FloatArraySeries(data);
    }

    @Override
    public FloatSeries div(FloatSeries s) {
        if (!(s instanceof FloatArraySeries)) {
            return super.add(s);
        }

        int len = size();
        if (len != s.size()) {
            throw new IllegalArgumentException("Another Series size " + s.size() + " is not the same as this size " + len);
        }

        FloatArraySeries as = (FloatArraySeries) s;

        // storing ivars in the local vars for performance
        float[] l = this.data;
        float[] r = as.data;

        float[] data = new float[len];
        for (int i = 0; i < len; i++) {
            data[i] = l[i] / r[i];
        }

        return new FloatArraySeries(data);
    }

    @Override
    public FloatSeries mod(FloatSeries s) {
        if (!(s instanceof FloatArraySeries)) {
            return super.add(s);
        }

        int len = size();
        if (len != s.size()) {
            throw new IllegalArgumentException("Another Series size " + s.size() + " is not the same as this size " + len);
        }

        FloatArraySeries as = (FloatArraySeries) s;

        // storing ivars in the local vars for performance
        float[] l = this.data;
        float[] r = as.data;

        float[] data = new float[len];
        for (int i = 0; i < len; i++) {
            data[i] = l[i] % r[i];
        }

        return new FloatArraySeries(data);
    }

    @Override
    public float getFloat(int index) {
        return data[index];
    }

    @Override
    public void copyToFloat(float[] to, int fromOffset, int toOffset, int len) {
        if (fromOffset + len > size()) {
            throw new ArrayIndexOutOfBoundsException(fromOffset + len);
        }

        System.arraycopy(data, fromOffset, to, toOffset, len);
    }

    @Override
    public FloatSeries rangeFloat(int fromInclusive, int toExclusive) {
        return fromInclusive == 0 && toExclusive == size()
                ? this
                : new FloatArrayRangeSeries(data, fromInclusive, toExclusive - fromInclusive);
    }

    @Override
    public FloatSeries materialize() {
        return this;
    }

    @Override
    public float max() {
        return PrimitiveSeriesMinMax.maxOfArray(data, 0, size());
    }

    @Override
    public float min() {
        return PrimitiveSeriesMinMax.minOfArray(data, 0, size());
    }

    @Override
    public double sum() {
        return PrimitiveSeriesSum.sumOfArray(data, 0, size());
    }

    @Override
    public float avg() {
        return PrimitiveSeriesAvg.avgOfArray(data, 0, size());
    }

    @Override
    public float quantile(double q) {
        return Percentiles.ofArray(data, 0, size(), q);
    }

    @Override
    public DoubleSeries cumSum() {
        double[] cumSum = PrimitiveSeriesSum.cumSumOfArray(data, 0, size());
        return new DoubleArraySeries(cumSum);
    }
}
