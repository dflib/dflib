package com.nhl.dflib.seriesexp.num;

import com.nhl.dflib.seriesexp.ColumnExp;
import com.nhl.dflib.NumericSeriesExp;

/**
 * @since 0.11
 */
public class LongColumn extends ColumnExp<Long> implements NumericSeriesExp<Long> {

    public LongColumn(String name) {
        super(name, Long.class);
    }

    public LongColumn(int position) {
        super(position, Long.class);
    }
}
