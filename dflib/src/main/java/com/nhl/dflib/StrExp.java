package com.nhl.dflib;

import com.nhl.dflib.exp.map.MapCondition1;
import com.nhl.dflib.exp.map.MapExp1;
import com.nhl.dflib.exp.map.MapExpScalarCondition2;
import com.nhl.dflib.exp.num.DecimalExp1;
import com.nhl.dflib.exp.num.IntExp1;
import com.nhl.dflib.exp.str.StrExp1;

import java.math.BigDecimal;
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

    /**
     * @since 0.16
     */
    default <E extends Enum<E>> Exp<E> castAsEnum(Class<E> type) {
        return MapExp1.mapVal("castAsEnum", type, this, s -> Enum.valueOf(type, s));
    }

    /**
     * @since 0.16
     */
    @Override
    default NumExp<Integer> castAsInt() {
        return IntExp1.mapVal("castAsInt", this, Integer::valueOf);
    }


    /**
     * @since 0.16
     */
    @Override
    default DecimalExp castAsDecimal() {
        return DecimalExp1.mapVal("castAsDecimal", this, BigDecimal::new);
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

    /**
     * @since 0.16
     */
    default StrExp trim() {
        return StrExp1.mapVal("trim", this, s -> {
            String trimmed = s.trim();
            return trimmed.isEmpty() ? null : trimmed;
        });
    }
}
