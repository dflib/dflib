package org.dflib.slice;

import org.dflib.BooleanSeries;
import org.dflib.DataFrame;
import org.dflib.Exp;
import org.dflib.IntSeries;
import org.dflib.RowSet;
import org.dflib.Series;
import org.dflib.Sorter;
import org.dflib.series.IntSequenceSeries;
import org.dflib.series.TrueSeries;

/**
 * A {@link org.dflib.RowSet} over the entire DataFrame with no filters. May still contain row expansions, etc.
 */
public class AllRowSet extends BaseRowSet {

    public AllRowSet(DataFrame source) {
        this(source, null, null, null);
    }

    protected AllRowSet(DataFrame source, Exp<?> expansionExp, int[] uniqueColumns, Sorter[] sorters) {
        super(source, expansionExp, uniqueColumns, sorters);
    }

    @Override
    public RowSet expand(Exp<?> splitExp) {
        return new AllRowSet(source, splitExp, uniqueKeyColumns, sorters);
    }

    @Override
    public RowSet unique(int... uniqueKeyColumns) {
        return new AllRowSet(source, expansionExp, uniqueKeyColumns, sorters);
    }

    @Override
    public RowSet sort(Sorter... sorters) {
        return new AllRowSet(source, expansionExp, uniqueKeyColumns, sorters);
    }

    @Override
    public DataFrame drop() {
        return DataFrame.empty(source.getColumnsIndex());
    }

    @Override
    public BooleanSeries locate() {
        return new TrueSeries(source.height());
    }

    @Override
    public IntSeries index() {
        return new IntSequenceSeries(0, source.height());
    }

    @Override
    protected <T> Series<T> selectCol(Series<T> sourceColumn) {
        return sourceColumn;
    }

    @Override
    protected RowSetMerger createMerger() {
        return RowSetMerger.ofAll(source);
    }
}
