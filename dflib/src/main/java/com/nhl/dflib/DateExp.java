package com.nhl.dflib;

import com.nhl.dflib.exp.UnaryExp;
import com.nhl.dflib.exp.datetime.DateUnaryExp;

import java.time.LocalDate;

/**
 * An expression applied to date columns.
 *
 * @since 0.11
 */
public interface DateExp extends Exp<LocalDate> {

    default DateExp plusDays(int days) {
        return new DateUnaryExp<>("plusDays", this, UnaryExp.toSeriesOp(ld -> ld.plusDays(days)));
    }

    default DateExp plusWeeks(int days) {
        return new DateUnaryExp<>("plusWeeks", this, UnaryExp.toSeriesOp(ld -> ld.plusWeeks(days)));
    }

    default DateExp plusMonths(int days) {
        return new DateUnaryExp<>("plusMonths", this, UnaryExp.toSeriesOp(ld -> ld.plusMonths(days)));
    }

    default DateExp plusYears(int days) {
        return new DateUnaryExp<>("plusYears", this, UnaryExp.toSeriesOp(ld -> ld.plusYears(days)));
    }
}
