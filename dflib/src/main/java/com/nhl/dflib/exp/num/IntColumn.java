package com.nhl.dflib.exp.num;

import com.nhl.dflib.exp.GenericColumn;
import com.nhl.dflib.NumExp;

/**
 * @since 0.11
 */
public class IntColumn extends GenericColumn<Integer> implements NumExp<Integer> {

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

}
