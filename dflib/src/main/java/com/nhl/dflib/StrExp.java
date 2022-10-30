package com.nhl.dflib;

import com.nhl.dflib.exp.map.MapCondition1;
import com.nhl.dflib.exp.map.MapExpScalarCondition2;

import java.util.regex.Pattern;

/**
 * An expression applied to String columns.
 *
 * @since 0.11
 */
public interface StrExp extends Exp<String> {

    @Override
    default StrExp castAsStr() {
        return this;
    }

    @Override
    default Condition castAsCondition() {
        return MapCondition1.mapVal("castAsCondition", this, Boolean::valueOf);
    }

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
}
