package com.nhl.dflib.exp.num;

import com.nhl.dflib.Exp;
import com.nhl.dflib.Series;
import com.nhl.dflib.NumericExp;
import com.nhl.dflib.exp.UnaryExp;

import java.util.function.Function;

/**
 * @since 0.11
 */
public class LongUnaryExp<F> extends UnaryExp<F, Long> implements NumericExp<Long> {

    public LongUnaryExp(Exp<F> exp, Function<Series<F>, Series<Long>> op) {
        super(exp, Long.class, op);
    }
}
