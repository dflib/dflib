package org.dflib.exp.num;

import org.dflib.exp.Column;
import org.dflib.NumExp;

/**
 * @since 0.11
 */
public class IntColumn extends Column<Integer> implements NumExp<Integer> {

    public IntColumn(String name) {
        super(name, Integer.class);
    }

    public IntColumn(int position) {
        super(position, Integer.class);
    }

    @Override
    public String toQL() {
        return position >= 0 ? "$int(" + position + ")" : name;
    }

    @Override
    public NumExp<Integer> castAsInt() {
        return this;
    }
}
