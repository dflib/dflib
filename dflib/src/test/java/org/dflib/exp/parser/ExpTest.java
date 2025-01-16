package org.dflib.exp.parser;

import org.antlr.v4.runtime.misc.ParseCancellationException;
import org.dflib.Exp;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.params.provider.Arguments.arguments;

class ExpTest {

    @ParameterizedTest
    @MethodSource
    void nullScalar(String text, Exp<?> expected) {
        Exp<?> exp = Exp.exp(text);
        assertEquals(expected, exp);
    }

    static Stream<Arguments> nullScalar() {
        return Stream.of(
                arguments("null", Exp.$val(null))
        );
    }

    @ParameterizedTest
    @ValueSource(strings = {"NULL", "Null", "None", "nil", "void"})
    void nullScalar_parsingError(String text) {
        assertThrows(ParseCancellationException.class, () -> Exp.exp(text));
    }

    @ParameterizedTest
    @MethodSource
    void ifExp(String text, Exp<?> expected) {
        Exp<?> exp = Exp.exp(text);
        assertEquals(expected, exp);
    }

    static Stream<Arguments> ifExp() {
        return Stream.of(
                arguments("if(true, 1, 0)", Exp.ifExp(Exp.$boolVal(true), Exp.$val(1), Exp.$val(0))),
                arguments("if(false, 1, 0)", Exp.ifExp(Exp.$boolVal(false), Exp.$val(1), Exp.$val(0))),
                arguments("if(false, 1, 0)", Exp.ifExp(Exp.$boolVal(false), Exp.$val(1), Exp.$val(0))),
                arguments("if(bool(1), 1, 0)", Exp.ifExp(Exp.$bool(1), Exp.$val(1), Exp.$val(0))),
                arguments("if(not bool(1), 1, 0)", Exp.ifExp(Exp.$bool(1).not(), Exp.$val(1), Exp.$val(0))),
                arguments("if(bool(1) and bool(2), 1, 0)",
                        Exp.ifExp(Exp.$bool(1).and(Exp.$bool(2)), Exp.$val(1), Exp.$val(0))),
                arguments("if(castAsBool(1), 1, 0)",
                        Exp.ifExp(Exp.$intVal(1).castAsBool(), Exp.$val(1), Exp.$val(0))),
                arguments("if(1 > 0, 'yes', 'no')",
                        Exp.ifExp(Exp.$intVal(1).gt(Exp.$val(0)), Exp.$val("yes"), Exp.$val("no")))
        );
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "IF(true, 1, 0)",
            "if(1, 1, 0)",
//            "if(true, 1, null)",
//            "if(true, 1, 'other')",
            "if(true, 1)",
            "if(true, 1, 0, 2)",
    })
    void ifExp_parsingError(String text) {
        assertThrows(ParseCancellationException.class, () -> Exp.exp(text));
    }

    @ParameterizedTest
    @MethodSource
    void ifNull(String text, Exp<?> expected) {
        Exp<?> exp = Exp.exp(text);
        assertEquals(expected, exp);
    }

    static Stream<Arguments> ifNull() {
        return Stream.of(
                arguments("ifNull(int(1), 0)", Exp.ifNull(Exp.$int(1), 0)),
                arguments("ifNull(str(1), 'unknown')", Exp.ifNull(Exp.$str(1), Exp.$strVal("unknown"))),
                arguments("ifNull(date(1), 1970-01-01)",
                        Exp.ifNull(Exp.$date(1), Exp.$val(LocalDate.parse("1970-01-01"))))
        );
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "IFNULL($int(1), 0)",
            "ifNull($int(1))",
            "ifNull($int(1), )",
            "ifNull(, $int(1))",
//            "ifNull($int(1), $str('name'))",
//            "ifNull(int(1), ifNull(int(2), 0))",
//            "ifNull(int(1), null)",
    })
    void ifNull_parsingError(String text) {
        assertThrows(ParseCancellationException.class, () -> Exp.exp(text));
    }

    @ParameterizedTest
    @MethodSource
    void shift(String text, Exp<?> expected) {
        Exp<?> exp = Exp.exp(text);
        assertEquals(expected, exp);
    }

    static Stream<Arguments> shift() {
        return Stream.of(
                arguments("shift(int(1), 2)", Exp.$int(1).shift(2)),
                arguments("shift(int(1), -2)", Exp.$int(1).shift(-2)),
                arguments("shift(int(1), 2, 0)", Exp.$int(1).shift(2, 0)),
                arguments("shift(1 + 2, 1)", Exp.$intVal(1).add(2).shift(1)),
                arguments("shift(str(1), 1)", Exp.$str(1).shift(1)),
                arguments("shift(str(1), 1, 'default')", Exp.$str(1).shift(1, "default")),
                arguments("shift(str(1), -1)", Exp.$str(1).shift(-1))
        );
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "SHIFT(int(1), 2)",
            "shift(int(1))",
            "shift(int(1), )",
            "shift(, 2)",
            "shift(int(1), 2, 'replace')",
            "shift(plusDays(date(1), 1), 1)",
    })
    void shift_parsingError(String text) {
        assertThrows(ParseCancellationException.class, () -> Exp.exp(text));
    }

    @ParameterizedTest
    @MethodSource
    void positionalAggregate(String text, Exp<?> expected) {
        Exp<?> exp = Exp.exp(text);
        assertEquals(expected, exp);
    }

    static Stream<Arguments> positionalAggregate() {
        return Stream.of(
                arguments("first(int(1))", Exp.$int(1).first()),
                arguments("last(int(1))", Exp.$int(1).last()),
                arguments("first(int(1), int(1) > 0)", Exp.$int(1).first(Exp.$int(1).gt(Exp.$val(0)))),
                arguments("first(1 + 2)", Exp.$intVal(1).add(2).first()),
                arguments("first(sum(int(1)))", Exp.$int(1).sum().first()),
                arguments("first(int(1) > 0)", Exp.$int(1).gt(0).first())
        );
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "FIRST(int(1))",
            "first(int(1), )",
            "first(, int(1) > 0)",
            "first(int(1), 0)",
            "last(int(1), int(1) > 0)",
    })
    void positionalAggregate_parsingError(String text) {
        assertThrows(ParseCancellationException.class, () -> Exp.exp(text));
    }

    @ParameterizedTest
    @MethodSource
    void timeLiterals(String text, Exp<?> expected) {
        Exp<?> exp = Exp.exp(text);
        assertEquals(expected, exp);
    }

    static Stream<Arguments> timeLiterals() {
        return Stream.of(
                arguments("00:00", Exp.$timeVal(LocalTime.of(0, 0))),
                arguments("00:00:00", Exp.$timeVal(LocalTime.of(0, 0, 0))),
                arguments("00:00:00.000", Exp.$timeVal(LocalTime.of(0, 0, 0, 0))),
                arguments("23:59", Exp.$timeVal(LocalTime.of(23, 59))),
                arguments("23:59:59", Exp.$timeVal(LocalTime.of(23, 59, 59))),
                arguments("23:59:59.999", Exp.$timeVal(LocalTime.of(23, 59, 59, 999_000_000)))
        );
    }

    @ParameterizedTest
    @MethodSource
    void dateLiterals(String text, Exp<?> expected) {
        Exp<?> exp = Exp.exp(text);
        assertEquals(expected, exp);
    }

    static Stream<Arguments> dateLiterals() {
        return Stream.of(
                arguments("0000-01-01", Exp.$dateVal(LocalDate.of(0, 1, 1))),
                arguments("9999-12-31", Exp.$dateVal(LocalDate.of(9999, 12, 31))),
                arguments("2025-08-15", Exp.$dateVal(LocalDate.of(2025, 8, 15)))
        );
    }

    @ParameterizedTest
    @MethodSource
    void dateTimeLiterals(String text, Exp<?> expected) {
        Exp<?> exp = Exp.exp(text);
        assertEquals(expected, exp);
    }

    static Stream<Arguments> dateTimeLiterals() {
        return Stream.of(
                arguments("0000-01-01T00:00", Exp.$dateTimeVal(LocalDateTime.of(
                        LocalDate.of(0, 1, 1),
                        LocalTime.of(0, 0)))),
                arguments("9999-12-31T23:59:59", Exp.$dateTimeVal(LocalDateTime.of(
                        LocalDate.of(9999, 12, 31),
                        LocalTime.of(23, 59, 59))))
        );
    }

    @ParameterizedTest
    @MethodSource
    void dateTimeWithOffsetLiterals(String text, Exp<?> expected) {
        Exp<?> exp = Exp.exp(text);
        assertEquals(expected, exp);
    }

    static Stream<Arguments> dateTimeWithOffsetLiterals() {
        return Stream.of(
                arguments("0000-01-01T00:00Z", Exp.$offsetDateTimeVal(OffsetDateTime.of(
                        LocalDateTime.of(
                            LocalDate.of(0, 1, 1),
                            LocalTime.of(0, 0)
                        ),
                        ZoneOffset.ofHours(0)))),
                arguments("9999-12-31T23:59:59+12:30", Exp.$offsetDateTimeVal(OffsetDateTime.of(
                        LocalDateTime.of(
                                LocalDate.of(9999, 12, 31),
                                LocalTime.of(23, 59, 59)
                        ),
                        ZoneOffset.ofHoursMinutes(12, 30)))),
                arguments("9999-12-31T23:59:59-12:30", Exp.$offsetDateTimeVal(OffsetDateTime.of(
                        LocalDateTime.of(
                                LocalDate.of(9999, 12, 31),
                                LocalTime.of(23, 59, 59)
                        ),
                        ZoneOffset.ofHoursMinutes(-12, -30))))
        );
    }
}
