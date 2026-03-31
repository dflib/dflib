package org.dflib.exp.num;

import org.dflib.BooleanSeries;
import org.dflib.Condition;
import org.dflib.DataFrame;
import org.dflib.Exp;
import org.dflib.Series;
import org.dflib.exp.Exp2;

import static org.dflib.exp.num.DynamicNumTypeResolver.*;

class DynamicNumCondition2 extends Exp2<Number, Number, Boolean> implements Condition {

    private final DynamicNumOps.Binary<Condition> op;

    @SuppressWarnings("unchecked")
    DynamicNumCondition2(
            String opName,
            Exp<? extends Number> left,
            Exp<? extends Number> right,
            DynamicNumOps.Binary<Condition> op) {
        super(opName, Boolean.class, (Exp<Number>) left, (Exp<Number>) right);
        this.op = op;
    }

    @Override
    public BooleanSeries eval(DataFrame df) {
        return resolve(left.eval(df), right.eval(df), op).eval(df);
    }

    @Override
    public BooleanSeries eval(Series<?> s) {
        return resolve(left.eval(s), right.eval(s), op).eval(s);
    }

    @Override
    public Boolean reduce(DataFrame df) {
        return resolve(left.reduce(df), right.reduce(df), op).reduce(df);
    }

    @Override
    public Boolean reduce(Series<?> s) {
        return resolve(left.reduce(s), right.reduce(s), op).reduce(s);
    }
}
