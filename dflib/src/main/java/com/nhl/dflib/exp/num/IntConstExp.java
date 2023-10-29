package com.nhl.dflib.exp.num;

import com.nhl.dflib.IntSeries;
import com.nhl.dflib.NumExp;
import com.nhl.dflib.exp.ExpScalar1;
import com.nhl.dflib.series.IntSingleValueSeries;

/**
 * @since 0.19
 */
public class IntConstExp extends ExpScalar1<Integer> implements NumExp<Integer> {

    private final int intValue;

    public IntConstExp(int value) {
        super(value, Integer.class);
        this.intValue = value;
    }

    @Override
    protected IntSeries doEval(int height) {
        return new IntSingleValueSeries(intValue, height);
    }
}
