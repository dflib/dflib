package com.nhl.dflib.series;

import com.nhl.dflib.BooleanSeries;
import com.nhl.dflib.Condition;
import com.nhl.dflib.DataFrame;
import com.nhl.dflib.FloatPredicate;
import com.nhl.dflib.FloatSeries;
import com.nhl.dflib.Index;
import com.nhl.dflib.IntSeries;
import com.nhl.dflib.Series;
import com.nhl.dflib.SeriesGroupBy;
import com.nhl.dflib.Sorter;
import com.nhl.dflib.ValueMapper;
import com.nhl.dflib.ValuePredicate;
import com.nhl.dflib.ValueToRowMapper;
import com.nhl.dflib.accumulator.BooleanAccumulator;
import com.nhl.dflib.accumulator.FloatAccumulator;
import com.nhl.dflib.accumulator.IntAccumulator;
import com.nhl.dflib.accumulator.ObjectAccumulator;
import com.nhl.dflib.accumulator.UniqueFloatAccumulator;
import com.nhl.dflib.concat.SeriesConcat;
import com.nhl.dflib.groupby.SeriesGrouper;
import com.nhl.dflib.map.Mapper;
import com.nhl.dflib.sample.Sampler;
import com.nhl.dflib.sort.SeriesSorter;

import java.util.Arrays;
import java.util.Comparator;
import java.util.Objects;
import java.util.Random;

/**
 * @since 0.6
 */
public abstract class FloatBaseSeries implements FloatSeries {

    @Override
    public <V> Series<V> map(ValueMapper<Float, V> mapper) {
        return new ColumnMappedSeries<>(this, mapper);
    }

    @Override
    public DataFrame map(Index resultColumns, ValueToRowMapper<Float> mapper) {
        return Mapper.map(this, resultColumns, mapper);
    }

    @Override
    public Series<Float> rangeOpenClosed(int fromInclusive, int toExclusive) {
        return rangeOpenClosedFloat(fromInclusive, toExclusive);
    }

    @Override
    public Series<Float> select(IntSeries positions) {

        int h = positions.size();

        float[] data = new float[h];

        for (int i = 0; i < h; i++) {
            int index = positions.getInt(i);

            // "index < 0" (often found in outer joins) indicate nulls.
            // If a null is encountered, we can no longer maintain primitive and have to change to Series<Float>...
            if (index < 0) {
                return selectAsObjectSeries(positions);
            }

            data[i] = getFloat(index);
        }

        return new FloatArraySeries(data);
    }

    @Override
    public Series<Float> select(Condition condition) {
        return selectFloat(condition);
    }

    @Override
    public Series<Float> select(ValuePredicate<Float> p) {
        return selectFloat(p::test);
    }

    @Override
    public FloatSeries selectFloat(Condition condition) {
        return selectFloat(condition.eval(this));
    }

    @Override
    public FloatSeries selectFloat(FloatPredicate p) {
        FloatAccumulator filtered = new FloatAccumulator();

        int len = size();

        for (int i = 0; i < len; i++) {
            Float v = getFloat(i);
            if (p.test(v)) {
                filtered.addFloat(v);
            }
        }

        return filtered.toSeries();
    }

    @Override
    public FloatSeries selectFloat(BooleanSeries positions) {
        int s = size();
        int ps = positions.size();

        if (s != ps) {
            throw new IllegalArgumentException("Positions size " + ps + " is not the same as this size " + s);
        }

        FloatAccumulator data = new FloatAccumulator();

        for (int i = 0; i < size(); i++) {
            if (positions.getBoolean(i)) {
                data.addFloat(getFloat(i));
            }
        }

        return data.toSeries();
    }

    @Override
    public Series<Float> select(BooleanSeries positions) {
        return selectFloat(positions);
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

    @Override
    public FloatSeries sort(Sorter... sorters) {
        return selectAsFloatSeries(new SeriesSorter<>(this).sortIndex(sorters));
    }

    // TODO: implement 'sortFloat(FloatComparator)' similar to how IntBaseSeries does "sortInt(IntComparator)"
    //   Reimplement this method to delegate to 'sortFloat'
    @Override
    public FloatSeries sort(Comparator<? super Float> comparator) {
        return selectAsFloatSeries(new SeriesSorter<>(this).sortIndex(comparator));
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
    public FloatSeries concatFloat(FloatSeries... other) {
        if (other.length == 0) {
            return this;
        }

        // TODO: use SeriesConcat

        int size = size();
        int h = size;
        for (FloatSeries s : other) {
            h += s.size();
        }

        float[] data = new float[h];
        copyToFloat(data, 0, 0, size);

        int offset = size;
        for (FloatSeries s : other) {
            int len = s.size();
            s.copyToFloat(data, 0, offset, len);
            offset += len;
        }

        return new FloatArraySeries(data);
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
    public Series<Float> head(int len) {
        return headFloat(len);
    }

    @Override
    public Series<Float> tail(int len) {
        return tailFloat(len);
    }

    @SafeVarargs
    @Override
    public final Series<Float> concat(Series<? extends Float>... other) {
        // concatenating as Float... to concat as FloatSeries, "concatFloat" should be used
        if (other.length == 0) {
            return this;
        }

        // TODO: use SeriesConcat

        Series<Float>[] combined = new Series[other.length + 1];
        combined[0] = this;
        System.arraycopy(other, 0, combined, 1, other.length);

        return SeriesConcat.concat(combined);
    }

    @Override
    public Series<Float> materialize() {
        return materializeFloat();
    }

    @Override
    public Float get(int index) {
        return getFloat(index);
    }

    @Override
    public void copyTo(Object[] to, int fromOffset, int toOffset, int len) {
        for (int i = 0; i < len; i++) {
            to[toOffset + i] = getFloat(i);
        }
    }

    @Override
    public IntSeries indexFloat(FloatPredicate predicate) {
        IntAccumulator index = new IntAccumulator();

        int len = size();

        for (int i = 0; i < len; i++) {
            if (predicate.test(getFloat(i))) {
                index.addInt(i);
            }
        }

        return index.toSeries();
    }

    @Override
    public IntSeries index(ValuePredicate<Float> predicate) {
        return indexFloat(predicate::test);
    }

    @Override
    public BooleanSeries locateFloat(FloatPredicate predicate) {
        int len = size();

        BooleanAccumulator matches = new BooleanAccumulator(len);

        for (int i = 0; i < len; i++) {
            matches.addBoolean(predicate.test(getFloat(i)));
        }

        return matches.toSeries();
    }

    @Override
    public BooleanSeries locate(ValuePredicate<Float> predicate) {
        return locateFloat(predicate::test);
    }

    @Override
    public Series<Float> replace(BooleanSeries condition, Float with) {
        return with != null
                ? replaceFloat(condition, with)
                : nullify(condition);
    }

    @Override
    public Series<Float> replaceNoMatch(BooleanSeries condition, Float with) {
        return with != null
                ? replaceNoMatchFloat(condition, with)
                : nullifyNoMatch(condition);
    }

    // TODO: make Float versions of replace public?

    private FloatSeries replaceFloat(BooleanSeries condition, Float with) {
        int s = size();
        int r = Math.min(s, condition.size());
        FloatAccumulator Floats = new FloatAccumulator(s);

        for (int i = 0; i < r; i++) {
            Floats.addFloat(condition.getBoolean(i) ? with : getFloat(i));
        }

        for (int i = r; i < s; i++) {
            Floats.addFloat(getFloat(i));
        }

        return Floats.toSeries();
    }

    private FloatSeries replaceNoMatchFloat(BooleanSeries condition, Float with) {

        int s = size();
        int r = Math.min(s, condition.size());
        FloatAccumulator Floats = new FloatAccumulator(s);

        for (int i = 0; i < r; i++) {
            Floats.addFloat(condition.getBoolean(i) ? getFloat(i) : with);
        }

        if (s > r) {
            Floats.fill(r, s, with);
        }

        return Floats.toSeries();
    }

    private Series<Float> nullify(BooleanSeries condition) {
        int s = size();
        int r = Math.min(s, condition.size());
        ObjectAccumulator<Float> values = new ObjectAccumulator<>(s);

        for (int i = 0; i < r; i++) {
            values.add(condition.getBoolean(i) ? null : getFloat(i));
        }

        for (int i = r; i < s; i++) {
            values.add(getFloat(i));
        }

        return values.toSeries();
    }

    private Series<Float> nullifyNoMatch(BooleanSeries condition) {
        int s = size();
        int r = Math.min(s, condition.size());
        ObjectAccumulator<Float> values = new ObjectAccumulator<>(s);

        for (int i = 0; i < r; i++) {
            values.add(condition.getBoolean(i) ? getFloat(i) : null);
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

        BooleanAccumulator bools = new BooleanAccumulator(s);

        if (another instanceof FloatSeries) {
            FloatSeries anotherFloat = (FloatSeries) another;

            for (int i = 0; i < s; i++) {
                bools.addBoolean(getFloat(i) == anotherFloat.getFloat(i));
            }
        } else {
            for (int i = 0; i < s; i++) {
                bools.addBoolean(Objects.equals(get(i), another.get(i)));
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

        BooleanAccumulator bools = new BooleanAccumulator(s);
        if (another instanceof FloatSeries) {
            FloatSeries anotherFloat = (FloatSeries) another;

            for (int i = 0; i < s; i++) {
                bools.addBoolean(getFloat(i) != anotherFloat.getFloat(i));
            }
        } else {
            for (int i = 0; i < s; i++) {
                bools.addBoolean(!Objects.equals(get(i), another.get(i)));
            }
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
    public Series<Float> unique() {
        return uniqueFloat();
    }

    @Override
    public FloatSeries uniqueFloat() {
        int size = size();
        if (size < 2) {
            return this;
        }

        FloatAccumulator unique = new UniqueFloatAccumulator();
        for (int i = 0; i < size; i++) {
            unique.add(get(i));
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

    /**
     * @since 0.7
     */
    @Override
    public FloatSeries sample(int size) {
        return selectAsFloatSeries(Sampler.sampleIndex(size, size()));
    }

    /**
     * @since 0.7
     */
    @Override
    public FloatSeries sample(int size, Random random) {
        return selectAsFloatSeries(Sampler.sampleIndex(size, size(), random));
    }

    @Override
    public String toString() {
        return ToString.toString(this);
    }
}
