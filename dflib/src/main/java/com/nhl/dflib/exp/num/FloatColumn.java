package com.nhl.dflib.exp.num;

import com.nhl.dflib.NumExp;
import com.nhl.dflib.exp.GenericColumn;

/**
 * @since 0.11
 */
public class FloatColumn extends GenericColumn<Float> implements NumExp<Float> {

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
}
