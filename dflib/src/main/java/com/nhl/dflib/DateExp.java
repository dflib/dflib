package com.nhl.dflib;

import com.nhl.dflib.exp.datetime.DateExpScalar2;

import java.time.LocalDate;

/**
 * An expression applied to date columns.
 *
 * @since 0.11
 */
public interface DateExp extends Exp<LocalDate> {

    default DateExp plusDays(int days) {
        return DateExpScalar2.mapVal("plusDays", this, days, (ld, d) -> ld.plusDays(days));
    }

    default DateExp plusWeeks(int weeks) {
        return DateExpScalar2.mapVal("plusWeeks", this, weeks, (ld, w) -> ld.plusWeeks(w));
    }

    default DateExp plusMonths(int months) {
        return DateExpScalar2.mapVal("plusMonths", this, months, (ld, m) -> ld.plusMonths(m));
    }

    default DateExp plusYears(int years) {
        return DateExpScalar2.mapVal("plusYears", this, years, (ld, y) -> ld.plusYears(y));
    }
}
