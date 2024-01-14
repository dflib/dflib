package org.dflib.slice;

import org.dflib.DataFrame;
import org.dflib.Index;
import org.dflib.RowColumnSet;
import org.dflib.RowMapper;
import org.dflib.Series;
import org.dflib.row.ColumnsRowProxy;
import org.dflib.row.MultiArrayRowBuilder;

import java.util.function.Predicate;

/**
 * A {@link org.dflib.RowSet} over the entire DataFrame.
 *
 * @since 1.0.0-M19
 */
public class AllRowSet extends BaseRowSet {

    public AllRowSet(DataFrame source, Series<?>[] sourceColumns) {
        super(source, sourceColumns);
    }

    @Override
    public RowColumnSet cols(String... columns) {
        return new AllRowColumnSet(source.cols(columns));
    }

    @Override
    public RowColumnSet cols(Index columnsIndex) {
        return new AllRowColumnSet(source.cols(columnsIndex));
    }

    @Override
    public RowColumnSet cols(int... columns) {
        return new AllRowColumnSet(source.cols(columns));
    }

    @Override
    public RowColumnSet cols(Predicate<String> condition) {
        return new AllRowColumnSet(source.cols(condition));
    }

    @Override
    public RowColumnSet colsExcept(String... columns) {
        return new AllRowColumnSet(source.colsExcept(columns));
    }

    @Override
    public RowColumnSet colsExcept(int... columns) {
        return new AllRowColumnSet(source.colsExcept(columns));
    }

    @Override
    public DataFrame select() {
        return source;
    }

    @Override
    protected int size() {
        return source.height();
    }

    @Override
    protected void selectByRow(RowMapper mapper, ColumnsRowProxy from, MultiArrayRowBuilder to) {
        int h = sourceColumns[0].size();
        for (int i = 0; i < h; i++) {
            from.next();
            to.next();
            mapper.map(from, to);
        }
    }

    @Override
    protected <T> Series<T> select(Series<T> sourceColumn) {
        return sourceColumn;
    }

    @Override
    protected RowSetMapper mapper() {
        return RowSetMapper.ofAll();
    }

    @Override
    protected RowSetMerger merger() {
        return RowSetMerger.ofAll();
    }
}
