package org.dflib.exp.parser;

import org.antlr.v4.runtime.misc.ParseCancellationException;
import org.dflib.Condition;
import org.dflib.Exp;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.params.provider.Arguments.arguments;

public class ConditionTest {

    @ParameterizedTest
    @MethodSource
    void exp(String text, Exp<?> expected) {
        Exp<?> exp = Exp.exp(text);
        assertInstanceOf(Condition.class, exp);
        assertEquals(expected, exp);
    }

    static Stream<Arguments> exp() {
        return Stream.of(
                arguments("1 > 0", Exp.$intVal(1).gt(Exp.$val(0))),
                arguments("1 >= 0", Exp.$intVal(1).ge(Exp.$val(0))),
                arguments("1 < 0", Exp.$intVal(1).lt(Exp.$val(0))),
                arguments("1 <= 0", Exp.$intVal(1).le(Exp.$val(0))),
                arguments("1 = 1", Exp.$intVal(1).eq(Exp.$val(1))),
                arguments("1 != 0", Exp.$intVal(1).ne(Exp.$val(0))),
                arguments("true and false", Exp.$boolVal(true).and(Exp.$boolVal(false))),
                arguments("true or false", Exp.$boolVal(true).or(Exp.$boolVal(false))),
                arguments("not true", Exp.$boolVal(true).not()),
                arguments("not bool(1)", Exp.$bool(1).not()),
                arguments("not int(1) > 0", Exp.$int(1).gt(0).not())
        );
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "true and",
            "or false",
            "true or",
            "not",
            "1 >",
            "int(1) and int(2)",
    })
    void exp_throws(String text) {
        assertThrows(ExpParserException.class, () -> Exp.exp(text));
    }

    @ParameterizedTest
    @MethodSource
    void scalar(String text, Exp<?> expected) {
        Exp<?> exp = Exp.exp(text);
        assertInstanceOf(Condition.class, exp);
        assertEquals(expected, exp);
    }

    static Stream<Arguments> scalar() {
        return Stream.of(
                arguments("true", Exp.$val(true)),
                arguments("false", Exp.$val(false))
        );
    }

    @ParameterizedTest
    @ValueSource(strings = {"TRUE", "FALSE", "True", "False"})
    void scalar_throws(String text) {
        assertThrows(ExpParserException.class, () -> Exp.exp(text));
    }

    @ParameterizedTest

    @MethodSource
    void column(String text, Exp<?> expected) {
        Exp<?> exp = Exp.exp(text);
        assertInstanceOf(Condition.class, exp);
        assertEquals(expected, exp);
    }

    static Stream<Arguments> column() {
        return Stream.of(
                arguments("bool(a)", Exp.$bool("a")),
                arguments("bool('a')", Exp.$bool("a")),
                arguments("bool(\"a\")", Exp.$bool("a")),
                arguments("bool(1)", Exp.$bool(1))
        );
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "BOOL(a)",
            "bool(1.1)",
            "bool(null)",
            "bool(true)",
            "bool(int(1))",
            "bool(-1)",
    })
    void column_throws(String text) {
        assertThrows(ExpParserException.class, () -> Exp.exp(text));
    }

    @ParameterizedTest
    @MethodSource
    void cast(String text, Exp<?> expected) {
        Exp<?> exp = Exp.exp(text);
        assertInstanceOf(Condition.class, exp);
        assertEquals(expected, exp);
    }

    static Stream<Arguments> cast() {
        return Stream.of(
                arguments("castAsBool(null)", Exp.$val(null).castAsBool()),
                arguments("castAsBool(1)", Exp.$val(1).castAsBool()),
                arguments("castAsBool(true)", Exp.$boolVal(true).castAsBool()),
                arguments("castAsBool('1')", Exp.$strVal("1").castAsBool()),
                arguments("castAsBool(12:00:00)", Exp.$val(LocalTime.parse("12:00:00")).castAsBool()),
                arguments("castAsBool(2024-01-15)", Exp.$val(LocalDate.parse("2024-01-15")).castAsBool()),
                arguments("castAsBool(2024-01-15T12:00:00)",
                        Exp.$val(LocalDateTime.parse("2024-01-15T12:00:00")).castAsBool()),
                arguments("castAsBool(2024-01-15T12:00:00+01:00)",
                        Exp.$val(OffsetDateTime.parse("2024-01-15T12:00:00+01:00")).castAsBool())
        );
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "CASTASBOOL(1)",
    })
    void cast_throws(String text) {
        assertThrows(ExpParserException.class, () -> Exp.exp(text));
    }

    @ParameterizedTest
    @MethodSource
    void function(String text, Exp<?> expected) {
        Exp<?> exp = Exp.exp(text);
        assertInstanceOf(Condition.class, exp);
        assertEquals(expected, exp);
    }

    static Stream<Arguments> function() {
        return Stream.of(
                arguments("matches('hello', 'he.*')", Exp.$strVal("hello").matches("he.*")),
                arguments("startsWith('hello', 'he')", Exp.$strVal("hello").startsWith("he")),
                arguments("endsWith('hello', 'lo')", Exp.$strVal("hello").endsWith("lo")),
                arguments("contains('hello', 'ell')", Exp.$strVal("hello").contains("ell")),
                arguments("matches(str(1), 'he.*')", Exp.$str(1).matches("he.*")),
                arguments("matches(trim('  hello'), 'he.*')", Exp.$strVal("  hello").trim().matches("he.*"))
        );
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "matches('hello')",
            "startsWith('hello', 'll', 2)",
            "contains('hello', true)",
            "matches(123, 'pattern')",
            "startsWith(true, 'prefix')",
            "endsWith('hello', null)",
    })
    void function_throws(String text) {
        assertThrows(ExpParserException.class, () -> Exp.exp(text));
    }
}
