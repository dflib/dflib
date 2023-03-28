package com.nhl.dflib.series;

import com.nhl.dflib.*;
import com.nhl.dflib.builder.BoolAccum;
import com.nhl.dflib.builder.DoubleAccum;
import com.nhl.dflib.builder.IntAccum;
import com.nhl.dflib.builder.ObjectAccum;
import com.nhl.dflib.builder.UniqueDoubleAccum;
import com.nhl.dflib.concat.SeriesConcat;
import com.nhl.dflib.groupby.SeriesGrouper;
import com.nhl.dflib.map.Mapper;
import com.nhl.dflib.sample.Sampler;
import com.nhl.dflib.sort.SeriesSorter;

import java.util.Arrays;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Objects;
import java.util.Random;
import java.util.Set;

/**
 * @since 0.6
 */
public abstract class DoubleBaseSeries implements DoubleSeries {

    @Override
    public DataFrame map(Index resultColumns, ValueToRowMapper<Double> mapper) {
        return Mapper.map(this, resultColumns, mapper);
    }

    @Override
    public Series<Double> rangeOpenClosed(int fromInclusive, int toExclusive) {
        return rangeOpenClosedDouble(fromInclusive, toExclusive);
    }

    @Override
    public Series<Double> select(IntSeries positions) {

        int h = positions.size();

        double[] data = new double[h];

        for (int i = 0; i < h; i++) {
            int index = positions.getInt(i);

            // "index < 0" (often found in outer joins) indicate nulls.
            // If a null is encountered, we can no longer maintain primitive and have to change to Series<Double>...
            if (index < 0) {
                return selectAsObjectSeries(positions);
            }

            data[i] = getDouble(index);
        }

        return new DoubleArraySeries(data);
    }

    @Override
    public Series<Double> select(Condition condition) {
        return selectDouble(condition);
    }

    @Override
    public Series<Double> select(ValuePredicate<Double> p) {
        return selectDouble(p::test);
    }

    @Override
    public DoubleSeries selectDouble(Condition condition) {
        return selectDouble(condition.eval(this));
    }

    @Override
    public DoubleSeries selectDouble(DoublePredicate p) {
        DoubleAccum filtered = new DoubleAccum();

        int len = size();

        for (int i = 0; i < len; i++) {
            double v = getDouble(i);
            if (p.test(v)) {
                filtered.pushDouble(v);
            }
        }

        return filtered.toSeries();
    }

    @Override
    public DoubleSeries selectDouble(BooleanSeries positions) {
        int s = size();
        int ps = positions.size();

        if (s != ps) {
            throw new IllegalArgumentException("Positions size " + ps + " is not the same as this size " + s);
        }

        DoubleAccum data = new DoubleAccum();

        for (int i = 0; i < size(); i++) {
            if (positions.getBool(i)) {
                data.pushDouble(getDouble(i));
            }
        }

        return data.toSeries();
    }

    @Override
    public Series<Double> select(BooleanSeries positions) {
        return selectDouble(positions);
    }

    @Override
    public DoubleSeries sortDouble() {
        int size = size();
        double[] sorted = new double[size];
        copyToDouble(sorted, 0, 0, size);

        // TODO: use "parallelSort" ?
        Arrays.sort(sorted);

        return new DoubleArraySeries(sorted);
    }

    @Override
    public DoubleSeries sort(Sorter... sorters) {
        return selectAsDoubleSeries(new SeriesSorter<>(this).sortIndex(sorters));
    }

    // TODO: implement 'sortDouble(DoubleComparator)' similar to how IntBaseSeries does "sortInt(IntComparator)"
    //   Reimplement this method to delegate to 'sortDouble'
    @Override
    public DoubleSeries sort(Comparator<? super Double> comparator) {
        return selectAsDoubleSeries(new SeriesSorter<>(this).sortIndex(comparator));
    }

    private DoubleSeries selectAsDoubleSeries(IntSeries positions) {

        int h = positions.size();

        double[] data = new double[h];

        for (int i = 0; i < h; i++) {
            // unlike SelectSeries, we do not expect negative ints in the index.
            // So if it happens, let it fall through to "getDouble()" and fail there
            int index = positions.getInt(i);
            data[i] = getDouble(index);
        }

        return new DoubleArraySeries(data);
    }

    private Series<Double> selectAsObjectSeries(IntSeries positions) {

        int h = positions.size();
        Double[] data = new Double[h];

        for (int i = 0; i < h; i++) {
            int index = positions.getInt(i);
            data[i] = index < 0 ? null : getDouble(index);
        }

        return new ArraySeries<>(data);
    }

    @Override
    public DoubleSeries concatDouble(DoubleSeries... other) {
        if (other.length == 0) {
            return this;
        }

        // TODO: use SeriesConcat

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
        // primitive series has no nulls
        return this;
    }

    @Override
    public Series<Double> fillNullsFromSeries(Series<? extends Double> values) {
        // primitive series has no nulls
        return this;
    }

    @Override
    public Series<Double> fillNullsBackwards() {
        // primitive series has no nulls
        return this;
    }

    @Override
    public Series<Double> fillNullsForward() {
        // primitive series has no nulls
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

    @SafeVarargs
    @Override
    public final Series<Double> concat(Series<? extends Double>... other) {
        // concatenating as Double... to concat as DoubleSeries, "concatDouble" should be used
        if (other.length == 0) {
            return this;
        }

        // TODO: use SeriesConcat

        Series<Double>[] combined = new Series[other.length + 1];
        combined[0] = this;
        System.arraycopy(other, 0, combined, 1, other.length);

        return SeriesConcat.concat(combined);
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
    public IntSeries indexDouble(DoublePredicate predicate) {
        IntAccum index = new IntAccum();

        int len = size();

        for (int i = 0; i < len; i++) {
            if (predicate.test(getDouble(i))) {
                index.pushInt(i);
            }
        }

        return index.toSeries();
    }

    @Override
    public IntSeries index(ValuePredicate<Double> predicate) {
        return indexDouble(predicate::test);
    }

    @Override
    public BooleanSeries locateDouble(DoublePredicate predicate) {
        int len = size();

        BoolAccum matches = new BoolAccum(len);

        for (int i = 0; i < len; i++) {
            matches.pushBool(predicate.test(getDouble(i)));
        }

        return matches.toSeries();
    }

    @Override
    public BooleanSeries locate(ValuePredicate<Double> predicate) {
        return locateDouble(predicate::test);
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
        DoubleAccum doubles = new DoubleAccum(s);

        for (int i = 0; i < r; i++) {
            doubles.pushDouble(condition.getBool(i) ? with : getDouble(i));
        }

        for (int i = r; i < s; i++) {
            doubles.pushDouble(getDouble(i));
        }

        return doubles.toSeries();
    }

    private DoubleSeries replaceNoMatchDouble(BooleanSeries condition, double with) {

        int s = size();
        int r = Math.min(s, condition.size());
        DoubleAccum doubles = new DoubleAccum(s);

        for (int i = 0; i < r; i++) {
            doubles.pushDouble(condition.getBool(i) ? getDouble(i) : with);
        }

        if (s > r) {
            doubles.fill(r, s, with);
        }

        return doubles.toSeries();
    }

    private Series<Double> nullify(BooleanSeries condition) {
        int s = size();
        int r = Math.min(s, condition.size());
        ObjectAccum<Double> values = new ObjectAccum<>(s);

        for (int i = 0; i < r; i++) {
            values.push(condition.getBool(i) ? null : getDouble(i));
        }

        for (int i = r; i < s; i++) {
            values.push(getDouble(i));
        }

        return values.toSeries();
    }

    private Series<Double> nullifyNoMatch(BooleanSeries condition) {
        int s = size();
        int r = Math.min(s, condition.size());
        ObjectAccum<Double> values = new ObjectAccum<>(s);

        for (int i = 0; i < r; i++) {
            values.push(condition.getBool(i) ? getDouble(i) : null);
        }

        if (s > r) {
            values.fill(r, s, null);
        }

        return values.toSeries();
    }

    @Override
    public BooleanSeries eq(Series<?> another) {
        int s = size();
        int as = another.size();

        if (s != as) {
            throw new IllegalArgumentException("Another Series size " + as + " is not the same as this size " + s);
        }

        BoolAccum bools = new BoolAccum(s);

        if (another instanceof DoubleSeries) {
            DoubleSeries anotherDouble = (DoubleSeries) another;

            for (int i = 0; i < s; i++) {
                bools.pushBool(getDouble(i) == anotherDouble.getDouble(i));
            }
        } else {
            for (int i = 0; i < s; i++) {
                bools.pushBool(Objects.equals(get(i), another.get(i)));
            }
        }

        return bools.toSeries();
    }

    @Override
    public BooleanSeries ne(Series<?> another) {
        int s = size();
        int as = another.size();

        if (s != as) {
            throw new IllegalArgumentException("Another Series size " + as + " is not the same as this size " + s);
        }

        BoolAccum bools = new BoolAccum(s);
        if (another instanceof DoubleSeries) {
            DoubleSeries anotherDouble = (DoubleSeries) another;

            for (int i = 0; i < s; i++) {
                bools.pushBool(getDouble(i) != anotherDouble.getDouble(i));
            }
        } else {
            for (int i = 0; i < s; i++) {
                bools.pushBool(!Objects.equals(get(i), another.get(i)));
            }
        }

        return bools.toSeries();
    }

    @Override
    public BooleanSeries in(Object... values) {
        int s = size();

        if (values == null || values.length == 0) {
            return new FalseSeries(s);
        }

        Set<Object> set = new HashSet<>();
        for (Object o : values) {
            // TODO: convert from other numeric types
            if (o instanceof Double) {
                set.add(o);
            }
        }

        if (set.isEmpty()) {
            return new FalseSeries(s);
        }

        BoolAccum bools = new BoolAccum(s);
        for (int i = 0; i < s; i++) {
            bools.pushBool(set.contains(get(i)));
        }

        return bools.toSeries();
    }

    @Override
    public BooleanSeries notIn(Object... values) {
        int s = size();

        if (values == null || values.length == 0) {
            return new TrueSeries(s);
        }

        Set<Object> set = new HashSet<>();
        for (Object o : values) {
            // TODO: convert from other numeric types
            if (o instanceof Double) {
                set.add(o);
            }
        }

        if (set.isEmpty()) {
            return new TrueSeries(s);
        }

        BoolAccum bools = new BoolAccum(s);
        for (int i = 0; i < s; i++) {
            bools.pushBool(!set.contains(get(i)));
        }

        return bools.toSeries();
    }

    @Override
    public BooleanSeries isNull() {
        return new FalseSeries(size());
    }

    @Override
    public BooleanSeries isNotNull() {
        return new TrueSeries(size());
    }

    @Override
    public Series<Double> unique() {
        return uniqueDouble();
    }

    @Override
    public DoubleSeries uniqueDouble() {
        int size = size();
        if (size < 2) {
            return this;
        }

        DoubleAccum unique = new UniqueDoubleAccum();
        for (int i = 0; i < size; i++) {
            unique.push(get(i));
        }

        return unique.size() < size() ? unique.toSeries() : this;
    }

    @Override
    public DataFrame valueCounts() {
        return ValueCounts.valueCountsNoNulls(this);
    }


    // TODO: some optimized version of "primitive" group by ...

    @Override
    public SeriesGroupBy<Double> group() {
        return group(d -> d);
    }

    @Override
    public SeriesGroupBy<Double> group(ValueMapper<Double, ?> by) {
        return new SeriesGrouper<>(by).group(this);
    }

    /**
     * @since 0.7
     */
    @Override
    public DoubleSeries sample(int size) {
        return selectAsDoubleSeries(Sampler.sampleIndex(size, size()));
    }

    /**
     * @since 0.7
     */
    @Override
    public DoubleSeries sample(int size, Random random) {
        return selectAsDoubleSeries(Sampler.sampleIndex(size, size(), random));
    }

    @Override
    public String toString() {
        return ToString.toString(this);
    }
}
