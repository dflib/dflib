package com.nhl.dflib.aggregate;

import com.nhl.dflib.Aggregator;
import com.nhl.dflib.DataFrame;
import com.nhl.dflib.Index;
import com.nhl.dflib.RowPredicate;
import com.nhl.dflib.row.RowProxy;

import java.util.function.Function;
import java.util.function.ToIntFunction;

/**
 * @since 0.7
 */
public class FirstFilteredAggregator<T> implements Aggregator<T> {

    private RowPredicate rowFilter;
    private ToIntFunction<Index> sourceColumnLocator;
    private Function<Index, String> targetColumnNamer;

    public FirstFilteredAggregator(
            RowPredicate rowFilter,
            ToIntFunction<Index> sourceColumnLocator,
            Function<Index, String> targetColumnNamer) {

        this.rowFilter = rowFilter;
        this.sourceColumnLocator = sourceColumnLocator;
        this.targetColumnNamer = targetColumnNamer;
    }

    @Override
    public T aggregate(DataFrame df) {

        for (RowProxy r : df) {
            if (rowFilter.test(r)) {
                return (T) r.get(sourceColumnLocator.applyAsInt(r.getIndex()));
            }
        }

        return null;
    }

    @Override
    public String aggregateLabel(Index columnIndex) {
        return targetColumnNamer.apply(columnIndex);
    }

    @Override
    public Aggregator named(String newAggregateLabel) {
        return new FirstFilteredAggregator<>(rowFilter, sourceColumnLocator, i -> newAggregateLabel);
    }
}
