package org.dflib.slice;

import org.dflib.BooleanSeries;
import org.dflib.DataFrame;
import org.dflib.Exp;
import org.dflib.IntSeries;
import org.dflib.RowSet;
import org.dflib.Series;
import org.dflib.Sorter;

/**
 * A {@link org.dflib.RowSet} based on BooleanSeries condition.
 */
public class ConditionalRowSet extends BaseRowSet {

    private final BooleanSeries conditionalIndex;

    public ConditionalRowSet(DataFrame source, BooleanSeries conditionalIndex) {
        this(source, null, null, null, conditionalIndex);
    }

    protected ConditionalRowSet(
            DataFrame source,
            Exp<?> expansionExp,
            int[] uniqueColumns,
            Sorter[] sorters,
            BooleanSeries conditionalIndex) {
        super(source, expansionExp, uniqueColumns, sorters);
        this.conditionalIndex = conditionalIndex;
    }

    @Override
    public RowSet expand(Exp<?> splitExp) {
        return new ConditionalRowSet(source, splitExp, uniqueKeyColumns, sorters, conditionalIndex);
    }

    @Override
    public RowSet unique(int... uniqueKeyColumns) {
        return new ConditionalRowSet(source, expansionExp, uniqueKeyColumns, sorters, conditionalIndex);
    }

    @Override
    public RowSet sort(Sorter... sorters) {
        return new ConditionalRowSet(source, expansionExp, uniqueKeyColumns, sorters, conditionalIndex);
    }

    @Override
    public DataFrame drop() {
        return new ConditionalRowSet(source, conditionalIndex.not()).select();
    }

    @Override
    public BooleanSeries locate() {
        return conditionalIndex;
    }

    @Override
    public IntSeries index() {
        return conditionalIndex.indexTrue();
    }

    @Override
    protected <T> Series<T> selectCol(Series<T> sourceColumn) {
        // TODO: an implicitly lazy impl instead of Series.select(..) to avoid evaluation of unneeded columns when
        //  calculating DefaultRowColumnSet
        return sourceColumn.select(conditionalIndex);
    }

    @Override
    protected RowSetMerger createMerger() {
        return RowSetMerger.ofCondition(source, selectRows(), conditionalIndex);
    }
}
