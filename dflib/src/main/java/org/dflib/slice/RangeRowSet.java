package org.dflib.slice;

import org.dflib.DataFrame;
import org.dflib.Index;
import org.dflib.RowColumnSet;
import org.dflib.RowMapper;
import org.dflib.Series;
import org.dflib.range.Range;
import org.dflib.row.ColumnsRowProxy;
import org.dflib.row.MultiArrayRowBuilder;

import java.util.function.Predicate;

/**
 * A {@link org.dflib.RowSet} based on BooleanSeries condition.
 *
 * @since 1.0.0-M19
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
        return sourceColumn.rangeOpenClosed(fromInclusive, toExclusive);
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
