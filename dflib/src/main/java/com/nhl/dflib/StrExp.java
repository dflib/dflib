package com.nhl.dflib;

import com.nhl.dflib.exp.map.MapExpScalarCondition2;

import java.util.Arrays;
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
        return MapExpScalarCondition2.mapVal("matches", this, regex, (s, r) -> p.matcher(s).matches());
    }

    default Condition startsWith(String prefix) {
        return MapExpScalarCondition2.mapVal("startsWith", this, prefix, (s, p) -> s.startsWith(prefix));
    }

    default Condition endsWith(String suffix) {
        return MapExpScalarCondition2.mapVal("endsWith", this, suffix, (s, p) -> s.endsWith(suffix));
    }


    default Condition in(String[] values) {
        return MapExpScalarCondition2.mapVal("in", this, values, (s, r) -> Arrays.asList(r).contains(s));
    }

    default Condition notIn(String[] values) {
        return MapExpScalarCondition2.mapVal("not in", this, values, (s, r) -> Arrays.stream(r).noneMatch(it-> it.equals(s)));
    }

    default Condition contains(String value) {
        return MapExpScalarCondition2.mapVal("contain", this, value, (s, p) -> s.contains(value));
    }
}
