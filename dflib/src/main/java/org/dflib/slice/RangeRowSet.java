package org.dflib.slice;

import org.dflib.BooleanSeries;
import org.dflib.DataFrame;
import org.dflib.IntSeries;
import org.dflib.RowSet;
import org.dflib.Series;
import org.dflib.range.Range;
import org.dflib.series.IntSequenceSeries;

import java.util.Arrays;

/**
 * A {@link org.dflib.RowSet} based on BooleanSeries condition.
 */
public class RangeRowSet extends BaseRowSet {

    private final int fromInclusive;
    private final int toExclusive;

    public RangeRowSet(DataFrame source, Series<?>[] sourceColumns, int fromInclusive, int toExclusive) {
        this(source, sourceColumns, -1, null, fromInclusive, toExclusive);
    }

    protected RangeRowSet(DataFrame source, Series<?>[] sourceColumns, int expansionColumn, int[] uniqueKeyColumns, int fromInclusive, int toExclusive) {
        super(source, sourceColumns, expansionColumn, uniqueKeyColumns);

        Range.checkRange(fromInclusive, toExclusive - fromInclusive, source.height());

        this.fromInclusive = fromInclusive;
        this.toExclusive = toExclusive;
    }

    @Override
    public RowSet expand(int columnPos) {
        return this.expansionColumn != columnPos
                ? new RangeRowSet(source, sourceColumns, columnPos, uniqueKeyColumns, fromInclusive, toExclusive)
                : this;
    }

    @Override
    public RowSet unique(int... uniqueKeyColumns) {
        return new RangeRowSet(source, sourceColumns, expansionColumn, uniqueKeyColumns, fromInclusive, toExclusive);
    }

    @Override
    public DataFrame drop() {
        int srcLen = source.height();
        int[] index = new int[srcLen - size()];

        int ii = 0;
        for (int i = 0; i < fromInclusive; i++) {
            index[ii++] = i;
        }

        for (int i = toExclusive; i < srcLen; i++) {
            index[ii++] = i;
        }

        return new IndexedRowSet(source, sourceColumns, Series.ofInt(index)).select();
    }

    @Override
    public BooleanSeries locate() {
        int h = source.height();
        boolean[] values = new boolean[h];
        Arrays.fill(values, fromInclusive, toExclusive, true);
        return Series.ofBool(values);
    }

    @Override
    public IntSeries index() {
        return new IntSequenceSeries(fromInclusive, toExclusive);
    }

    @Override
    protected int size() {
        return toExclusive - fromInclusive;
    }

    @Override
    protected <T> Series<T> selectCol(Series<T> sourceColumn) {
        return sourceColumn.selectRange(fromInclusive, toExclusive);
    }

    @Override
    protected RowSetMerger merger() {
        return RowSetMerger.ofRange(source.height(), fromInclusive, toExclusive);
    }
}
