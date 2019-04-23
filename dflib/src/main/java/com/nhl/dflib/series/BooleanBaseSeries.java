package com.nhl.dflib.series;

import com.nhl.dflib.BooleanSeries;
import com.nhl.dflib.IntSeries;
import com.nhl.dflib.Series;
import com.nhl.dflib.collection.BooleanMutableList;
import com.nhl.dflib.collection.IntMutableList;
import com.nhl.dflib.collection.MutableList;
import com.nhl.dflib.concat.SeriesConcat;
import com.nhl.dflib.filter.ValuePredicate;

import static java.util.Arrays.asList;

/**
 * @since 0.6
 */
public abstract class BooleanBaseSeries implements BooleanSeries {

    @Override
    public Series<Boolean> rangeOpenClosed(int fromInclusive, int toExclusive) {
        return rangeOpenClosedBoolean(fromInclusive, toExclusive);
    }

    @Override
    public BooleanSeries selectBoolean(IntSeries positions) {
        return new BooleanIndexedSeries(this, positions);
    }

    @Override
    public BooleanSeries concatBoolean(BooleanSeries... other) {
        if (other.length == 0) {
            return this;
        }

        int size = size();
        int h = size;
        for (BooleanSeries s : other) {
            h += s.size();
        }

        boolean[] data = new boolean[h];
        copyToBoolean(data, 0, 0, size);

        int offset = size;
        for (BooleanSeries s : other) {
            int len = s.size();
            s.copyToBoolean(data, 0, offset, len);
            offset += len;
        }

        return new BooleanArraySeries(data);
    }

    @Override
    public Series<Boolean> fillNulls(Boolean value) {
        return this;
    }

    @Override
    public Series<Boolean> fillNullsBackwards() {
        return this;
    }

    @Override
    public Series<Boolean> fillNullsForward() {
        return this;
    }

    @Override
    public Series<Boolean> head(int len) {
        return headBoolean(len);
    }

    @Override
    public Series<Boolean> tail(int len) {
        return tailBoolean(len);
    }

    @Override
    public Series<Boolean> concat(Series<? extends Boolean>... other) {
        // concatenating as Integer... to concat as IntSeries, "concatInt" should be used
        if (other.length == 0) {
            return this;
        }

        Series<Boolean>[] combined = new Series[other.length + 1];
        combined[0] = this;
        System.arraycopy(other, 0, combined, 1, other.length);

        return SeriesConcat.concat(asList(combined));
    }

    @Override
    public Series<Boolean> materialize() {
        return materializeBoolean();
    }

    @Override
    public Boolean get(int index) {
        return getBoolean(index);
    }

    @Override
    public void copyTo(Object[] to, int fromOffset, int toOffset, int len) {
        for (int i = 0; i < len; i++) {
            to[toOffset + i] = getBoolean(i);
        }
    }

    @Override
    public Series<Boolean> select(IntSeries positions) {
        return selectBoolean(positions);
    }

    @Override
    public IntSeries indexTrue() {
        IntMutableList filtered = new IntMutableList();

        int len = size();

        for (int i = 0; i < len; i++) {
            if (getBoolean(i)) {
                filtered.add(i);
            }
        }

        return filtered.toIntSeries();
    }

    @Override
    public IntSeries indexFalse() {
        IntMutableList filtered = new IntMutableList();

        int len = size();

        for (int i = 0; i < len; i++) {
            if (!getBoolean(i)) {
                filtered.add(i);
            }
        }

        return filtered.toIntSeries();
    }

    @Override
    public IntSeries index(ValuePredicate<Boolean> predicate) {
        IntMutableList index = new IntMutableList();

        int len = size();

        for (int i = 0; i < len; i++) {
            if (predicate.test(get(i))) {
                index.add(i);
            }
        }

        return index.toIntSeries();
    }

    @Override
    public Series<Boolean> replace(BooleanSeries condition, Boolean with) {
        return with != null
                ? replaceBoolean(condition, with)
                : nullify(condition);
    }

    @Override
    public Series<Boolean> replaceNoMatch(BooleanSeries condition, Boolean with) {
        return with != null
                ? replaceNoMatchBoolean(condition, with)
                : nullifyNoMatch(condition);
    }

    // TODO: make bool versions of replace public?

    private BooleanSeries replaceBoolean(BooleanSeries condition, boolean with) {
        int s = size();
        int r = Math.min(s, condition.size());
        BooleanMutableList bools = new BooleanMutableList(s);

        for (int i = 0; i < r; i++) {
            bools.add(condition.getBoolean(i) ? with : getBoolean(i));
        }

        for (int i = r; i < s; i++) {
            bools.add(getBoolean(i));
        }

        return bools.toBooleanSeries();
    }

    private BooleanSeries replaceNoMatchBoolean(BooleanSeries condition, boolean with) {

        int s = size();
        int r = Math.min(s, condition.size());
        BooleanMutableList bools = new BooleanMutableList(s);

        for (int i = 0; i < r; i++) {
            bools.add(condition.getBoolean(i) ? getBoolean(i) : with);
        }

        if (s > r) {
            bools.fill(r, s, with);
        }

        return bools.toBooleanSeries();
    }

    private Series<Boolean> nullify(BooleanSeries condition) {
        int s = size();
        int r = Math.min(s, condition.size());
        MutableList vals = new MutableList(s);

        for (int i = 0; i < r; i++) {
            vals.add(condition.getBoolean(i) ? null : getBoolean(i));
        }

        for (int i = r; i < s; i++) {
            vals.add(getBoolean(i));
        }

        return vals.toSeries();
    }

    private Series<Boolean> nullifyNoMatch(BooleanSeries condition) {
        int s = size();
        int r = Math.min(s, condition.size());
        MutableList vals = new MutableList(s);

        for (int i = 0; i < r; i++) {
            vals.add(condition.getBoolean(i) ? getBoolean(i) : null);
        }

        if (s > r) {
            vals.fill(r, s, null);
        }

        return vals.toSeries();
    }
}
