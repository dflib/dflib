package org.dflib.series;

import org.dflib.BooleanSeries;
import org.dflib.Condition;
import org.dflib.DataFrame;
import org.dflib.DoubleSeries;
import org.dflib.Index;
import org.dflib.IntSeries;
import org.dflib.Series;
import org.dflib.SeriesGroupBy;
import org.dflib.Sorter;
import org.dflib.ValueMapper;
import org.dflib.ValueToRowMapper;
import org.dflib.builder.DoubleAccum;
import org.dflib.builder.IntAccum;
import org.dflib.builder.ObjectAccum;
import org.dflib.builder.UniqueDoubleAccum;
import org.dflib.concat.SeriesConcat;
import org.dflib.groupby.SeriesGrouper;
import org.dflib.map.Mapper;
import org.dflib.sample.Sampler;
import org.dflib.sort.SeriesSorter;

import java.util.Arrays;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.function.DoublePredicate;
import java.util.function.Predicate;

public abstract class DoubleBaseSeries implements DoubleSeries {

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
    public Series<Double> selectRange(int fromInclusive, int toExclusive) {
        return rangeDouble(fromInclusive, toExclusive);
    }

    @Override
    public DoubleSeries select(Predicate<Double> p) {
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

        // skip as many of the elements as we can before allocating an Accum
        int i = 0;
        for (; i < len; i++) {
            if (positions.getBool(i)) {
                break;
            }
        }

        if (i == len) {
            return Series.ofDouble();
        }

        // Allocate the max possible buffer, trading temp memory for speed (2x speedup). The Accum will
        // shrink the buffer to the actual size when creating the result.

        // Replacing Accum with an double[] gives very little performance gain (no vectorization). So keeping the Accum.

        DoubleAccum filtered = new DoubleAccum(len - i);
        filtered.pushDouble(getDouble(i));

        for (i++; i < len; i++) {
            if (positions.getBool(i)) {
                filtered.pushDouble(getDouble(i));
            }
        }

        return filtered.toSeries();
    }

    @Override
    public DoubleSeries selectDouble(DoublePredicate p) {

        int len = size();

        // skip as many of the elements as we can before allocating an Accum
        int i = 0;
        for (; i < len; i++) {
            if (p.test(getDouble(i))) {
                break;
            }
        }

        if (i == len) {
            return Series.ofDouble();
        }

        // Allocate the max possible buffer, trading temp memory for speed (2x speedup). The Accum will
        // shrink the buffer to the actual size when creating the result.

        DoubleAccum filtered = new DoubleAccum(len - i);
        filtered.pushDouble(getDouble(i));

        for (i++; i < len; i++) {
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
    public void copyTo(Object[] to, int fromOffset, int toOffset, int len) {
        for (int i = 0; i < len; i++) {
            to[toOffset + i] = getDouble(fromOffset + i);
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
    public IntSeries index(Predicate<Double> predicate) {

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
    public Series<Double> replace(Map<Double, Double> oldToNewValues) {

        int len = size();
        double[] replaced = new double[len];

        for (int i = 0; i < len; i++) {
            Double val = getDouble(i);
            Double newVal = oldToNewValues.getOrDefault(val, val);

            if (newVal == null) {
                // abandon the doubles collected so far, and return object series
                return replaceAsObjects(oldToNewValues);
            }

            replaced[i] = newVal;
        }

        return Series.ofDouble(replaced);
    }

    private Series<Double> replaceAsObjects(Map<Double, Double> oldToNewValues) {

        int len = size();
        Double[] replaced = new Double[len];

        for (int i = 0; i < len; i++) {
            Double val = getDouble(i);
            replaced[i] = oldToNewValues.getOrDefault(val, val);
        }

        return Series.of(replaced);
    }

    @Override
    public Series<Double> replace(IntSeries positions, Series<Double> with) {
        int rs = positions.size();
        if (rs != with.size()) {
            throw new IllegalArgumentException("Positions size " + rs + " is not the same replacement Series size " + with.size());
        }

        if (rs == 0) {
            return this;
        }

        int s = size();

        // Quick check for nulls in "with". May result in false positives (no nulls in Series<Double>), but does not
        // require checking each value

        if (with instanceof DoubleSeries) {

            DoubleSeries withDouble = (DoubleSeries) with;
            DoubleAccum values = new DoubleAccum(s);

            values.fill(this, 0, 0, s);

            for (int i = 0; i < rs; i++) {
                values.replace(positions.getInt(i), withDouble.getDouble(i));
            }

            return values.toSeries();
        } else {
            ObjectAccum<Double> values = new ObjectAccum<>(s);

            values.fill(this, 0, 0, s);

            for (int i = 0; i < rs; i++) {
                values.replace(positions.getInt(i), with.get(i));
            }

            return values.toSeries();
        }
    }

    @Override
    public Series<Double> replace(BooleanSeries condition, Double with) {
        return with != null
                ? replaceDouble(condition, with)
                : nullify(condition);
    }

    @Override
    public Series<Double> replaceExcept(BooleanSeries condition, Double with) {
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


    @Override
    public DoubleSeries sample(int size) {
        return selectAsDoubleSeries(Sampler.sampleIndex(size, size()));
    }


    @Override
    public DoubleSeries sample(int size, Random random) {
        return selectAsDoubleSeries(Sampler.sampleIndex(size, size(), random));
    }

    @Override
    public String toString() {
        return ToString.toString(this);
    }
}
