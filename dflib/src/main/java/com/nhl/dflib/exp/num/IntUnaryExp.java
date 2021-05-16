package com.nhl.dflib.exp.num;

import com.nhl.dflib.NumExp;
import com.nhl.dflib.Series;
import com.nhl.dflib.Exp;
import com.nhl.dflib.exp.UnaryExp;

import java.util.function.Function;

/**
 * @since 0.11
 */
public class IntUnaryExp<F> extends UnaryExp<F, Integer> implements NumExp<Integer> {

    public IntUnaryExp(String opName, Exp<F> exp, Function<Series<F>, Series<Integer>> op) {
        super(opName, Integer.class, exp, op);
    }
}
