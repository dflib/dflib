package com.nhl.dflib.exp.num;

import com.nhl.dflib.NumericExp;
import com.nhl.dflib.Series;
import com.nhl.dflib.Exp;
import com.nhl.dflib.exp.UnaryExp;

import java.math.BigDecimal;
import java.util.function.Function;

/**
 * @since 0.11
 */
public class DecimalUnaryExp<F> extends UnaryExp<F, BigDecimal> implements NumericExp<BigDecimal> {

    public DecimalUnaryExp(String opName, Exp<F> exp, Function<Series<F>, Series<BigDecimal>> op) {
        super(opName, exp, BigDecimal.class, op);
    }
}
