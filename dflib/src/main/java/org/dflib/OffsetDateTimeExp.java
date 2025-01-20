package org.dflib;

import org.dflib.exp.datetime.DateExp1;
import org.dflib.exp.datetime.DateTimeExp1;
import org.dflib.exp.datetime.OffsetDateTimeExp2;
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

import static org.dflib.Exp.$val;

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

    /**
     * @param val a date-time with an offset value in ISO-8601 format (YYYY-MM-DD`T`HH:mm:ss±Z)
     * @return `less then` condition
     * @since 2.0.0
     */
    default Condition lt(String val) {
        return lt(OffsetDateTime.parse(val));
    }

    default Condition le(Exp<OffsetDateTime> exp) {
        return MapCondition2.mapVal("<=", this, exp.castAsOffsetDateTime(), (d1, d2) -> d1.compareTo(d2) <= 0);
    }

    default Condition le(OffsetDateTime val) {
        return le(Exp.$offsetDateTimeVal(val));
    }

    /**
     * @param val a date-time with an offset value in ISO-8601 format (YYYY-MM-DD`T`HH:mm:ss±Z)
     * @return `less or equals` condition
     * @since 2.0.0
     */
    default Condition le(String val) {
        return le(OffsetDateTime.parse(val));
    }

    default Condition gt(Exp<OffsetDateTime> exp) {
        return MapCondition2.mapVal(">", this, exp.castAsOffsetDateTime(), (d1, d2) -> d1.compareTo(d2) > 0);
    }

    default Condition gt(OffsetDateTime val) {
        return gt(Exp.$offsetDateTimeVal(val));
    }

    /**
     * @param val a date-time with an offset value in ISO-8601 format (YYYY-MM-DD`T`HH:mm:ss±Z)
     * @return `greater then` condition
     * @since 2.0.0
     */
    default Condition gt(String val) {
        return gt(OffsetDateTime.parse(val));
    }

    default Condition ge(Exp<OffsetDateTime> exp) {
        return MapCondition2.mapVal(">=", this, exp.castAsOffsetDateTime(), (d1, d2) -> d1.compareTo(d2) >= 0);
    }

    default Condition ge(OffsetDateTime val) {
        return ge(Exp.$offsetDateTimeVal(val));
    }

    /**
     * @param val a date-time with an offset value in ISO-8601 format (YYYY-MM-DD`T`HH:mm:ss±Z)
     * @return `greater or equals` condition
     * @since 2.0.0
     */
    default Condition ge(String val) {
        return ge(OffsetDateTime.parse(val));
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

    /**
     * @param from a date-time with an offset value in ISO-8601 format (YYYY-MM-DD`T`HH:mm:ss±Z)
     * @param to a date-time with an offset value in ISO-8601 format (YYYY-MM-DD`T`HH:mm:ss±Z)
     * @return `between` condition
     * @since 2.0.0
     */
    default Condition between(String from, String to) {
        return between(OffsetDateTime.parse(from), OffsetDateTime.parse(to));
    }

    /**
     * @param val a date-time with an offset value in ISO-8601 format (YYYY-MM-DD`T`HH:mm:ss±Z)
     * @return `equals to` condition
     * @since 2.0.0
     */
    default Condition eq(String val) {
        return Exp.super.eq(val == null ? null : OffsetDateTime.parse(val));
    }

    /**
     * @param val a date-time with an offset value in ISO-8601 format (YYYY-MM-DD`T`HH:mm:ss±Z)
     * @return `not equals to` condition
     * @since 2.0.0
     */
    default Condition ne(String val) {
        return Exp.super.ne(val == null ? null : OffsetDateTime.parse(val));
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
        return OffsetDateTimeExp2.mapVal("plusHours", this, $val(hours), (lt, hrs) -> lt.plusHours(hours));
    }

    default OffsetDateTimeExp plusMinutes(int minutes) {
        return OffsetDateTimeExp2.mapVal("plusMinutes", this, $val(minutes), (lt, m) -> lt.plusMinutes(minutes));
    }

    default OffsetDateTimeExp plusSeconds(int seconds) {
        return OffsetDateTimeExp2.mapVal("plusSeconds", this, $val(seconds), (lt, s) -> lt.plusSeconds(seconds));
    }

    default OffsetDateTimeExp plusMilliseconds(int ms) {
        return OffsetDateTimeExp2.mapVal("plusMilliseconds", this, $val(ms), (lt, m) -> lt.plus(ms, ChronoUnit.MILLIS));
    }

    default OffsetDateTimeExp plusNanos(int nanos) {
        return OffsetDateTimeExp2.mapVal("plusNanos", this, $val(nanos), (lt, n) -> lt.plusNanos(nanos));
    }

    default OffsetDateTimeExp plusDays(int days) {
        return OffsetDateTimeExp2.mapVal("plusDays", this, $val(days), (ld, d) -> ld.plusDays(days));
    }

    default OffsetDateTimeExp plusWeeks(int weeks) {
        return OffsetDateTimeExp2.mapVal("plusWeeks", this, $val(weeks), (ld, w) -> ld.plusWeeks(w));
    }

    default OffsetDateTimeExp plusMonths(int months) {
        return OffsetDateTimeExp2.mapVal("plusMonths", this, $val(months), (ld, m) -> ld.plusMonths(m));
    }

    default OffsetDateTimeExp plusYears(int years) {
        return OffsetDateTimeExp2.mapVal("plusYears", this, $val(years), (ld, y) -> ld.plusYears(y));
    }
}
