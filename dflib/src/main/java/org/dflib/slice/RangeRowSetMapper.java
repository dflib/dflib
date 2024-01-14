package org.dflib.slice;

import org.dflib.Index;
import org.dflib.RowMapper;
import org.dflib.Series;
import org.dflib.row.ColumnsRowProxy;
import org.dflib.row.MultiArrayRowBuilder;

/**
 * @since 1.0.0-M19
 */
public class RangeRowSetMapper extends RowSetMapper {

    private final int fromInclusive;
    private final int toExclusive;

    public RangeRowSetMapper(int fromInclusive, int toExclusive) {
        this.fromInclusive = fromInclusive;
        this.toExclusive = toExclusive;
    }

    @Override
    public Series<?>[] map(Index sourceColumnsIndex, Series[] sourceColumns, RowMapper mapper) {
        int h = sourceColumns[0].size();

        ColumnsRowProxy from = new ColumnsRowProxy(sourceColumnsIndex, sourceColumns, h);
        MultiArrayRowBuilder to = new MultiArrayRowBuilder(sourceColumnsIndex, h);

        int w = sourceColumns.length;

        for (int i = 0; i < w; i++) {
            to.fill(i, sourceColumns[i], 0, 0, h);
        }

        for (int i = fromInclusive; i < toExclusive; i++) {
            from.next(i);
            to.next(i);
            mapper.map(from, to);
        }

        return to.getData();
    }
}
