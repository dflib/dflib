package org.dflib.exp.num;

import org.dflib.NumExp;
import org.dflib.exp.ScalarExp;

/**
 * @since 2.0.0
 */
public class FloatScalarExp extends ScalarExp<Float> implements NumExp<Float> {

    public FloatScalarExp(float value) {
        super(value, Float.class);
    }
}
