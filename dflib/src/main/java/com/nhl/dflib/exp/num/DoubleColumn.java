package com.nhl.dflib.exp.num;

import com.nhl.dflib.exp.GenericColumn;
import com.nhl.dflib.NumExp;

/**
 * @since 0.11
 */
public class DoubleColumn extends GenericColumn<Double> implements NumExp<Double> {

    public DoubleColumn(String name) {
        super(name, Double.class);
    }

    public DoubleColumn(int position) {
        super(position, Double.class);
    }

    @Override
    public String getName() {
        return position >= 0 ? "$double(" + position + ")" : name;
    }
}
