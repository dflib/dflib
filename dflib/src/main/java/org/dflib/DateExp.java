package org.dflib;

import org.dflib.exp.datetime.DateExpScalar2;
import org.dflib.exp.map.MapCondition2;
import org.dflib.exp.map.MapCondition3;
import org.dflib.exp.num.IntExp1;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

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
        return MapCondition2.mapVal("<", this, exp.castAsDate(), (d1, d2) -> d1.compareTo(d2) < 0);
    }

    /**
     * @since 0.16
     */
    default Condition lt(LocalDate val) {
        return lt(Exp.$dateVal(val));
    }

    /**
     * @since 0.16
     */
    default Condition le(Exp<LocalDate> exp) {
        return MapCondition2.mapVal("<=", this, exp.castAsDate(), (d1, d2) -> d1.compareTo(d2) <= 0);
    }

    /**
     * @since 0.16
     */
    default Condition le(LocalDate val) {
        return le(Exp.$dateVal(val));
    }

    /**
     * @since 0.16
     */
    default Condition gt(Exp<LocalDate> exp) {
        return MapCondition2.mapVal(">", this, exp.castAsDate(), (d1, d2) -> d1.compareTo(d2) > 0);
    }

    /**
     * @since 0.16
     */
    default Condition gt(LocalDate val) {
        return gt(Exp.$dateVal(val));
    }

    /**
     * @since 0.16
     */
    default Condition ge(Exp<LocalDate> exp) {
        return MapCondition2.mapVal(">=", this, exp.castAsDate(), (d1, d2) -> d1.compareTo(d2) >= 0);
    }

    /**
     * @since 0.16
     */
    default Condition ge(LocalDate val) {
        return ge(Exp.$dateVal(val));
    }

    /**
     * @since 1.0.0-M19
     */
    default Condition between(Exp<LocalDate> from, Exp<LocalDate> to) {
        return MapCondition3.mapVal(
                "between",
                "and",
                this,
                from.castAsDate(),
                to.castAsDate(),
                (d1, d2, d3) -> d1.compareTo(d2) >= 0 && d1.compareTo(d3) <= 0);
    }

    /**
     * @since 1.0.0-M19
     */
    default Condition between(LocalDate from, LocalDate to) {
        return between(Exp.$val(from), Exp.$val(to));
    }

    /**
     * @since 0.16
     */
    @Override
    default DateExp castAsDate() {
        return this;
    }

    /**
     * @since 0.16
     */
    @Override
    default DateExp castAsDate(String format) {
        return this;
    }

    /**
     * @since 0.16
     */
    @Override
    default DateExp castAsDate(DateTimeFormatter formatter) {
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
