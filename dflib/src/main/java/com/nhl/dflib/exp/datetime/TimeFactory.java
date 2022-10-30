package com.nhl.dflib.exp.datetime;

import com.nhl.dflib.Condition;
import com.nhl.dflib.Exp;
import com.nhl.dflib.TimeExp;
import com.nhl.dflib.exp.map.MapCondition2;

import java.time.LocalTime;

/**
 * @since 0.16
 */
public class TimeFactory {

    public static Condition lt(Exp<LocalTime> left, Exp<LocalTime> right) {
        return MapCondition2.mapVal("<", castAsTime(left), castAsTime(right), (t1, t2) -> t1.compareTo(t2) < 0);
    }

    public static Condition le(Exp<LocalTime> left, Exp<LocalTime> right) {
        return MapCondition2.mapVal("<=", castAsTime(left), castAsTime(right), (t1, t2) -> t1.compareTo(t2) <= 0);
    }

    public static Condition gt(Exp<LocalTime> left, Exp<LocalTime> right) {
        return MapCondition2.mapVal(">", castAsTime(left), castAsTime(right), (t1, t2) -> t1.compareTo(t2) > 0);
    }

    public static Condition ge(Exp<LocalTime> left, Exp<LocalTime> right) {
        return MapCondition2.mapVal(">=", castAsTime(left), castAsTime(right), (t1, t2) -> t1.compareTo(t2) >= 0);
    }

    public static TimeExp castAsTime(Exp<?> exp) {

        if (exp instanceof TimeExp) {
            return (TimeExp) exp;
        }

        Class<?> t = exp.getType();
        if (t.equals(LocalTime.class)) {
            Exp<LocalTime> tExp = (Exp<LocalTime>) exp;
            return TimeExp1.mapVal("castAsTime", tExp, time -> time);
        }
        else if (t.equals(String.class)) {
            Exp<String> sExp = (Exp<String>) exp;
            return TimeExp1.mapVal("castAsTime", sExp, LocalTime::parse);
        }

        return TimeExp1.mapVal("castAsTime", exp, o -> LocalTime.parse(o.toString()));
    }
}
