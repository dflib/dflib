package com.nhl.dflib.exp.datetime;

import com.nhl.dflib.Condition;
import com.nhl.dflib.DateExp;
import com.nhl.dflib.Exp;
import com.nhl.dflib.exp.map.MapCondition2;

import java.time.LocalDate;

/**
 * @since 0.16
 */
public class DateFactory {

    public static Condition lt(Exp<LocalDate> left, Exp<LocalDate> right) {
        return MapCondition2.mapVal("<", castAsDate(left), castAsDate(right), (d1, d2) -> d1.compareTo(d2) < 0);
    }

    public static Condition le(Exp<LocalDate> left, Exp<LocalDate> right) {
        return MapCondition2.mapVal("<=", castAsDate(left), castAsDate(right), (d1, d2) -> d1.compareTo(d2) <= 0);
    }

    public static Condition gt(Exp<LocalDate> left, Exp<LocalDate> right) {
        return MapCondition2.mapVal(">", castAsDate(left), castAsDate(right), (d1, d2) -> d1.compareTo(d2) > 0);
    }

    public static Condition ge(Exp<LocalDate> left, Exp<LocalDate> right) {
        return MapCondition2.mapVal(">=", castAsDate(left), castAsDate(right), (d1, d2) -> d1.compareTo(d2) >= 0);
    }

    public static DateExp castAsDate(Exp<?> exp) {

        if (exp instanceof DateExp) {
            return (DateExp) exp;
        }

        Class<?> t = exp.getType();
        if (t.equals(LocalDate.class)) {
            Exp<LocalDate> dExp = (Exp<LocalDate>) exp;
            return DateExp1.mapVal("castAsDate", dExp, d -> d);
        } else if (t.equals(String.class)) {
            Exp<String> sExp = (Exp<String>) exp;
            return DateExp1.mapVal("castAsDate", sExp, LocalDate::parse);
        }

        return DateExp1.mapVal("castAsDate", exp, o -> LocalDate.parse(o.toString()));
    }
}
