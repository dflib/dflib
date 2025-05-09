package org.dflib.slice;

import org.dflib.BooleanSeries;
import org.dflib.DataFrame;
import org.dflib.IntSeries;
import org.dflib.RowSet;
import org.dflib.Series;

/**
 * A {@link org.dflib.RowSet} based on BooleanSeries condition.
 */
public class ConditionalRowSet extends BaseRowSet {

    private final BooleanSeries conditionalIndex;

    public ConditionalRowSet(DataFrame source, BooleanSeries conditionalIndex) {
        this(source, -1, null, conditionalIndex);
    }

    protected ConditionalRowSet(
            DataFrame source,
            int expansionColumn,
            int[] uniqueColumns,
            BooleanSeries conditionalIndex) {
        super(source, expansionColumn, uniqueColumns);
        this.conditionalIndex = conditionalIndex;
    }

    @Override
    public RowSet expand(int columnPos) {
        return this.expansionColumn != columnPos
                ? new ConditionalRowSet(source, columnPos, uniqueKeyColumns, conditionalIndex)
                : this;
    }

    @Override
    public RowSet unique(int... uniqueKeyColumns) {
        return new ConditionalRowSet(source, expansionColumn, uniqueKeyColumns, conditionalIndex);
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
