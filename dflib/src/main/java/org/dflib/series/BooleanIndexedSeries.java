package org.dflib.series;

import org.dflib.BooleanSeries;
import org.dflib.IntSeries;
import org.dflib.Series;
import org.dflib.builder.BoolBuilder;

import java.util.Objects;

/**
 * A lazily-resolved BooleanSeries that is a subset of another IntSeries based on an IntSeries index. Most
 * operations are implemented as read-through on the underlying Series and do not cause "materialization".
 */
public class BooleanIndexedSeries extends BooleanBaseSeries {

    protected volatile Raw raw;
    protected volatile BooleanSeries materialized;

    public static Series<Boolean> of(BooleanSeries source, IntSeries includePositions) {

        // check for negative indices (that are found in joins) and return either an IntSeries or a Series<Integer>.
        int len = includePositions.size();
        for (int i = 0; i < len; i++) {
            if (includePositions.getInt(i) < 0) {
                return new IndexedSeries<>(source, includePositions);
            }
        }

        return new BooleanIndexedSeries(source, includePositions);
    }

    public BooleanIndexedSeries(BooleanSeries source, IntSeries includePositions) {
        this.raw = new Raw(source, includePositions);
    }

    public boolean isMaterialized() {
        return materialized != null;
    }

    @Override
    public int size() {
        Raw raw = this.raw;
        return raw != null ? raw.size() : materialized.size();
    }

    @Override
    public boolean getBool(int index) {
        Raw raw = this.raw;
        return raw != null ? raw.getBool(index) : materialized.getBool(index);
    }

    @Override
    public void copyToBool(boolean[] to, int fromOffset, int toOffset, int len) {
        materialize().copyToBool(to, fromOffset, toOffset, len);
    }

    @Override
    public BooleanSeries rangeBool(int fromInclusive, int toExclusive) {
        Raw raw = this.raw;
        return raw != null
                ? new BooleanIndexedSeries(raw.source, raw.includePositions.rangeInt(fromInclusive, toExclusive))
                : materialized.rangeBool(fromInclusive, toExclusive);
    }

    @Override
    public BooleanSeries materialize() {
        if (materialized == null) {
            synchronized (this) {
                if (materialized == null) {
                    materialized = raw.materialize();

                    // reset source reference, allowing to free up memory
                    raw = null;
                }
            }
        }

        return materialized;
    }

    @Override
    public int firstTrue() {
        int size = size();
        for (int i = 0; i < size; i++) {
            if (getBool(i)) {
                return i;
            }
        }
        return -1;
    }

    @Override
    public int firstFalse() {
        int size = size();
        for (int i = 0; i < size; i++) {
            if (!getBool(i)) {
                return i;
            }
        }
        return -1;
    }

    @Override
    public int countTrue() {
        return materialize().countTrue();
    }

    @Override
    public int countFalse() {
        return materialize().countFalse();
    }

    @Override
    public IntSeries cumSum() {
        return materialize().cumSum();
    }

    protected static class Raw {
        final BooleanSeries source;
        final IntSeries includePositions;

        Raw(BooleanSeries source, IntSeries includePositions) {
            this.source = Objects.requireNonNull(source);
            this.includePositions = Objects.requireNonNull(includePositions);
        }

        int size() {
            return includePositions.size();
        }

        boolean getBool(int index) {
            return source.getBool(includePositions.getInt(index));
        }

        BooleanSeries materialize() {
            int h = includePositions.size();
            return BoolBuilder.buildSeries(this::getBool, h);
        }
    }
}
