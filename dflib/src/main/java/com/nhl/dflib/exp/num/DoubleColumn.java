package com.nhl.dflib.exp.num;

import com.nhl.dflib.exp.ColumnExp;
import com.nhl.dflib.NumericExp;

/**
 * @since 0.11
 */
public class DoubleColumn extends ColumnExp<Double> implements NumericExp<Double> {

    public DoubleColumn(String name) {
        super(name, Double.class);
    }

    public DoubleColumn(int position) {
        super(position, Double.class);
    }
}
