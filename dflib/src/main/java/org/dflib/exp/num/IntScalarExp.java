package org.dflib.exp.num;

import org.dflib.IntSeries;
import org.dflib.NumExp;
import org.dflib.exp.ScalarExp;
import org.dflib.series.IntSingleValueSeries;

public class IntScalarExp extends ScalarExp<Integer> implements NumExp<Integer> {

    private final int intValue;

    public IntScalarExp(int value) {
        super(value, Integer.class);
        this.intValue = value;
    }

    @Override
    protected IntSeries doEval(int height) {
        return new IntSingleValueSeries(intValue, height);
    }
}
