package com.nhl.dflib.rowset;

import com.nhl.dflib.BooleanSeries;
import com.nhl.dflib.DataFrame;
import com.nhl.dflib.IntSeries;
import com.nhl.dflib.RowMapper;
import com.nhl.dflib.Series;
import com.nhl.dflib.row.ColumnsRowProxy;
import com.nhl.dflib.row.MultiArrayRowBuilder;

/**
 * @since 1.0.0-M19
 */
public class ConditionalRowSet extends BaseRowSet {

    private final BooleanSeries conditionalIndex;

    public ConditionalRowSet(DataFrame source, Series[] data, BooleanSeries conditionalIndex) {
        super(source, data);
        this.conditionalIndex = conditionalIndex;
    }

    @Override
    protected void mapByRow(RowMapper mapper, ColumnsRowProxy from, MultiArrayRowBuilder to) {
        int h = data[0].size();
        for (int i = 0; i < h; i++) {

            from.next();
            to.next();

            if (conditionalIndex.getBool(i)) {
                mapper.map(from, to);
            } else {
                from.copy(to);
            }
        }
    }

    @Override
    protected <T> Series<T> selectRowSet(Series<T> sourceColumn) {
        return sourceColumn.select(conditionalIndex);
    }

    @Override
    protected <T> Series<T> noResizeMerge(Series<T> sourceColumn, Series<T> rowSetColumn) {
        return doNoResizeMerge(sourceColumn, rowSetColumn, conditionalIndex);
    }

    @Override
    protected <T> Series<T> shrinkMerge(Series<T> sourceColumn, Series<T> rowSetColumn, BooleanSeries rowSetIndex) {
        return doShrinkMerge(sourceColumn, rowSetColumn, conditionalIndex, rowSetIndex);
    }

    @Override
    protected <T> Series<T> explodeMerge(
            Series<T> sourceColumn,
            Series<T> rowSetExplodedColumn,
            IntSeries rowSetStretchCounts) {
        return doExplodeMerge(sourceColumn, rowSetExplodedColumn, conditionalIndex, rowSetStretchCounts);
    }

    @Override
    protected <T> Series<T> stretchMerge(
            Series<T> sourceColumn,
            IntSeries rowSetStretchCounts) {
        return doStretchMerge(sourceColumn, conditionalIndex, rowSetStretchCounts);
    }
}
