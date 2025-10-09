package org.dflib.exp.agg;

import org.dflib.Series;
import org.dflib.agg.VConcatAgg;

import java.util.function.Function;

/**
 * @deprecated in favor of {@link VConcatAgg}
 */
@Deprecated(since = "2.0.0", forRemoval = true)
public class StringAggregators {

    public static <S> Function<Series<S>, String> vConcat(String delimiter) {
        return s -> VConcatAgg.ofStrings(s, delimiter);
    }

    public static <S> Function<Series<S>, String> vConcat(String delimiter, String prefix, String suffix) {
        return s -> VConcatAgg.ofStrings(s, delimiter, prefix, suffix);
    }
}
