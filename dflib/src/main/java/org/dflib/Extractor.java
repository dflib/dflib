package org.dflib;

import org.dflib.builder.BitsetExtractor;
import org.dflib.builder.BoolExtractor;
import org.dflib.builder.DoubleExtractor;
import org.dflib.builder.FloatExtractor;
import org.dflib.builder.IntExtractor;
import org.dflib.builder.LongExtractor;
import org.dflib.builder.ObjectExtractor;
import org.dflib.builder.SelfExtractor;
import org.dflib.builder.SingleValueExtractor;
import org.dflib.builder.ValueAccum;
import org.dflib.builder.ValueHolder;
import org.dflib.builder.ValueStore;

/**
 * A strategy for extracting single column values from some data source when building a Series or a DataFrame. Reads
 * values from individual source objects passing the results to an abstract "value store". The API allows extraction
 * without "unboxing" the result, and thus can be used for either objects or primitives. Extractors are themselves
 * immutable, but serve as factories of mutable stores that match the extractor primitive type.
 *
 * <p>This API is vaguely symmetrical with {@link Exp}, only it serves to obtains the data from a source that is not
 * a DataFrame or a Series</p>

 */
public interface Extractor<F, T> {

    void extractAndStore(F from, ValueStore<T> to);

    void extractAndStore(F from, ValueStore<T> to, int toPos);

    ValueAccum<T> createAccum(int capacity);

    ValueHolder<T> createHolder();

    /**
     * Returns a version of this extractor that would "compact" extracted values, removing any duplicates based on
     * object equality. For low cardinality columns it can save significant amount of memory and improve GC pauses.
     */
    Extractor<F, T> compact();

    /**
     * Returns an extractor that generates a column filled with a constant value that is known upfront and is not
     * extracted from the source object.
     */
    static <F, T> Extractor<F, T> $val(T val) {
        return new SingleValueExtractor<>(val);
    }

    static <T> Extractor<T, T> $col() {
        return new SelfExtractor<>();
    }

    static <F, T> Extractor<F, T> $col(ValueMapper<F, T> mapper) {
        return new ObjectExtractor<>(mapper);
    }

    static <F> IntExtractor<F> $int(IntValueMapper<F> mapper) {
        return new IntExtractor<>(mapper);
    }

    static <F> LongExtractor<F> $long(LongValueMapper<F> mapper) {
        return new LongExtractor<>(mapper);
    }

    /**
     * @since 1.1.0
     */
    static <F> FloatExtractor<F> $float(FloatValueMapper<F> mapper) {
        return new FloatExtractor<>(mapper);
    }

    static <F> DoubleExtractor<F> $double(DoubleValueMapper<F> mapper) {
        return new DoubleExtractor<>(mapper);
    }

    static <F> BoolExtractor<F> $bool(BoolValueMapper<F> mapper) {
        return new BoolExtractor<>(mapper);
    }

    /**
     * Returns an extractor that generates a boolean column with a bitset-backed storage.
     *
     * @see #$bool(BoolValueMapper)
     * @since 1.1.0
     */
    static <F> BitsetExtractor<F> $bitset(BoolValueMapper<F> mapper) {
        return new BitsetExtractor<>(mapper);
    }
}
