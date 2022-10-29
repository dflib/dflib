package com.nhl.dflib;

import com.nhl.dflib.exp.datetime.DateExpScalar2;
import com.nhl.dflib.exp.datetime.DateFactory;
import com.nhl.dflib.exp.num.IntExp1;

import java.time.LocalDate;

/**
 * An expression applied to date columns.
 *
 * @since 0.11
 */
public interface DateExp extends Exp<LocalDate> {

    /**
     * @since 0.16
     */
    default NumExp<Integer> year() {
        return IntExp1.mapVal("year", this, LocalDate::getYear);
    }

    /**
     * @since 0.16
     */
    default NumExp<Integer> month() {
        return IntExp1.mapVal("month", this, LocalDate::getMonthValue);
    }

    /**
     * @since 0.16
     */
    default NumExp<Integer> day() {
        return IntExp1.mapVal("year", this, LocalDate::getDayOfMonth);
    }

    /**
     * @since 0.16
     */
    default Condition lt(Exp<LocalDate> exp) {
        return DateFactory.lt(this, exp);
    }

    /**
     * @since 0.16
     */
    default Condition lt(LocalDate val) {
        return DateFactory.lt(this, Exp.$val(val));
    }

    /**
     * @since 0.16
     */
    default Condition le(Exp<LocalDate> exp) {
        return DateFactory.le(this, exp);
    }

    /**
     * @since 0.16
     */
    default Condition le(LocalDate val) {
        return DateFactory.le(this, Exp.$val(val));
    }

    /**
     * @since 0.16
     */
    default Condition gt(Exp<LocalDate> exp) {
        return DateFactory.gt(this, exp);
    }

    /**
     * @since 0.16
     */
    default Condition gt(LocalDate val) {
        return DateFactory.gt(this, Exp.$val(val));
    }

    /**
     * @since 0.16
     */
    default Condition ge(Exp<LocalDate> exp) {
        return DateFactory.ge(this, exp);
    }

    /**
     * @since 0.16
     */
    default Condition ge(LocalDate val) {
        return DateFactory.ge(this, Exp.$val(val));
    }

    /**
     * @since 0.16
     */
    @Override
    default DateExp castAsDate() {
        return this;
    }

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
