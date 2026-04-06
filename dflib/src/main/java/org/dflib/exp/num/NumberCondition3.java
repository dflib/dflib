package org.dflib.exp.num;

import org.dflib.BooleanSeries;
import org.dflib.Condition;
import org.dflib.DataFrame;
import org.dflib.Exp;
import org.dflib.Series;
import org.dflib.exp.Exp3;

class NumberCondition3 extends Exp3<Number, Number, Number, Boolean> implements Condition {

    private final NumberOps.TernaryCondition op;

    @SuppressWarnings("unchecked")
    NumberCondition3(
            String opName1,
            String opName2,
            Exp<? extends Number> one,
            Exp<? extends Number> two,
            Exp<? extends Number> three,
            NumberOps.TernaryCondition op) {
        super(opName1, opName2, Boolean.class, (Exp<Number>) one, (Exp<Number>) two, (Exp<Number>) three);
        this.op = op;
    }

    @Override
    public BooleanSeries eval(DataFrame df) {
        return NumberTypeResolver.resolve(one.eval(df), two.eval(df), three.eval(df), op).eval(df);
    }

    @Override
    public BooleanSeries eval(Series<?> s) {
        return NumberTypeResolver.resolve(one.eval(s), two.eval(s), three.eval(s), op).eval(s);
    }

    @Override
    public Boolean reduce(DataFrame df) {
        return NumberTypeResolver.resolve(one.reduce(df), two.reduce(df), three.reduce(df), op).reduce(df);
    }

    @Override
    public Boolean reduce(Series<?> s) {
        return NumberTypeResolver.resolve(one.reduce(s), two.reduce(s), three.reduce(s), op).reduce(s);
    }
}
