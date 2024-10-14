package org.dflib.series;

import org.dflib.BooleanSeries;
import org.dflib.Condition;
import org.dflib.DataFrame;
import org.dflib.Index;
import org.dflib.IntSeries;
import org.dflib.Series;
import org.dflib.SeriesGroupBy;
import org.dflib.Sorter;
import org.dflib.ValueMapper;
import org.dflib.ValueToRowMapper;
import org.dflib.builder.IntAccum;
import org.dflib.builder.IntegerAccum;
import org.dflib.concat.SeriesConcat;
import org.dflib.map.Mapper;

import java.util.Arrays;
import java.util.Comparator;
import java.util.Map;
import java.util.Random;
import java.util.function.IntPredicate;
import java.util.function.Predicate;

public class IntegerSeries implements Series<Integer> {

    int[] data;
    FixedSizeBitSet nulls;

    public IntegerSeries(int[] data, FixedSizeBitSet nulls) {
        this.data = data;
        this.nulls = nulls;
    }

    @Override
    public Class<?> getNominalType() {
        return Integer.class;
    }

    @Override
    public Class<?> getInferredType() {
        return Integer.class;
    }

    @Override
    public int size() {
        return data.length;
    }

    @Override
    public Integer get(int index) {
        return nulls.get(index) ? null : data[index];
    }

    @Override
    public void copyTo(Object[] to, int fromOffset, int toOffset, int len) {
        for(int i = 0; i < len; i++) {
            to[toOffset + i] = get(fromOffset + i);
        }
    }

    @Override
    public int position(Integer value) {
        if(value == null) {
            return nulls.firstTrue();
        }
        int search = value;
        int lastPosition = 0;
        while(lastPosition <= data.length) {
            lastPosition = Arrays.binarySearch(data, search, lastPosition, data.length - 1);
            if(lastPosition < 0) {
                return -1;
            }
            if(!nulls.get(lastPosition)) {
                return lastPosition;
            }
        }
        return -1;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <S> Series<S> castAs(Class<S> type) throws ClassCastException {
        if (!type.isAssignableFrom(Integer.class) && !type.equals(Integer.TYPE)) {
            throw new ClassCastException("IntSeries can not be cast to " + type);
        }
        return (Series<S>) this;
    }

    @Override
    public DataFrame map(Index resultColumns, ValueToRowMapper<Integer> mapper) {
        return Mapper.map(this, resultColumns, mapper);
    }

    @Override
    public Series<Integer> selectRange(int fromInclusive, int toExclusive) {
        return null;
    }

    @Override
    public Series<Integer> materialize() {
        return this;
    }

    @Override
    public Series<Integer> fillNulls(Integer value) {
        if(nulls.firstTrue() == -1 || value == null) {
            // nothing to do here
            return this;
        }
        int[] newData = new int[data.length];
        System.arraycopy(data, 0, newData, 0, data.length);
        // TODO: check if it would be beneficial to traverse by the nulls set values
        for(int i = nulls.firstTrue(); i < newData.length; i++) {
            if(nulls.get(i)) {
                newData[i] = value;
            }
        }
        return new IntArraySeries(newData);
    }

    @Override
    public Series<Integer> fillNullsFromSeries(Series<? extends Integer> values) {
        return null;
    }

    @Override
    public Series<Integer> fillNullsBackwards() {
        return null;
    }

    @Override
    public Series<Integer> fillNullsForward() {
        return null;
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
    public Series<Integer> diff(Series<? extends Integer> other) {
        return null;
    }

    @Override
    public Series<Integer> intersect(Series<? extends Integer> other) {
        return null;
    }

    @Override
    public Series<Integer> head(int len) {
        return null;
    }

    @Override
    public Series<Integer> tail(int len) {
        return null;
    }

    @Override
    public Series<Integer> select(Condition condition) {
        return select(condition.eval(this));
    }

    @Override
    public Series<Integer> select(IntSeries positions) {
        // TODO: return IntegerIndexedSeries.of(this, positions);
        return null;
    }

    @Override
    public Series<Integer> select(Predicate<Integer> p) {
        int len = size();

        // skip as many of the elements as we can before allocating an Accum
        int i = 0;
        for (; i < len; i++) {
            if (p.test(get(i))) {
                break;
            }
        }

        if (i == len) {
            return Series.ofInt();
        }

        // Allocate the max possible buffer, trading temp memory for speed (2x speedup). The Accum will
        // shrink the buffer to the actual size when creating the result.

        IntegerAccum filtered = new IntegerAccum(len - i);
        if(nulls.get(i)) {
            filtered.pushNull();
        } else {
            filtered.pushInt(data[i]);
        }

        for (i++; i < len; i++) {
            if(nulls.get(i)) {
                if (p.test(null)) {
                    filtered.pushNull();
                }
            } else {
                if (p.test(data[i])) {
                    filtered.pushInt(data[i]);
                }
            }
        }

        return filtered.toSeries();
    }

    @Override
    public Series<Integer> select(BooleanSeries positions) {
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
            return Series.ofInt();
        }

        // Allocate the max possible buffer, trading temp memory for speed (2x speedup). The Accum will
        // shrink the buffer to the actual size when creating the result.

        // Replacing Accum with an int[] gives very little performance gain (no vectorization). So keeping the Accum.

        IntegerAccum filtered = new IntegerAccum(len - i);
        if(nulls.get(i)) {
            filtered.pushNull();
        } else {
            filtered.pushInt(data[i]);
        }

        for (i++; i < len; i++) {
            if (positions.getBool(i)) {
                if(nulls.get(i)) {
                    filtered.pushNull();
                } else {
                    filtered.pushInt(data[i]);
                }
            }
        }

        return filtered.toSeries();
    }

    @Override
    public IntSeries index(Predicate<Integer> predicate) {
        int len = size();
        // Allocate the max possible buffer, trading temp memory for speed (2x speedup).
        // The Accum will shrink the buffer to the actual size when creating the result.
        IntAccum index = new IntAccum(len);

        for (int i = 0; i < len; i++) {
            if(predicate.test(nulls.get(i) ? null : data[i])) {
                index.pushInt(i);
            }
        }

        return index.toSeries();
    }

    public IntSeries indexInt(IntPredicate predicate) {
        int len = size();
        // Allocate the max possible buffer, trading temp memory for speed (2x speedup).
        // The Accum will shrink the buffer to the actual size when creating the result.
        IntAccum index = new IntAccum(len);

        for (int i = 0; i < len; i++) {
            if(!nulls.get(i)) {
                if (predicate.test(data[i])) {
                    index.pushInt(i);
                }
            }
        }

        return index.toSeries();
    }

    @Override
    public Series<Integer> sort(Sorter... sorters) {
        return null;
    }

    @Override
    public Series<Integer> sort(Comparator<? super Integer> comparator) {
        return null;
    }

    @Override
    public BooleanSeries isNull() {
        int nullsCount = nulls.countTrue();
        if(nullsCount == 0) {
            return new FalseSeries(size());
        }
        if(nullsCount == size()) {
            return new TrueSeries(size());
        }
        return new BooleanBitSetSeries(nulls);
    }

    @Override
    public BooleanSeries isNotNull() {
        int nullsCount = nulls.countTrue();
        if(nullsCount == 0) {
            return new TrueSeries(size());
        }
        if(nullsCount == size()) {
            return new FalseSeries(size());
        }
        return new BooleanBitSetSeries(nulls.not());
    }

    @Override
    public BooleanSeries in(Object... values) {
        return null;
    }

    @Override
    public BooleanSeries notIn(Object... values) {
        return null;
    }

    @Override
    public Series<Integer> replace(IntSeries positions, Series<Integer> with) {
        return null;
    }

    @Override
    public Series<Integer> replace(BooleanSeries condition, Integer with) {
        return null;
    }

    @Override
    public Series<Integer> replace(Map<Integer, Integer> oldToNewValues) {
        return null;
    }

    @Override
    public Series<Integer> replaceExcept(BooleanSeries condition, Integer with) {
        return null;
    }

    @Override
    public Series<Integer> unique() {
        return null;
    }

    @Override
    public DataFrame valueCounts() {
        return null;
    }

    @Override
    public SeriesGroupBy<Integer> group() {
        return null;
    }

    @Override
    public SeriesGroupBy<Integer> group(ValueMapper<Integer, ?> by) {
        return null;
    }

    @Override
    public Series<Integer> sample(int size) {
        return null;
    }

    @Override
    public Series<Integer> sample(int size, Random random) {
        return null;
    }

    /**
     * Performs per-element addition between this and another IntegerSeries, returning the Series of the same length.
     * <p>
     * <b>NOTE:</b> nulls are treated as 0, however if both arguments are null, result is null
     */
    public IntegerSeries add(IntegerSeries s) {
        int len = size();
        if (len != s.size()) {
            throw new IllegalArgumentException("Another Series size " + s.size() + " is not the same as this size " + len);
        }

        int[] data = new int[len];
        FixedSizeBitSet nulls = new FixedSizeBitSet(len);
        for (int i = 0; i < len; i++) {
            data[i] = this.data[i] + s.data[i];
            if(nulls.get(i) && s.nulls.get(i)) {
                nulls.set(i);
            }
        }
        return new IntegerSeries(data, nulls);
    }
}
