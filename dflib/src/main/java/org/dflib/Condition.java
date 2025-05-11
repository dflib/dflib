package org.dflib;

import org.dflib.exp.agg.BoolAggregators;
import org.dflib.exp.bool.AndCondition;
import org.dflib.exp.bool.AsCondition;
import org.dflib.exp.bool.NotCondition;
import org.dflib.exp.bool.OrCondition;
import org.dflib.exp.num.IntExp1;

import java.util.Objects;

/**
 * A {@link Exp} that evaluates to a BooleanSeries indicating whether the condition is true for any given
 * row of the source DataFrame.
 */
public interface Condition extends Exp<Boolean> {

    /**
     * @since 2.0.0
     */
    @Override
    default Condition as(String name) {
        Objects.requireNonNull(name, "Null 'name'");
        return new AsCondition(name, this);
    }

    @Override
    BooleanSeries eval(DataFrame df);

    @Override
    BooleanSeries eval(Series<?> s);

    default int firstMatch(DataFrame df) {
        return eval(df).firstTrue();
    }

    default int firstMatch(Series<?> s) {
        return eval(s).firstTrue();
    }

    default Condition and(Condition c) {
        return new AndCondition(this, c);
    }

    default Condition or(Condition c) {
        return new OrCondition(this, c);
    }

    default Condition not() {
        return new NotCondition(this);
    }

    /**
     * A "running total" function that produces a cumulative sum of each row from the beginning of the DataFrame or
     * Series. "true" value is assumed to be 1, and "false" - 0.
     *
     * @since 1.1.0
     */
    default NumExp<Integer> cumSum() {
        return IntExp1.map("cumSum", this, BoolAggregators::cumSum);
    }

    @Override
    default Class<Boolean> getType() {
        return Boolean.class;
    }

    @Override
    default Condition castAsBool() {
        return this;
    }
}
