package org.dflib.exp.num;

import org.dflib.FloatSeries;
import org.dflib.NumExp;
import org.dflib.exp.ExpScalar1;
import org.dflib.series.FloatSingleValueSeries;

/**
 * @since 1.1.0
 */
public class FloatConstExp extends ExpScalar1<Float> implements NumExp<Float> {

    private final float floatValue;

    public FloatConstExp(float value) {
        super(value, Float.class);
        this.floatValue = value;
    }

    @Override
    protected FloatSeries doEval(int height) {
        return new FloatSingleValueSeries(floatValue, height);
    }
}
