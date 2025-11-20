package org.dflib;

import org.dflib.agg.Average;
import org.dflib.agg.Max;
import org.dflib.agg.Min;
import org.dflib.agg.Percentiles;
import org.dflib.exp.agg.DateTimeReduceExp1;
import org.dflib.exp.datetime.DateExp1;
import org.dflib.exp.datetime.DateTimeAsExp;
import org.dflib.exp.datetime.DateTimeExp2;
import org.dflib.exp.datetime.DateTimeShiftExp;
import org.dflib.exp.datetime.TimeExp1;
import org.dflib.exp.map.MapCondition2;
import org.dflib.exp.map.MapCondition3;
import org.dflib.exp.num.IntExp1;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoField;
import java.time.temporal.ChronoUnit;
import java.util.Objects;

import static org.dflib.Exp.$val;

/**
 * An expression applied to datetime columns.
 */
public interface DateTimeExp extends Exp<LocalDateTime> {

    @Override
    default Class<LocalDateTime> getType() {
        return LocalDateTime.class;
    }

    /**
     * @since 2.0.0
     */
    @Override
    default DateTimeExp as(String name) {
        Objects.requireNonNull(name, "Null 'name'");
        return new DateTimeAsExp(name, this);
    }

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

    /**
     * @param val a date-time value in ISO-8601 format (YYYY-MM-DD`T`HH:mm:ss)
     * @return `less then` condition
     * @since 2.0.0
     */
    default Condition lt(String val) {
        return lt(LocalDateTime.parse(val));
    }

    default Condition le(Exp<LocalDateTime> exp) {
        return MapCondition2.mapVal("<=", this, exp.castAsDateTime(), (d1, d2) -> d1.compareTo(d2) <= 0);
    }

    default Condition le(LocalDateTime val) {
        return le(Exp.$dateTimeVal(val));
    }

    /**
     * @param val a date-time value in ISO-8601 format (YYYY-MM-DD`T`HH:mm:ss)
     * @return `less or equals` condition
     * @since 2.0.0
     */
    default Condition le(String val) {
        return le(LocalDateTime.parse(val));
    }

    default Condition gt(Exp<LocalDateTime> exp) {
        return MapCondition2.mapVal(">", this, exp.castAsDateTime(), (d1, d2) -> d1.compareTo(d2) > 0);
    }

    default Condition gt(LocalDateTime val) {
        return gt(Exp.$dateTimeVal(val));
    }

    /**
     * @param val a date-time value in ISO-8601 format (YYYY-MM-DD`T`HH:mm:ss)
     * @return `greater then` condition
     * @since 2.0.0
     */
    default Condition gt(String val) {
        return gt(LocalDateTime.parse(val));
    }

    default Condition ge(Exp<LocalDateTime> exp) {
        return MapCondition2.mapVal(">=", this, exp.castAsDateTime(), (d1, d2) -> d1.compareTo(d2) >= 0);
    }

    default Condition ge(LocalDateTime val) {
        return ge(Exp.$dateTimeVal(val));
    }

    /**
     * @param val a date-time value in ISO-8601 format (YYYY-MM-DD`T`HH:mm:ss)
     * @return `greater or equals` condition
     * @since 2.0.0
     */
    default Condition ge(String val) {
        return ge(LocalDateTime.parse(val));
    }

    default Condition between(Exp<LocalDateTime> from, Exp<LocalDateTime> to) {
        return MapCondition3.mapVal(
                "between",
                "and",
                this,
                from.castAsDateTime(),
                to.castAsDateTime(),
                (d1, d2, d3) -> d1.compareTo(d2) >= 0 && d1.compareTo(d3) <= 0);
    }


    default Condition between(LocalDateTime from, LocalDateTime to) {
        return between(Exp.$val(from), Exp.$val(to));
    }

    /**
     * @param from a date-time value in ISO-8601 format (YYYY-MM-DD`T`HH:mm:ss)
     * @param to a date-time value in ISO-8601 format (YYYY-MM-DD`T`HH:mm:ss)
     * @return `between` condition
     * @since 2.0.0
     */
    default Condition between(String from, String to) {
        return between(LocalDateTime.parse(from), LocalDateTime.parse(to));
    }

    /**
     * @param from a date-time expression
     * @param to a date-time expression
     * @return `not between` condition
     * @since 2.0.0
     */
    default Condition notBetween(Exp<LocalDateTime> from, Exp<LocalDateTime> to) {
        return MapCondition3.mapVal(
                "notBetween",
                "and",
                this,
                from.castAsDateTime(),
                to.castAsDateTime(),
                (d1, d2, d3) -> d1.isBefore(d2) || d1.isAfter(d3));
    }

    /**
     * @param from a date-time value
     * @param to a date-time value
     * @return `not between` condition
     * @since 2.0.0
     */
    default Condition notBetween(LocalDateTime from, LocalDateTime to) {
        return notBetween(Exp.$val(from), Exp.$val(to));
    }

    /**
     * @param from a date-time value in ISO-8601 format (YYYY-MM-DD`T`HH:mm:ss)
     * @param to a date-time value in ISO-8601 format (YYYY-MM-DD`T`HH:mm:ss)
     * @return `not between` condition
     * @since 2.0.0
     */
    default Condition notBetween(String from, String to) {
        return notBetween(LocalDateTime.parse(from), LocalDateTime.parse(to));
    }

    /**
     * @param val a date-time value in ISO-8601 format (YYYY-MM-DD`T`HH:mm:ss)
     * @return `equals to` condition
     * @since 2.0.0
     */
    default Condition eq(String val) {
        return Exp.super.eq(val == null ? null : LocalDateTime.parse(val));
    }

    /**
     * @param val a date-time value in ISO-8601 format (YYYY-MM-DD`T`HH:mm:ss)
     * @return `not equals to` condition
     * @since 2.0.0
     */
    default Condition ne(String val) {
        return Exp.super.ne(val == null ? null : LocalDateTime.parse(val));
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
        return DateTimeExp2.mapVal("plusHours", this, $val(hours), (lt, hrs) -> lt.plusHours(hours));
    }

    default DateTimeExp plusMinutes(int minutes) {
        return DateTimeExp2.mapVal("plusMinutes", this, $val(minutes), (lt, m) -> lt.plusMinutes(minutes));
    }

    default DateTimeExp plusSeconds(int seconds) {
        return DateTimeExp2.mapVal("plusSeconds", this, $val(seconds), (lt, s) -> lt.plusSeconds(seconds));
    }

    default DateTimeExp plusMilliseconds(int ms) {
        return DateTimeExp2.mapVal("plusMilliseconds", this, $val(ms), (lt, m) -> lt.plus(ms, ChronoUnit.MILLIS));
    }

    default DateTimeExp plusNanos(int nanos) {
        return DateTimeExp2.mapVal("plusNanos", this, $val(nanos), (lt, n) -> lt.plusNanos(nanos));
    }

    default DateTimeExp plusDays(int days) {
        return DateTimeExp2.mapVal("plusDays", this, $val(days), (ld, d) -> ld.plusDays(days));
    }

    default DateTimeExp plusWeeks(int weeks) {
        return DateTimeExp2.mapVal("plusWeeks", this, $val(weeks), (ld, w) -> ld.plusWeeks(w));
    }

    default DateTimeExp plusMonths(int months) {
        return DateTimeExp2.mapVal("plusMonths", this, $val(months), (ld, m) -> ld.plusMonths(m));
    }

    default DateTimeExp plusYears(int years) {
        return DateTimeExp2.mapVal("plusYears", this, $val(years), (ld, y) -> ld.plusYears(y));
    }

    /**
     * @since 2.0.0
     */
    default DateTimeExp min() {
        return min(null);
    }

    /**
     * @since 2.0.0
     */
    default DateTimeExp min(Condition filter) {
        return new DateTimeReduceExp1<>("min", this, s -> (LocalDateTime) Min.ofComparables(s), filter);
    }

    /**
     * @since 2.0.0
     */
    default DateTimeExp max() {
        return max(null);
    }

    /**
     * @since 2.0.0
     */
    default DateTimeExp max(Condition filter) {
        return new DateTimeReduceExp1<>("max", this, s -> (LocalDateTime) Max.ofComparables(s), filter);
    }

    /**
     * @since 2.0.0
     */
    default DateTimeExp avg() {
        return avg(null);
    }

    /**
     * @since 2.0.0
     */
    default DateTimeExp avg(Condition filter) {
        return new DateTimeReduceExp1<>("avg", this, Average::ofDateTimes, filter);
    }

    /**
     * @since 2.0.0
     */
    default DateTimeExp median() {
        return median(null);
    }

    /**
     * @since 2.0.0
     */
    default DateTimeExp median(Condition filter) {
        return new DateTimeReduceExp1<>("median", this, s -> Percentiles.ofDateTimes(s, 0.5), filter);
    }

    /**
     * @since 2.0.0
     */
    default DateTimeExp quantile(double q) {
        return quantile(q, null);
    }

    /**
     * @since 2.0.0
     */
    default DateTimeExp quantile(double q, Condition filter) {
        return new DateTimeReduceExp1<>("quantile", this, s -> Percentiles.ofDateTimes(s, q), filter);
    }

    @Override
    default DateTimeExp shift(int offset) {
        return shift(offset, null);
    }

    @Override
    default DateTimeExp shift(int offset, LocalDateTime filler) {
        return new DateTimeShiftExp(this, offset, filler);
    }
}
