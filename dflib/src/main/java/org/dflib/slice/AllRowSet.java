package org.dflib.slice;

import org.dflib.BooleanSeries;
import org.dflib.DataFrame;
import org.dflib.IntSeries;
import org.dflib.RowSet;
import org.dflib.Series;
import org.dflib.series.IntSequenceSeries;
import org.dflib.series.TrueSeries;

/**
 * A {@link org.dflib.RowSet} over the entire DataFrame with no filters. May still contain row expansions, etc.
 */
public class AllRowSet extends BaseRowSet {

    public AllRowSet(DataFrame source, Series<?>[] sourceColumns) {
        this(source, sourceColumns, -1, null);
    }

    protected AllRowSet(DataFrame source, Series<?>[] sourceColumns, int expansionColumn, int[] uniqueColumns) {
        super(source, sourceColumns, expansionColumn, uniqueColumns);
    }

    @Override
    public RowSet expand(int columnPos) {
        return this.expansionColumn != columnPos
                ? new AllRowSet(source, sourceColumns, columnPos, uniqueKeyColumns)
                : this;
    }

    @Override
    public RowSet unique(int... uniqueKeyColumns) {
        return new AllRowSet(source, sourceColumns, expansionColumn, uniqueKeyColumns);
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
    protected int size() {
        return source.height();
    }

    @Override
    protected <T> Series<T> selectCol(Series<T> sourceColumn) {
        return sourceColumn;
    }

    @Override
    protected RowSetMerger merger() {
        return RowSetMerger.ofAll();
    }
}
