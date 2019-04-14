package com.nhl.dflib.series;

import com.nhl.dflib.IntSeries;
import com.nhl.dflib.Series;
import com.nhl.dflib.collection.IntMutableList;
import com.nhl.dflib.concat.SeriesConcat;
import com.nhl.dflib.filter.IntPredicate;
import com.nhl.dflib.filter.ValuePredicate;

import static java.util.Arrays.asList;

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
    public IntSeries selectInt(IntSeries positions) {
        return new IntIndexedSeries(this, positions);
    }

    @Override
    public IntSeries concatInt(IntSeries... other) {
        if (other.length == 0) {
            return this;
        }

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
        // TODO: should we replace zeros?
        return this;
    }

    @Override
    public Series<Integer> fillNullsBackwards() {
        // TODO: should we replace zeros?
        return this;
    }

    @Override
    public Series<Integer> fillNullsForward() {
        // TODO: should we replace zeros?
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

        Series<Integer>[] combined = new Series[other.length + 1];
        combined[0] = this;
        System.arraycopy(other, 0, combined, 1, other.length);

        return SeriesConcat.concat(asList(combined));
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
    public Series<Integer> select(IntSeries positions) {
        return selectInt(positions);
    }

    @Override
    public IntSeries filterInt(IntPredicate predicate) {
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
    public IntSeries filter(ValuePredicate<Integer> predicate) {
        IntMutableList index = new IntMutableList();

        int len = size();

        for (int i = 0; i < len; i++) {
            if (predicate.test(get(i))) {
                index.add(i);
            }
        }

        return index.toIntSeries();
    }
}
