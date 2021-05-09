package com.nhl.dflib.seriesexp.str;

import com.nhl.dflib.SeriesExp;
import com.nhl.dflib.seriesexp.BinarySeriesExp;
import com.nhl.dflib.seriesexp.agg.AggregatorFunctions;

/**
 * @since 0.11
 */
public interface StringSeriesExp extends SeriesExp<String> {

    /**
     * String concatenation operation that returns a Series of the same length as the original Series, with values
     * being concatenated pairs of the left and right Series.
     */
    default SeriesExp<String> concat(SeriesExp<String> c) {
        return new BinarySeriesExp<>(
                "+",
                String.class,
                this,
                c,
                BinarySeriesExp.toSeriesOp((s1, s2) -> s1 + s2));
    }

    /**
     * Aggregating String concatenation operation that returns a single-value Series with a String of concatenated
     * values separated by delimiter.
     */
    default SeriesExp<String> vConcat(String delimiter) {
        return agg(AggregatorFunctions.concat(delimiter));
    }

    /**
     * Aggregating String concatenation operation that returns a single-value Series with a String of concatenated
     * values separated by delimiter.
     */
    default SeriesExp<String> vConcat(String delimiter, String prefix, String suffix) {
        return agg(AggregatorFunctions.concat(delimiter, prefix, suffix));
    }
}
