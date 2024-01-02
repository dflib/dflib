package org.dflib.exp.num;

import org.dflib.DoubleSeries;
import org.dflib.NumExp;
import org.dflib.exp.ExpScalar1;
import org.dflib.series.DoubleSingleValueSeries;

/**
 * @since 1.0.0-M19
 */
public class DoubleConstExp extends ExpScalar1<Double> implements NumExp<Double> {

    private final double doubleValue;

    public DoubleConstExp(double value) {
        super(value, Double.class);
        this.doubleValue = value;
    }

    @Override
    protected DoubleSeries doEval(int height) {
        return new DoubleSingleValueSeries(doubleValue, height);
    }
}
