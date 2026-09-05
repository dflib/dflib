package org.dflib.slice;

import org.dflib.ColumnDataFrame;
import org.dflib.DataFrame;
import org.dflib.Exp;
import org.dflib.Index;
import org.dflib.IntSeries;
import org.dflib.Series;
import org.dflib.Sorter;
import org.dflib.exp.ExpEvaluator;
import org.dflib.f.IntObjectFunction2;
import org.dflib.sort.IntComparator;

import java.util.function.UnaryOperator;

class RowSetSelector {

    private final DataFrame rowSet;

    public RowSetSelector(DataFrame rowSet) {
        this.rowSet = rowSet;
    }

    public RowSetSelector expand(Exp<?> expansionExp) {

        if (expansionExp == null) {
            return this;
        }

        Index index = rowSet.getColumnsIndex();
        String name = expansionExp.getColumnName(rowSet);

        // if the expansion expression maps to an existing column, the expanded values replace that column,
        // otherwise they are appended as a new column
        int expansionCol = index.contains(name) ? index.position(name) : -1;

        ColumnExpander expander = ColumnExpander.expand(expansionExp.eval(rowSet));

        // TODO: if we end up not needing ColumnExpander.getStretchCounts() in the new approach, we should rewrite
        //  ColumnExpander to skip that step and build "stretchIndex" directly bypassing stretch counts?
        IntSeries srcPositionsExpanded = Series.ofInt(expander.getStretchIndex());
        int w = rowSet.width();

        Index expandedIndex = expansionCol < 0 ? index.expand(name) : index;
        Series<?>[] cols = new Series[expandedIndex.size()];

        for (int i = 0; i < w; i++) {
            cols[i] = i == expansionCol
                    ? expander.getExpanded()
                    : rowSet.getColumn(i).select(srcPositionsExpanded);
        }

        if (expansionCol < 0) {
            cols[w] = expander.getExpanded();
        }

        return new RowSetSelector(new ColumnDataFrame(null, expandedIndex, cols));
    }

    public RowSetSelector mapColumns(Exp<?>[] exps) {
        Series<?>[] cols = ExpEvaluator.eval(rowSet, exps);
        return new RowSetSelector(new ColumnDataFrame(null, rowSet.getColumnsIndex(), cols));
    }

    public RowSetSelector mapColumns(IntObjectFunction2<DataFrame, Series<?>> colMapper) {

        int w = rowSet.width();

        Series<?>[] cols = new Series[w];
        for (int i = 0; i < w; i++) {
            cols[i] = colMapper.apply(i, rowSet);
        }

        return new RowSetSelector(new ColumnDataFrame(null, rowSet.getColumnsIndex(), cols));
    }

    public RowSetSelector mapDf(UnaryOperator<DataFrame> mapper) {
        // TODO: check that mapped rowSet has the same geometry as rowSet?
        return new RowSetSelector(rowSet.map(mapper));
    }

    public RowSetSelector unique(int[] uniqueKeyColumns) {
        if (uniqueKeyColumns == null || uniqueKeyColumns.length == 0) {
            return this;
        }

        IntSeries uniqueIndex = rowSet
                .over().partition(uniqueKeyColumns).rowNumber()
                .indexInt(i -> i == 1);

        if (uniqueIndex.size() == 0) {
            return this;
        }

        int w = rowSet.width();
        Series<?>[] cols = new Series[w];
        for (int i = 0; i < w; i++) {
            cols[i] = rowSet.getColumn(i).select(uniqueIndex);
        }

        return new RowSetSelector(new ColumnDataFrame(null, rowSet.getColumnsIndex(), cols));
    }

    public RowSetSelector sort(Sorter... sorters) {
        if (sorters == null || sorters.length == 0) {
            return this;
        }

        IntSeries sortIndex = IntComparator.of(rowSet, sorters).sortIndex(rowSet.height());
        DataFrame sortedRowSet = rowSet.rows(sortIndex).select();
        return new RowSetSelector(sortedRowSet);
    }

    public DataFrame select() {
        return rowSet;
    }
}
