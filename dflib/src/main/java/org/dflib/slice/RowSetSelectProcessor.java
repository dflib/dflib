package org.dflib.slice;

import org.dflib.BooleanSeries;
import org.dflib.ColumnDataFrame;
import org.dflib.DataFrame;
import org.dflib.Index;
import org.dflib.Series;
import org.dflib.f.IntObjectFunction2;
import org.dflib.series.IntSingleValueSeries;

import java.util.Objects;
import java.util.function.UnaryOperator;

class RowSetSelectProcessor {

    protected final Series<?>[] rowSelection;
    protected BooleanSeries uniqueIndex;
    protected IntObjectFunction2<DataFrame, Series<?>> colMapper;
    private UnaryOperator<DataFrame> mapper;

    public static RowSetSelectProcessor of(Series<?>[] rowSetSelection) {
        return new RowSetSelectProcessor(rowSetSelection);
    }

    protected RowSetSelectProcessor(Series<?>[] rowSelection) {
        this.rowSelection = Objects.requireNonNull(rowSelection);
    }

    public RowSetSelectProcessor expansion(int expansionCol) {

        if (expansionCol < 0) {
            return this;
        }

        doExpansion(expansionCol, ColumnExpander.expand(rowSelection[expansionCol]));
        return this;
    }

    protected void doExpansion(int expansionCol, ColumnExpander expander) {

        int[] stretchIndex = expander.getStretchIndex();
        int w = rowSelection.length;

        for (int i = 0; i < w; i++) {
            rowSelection[i] = i == expansionCol
                    ? expander.getExpanded()
                    : rowSelection[i].select(stretchIndex);
        }
    }

    public RowSetSelectProcessor unique(Index sourceColumnsIndex, int[] uniqueKeyColumns) {
        if (uniqueKeyColumns == null || uniqueKeyColumns.length == 0) {
            return this;
        }

        DataFrame df = new ColumnDataFrame(null, sourceColumnsIndex, rowSelection);
        BooleanSeries uniqueIndex = df
                .over().partition(uniqueKeyColumns).rowNumber()
                .eq(new IntSingleValueSeries(1, df.height()));

        if (!uniqueIndex.isTrue()) {
            this.uniqueIndex = uniqueIndex;
        }

        return this;
    }

    public RowSetSelectProcessor colMapper(IntObjectFunction2<DataFrame, Series<?>> colMapper) {
        this.colMapper = colMapper;
        return this;
    }

    public RowSetSelectProcessor mapper(UnaryOperator<DataFrame> mapper) {
        this.mapper = mapper;
        return this;
    }

    public DataFrame select(Index index) {
        Series<?>[] cols = uniqueIndex != null ? selectUnique(rowSelection) : rowSelection;
        DataFrame df = selectAndMap(index, cols);
        return colMapper != null ? selectColMapper(df) : df;
    }

    private Series[] selectUnique(Series<?>[] src) {
        int w = src.length;
        Series<?>[] cols = new Series[w];
        for (int i = 0; i < w; i++) {
            cols[i] = src[i].select(uniqueIndex);
        }
        return cols;
    }

    protected DataFrame selectAndMap(Index index, Series<?>[] cols) {
        DataFrame rowsDf = new ColumnDataFrame(null, index, cols);
        return mapper != null ? rowsDf.map(mapper) : rowsDf;
    }

    private DataFrame selectColMapper(DataFrame df) {
        int w = df.width();

        Series<?>[] cols = new Series[w];
        for (int i = 0; i < w; i++) {
            cols[i] = colMapper.apply(i, df);
        }

        return new ColumnDataFrame(null, df.getColumnsIndex(), cols);
    }
}
