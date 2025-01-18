package org.dflib.exp.bool;

import org.dflib.BooleanSeries;
import org.dflib.Condition;
import org.dflib.Exp;
import org.dflib.NumExp;
import org.dflib.Series;
import org.dflib.exp.map.MapCondition1;

public class ConditionFactory {

    public static Condition castAsBool(Exp<?> exp) {

        if (exp instanceof Condition) {
            return (Condition) exp;
        }

        Class<?> t = exp.getType();
        if (t.equals(Boolean.class)) {
            Exp<Boolean> bExp = (Exp<Boolean>) exp;
            return MapCondition1.map("castAsBool", bExp, ConditionFactory::castBool);
        }

        if (Number.class.isAssignableFrom(t)) {
            Exp<? extends Number> bExp = (Exp<? extends Number>) exp;
            return MapCondition1.map("castAsBool", bExp, ConditionFactory::castNumber);
        }

        if (t.equals(String.class)) {
            Exp<String> sExp = (Exp<String>) exp;
            return MapCondition1.mapVal("castAsBool", sExp, Boolean::valueOf);
        }

        // if no specific type is set (which is the case with "mapVal" expressions), we will have to run a slow inspection
        // of the individual values
        if (t.equals(Object.class)) {
            return MapCondition1.mapVal("castAsBool", exp, ConditionFactory::isTrue);
        }

        // finally, if the type is something specific that we don't know about,
        return MapCondition1.map("castAsBool", exp, Series::isNotNull);
    }

    public static Condition castAsBool(NumExp<?> exp) {
        return MapCondition1.map("castAsBool", exp, ConditionFactory::castNumber);
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

        return s.compactBool(b -> b != null ? b : false);
    }

    private static BooleanSeries castNumber(Series<? extends Number> s) {
        return s.compactBool(n -> n != null ? n.intValue() != 0 : false);
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
