package com.nhl.dflib.exp.num;

import com.nhl.dflib.exp.ColumnExp;
import com.nhl.dflib.NumExp;

/**
 * @since 0.11
 */
public class LongColumn extends ColumnExp<Long> implements NumExp<Long> {

    public LongColumn(String name) {
        super(name, Long.class);
    }

    public LongColumn(int position) {
        super(position, Long.class);
    }

    @Override
    public String getName() {
        return position >= 0 ? "$long(" + position + ")" : name;
    }
}
