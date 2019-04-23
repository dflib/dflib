package com.nhl.dflib.series;

import com.nhl.dflib.BooleanSeries;
import com.nhl.dflib.DoubleSeries;
import com.nhl.dflib.IntSeries;
import com.nhl.dflib.Series;
import com.nhl.dflib.collection.BooleanMutableList;
import com.nhl.dflib.collection.DoubleMutableList;
import com.nhl.dflib.collection.IntMutableList;
import com.nhl.dflib.collection.MutableList;
import com.nhl.dflib.concat.SeriesConcat;
import com.nhl.dflib.filter.DoublePredicate;
import com.nhl.dflib.filter.ValuePredicate;

import java.util.Objects;

import static java.util.Arrays.asList;

/**
 * @since 0.6
 */
public abstract class DoubleBaseSeries implements DoubleSeries {

    @Override
    public Series<Double> rangeOpenClosed(int fromInclusive, int toExclusive) {
        return rangeOpenClosedDouble(fromInclusive, toExclusive);
    }

    @Override
    public DoubleSeries selectDouble(IntSeries positions) {
        return new DoubleIndexedSeries(this, positions);
    }

    @Override
    public DoubleSeries concatDouble(DoubleSeries... other) {
        if (other.length == 0) {
            return this;
        }

        int size = size();
        int h = size;
        for (DoubleSeries s : other) {
            h += s.size();
        }

        double[] data = new double[h];
        copyToDouble(data, 0, 0, size);

        int offset = size;
        for (DoubleSeries s : other) {
            int len = s.size();
            s.copyToDouble(data, 0, offset, len);
            offset += len;
        }

        return new DoubleArraySeries(data);
    }

    @Override
    public Series<Double> fillNulls(Double value) {
        // TODO: should we replace zeros?
        return this;
    }

    @Override
    public Series<Double> fillNullsBackwards() {
        // TODO: should we replace zeros?
        return this;
    }

    @Override
    public Series<Double> fillNullsForward() {
        // TODO: should we replace zeros?
        return this;
    }

    @Override
    public Series<Double> head(int len) {
        return headDouble(len);
    }

    @Override
    public Series<Double> tail(int len) {
        return tailDouble(len);
    }

    @Override
    public Series<Double> concat(Series<? extends Double>... other) {
        // concatenating as Integer... to concat as IntSeries, "concatInt" should be used
        if (other.length == 0) {
            return this;
        }

        Series<Double>[] combined = new Series[other.length + 1];
        combined[0] = this;
        System.arraycopy(other, 0, combined, 1, other.length);

        return SeriesConcat.concat(asList(combined));
    }

    @Override
    public Series<Double> materialize() {
        return materializeDouble();
    }

    @Override
    public Double get(int index) {
        return getDouble(index);
    }

    @Override
    public void copyTo(Object[] to, int fromOffset, int toOffset, int len) {
        for (int i = 0; i < len; i++) {
            to[toOffset + i] = getDouble(i);
        }
    }

    @Override
    public Series<Double> select(IntSeries positions) {
        return selectDouble(positions);
    }

    @Override
    public IntSeries indexDouble(DoublePredicate predicate) {
        IntMutableList filtered = new IntMutableList();

        int len = size();

        for (int i = 0; i < len; i++) {
            if (predicate.test(getDouble(i))) {
                filtered.add(i);
            }
        }

        return filtered.toIntSeries();
    }

    @Override
    public IntSeries index(ValuePredicate<Double> predicate) {
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
    public Series<Double> replace(BooleanSeries condition, Double with) {
        return with != null
                ? replaceDouble(condition, with)
                : nullify(condition);
    }

    @Override
    public Series<Double> replaceNoMatch(BooleanSeries condition, Double with) {
        return with != null
                ? replaceNoMatchDouble(condition, with)
                : nullifyNoMatch(condition);
    }

    // TODO: make double versions of replace public?

    private DoubleSeries replaceDouble(BooleanSeries condition, double with) {
        int s = size();
        int r = Math.min(s, condition.size());
        DoubleMutableList doubles = new DoubleMutableList(s);

        for (int i = 0; i < r; i++) {
            doubles.add(condition.getBoolean(i) ? with : getDouble(i));
        }

        for (int i = r; i < s; i++) {
            doubles.add(getDouble(i));
        }

        return doubles.toDoubleSeries();
    }

    private DoubleSeries replaceNoMatchDouble(BooleanSeries condition, double with) {

        int s = size();
        int r = Math.min(s, condition.size());
        DoubleMutableList doubles = new DoubleMutableList(s);

        for (int i = 0; i < r; i++) {
            doubles.add(condition.getBoolean(i) ? getDouble(i) : with);
        }

        if (s > r) {
            doubles.fill(r, s, with);
        }

        return doubles.toDoubleSeries();
    }

    private Series<Double> nullify(BooleanSeries condition) {
        int s = size();
        int r = Math.min(s, condition.size());
        MutableList<Double> vals = new MutableList<>(s);

        for (int i = 0; i < r; i++) {
            vals.add(condition.getBoolean(i) ? null : getDouble(i));
        }

        for (int i = r; i < s; i++) {
            vals.add(getDouble(i));
        }

        return vals.toSeries();
    }

    private Series<Double> nullifyNoMatch(BooleanSeries condition) {
        int s = size();
        int r = Math.min(s, condition.size());
        MutableList<Double> vals = new MutableList<>(s);

        for (int i = 0; i < r; i++) {
            vals.add(condition.getBoolean(i) ? getDouble(i) : null);
        }

        if (s > r) {
            vals.fill(r, s, null);
        }

        return vals.toSeries();
    }

    @Override
    public BooleanSeries eq(Series<Double> another) {
        int s = size();
        int as = another.size();

        if (s != as) {
            throw new IllegalArgumentException("Another Series size " + as + " is not the same as this size " + s);
        }

        BooleanMutableList bools = new BooleanMutableList(s);

        if (another instanceof DoubleSeries) {
            DoubleSeries anotherDouble = (DoubleSeries) another;

            for (int i = 0; i < s; i++) {
                bools.add(getDouble(i) == anotherDouble.getDouble(i));
            }
        } else {
            for (int i = 0; i < s; i++) {
                bools.add(Objects.equals(get(i), another.get(i)));
            }
        }

        return bools.toBooleanSeries();
    }

    @Override
    public BooleanSeries ne(Series<Double> another) {
        int s = size();
        int as = another.size();

        if (s != as) {
            throw new IllegalArgumentException("Another Series size " + as + " is not the same as this size " + s);
        }

        BooleanMutableList bools = new BooleanMutableList(s);
        if (another instanceof DoubleSeries) {
            DoubleSeries anotherDouble = (DoubleSeries) another;

            for (int i = 0; i < s; i++) {
                bools.add(getDouble(i) != anotherDouble.getDouble(i));
            }
        } else {
            for (int i = 0; i < s; i++) {
                bools.add(!Objects.equals(get(i), another.get(i)));
            }
        }

        return bools.toBooleanSeries();
    }
}
