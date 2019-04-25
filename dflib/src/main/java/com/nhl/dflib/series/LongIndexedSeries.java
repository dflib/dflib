package com.nhl.dflib.series;

import com.nhl.dflib.IntSeries;
import com.nhl.dflib.LongSeries;

import java.util.Objects;

/**
 * @since 0.6
 */
public class LongIndexedSeries extends LongBaseSeries {

    private LongSeries source;
    private IntSeries includePositions;

    private LongSeries materialized;

    public LongIndexedSeries(LongSeries source, IntSeries includePositions) {
        this.source = Objects.requireNonNull(source);
        this.includePositions = Objects.requireNonNull(includePositions);
    }

    @Override
    public int size() {
        return includePositions != null ? includePositions.size() : materialized.size();
    }

    @Override
    public long getLong(int index) {
        return materializeLong().getLong(index);
    }

    @Override
    public void copyToLong(long[] to, int fromOffset, int toOffset, int len) {
        materializeLong().copyToLong(to, fromOffset, toOffset, len);
    }

    @Override
    public LongSeries rangeOpenClosedLong(int fromInclusive, int toExclusive) {
        return materializeLong().rangeOpenClosedLong(fromInclusive, toExclusive);
    }

    @Override
    public LongSeries headLong(int len) {
        return includePositions != null
                ? new LongIndexedSeries(source, includePositions.headInt(len))
                : materialized.headLong(len);
    }

    @Override
    public LongSeries tailLong(int len) {
        return includePositions != null
                ? new LongIndexedSeries(source, includePositions.tailInt(len))
                : materialized.tailLong(len);
    }

    @Override
    public LongSeries materializeLong() {
        if (materialized == null) {
            synchronized (this) {
                if (materialized == null) {
                    materialized = doMaterializeLong();
                }
            }
        }

        return materialized;
    }

    protected LongSeries doMaterializeLong() {

        int h = includePositions.size();

        long[] data = new long[h];

        for (int i = 0; i < h; i++) {
            int index = includePositions.getInt(i);

            // skipped positions (index < 0) are found in joins
            // TODO: we haven't decided whether NULL == 0 in LongSeries yet...
            data[i] = index < 0 ? 0 : source.getLong(index);
        }

        // reset source reference, allowing to free up memory..
        source = null;
        includePositions = null;

        return new LongArraySeries(data);
    }
}
