package com.nhl.dflib.rowset;

import com.nhl.dflib.BooleanSeries;
import com.nhl.dflib.DataFrame;
import com.nhl.dflib.Exp;
import com.nhl.dflib.IntSeries;
import com.nhl.dflib.RowMapper;
import com.nhl.dflib.RowToValueMapper;
import com.nhl.dflib.Series;
import com.nhl.dflib.Sorter;
import com.nhl.dflib.builder.BoolAccum;
import com.nhl.dflib.row.ColumnsRowProxy;
import com.nhl.dflib.row.MultiArrayRowBuilder;

/**
 * @since 1.0.0-M19
 */
public class IndexedRowSet extends BaseRowSet {

    private final IntSeries intIndex;
    private BooleanSeries conditionalIndex;

    public IndexedRowSet(DataFrame source, Series[] data, IntSeries intIndex) {
        super(source, data);
        this.intIndex = intIndex;
    }

    @Override
    public DataFrame map(RowToValueMapper<?>... mappers) {
        if (intIndex.size() == 0) {
            return source;
        }

        return super.map(mappers);
    }

    @Override
    public DataFrame map(Exp<?>... exps) {
        if (intIndex.size() == 0) {
            return source;
        }

        return super.map(exps);
    }

    @Override
    public DataFrame map(RowMapper mapper) {
        if (intIndex.size() == 0) {
            return source;
        }

        return super.map(mapper);
    }

    @Override
    public DataFrame sort(Sorter... sorters) {
        if (intIndex.size() < 2) {
            return source;
        }

        return super.sort(sorters);
    }

    @Override
    protected void mapByRow(RowMapper mapper, ColumnsRowProxy from, MultiArrayRowBuilder to) {

        int w = data.length;
        int h = data[0].size();
        int ih = intIndex.size();

        // Bulk-fill the builder with current values
        for (int i = 0; i < w; i++) {
            to.fill(i, data[i], 0, 0, h);
        }

        // Replace a subset with the mapper-produced values.
        // To ensure mapper correctness (including correctness of its possible side effects), preserve row
        // selection order and even duplicates when invoking the "mapper"

        for (int i = 0; i < ih; i++) {

            int si = intIndex.getInt(i);

            from.next(si);
            to.next(si);
            mapper.map(from, to);
        }
    }

    @Override
    protected <T> Series<T> selectRowSet(Series<T> sourceColumn) {
        return sourceColumn.select(intIndex);
    }

    @Override
    protected <T> Series<T> noResizeMerge(Series<T> sourceColumn, Series<T> rowSetColumn) {
        return sourceColumn.replace(intIndex, rowSetColumn);
    }

    @Override
    protected <T> Series<T> shrinkMerge(Series<T> sourceColumn, Series<T> rowSetColumn, BooleanSeries rowSetIndex) {
        return doShrinkMerge(sourceColumn, rowSetColumn, conditionalIndex(), rowSetIndex);
    }

    @Override
    protected <T> Series<T> explodeMerge(
            Series<T> sourceColumn,
            Series<T> rowSetExplodedColumn,
            IntSeries rowSetStretchCounts) {
        return doExplodeMerge(sourceColumn, rowSetExplodedColumn, conditionalIndex(), rowSetStretchCounts);
    }

    @Override
    protected <T> Series<T> stretchMerge(
            Series<T> sourceColumn,
            IntSeries rowSetStretchCounts) {
        return doStretchMerge(sourceColumn, conditionalIndex(), rowSetStretchCounts);
    }

    private BooleanSeries conditionalIndex() {
        if (this.conditionalIndex == null) {

            BoolAccum accum = new BoolAccum(source.height());

            // by default, the internal boolean[] is filled with "false", so just flip the RowSet rows to "true"
            int ih = intIndex.size();
            for (int i = 0; i < ih; i++) {
                accum.replaceBool(intIndex.getInt(i), true);
            }

            this.conditionalIndex = accum.toSeries();
        }

        return this.conditionalIndex;
    }
}
