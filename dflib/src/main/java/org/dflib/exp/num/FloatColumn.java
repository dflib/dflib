package org.dflib.exp.num;

import org.dflib.NumExp;
import org.dflib.exp.Column;

/**
 * @since 1.1.0
 */
public class FloatColumn extends Column<Float> implements NumExp<Float> {

    public FloatColumn(String name) {
        super(name, Float.class);
    }

    public FloatColumn(int position) {
        super(position, Float.class);
    }

    @Override
    public String toQL() {
        return position >= 0 ? "$float(" + position + ")" : name;
    }

    @Override
    public NumExp<Float> castAsFloat() {
        return this;
    }
}
