package com.nhl.dflib.aggregate;

import com.nhl.dflib.Aggregator;
import com.nhl.dflib.DataFrame;
import com.nhl.dflib.Index;
import com.nhl.dflib.Series;
import com.nhl.dflib.SeriesAggregator;
import com.nhl.dflib.map.ColumnLocator;

/**
 * @see Aggregator for convenience factory methods of creating common ColumnAggregators.
 */
public class ColumnAggregator<S, T> {

    private SeriesAggregator<S, T> aggregator;
    private ColumnLocator columnLocator;

    public ColumnAggregator(ColumnLocator columnLocator, SeriesAggregator<S, T> aggregator) {
        this.columnLocator = columnLocator;
        this.aggregator = aggregator;
    }

    protected T aggregate(Series<S> s) {
        return aggregator.aggregate(s);
    }

    public T aggregate(DataFrame df) {
        int pos = columnLocator.position(df.getColumnsIndex());
        return aggregate(df.getColumn(pos));
    }

    public String getLabel(Index columnIndex) {
        int pos = columnLocator.position(columnIndex);
        return columnIndex.getLabel(pos);
    }
}
