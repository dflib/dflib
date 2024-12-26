package org.dflib;

import org.dflib.exp.datetime.DateExp1;
import org.dflib.exp.datetime.DateTimeExp1;
import org.dflib.exp.datetime.OffsetDateTimeExpScalar2;
import org.dflib.exp.datetime.TimeExp1;
import org.dflib.exp.map.MapCondition2;
import org.dflib.exp.map.MapCondition3;
import org.dflib.exp.map.MapExp1;
import org.dflib.exp.num.IntExp1;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoField;
import java.time.temporal.ChronoUnit;

/**
 * An expression applied to datetime columns.
 *
 * @since 1.1.0
 */
public interface OffsetDateTimeExp extends Exp<OffsetDateTime> {

    default NumExp<Integer> year() {
        return IntExp1.mapVal("year", this, OffsetDateTime::getYear);
    }

    default NumExp<Integer> month() {
        return IntExp1.mapVal("month", this, OffsetDateTime::getMonthValue);
    }

    default NumExp<Integer> day() {
        return IntExp1.mapVal("year", this, OffsetDateTime::getDayOfMonth);
    }

    default NumExp<Integer> hour() {
        return IntExp1.mapVal("hour", this, OffsetDateTime::getHour);
    }

    default NumExp<Integer> minute() {
        return IntExp1.mapVal("minute", this, OffsetDateTime::getMinute);
    }

    default NumExp<Integer> second() {
        return IntExp1.mapVal("second", this, OffsetDateTime::getSecond);
    }

    default NumExp<Integer> millisecond() {
        return IntExp1.mapVal("millisecond", this, lt -> lt.get(ChronoField.MILLI_OF_SECOND));
    }

    default Exp<ZoneOffset> offset() {
        return MapExp1.mapVal("offset", ZoneOffset.class, this, OffsetDateTime::getOffset);
    }

    default Condition lt(Exp<OffsetDateTime> exp) {
        return MapCondition2.mapVal("<", this, exp.castAsOffsetDateTime(), (d1, d2) -> d1.compareTo(d2) < 0);
    }

    default Condition lt(OffsetDateTime val) {
        return lt(Exp.$offsetDateTimeVal(val));
    }

    default Condition le(Exp<OffsetDateTime> exp) {
        return MapCondition2.mapVal("<=", this, exp.castAsOffsetDateTime(), (d1, d2) -> d1.compareTo(d2) <= 0);
    }

    default Condition le(OffsetDateTime val) {
        return le(Exp.$offsetDateTimeVal(val));
    }

    default Condition gt(Exp<OffsetDateTime> exp) {
        return MapCondition2.mapVal(">", this, exp.castAsOffsetDateTime(), (d1, d2) -> d1.compareTo(d2) > 0);
    }

    default Condition gt(OffsetDateTime val) {
        return gt(Exp.$offsetDateTimeVal(val));
    }

    default Condition ge(Exp<OffsetDateTime> exp) {
        return MapCondition2.mapVal(">=", this, exp.castAsOffsetDateTime(), (d1, d2) -> d1.compareTo(d2) >= 0);
    }

    default Condition ge(OffsetDateTime val) {
        return ge(Exp.$offsetDateTimeVal(val));
    }


    default Condition between(Exp<OffsetDateTime> from, Exp<OffsetDateTime> to) {
        return MapCondition3.mapVal(
                "between",
                "and",
                this,
                from.castAsOffsetDateTime(),
                to.castAsOffsetDateTime(),
                (d1, d2, d3) -> d1.compareTo(d2) >= 0 && d1.compareTo(d3) <= 0);
    }


    default Condition between(OffsetDateTime from, OffsetDateTime to) {
        return between(Exp.$val(from), Exp.$val(to));
    }

    @Override
    default OffsetDateTimeExp castAsOffsetDateTime(String format) {
        return this;
    }

    @Override
    default OffsetDateTimeExp castAsOffsetDateTime(DateTimeFormatter formatter) {
        return this;
    }

    @Override
    default OffsetDateTimeExp castAsOffsetDateTime() {
        return this;
    }

    @Override
    default DateTimeExp castAsDateTime() {
        return DateTimeExp1.mapVal("dateTime", this, OffsetDateTime::toLocalDateTime);
    }

    @Override
    default DateExp castAsDate() {
        return DateExp1.mapVal("date", this, OffsetDateTime::toLocalDate);
    }

    @Override
    default TimeExp castAsTime() {
        return TimeExp1.mapVal("time", this, OffsetDateTime::toLocalTime);
    }

    default OffsetDateTimeExp plusHours(int hours) {
        return OffsetDateTimeExpScalar2.mapVal("plusHours", this, hours, (lt, hrs) -> lt.plusHours(hours));
    }

    default OffsetDateTimeExp plusMinutes(int minutes) {
        return OffsetDateTimeExpScalar2.mapVal("plusMinutes", this, minutes, (lt, m) -> lt.plusMinutes(minutes));
    }

    default OffsetDateTimeExp plusSeconds(int seconds) {
        return OffsetDateTimeExpScalar2.mapVal("plusSeconds", this, seconds, (lt, s) -> lt.plusSeconds(seconds));
    }

    default OffsetDateTimeExp plusMilliseconds(int ms) {
        return OffsetDateTimeExpScalar2.mapVal("plusMilliseconds", this, ms, (lt, m) -> lt.plus(ms, ChronoUnit.MILLIS));
    }

    default OffsetDateTimeExp plusNanos(int nanos) {
        return OffsetDateTimeExpScalar2.mapVal("plusNanos", this, nanos, (lt, n) -> lt.plusNanos(nanos));
    }

    default OffsetDateTimeExp plusDays(int days) {
        return OffsetDateTimeExpScalar2.mapVal("plusDays", this, days, (ld, d) -> ld.plusDays(days));
    }

    default OffsetDateTimeExp plusWeeks(int weeks) {
        return OffsetDateTimeExpScalar2.mapVal("plusWeeks", this, weeks, (ld, w) -> ld.plusWeeks(w));
    }

    default OffsetDateTimeExp plusMonths(int months) {
        return OffsetDateTimeExpScalar2.mapVal("plusMonths", this, months, (ld, m) -> ld.plusMonths(m));
    }

    default OffsetDateTimeExp plusYears(int years) {
        return OffsetDateTimeExpScalar2.mapVal("plusYears", this, years, (ld, y) -> ld.plusYears(y));
    }
}
