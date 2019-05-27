package com.nhl.dflib.series;

import com.nhl.dflib.BooleanSeries;
import com.nhl.dflib.IntSeries;
import com.nhl.dflib.Series;
import com.nhl.dflib.collection.BooleanMutableList;
import com.nhl.dflib.collection.IntMutableList;
import com.nhl.dflib.collection.MutableList;
import com.nhl.dflib.collection.UniqueIntMutableList;
import com.nhl.dflib.concat.SeriesConcat;
import com.nhl.dflib.filter.IntPredicate;
import com.nhl.dflib.filter.ValuePredicate;

import java.util.Objects;

/**
 * A base implementation of various boilerplate methods for {@link IntSeries}.
 *
 * @since 0.6
 */
public abstract class IntBaseSeries implements IntSeries {

    @Override
    public Series<Integer> rangeOpenClosed(int fromInclusive, int toExclusive) {
        return rangeOpenClosedInt(fromInclusive, toExclusive);
    }

    @Override
    public Series<Integer> select(IntSeries positions) {

        int h = positions.size();

        int[] data = new int[h];

        for (int i = 0; i < h; i++) {
            int index = positions.getInt(i);

            // "index < 0" (often found in outer joins) indicate nulls.
            // If a null is encountered, we can no longer maintain IntSeries and have to change to Series<Integer>...
            if (index < 0) {
                // TODO: implement NullableIntSeries as a Series of long[], where NULLs are encoded as Integer.MAX_VALUE + 1
                return selectAsObjectSeries(positions);
            }

            data[i] = getInt(index);
        }

        return new IntArraySeries(data);
    }

    private Series<Integer> selectAsObjectSeries(IntSeries positions) {

        int h = positions.size();
        Integer[] data = new Integer[h];

        for (int i = 0; i < h; i++) {
            int index = positions.getInt(i);
            data[i] = index < 0 ? null : getInt(index);
        }

        return new ArraySeries<>(data);
    }

    @Override
    public IntSeries concatInt(IntSeries... other) {

        if (other.length == 0) {
            return this;
        }

        // TODO: use SeriesConcat
        int size = size();
        int h = size;
        for (IntSeries s : other) {
            h += s.size();
        }

        int[] data = new int[h];
        copyToInt(data, 0, 0, size);

        int offset = size;
        for (IntSeries s : other) {
            int len = s.size();
            s.copyToInt(data, 0, offset, len);
            offset += len;
        }

        return new IntArraySeries(data);
    }

    @Override
    public Series<Integer> fillNulls(Integer value) {
        // primitive series has no nulls
        return this;
    }

    @Override
    public Series<Integer> fillNullsFromSeries(Series<? extends Integer> values) {
        // primitive series has no nulls
        return this;
    }

    @Override
    public Series<Integer> fillNullsBackwards() {
        // primitive series has no nulls
        return this;
    }

    @Override
    public Series<Integer> fillNullsForward() {
        // primitive series has no nulls
        return this;
    }

    @Override
    public Series<Integer> head(int len) {
        return headInt(len);
    }

    @Override
    public Series<Integer> tail(int len) {
        return tailInt(len);
    }

    @Override
    public Series<Integer> concat(Series<? extends Integer>... other) {
        // concatenating as Integer... to concat as IntSeries, "concatInt" should be used
        if (other.length == 0) {
            return this;
        }

        // TODO: use SeriesConcat

        Series<Integer>[] combined = new Series[other.length + 1];
        combined[0] = this;
        System.arraycopy(other, 0, combined, 1, other.length);

        return SeriesConcat.concat(combined);
    }

    @Override
    public Series<Integer> materialize() {
        return materializeInt();
    }

    @Override
    public Integer get(int index) {
        return getInt(index);
    }

    @Override
    public void copyTo(Object[] to, int fromOffset, int toOffset, int len) {
        for (int i = 0; i < len; i++) {
            to[toOffset + i] = getInt(i);
        }
    }

    @Override
    public IntSeries indexInt(IntPredicate predicate) {
        IntMutableList filtered = new IntMutableList();

        int len = size();

        for (int i = 0; i < len; i++) {
            if (predicate.test(getInt(i))) {
                filtered.add(i);
            }
        }

        return filtered.toIntSeries();
    }

    @Override
    public IntSeries index(ValuePredicate<Integer> predicate) {
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
    public Series<Integer> replace(BooleanSeries condition, Integer with) {
        return with != null
                ? replaceInt(condition, with)
                : nullify(condition);
    }

    @Override
    public Series<Integer> replaceNoMatch(BooleanSeries condition, Integer with) {
        return with != null
                ? replaceNoMatchInt(condition, with)
                : nullifyNoMatch(condition);
    }

    // TODO: make int versions of replace public?

    private IntSeries replaceInt(BooleanSeries condition, int with) {
        int s = size();
        int r = Math.min(s, condition.size());
        IntMutableList ints = new IntMutableList(s);

        for (int i = 0; i < r; i++) {
            ints.add(condition.getBoolean(i) ? with : getInt(i));
        }

        for (int i = r; i < s; i++) {
            ints.add(getInt(i));
        }

        return ints.toIntSeries();
    }

    private IntSeries replaceNoMatchInt(BooleanSeries condition, int with) {

        int s = size();
        int r = Math.min(s, condition.size());
        IntMutableList ints = new IntMutableList(s);

        for (int i = 0; i < r; i++) {
            ints.add(condition.getBoolean(i) ? getInt(i) : with);
        }

        if (s > r) {
            ints.fill(r, s, with);
        }

        return ints.toIntSeries();
    }

    private Series<Integer> nullify(BooleanSeries condition) {
        int s = size();
        int r = Math.min(s, condition.size());
        MutableList vals = new MutableList(s);

        for (int i = 0; i < r; i++) {
            vals.add(condition.getBoolean(i) ? null : getInt(i));
        }

        for (int i = r; i < s; i++) {
            vals.add(getInt(i));
        }

        return vals.toSeries();
    }

    private Series<Integer> nullifyNoMatch(BooleanSeries condition) {
        int s = size();
        int r = Math.min(s, condition.size());
        MutableList vals = new MutableList(s);

        for (int i = 0; i < r; i++) {
            vals.add(condition.getBoolean(i) ? getInt(i) : null);
        }

        if (s > r) {
            vals.fill(r, s, null);
        }

        return vals.toSeries();
    }

    @Override
    public BooleanSeries eq(Series<Integer> another) {
        int s = size();
        int as = another.size();

        if (s != as) {
            throw new IllegalArgumentException("Another Series size " + as + " is not the same as this size " + s);
        }

        BooleanMutableList bools = new BooleanMutableList(s);

        if (another instanceof IntSeries) {
            IntSeries anotherInt = (IntSeries) another;

            for (int i = 0; i < s; i++) {
                bools.add(getInt(i) == anotherInt.getInt(i));
            }
        } else {
            for (int i = 0; i < s; i++) {
                bools.add(Objects.equals(get(i), another.get(i)));
            }
        }

        return bools.toBooleanSeries();
    }

    @Override
    public BooleanSeries ne(Series<Integer> another) {
        int s = size();
        int as = another.size();

        if (s != as) {
            throw new IllegalArgumentException("Another Series size " + as + " is not the same as this size " + s);
        }

        BooleanMutableList bools = new BooleanMutableList(s);
        if (another instanceof IntSeries) {
            IntSeries anotherInt = (IntSeries) another;

            for (int i = 0; i < s; i++) {
                bools.add(getInt(i) != anotherInt.getInt(i));
            }
        } else {
            for (int i = 0; i < s; i++) {
                bools.add(!Objects.equals(get(i), another.get(i)));
            }
        }

        return bools.toBooleanSeries();
    }

    @Override
    public Series<Integer> unique() {
        return uniqueInt();
    }

    @Override
    public IntSeries uniqueInt() {
        int size = size();
        if (size < 2) {
            return this;
        }

        IntMutableList unique = new UniqueIntMutableList();
        for (int i = 0; i < size; i++) {
            unique.add(get(i));
        }

        return unique.size() < size() ? unique.toIntSeries() : this;
    }

    @Override
    public String toString() {
        return ToString.toString(this);
    }
}
