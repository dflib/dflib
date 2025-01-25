package org.dflib.slice;

import org.dflib.ColumnSet;
import org.dflib.DataFrame;
import org.dflib.Exp;
import org.dflib.Index;
import org.dflib.RowColumnSet;
import org.dflib.RowMapper;
import org.dflib.RowToValueMapper;
import org.dflib.Series;
import org.dflib.f.Tuple2;
import org.dflib.series.SingleValueSeries;

import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;

public class DefaultRowColumnSet implements RowColumnSet {

    private final DataFrame source;
    private final BaseRowSet rowSet;
    private final Function<DataFrame, ColumnSet> columnSetMaker;
    private final Supplier<RowSetMerger> rowSetMergerMaker;

    public DefaultRowColumnSet(
            DataFrame source,
            BaseRowSet rowSet,
            Function<DataFrame, ColumnSet> columnSetMaker,
            Supplier<RowSetMerger> rowSetMergerMaker) {
        this.source = source;
        this.rowSet = rowSet;
        this.columnSetMaker = columnSetMaker;
        this.rowSetMergerMaker = rowSetMergerMaker;
    }

    @Override
    public DataFrame merge() {
        Tuple2<Series<?>[], ColumnExpander> rows = rowSet.doSelect();
        DataFrame rowsAsDf = DataFrame.byColumn(source.getColumnsIndex()).of(rows.one);
        DataFrame hSlice = columnSetMaker.apply(rowsAsDf).merge();
        return mergeRows(hSlice, rows.two);
    }

    @Override
    public DataFrame merge(Exp<?>... exps) {
        Tuple2<Series<?>[], ColumnExpander> rows = rowSet.doSelect();
        DataFrame rowsAsDf = DataFrame.byColumn(source.getColumnsIndex()).of(rows.one);

        DataFrame hSlice = columnSetMaker.apply(rowsAsDf).merge(exps);
        return mergeRows(hSlice, rows.two);
    }

    @Override
    public DataFrame merge(RowMapper mapper) {
        Tuple2<Series<?>[], ColumnExpander> rows = rowSet.doSelect();
        DataFrame rowsAsDf = DataFrame.byColumn(source.getColumnsIndex()).of(rows.one);

        DataFrame hSlice = columnSetMaker.apply(rowsAsDf).merge(mapper);
        return mergeRows(hSlice, rows.two);
    }

    @Override
    public DataFrame merge(RowToValueMapper<?>... mappers) {
        Tuple2<Series<?>[], ColumnExpander> rows = rowSet.doSelect();
        DataFrame rowsAsDf = DataFrame.byColumn(source.getColumnsIndex()).of(rows.one);

        DataFrame hSlice = columnSetMaker.apply(rowsAsDf).merge(mappers);
        return mergeRows(hSlice, rows.two);
    }

    @Override
    public DataFrame drop() {
        return columnSetMaker.apply(rowSet.drop()).drop();
    }

    @Override
    public DataFrame select() {
        return columnSetMaker.apply(rowSet.select()).select();
    }

    @Override
    public DataFrame select(Exp<?>... exps) {
        return columnSetMaker.apply(rowSet.select()).select(exps);
    }

    @Override
    public DataFrame select(RowMapper mapper) {
        return columnSetMaker.apply(rowSet.select()).select(mapper);
    }

    @Override
    public DataFrame select(RowToValueMapper<?>... mappers) {
        return columnSetMaker.apply(rowSet.select()).select(mappers);
    }

    @Override
    public DataFrame selectAs(Map<String, String> oldToNewNames) {
        return columnSetMaker.apply(rowSet.select()).selectAs(oldToNewNames);
    }

    @Override
    public DataFrame selectAs(UnaryOperator<String> renamer) {
        return columnSetMaker.apply(rowSet.select()).selectAs(renamer);
    }

    @Override
    public DataFrame selectAs(String... newColumnNames) {
        return columnSetMaker.apply(rowSet.select()).selectAs(newColumnNames);
    }

    private DataFrame mergeRows(DataFrame rowSetRowsResultCols, ColumnExpander expander) {

        Series<?>[] resultAlignedSourceCols = resultAlignedSourceCols(rowSetRowsResultCols.getColumnsIndex());

        RowSetMerger merger = expander != null
                ? rowSetMergerMaker.get().expandCols(expander)
                : rowSetMergerMaker.get();

        int w = resultAlignedSourceCols.length;
        Series<?>[] columns = new Series[w];
        for (int i = 0; i < w; i++) {
            columns[i] = merger.merge(resultAlignedSourceCols[i], rowSetRowsResultCols.getColumn(i));
        }

        return DataFrame.byColumn(rowSetRowsResultCols.getColumnsIndex()).of(columns);
    }

    private Series<?>[] resultAlignedSourceCols(Index resultIndex) {

        Index index = source.getColumnsIndex();
        int w = resultIndex.size();
        Series<?>[] columns = new Series[w];
        for (int i = 0; i < w; i++) {
            String name = resultIndex.get(i);
            columns[i] = index.contains(name)
                    ? source.getColumn(name)
                    : new SingleValueSeries<>(null, source.height());
        }

        return columns;
    }
}
