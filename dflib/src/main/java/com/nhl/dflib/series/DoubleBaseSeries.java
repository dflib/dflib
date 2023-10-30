package com.nhl.dflib.series;

import com.nhl.dflib.BooleanSeries;
import com.nhl.dflib.Condition;
import com.nhl.dflib.DataFrame;
import com.nhl.dflib.DoublePredicate;
import com.nhl.dflib.DoubleSeries;
import com.nhl.dflib.Index;
import com.nhl.dflib.IntSeries;
import com.nhl.dflib.Series;
import com.nhl.dflib.SeriesGroupBy;
import com.nhl.dflib.Sorter;
import com.nhl.dflib.ValueMapper;
import com.nhl.dflib.ValuePredicate;
import com.nhl.dflib.ValueToRowMapper;
import com.nhl.dflib.builder.DoubleAccum;
import com.nhl.dflib.builder.IntAccum;
import com.nhl.dflib.builder.UniqueDoubleAccum;
import com.nhl.dflib.concat.SeriesConcat;
import com.nhl.dflib.groupby.SeriesGrouper;
import com.nhl.dflib.map.Mapper;
import com.nhl.dflib.sample.Sampler;
import com.nhl.dflib.sort.SeriesSorter;

import java.util.Arrays;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

/**
 * @since 0.6
 */
public abstract class DoubleBaseSeries implements DoubleSeries {

    /**
     * @since 0.18
     */
    @Override
    public <S> Series<S> castAs(Class<S> type) {
        if (!type.isAssignableFrom(Double.class) && !type.equals(Double.TYPE)) {
            throw new ClassCastException("DoubleSeries can not be cast to " + type);
        }

        return (Series<S>) this;
    }

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
    public DoubleSeries select(ValuePredicate<Double> p) {
        return selectDouble(p::test);
    }

    @Override
    public DoubleSeries select(Condition condition) {
        return select(condition.eval(this));
    }

    @Override
    public DoubleSeries select(BooleanSeries positions) {
        int len = size();

        if (len != positions.size()) {
            throw new IllegalArgumentException("Positions size " + positions.size() + " is not the same as this size " + len);
        }

        // Allocate the max possible buffer, trading temp memory for speed (2x speedup). The Accum will shrink the
        // buffer to the actual size when creating the result.

        // Replacing the Accum with a double[] gives very little performance gain, presumably due to the need for another
        // loop index, that does not allow HotSpot to vectorize the loop. So keeping the Accum.

        DoubleAccum filtered = new DoubleAccum(len);

        for (int i = 0; i < len; i++) {
            if (positions.getBool(i)) {
                filtered.pushDouble(getDouble(i));
            }
        }

        return filtered.toSeries();
    }

    @Override
    public DoubleSeries selectDouble(DoublePredicate p) {

        int len = size();

        // Allocate the max possible buffer, trading temp memory for speed (2x speedup). The Accum will shrink the
        // buffer to the actual size when creating the result.

        // Replacing Accum with a double[] gives very little performance gain, presumably due to the need for another
        // loop index, that does not allow HotSpot to vectorize the loop. So keeping the Accum.
        DoubleAccum filtered = new DoubleAccum(len);

        for (int i = 0; i < len; i++) {
            double v = getDouble(i);
            if (p.test(v)) {
                filtered.pushDouble(v);
            }
        }

        return filtered.toSeries();
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

    @Override
    public DoubleSeries sortDouble() {
        int size = size();
        double[] sorted = new double[size];
        copyToDouble(sorted, 0, 0, size);

        // TODO: use "parallelSort" ?
        Arrays.sort(sorted);

        return new DoubleArraySeries(sorted);
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

        int len = size();

        // Allocate the max possible buffer, trading temp memory for speed (2x speedup). The Accum will shrink the
        // buffer to the actual size when creating the result.
        IntAccum index = new IntAccum(len);

        for (int i = 0; i < len; i++) {
            if (predicate.test(getDouble(i))) {
                index.pushInt(i);
            }
        }

        return index.toSeries();
    }

    @Override
    public IntSeries index(ValuePredicate<Double> predicate) {

        // reimplementing instead of calling "indexDouble", as it seems to be doing less (un)boxing and is about 13% faster
        // as a result

        int len = size();

        // Allocate the max possible buffer, trading temp memory for speed (2x speedup). The Accum will shrink the
        // buffer to the actual size when creating the result.
        IntAccum index = new IntAccum(len);

        for (int i = 0; i < len; i++) {
            if (predicate.test(get(i))) {
                index.pushInt(i);
            }
        }

        return index.toSeries();
    }

    @Override
    public BooleanSeries locateDouble(DoublePredicate predicate) {
        int len = size();

        boolean[] data = new boolean[len];

        for (int i = 0; i < len; i++) {
            data[i] = predicate.test(getDouble(i));
        }

        return new BooleanArraySeries(data);
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
        int len = size();
        int r = Math.min(len, condition.size());
        double[] data = new double[len];

        for (int i = 0; i < r; i++) {
            data[i] = condition.getBool(i) ? with : getDouble(i);
        }

        for (int i = r; i < len; i++) {
            data[i] = getDouble(i);
        }

        return new DoubleArraySeries(data);
    }

    private DoubleSeries replaceNoMatchDouble(BooleanSeries condition, double with) {

        int len = size();
        int r = Math.min(len, condition.size());
        double[] data = new double[len];

        for (int i = 0; i < r; i++) {
            data[i] = condition.getBool(i) ? getDouble(i) : with;
        }

        if (len > r) {
            Arrays.fill(data, r, len, with);
        }

        return new DoubleArraySeries(data);
    }

    private Series<Double> nullify(BooleanSeries condition) {
        int len = size();
        int r = Math.min(len, condition.size());
        Double[] data = new Double[len];

        for (int i = 0; i < r; i++) {
            data[i] = condition.getBool(i) ? null : getDouble(i);
        }

        for (int i = r; i < len; i++) {
            data[i] = getDouble(i);
        }

        return new ArraySeries<>(data);
    }

    private Series<Double> nullifyNoMatch(BooleanSeries condition) {
        int len = size();
        int r = Math.min(len, condition.size());
        Double[] data = new Double[len];

        for (int i = 0; i < r; i++) {
            data[i] = condition.getBool(i) ? getDouble(i) : null;
        }

        if (len > r) {
            Arrays.fill(data, r, len, null);
        }

        return new ArraySeries<>(data);
    }

    @Override
    public BooleanSeries in(Object... values) {
        int len = size();

        if (values == null || values.length == 0) {
            return new FalseSeries(len);
        }

        Set<Object> set = new HashSet<>();
        for (Object o : values) {
            // TODO: convert from other numeric types
            if (o instanceof Double) {
                set.add(o);
            }
        }

        if (set.isEmpty()) {
            return new FalseSeries(len);
        }

        boolean[] data = new boolean[len];
        for (int i = 0; i < len; i++) {
            data[i] = set.contains(get(i));
        }

        return new BooleanArraySeries(data);
    }

    @Override
    public BooleanSeries notIn(Object... values) {
        int len = size();

        if (values == null || values.length == 0) {
            return new TrueSeries(len);
        }

        Set<Object> set = new HashSet<>();
        for (Object o : values) {
            // TODO: convert from other numeric types
            if (o instanceof Double) {
                set.add(o);
            }
        }

        if (set.isEmpty()) {
            return new TrueSeries(len);
        }

        boolean[] data = new boolean[len];
        for (int i = 0; i < len; i++) {
            data[i] = !set.contains(get(i));
        }

        return new BooleanArraySeries(data);
    }

    @Override
    public DoubleSeries unique() {
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
