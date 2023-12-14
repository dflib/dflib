package com.nhl.dflib;

import com.nhl.dflib.exp.datetime.DateExp1;
import com.nhl.dflib.exp.datetime.DateTimeExpScalar2;
import com.nhl.dflib.exp.datetime.TimeExp1;
import com.nhl.dflib.exp.map.MapCondition2;
import com.nhl.dflib.exp.map.MapCondition3;
import com.nhl.dflib.exp.num.IntExp1;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoField;
import java.time.temporal.ChronoUnit;

/**
 * An expression applied to datetime columns.
 *
 * @since 0.16
 */
public interface DateTimeExp extends Exp<LocalDateTime> {

    default NumExp<Integer> year() {
        return IntExp1.mapVal("year", this, LocalDateTime::getYear);
    }

    default NumExp<Integer> month() {
        return IntExp1.mapVal("month", this, LocalDateTime::getMonthValue);
    }

    default NumExp<Integer> day() {
        return IntExp1.mapVal("year", this, LocalDateTime::getDayOfMonth);
    }

    default NumExp<Integer> hour() {
        return IntExp1.mapVal("hour", this, LocalDateTime::getHour);
    }

    default NumExp<Integer> minute() {
        return IntExp1.mapVal("minute", this, LocalDateTime::getMinute);
    }

    default NumExp<Integer> second() {
        return IntExp1.mapVal("second", this, LocalDateTime::getSecond);
    }

    default NumExp<Integer> millisecond() {
        return IntExp1.mapVal("millisecond", this, lt -> lt.get(ChronoField.MILLI_OF_SECOND));
    }

    default Condition lt(Exp<LocalDateTime> exp) {
        return MapCondition2.mapVal("<", this, exp.castAsDateTime(), (d1, d2) -> d1.compareTo(d2) < 0);
    }

    default Condition lt(LocalDateTime val) {
        return lt(Exp.$dateTimeVal(val));
    }

    default Condition le(Exp<LocalDateTime> exp) {
        return MapCondition2.mapVal("<=", this, exp.castAsDateTime(), (d1, d2) -> d1.compareTo(d2) <= 0);
    }

    default Condition le(LocalDateTime val) {
        return le(Exp.$dateTimeVal(val));
    }

    default Condition gt(Exp<LocalDateTime> exp) {
        return MapCondition2.mapVal(">", this, exp.castAsDateTime(), (d1, d2) -> d1.compareTo(d2) > 0);
    }

    default Condition gt(LocalDateTime val) {
        return gt(Exp.$dateTimeVal(val));
    }

    default Condition ge(Exp<LocalDateTime> exp) {
        return MapCondition2.mapVal(">=", this, exp.castAsDateTime(), (d1, d2) -> d1.compareTo(d2) >= 0);
    }

    default Condition ge(LocalDateTime val) {
        return ge(Exp.$dateTimeVal(val));
    }

    /**
     * @since 1.0.0-M19
     */
    default Condition between(Exp<LocalDateTime> from, Exp<LocalDateTime> to) {
        return MapCondition3.mapVal(
                "between",
                "and",
                this,
                from.castAsDateTime(),
                to.castAsDateTime(),
                (d1, d2, d3) -> d1.compareTo(d2) >= 0 && d1.compareTo(d3) <= 0);
    }

    /**
     * @since 1.0.0-M19
     */
    default Condition between(LocalDateTime from, LocalDateTime to) {
        return between(Exp.$val(from), Exp.$val(to));
    }

    @Override
    default DateTimeExp castAsDateTime() {
        return this;
    }

    @Override
    default DateTimeExp castAsDateTime(String format) {
        return this;
    }

    @Override
    default DateTimeExp castAsDateTime(DateTimeFormatter formatter) {
        return this;
    }

    @Override
    default DateExp castAsDate() {
        return DateExp1.mapVal("date", this, LocalDateTime::toLocalDate);
    }

    @Override
    default TimeExp castAsTime() {
        return TimeExp1.mapVal("time", this, LocalDateTime::toLocalTime);
    }

    default DateTimeExp plusHours(int hours) {
        return DateTimeExpScalar2.mapVal("plusHours", this, hours, (lt, hrs) -> lt.plusHours(hours));
    }

    default DateTimeExp plusMinutes(int minutes) {
        return DateTimeExpScalar2.mapVal("plusMinutes", this, minutes, (lt, m) -> lt.plusMinutes(minutes));
    }

    default DateTimeExp plusSeconds(int seconds) {
        return DateTimeExpScalar2.mapVal("plusSeconds", this, seconds, (lt, s) -> lt.plusSeconds(seconds));
    }

    default DateTimeExp plusMilliseconds(int ms) {
        return DateTimeExpScalar2.mapVal("plusMilliseconds", this, ms, (lt, m) -> lt.plus(ms, ChronoUnit.MILLIS));
    }

    default DateTimeExp plusNanos(int nanos) {
        return DateTimeExpScalar2.mapVal("plusNanos", this, nanos, (lt, n) -> lt.plusNanos(nanos));
    }

    default DateTimeExp plusDays(int days) {
        return DateTimeExpScalar2.mapVal("plusDays", this, days, (ld, d) -> ld.plusDays(days));
    }

    default DateTimeExp plusWeeks(int weeks) {
        return DateTimeExpScalar2.mapVal("plusWeeks", this, weeks, (ld, w) -> ld.plusWeeks(w));
    }

    default DateTimeExp plusMonths(int months) {
        return DateTimeExpScalar2.mapVal("plusMonths", this, months, (ld, m) -> ld.plusMonths(m));
    }

    default DateTimeExp plusYears(int years) {
        return DateTimeExpScalar2.mapVal("plusYears", this, years, (ld, y) -> ld.plusYears(y));
    }
}
