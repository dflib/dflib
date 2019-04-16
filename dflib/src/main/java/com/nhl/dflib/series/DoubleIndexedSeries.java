package com.nhl.dflib.series;

import com.nhl.dflib.DoubleSeries;
import com.nhl.dflib.IntSeries;

import java.util.Objects;

/**
 * @since 0.6
 */
public class DoubleIndexedSeries extends DoubleBaseSeries {

    private DoubleSeries source;
    private IntSeries includePositions;

    private DoubleSeries materialized;

    public DoubleIndexedSeries(DoubleSeries source, IntSeries includePositions) {
        this.source = Objects.requireNonNull(source);
        this.includePositions = Objects.requireNonNull(includePositions);
    }

    @Override
    public int size() {
        return includePositions != null ? includePositions.size() : materialized.size();
    }

    @Override
    public double getDouble(int index) {
        return materializeDouble().getDouble(index);
    }

    @Override
    public void copyToDouble(double[] to, int fromOffset, int toOffset, int len) {
        materializeDouble().copyToDouble(to, fromOffset, toOffset, len);
    }

    @Override
    public DoubleSeries rangeOpenClosedDouble(int fromInclusive, int toExclusive) {
        return materializeDouble().rangeOpenClosedDouble(fromInclusive, toExclusive);
    }

    @Override
    public DoubleSeries headDouble(int len) {
        return includePositions != null
                ? new DoubleIndexedSeries(source, includePositions.headInt(len))
                : materialized.headDouble(len);
    }

    @Override
    public DoubleSeries tailDouble(int len) {
        return includePositions != null
                ? new DoubleIndexedSeries(source, includePositions.tailInt(len))
                : materialized.tailDouble(len);
    }

    @Override
    public DoubleSeries materializeDouble() {
        if (materialized == null) {
            synchronized (this) {
                if (materialized == null) {
                    materialized = doMaterializeDouble();
                }
            }
        }

        return materialized;
    }

    protected DoubleSeries doMaterializeDouble() {

        int h = includePositions.size();

        double[] data = new double[h];

        for (int i = 0; i < h; i++) {
            int index = includePositions.getInt(i);

            // skipped positions (index < 0) are found in joins
            // TODO: we haven't decided whether NULL == 0 in DoubleSeries yet...
            data[i] = index < 0 ? 0 : source.getDouble(index);
        }

        // reset source reference, allowing to free up memory..
        source = null;
        includePositions = null;

        return new DoubleArraySeries(data);
    }
}
