package org.dflib.exp.agg;

import org.dflib.Series;

import java.util.function.Function;
import java.util.stream.Collectors;


public class StringAggregators {

    public static <S> Function<Series<S>, String> vConcat(String delimiter) {
        return CollectorAggregator.create(Collectors.joining(delimiter), String::valueOf);
    }

    public static <S> Function<Series<S>, String> vConcat(String delimiter, String prefix, String suffix) {
        return CollectorAggregator.create(Collectors.joining(delimiter, prefix, suffix), String::valueOf);
    }
}
