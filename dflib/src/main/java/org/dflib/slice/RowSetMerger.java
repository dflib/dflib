package org.dflib.slice;

import org.dflib.BooleanSeries;
import org.dflib.ColumnDataFrame;
import org.dflib.DataFrame;
import org.dflib.Index;
import org.dflib.IntSeries;
import org.dflib.Series;
import org.dflib.Sorter;
import org.dflib.f.IntObjectFunction2;
import org.dflib.series.IntReverseSequenceSeries;
import org.dflib.series.IntSequenceSeries;
import org.dflib.series.SingleValueSeries;
import org.dflib.sort.IntComparator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.UnaryOperator;

class RowSetMerger {

    private final DataFrame source;
    private final DataFrame rowSet;

    // An index to reconstruct a Series from the original source and a transformed row set. It encodes a source
    // of row value in each position (source Series, or transformed row set series), and also implicitly allows
    // to generate more or less values (compared to the original Series)

    // Model of the index:
    //   "mergeIndex.length":                 merged size, can be bigger, smaller or the same as the source
    //   "i" in 0..length-1:                  merged Series position
    //   "mergeIndex[i]" is negative:         rowSetPos = -1 - i
    //   "mergeIndex[i]" is positive or zero: srcPos = i

    private final IntSeries mergeIndex;

    public static RowSetMerger ofNone(DataFrame source) {
        IntSeries mergeIndex = new IntSequenceSeries(0, source.height());
        return new RowSetMerger(source, DataFrame.empty(source.getColumnsIndex()), mergeIndex);
    }

    public static RowSetMerger ofAll(DataFrame source) {
        IntSeries mergeIndex = new IntReverseSequenceSeries(-1, -1 - source.height());
        return new RowSetMerger(source, source, mergeIndex);
    }

    public static RowSetMerger ofRange(DataFrame source, DataFrame rowSet, int fromInclusive, int toExclusive) {
        int srcLen = source.height();
        int[] mi = new int[srcLen];
        for (int i = 0; i < fromInclusive; i++) {
            mi[i] = i;
        }

        for (int i = fromInclusive; i < toExclusive; i++) {
            mi[i] = -1 - i + fromInclusive;
        }

        for (int i = toExclusive; i < srcLen; i++) {
            mi[i] = i;
        }

        return new RowSetMerger(source, rowSet, Series.ofInt(mi));
    }

    public static RowSetMerger ofIndex(DataFrame source, DataFrame rowSet, IntSeries rowIndex) {

        int srcLen = source.height();

        int[] mi = new int[srcLen];
        for (int i = 0; i < srcLen; i++) {
            mi[i] = i;
        }

        int[] mie = null;

        int h = rowIndex.size();
        int ei = 0;
        for (int i = 0; i < h; i++) {

            int pos = rowIndex.getInt(i);

            if (mi[pos] >= 0) {
                mi[pos] = -i - 1;
            }
            // account for repeating positions in the index
            else {
                if (ei == 0) {
                    mie = new int[h - i];
                }

                mie[ei++] = -i - 1;
            }
        }

        int[] miFinal;
        if (ei > 0) {
            int[] miCombined = Arrays.copyOf(mi, srcLen + ei);
            System.arraycopy(mie, 0, miCombined, srcLen, ei);
            miFinal = miCombined;
        } else {
            miFinal = mi;
        }

        return new RowSetMerger(source, rowSet, Series.ofInt(miFinal));
    }

    public static RowSetMerger ofCondition(DataFrame source, DataFrame rowSet, BooleanSeries condition) {

        int h = source.height();
        int[] mi = new int[h];

        for (int i = 0, rsi = 0; i < h; i++) {
            mi[i] = condition.getBool(i) ? -1 - rsi++ : i;
        }

        return new RowSetMerger(source, rowSet, Series.ofInt(mi));
    }

    private RowSetMerger(DataFrame source, DataFrame rowSet, IntSeries mergeIndex) {
        this.source = source;
        this.rowSet = rowSet;
        this.mergeIndex = mergeIndex;
    }

    public RowSetMerger expand(int expansionCol) {

        if (expansionCol < 0) {
            return this;
        }

        ColumnExpander expander = ColumnExpander.expand(rowSet.getColumn(expansionCol));

        IntSeries srcPositionsExpanded = Series.ofInt(expander.getStretchIndex());
        int w = rowSet.width();

        Series<?>[] cols = new Series[w];

        for (int i = 0; i < w; i++) {
            cols[i] = i == expansionCol
                    ? expander.getExpanded()
                    : rowSet.getColumn(i).select(srcPositionsExpanded);
        }

        return new RowSetMerger(
                source,
                new ColumnDataFrame(null, rowSet.getColumnsIndex(), cols),
                expandMergeIndex(expander));
    }

    public RowSetMerger mapColumns(IntObjectFunction2<DataFrame, Series<?>> colMapper) {

        int w = rowSet.width();

        Series<?>[] cols = new Series[w];
        for (int i = 0; i < w; i++) {
            cols[i] = colMapper.apply(i, rowSet);
        }

        return new RowSetMerger(
                source,
                new ColumnDataFrame(null, rowSet.getColumnsIndex(), cols),
                mergeIndex);
    }

    public RowSetMerger mapDf(UnaryOperator<DataFrame> mapper) {
        return new RowSetMerger(
                source,
                // TODO: check that mapped rowSet has the same geometry as rowSet?
                rowSet.map(mapper),
                mergeIndex);
    }

    public RowSetMerger unique(int[] uniqueKeyColumns) {
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

        return new RowSetMerger(
                source,
                new ColumnDataFrame(null, rowSet.getColumnsIndex(), cols),
                selectMergeIndex(uniqueIndex)
        );
    }

    /**
     * Called explicitly (usually by RowColumnSet) when the RowSet columns are expected (or known) to be different from
     * the source columns, returning a new RowSetMerger with an altered set of columns to match the row set.
     */
    public RowSetMerger syncSourceColumnsFromRowSet() {

        Index index = source.getColumnsIndex();
        Index expandedIndex = rowSet.getColumnsIndex();

        int w = expandedIndex.size();
        Series<?>[] columns = new Series[w];
        for (int i = 0; i < w; i++) {
            String name = expandedIndex.get(i);
            columns[i] = index.contains(name)
                    ? source.getColumn(name)
                    : new SingleValueSeries<>(null, source.height());
        }

        DataFrame expanded = new ColumnDataFrame(null, expandedIndex, columns);
        return new RowSetMerger(expanded, rowSet, mergeIndex);
    }

    public RowSetMerger sort(Sorter... sorters) {
        if (sorters == null || sorters.length == 0) {
            return this;
        }

        IntSeries sortIndex = IntComparator.of(rowSet, sorters).sortIndex(rowSet.height());
        DataFrame sortedRowSet = rowSet.rows(sortIndex).select();
        return new RowSetMerger(source, sortedRowSet, mergeIndex);
    }

    public DataFrame merge() {

        int w = rowSet.width();

        Series<?>[] cols = new Series[w];
        for (int i = 0; i < w; i++) {
            cols[i] = mergeCol(source.getColumn(i), rowSet.getColumn(i));
        }

        return new ColumnDataFrame(null, rowSet.getColumnsIndex(), cols);
    }

    private <T> Series<T> mergeCol(Series<T> srcColumn, Series<T> rsColumn) {

        int h = mergeIndex.size();

        // TODO: primitive Series
        T[] values = (T[]) new Object[h];

        for (int i = 0; i < h; i++) {
            int si = mergeIndex.getInt(i);
            values[i] = si < 0 ? rsColumn.get(-1 - si) : srcColumn.get(si);
        }

        return Series.of(values);
    }

    private IntSeries expandMergeIndex(ColumnExpander expander) {
        IntSeries stretchCounts = expander.getStretchCounts();

        int ch = mergeIndex.size();
        int nn = ch - stretchCounts.size() + expander.getExpanded().size();

        int[] expandedMergeIndex = new int[nn];

        for (int i = 0, si = 0, rsi = 0, et = 0; i < ch; i++) {
            int mv = mergeIndex.getInt(i);

            if (mv < 0) {
                int explodeBy = stretchCounts.getInt(rsi++);
                for (int j = 0; j < explodeBy; j++) {
                    expandedMergeIndex[si++] = mv - et - j;
                }

                // subtract "1", as we are only interested in the expansion delta vs. the original row set
                et += explodeBy - 1;

            } else {
                expandedMergeIndex[si++] = mv;
            }
        }

        return Series.ofInt(expandedMergeIndex);
    }

    private IntSeries selectMergeIndex(IntSeries index) {
        int ih = index.size();
        int ch = mergeIndex.size();
        int nh = ch - rowSet.height() + ih;

        int[] selectedMergeIndex = new int[nh];

        Map<Integer, List<Integer>> newPosByOldPos = new HashMap<>();
        for (int i = 0; i < ih; i++) {
            newPosByOldPos.computeIfAbsent(index.getInt(i), rsi -> new ArrayList<>()).add(i);
        }

        for (int i = 0, si = 0; i < ch; i++) {
            int mv = mergeIndex.getInt(i);

            if (mv < 0) {

                List<Integer> indices = newPosByOldPos.get(-1 - mv);

                if (indices != null) {
                    for (Integer j : indices) {
                        selectedMergeIndex[si++] = -1 - j;
                    }
                }
                // else - delete row (don't add to the produced index)
            } else {
                selectedMergeIndex[si++] = mv;
            }
        }

        return Series.ofInt(selectedMergeIndex);
    }
}
