package org.dflib.exp;


import org.dflib.BooleanSeries;
import org.dflib.Condition;
import org.dflib.DataFrame;
import org.dflib.Exp;
import org.dflib.Series;

/**
 * @since 0.11
 */
public abstract class ExpScalarCondition2<L, R> implements Condition {

    private final String opName;
    protected final Exp<L> left;
    protected final R right;

    public ExpScalarCondition2(String opName, Exp<L> left, R right) {
        this.opName = opName;
        this.left = left;
        this.right = right;
    }

    @Override
    public String toString() {
        return toQL();
    }

    public String toQL() {
        return left.toQL() + opName + right;
    }

    @Override
    public String toQL(DataFrame df) {
        return left.toQL(df) + opName + right;
    }

    @Override
    public BooleanSeries eval(DataFrame df) {
        return doEval(left.eval(df), right);
    }

    @Override
    public BooleanSeries eval(Series<?> s) {
        return doEval(left.eval(s), right);
    }

    protected abstract BooleanSeries doEval(Series<L> left, R right);
}
