package org.dflib;

import org.dflib.exp.datetime.TimeExpScalar2;
import org.dflib.exp.map.MapCondition2;
import org.dflib.exp.map.MapCondition3;
import org.dflib.exp.num.IntExp1;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoField;
import java.time.temporal.ChronoUnit;

public interface TimeExp extends Exp<LocalTime> {

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
        return TimeExpScalar2.mapVal("plusHours", this, hours, (lt, hrs) -> lt.plusHours(hours));
    }

    default TimeExp plusMinutes(int minutes) {
        return TimeExpScalar2.mapVal("plusMinutes", this, minutes, (lt, m) -> lt.plusMinutes(minutes));
    }

    default TimeExp plusSeconds(int seconds) {
        return TimeExpScalar2.mapVal("plusSeconds", this, seconds, (lt, s) -> lt.plusSeconds(seconds));
    }

    default TimeExp plusMilliseconds(int ms) {
        return TimeExpScalar2.mapVal("plusMilliseconds", this, ms, (lt, m) -> lt.plus(ms, ChronoUnit.MILLIS));
    }

    default TimeExp plusNanos(int nanos) {
        return TimeExpScalar2.mapVal("plusNanos", this, nanos, (lt, n) -> lt.plusNanos(nanos));
    }

    default Condition lt(Exp<LocalTime> exp) {
        return MapCondition2.mapVal("<", this, exp.castAsTime(), (t1, t2) -> t1.compareTo(t2) < 0);
    }

    default Condition lt(LocalTime val) {
        return lt(Exp.$timeVal(val));
    }

    default Condition le(Exp<LocalTime> exp) {
        return MapCondition2.mapVal("<=", this, exp.castAsTime(), (t1, t2) -> t1.compareTo(t2) <= 0);
    }

    default Condition le(LocalTime val) {
        return le(Exp.$timeVal(val));
    }

    default Condition gt(Exp<LocalTime> exp) {
        return MapCondition2.mapVal(">", this, exp.castAsTime(), (t1, t2) -> t1.compareTo(t2) > 0);
    }

    default Condition gt(LocalTime val) {
        return gt(Exp.$timeVal(val));
    }

    default Condition ge(Exp<LocalTime> exp) {
        return MapCondition2.mapVal(">=", this, exp.castAsTime(), (t1, t2) -> t1.compareTo(t2) >= 0);
    }

    default Condition ge(LocalTime val) {
        return ge(Exp.$timeVal(val));
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
}
