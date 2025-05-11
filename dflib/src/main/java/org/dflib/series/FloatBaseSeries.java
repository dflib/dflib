package org.dflib.series;

import org.dflib.BooleanSeries;
import org.dflib.Condition;
import org.dflib.DataFrame;
import org.dflib.FloatSeries;
import org.dflib.Index;
import org.dflib.IntSeries;
import org.dflib.Series;
import org.dflib.SeriesGroupBy;
import org.dflib.Sorter;
import org.dflib.ValueMapper;
import org.dflib.ValueToRowMapper;
import org.dflib.builder.BoolBuilder;
import org.dflib.builder.FloatAccum;
import org.dflib.builder.IntAccum;
import org.dflib.builder.ObjectAccum;
import org.dflib.builder.UniqueFloatAccum;
import org.dflib.f.FloatPredicate;
import org.dflib.groupby.SeriesGrouper;
import org.dflib.map.Mapper;
import org.dflib.sample.Sampler;
import org.dflib.sort.IntComparator;

import java.util.Arrays;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.function.Predicate;

/**
 * @since 1.1.0
 */
public abstract class FloatBaseSeries implements FloatSeries {

    @Override
    public <S> Series<S> castAs(Class<S> type) {
        if (!type.isAssignableFrom(Float.class) && !type.equals(Float.TYPE)) {
            throw new ClassCastException("FloatSeries can not be cast to " + type);
        }

        return (Series<S>) this;
    }

    @Override
    public DataFrame map(Index resultColumns, ValueToRowMapper<Float> mapper) {
        return Mapper.map(this, resultColumns, mapper);
    }

    @Override
    public Series<Float> selectRange(int fromInclusive, int toExclusive) {
        return rangeFloat(fromInclusive, toExclusive);
    }

    @Override
    public FloatSeries select(Predicate<Float> p) {
        return selectFloat(p::test);
    }

    @Override
    public FloatSeries select(Condition condition) {
        return select(condition.eval(this));
    }

    @Override
    public FloatSeries select(BooleanSeries positions) {
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
            return Series.ofFloat();
        }

        // Allocate the max possible buffer, trading temp memory for speed (2x speedup). The Accum will
        // shrink the buffer to the actual size when creating the result.

        // Replacing Accum with a float[] gives very little performance gain (no vectorization). So keeping the Accum.

        FloatAccum filtered = new FloatAccum(len - i);
        filtered.pushFloat(getFloat(i));

        for (i++; i < len; i++) {
            if (positions.getBool(i)) {
                filtered.pushFloat(getFloat(i));
            }
        }

        return filtered.toSeries();
    }

    @Override
    public FloatSeries selectFloat(FloatPredicate p) {

        int len = size();

        // skip as many of the elements as we can before allocating an Accum
        int i = 0;
        for (; i < len; i++) {
            if (p.test(getFloat(i))) {
                break;
            }
        }

        if (i == len) {
            return Series.ofFloat();
        }

        // Allocate the max possible buffer, trading temp memory for speed (2x speedup). The Accum will
        // shrink the buffer to the actual size when creating the result.

        FloatAccum filtered = new FloatAccum(len - i);
        filtered.pushFloat(getFloat(i));

        for (i++; i < len; i++) {
            float v = getFloat(i);
            if (p.test(v)) {
                filtered.pushFloat(v);
            }
        }

        return filtered.toSeries();
    }

    @Override
    public FloatSeries sort(Sorter... sorters) {
        IntSeries index = IntComparator.of(this, sorters).sortIndex(size());
        return selectAsFloatSeries(index);
    }

    @Override
    public FloatSeries sort(Comparator<? super Float> comparator) {
        return selectAsFloatSeries(sortIndex(comparator));
    }

    @Override
    public FloatSeries sortFloat() {
        int size = size();
        float[] sorted = new float[size];
        copyToFloat(sorted, 0, 0, size);

        // TODO: use "parallelSort" ?
        Arrays.sort(sorted);

        return new FloatArraySeries(sorted);
    }

    private FloatSeries selectAsFloatSeries(IntSeries positions) {

        int h = positions.size();

        float[] data = new float[h];
        for (int i = 0; i < h; i++) {
            // unlike SelectSeries, we do not expect negative ints in the index.
            // So if it happens, let it fall through to "getFloat()" and fail there
            int index = positions.getInt(i);
            data[i] = getFloat(index);
        }

        return new FloatArraySeries(data);
    }

    private Series<Float> selectAsObjectSeries(IntSeries positions) {

        int h = positions.size();
        Float[] data = new Float[h];

        for (int i = 0; i < h; i++) {
            int index = positions.getInt(i);
            data[i] = index < 0 ? null : getFloat(index);
        }

        return new ArraySeries<>(data);
    }

    @Override
    public Series<Float> fillNulls(Float value) {
        // primitive series has no nulls
        return this;
    }

    @Override
    public Series<Float> fillNullsFromSeries(Series<? extends Float> values) {
        // primitive series has no nulls
        return this;
    }

    @Override
    public Series<Float> fillNullsBackwards() {
        // primitive series has no nulls
        return this;
    }

    @Override
    public Series<Float> fillNullsForward() {
        // primitive series has no nulls
        return this;
    }

    @Override
    public void copyTo(Object[] to, int fromOffset, int toOffset, int len) {
        for (int i = 0; i < len; i++) {
            to[toOffset + i] = getFloat(fromOffset + i);
        }
    }

    @Override
    public IntSeries indexFloat(FloatPredicate predicate) {

        int len = size();

        // Allocate the max possible buffer, trading temp memory for speed (2x speedup). The Accum will shrink the
        // buffer to the actual size when creating the result.
        IntAccum index = new IntAccum(len);

        for (int i = 0; i < len; i++) {
            if (predicate.test(getFloat(i))) {
                index.pushInt(i);
            }
        }

        return index.toSeries();
    }

    @Override
    public IntSeries index(Predicate<Float> predicate) {

        // reimplementing instead of calling "indexFloat", as it seems to be doing less (un)boxing and is about 13% faster
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
    public BooleanSeries locateFloat(FloatPredicate predicate) {
        return BoolBuilder.buildSeries(i -> predicate.test(getFloat(i)), size());
    }

    @Override
    public Series<Float> replace(Map<Float, Float> oldToNewValues) {

        int len = size();
        float[] replaced = new float[len];

        for (int i = 0; i < len; i++) {
            Float val = getFloat(i);
            Float newVal = oldToNewValues.getOrDefault(val, val);

            if (newVal == null) {
                // abandon the floats collected so far, and return object series
                return replaceAsObjects(oldToNewValues);
            }

            replaced[i] = newVal;
        }

        return Series.ofFloat(replaced);
    }

    private Series<Float> replaceAsObjects(Map<Float, Float> oldToNewValues) {

        int len = size();
        Float[] replaced = new Float[len];

        for (int i = 0; i < len; i++) {
            Float val = getFloat(i);
            replaced[i] = oldToNewValues.getOrDefault(val, val);
        }

        return Series.of(replaced);
    }

    @Override
    public Series<Float> replace(IntSeries positions, Series<Float> with) {
        int rs = positions.size();
        if (rs != with.size()) {
            throw new IllegalArgumentException("Positions size " + rs + " is not the same replacement Series size " + with.size());
        }

        if (rs == 0) {
            return this;
        }

        int s = size();

        // Quick check for nulls in "with". May result in false positives (no nulls in Series<Float>), but does not
        // require checking each value

        if (with instanceof FloatSeries) {

            FloatSeries withFloat = (FloatSeries) with;
            FloatAccum values = new FloatAccum(s);

            values.fill(this, 0, 0, s);

            for (int i = 0; i < rs; i++) {
                values.replace(positions.getInt(i), withFloat.getFloat(i));
            }

            return values.toSeries();
        } else {
            ObjectAccum<Float> values = new ObjectAccum<>(s);

            values.fill(this, 0, 0, s);

            for (int i = 0; i < rs; i++) {
                values.replace(positions.getInt(i), with.get(i));
            }

            return values.toSeries();
        }
    }

    @Override
    public Series<Float> replace(BooleanSeries condition, Float with) {
        return with != null
                ? replaceFloat(condition, with)
                : nullify(condition);
    }

    @Override
    public Series<Float> replaceExcept(BooleanSeries condition, Float with) {
        return with != null
                ? replaceNoMatchFloat(condition, with)
                : nullifyNoMatch(condition);
    }

    // TODO: make float versions of replace public?

    private FloatSeries replaceFloat(BooleanSeries condition, float with) {
        int len = size();
        int r = Math.min(len, condition.size());
        float[] data = new float[len];

        for (int i = 0; i < r; i++) {
            data[i] = condition.getBool(i) ? with : getFloat(i);
        }

        for (int i = r; i < len; i++) {
            data[i] = getFloat(i);
        }

        return new FloatArraySeries(data);
    }

    private FloatSeries replaceNoMatchFloat(BooleanSeries condition, float with) {

        int len = size();
        int r = Math.min(len, condition.size());
        float[] data = new float[len];

        for (int i = 0; i < r; i++) {
            data[i] = condition.getBool(i) ? getFloat(i) : with;
        }

        if (len > r) {
            Arrays.fill(data, r, len, with);
        }

        return new FloatArraySeries(data);
    }

    private Series<Float> nullify(BooleanSeries condition) {
        int len = size();
        int r = Math.min(len, condition.size());
        Float[] data = new Float[len];

        for (int i = 0; i < r; i++) {
            data[i] = condition.getBool(i) ? null : getFloat(i);
        }

        for (int i = r; i < len; i++) {
            data[i] = getFloat(i);
        }

        return new ArraySeries<>(data);
    }

    private Series<Float> nullifyNoMatch(BooleanSeries condition) {
        int len = size();
        int r = Math.min(len, condition.size());
        Float[] data = new Float[len];

        for (int i = 0; i < r; i++) {
            data[i] = condition.getBool(i) ? getFloat(i) : null;
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
            if (o instanceof Float) {
                set.add(o);
            }
        }

        if (set.isEmpty()) {
            return new FalseSeries(len);
        }

        return BoolBuilder.buildSeries(i -> set.contains(get(i)), len);
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
            if (o instanceof Float) {
                set.add(o);
            }
        }

        if (set.isEmpty()) {
            return new TrueSeries(len);
        }

        return BoolBuilder.buildSeries(i -> !set.contains(get(i)), len);
    }

    @Override
    public FloatSeries unique() {
        int size = size();
        if (size < 2) {
            return this;
        }

        FloatAccum unique = new UniqueFloatAccum();
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
    public SeriesGroupBy<Float> group() {
        return group(d -> d);
    }

    @Override
    public SeriesGroupBy<Float> group(ValueMapper<Float, ?> by) {
        return new SeriesGrouper<>(by).group(this);
    }


    @Override
    public FloatSeries sample(int size) {
        return selectAsFloatSeries(Sampler.sampleIndex(size, size()));
    }


    @Override
    public FloatSeries sample(int size, Random random) {
        return selectAsFloatSeries(Sampler.sampleIndex(size, size(), random));
    }

    @Override
    public String toString() {
        return ToString.toString(this);
    }
}
