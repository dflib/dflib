package com.nhl.dflib;

import com.nhl.dflib.exp.condition.UnaryCondition;

import java.util.regex.Pattern;

/**
 * An expression applied to String columns.
 *
 * @since 0.11
 */
public interface StrExp extends Exp<String> {

    default Condition matches(String regex) {
        // precompile pattern..
        Pattern p = Pattern.compile(regex);
        return new UnaryCondition<>("matches",
                this,
                UnaryCondition.toSeriesCondition(s -> p.matcher(s).matches()));
    }

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
