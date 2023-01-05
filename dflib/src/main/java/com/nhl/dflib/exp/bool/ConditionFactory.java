package com.nhl.dflib.exp.bool;

import com.nhl.dflib.BooleanSeries;
import com.nhl.dflib.Condition;
import com.nhl.dflib.Exp;
import com.nhl.dflib.NumExp;
import com.nhl.dflib.Series;
import com.nhl.dflib.exp.map.MapCondition1;

/**
 * @since 0.14
 */
public class ConditionFactory {

    public static Condition castAsCondition(Exp<?> exp) {

        if (exp instanceof Condition) {
            return (Condition) exp;
        }

        Class<?> t = exp.getType();
        if (t.equals(Boolean.class)) {
            Exp<Boolean> bExp = (Exp<Boolean>) exp;
            return MapCondition1.map("castAsCondition", bExp, ConditionFactory::castBool);
        }

        if (Number.class.isAssignableFrom(t)) {
            Exp<? extends Number> bExp = (Exp<? extends Number>) exp;
            return MapCondition1.map("castAsCondition", bExp, ConditionFactory::castNumber);
        }

        if (t.equals(String.class)) {
            Exp<String> sExp = (Exp<String>) exp;
            return MapCondition1.mapVal("castAsCondition", sExp, Boolean::valueOf);
        }

        // if no specific type is set (which is the case with "mapVal" expressions), we will have to run a slow inspection
        // of the individual values
        if (t.equals(Object.class)) {
            return MapCondition1.mapVal("castAsCondition", exp, ConditionFactory::isTrue);
        }

        // finally, if the type is something specific that we don't know about,
        return MapCondition1.map("castAsCondition", exp, Series::isNotNull);
    }

    public static Condition castAsCondition(NumExp<?> exp) {
        return MapCondition1.map("castAsCondition", exp, ConditionFactory::castNumber);
    }

    public static Condition isNull(Exp<?> exp) {
        return MapCondition1.map("isNull", exp, Series::isNull);
    }

    public static Condition isNotNull(Exp<?> exp) {
        return MapCondition1.map("isNotNull", exp, Series::isNotNull);
    }

    private static BooleanSeries castBool(Series<Boolean> s) {
        if (s instanceof BooleanSeries) {
            return (BooleanSeries) s;
        }

        return s.mapAsBool(b -> b != null ? b : false);
    }

    private static BooleanSeries castNumber(Series<? extends Number> s) {
        return s.mapAsBool(n -> n != null ? n.intValue() != 0 : false);
    }

    private static boolean isTrue(Object o) {

        if (o == null) {
            return false;
        }

        if (o instanceof Number) {
            return ((Number) o).intValue() != 0;
        }

        if (o instanceof Boolean) {
            return (Boolean) o;
        }

        return Boolean.valueOf(o.toString());
    }
}
