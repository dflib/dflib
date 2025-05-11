package org.dflib.exp.bool;

import org.dflib.BooleanSeries;
import org.dflib.Condition;
import org.dflib.DataFrame;
import org.dflib.Series;
import org.dflib.exp.AsExp;

import java.util.Objects;

/**
 * @since 2.0.0
 */
public class AsCondition extends AsExp<Boolean> implements Condition {

    public AsCondition(String name, Condition delegate) {
        super(name, delegate);
    }

    @Override
    public Condition as(String name) {
        return Objects.equals(name, this.name) ? this : new AsCondition(name, (Condition) delegate);
    }

    @Override
    public BooleanSeries eval(Series<?> s) {
        return ((Condition) delegate).eval(s);
    }

    @Override
    public BooleanSeries eval(DataFrame df) {
        return ((Condition) delegate).eval(df);
    }
}
