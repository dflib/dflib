package org.dflib.exp.parser;

import org.antlr.v4.runtime.misc.ParseCancellationException;
import org.dflib.Condition;
import org.dflib.Exp;
import org.dflib.NumExp;
import org.dflib.OffsetDateTimeExp;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.params.provider.Arguments.arguments;

public class OffsetDateTimeExpTest {

    @ParameterizedTest
    @MethodSource
    void column(String text, Exp<?> expected) {
        Exp<?> exp = Exp.exp(text);
        assertInstanceOf(OffsetDateTimeExp.class, exp);
        assertEquals(expected, exp);
    }

    static Stream<Arguments> column() {
        return Stream.of(
                arguments("offsetDateTime(a)", Exp.$offsetDateTime("a")),
                arguments("offsetDateTime('a')", Exp.$offsetDateTime("a")),
                arguments("offsetDateTime(\"a\")", Exp.$offsetDateTime("a")),
                arguments("offsetDateTime(1)", Exp.$offsetDateTime(1))
        );
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "OFFSETDATETIME(a)",
            "offsetDateTime(1.1)",
            "offsetDateTime(null)",
            "offsetDateTime(true)",
            "offsetDateTime(int(1))",
    })
    void column_parsingError(String text) {
        assertThrows(ParseCancellationException.class, () -> Exp.exp(text));
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "offsetDateTime(-1)",
    })
    void column_apiError(String text) {
        assertThrows(IllegalArgumentException.class, () -> Exp.exp(text));
    }

    @ParameterizedTest
    @MethodSource
    void cast(String text, Exp<?> expected) {
        Exp<?> exp = Exp.exp(text);
        assertInstanceOf(OffsetDateTimeExp.class, exp);
        assertEquals(expected, exp);
    }

    static Stream<Arguments> cast() {
        return Stream.of(
                arguments("castAsOffsetDateTime(null)", Exp.$val(null).castAsOffsetDateTime()),
                arguments("castAsOffsetDateTime(1)", Exp.$intVal(1).castAsOffsetDateTime()),
                arguments("castAsOffsetDateTime(true)", Exp.$boolVal(true).castAsOffsetDateTime()),
                arguments("castAsOffsetDateTime('1')", Exp.$strVal("1").castAsOffsetDateTime())
        );
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "CASTASOFFSETDATETIME(1)",
    })
    void cast_parsingError(String text) {
        assertThrows(ParseCancellationException.class, () -> Exp.exp(text));
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
                arguments("offsetDateTime(1) > offsetDateTime(2)", Exp.$offsetDateTime(1).gt(Exp.$offsetDateTime(2))),
                arguments("offsetDateTime(1) >= offsetDateTime(2)", Exp.$offsetDateTime(1).ge(Exp.$offsetDateTime(2))),
                arguments("offsetDateTime(1) < offsetDateTime(2)", Exp.$offsetDateTime(1).lt(Exp.$offsetDateTime(2))),
                arguments("offsetDateTime(1) <= offsetDateTime(2)", Exp.$offsetDateTime(1).le(Exp.$offsetDateTime(2))),
                arguments("offsetDateTime(1) = offsetDateTime(2)", Exp.$offsetDateTime(1).eq(Exp.$offsetDateTime(2))),
                arguments("offsetDateTime(1) != offsetDateTime(2)", Exp.$offsetDateTime(1).ne(Exp.$offsetDateTime(2))),
                arguments("offsetDateTime(1) between offsetDateTime(2) and offsetDateTime(3)",
                        Exp.$offsetDateTime(1).between(Exp.$offsetDateTime(2), Exp.$offsetDateTime(3))),
                arguments("offsetDateTime(2) = '1970-01-01T12:00:00+01:00'",
                        Exp.$offsetDateTime(2).eq("1970-01-01T12:00:00+01:00")),
                arguments("offsetDateTime(1) = plusDays(offsetDateTime(2), 1)",
                        Exp.$offsetDateTime(1).eq(Exp.$offsetDateTime(2).plusDays(1)))
        );
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "offsetDateTime(1) = time(2)",
            "offsetDateTime(1) = date(2)",
            "offsetDateTime(1) = true",
            "offsetDateTime(1) = null",
    })
    void relation_parsingError(String text) {
        assertThrows(ParseCancellationException.class, () -> Exp.exp(text));
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
                arguments("year(offsetDateTime(1))", Exp.$offsetDateTime(1).year()),
                arguments("month(offsetDateTime(1))", Exp.$offsetDateTime(1).month()),
                arguments("day(offsetDateTime(1))", Exp.$offsetDateTime(1).day()),
                arguments("hour(offsetDateTime(1))", Exp.$offsetDateTime(1).hour()),
                arguments("minute(offsetDateTime(1))", Exp.$offsetDateTime(1).minute()),
                arguments("second(offsetDateTime(1))", Exp.$offsetDateTime(1).second()),
                arguments("millisecond(offsetDateTime(1))", Exp.$offsetDateTime(1).millisecond())
        );
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "year('1970-01-01T12:34:56Z+01:00')",
            "day(castAsOffsetDateTime('1970-01-01T12:00:00Z+01:00'), 2)"
    })
    void fieldFunction_parsingError(String text) {
        assertThrows(ParseCancellationException.class, () -> Exp.exp(text));
    }

    @ParameterizedTest
    @MethodSource
    void function(String text, Exp<?> expected) {
        Exp<?> exp = Exp.exp(text);
        assertInstanceOf(OffsetDateTimeExp.class, exp);
        assertEquals(expected, exp);
    }

    static Stream<Arguments> function() {
        return Stream.of(
                arguments("plusYears(offsetDateTime(1), 2)", Exp.$offsetDateTime(1).plusYears(2)),
                arguments("plusMonths(offsetDateTime(1), 6)", Exp.$offsetDateTime(1).plusMonths(6)),
                arguments("plusWeeks(offsetDateTime(1), 3)", Exp.$offsetDateTime(1).plusWeeks(3)),
                arguments("plusDays(offsetDateTime(1), 10)", Exp.$offsetDateTime(1).plusDays(10)),
                arguments("plusHours(offsetDateTime(1), 2)", Exp.$offsetDateTime(1).plusHours(2)),
                arguments("plusMinutes(offsetDateTime(1), 30)", Exp.$offsetDateTime(1).plusMinutes(30)),
                arguments("plusSeconds(offsetDateTime(1), 45)", Exp.$offsetDateTime(1).plusSeconds(45)),
                arguments("plusMilliseconds(offsetDateTime(1), 500)", Exp.$offsetDateTime(1).plusMilliseconds(500)),
                arguments("plusNanos(offsetDateTime(1), 1000)", Exp.$offsetDateTime(1).plusNanos(1000)),
                arguments("plusYears(offsetDateTime(1), -2)", Exp.$offsetDateTime(1).plusYears(-2))
        );
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "plusYears('1970-01-01T12:00:00Z+01:00')",
            "plusMonths(offsetDateTime(1))",
            "plusWeeks(offsetDateTime(1), 1 + 2)",
            "plusDays(offsetDateTime(1), '3')",
            "plusHours(offsetDateTime(1), null)",
    })
    void function_parsingError(String text) {
        assertThrows(ParseCancellationException.class, () -> Exp.exp(text));
    }
}
