package org.dflib.slice;

import org.dflib.BooleanSeries;
import org.dflib.DataFrame;
import org.dflib.Index;
import org.dflib.IntSeries;
import org.dflib.RowColumnSet;
import org.dflib.RowMapper;
import org.dflib.Series;
import org.dflib.range.Range;
import org.dflib.row.ColumnsRowProxy;
import org.dflib.row.MultiArrayRowBuilder;
import org.dflib.series.IntSequenceSeries;

import java.util.Arrays;
import java.util.function.Predicate;

/**
 * A {@link org.dflib.RowSet} based on BooleanSeries condition.
 */
public class RangeRowSet extends BaseRowSet {

    private final int fromInclusive;
    private final int toExclusive;

    public RangeRowSet(DataFrame source, Series<?>[] sourceColumns, int fromInclusive, int toExclusive) {
        super(source, sourceColumns);

        Range.checkRange(fromInclusive, toExclusive - fromInclusive, source.height());

        this.fromInclusive = fromInclusive;
        this.toExclusive = toExclusive;
    }

    @Override
    public RowColumnSet cols() {
        return new DefaultRowColumnSet(source, this, df -> df.cols(), this::merger);
    }

    @Override
    public RowColumnSet cols(String... columns) {
        return new DefaultRowColumnSet(source, this, df -> df.cols(columns), this::merger);
    }

    @Override
    public RowColumnSet cols(Index columnsIndex) {
        return new DefaultRowColumnSet(source, this, df -> df.cols(columnsIndex), this::merger);
    }

    @Override
    public RowColumnSet cols(int... columns) {
        return new DefaultRowColumnSet(source, this, df -> df.cols(columns), this::merger);
    }

    @Override
    public RowColumnSet cols(Predicate<String> condition) {
        return new DefaultRowColumnSet(source, this, df -> df.cols(condition), this::merger);
    }

    @Override
    public RowColumnSet colsExcept(String... columns) {
        return new DefaultRowColumnSet(source, this, df -> df.colsExcept(columns), this::merger);
    }

    @Override
    public RowColumnSet colsExcept(int... columns) {
        return new DefaultRowColumnSet(source, this, df -> df.colsExcept(columns), this::merger);
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
    protected void doSelectByRow(RowMapper mapper, ColumnsRowProxy from, MultiArrayRowBuilder to) {
        for (int i = fromInclusive; i < toExclusive; i++) {
            from.next(i);
            to.next();
            mapper.map(from, to);
        }
    }

    @Override
    protected <T> Series<T> doSelect(Series<T> sourceColumn) {
        return sourceColumn.selectRange(fromInclusive, toExclusive);
    }

    @Override
    protected RowSetMapper mapper() {
        return RowSetMapper.ofRange(fromInclusive, toExclusive);
    }

    @Override
    protected RowSetMerger merger() {
        return RowSetMerger.ofRange(source.height(), fromInclusive, toExclusive);
    }
}
