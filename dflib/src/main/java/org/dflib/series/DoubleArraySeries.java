package org.dflib.series;

import org.dflib.DoubleSeries;
import org.dflib.agg.Average;
import org.dflib.agg.CumSum;
import org.dflib.agg.Max;
import org.dflib.agg.Min;
import org.dflib.agg.Percentiles;
import org.dflib.agg.Sum;

public class DoubleArraySeries extends DoubleBaseSeries {

    private final double[] data;

    public DoubleArraySeries(double... data) {
        this.data = data;
    }

    @Override
    public int size() {
        return data.length;
    }

    @Override
    public DoubleSeries add(DoubleSeries s) {
        if (!(s instanceof DoubleArraySeries as)) {
            return super.add(s);
        }

        int len = size();
        if (len != s.size()) {
            throw new IllegalArgumentException("Another Series size " + s.size() + " is not the same as this size " + len);
        }

        // storing ivars in the local vars for performance
        double[] l = this.data;
        double[] r = as.data;

        double[] data = new double[len];
        for (int i = 0; i < len; i++) {
            data[i] = l[i] + r[i];
        }

        return new DoubleArraySeries(data);
    }

    @Override
    public DoubleSeries sub(DoubleSeries s) {
        if (!(s instanceof DoubleArraySeries as)) {
            return super.add(s);
        }

        int len = size();
        if (len != s.size()) {
            throw new IllegalArgumentException("Another Series size " + s.size() + " is not the same as this size " + len);
        }

        // storing ivars in the local vars for performance
        double[] l = this.data;
        double[] r = as.data;

        double[] data = new double[len];
        for (int i = 0; i < len; i++) {
            data[i] = l[i] - r[i];
        }

        return new DoubleArraySeries(data);
    }

    @Override
    public DoubleSeries mul(DoubleSeries s) {
        if (!(s instanceof DoubleArraySeries as)) {
            return super.add(s);
        }

        int len = size();
        if (len != s.size()) {
            throw new IllegalArgumentException("Another Series size " + s.size() + " is not the same as this size " + len);
        }

        // storing ivars in the local vars for performance
        double[] l = this.data;
        double[] r = as.data;

        double[] data = new double[len];
        for (int i = 0; i < len; i++) {
            data[i] = l[i] * r[i];
        }

        return new DoubleArraySeries(data);
    }

    @Override
    public DoubleSeries div(DoubleSeries s) {
        if (!(s instanceof DoubleArraySeries as)) {
            return super.add(s);
        }

        int len = size();
        if (len != s.size()) {
            throw new IllegalArgumentException("Another Series size " + s.size() + " is not the same as this size " + len);
        }

        // storing ivars in the local vars for performance
        double[] l = this.data;
        double[] r = as.data;

        double[] data = new double[len];
        for (int i = 0; i < len; i++) {
            data[i] = l[i] / r[i];
        }

        return new DoubleArraySeries(data);
    }

    @Override
    public DoubleSeries mod(DoubleSeries s) {
        if (!(s instanceof DoubleArraySeries as)) {
            return super.add(s);
        }

        int len = size();
        if (len != s.size()) {
            throw new IllegalArgumentException("Another Series size " + s.size() + " is not the same as this size " + len);
        }

        // storing ivars in the local vars for performance
        double[] l = this.data;
        double[] r = as.data;

        double[] data = new double[len];
        for (int i = 0; i < len; i++) {
            data[i] = l[i] % r[i];
        }

        return new DoubleArraySeries(data);
    }

    @Override
    public double getDouble(int index) {
        return data[index];
    }

    @Override
    public void copyToDouble(double[] to, int fromOffset, int toOffset, int len) {
        if (fromOffset + len > size()) {
            throw new ArrayIndexOutOfBoundsException(fromOffset + len);
        }

        System.arraycopy(data, fromOffset, to, toOffset, len);
    }

    @Override
    public DoubleSeries rangeDouble(int fromInclusive, int toExclusive) {
        return fromInclusive == 0 && toExclusive == size()
                ? this
                : new DoubleArrayRangeSeries(data, fromInclusive, toExclusive - fromInclusive);
    }

    @Override
    public DoubleSeries materialize() {
        return this;
    }

    @Override
    public double max() {
        return Max.ofArray(data, 0, size());
    }

    @Override
    public double min() {
        return Min.ofArray(data, 0, size());
    }

    @Override
    public double sum() {
        return Sum.ofArray(data, 0, size());
    }

    @Override
    public double avg() {
        return Average.ofArray(data, 0, size());
    }

    @Override
    public double quantile(double q) {
        return Percentiles.ofArray(data, 0, size(), q);
    }

    @Override
    public DoubleSeries cumSum() {
        double[] cumSum = CumSum.ofArray(data, 0, size());
        return new DoubleArraySeries(cumSum);
    }
}
