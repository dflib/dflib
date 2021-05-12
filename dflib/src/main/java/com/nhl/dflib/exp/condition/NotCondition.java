package com.nhl.dflib.exp.condition;

import com.nhl.dflib.BooleanSeries;
import com.nhl.dflib.Condition;
import com.nhl.dflib.DataFrame;
import com.nhl.dflib.Series;

/**
 * @since 0.11
 */
public class NotCondition implements Condition {

    private final Condition condition;

    public NotCondition(Condition condition) {
        this.condition = condition;
    }

    @Override
    public BooleanSeries eval(DataFrame df) {
        return condition.eval(df).not();
    }

    @Override
    public BooleanSeries eval(Series<?> s) {
        return condition.eval(s).not();
    }

    @Override
    public String getName() {
        return "not(" + condition.getName() + ")";
    }

    @Override
    public String getName(DataFrame df) {
        return "not(" + condition.getName(df) + ")";
    }

    @Override
    public Condition not() {
        return condition;
    }
}
