package org.dflib.slice;

import org.dflib.Index;
import org.dflib.RowMapper;
import org.dflib.Series;
import org.dflib.row.ColumnsRowProxy;
import org.dflib.row.MultiArrayRowBuilder;

/**
 * @since 1.0.0-M19
 */
public class AllRowSetMapper extends RowSetMapper {

    static final AllRowSetMapper instance = new AllRowSetMapper();

    @Override
    public Series<?>[] map(Index sourceColumnsIndex, Series[] sourceColumns, RowMapper mapper) {
        int h = sourceColumns[0].size();

        ColumnsRowProxy from = new ColumnsRowProxy(sourceColumnsIndex, sourceColumns, h);
        MultiArrayRowBuilder to = new MultiArrayRowBuilder(sourceColumnsIndex, h);

        for (int i = 0; i < h; i++) {
            from.next();
            to.next();
            mapper.map(from, to);
        }

        return to.getData();
    }
}
