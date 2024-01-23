package org.dflib.slice;

import org.dflib.BooleanSeries;
import org.dflib.DataFrame;
import org.dflib.Index;
import org.dflib.RowColumnSet;
import org.dflib.RowMapper;
import org.dflib.Series;
import org.dflib.row.ColumnsRowProxy;
import org.dflib.row.MultiArrayRowBuilder;

import java.util.function.Predicate;

/**
 * A {@link org.dflib.RowSet} based on BooleanSeries condition.
 *
 * @since 1.0.0-M19
 */
public class ConditionalRowSet extends BaseRowSet {

    private final BooleanSeries conditionalIndex;

    public ConditionalRowSet(DataFrame source, Series<?>[] sourceColumns, BooleanSeries conditionalIndex) {
        super(source, sourceColumns);
        this.conditionalIndex = conditionalIndex;
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
        return new ConditionalRowSet(source, sourceColumns, conditionalIndex.not()).select();
    }

    @Override
    protected int size() {
        return conditionalIndex.countTrue();
    }

    @Override
    protected void doSelectByRow(RowMapper mapper, ColumnsRowProxy from, MultiArrayRowBuilder to) {
        int h = sourceColumns[0].size();
        for (int i = 0; i < h; i++) {

            from.next();
            if (conditionalIndex.getBool(i)) {
                to.next();
                mapper.map(from, to);
            }
        }
    }

    @Override
    protected <T> Series<T> doSelect(Series<T> sourceColumn) {
        // TODO: an implicitly lazy impl instead of Series.select(..) to avoid evaluation of unneeded columns when
        //  calculating DefaultRowColumnSet
        return sourceColumn.select(conditionalIndex);
    }

    @Override
    protected RowSetMapper mapper() {
        return RowSetMapper.of(conditionalIndex);
    }

    @Override
    protected RowSetMerger merger() {
        return RowSetMerger.of(conditionalIndex);
    }
}
