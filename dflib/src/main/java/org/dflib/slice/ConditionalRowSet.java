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

    public ConditionalRowSet(DataFrame source, Series<?>[] sourceColumns, BooleanSeries conditionalIndex) {
        this(source, sourceColumns, -1, conditionalIndex);
    }

    protected ConditionalRowSet(
            DataFrame source,
            Series<?>[] sourceColumns,
            int expansionColumn,
            BooleanSeries conditionalIndex) {
        super(source, sourceColumns, expansionColumn);
        this.conditionalIndex = conditionalIndex;
    }

    @Override
    public RowSet expand(int columnPos) {
        return this.expansionColumn != columnPos
                ? new ConditionalRowSet(source, sourceColumns, columnPos, conditionalIndex)
                : this;
    }

    @Override
    public DataFrame drop() {
        return new ConditionalRowSet(source, sourceColumns, conditionalIndex.not()).select();
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
    protected int size() {
        return conditionalIndex.countTrue();
    }

    @Override
    protected <T> Series<T> doSelect(Series<T> sourceColumn) {
        // TODO: an implicitly lazy impl instead of Series.select(..) to avoid evaluation of unneeded columns when
        //  calculating DefaultRowColumnSet
        return sourceColumn.select(conditionalIndex);
    }

    @Override
    protected RowSetMerger merger() {
        return RowSetMerger.of(conditionalIndex);
    }
}
