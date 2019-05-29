package com.nhl.dflib.series;

import com.nhl.dflib.BooleanSeries;
import com.nhl.dflib.IntSeries;
import com.nhl.dflib.LongPredicate;
import com.nhl.dflib.LongSeries;
import com.nhl.dflib.Series;
import com.nhl.dflib.ValueMapper;
import com.nhl.dflib.ValuePredicate;
import com.nhl.dflib.collection.BooleanMutableList;
import com.nhl.dflib.collection.IntMutableList;
import com.nhl.dflib.collection.LongMutableList;
import com.nhl.dflib.collection.MutableList;
import com.nhl.dflib.collection.UniqueLongMutableList;
import com.nhl.dflib.concat.SeriesConcat;

import java.util.Objects;

/**
 * @since 0.6
 */
public abstract class LongBaseSeries implements LongSeries {

    @Override
    public <V> Series<V> map(ValueMapper<Long, V> mapper) {
        return new ColumnMappedSeries<>(this, mapper);
    }

    @Override
    public Series<Long> rangeOpenClosed(int fromInclusive, int toExclusive) {
        return rangeOpenClosedLong(fromInclusive, toExclusive);
    }

    @Override
    public Series<Long> select(IntSeries positions) {

        int h = positions.size();

        long[] data = new long[h];

        for (int i = 0; i < h; i++) {
            int index = positions.getInt(i);

            // "index < 0" (often found in outer joins) indicate nulls.
            // If a null is encountered, we can no longer maintain primitive and have to change to Series<Long>...
            if (index < 0) {
                return selectAsObjectSeries(positions);
            }

            data[i] = getLong(index);
        }

        return new LongArraySeries(data);
    }

    private Series<Long> selectAsObjectSeries(IntSeries positions) {

        int h = positions.size();
        Long[] data = new Long[h];

        for (int i = 0; i < h; i++) {
            int index = positions.getInt(i);
            data[i] = index < 0 ? null : getLong(index);
        }

        return new ArraySeries<>(data);
    }

    @Override
    public LongSeries concatLong(LongSeries... other) {
        if (other.length == 0) {
            return this;
        }

        // TODO: use SeriesConcat

        int size = size();
        int h = size;
        for (LongSeries s : other) {
            h += s.size();
        }

        long[] data = new long[h];
        copyToLong(data, 0, 0, size);

        int offset = size;
        for (LongSeries s : other) {
            int len = s.size();
            s.copyToLong(data, 0, offset, len);
            offset += len;
        }

        return new LongArraySeries(data);
    }

    @Override
    public Series<Long> fillNulls(Long value) {
        // primitive series has no nulls
        return this;
    }

    @Override
    public Series<Long> fillNullsFromSeries(Series<? extends Long> values) {
        // primitive series has no nulls
        return this;
    }

    @Override
    public Series<Long> fillNullsBackwards() {
        // primitive series has no nulls
        return this;
    }

    @Override
    public Series<Long> fillNullsForward() {
        // primitive series has no nulls
        return this;
    }

    @Override
    public Series<Long> head(int len) {
        return headLong(len);
    }

    @Override
    public Series<Long> tail(int len) {
        return tailLong(len);
    }

    @Override
    public Series<Long> concat(Series<? extends Long>... other) {
        // concatenating as Double... to concat as DoubleServies, "concatDouble" should be used
        if (other.length == 0) {
            return this;
        }

        // TODO: use SeriesConcat

        Series<Long>[] combined = new Series[other.length + 1];
        combined[0] = this;
        System.arraycopy(other, 0, combined, 1, other.length);

        return SeriesConcat.concat(combined);
    }

    @Override
    public Series<Long> materialize() {
        return materializeLong();
    }

    @Override
    public Long get(int index) {
        return getLong(index);
    }

    @Override
    public void copyTo(Object[] to, int fromOffset, int toOffset, int len) {
        for (int i = 0; i < len; i++) {
            to[toOffset + i] = getLong(i);
        }
    }

    @Override
    public IntSeries indexLong(LongPredicate predicate) {
        IntMutableList filtered = new IntMutableList();

        int len = size();

        for (int i = 0; i < len; i++) {
            if (predicate.test(getLong(i))) {
                filtered.add(i);
            }
        }

        return filtered.toIntSeries();
    }

    @Override
    public IntSeries index(ValuePredicate<Long> predicate) {
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
    public Series<Long> replace(BooleanSeries condition, Long with) {
        return with != null
                ? replaceLong(condition, with)
                : nullify(condition);
    }

    @Override
    public Series<Long> replaceNoMatch(BooleanSeries condition, Long with) {
        return with != null
                ? replaceNoMatchLong(condition, with)
                : nullifyNoMatch(condition);
    }

    // TODO: make long versions of 'replace' public?

    private LongSeries replaceLong(BooleanSeries condition, long with) {
        int s = size();
        int r = Math.min(s, condition.size());
        LongMutableList longs = new LongMutableList(s);

        for (int i = 0; i < r; i++) {
            longs.add(condition.getBoolean(i) ? with : getLong(i));
        }

        for (int i = r; i < s; i++) {
            longs.add(getLong(i));
        }

        return longs.toLongSeries();
    }

    private LongSeries replaceNoMatchLong(BooleanSeries condition, long with) {

        int s = size();
        int r = Math.min(s, condition.size());
        LongMutableList longs = new LongMutableList(s);

        for (int i = 0; i < r; i++) {
            longs.add(condition.getBoolean(i) ? getLong(i) : with);
        }

        if (s > r) {
            longs.fill(r, s, with);
        }

        return longs.toLongSeries();
    }

    private Series<Long> nullify(BooleanSeries condition) {
        int s = size();
        int r = Math.min(s, condition.size());
        MutableList<Long> vals = new MutableList<>(s);

        for (int i = 0; i < r; i++) {
            vals.add(condition.getBoolean(i) ? null : getLong(i));
        }

        for (int i = r; i < s; i++) {
            vals.add(getLong(i));
        }

        return vals.toSeries();
    }

    private Series<Long> nullifyNoMatch(BooleanSeries condition) {
        int s = size();
        int r = Math.min(s, condition.size());
        MutableList<Long> vals = new MutableList<>(s);

        for (int i = 0; i < r; i++) {
            vals.add(condition.getBoolean(i) ? getLong(i) : null);
        }

        if (s > r) {
            vals.fill(r, s, null);
        }

        return vals.toSeries();
    }

    @Override
    public BooleanSeries eq(Series<Long> another) {
        int s = size();
        int as = another.size();

        if (s != as) {
            throw new IllegalArgumentException("Another Series size " + as + " is not the same as this size " + s);
        }

        BooleanMutableList bools = new BooleanMutableList(s);

        if (another instanceof LongSeries) {
            LongSeries anotherLong = (LongSeries) another;

            for (int i = 0; i < s; i++) {
                bools.add(getLong(i) == anotherLong.getLong(i));
            }
        } else {
            for (int i = 0; i < s; i++) {
                bools.add(Objects.equals(get(i), another.get(i)));
            }
        }

        return bools.toBooleanSeries();
    }

    @Override
    public BooleanSeries ne(Series<Long> another) {
        int s = size();
        int as = another.size();

        if (s != as) {
            throw new IllegalArgumentException("Another Series size " + as + " is not the same as this size " + s);
        }

        BooleanMutableList bools = new BooleanMutableList(s);
        if (another instanceof LongSeries) {
            LongSeries anotherLong = (LongSeries) another;

            for (int i = 0; i < s; i++) {
                bools.add(getLong(i) != anotherLong.getLong(i));
            }
        } else {
            for (int i = 0; i < s; i++) {
                bools.add(!Objects.equals(get(i), another.get(i)));
            }
        }

        return bools.toBooleanSeries();
    }

    @Override
    public Series<Long> unique() {
        return uniqueLong();
    }

    @Override
    public LongSeries uniqueLong() {
        int size = size();
        if (size < 2) {
            return this;
        }

        LongMutableList unique = new UniqueLongMutableList();
        for (int i = 0; i < size; i++) {
            unique.add(get(i));
        }

        return unique.size() < size() ? unique.toLongSeries() : this;
    }

    @Override
    public String toString() {
        return ToString.toString(this);
    }
}
