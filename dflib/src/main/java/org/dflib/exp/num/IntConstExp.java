package org.dflib.exp.num;

import org.dflib.IntSeries;
import org.dflib.NumExp;
import org.dflib.exp.ExpScalar1;
import org.dflib.series.IntSingleValueSeries;

/**
 * @since 1.0.0-M19
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
