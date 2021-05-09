package com.nhl.dflib.seriesexp.str;

import com.nhl.dflib.seriesexp.ColumnExp;

/**
 * @since 0.11
 */
public class StringColumn extends ColumnExp<String> implements StringSeriesExp {

    public StringColumn(String name) {
        super(name, String.class);
    }

    public StringColumn(int position) {
        super(position, String.class);
    }
}
