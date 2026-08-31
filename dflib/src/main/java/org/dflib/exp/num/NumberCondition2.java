package org.dflib.exp.num;

import org.dflib.BooleanSeries;
import org.dflib.Condition;
import org.dflib.DataFrame;
import org.dflib.Exp;
import org.dflib.Series;
import org.dflib.exp.Exp2;

class NumberCondition2 extends Exp2<Number, Number, Boolean> implements Condition {

    private final NumberOps.BinaryCondition op;

    @SuppressWarnings("unchecked")
    NumberCondition2(
            String opName,
            Exp<? extends Number> left,
            Exp<? extends Number> right,
            NumberOps.BinaryCondition op) {
        super(opName, Boolean.class, (Exp<Number>) left, (Exp<Number>) right);
        this.op = op;
    }

    @Override
    public BooleanSeries eval(DataFrame df) {
        return NumberTypeEvaluator.eval(left.eval(df), right.eval(df), op);
    }

    @Override
    public BooleanSeries eval(Series<?> s) {
        return NumberTypeEvaluator.eval(left.eval(s), right.eval(s), op);
    }

    @Override
    public Boolean reduce(DataFrame df) {
        return NumberTypeReducer.reduce(left.reduce(df), right.reduce(df), op);
    }

    @Override
    public Boolean reduce(Series<?> s) {
        return NumberTypeReducer.reduce(left.reduce(s), right.reduce(s), op);
    }
}
