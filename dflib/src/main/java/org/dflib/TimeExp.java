package org.dflib;

import org.dflib.agg.Average;
import org.dflib.agg.Max;
import org.dflib.agg.Min;
import org.dflib.agg.Percentiles;
import org.dflib.exp.agg.TimeReduceExp1;
import org.dflib.exp.datetime.TimeAsExp;
import org.dflib.exp.datetime.TimeExp2;
import org.dflib.exp.datetime.TimeShiftExp;
import org.dflib.exp.map.MapCondition2;
import org.dflib.exp.map.MapCondition3;
import org.dflib.exp.num.IntExp1;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoField;
import java.time.temporal.ChronoUnit;
import java.util.Objects;

import static org.dflib.Exp.$val;

public interface TimeExp extends Exp<LocalTime> {

    /**
     * @since 2.0.0
     */
    @Override
    default TimeExp as(String name) {
        Objects.requireNonNull(name, "Null 'name'");
        return new TimeAsExp(name, this);
    }

    @Override
    default TimeExp castAsTime() {
        return this;
    }

    @Override
    default TimeExp castAsTime(String formatter) {
        return this;
    }

    @Override
    default TimeExp castAsTime(DateTimeFormatter formatter) {
        return this;
    }

    default NumExp<Integer> hour() {
        return IntExp1.mapVal("hour", this, LocalTime::getHour);
    }

    default NumExp<Integer> minute() {
        return IntExp1.mapVal("minute", this, LocalTime::getMinute);
    }

    default NumExp<Integer> second() {
        return IntExp1.mapVal("second", this, LocalTime::getSecond);
    }

    default NumExp<Integer> millisecond() {
        return IntExp1.mapVal("millisecond", this, lt -> lt.get(ChronoField.MILLI_OF_SECOND));
    }


    default TimeExp plusHours(int hours) {
        return TimeExp2.mapVal("plusHours", this, $val(hours), (lt, hrs) -> lt.plusHours(hours));
    }

    default TimeExp plusMinutes(int minutes) {
        return TimeExp2.mapVal("plusMinutes", this, $val(minutes), (lt, m) -> lt.plusMinutes(minutes));
    }

    default TimeExp plusSeconds(int seconds) {
        return TimeExp2.mapVal("plusSeconds", this, $val(seconds), (lt, s) -> lt.plusSeconds(seconds));
    }

    default TimeExp plusMilliseconds(int ms) {
        return TimeExp2.mapVal("plusMilliseconds", this, $val(ms), (lt, m) -> lt.plus(ms, ChronoUnit.MILLIS));
    }

    default TimeExp plusNanos(int nanos) {
        return TimeExp2.mapVal("plusNanos", this, $val(nanos), (lt, n) -> lt.plusNanos(nanos));
    }

    default Condition lt(Exp<LocalTime> exp) {
        return MapCondition2.mapVal("<", this, exp.castAsTime(), (t1, t2) -> t1.compareTo(t2) < 0);
    }

    default Condition lt(LocalTime val) {
        return lt(Exp.$timeVal(val));
    }

    /**
     * @param val a time value in ISO-8601 format (HH:mm:ss)
     * @return `less then` condition
     * @since 2.0.0
     */
    default Condition lt(String val) {
        return lt(LocalTime.parse(val));
    }

    default Condition le(Exp<LocalTime> exp) {
        return MapCondition2.mapVal("<=", this, exp.castAsTime(), (t1, t2) -> t1.compareTo(t2) <= 0);
    }

    default Condition le(LocalTime val) {
        return le(Exp.$timeVal(val));
    }

    /**
     * @param val a time value in ISO-8601 format (HH:mm:ss)
     * @return `less or equals` condition
     * @since 2.0.0
     */
    default Condition le(String val) {
        return le(LocalTime.parse(val));
    }

    default Condition gt(Exp<LocalTime> exp) {
        return MapCondition2.mapVal(">", this, exp.castAsTime(), (t1, t2) -> t1.compareTo(t2) > 0);
    }

    default Condition gt(LocalTime val) {
        return gt(Exp.$timeVal(val));
    }

    /**
     * @param val a time value in ISO-8601 format (HH:mm:ss)
     * @return `greater then` condition
     * @since 2.0.0
     */
    default Condition gt(String val) {
        return gt(LocalTime.parse(val));
    }

    default Condition ge(Exp<LocalTime> exp) {
        return MapCondition2.mapVal(">=", this, exp.castAsTime(), (t1, t2) -> t1.compareTo(t2) >= 0);
    }

    default Condition ge(LocalTime val) {
        return ge(Exp.$timeVal(val));
    }

    /**
     * @param val a time value in ISO-8601 format (HH:mm:ss)
     * @return `greater or equals` condition
     * @since 2.0.0
     */
    default Condition ge(String val) {
        return ge(LocalTime.parse(val));
    }

    default Condition between(Exp<LocalTime> from, Exp<LocalTime> to) {
        return MapCondition3.mapVal(
                "between",
                "and",
                this,
                from.castAsTime(),
                to.castAsTime(),
                (t1, t2, t3) -> t1.compareTo(t2) >= 0 && t1.compareTo(t3) <= 0);
    }

    default Condition between(LocalTime from, LocalTime to) {
        return between(Exp.$val(from), Exp.$val(to));
    }

    /**
     * @param from a time value in ISO-8601 format (HH:mm:ss)
     * @param to a time value in ISO-8601 format (HH:mm:ss)
     * @return `not between` condition
     * @since 2.0.0
     */
    default Condition between(String from, String to) {
        return between(LocalTime.parse(from), LocalTime.parse(to));
    }

    /**
     * @param from a time expression
     * @param to a time expression
     * @return `not between` condition
     * @since 2.0.0
     */
    default Condition notBetween(Exp<LocalTime> from, Exp<LocalTime> to) {
        return MapCondition3.mapVal(
                "notBetween",
                "and",
                this,
                from.castAsTime(),
                to.castAsTime(),
                (t1, t2, t3) -> t1.isBefore(t2) || t1.isAfter(t3));
    }

    /**
     * @param from a time value
     * @param to a time value
     * @return `not between` condition
     * @since 2.0.0
     */
    default Condition notBetween(LocalTime from, LocalTime to) {
        return notBetween(Exp.$val(from), Exp.$val(to));
    }

    /**
     * @param from a time value in ISO-8601 format (HH:mm:ss)
     * @param to a time value in ISO-8601 format (HH:mm:ss)
     * @return `between` condition
     * @since 2.0.0
     */
    default Condition notBetween(String from, String to) {
        return notBetween(LocalTime.parse(from), LocalTime.parse(to));
    }


    /**
     * @param val a time value in ISO-8601 format (HH:mm:ss)
     * @return `equals to` condition
     * @since 2.0.0
     */
    default Condition eq(String val) {
        return Exp.super.eq(val == null ? null : LocalTime.parse(val));
    }

    /**
     * @param val a time value in ISO-8601 format (HH:mm:ss)
     * @return `not equals to` condition
     * @since 2.0.0
     */
    default Condition ne(String val) {
        return Exp.super.ne(val == null ? null : LocalTime.parse(val));
    }

    /**
     * @since 2.0.0
     */
    default TimeExp min() {
        return min(null);
    }

    /**
     * @since 2.0.0
     */
    default TimeExp min(Condition filter) {
        return new TimeReduceExp1<>("min", this, Min::ofComparables, filter);
    }

    /**
     * @since 2.0.0
     */
    default TimeExp max() {
        return max(null);
    }

    /**
     * @since 2.0.0
     */
    default TimeExp max(Condition filter) {
        return new TimeReduceExp1<>("max", this, Max::ofComparables, filter);
    }

    /**
     * @since 2.0.0
     */
    default TimeExp avg() {
        return avg(null);
    }

    /**
     * @since 2.0.0
     */
    default TimeExp avg(Condition filter) {
        return new TimeReduceExp1<>("avg", this, Average::ofTimes, filter);
    }

    /**
     * @since 2.0.0
     */
    default TimeExp median() {
        return median(null);
    }

    /**
     * @since 2.0.0
     */
    default TimeExp median(Condition filter) {
        return new TimeReduceExp1<>("median", this, s -> Percentiles.ofTimes(s, 0.5), filter);
    }


    /**
     * @since 2.0.0
     */
    default TimeExp quantile(double q) {
        return quantile(q, null);
    }

    /**
     * @since 2.0.0
     */
    default TimeExp quantile(double q, Condition filter) {
        return new TimeReduceExp1<>("quantile", this, s -> Percentiles.ofTimes(s, q), filter);
    }

    @Override
    default TimeExp shift(int offset) {
        return shift(offset, null);
    }

    @Override
    default TimeExp shift(int offset, LocalTime filler) {
        return new TimeShiftExp(this, offset, filler);
    }
}
