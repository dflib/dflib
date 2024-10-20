package org.dflib.exp.num;

import org.dflib.FloatSeries;
import org.dflib.NumExp;
import org.dflib.exp.ScalarExp;
import org.dflib.series.FloatSingleValueSeries;

/**
 * @since 2.0.0
 */
public class FloatScalarExp extends ScalarExp<Float> implements NumExp<Float> {

    private final float floatValue;

    public FloatScalarExp(float value) {
        super(value, Float.class);
        this.floatValue = value;
    }

    @Override
    protected FloatSeries doEval(int height) {
        return new FloatSingleValueSeries(floatValue, height);
    }
}
