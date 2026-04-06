package org.dflib.exp.num;

import org.dflib.DataFrame;
import org.dflib.Exp;
import org.dflib.NumExp;
import org.dflib.Series;
import org.dflib.exp.Exp2;

class NumberExp2 extends Exp2<Number, Number, Number> implements NumExp<Number> {

    private final NumberOps.Binary op;

    @SuppressWarnings("unchecked")
    NumberExp2(String opName, Exp<? extends Number> left, Exp<? extends Number> right,
               NumberOps.Binary op) {
        super(opName, Number.class, (Exp<Number>) left, (Exp<Number>) right);
        this.op = op;
    }

    @SuppressWarnings("unchecked")
    @Override
    public Series<Number> eval(DataFrame df) {
        return (Series<Number>) NumberTypeResolver.resolve(left.eval(df), right.eval(df), op).eval(df);
    }

    @SuppressWarnings("unchecked")
    @Override
    public Series<Number> eval(Series<?> s) {
        return (Series<Number>) NumberTypeResolver.resolve(left.eval(s), right.eval(s), op).eval(s);
    }

    @Override
    public Number reduce(DataFrame df) {
        return NumberTypeResolver.resolve(left.reduce(df), right.reduce(df), op).reduce(df);
    }

    @Override
    public Number reduce(Series<?> s) {
        return NumberTypeResolver.resolve(left.reduce(s), right.reduce(s), op).reduce(s);
    }
}
