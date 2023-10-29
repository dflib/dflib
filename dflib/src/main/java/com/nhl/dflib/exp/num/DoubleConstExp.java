package com.nhl.dflib.exp.num;

import com.nhl.dflib.DoubleSeries;
import com.nhl.dflib.NumExp;
import com.nhl.dflib.exp.ExpScalar1;
import com.nhl.dflib.series.DoubleSingleValueSeries;

/**
 * @since 0.19
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
