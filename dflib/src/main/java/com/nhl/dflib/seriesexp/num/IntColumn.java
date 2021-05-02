package com.nhl.dflib.seriesexp.num;

import com.nhl.dflib.seriesexp.ColumnExp;
import com.nhl.dflib.NumericSeriesExp;

/**
 * @since 0.11
 */
public class IntColumn extends ColumnExp<Integer> implements NumericSeriesExp<Integer> {

    public IntColumn(String name) {
        super(name, Integer.class);
    }

    public IntColumn(int position) {
        super(position, Integer.class);
    }
}
