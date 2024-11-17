package org.dflib;

import org.dflib.agg.Percentiles;
import org.dflib.exp.agg.ComparableAggregators;
import org.dflib.exp.agg.DateAggregators;
import org.dflib.exp.agg.DateReduceExp1;
import org.dflib.exp.datetime.DateExp2;
import org.dflib.exp.map.MapCondition2;
import org.dflib.exp.map.MapCondition3;
import org.dflib.exp.num.IntExp1;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import static org.dflib.Exp.$val;

/**
 * An expression applied to date columns.
 */
public interface DateExp extends Exp<LocalDate> {

    default NumExp<Integer> year() {
        return IntExp1.mapVal("year", this, LocalDate::getYear);
    }

    default NumExp<Integer> month() {
        return IntExp1.mapVal("month", this, LocalDate::getMonthValue);
    }

    default NumExp<Integer> day() {
        return IntExp1.mapVal("year", this, LocalDate::getDayOfMonth);
    }

    default Condition lt(Exp<LocalDate> exp) {
        return MapCondition2.mapVal("<", this, exp.castAsDate(), (d1, d2) -> d1.compareTo(d2) < 0);
    }

    default Condition lt(LocalDate val) {
        return lt(Exp.$dateVal(val));
    }

    default Condition le(Exp<LocalDate> exp) {
        return MapCondition2.mapVal("<=", this, exp.castAsDate(), (d1, d2) -> d1.compareTo(d2) <= 0);
    }

    default Condition le(LocalDate val) {
        return le(Exp.$dateVal(val));
    }

    default Condition gt(Exp<LocalDate> exp) {
        return MapCondition2.mapVal(">", this, exp.castAsDate(), (d1, d2) -> d1.compareTo(d2) > 0);
    }

    default Condition gt(LocalDate val) {
        return gt(Exp.$dateVal(val));
    }

    default Condition ge(Exp<LocalDate> exp) {
        return MapCondition2.mapVal(">=", this, exp.castAsDate(), (d1, d2) -> d1.compareTo(d2) >= 0);
    }

    default Condition ge(LocalDate val) {
        return ge(Exp.$dateVal(val));
    }

    default Condition between(Exp<LocalDate> from, Exp<LocalDate> to) {
        return MapCondition3.mapVal(
                "between",
                "and",
                this,
                from.castAsDate(),
                to.castAsDate(),
                (d1, d2, d3) -> d1.compareTo(d2) >= 0 && d1.compareTo(d3) <= 0);
    }

    default Condition between(LocalDate from, LocalDate to) {
        return between(Exp.$val(from), Exp.$val(to));
    }

    @Override
    default DateExp castAsDate() {
        return this;
    }

    @Override
    default DateExp castAsDate(String format) {
        return this;
    }


    @Override
    default DateExp castAsDate(DateTimeFormatter formatter) {
        return this;
    }

    default DateExp plusDays(int days) {
        return DateExp2.mapVal("plusDays", this, $val(days), (ld, d) -> ld.plusDays(d));
    }

    default DateExp plusWeeks(int weeks) {
        return DateExp2.mapVal("plusWeeks", this, $val(weeks), (ld, w) -> ld.plusWeeks(w));
    }

    default DateExp plusMonths(int months) {
        return DateExp2.mapVal("plusMonths", this, $val(months), (ld, m) -> ld.plusMonths(m));
    }

    default DateExp plusYears(int years) {
        return DateExp2.mapVal("plusYears", this, $val(years), (ld, y) -> ld.plusYears(y));
    }

    /**
     * @since 2.0.0
     */
    default DateExp min() {
        return min(null);
    }

    /**
     * @since 2.0.0
     */
    default DateExp min(Condition filter) {
        return new DateReduceExp1<>("min", this, s -> (LocalDate) ComparableAggregators.min(s), filter);
    }

    /**
     * @since 2.0.0
     */
    default DateExp max() {
        return max(null);
    }

    /**
     * @since 2.0.0
     */
    default DateExp max(Condition filter) {
        return new DateReduceExp1<>("max", this, s -> (LocalDate) ComparableAggregators.max(s), filter);
    }

    /**
     * @since 2.0.0
     */
    default DateExp avg() {
        return avg(null);
    }

    /**
     * @since 2.0.0
     */
    default DateExp avg(Condition filter) {
        return new DateReduceExp1<>("avg", this, DateAggregators::avg, filter);
    }

    /**
     * @since 2.0.0
     */
    default DateExp median() {
        return median(null);
    }

    /**
     * @since 2.0.0
     */
    default DateExp median(Condition filter) {
        return new DateReduceExp1<>("median", this, s -> Percentiles.ofDates(s, 0.5), filter);
    }

    /**
     * @since 2.0.0
     */
    default DateExp quantile(double q) {
        return quantile(q, null);
    }

    /**
     * @since 2.0.0
     */
    default DateExp quantile(double q, Condition filter) {
        return new DateReduceExp1<>("quantile", this, s -> Percentiles.ofDates(s, q), filter);
    }
}
