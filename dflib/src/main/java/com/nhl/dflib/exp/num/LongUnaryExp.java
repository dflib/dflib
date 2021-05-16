package com.nhl.dflib.exp.num;

import com.nhl.dflib.NumExp;
import com.nhl.dflib.Series;
import com.nhl.dflib.Exp;
import com.nhl.dflib.exp.UnaryExp;

import java.util.function.Function;

/**
 * @since 0.11
 */
public class LongUnaryExp<F> extends UnaryExp<F, Long> implements NumExp<Long> {

    public LongUnaryExp(String opName, Exp<F> exp, Function<Series<F>, Series<Long>> op) {
        super(opName, Long.class, exp, op);
    }
}
