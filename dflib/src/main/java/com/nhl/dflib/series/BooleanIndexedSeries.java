package com.nhl.dflib.series;

import com.nhl.dflib.BooleanSeries;
import com.nhl.dflib.IntSeries;

import java.util.Objects;

/**
 * @since 0.6
 */
public class BooleanIndexedSeries extends BooleanBaseSeries {

    private BooleanSeries source;
    private IntSeries includePositions;

    private BooleanSeries materialized;

    public BooleanIndexedSeries(BooleanSeries source, IntSeries includePositions) {
        this.source = Objects.requireNonNull(source);
        this.includePositions = Objects.requireNonNull(includePositions);
    }

    @Override
    public int size() {
        return includePositions != null ? includePositions.size() : materialized.size();
    }

    @Override
    public boolean getBoolean(int index) {
        return materializeBoolean().getBoolean(index);
    }

    @Override
    public void copyToBoolean(boolean[] to, int fromOffset, int toOffset, int len) {
        materializeBoolean().copyToBoolean(to, fromOffset, toOffset, len);
    }

    @Override
    public BooleanSeries rangeOpenClosedBoolean(int fromInclusive, int toExclusive) {
        return materializeBoolean().rangeOpenClosedBoolean(fromInclusive, toExclusive);
    }

    @Override
    public BooleanSeries headBoolean(int len) {
        return includePositions != null
                ? new BooleanIndexedSeries(source, includePositions.headInt(len))
                : materialized.headBoolean(len);
    }

    @Override
    public BooleanSeries tailBoolean(int len) {
        return includePositions != null
                ? new BooleanIndexedSeries(source, includePositions.tailInt(len))
                : materialized.tailBoolean(len);
    }

    @Override
    public BooleanSeries materializeBoolean() {
        if (materialized == null) {
            synchronized (this) {
                if (materialized == null) {
                    materialized = doMaterializeBoolean();
                }
            }
        }

        return materialized;
    }

    protected BooleanSeries doMaterializeBoolean() {

        int h = includePositions.size();

        boolean[] data = new boolean[h];

        for (int i = 0; i < h; i++) {
            int index = includePositions.getInt(i);

            // skipped positions (index < 0) are found in joins
            // TODO: we haven't decided whether NULL == false in BooleanSeries yet...
            data[i] = index < 0 ? false : source.getBoolean(index);
        }

        // reset source reference, allowing to free up memory..
        source = null;
        includePositions = null;

        return new BooleanArraySeries(data);
    }
}
