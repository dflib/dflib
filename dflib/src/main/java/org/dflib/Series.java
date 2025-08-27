package org.dflib;

import org.dflib.agg.SeriesAggregator;
import org.dflib.builder.BoolBuilder;
import org.dflib.builder.SeriesByElementBuilder;
import org.dflib.builder.ValueCompactor;
import org.dflib.op.ReplaceOp;
import org.dflib.series.ArraySeries;
import org.dflib.series.ColumnMappedSeries;
import org.dflib.series.DoubleArraySeries;
import org.dflib.series.DoubleSingleValueSeries;
import org.dflib.series.EmptySeries;
import org.dflib.series.FalseSeries;
import org.dflib.series.FloatArraySeries;
import org.dflib.series.FloatSingleValueSeries;
import org.dflib.series.IntArraySeries;
import org.dflib.series.IntSingleValueSeries;
import org.dflib.series.LongArraySeries;
import org.dflib.series.LongSingleValueSeries;
import org.dflib.series.OffsetLagSeries;
import org.dflib.series.OffsetLeadSeries;
import org.dflib.series.SingleValueSeries;
import org.dflib.series.TrueSeries;
import org.dflib.sort.IntComparator;

import java.lang.reflect.Array;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Random;
import java.util.Set;
import java.util.function.BinaryOperator;
import java.util.function.Predicate;

import static java.util.Arrays.asList;

/**
 * A wrapper around an array of values of a certain type.
 *
 * @param <T>
 */
public interface Series<T> extends Iterable<T> {

    static <S, T> SeriesByElementBuilder<S, T> byElement(Extractor<S, T> extractor) {
        return new SeriesByElementBuilder<>(extractor);
    }

    @SafeVarargs
    static <T> Series<T> of(T... data) {
        return data != null && data.length > 0 ? new ArraySeries<>(data) : new EmptySeries<>();
    }

    static <T> Series<T> ofIterable(Iterable<T> data) {

        return byElement(Extractor.<T>$col())
                .guessCapacity(data)
                .appender()
                .append(data)
                .toSeries();
    }

    static BooleanSeries ofBool(boolean... bools) {
        return BoolBuilder.buildSeries(i -> bools[i], bools.length);
    }

    static IntSeries ofInt(int... ints) {
        return new IntArraySeries(ints);
    }

    /**
     * @since 1.1.0
     */
    static FloatSeries ofFloat(float... floats) {
        return new FloatArraySeries(floats);
    }

    static DoubleSeries ofDouble(double... doubles) {
        return new DoubleArraySeries(doubles);
    }

    static LongSeries ofLong(long... longs) {
        return new LongArraySeries(longs);
    }

    /**
     * Returns a Series of the specified size filled with a single value.
     */
    static <T> Series<T> ofVal(T value, int size) {
        if (value == null) {
            return new SingleValueSeries<>(null, size);
        } else if (value instanceof Integer) {
            return (Series<T>) new IntSingleValueSeries((int) value, size);
        } else if (value instanceof Long) {
            return (Series<T>) new LongSingleValueSeries((long) value, size);
        } else if (value instanceof Double) {
            return (Series<T>) new DoubleSingleValueSeries((double) value, size);
        } else if (value instanceof Boolean) {
            return (boolean) value ? (Series<T>) new TrueSeries(size) : (Series<T>) new FalseSeries(size);
        } else if (value instanceof Float) {
            return (Series<T>) new FloatSingleValueSeries((float) value, size);
        } else {
            return new SingleValueSeries<>(value, size);
        }
    }

    /**
     * Returns a "nominal" type of elements this Series object. Since most Series do not carry the type around, this
     * may be the "Object.class" in all cases except for primitive Series. Use a more expensive
     * {@link #getInferredType()} to check the real type of series values.
     *
     * @return the nominal type of values in the series
     */
    Class<?> getNominalType();

    /**
     * Returns the most specific common superclass of the values in this Series object. If all values are null, returns
     * Object.class. First invocation of this method may be quite slow in many cases, as it entails a full scan of the
     * Series values.
     *
     * @return the most specific common superclass of the values in this Series object.
     */
    Class<?> getInferredType();

    int size();

    T get(int index);

    void copyTo(Object[] to, int fromOffset, int toOffset, int len);

    /**
     * Finds a position of the first occurrence of the value. Returns -1, if the value is not found. Most Series are not
     * indexed, so this operation will have an O(N) performance (unlike O(1) performance of {@link Index#contains(String)}).
     */
    int position(T value);

    /**
     * Returns true if the value is found in the Series. Most Series are not indexed, so this operation will have an
     * O(N) performance (unlike O(1) performance of {@link Index#contains(String)}).
     */
    default boolean contains(T value) {
        return position(value) >= 0;
    }


    /**
     * Parses the provided String argument into an expression and evalutes it with Series, producing a Series of the
     * same size.
     *
     * @since 2.0.0
     */
    default <V> Series<V> map(String mapperExp, Object... params) {
        return (Series<V>) map(Exp.parseExp(mapperExp, params));
    }

    default <V> Series<V> map(Exp<V> mapper) {
        return mapper.eval(this);
    }

    /**
     * Extends the Series, adding provided values to the end of this Series.
     */
    // Didn't name this "add", as "add" in numeric series is an operation for summing two series together
    default Series<?> expand(Object... values) {
        int rlen = values.length;
        if (rlen == 0) {
            return this;
        }

        int llen = size();

        Object[] expanded = new Object[llen + rlen];
        this.copyTo(expanded, 0, 0, llen);
        System.arraycopy(values, 0, expanded, llen, rlen);

        return Series.of(expanded);
    }

    /**
     * Extends the Series, inserting provided values at the specified position.
     *
     * @since 1.2.0
     */
    default Series<?> insert(int pos, Object... values) {

        if (pos < 0) {
            // TODO: treat it as offset from the end?
            throw new IllegalArgumentException("Negative insert position: " + pos);
        }

        int slen = size();
        if (pos > slen) {
            throw new IllegalArgumentException("Insert position past the end of the Series: " + pos + ", len: " + slen);
        }

        int ilen = values.length;
        if (ilen == 0) {
            return this;
        }

        Object[] expanded = new Object[slen + ilen];
        if (pos > 0) {
            this.copyTo(expanded, 0, 0, pos);
        }

        System.arraycopy(values, 0, expanded, pos, ilen);

        if (pos < slen) {
            this.copyTo(expanded, pos, pos + ilen, slen - pos);
        }

        return Series.of(expanded);
    }

    /**
     * Converts the Series to another Series of the same length, applying the provided function.
     *
     * @param mapper a function that maps each Series value to some other value
     * @param <V>    value type produced by the mapper
     * @return a Series produced by applying a mapper to this Series
     */
    default <V> Series<V> map(ValueMapper<T, V> mapper) {
        return new ColumnMappedSeries<>(this, mapper);
    }

    /**
     * Returns this or equivalent Series with "compacted" values. Internally, values will be checked for equality, and
     * any duplicates replaced with a single value. Should be used to save memory for low-cardinality columns.
     *
     * @since 2.0.0
     */
    default Series<T> compact() {
        ValueCompactor<T> compactor = new ValueCompactor<>();

        int s = size();
        Object[] data = new Object[s];

        for (int i = 0; i < s; i++) {
            data[i] = compactor.get(get(i));
        }

        return new ArraySeries(data);
    }

    /**
     * Produces a primitive BooleanSeries by converting each series value to a boolean.
     *
     * @since 2.0.0
     */
    default BooleanSeries compactBool() {
        return compactBool(BoolValueMapper.of());
    }

    /**
     * Produces a primitive BooleanSeries by applying the provided mapper to each Series value.
     *
     * @since 2.0.0
     */
    // TODO: functionally, this is a duplicate of "locate()"
    default BooleanSeries compactBool(BoolValueMapper<? super T> mapper) {
        int len = size();
        return BoolBuilder.buildSeries(i -> mapper.map(get(i)), len);
    }

    /**
     * @deprecated in favor of {@link #compactBool(BoolValueMapper)}
     */
    @Deprecated(since = "2.0.0", forRemoval = true)
    default BooleanSeries mapAsBool(BoolValueMapper<? super T> mapper) {
        return compactBool(mapper);
    }

    /**
     * Produces a primitive IntSeries by converting each series value to an int and replacing nulls with the
     * "forNull" argument.
     *
     * @since 2.0.0
     */
    default IntSeries compactInt(int forNull) {
        return compactInt(IntValueMapper.of(forNull));
    }

    /**
     * Produces a primitive IntSeries by applying the provided mapper to each Series value.
     *
     * @since 2.0.0
     */
    default IntSeries compactInt(IntValueMapper<? super T> mapper) {
        int len = size();

        int[] data = new int[len];
        for (int i = 0; i < len; i++) {
            data[i] = mapper.map(get(i));
        }

        return new IntArraySeries(data);
    }

    /**
     * @deprecated in favor of {@link #compactInt(IntValueMapper)}
     */
    @Deprecated(since = "2.0.0", forRemoval = true)
    default IntSeries mapAsInt(IntValueMapper<? super T> mapper) {
        return compactInt(mapper);
    }

    /**
     * Produces a primitive LongSeries by converting each series value to a long and replacing nulls with the
     * "forNull" argument.
     *
     * @since 2.0.0
     */
    default LongSeries compactLong(long forNull) {
        return compactLong(LongValueMapper.of(forNull));
    }

    /**
     * Produces a primitive LongSeries by applying the provided mapper to each Series value.
     *
     * @since 2.0.0
     */
    default LongSeries compactLong(LongValueMapper<? super T> mapper) {
        int len = size();
        long[] data = new long[len];
        for (int i = 0; i < len; i++) {
            data[i] = mapper.map(get(i));
        }

        return new LongArraySeries(data);
    }

    /**
     * @deprecated in favor of {@link #compactLong(LongValueMapper)}
     */
    @Deprecated(since = "2.0.0", forRemoval = true)
    default LongSeries mapAsLong(LongValueMapper<? super T> mapper) {
        return compactLong(mapper);
    }

    /**
     * Produces a primitive FloatSeries by converting each series value to a long and replacing nulls with the
     * "forNull" argument.
     *
     * @since 2.0.0
     */
    default FloatSeries compactFloat(float forNull) {
        return compactFloat(FloatValueMapper.of(forNull));
    }

    /**
     * Produces a primitive FloatSeries by applying the provided mapper to each Series value.
     *
     * @since 2.0.0
     */
    default FloatSeries compactFloat(FloatValueMapper<? super T> mapper) {
        int len = size();
        float[] data = new float[len];
        for (int i = 0; i < len; i++) {
            data[i] = mapper.map(get(i));
        }

        return new FloatArraySeries(data);
    }

    /**
     * @since 1.1.0
     * @deprecated in favor of {@link #compactFloat(FloatValueMapper)}
     */
    @Deprecated(since = "2.0.0", forRemoval = true)
    default FloatSeries mapAsFloat(FloatValueMapper<? super T> mapper) {
        return compactFloat(mapper);
    }

    /**
     * Produces a primitive DoubleSeries by converting each series value to a long and replacing nulls with the
     * "forNull" argument.
     *
     * @since 2.0.0
     */
    default DoubleSeries compactDouble(double forNull) {
        return compactDouble(DoubleValueMapper.of(forNull));
    }

    /**
     * Produces a primitive DoubleSeries by applying the provided mapper to each Series value.
     *
     * @since 2.0.0
     */
    default DoubleSeries compactDouble(DoubleValueMapper<? super T> mapper) {
        int len = size();
        double[] data = new double[len];
        for (int i = 0; i < len; i++) {
            data[i] = mapper.map(get(i));
        }

        return new DoubleArraySeries(data);
    }

    /**
     * @deprecated in favor of {@link #compactDouble(DoubleValueMapper)}
     */
    @Deprecated(since = "2.0.0", forRemoval = true)
    default DoubleSeries mapAsDouble(DoubleValueMapper<? super T> mapper) {
        return compactDouble(mapper);
    }

    /**
     * Casts the Series to a specific type. This is a fast (aka "unsafe") version of cast that does not check that all
     * elements of the Series conform to the provided type. Instead, it relies on user knowledge of the specific type.
     * In case of user errors, this will cause ClassCastExceptions downstream.  One example where this method is useful
     * is when retrieving untyped Series from DataFrames. Applying a cast allows to keep Series transformation
     * invocations "fluent".
     *
     * @see #castAs(Class)
     */
    default <S> Series<S> unsafeCastAs(Class<S> type) {
        return (Series<S>) this;
    }

    /**
     * Casts the Series to a specific type. This is a potentially slow version of cast that checks that all elements
     * of the Series conform to the provided type. One example where this method is useful is when retrieving untyped
     * Series from DataFrames. Applying a cast allows to keep Series transformation invocations "fluent".
     *
     * @see #unsafeCastAs(Class)
     */
    <S> Series<S> castAs(Class<S> type) throws ClassCastException;

    default BooleanSeries castAsBool() throws ClassCastException {
        throw new ClassCastException("Can't cast to BooleanSeries");
    }

    /**
     * @since 1.1.0
     */
    default FloatSeries castAsFloat() throws ClassCastException {
        throw new ClassCastException("Can't cast to FloatSeries");
    }

    default DoubleSeries castAsDouble() throws ClassCastException {
        throw new ClassCastException("Can't cast to DoubleSeries");
    }

    default IntSeries castAsInt() throws ClassCastException {
        throw new ClassCastException("Can't cast to IntSeries");
    }

    default LongSeries castAsLong() throws ClassCastException {
        throw new ClassCastException("Can't cast to LongSeries");
    }

    /**
     * A map function over the Series that generates a DataFrame of the same height as the length of the Series,
     * which each row produced by applying the map function to each Series value.
     *
     * @return a new DataFrame built from Series values.
     */
    DataFrame map(Index resultColumns, ValueToRowMapper<T> mapper);

    /**
     * Returns a {@link Series} that contains a range of data from this series.
     *
     * @param fromInclusive a left boundary index of the returned range (included in the returned range)
     * @param toExclusive   a right boundary index (excluded in the returned range)
     * @return a Series that contains a sub-range of data from this Series.
     */
    Series<T> selectRange(int fromInclusive, int toExclusive);

    /**
     * Finalizes any lazy calculations that might be still pending in the Series. If called more than once, the first
     * evaluation result is reused.
     */
    Series<T> materialize();

    Series<T> fillNulls(T value);

    /**
     * @param values a Series to take null replacement values from
     * @return a new Series with nulls replaced with values from another Series with matching positions
     */
    Series<T> fillNullsFromSeries(Series<? extends T> values);

    Series<T> fillNullsBackwards();

    Series<T> fillNullsForward();

    /**
     * Combines this Series with multiple other Series. This is an operation similar to SQL "UNION"
     */
    default Series<T> concat(Series<? extends T>... other) {
        if (other.length == 0) {
            return this;
        }

        int size = size();

        int h = size;
        for (Series<? extends T> s : other) {
            h += s.size();
        }

        T[] data = (T[]) new Object[h];
        copyTo(data, 0, 0, size);

        int offset = size;
        for (Series<? extends T> s : other) {
            int len = s.size();
            s.copyTo(data, 0, offset, len);
            offset += len;
        }

        return new ArraySeries<>(data);
    }

    /**
     * Returns a Series with elements from this Series that are not present in another Series. This is an operation
     * similar to SQL "EXCEPT".
     */
    Series<T> diff(Series<? extends T> other);

    /**
     * Returns a Series with elements that are present in this and another Series. This is an operation similar to
     * SQL "INTERSECT".
     */
    Series<T> intersect(Series<? extends T> other);

    /**
     * Returns a Series with the first <code>len</code> elements of this Series. If this Series is shorter than the
     * requested length, then the entire Series is returned. If <code>len</code> is negative, instead of returning the
     * leading elements, they are skipped, and the rest of the Series is returned.
     */
    Series<T> head(int len);

    /**
     * Returns a Series with the last <code>len</code> elements of this Series. If this Series is shorter than the
     * requested length, then the entire Series is returned. If <code>len</code> is negative, instead of returning the
     * trailing elements, they are skipped, and the rest of the Series is returned.
     */
    Series<T> tail(int len);

    /**
     * @since 2.0.0
     */
    default Series<T> select(String conditionalExp, Object... params) {
        return select(Exp.parseExp(conditionalExp, params).castAsBool());
    }

    Series<T> select(Condition condition);

    default Series<T> select(int... positions) {
        return select(Series.ofInt(positions));
    }

    Series<T> select(IntSeries positions);

    Series<T> select(Predicate<T> p);

    Series<T> select(BooleanSeries positions);

    // TODO: can't have "select(boolean...)" as it conflicts with "select(int...)". Should we change to "select(int, int...)" ?

    /**
     * Returns an IntSeries that represents positions in the Series that match the predicate. The returned value can be
     * used to "select" data from this Series or from DataFrame containing this Series.
     *
     * @param predicate match condition
     * @return an IntSeries that represents positions in the Series that match the predicate.
     */
    IntSeries index(Predicate<T> predicate);

    /**
     * Parses the provided string to sorters and calls {@link #sort(Sorter...)}.
     *
     * @since 2.0.0
     */
    default Series<T> sort(String sorters, Object... params) {
        return sort(Sorter.parseSorterArray(sorters, params));
    }

    Series<T> sort(Sorter... sorters);

    /**
     * Returns a sorted copy of this Series using provided Comparator.
     *
     * @return sorted copy of this series.
     */
    Series<T> sort(Comparator<? super T> comparator);

    /**
     * Calculates and returns an IntSeries representing element indices from the original Series in the order dictated
     * by the comparator. This operation is useful when we want to sort another Series based on the ordering of this
     * Series.
     * <p>Note that calling "sortIndexInt" on the result of this method produces a Series of sort-aware row numbers.</p>
     *
     * @param comparator value comparator for sorting¬
     * @return an IntSeries representing element indices from the original Series
     */
    default IntSeries sortIndex(Comparator<? super T> comparator) {
        return IntComparator.of(this, comparator).sortIndex(size());
    }

    /**
     * @param s a Series to compare with.
     * @return a BooleanSeries with true/false elements corresponding to the result of comparison of this Series with
     * another.
     */
    default BooleanSeries eq(Series<?> s) {
        int len = size();

        if (len != s.size()) {
            throw new IllegalArgumentException("Another Series size " + s.size() + " is not the same as this size " + len);
        }

        return BoolBuilder.buildSeries(i -> Objects.equals(get(i), s.get(i)), len);
    }

    /**
     * @param s a Series to compare with.
     * @return a BooleanSeries with true/false elements corresponding to the result of comparison of this Series with
     * another.
     */
    default BooleanSeries ne(Series<?> s) {
        int len = size();

        if (len != s.size()) {
            throw new IllegalArgumentException("Another Series size " + s.size() + " is not the same as this size " + len);
        }

        return BoolBuilder.buildSeries(i -> !Objects.equals(get(i), s.get(i)), len);
    }

    BooleanSeries isNull();

    BooleanSeries isNotNull();

    BooleanSeries in(Object... values);

    BooleanSeries notIn(Object... values);

    /**
     * Returns a boolean series indicating whether each original Series position matched the predicate
     *
     * @param predicate match condition
     * @return a BooleanSeries with true/false elements corresponding whether a given position in "this" Series matched
     * the predicate.
     */
    default BooleanSeries locate(Predicate<T> predicate) {
        // even for primitive Series it is slightly faster to implement "locate" directly than delegating
        // to "locateXyz", because the predicate signature requires primitive boxing
        return BoolBuilder.buildSeries(i -> predicate.test(get(i)), size());
    }

    /**
     * Returns a new Series with the value in the original Series at a given index replaced with the provided value.
     *
     * @since 2.0.0
     */
    default Series<T> replace(int index, T with) {
        return ReplaceOp.replace(this, index, with);
    }

    /**
     * Replaces values at positions with values from another Series. "with" Series should have the same size as
     * the "positions" Series.
     */
    Series<T> replace(IntSeries positions, Series<T> with);

    /**
     * @param condition a BooleanSeries that determines which cells need to be replaced.
     * @param with      a value to replace matching cells with
     * @return a new series with replaced values
     */
    Series<T> replace(BooleanSeries condition, T with);

    /**
     * Replaces Series values that match map keys with map values.
     */
    Series<T> replace(Map<T, T> oldToNewValues);

    /**
     * Creates a new Series replacing the elements from this Series with the provided value at positions that correspond
     * to "false" values in the "condition" Series.
     *
     * @param condition a BooleanSeries that determines which cells need not be replaced.
     * @param with      a value to replace non-matching cells with
     * @return a new series with replaced values
     */
    Series<T> replaceExcept(BooleanSeries condition, T with);

    /**
     * @return a Series that contains non-repeating values from this Series.
     */
    Series<T> unique();

    /**
     * Returns a 2-column DataFrame that provides counts of distinct values present in the Series. Null values are
     * not included in the counts.
     */
    DataFrame valueCounts();

    /**
     * Groups Series by its values.
     */
    SeriesGroupBy<T> group();

    /**
     * Groups Series, using the provided function to calculate grouping key.
     */
    SeriesGroupBy<T> group(ValueMapper<T, ?> by);

    /**
     * Returns a scalar value that is a result of applying provided aggregator to Series.
     *
     * @deprecated in favor of {@link #reduce(Exp)}
     */
    @Deprecated(since = "2.0.0", forRemoval = true)
    default <R> Series<R> agg(Exp<R> aggregator) {
        return Series.ofVal(aggregator.reduce(this), 1);
    }

    /**
     * Parses the provided String into an expression and returns a scalar value that is a result of applying
     * {@link Exp#reduce(Series)} to the Series.
     *
     * @since 2.0.0
     */
    default <R> R reduce(String aggregatingExps, Object... params) {
        return (R) reduce(Exp.parseExp(aggregatingExps, params));
    }

    /**
     * Returns a scalar value that is a result of applying provided aggregator to Series.
     *
     * @since 2.0.0
     */
    default <R> R reduce(Exp<R> aggregatingExps) {
        return aggregatingExps.reduce(this);
    }

    /**
     * Returns a DataFrame, with each column corresponding to the result of aggregation with each aggregator. The number
     * of columns will be equal to the number of passed aggregators.
     */
    default DataFrame aggMultiple(String aggregatingExps, Object... params) {
        return aggMultiple(Exp.parseExpArray(aggregatingExps, params));
    }

    /**
     * Returns a DataFrame, with each column corresponding to the result of aggregation with each aggregator. The number
     * of columns will be equal to the number of passed aggregators.
     */
    default DataFrame aggMultiple(Exp<?>... aggregatingExps) {
        return SeriesAggregator.aggAsDataFrame(this, aggregatingExps);
    }

    default T first() {
        return size() > 0 ? get(0) : null;
    }

    default T last() {
        int size = size();
        return size > 0 ? get(size - 1) : null;
    }

    /**
     * Aggregating operation, concatenating Series values into a single String using the provided element separator.
     */
    default String concat(String separator) {
        return reduce(Exp.$col("").vConcat(separator));
    }

    /**
     * Aggregating operation, concatenating Series values into a single String using the provided element separator,
     * prefix and suffix.
     */
    default String concat(String separator, String prefix, String suffix) {
        return reduce(Exp.$col("").vConcat(separator, prefix, suffix));
    }

    /**
     * Returns a Series object that is a random sample of values from this object, with the specified sample size. If you are
     * doing sampling in a high concurrency application, consider using {@link #sample(int, Random)}, as this method
     * is using a shared {@link Random} instance with synchronization.
     *
     * @param size the size of the sample. Can't be bigger than the size of this Series.
     * @return a Series object that is a sample of values from this object
     */
    Series<T> sample(int size);

    /**
     * Returns a Series object that is a random sample of values from this object, with the specified sample size.
     *
     * @param size   the size of the sample. Can't be bigger than the size of this Series.
     * @param random a custom random number generator
     * @return a Series object that is a sample of values from this object
     */
    Series<T> sample(int size, Random random);

    /**
     * Performs a reduction over the values similarly to {@link java.util.stream.Stream#reduce(Object, BinaryOperator)}.
     * It applies the accumulator over all the values from the Series
     *
     * @param identity    Identity value for the accumulator
     * @param accumulator Associative operator to combine two values
     */
    default T reduce(T identity, BinaryOperator<T> accumulator) {
        Objects.requireNonNull(identity);
        Objects.requireNonNull(accumulator);

        int len = size();
        T result = identity;
        for (int i = 0; i < len; i++) {
            result = accumulator.apply(result, get(i));
        }
        return result;
    }

    /**
     * Produces a Series with the same size as this Series, with values shifted forward or backwards depending on the
     * sign of the offset parameter. Head or tail gaps produced by the shift are filled with nulls.
     */
    default Series<T> shift(int offset) {
        return shift(offset, null);
    }

    /**
     * Produces a Series with the same size as this Series, with values shifted forward or backwards depending on the
     * sign of the offset parameter. Head or tail gaps produced by the shift are filled with the provided filler value.
     */
    default Series<T> shift(int offset, T filler) {
        if (offset > 0) {
            return new OffsetLeadSeries<>(this, offset, filler);
        } else if (offset < 0) {
            return new OffsetLagSeries<>(this, offset, filler);
        } else {
            return this;
        }
    }

    /**
     * @deprecated in favor of an identical {@link #map(Exp)} method.
     */
    @Deprecated(since = "2.0.0", forRemoval = true)
    default <V> Series<V> eval(Exp<V> exp) {
        return map(exp);
    }

    @Override
    default Iterator<T> iterator() {

        return new Iterator<T>() {

            final int len = Series.this.size();
            int i;

            @Override
            public boolean hasNext() {
                return i < len;
            }

            @Override
            public T next() {

                if (!hasNext()) {
                    throw new NoSuchElementException("Past the end of the iterator: " + i);
                }

                return Series.this.get(i++);
            }
        };
    }

    /**
     * Returns a List with this Series values. Returned list is mutable and contains a copy of this Series data, so
     * modifying it will not affect the underlying Series.
     *
     * @return a new List with data from this Series
     */
    default List<T> toList() {
        int len = size();
        Object[] copy = new Object[len];
        copyTo(copy, 0, 0, len);
        return asList((T[]) copy);
    }

    /**
     * Returns a Set with this Series values. Returned set is mutable and contains a copy of this Series data, so
     * modifying it will not affect the underlying Series.
     *
     * @return a new Set with data from this Series
     */
    default Set<T> toSet() {
        int len = size();

        // 1. use a Set with predictable ordering.
        // 2. Start with a set size higher than the default to minimize resizing
        // (have to guess the capacity, as we don't know the Series cardinality)

        int capacity = len < 1000 ? (int) (1 + len / 0.75) : 1300;
        Set<T> set = new LinkedHashSet<>(capacity);
        for (int i = 0; i < len; i++) {
            set.add(get(i));
        }

        return set;
    }

    /**
     * Returns an array with this Series values. Returned array contains a copy of this Series data, so modifying it
     * will not affect the underlying Series.
     *
     * @param a the array into which the elements of this collection are to be stored, if it is big enough; otherwise,
     *          a new array of the same  runtime type is allocated for this purpose. This is similar to
     *          {@link java.util.Collection#toArray(Object[])} behavior.
     * @return a new array with data from this Series
     */
    default T[] toArray(T[] a) {
        int len = size();

        // TODO: wish we could use Series.getType(), but its default implementation returns Object.class, and is useless
        //  here, so we need to rely on the passed array prototype
        T[] copy = a.length < len
                ? (T[]) Array.newInstance(a.getClass().getComponentType(), len)
                : a;

        copyTo(copy, 0, 0, len);
        return copy;
    }
}
