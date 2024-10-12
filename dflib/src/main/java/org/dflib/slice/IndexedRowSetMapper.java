package org.dflib.slice;

import org.dflib.Index;
import org.dflib.IntSeries;
import org.dflib.RowMapper;
import org.dflib.Series;
import org.dflib.row.ColumnsRowProxy;
import org.dflib.row.MultiArrayRowBuilder;

public class IndexedRowSetMapper extends RowSetMapper {

    private final IntSeries index;

    public IndexedRowSetMapper(IntSeries index) {
        this.index = index;
    }

    @Override
    public Series<?>[] map(Index sourceColumnsIndex, Series[] sourceColumns, RowMapper mapper) {

        int sh = sourceColumns[0].size();

        // TODO: a rather expensive operation run only to presize "to" and determine the strategy.
        //  A cheaper way to do it?
        int rh = resultHeight(sh);

        ColumnsRowProxy from = new ColumnsRowProxy(sourceColumnsIndex, sourceColumns, sh);
        MultiArrayRowBuilder to = new MultiArrayRowBuilder(sourceColumnsIndex, rh);

        int w = sourceColumns.length;

        // Bulk-fill the builder with current values
        for (int i = 0; i < w; i++) {
            to.fill(i, sourceColumns[i], 0, 0, sh);
        }

        return sh < rh
                ? mapNonUniqueIndices(from, to, mapper, sh)
                : mapUniqueIndices(from, to, mapper);
    }

    private Series<?>[] mapNonUniqueIndices(ColumnsRowProxy from, MultiArrayRowBuilder to, RowMapper mapper, int sh) {

        int[] dupeCounts = new int[sh];
        int ih = index.size();

        // transform indexed rows in the order they are found in the original index
        for (int i = 0, eh = 0; i < ih; i++) {
            int si = index.getInt(i);

            dupeCounts[si] += 1;

            from.next(si);
            to.next(dupeCounts[si] == 1 ? si : sh + eh++);
            mapper.map(from, to);
        }

        return to.getData();
    }

    private Series<?>[] mapUniqueIndices(ColumnsRowProxy from, MultiArrayRowBuilder to, RowMapper mapper) {

        int ih = index.size();

        // transform indexed rows in the order they are found in the original index
        for (int i = 0; i < ih; i++) {
            int si = index.getInt(i);
            from.next(si);
            to.next(si);
            mapper.map(from, to);
        }

        return to.getData();
    }

    private int resultHeight(int sh) {

        // TODO: implement IntSeries.group().agg(count()) like this instead of using HashMap for int grouping

        int ih = index.size();
        int[] dupeCounts = new int[sh];
        for (int i = 0; i < ih; i++) {
            int si = index.getInt(i);
            dupeCounts[si] += 1;
        }

        int h = dupeCounts.length;
        int s = 0;
        for (int i = 0; i < h; i++) {
            // "dupeCounts" contains 0's for the rows outside the row set
            s += dupeCounts[i] == 0 ? 1 : dupeCounts[i];
        }

        return s;
    }
}
