package org.dflib.exp.num;

import org.dflib.DataFrame;
import org.dflib.Exp;
import org.dflib.NumExp;
import org.dflib.Series;
import org.dflib.exp.Exp1;

class NumberExp1 extends Exp1<Number, Number> implements NumExp<Number> {

    private final NumberOps.Unary op;

    @SuppressWarnings("unchecked")
    NumberExp1(String opName, Exp<? extends Number> exp, NumberOps.Unary op) {
        super(opName, Number.class, (Exp<Number>) exp);
        this.op = op;
    }

    @SuppressWarnings("unchecked")
    @Override
    public Series<Number> eval(DataFrame df) {
        return (Series<Number>) NumberTypeResolver.resolve(exp.eval(df), op).eval(df);
    }

    @SuppressWarnings("unchecked")
    @Override
    public Series<Number> eval(Series<?> s) {
        return (Series<Number>) NumberTypeResolver.resolve(exp.eval(s), op).eval(s);
    }

    @Override
    public Number reduce(DataFrame df) {
        return NumberTypeResolver.resolve(exp.reduce(df), op).reduce(df);
    }

    @Override
    public Number reduce(Series<?> s) {
        return NumberTypeResolver.resolve(exp.reduce(s), op).reduce(s);
    }
}
