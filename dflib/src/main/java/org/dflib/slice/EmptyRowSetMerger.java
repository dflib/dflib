package org.dflib.slice;

import org.dflib.BooleanSeries;
import org.dflib.IntSeries;
import org.dflib.Series;

/**
 * @since 1.0.0-M19
 */
public class EmptyRowSetMerger extends RowSetMerger {

    static final EmptyRowSetMerger instance = new EmptyRowSetMerger();

    @Override
    public <T> Series<T> merge(Series<T> srcColumn, Series<T> rsColumn) {
        return srcColumn;
    }

    @Override
    public RowSetMerger removeUnmatchedRows(BooleanSeries rsCondition) {
        return this;
    }

    @Override
    public RowSetMerger explodeRows(int rsLen, IntSeries rsStretchCounts) {
        return this;
    }

    @Override
    public RowSetMerger stretchRows(int rsLen, IntSeries rsStretchCounts) {
        return this;
    }
}
