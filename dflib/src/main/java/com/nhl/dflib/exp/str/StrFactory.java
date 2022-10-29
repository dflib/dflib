package com.nhl.dflib.exp.str;

import com.nhl.dflib.Exp;
import com.nhl.dflib.StrExp;

/**
 * @since 0.16
 */
public class StrFactory {

    public static StrExp castAsStr(Exp<?> exp) {

        if (exp instanceof StrExp) {
            return (StrExp) exp;
        }

        Class<?> t = exp.getType();
        if (t.equals(String.class)) {
            Exp<String> sExp = (Exp<String>) exp;
            return StrExp1.mapVal("castAsStr", sExp, o -> o);
        }

        return StrExp1.mapVal("castAsStr", exp, o -> o.toString());
    }
}
