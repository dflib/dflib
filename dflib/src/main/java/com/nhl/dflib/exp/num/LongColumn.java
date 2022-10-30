package com.nhl.dflib.exp.num;

import com.nhl.dflib.exp.GenericColumn;
import com.nhl.dflib.NumExp;

/**
 * @since 0.11
 */
public class LongColumn extends GenericColumn<Long> implements NumExp<Long> {

    public LongColumn(String name) {
        super(name, Long.class);
    }

    public LongColumn(int position) {
        super(position, Long.class);
    }

    @Override
    public String toQL() {
        return position >= 0 ? "$long(" + position + ")" : name;
    }

    @Override
    public NumExp<Long> castAsLong() {
        return this;
    }
}
