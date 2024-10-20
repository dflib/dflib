package org.dflib.exp.num;

import org.dflib.DoubleSeries;
import org.dflib.NumExp;
import org.dflib.exp.ScalarExp;
import org.dflib.series.DoubleSingleValueSeries;

/**
 * @since 2.0.0
 */
public class DoubleScalarExp extends ScalarExp<Double> implements NumExp<Double> {

    private final double doubleValue;

    public DoubleScalarExp(double value) {
        super(value, Double.class);
        this.doubleValue = value;
    }

    @Override
    protected DoubleSeries doEval(int height) {
        return new DoubleSingleValueSeries(doubleValue, height);
    }
}
