package com.nhl.dflib.exp.datetime;

import com.nhl.dflib.Exp;
import com.nhl.dflib.TimeExp;

import java.time.LocalTime;

/**
 * @since 0.16
 */
public class TimeFactory {

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
