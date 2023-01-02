package com.nhl.dflib;

import com.nhl.dflib.builder.BooleanExtractor;
import com.nhl.dflib.builder.DoubleExtractor;
import com.nhl.dflib.builder.IntExtractor;
import com.nhl.dflib.builder.LongExtractor;
import com.nhl.dflib.builder.ObjectExtractor;
import com.nhl.dflib.builder.SelfExtractor;
import com.nhl.dflib.builder.ValueAccum;
import com.nhl.dflib.builder.ValueHolder;
import com.nhl.dflib.builder.ValueStore;

/**
 * A strategy for extracting single column values from some data source when building a Series or a DataFrame. Reads
 * values from individual source objects passing the results to an abstract "value store". The API allows extraction
 * without "unboxing" the result, and thus can be used for either objects or primitives. Extractors are themselves
 * immutable, but serve as factories of mutable stores that match the extractor primitive type.
 *
 * <p>This API is vaguely symmetrical with {@link Exp}, only it serves to obtains the data from a source that is not
 * a DataFrame or a Series</p>
 *
 * @since 0.16
 */
public interface Extractor<F, T> {

    void extractAndStore(F from, ValueStore<T> to);

    void extractAndStore(F from, ValueStore<T> to, int toPos);
    
    ValueAccum<T> createAccum(int capacity);

    ValueHolder<T> createHolder();

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

    static <F> DoubleExtractor<F> $double(DoubleValueMapper<F> mapper) {
        return new DoubleExtractor<>(mapper);
    }

    static <F> BooleanExtractor<F> $bool(BooleanValueMapper<F> mapper) {
        return new BooleanExtractor<>(mapper);
    }
}
