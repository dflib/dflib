package org.dflib.slice;

import org.dflib.BooleanSeries;
import org.dflib.DataFrame;
import org.dflib.IntSeries;
import org.dflib.RowSet;
import org.dflib.Series;
import org.dflib.Sorter;

import java.util.Arrays;

/**
 * A {@link org.dflib.RowSet} based on {@link IntSeries} row selection index.
 */
public class IndexedRowSet extends BaseRowSet {

    private final IntSeries intIndex;

    public IndexedRowSet(DataFrame source, IntSeries intIndex) {
        this(source, -1, null, null, intIndex);
    }

    protected IndexedRowSet(DataFrame source, int expansionColumn, int[] uniqueKeyColumns, Sorter[] sorters, IntSeries intIndex) {
        super(source, expansionColumn, uniqueKeyColumns, sorters);
        this.intIndex = intIndex;
    }

    @Override
    public RowSet expand(int columnPos) {
        return this.expansionColumn != columnPos
                ? new IndexedRowSet(source, columnPos, null, sorters, intIndex)
                : this;
    }

    @Override
    public RowSet unique(int... uniqueKeyColumns) {
        return new IndexedRowSet(source, expansionColumn, uniqueKeyColumns, sorters, intIndex);
    }

    @Override
    public RowSet sort(Sorter... sorters) {
        return new IndexedRowSet(source, expansionColumn, uniqueKeyColumns, sorters, intIndex);
    }

    @Override
    public DataFrame drop() {

        // build an inverted Boolean condition

        int srcLen = source.height();
        boolean[] condition = new boolean[srcLen];
        Arrays.fill(condition, true);

        int iiLen = intIndex.size();
        for (int i = 0; i < iiLen; i++) {
            condition[intIndex.getInt(i)] = false;
        }

        return new ConditionalRowSet(source, Series.ofBool(condition)).select();
    }

    @Override
    public BooleanSeries locate() {
        int h = source.height();
        int ih = intIndex.size();

        boolean[] values = new boolean[h];

        for (int i = 0; i < ih; i++) {
            values[intIndex.getInt(i)] = true;
        }

        return Series.ofBool(values);
    }

    @Override
    public IntSeries index() {
        return intIndex;
    }

    @Override
    protected <T> Series<T> selectCol(Series<T> sourceColumn) {
        return sourceColumn.select(intIndex);
    }

    @Override
    protected RowSetMerger createMerger() {
        return RowSetMerger.ofIndex(source, selectRows(), intIndex);
    }
}
