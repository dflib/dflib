package org.dflib.exp.num;

import org.dflib.Condition;
import org.dflib.DataFrame;
import org.dflib.Exp;
import org.dflib.NumExp;
import org.dflib.Series;
import org.dflib.exp.Exp1;

class NumberReduceExp1 extends Exp1<Number, Number> implements NumExp<Number> {

    private final NumberOps.Unary op;
    private final Condition filter;

    @SuppressWarnings("unchecked")
    NumberReduceExp1(
            String opName,
            Exp<? extends Number> exp,
            NumberOps.Unary op,
            Condition filter) {
        super(opName, Number.class, (Exp<Number>) exp);
        this.op = op;
        this.filter = filter;
    }

    @SuppressWarnings("unchecked")
    @Override
    public Series<Number> eval(DataFrame df) {
        return (Series<Number>) resolve(df).eval(df);
    }

    @SuppressWarnings("unchecked")
    @Override
    public Series<Number> eval(Series<?> s) {
        return (Series<Number>) resolve(s).eval(s);
    }

    @Override
    public Number reduce(DataFrame df) {
        return resolve(df).reduce(df);
    }

    @Override
    public Number reduce(Series<?> s) {
        return resolve(s).reduce(s);
    }

    private Exp<? extends Number> resolve(DataFrame df) {
        DataFrame filtered = filter != null ? df.rows(filter).select() : df;
        return NumberTypeResolver.resolve(exp.eval(filtered), op);
    }

    private Exp<? extends Number> resolve(Series<?> s) {
        Series<?> filtered = filter != null ? s.select(filter) : s;
        return NumberTypeResolver.resolve(exp.eval(filtered), op);
    }
}
