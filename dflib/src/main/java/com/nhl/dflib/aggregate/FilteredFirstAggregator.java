package com.nhl.dflib.aggregate;

import com.nhl.dflib.*;

import java.util.function.Function;

/**
 * @since 0.7
 */
public class FilteredFirstAggregator<T> implements Aggregator<T> {

    private final SeriesCondition rowFilter;
    private final SeriesExp<T> exp;
    private final Function<Index, String> targetColumnNamer;

    public FilteredFirstAggregator(
            SeriesCondition rowFilter,
            SeriesExp<T> exp,
            Function<Index, String> targetColumnNamer) {

        this.rowFilter = rowFilter;
        this.exp = exp;
        this.targetColumnNamer = targetColumnNamer;
    }

    @Override
    public T aggregate(DataFrame df) {
        int index = rowFilter.firstMatch(df);
        return index < 0 ? null : exp.eval(df.selectRows(index)).get(0);
    }

    @Override
    public String aggregateLabel(Index columnIndex) {
        return targetColumnNamer.apply(columnIndex);
    }

    @Override
    public Aggregator named(String newAggregateLabel) {
        return new FilteredFirstAggregator<>(rowFilter, exp, i -> newAggregateLabel);
    }
}
