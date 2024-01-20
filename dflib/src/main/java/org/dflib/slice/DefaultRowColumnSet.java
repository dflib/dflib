package org.dflib.slice;

import org.dflib.ColumnSet;
import org.dflib.DataFrame;
import org.dflib.Exp;
import org.dflib.Index;
import org.dflib.RowColumnSet;
import org.dflib.RowMapper;
import org.dflib.RowSet;
import org.dflib.RowToValueMapper;
import org.dflib.Series;
import org.dflib.series.SingleValueSeries;

import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;

/**
 * @since 1.0.0-M19
 */
public class DefaultRowColumnSet implements RowColumnSet {

    private final DataFrame source;
    private final RowSet rowSet;
    private final Function<DataFrame, ColumnSet> columnSetMaker;
    private final Supplier<RowSetMerger> rowSetMergerMaker;

    public DefaultRowColumnSet(
            DataFrame source,
            RowSet rowSet,
            Function<DataFrame, ColumnSet> columnSetMaker,
            Supplier<RowSetMerger> rowSetMergerMaker) {
        this.source = source;
        this.rowSet = rowSet;
        this.columnSetMaker = columnSetMaker;
        this.rowSetMergerMaker = rowSetMergerMaker;
    }

    @Override
    public DataFrame map(Exp<?>... exps) {
        DataFrame rowsResolved = rowSet.select();
        DataFrame hSlice = columnSetMaker.apply(rowsResolved).map(exps);
        return mergeRows(hSlice);
    }

    @Override
    public DataFrame map(RowMapper mapper) {
        DataFrame rowsResolved = rowSet.select();
        DataFrame hSlice = columnSetMaker.apply(rowsResolved).map(mapper);
        return mergeRows(hSlice);
    }

    @Override
    public DataFrame map(RowToValueMapper<?>... mappers) {
        DataFrame rowsResolved = rowSet.select();
        DataFrame hSlice = columnSetMaker.apply(rowsResolved).map(mappers);
        return mergeRows(hSlice);
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
    public DataFrame selectRename(Map<String, String> oldToNewNames) {
        return columnSetMaker.apply(rowSet.select()).selectAs(oldToNewNames);
    }

    @Override
    public DataFrame selectRename(UnaryOperator<String> renamer) {
        return columnSetMaker.apply(rowSet.select()).selectAs(renamer);
    }

    @Override
    public DataFrame selectRename(String... newColumnNames) {
        return columnSetMaker.apply(rowSet.select()).selectAs(newColumnNames);
    }

    private DataFrame mergeRows(DataFrame hSlice) {

        Index hSliceIndex = hSlice.getColumnsIndex();
        DataFrame sourceExpanded = pickColumns(hSliceIndex);

        RowSetMerger merger = rowSetMergerMaker.get();

        int w = hSliceIndex.size();
        Series<?>[] columns = new Series[w];
        for (int i = 0; i < w; i++) {
            columns[i] = merger.merge(sourceExpanded.getColumn(i), hSlice.getColumn(i));
        }

        return DataFrame.byColumn(hSliceIndex).of(columns);
    }

    private DataFrame pickColumns(Index hSliceIndex) {

        Index index = source.getColumnsIndex();
        int w = hSliceIndex.size();
        Series<?>[] columns = new Series[w];
        for (int i = 0; i < w; i++) {
            String name = hSliceIndex.getLabel(i);
            columns[i] = index.hasLabel(name)
                    ? source.getColumn(name)
                    : new SingleValueSeries<>(null, source.height());
        }

        return DataFrame.byColumn(hSliceIndex).of(columns);
    }
}
