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

    public IndexedRowSet(DataFrame source, Series<?>[] sourceColumns, IntSeries intIndex) {
        this(source, sourceColumns, -1, intIndex);
    }

    protected IndexedRowSet(DataFrame source, Series<?>[] sourceColumns, int expansionColumn, IntSeries intIndex) {
        super(source, sourceColumns, expansionColumn);
        this.intIndex = intIndex;
    }

    @Override
    public RowSet expand(int columnPos) {
        return this.expansionColumn != columnPos
                ? new IndexedRowSet(source, sourceColumns, columnPos, intIndex)
                : this;
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

        return new ConditionalRowSet(source, sourceColumns, Series.ofBool(condition)).select();
    }

    @Override
    public DataFrame sort(Sorter... sorters) {
        if (intIndex.size() < 2) {
            return source;
        }

        return super.sort(sorters);
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
    protected int size() {
        return intIndex.size();
    }

    @Override
    protected <T> Series<T> doSelect(Series<T> sourceColumn) {
        return sourceColumn.select(intIndex);
    }

    @Override
    protected RowSetMerger merger() {
        return RowSetMerger.of(source.height(), intIndex);
    }
}
