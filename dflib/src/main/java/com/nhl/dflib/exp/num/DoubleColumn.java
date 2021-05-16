package com.nhl.dflib.exp.num;

import com.nhl.dflib.exp.ColumnExp;
import com.nhl.dflib.NumExp;

/**
 * @since 0.11
 */
public class DoubleColumn extends ColumnExp<Double> implements NumExp<Double> {

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
