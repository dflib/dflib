package com.nhl.dflib.exp.num;

import com.nhl.dflib.NumericExp;
import com.nhl.dflib.Series;
import com.nhl.dflib.Exp;
import com.nhl.dflib.exp.UnaryExp;

import java.util.function.Function;

/**
 * @since 0.11
 */
public class DoubleUnaryExp<F> extends UnaryExp<F, Double> implements NumericExp<Double> {

    public DoubleUnaryExp(String opName, Exp<F> exp, Function<Series<F>, Series<Double>> op) {
        super(opName, exp, Double.class, op);
    }
}
