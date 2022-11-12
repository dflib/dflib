package com.nhl.dflib.exp.num;

import com.nhl.dflib.exp.Column;
import com.nhl.dflib.NumExp;

/**
 * @since 0.11
 */
public class DoubleColumn extends Column<Double> implements NumExp<Double> {

    public DoubleColumn(String name) {
        super(name, Double.class);
    }

    public DoubleColumn(int position) {
        super(position, Double.class);
    }

    @Override
    public String toQL() {
        return position >= 0 ? "$double(" + position + ")" : name;
    }

    @Override
    public NumExp<Double> castAsDouble() {
        return this;
    }
}
