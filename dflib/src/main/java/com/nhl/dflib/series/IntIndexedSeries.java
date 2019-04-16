package com.nhl.dflib.series;

import com.nhl.dflib.IntSeries;

import java.util.Objects;

/**
 * @since 0.6
 */
public class IntIndexedSeries extends IntBaseSeries {

    private IntSeries source;
    private IntSeries includePositions;

    private IntSeries materialized;

    public IntIndexedSeries(IntSeries source, IntSeries includePositions) {
        this.source = Objects.requireNonNull(source);
        this.includePositions = Objects.requireNonNull(includePositions);
    }

    @Override
    public int size() {
        return includePositions != null ? includePositions.size() : materialized.size();
    }

    @Override
    public int getInt(int index) {
        return materializeInt().getInt(index);
    }

    @Override
    public void copyToInt(int[] to, int fromOffset, int toOffset, int len) {
        materializeInt().copyToInt(to, fromOffset, toOffset, len);
    }

    @Override
    public IntSeries rangeOpenClosedInt(int fromInclusive, int toExclusive) {
        return materializeInt().rangeOpenClosedInt(fromInclusive, toExclusive);
    }

    @Override
    public IntSeries headInt(int len) {
        return includePositions != null
                ? new IntIndexedSeries(source, includePositions.headInt(len))
                : materialized.headInt(len);
    }

    @Override
    public IntSeries tailInt(int len) {
        return includePositions != null
                ? new IntIndexedSeries(source, includePositions.tailInt(len))
                : materialized.tailInt(len);
    }

    @Override
    public IntSeries materializeInt() {
        if (materialized == null) {
            synchronized (this) {
                if (materialized == null) {
                    materialized = doMaterializeInt();
                }
            }
        }

        return materialized;
    }

    protected IntSeries doMaterializeInt() {

        int h = includePositions.size();

        int[] data = new int[h];

        for (int i = 0; i < h; i++) {
            int index = includePositions.getInt(i);

            // skipped positions (index < 0) are found in joins
            // TODO: we haven't decided whether NULL == 0 in IntSeries yet...
            data[i] = index < 0 ? 0 : source.getInt(index);
        }

        // reset source reference, allowing to free up memory..
        source = null;
        includePositions = null;

        return new IntArraySeries(data);
    }
}
