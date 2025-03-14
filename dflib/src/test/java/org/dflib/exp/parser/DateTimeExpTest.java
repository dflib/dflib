package org.dflib.exp.parser;

import org.dflib.Condition;
import org.dflib.DateTimeExp;
import org.dflib.Exp;
import org.dflib.NumExp;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.time.LocalDateTime;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.params.provider.Arguments.arguments;

public class DateTimeExpTest {

    @ParameterizedTest
    @MethodSource
    void column(String text, Exp<?> expected) {
        Exp<?> exp = Exp.exp(text);
        assertInstanceOf(DateTimeExp.class, exp);
        assertEquals(expected, exp);
    }

    static Stream<Arguments> column() {
        return Stream.of(
                arguments("dateTime(a)", Exp.$dateTime("a")),
                arguments("dateTime('a')", Exp.$dateTime("a")),
                arguments("dateTime(\"a\")", Exp.$dateTime("a")),
                arguments("dateTime(1)", Exp.$dateTime(1))
        );
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "DATETIME(a)",
            "dateTime(1.1)",
            "dateTime(null)",
            "dateTime(true)",
            "dateTime(int(1))",
            "dateTime(-1)",
    })
    void column_throws(String text) {
        assertThrows(ExpParserException.class, () -> Exp.exp(text));
    }

    @ParameterizedTest
    @MethodSource
    void cast(String text, Exp<?> expected) {
        Exp<?> exp = Exp.exp(text);
        assertInstanceOf(DateTimeExp.class, exp);
        assertEquals(expected, exp);
    }

    static Stream<Arguments> cast() {
        return Stream.of(
                arguments("castAsDateTime(null)", Exp.$val(null).castAsDateTime()),
                arguments("castAsDateTime(1)", Exp.$intVal(1).castAsDateTime()),
                arguments("castAsDateTime(true)", Exp.$boolVal(true).castAsDateTime()),
                arguments("castAsDateTime('1')", Exp.$strVal("1").castAsDateTime())
        );
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "CASTASDATETIME(1)",
    })
    void cast_throws(String text) {
        assertThrows(ExpParserException.class, () -> Exp.exp(text));
    }

    @ParameterizedTest
    @MethodSource
    void relation(String text, Exp<?> expected) {
        Exp<?> exp = Exp.exp(text);
        assertInstanceOf(Condition.class, exp);
        assertEquals(expected, exp);
    }

    static Stream<Arguments> relation() {
        return Stream.of(
                arguments("dateTime(1) > dateTime(2)", Exp.$dateTime(1).gt(Exp.$dateTime(2))),
                arguments("dateTime(1) >= dateTime(2)", Exp.$dateTime(1).ge(Exp.$dateTime(2))),
                arguments("dateTime(1) < dateTime(2)", Exp.$dateTime(1).lt(Exp.$dateTime(2))),
                arguments("dateTime(1) <= dateTime(2)", Exp.$dateTime(1).le(Exp.$dateTime(2))),
                arguments("dateTime(1) = dateTime(2)", Exp.$dateTime(1).eq(Exp.$dateTime(2))),
                arguments("dateTime(1) != dateTime(2)", Exp.$dateTime(1).ne(Exp.$dateTime(2))),
                arguments("dateTime(1) between dateTime(2) and dateTime(3)",
                        Exp.$dateTime(1).between(Exp.$dateTime(2), Exp.$dateTime(3))),
                arguments("dateTime(2) = '1970-01-01T12:00:00'",
                        Exp.$dateTime(2).eq("1970-01-01T12:00:00")),
                arguments("dateTime(1) = plusDays(dateTime(2), 1)", Exp.$dateTime(1).eq(Exp.$dateTime(2).plusDays(1)))
        );
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "dateTime(1) = time(2)",
            "dateTime(1) = date(2)",
            "dateTime(1) = true",
            "dateTime(1) = null",
    })
    void relation_throws(String text) {
        assertThrows(ExpParserException.class, () -> Exp.exp(text));
    }

    @ParameterizedTest
    @MethodSource
    void fieldFunction(String text, Exp<?> expected) {
        Exp<?> exp = Exp.exp(text);
        assertInstanceOf(NumExp.class, exp);
        assertEquals(expected, exp);
    }

    static Stream<Arguments> fieldFunction() {
        return Stream.of(
                arguments("year(dateTime(1))", Exp.$dateTime(1).year()),
                arguments("month(dateTime(1))", Exp.$dateTime(1).month()),
                arguments("day(dateTime(1))", Exp.$dateTime(1).day()),
                arguments("hour(dateTime(1))", Exp.$dateTime(1).hour()),
                arguments("minute(dateTime(1))", Exp.$dateTime(1).minute()),
                arguments("second(dateTime(1))", Exp.$dateTime(1).second()),
                arguments("millisecond(dateTime(1))", Exp.$dateTime(1).millisecond())
        );
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "year('1970-01-01T12:34:56Z')",
            "day(castAsDateTime('1970-01-01T12:00:00Z'), 2)"
    })
    void fieldFunction_throws(String text) {
        assertThrows(ExpParserException.class, () -> Exp.exp(text));
    }

    @ParameterizedTest
    @MethodSource
    void function(String text, Exp<?> expected) {
        Exp<?> exp = Exp.exp(text);
        assertInstanceOf(DateTimeExp.class, exp);
        assertEquals(expected, exp);
    }

    static Stream<Arguments> function() {
        return Stream.of(
                arguments("plusYears(dateTime(1), 2)", Exp.$dateTime(1).plusYears(2)),
                arguments("plusMonths(dateTime(1), 6)", Exp.$dateTime(1).plusMonths(6)),
                arguments("plusWeeks(dateTime(1), 3)", Exp.$dateTime(1).plusWeeks(3)),
                arguments("plusDays(dateTime(1), 10)", Exp.$dateTime(1).plusDays(10)),
                arguments("plusHours(dateTime(1), 2)", Exp.$dateTime(1).plusHours(2)),
                arguments("plusMinutes(dateTime(1), 30)", Exp.$dateTime(1).plusMinutes(30)),
                arguments("plusSeconds(dateTime(1), 45)", Exp.$dateTime(1).plusSeconds(45)),
                arguments("plusMilliseconds(dateTime(1), 500)", Exp.$dateTime(1).plusMilliseconds(500)),
                arguments("plusNanos(dateTime(1), 1000)", Exp.$dateTime(1).plusNanos(1000)),
                arguments("plusYears(dateTime(1), -2)", Exp.$dateTime(1).plusYears(-2))
        );
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "plusYears('1970-01-01T12:00:00Z')",
            "plusMonths(dateTime(1))",
            "plusWeeks(dateTime(1), 1 + 2)",
            "plusDays(dateTime(1), '3')",
            "plusHours(dateTime(1), null)",
    })
    void function_throws(String text) {
        assertThrows(ExpParserException.class, () -> Exp.exp(text));
    }

    @ParameterizedTest
    @MethodSource
    void aggregate(String text, Exp<?> expected) {
        Exp<?> exp = Exp.exp(text);
        assertInstanceOf(DateTimeExp.class, exp);
        assertEquals(expected, exp);
    }

    static Stream<Arguments> aggregate() {
        return Stream.of(
                arguments("min(dateTime(1))", Exp.$dateTime(1).min()),
                arguments("max(dateTime(1))", Exp.$dateTime(1).max()),
                arguments("avg(dateTime(1))", Exp.$dateTime(1).avg()),
                arguments("median(dateTime(1))", Exp.$dateTime(1).median()),
                arguments("quantile(dateTime(1), 0.5)", Exp.$dateTime(1).quantile(0.5)),
                arguments("min(dateTime(1), dateTime(1) > dateTime(2))",
                        Exp.$dateTime(1).min(Exp.$dateTime(1).gt(Exp.$dateTime(2)))),
                arguments("max(dateTime(1), dateTime(1) > dateTime(2))",
                        Exp.$dateTime(1).max(Exp.$dateTime(1).gt(Exp.$dateTime(2)))),
                arguments("avg(dateTime(1), dateTime(1) > dateTime(2))",
                        Exp.$dateTime(1).avg(Exp.$dateTime(1).gt(Exp.$dateTime(2)))),
                arguments("median(dateTime(1), dateTime(1) > dateTime(2))",
                        Exp.$dateTime(1).median(Exp.$dateTime(1).gt(Exp.$dateTime(2)))),
                arguments("quantile(dateTime(1), 0.5, dateTime(1) > dateTime(2))",
                        Exp.$dateTime(1).quantile(0.5, Exp.$dateTime(1).gt(Exp.$dateTime(2)))),
                arguments("min(dateTime(1), int(2) > 0)", Exp.$dateTime(1).min(Exp.$int(2).gt(0)))
        );
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "SUM(dateTime(1))",
            "min()",
            "max(dateTime(1), )",
            "avg(dateTime(1), 0)",
            "quantile(dateTime(1), dateTime(1) > 0)",
    })
    void aggregate_throws(String text) {
        assertThrows(ExpParserException.class, () -> Exp.exp(text));
    }
}
