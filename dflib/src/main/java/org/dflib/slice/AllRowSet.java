package org.dflib.slice;

import org.dflib.BooleanSeries;
import org.dflib.DataFrame;
import org.dflib.Index;
import org.dflib.IntSeries;
import org.dflib.RowColumnSet;
import org.dflib.RowMapper;
import org.dflib.Series;
import org.dflib.row.ColumnsRowProxy;
import org.dflib.row.MultiArrayRowBuilder;
import org.dflib.series.IntSequenceSeries;
import org.dflib.series.TrueSeries;

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
    public RowColumnSet cols() {
        return new AllRowColumnSet(source.cols());
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
    public DataFrame drop() {
        return DataFrame.empty(source.getColumnsIndex());
    }

    @Override
    public DataFrame select() {
        return source;
    }

    @Override
    public BooleanSeries locate() {
        return new TrueSeries(source.height());
    }

    @Override
    public IntSeries index() {
        return new IntSequenceSeries(0, source.height());
    }

    @Override
    protected int size() {
        return source.height();
    }

    @Override
    protected void doSelectByRow(RowMapper mapper, ColumnsRowProxy from, MultiArrayRowBuilder to) {
        int h = sourceColumns[0].size();
        for (int i = 0; i < h; i++) {
            from.next();
            to.next();
            mapper.map(from, to);
        }
    }

    @Override
    protected <T> Series<T> doSelect(Series<T> sourceColumn) {
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
