package org.dflib.slice;

import org.dflib.ColumnDataFrame;
import org.dflib.DataFrame;
import org.dflib.Index;
import org.dflib.IntSeries;
import org.dflib.Series;
import org.dflib.f.IntObjectFunction2;
import org.dflib.series.SingleValueSeries;

import java.util.function.UnaryOperator;

class RowSetMergeProcessor extends RowSetSelectProcessor {

    private final RowSetMerger merger;
    private IntSeries stretchCounts;

    public static RowSetMergeProcessor of(Series<?>[] rowSetSelection, RowSetMerger merger) {
        return new RowSetMergeProcessor(rowSetSelection, merger);
    }

    private RowSetMergeProcessor(Series<?>[] rowSelection, RowSetMerger merger) {
        super(rowSelection);
        this.merger = merger;
    }

    @Override
    public RowSetMergeProcessor expansion(int expansionCol) {
        return (RowSetMergeProcessor) super.expansion(expansionCol);
    }

    @Override
    protected void doExpansion(int expansionCol, ColumnExpander expander) {
        super.doExpansion(expansionCol, expander);
        this.stretchCounts = expander.getStretchCounts();
    }

    @Override
    public RowSetMergeProcessor unique(Index sourceColumnsIndex, int[] uniqueKeyColumns) {
        return (RowSetMergeProcessor) super.unique(sourceColumnsIndex, uniqueKeyColumns);
    }

    @Override
    public RowSetMergeProcessor colMapper(IntObjectFunction2<DataFrame, Series<?>> colMapper) {
        return (RowSetMergeProcessor) super.colMapper(colMapper);
    }

    @Override
    public RowSetMergeProcessor mapper(UnaryOperator<DataFrame> mapper) {
        return (RowSetMergeProcessor) super.mapper(mapper);
    }

    public DataFrame merge(DataFrame srcDf, boolean colsCanChange) {
        // TODO: is it possible to somehow apply optional "unique" BEFORE the mapper?
        DataFrame rowSetDf = selectAndMap(srcDf.getColumnsIndex(), rowSelection);
        RowSetMerger merger = adjustMerger();

        // "colsCanChange" is true for RowColumnSet and false for RowSet caller
        DataFrame alignedSrcDf = colsCanChange ? fillMissingColumns(srcDf, rowSetDf.getColumnsIndex()) : srcDf;
        return colMapper != null ? mergeColMapper(merger, alignedSrcDf, rowSetDf) : merge(merger, alignedSrcDf, rowSetDf);
    }

    private RowSetMerger adjustMerger() {
        RowSetMerger merger = this.merger;
        if (stretchCounts != null) {
            merger = merger.expandCols(
                    stretchCounts,
                    rowSelection.length > 0 ? rowSelection[0].size() : 0);
        }

        if (uniqueIndex != null) {
            merger = merger.removeUnmatchedRows(uniqueIndex);
        }

        return merger;
    }

    private DataFrame mergeColMapper(RowSetMerger merger, DataFrame srcDf, DataFrame rowSetDf) {
        int w = rowSetDf.width();

        Series<?>[] cols = new Series[w];
        for (int i = 0; i < w; i++) {
            // TODO: is it possible to apply optional "unique" BEFORE "colMapper"?
            cols[i] = merger.merge(srcDf.getColumn(i), colMapper.apply(i, rowSetDf));
        }

        return new ColumnDataFrame(null, rowSetDf.getColumnsIndex(), cols);
    }

    private DataFrame merge(RowSetMerger merger, DataFrame srcDf, DataFrame rowSetDf) {
        int w = rowSetDf.width();

        Series<?>[] cols = new Series[w];
        for (int i = 0; i < w; i++) {
            // TODO: is it possible to apply optional "unique" BEFORE "colMapper"?
            cols[i] = merger.merge(srcDf.getColumn(i), rowSetDf.getColumn(i));
        }

        return new ColumnDataFrame(null, rowSetDf.getColumnsIndex(), cols);
    }

    private DataFrame fillMissingColumns(DataFrame df, Index expandedIndex) {

        Index index = df.getColumnsIndex();
        int w = expandedIndex.size();
        Series<?>[] columns = new Series[w];
        for (int i = 0; i < w; i++) {
            String name = expandedIndex.get(i);
            columns[i] = index.contains(name)
                    ? df.getColumn(name)
                    : new SingleValueSeries<>(null, df.height());
        }

        return new ColumnDataFrame(null, expandedIndex, columns);
    }
}
