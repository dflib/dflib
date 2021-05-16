package com.nhl.dflib.exp.datetime;

import com.nhl.dflib.DateExp;
import com.nhl.dflib.Exp;
import com.nhl.dflib.Series;
import com.nhl.dflib.exp.UnaryExp;

import java.time.LocalDate;
import java.util.function.Function;

/**
 * @since 0.11
 */
public class DateUnaryExp<F> extends UnaryExp<F, LocalDate> implements DateExp {

    public DateUnaryExp(String opName, Exp<F> exp, Function<Series<F>, Series<LocalDate>> op) {
        super(opName, LocalDate.class, exp, op);
    }
}
