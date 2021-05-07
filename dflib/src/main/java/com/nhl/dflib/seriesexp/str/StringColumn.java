package com.nhl.dflib.seriesexp.str;

import com.nhl.dflib.SeriesExp;
import com.nhl.dflib.seriesexp.BinarySeriesExp;
import com.nhl.dflib.seriesexp.ColumnExp;

/**
 * @since 0.11
 */
public class StringColumn extends ColumnExp<String> {

    public StringColumn(String name) {
        super(name, String.class);
    }

    public StringColumn(int position) {
        super(position, String.class);
    }

    public SeriesExp<String> concat(SeriesExp<String> c) {
        return new BinarySeriesExp<>(
                "+",
                String.class,
                this,
                c,
                BinarySeriesExp.toSeriesOp((s1, s2) -> s1 + s2));
    }
}
