package org.dflib.exp.parser;

import org.dflib.Condition;
import org.dflib.Exp;
import org.dflib.NumExp;
import org.dflib.TimeExp;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.time.LocalTime;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.params.provider.Arguments.arguments;

public class TimeExpTest {

    @ParameterizedTest
    @MethodSource
    void column(String text, Exp<?> expected) {
        Exp<?> exp = Exp.exp(text);
        assertInstanceOf(TimeExp.class, exp);
        assertEquals(expected, exp);
    }

    static Stream<Arguments> column() {
        return Stream.of(
                arguments("time(a)", Exp.$time("a")),
                arguments("time('a')", Exp.$time("a")),
                arguments("time(\"a\")", Exp.$time("a")),
                arguments("time(1)", Exp.$time(1))
        );
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "TIME(a)",
            "time(1.1)",
            "time(null)",
            "time(true)",
            "time(int(1))",
            "time(-1)",
    })
    void column_throws(String text) {
        assertThrows(ExpParserException.class, () -> Exp.exp(text));
    }

    @ParameterizedTest
    @MethodSource
    void cast(String text, Exp<?> expected) {
        Exp<?> exp = Exp.exp(text);
        assertInstanceOf(TimeExp.class, exp);
        assertEquals(expected, exp);
    }

    static Stream<Arguments> cast() {
        return Stream.of(
                arguments("castAsTime(null)", Exp.$val(null).castAsTime()),
                arguments("castAsTime(1)", Exp.$intVal(1).castAsTime()),
                arguments("castAsTime(true)", Exp.$boolVal(true).castAsTime()),
                arguments("castAsTime('1')", Exp.$strVal("1").castAsTime())
        );
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "CASTASTIME(1)",
    })
    void cast_throws(String text) {
        assertThrows(ExpParserException.class, () -> Exp.exp(text));
    }

    @ParameterizedTest
    @MethodSource
    void timeRelation(String text, Exp<?> expected) {
        Exp<?> exp = Exp.exp(text);
        assertInstanceOf(Condition.class, exp);
        assertEquals(expected, exp);
    }

    static Stream<Arguments> timeRelation() {
        return Stream.of(
                arguments("time(1) > time(2)", Exp.$time(1).gt(Exp.$time(2))),
                arguments("time(1) >= time(2)", Exp.$time(1).ge(Exp.$time(2))),
                arguments("time(1) < time(2)", Exp.$time(1).lt(Exp.$time(2))),
                arguments("time(1) <= time(2)", Exp.$time(1).le(Exp.$time(2))),
                arguments("time(1) = time(2)", Exp.$time(1).eq(Exp.$time(2))),
                arguments("time(1) != time(2)", Exp.$time(1).ne(Exp.$time(2))),
                arguments("time(1) between time(2) and time(3)", Exp.$time(1).between(Exp.$time(2), Exp.$time(3))),
                arguments("time(2) = '12:00:00'", Exp.$time(2).eq("12:00:00")),
                arguments("time(1) = plusHours(time(2), 1)", Exp.$time(1).eq(Exp.$time(2).plusHours(1)))
        );
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "time(1) = dateTime(2)",
            "time(1) = true",
            "time(1) = null",
    })
    void timeRelation_throws(String text) {
        assertThrows(ExpParserException.class, () -> Exp.exp(text));
    }

    @ParameterizedTest
    @MethodSource
    void timeFieldFn(String text, Exp<?> expected) {
        Exp<?> exp = Exp.exp(text);
        assertInstanceOf(NumExp.class, exp);
        assertEquals(expected, exp);
    }

    static Stream<Arguments> timeFieldFn() {
        return Stream.of(
                arguments("hour(time(1))", Exp.$time(1).hour()),
                arguments("minute(time(1))", Exp.$time(1).minute()),
                arguments("second(time(1))", Exp.$time(1).second()),
                arguments("millisecond(time(1))", Exp.$time(1).millisecond())
        );
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "hour('12:34:56')",
            "second(123)",
            "millisecond(null)",
            "minute()",
            "hour(castAsTime('12:34:56'), 2)",
    })
    void timeFieldFn_throws(String text) {
        assertThrows(ExpParserException.class, () -> Exp.exp(text));
    }

    @ParameterizedTest
    @MethodSource
    void timeFn(String text, Exp<?> expected) {
        Exp<?> exp = Exp.exp(text);
        assertInstanceOf(TimeExp.class, exp);
        assertEquals(expected, exp);
    }

    static Stream<Arguments> timeFn() {
        return Stream.of(
                arguments("plusHours(time(1), 2)", Exp.$time(1).plusHours(2)),
                arguments("plusMinutes(time(1), 30)", Exp.$time(1).plusMinutes(30)),
                arguments("plusSeconds(time(1), 45)", Exp.$time(1).plusSeconds(45)),
                arguments("plusMilliseconds(time(1), 500)", Exp.$time(1).plusMilliseconds(500)),
                arguments("plusNanos(time(1), 1000)", Exp.$time(1).plusNanos(1000)),
                arguments("plusHours(time(1), -2)", Exp.$time(1).plusHours(-2)),
                arguments("plusHours(castAsTime('12:00:00'), 2)", Exp.$strVal("12:00:00").castAsTime().plusHours(2))
        );
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "plusHours('12:34:56')",
            "plusMinutes(time(1))",
            "plusSeconds(time(1), 1 + 2)",
            "plusMilliseconds(time(1), time(2))",
            "plusNanos(time(1), '1000')",
            "plusNanos(time(1), null)",
    })
    void timeFn_throws(String text) {
        assertThrows(ExpParserException.class, () -> Exp.exp(text));
    }

    @ParameterizedTest
    @MethodSource
    void timeAgg(String text, Exp<?> expected) {
        Exp<?> exp = Exp.exp(text);
        assertInstanceOf(TimeExp.class, exp);
        assertEquals(expected, exp);
    }

    static Stream<Arguments> timeAgg() {
        return Stream.of(
                arguments("min(time(1))", Exp.$time(1).min()),
                arguments("max(time(1))", Exp.$time(1).max()),
                arguments("avg(time(1))", Exp.$time(1).avg()),
                arguments("median(time(1))", Exp.$time(1).median()),
                arguments("quantile(time(1), 0.5)", Exp.$time(1).quantile(0.5)),
                arguments("min(time(1), time(1) > time(2))", Exp.$time(1).min(Exp.$time(1).gt(Exp.$time(2)))),
                arguments("max(time(1), time(1) > time(2))", Exp.$time(1).max(Exp.$time(1).gt(Exp.$time(2)))),
                arguments("avg(time(1), time(1) > time(2))", Exp.$time(1).avg(Exp.$time(1).gt(Exp.$time(2)))),
                arguments("median(time(1), time(1) > time(2))", Exp.$time(1).median(Exp.$time(1).gt(Exp.$time(2)))),
                arguments("quantile(time(1), 0.5, time(1) > time(2))",
                        Exp.$time(1).quantile(0.5, Exp.$time(1).gt(Exp.$time(2)))),
                arguments("min(time(1), int(2) > 0)", Exp.$time(1).min(Exp.$int(2).gt(0)))
        );
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "SUM(time(1))",
            "min()",
            "max(time(1), )",
            "avg(time(1), 0)",
            "quantile(time(1), time(1) > 0)",
    })
    void timeAgg_throws(String text) {
        assertThrows(ExpParserException.class, () -> Exp.exp(text));
    }
}
