package org.dflib.exp.num;

import org.dflib.exp.Column;
import org.dflib.NumExp;

/**
 * @since 0.11
 */
public class LongColumn extends Column<Long> implements NumExp<Long> {

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
