package com.nhl.dflib;

import com.nhl.dflib.exp.condition.UnaryCondition;

/**
 * An expression applied to String columns.
 *
 * @since 0.11
 */
public interface StrExp extends Exp<String> {

    default Condition startsWith(String prefix) {
        return new UnaryCondition<>("startsWith",
                this,
                UnaryCondition.toSeriesCondition(s -> s.startsWith(prefix)));
    }

    default Condition endsWith(String suffix) {
        return new UnaryCondition<>("endsWith",
                this,
                UnaryCondition.toSeriesCondition(s -> s.endsWith(suffix)));
    }
}
