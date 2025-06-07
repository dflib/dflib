package org.dflib.exp.parser;

import org.dflib.Condition;
import org.dflib.DateExp;
import org.dflib.Exp;
import org.dflib.NumExp;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.stream.Stream;

import static org.dflib.Exp.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.params.provider.Arguments.arguments;

public class DateExpTest {

    @ParameterizedTest
    @MethodSource
    void column(String text, Exp<?> expected) {
        Exp<?> exp = $(text);
        assertInstanceOf(DateExp.class, exp);
        assertEquals(expected, exp);
    }

    static Stream<Arguments> column() {
        return Stream.of(
                arguments("date(a)", $date("a")),
                arguments("date(`a`)", $date("a")),
                arguments("date(1)", $date(1))
        );
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "DATE(a)",
            "date(1.1)",
            "date(null)",
            "date(true)",
            "date(int(1))",
            "date(-1)",
    })
    void column_throws(String text) {
        assertThrows(ExpParserException.class, () -> $(text));
    }

    @ParameterizedTest
    @MethodSource
    void cast(String text, Exp<?> expected) {
        Exp<?> exp = $(text);
        assertInstanceOf(DateExp.class, exp);
        assertEquals(expected, exp);
    }

    static Stream<Arguments> cast() {
        return Stream.of(
                arguments("castAsDate(null)", $val(null).castAsDate()),
                arguments("castAsDate(1)", $intVal(1).castAsDate()),
                arguments("castAsDate(true)", $boolVal(true).castAsDate()),
                arguments("castAsDate('1')", $strVal("1").castAsDate())
        );
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "CASTASDATE(1)",
    })
    void cast_throws(String text) {
        assertThrows(ExpParserException.class, () -> $(text));
    }

    @ParameterizedTest
    @MethodSource
    void relation(String text, Exp<?> expected) {
        Exp<?> exp = $(text);
        assertInstanceOf(Condition.class, exp);
        assertEquals(expected, exp);
    }

    static Stream<Arguments> relation() {
        return Stream.of(
                arguments("date(1) > date(2)", $date(1).gt($date(2))),
                arguments("date(1) >= date(2)", $date(1).ge($date(2))),
                arguments("date(1) < date(2)", $date(1).lt($date(2))),
                arguments("date(1) <= date(2)", $date(1).le($date(2))),
                arguments("date(1) = date(2)", $date(1).eq($date(2))),
                arguments("date(1) != date(2)", $date(1).ne($date(2))),
                arguments("date(1) between date(2) and date(3)", $date(1).between($date(2), $date(3))),
                arguments("date(1) = plusDays(date(2), 1)", $date(1).eq($date(2).plusDays(1))),
                arguments("date(1) = '1970-01-01'", $date(1).eq("1970-01-01"))
        );
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "date(1) = dateTime(2)",
            "date(1) = true",
            "date(1) = null",
    })
    void relation_throws(String text) {
        assertThrows(ExpParserException.class, () -> $(text));
    }

    @ParameterizedTest
    @MethodSource
    void fieldFunction(String text, Exp<?> expected) {
        Exp<?> exp = $(text);
        assertInstanceOf(NumExp.class, exp);
        assertEquals(expected, exp);
    }

    static Stream<Arguments> fieldFunction() {
        return Stream.of(
                arguments("year(date(1))", $date(1).year()),
                arguments("month(date(1))", $date(1).month()),
                arguments("day(date(1))", $date(1).day())
        );
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "year('12:34:56')",
            "day(123)",
            "month(null)",
            "year()",
            "day(castAsDate('1970-01-01'), 2)",
    })
    void filedFunction_throws(String text) {
        assertThrows(ExpParserException.class, () -> $(text));
    }

    @ParameterizedTest
    @MethodSource
    void function(String text, Exp<?> expected) {
        Exp<?> exp = $(text);
        assertInstanceOf(DateExp.class, exp);
        assertEquals(expected, exp);
    }

    static Stream<Arguments> function() {
        return Stream.of(
                arguments("plusYears(date(1), 2)", $date(1).plusYears(2)),
                arguments("plusMonths(date(1), 6)", $date(1).plusMonths(6)),
                arguments("plusWeeks(date(1), 3)", $date(1).plusWeeks(3)),
                arguments("plusDays(date(1), 10)", $date(1).plusDays(10)),
                arguments("plusYears(date(1), -2)", $date(1).plusYears(-2))
        );
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "plusYears('1970-01-01')",
            "plusMonths(date(1))",
            "plusWeeks(date(1), 1 + 2)",
            "plusDays(date(1), '3')",
            "plusDays(date(1), null)",
    })
    void function_throws(String text) {
        assertThrows(ExpParserException.class, () -> $(text));
    }

    @ParameterizedTest
    @MethodSource
    void aggregate(String text, Exp<?> expected) {
        Exp<?> exp = $(text);
        assertInstanceOf(DateExp.class, exp);
        assertEquals(expected, exp);
    }

    static Stream<Arguments> aggregate() {
        return Stream.of(
                arguments("min(date(1))", $date(1).min()),
                arguments("max(date(1))", $date(1).max()),
                arguments("avg(date(1))", $date(1).avg()),
                arguments("median(date(1))", $date(1).median()),
                arguments("quantile(date(1), 0.5)", $date(1).quantile(0.5)),
                arguments("min(date(1), date(1) > date(2))", $date(1).min($date(1).gt($date(2)))),
                arguments("max(date(1), date(1) > date(2))", $date(1).max($date(1).gt($date(2)))),
                arguments("avg(date(1), date(1) > date(2))", $date(1).avg($date(1).gt($date(2)))),
                arguments("median(date(1), date(1) > date(2))", $date(1).median($date(1).gt($date(2)))),
                arguments("quantile(date(1), 0.5, date(1) > date(2))",
                        $date(1).quantile(0.5, $date(1).gt($date(2)))),
                arguments("min(date(1), int(2) > 0)", $date(1).min($int(2).gt(0)))
        );
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "SUM(date(1))",
            "min()",
            "max(date(1), )",
            "avg(date(1), 0)",
            "quantile(date(1), date(1) > 0)",
    })
    void aggregate_throws(String text) {
        assertThrows(ExpParserException.class, () -> $(text));
    }
}
