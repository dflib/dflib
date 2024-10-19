package org.dflib.exp.num;

import org.dflib.NumExp;
import org.dflib.exp.ScalarExp;

/**
 * @since 2.0.0
 */
public class DoubleScalarExp extends ScalarExp<Double> implements NumExp<Double> {

    public DoubleScalarExp(double value) {
        super(value, Double.class);
    }
}
