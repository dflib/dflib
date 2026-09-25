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

    @Override
    public Series<Number> eval(DataFrame df) {
        return Series.ofVal(reduce(df), df.height());
    }

    @Override
    public Series<Number> eval(Series<?> s) {
        return Series.ofVal(reduce(s), s.size());
    }

    @Override
    public Number reduce(DataFrame df) {
        return NumberTypeReducer.reduce(exp.eval(filtered(df)), op);
    }

    @Override
    public Number reduce(Series<?> s) {
        return NumberTypeReducer.reduce(exp.eval(filtered(s)), op);
    }

    private DataFrame filtered(DataFrame df) {
        return filter != null ? df.rows(filter).select() : df;
    }

    private Series<?> filtered(Series<?> s) {
        return filter != null ? s.select(filter) : s;
    }
}
