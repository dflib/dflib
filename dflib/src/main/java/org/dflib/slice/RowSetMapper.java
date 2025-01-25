package org.dflib.slice;

import org.dflib.BooleanSeries;
import org.dflib.Index;
import org.dflib.IntSeries;
import org.dflib.RowMapper;
import org.dflib.Series;

/**
 * @deprecated unused
 */
@Deprecated(since = "2.0.0", forRemoval = true)
public abstract class RowSetMapper {

    public static RowSetMapper of(IntSeries rowIndex) {
        return new IndexedRowSetMapper(rowIndex);
    }

    public static RowSetMapper of(BooleanSeries rowCondition) {
        return new ConditionalRowSetMapper(rowCondition);
    }

    public static RowSetMapper ofRange(int fromInclusive, int toExclusive) {
        return new RangeRowSetMapper(fromInclusive, toExclusive);
    }

    public static RowSetMapper ofAll() {
        return AllRowSetMapper.instance;
    }

    public abstract Series<?>[] map(Index sourceColumnsIndex, Series[] sourceColumns, RowMapper mapper);
}
