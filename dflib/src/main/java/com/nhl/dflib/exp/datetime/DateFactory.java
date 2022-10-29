package com.nhl.dflib.exp.datetime;

import com.nhl.dflib.DateExp;
import com.nhl.dflib.Exp;

import java.time.LocalDate;

/**
 * @since 0.16
 */
public class DateFactory {

    public static DateExp castAsDate(Exp<?> exp) {

        if (exp instanceof DateExp) {
            return (DateExp) exp;
        }

        Class<?> t = exp.getType();
        if (t.equals(String.class)) {
            Exp<String> sExp = (Exp<String>) exp;
            return DateExp1.mapVal("castAsDate", sExp, LocalDate::parse);
        }

        return DateExp1.mapVal("castAsDate", exp, o -> LocalDate.parse(o.toString()));
    }
}
