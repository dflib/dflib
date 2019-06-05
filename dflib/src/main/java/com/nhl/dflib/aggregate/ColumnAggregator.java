package com.nhl.dflib.aggregate;

import com.nhl.dflib.Aggregator;
import com.nhl.dflib.DataFrame;
import com.nhl.dflib.Index;
import com.nhl.dflib.Series;
import com.nhl.dflib.SeriesAggregator;

import java.util.function.Function;
import java.util.function.ToIntFunction;

/**
 * @see Aggregator for convenience factory methods of creating common ColumnAggregators.
 */
public class ColumnAggregator<S, T> {

    private SeriesAggregator<S, T> aggregator;
    private ToIntFunction<Index> sourceColumnLocator;
    private Function<Index, String> targetColumnNamer;

    public ColumnAggregator(
            SeriesAggregator<S, T> aggregator,
            ToIntFunction<Index> sourceColumnLocator,
            Function<Index, String> targetColumnNamer) {

        this.sourceColumnLocator = sourceColumnLocator;
        this.aggregator = aggregator;
        this.targetColumnNamer = targetColumnNamer;
    }

    protected T aggregate(Series<S> s) {
        return aggregator.aggregate(s);
    }

    public T aggregate(DataFrame df) {
        int pos = sourceColumnLocator.applyAsInt(df.getColumnsIndex());
        return aggregate(df.getColumn(pos));
    }

    public String getLabel(Index columnIndex) {
        return targetColumnNamer.apply(columnIndex);
    }
}
