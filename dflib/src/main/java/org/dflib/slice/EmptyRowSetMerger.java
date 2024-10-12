package org.dflib.slice;

import org.dflib.BooleanSeries;
import org.dflib.Series;

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
    public RowSetMerger expandCols(ColumnExpander expander) {
        return this;
    }

    @Override
    public RowSetMerger stretchCols(ColumnExpander expander) {
        return this;
    }
}
