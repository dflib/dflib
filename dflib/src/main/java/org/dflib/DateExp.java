package org.dflib;

import org.dflib.exp.datetime.DateExp2;
import org.dflib.exp.map.MapCondition3;
import org.dflib.exp.map.MapCondition2;
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
}
