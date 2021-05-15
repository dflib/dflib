package com.nhl.dflib.exp.agg;

import com.nhl.dflib.Series;

import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @since 0.11
 */
public class StringAggregators {

    public static <S> Function<Series<S>, String> concat(String delimiter) {
        return CollectorAggregator.create(Collectors.joining(delimiter), String::valueOf);
    }

    public static <S> Function<Series<S>, String> concat(String delimiter, String prefix, String suffix) {
        return CollectorAggregator.create(Collectors.joining(delimiter, prefix, suffix), String::valueOf);
    }
}
