package com.nhl.dflib.rowset;

import com.nhl.dflib.BooleanSeries;
import com.nhl.dflib.DataFrame;
import com.nhl.dflib.IntSeries;
import com.nhl.dflib.RowMapper;
import com.nhl.dflib.Series;
import com.nhl.dflib.builder.ObjectAccum;
import com.nhl.dflib.row.ColumnsRowProxy;
import com.nhl.dflib.row.MultiArrayRowBuilder;

/**
 * @since 1.0.0-M19
 */
public class AllRowSet extends BaseRowSet {

    public AllRowSet(DataFrame source, Series[] data) {
        super(source, data);
    }

    @Override
    public DataFrame select() {
        return source;
    }

    @Override
    protected void mapByRow(RowMapper mapper, ColumnsRowProxy from, MultiArrayRowBuilder to) {
        int h = data[0].size();
        for (int i = 0; i < h; i++) {
            from.next();
            to.next();
            mapper.map(from, to);
        }
    }

    @Override
    protected <T> Series<T> selectRowSet(Series<T> sourceColumn) {
        return sourceColumn;
    }

    @Override
    protected <T> Series<T> noResizeMerge(Series<T> sourceColumn, Series<T> rowSetColumn) {
        return rowSetColumn;
    }

    @Override
    protected <T> Series<T> shrinkMerge(Series<T> sourceColumn, Series<T> rowSetColumn, BooleanSeries rowSetIndex) {
        return sourceColumn.select(rowSetIndex);
    }

    @Override
    protected <T> Series<T> explodeMerge(
            Series<T> sourceColumn,
            Series<T> rowSetExplodedColumn,
            IntSeries rowSetStretchCounts) {
        return rowSetExplodedColumn;
    }

    @Override
    protected <T> Series<T> stretchMerge(Series<T> sourceColumn, IntSeries rowSetStretchCounts) {
        // TODO: preserve primitive Series

        int len = sourceColumn.size();
        int elen = (int) rowSetStretchCounts.sum();

        ObjectAccum<T> values = new ObjectAccum<>(elen);

        for (int i = 0, mi = 0; i < len; i++) {
            int explode = rowSetStretchCounts.getInt(mi++);

            if (explode > 1) {
                values.fill(values.size(), values.size() + explode, sourceColumn.get(i));
            } else {
                values.push(sourceColumn.get(i));
            }
        }

        return values.toSeries();
    }
}
