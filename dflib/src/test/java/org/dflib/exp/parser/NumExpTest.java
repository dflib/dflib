package org.dflib.exp.parser;

import org.antlr.v4.runtime.misc.ParseCancellationException;
import org.dflib.Condition;
import org.dflib.Exp;
import org.dflib.NumExp;
import org.dflib.exp.parser.antlr4.LexerCancellationException;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.params.provider.Arguments.arguments;

public class NumExpTest {

    @ParameterizedTest
    @MethodSource
    void exp(String text, Exp<?> expected) {
        Exp<?> exp = Exp.exp(text);
        assertInstanceOf(NumExp.class, exp);
        assertEquals(expected, exp);
    }

    static Stream<Arguments> exp() {
        return Stream.of(
                arguments("1 + 2", Exp.$intVal(1).add(2)),
                arguments("3 - 1", Exp.$intVal(3).sub(1)),
                arguments("2 * 3", Exp.$intVal(2).mul(3)),
                arguments("6 / 2", Exp.$intVal(6).div(2)),
                arguments("5 % 2", Exp.$intVal(5).mod(2)),
                arguments("1 + 2 * 3", Exp.$intVal(1).add(Exp.$intVal(2).mul(3))),
                arguments("(1 + 2) * 3", Exp.$intVal(1).add(2).mul(3)),
                arguments("sum(int(1)) + abs(int(2))", Exp.$int(1).sum().add(Exp.$int(2).abs())),
                arguments("int(1) + castAsDouble('3.0')", Exp.$int(1).add(Exp.$val("3.0").castAsDouble()))
        );
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "1 +",
            "(1 + 2",
            "1 ** 2",
    })
    void exp_parsingError(String text) {
        assertThrows(ParseCancellationException.class, () -> Exp.exp(text));
    }

    @ParameterizedTest
    @MethodSource
    void integerScalar(String text, Exp<?> expected) {
        Exp<?> exp = Exp.exp(text);
        assertInstanceOf(NumExp.class, exp);
        assertEquals(expected, exp);
    }

    static Stream<Arguments> integerScalar() {
        return Stream.of(
                arguments("123", Exp.$val(123)),
                arguments("-456", Exp.$val(-456)),
                arguments("+0", Exp.$val(0)),
                arguments("0", Exp.$val(0)),
                arguments("0X1A3F", Exp.$val(0x1A3F)),
                arguments("0xabc", Exp.$val(0xABC)),
                arguments("-0xFF", Exp.$val(-0xFF)),
                arguments("+0x10", Exp.$val(0x10)),
                arguments("0b1010", Exp.$val(0b1010)),
                arguments("0B1101", Exp.$val(0b1101)),
                arguments("-0b11111111", Exp.$val(-0b11111111)),
                arguments("+0b1", Exp.$val(0b1)),
                arguments("0123", Exp.$val(0123)),
                arguments("-077", Exp.$val(-077)),
                arguments("2147483647", Exp.$val(Integer.MAX_VALUE)),
                arguments("-2147483648", Exp.$val(Integer.MIN_VALUE)),
                arguments("000", Exp.$val(0)),
                arguments("-000", Exp.$val(0))
        );
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "123abc",
            "0xGHIJ",
            "0x1A3.5",
            "0b102",
            "0b1.01",
            "089",
            "0x",
            "0b",
    })
    void integerScalar_parsingError(String text) {
        assertThrows(ParseCancellationException.class, () -> Exp.exp(text));
    }

    @ParameterizedTest
    @ValueSource(strings = {"2147483648", "-2147483649"})
    void integerScalar_outOfRange(String text) {
        assertThrows(NumberFormatException.class, () -> Exp.exp(text));
    }

    @ParameterizedTest
    @MethodSource
    void longScalar(String text, Exp<?> expected) {
        Exp<?> exp = Exp.exp(text);
        assertInstanceOf(NumExp.class, exp);
        assertEquals(expected, exp);
    }

    static Stream<Arguments> longScalar() {
        return Stream.of(
                arguments("123L", Exp.$val(123L)),
                arguments("-456L", Exp.$val(-456L)),
                arguments("+0L", Exp.$val(0L)),
                arguments("0x1A3FL", Exp.$val(0x1A3FL)),
                arguments("-0xFFL", Exp.$val(-0xFFL)),
                arguments("0b1010L", Exp.$val(0b1010L)),
                arguments("-0b11111111L", Exp.$val(-0b11111111L)),
                arguments("0123L", Exp.$val(0123L)),
                arguments("-077L", Exp.$val(-077L)),
                arguments("9223372036854775807L", Exp.$val(Long.MAX_VALUE)),
                arguments("-9223372036854775808L", Exp.$val(Long.MIN_VALUE)),
                arguments("0L", Exp.$val(0L)),
                arguments("-0L", Exp.$val(0L))
        );
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "123Labc",
            "123abcL",
            "0xGHIJL",
            "0x1A3.5L",
            "0b102L",
            "0b1.01L",
            "089L",
            "L",
            "-L",
            "0xL",
            "0bL",
            "1e10L",
    })
    void longScalar_parsingError(String text) {
        assertThrows(ParseCancellationException.class, () -> Exp.exp(text));
    }

    @ParameterizedTest
    @ValueSource(strings = {"9223372036854775808L", "-9223372036854775809L"})
    void longScalar_outOfRange(String text) {
        assertThrows(NumberFormatException.class, () -> Exp.exp(text));
    }

    @ParameterizedTest
    @MethodSource
    void floatScalar(String text, Exp<?> expected) {
        Exp<?> exp = Exp.exp(text);
        assertInstanceOf(NumExp.class, exp);
        assertEquals(expected, exp);
    }

    static Stream<Arguments> floatScalar() {
        return Stream.of(
                arguments("123.45f", Exp.$val(123.45f)),
                arguments("-456.78f", Exp.$val(-456.78f)),
                arguments("+0.0f", Exp.$val(0.0f)),
                arguments("0.0f", Exp.$val(0.0f)),
                arguments(".5f", Exp.$val(0.5f)),
                arguments("-.5f", Exp.$val(-0.5f)),
                arguments("+.0f", Exp.$val(0.0f)),
                arguments("123.45000f", Exp.$val(123.45f)),
                arguments("0.0000f", Exp.$val(0.0f)),
                arguments("1e10f", Exp.$val(1e10f)),
                arguments("1E10f", Exp.$val(1e10f)),
                arguments("-1e-10f", Exp.$val(-1e-10f)),
                arguments("+1.23e+4f", Exp.$val(1.23e4f)),
                arguments("123.45f", Exp.$val(123.45f)),
                arguments("-456.78F", Exp.$val(-456.78f)),
                arguments("3.4028235e38f", Exp.$val(Float.MAX_VALUE)),
                arguments("-3.4028235e38f", Exp.$val(-Float.MAX_VALUE)),
                arguments("1.4e-45f", Exp.$val(Float.MIN_VALUE)),
                arguments("-1.4e-45f", Exp.$val(-Float.MIN_VALUE))
        );
    }

    @ParameterizedTest
    @MethodSource
    void floatScalar_outOfRange(String text, Exp<?> expected) {
        Exp<?> exp = Exp.exp(text);
        assertInstanceOf(NumExp.class, exp);
        assertEquals(expected, exp);
    }

    static Stream<Arguments> floatScalar_outOfRange() {
        return Stream.of(
                arguments("3.4028235e39f", Exp.$val(Float.POSITIVE_INFINITY)),
                arguments("-3.4028235e39f", Exp.$val(Float.NEGATIVE_INFINITY)),
                arguments("1.4e-46f", Exp.$val(0f)),
                arguments("-1.4e-46f", Exp.$val(-0f))
        );
    }

    @ParameterizedTest
    @ValueSource(strings = {"1ef", "1.23.4f", "1e1.0f", "1.23f4"})
    void floatScalar_parsingError(String text) {
        assertThrows(ParseCancellationException.class, () -> Exp.exp(text));
    }

    @ParameterizedTest
    @ValueSource(strings = {".e10f"})
    void floatScalar_lexicalError(String text) {
        assertThrows(LexerCancellationException.class, () -> Exp.exp(text));
    }

    @ParameterizedTest
    @MethodSource
    void doubleScalar(String text, Exp<?> expected) {
        Exp<?> exp = Exp.exp(text);
        assertInstanceOf(NumExp.class, exp);
        assertEquals(expected, exp);
    }

    static Stream<Arguments> doubleScalar() {
        return Stream.of(
                arguments("123.45", Exp.$val(123.45)),
                arguments("-456.78", Exp.$val(-456.78)),
                arguments("+0.0", Exp.$val(0.0)),
                arguments("0.0", Exp.$val(0.0)),
                arguments(".5", Exp.$val(0.5)),
                arguments("-.5", Exp.$val(-0.5)),
                arguments("+.0", Exp.$val(0.0)),
                arguments("123.45000", Exp.$val(123.45)),
                arguments("0.0000", Exp.$val(0.0)),
                arguments("1e10", Exp.$val(1e10)),
                arguments("1E10", Exp.$val(1e10)),
                arguments("-1e-10", Exp.$val(-1e-10)),
                arguments("+1.23e+4", Exp.$val(1.23e4)),
                arguments("123.45", Exp.$val(123.45)),
                arguments("-456.78", Exp.$val(-456.78)),
                arguments("1.7976931348623157e308", Exp.$val(Double.MAX_VALUE)),
                arguments("-1.7976931348623157e308", Exp.$val(-Double.MAX_VALUE)),
                arguments("4.9e-324", Exp.$val(Double.MIN_VALUE)),
                arguments("-4.9e-324", Exp.$val(-Double.MIN_VALUE))
        );
    }

    @ParameterizedTest
    @MethodSource
    void doubleScalar_outOfRange(String text, Exp<?> expected) {
        Exp<?> exp = Exp.exp(text);
        assertInstanceOf(NumExp.class, exp);
        assertEquals(expected, exp);
    }

    static Stream<Arguments> doubleScalar_outOfRange() {
        return Stream.of(
                arguments("1.7976931348623157e309", Exp.$val(Double.POSITIVE_INFINITY)),
                arguments("-1.7976931348623157e309", Exp.$val(Double.NEGATIVE_INFINITY)),
                arguments("4.9e-325", Exp.$val(0.0)),
                arguments("-4.9e-325", Exp.$val(-0.0))
        );
    }

    @ParameterizedTest
    @ValueSource(strings = {"1e", "1.23.4", "1e1.0", "1.23d4"})
    void doubleScalar_parsingError(String text) {
        assertThrows(ParseCancellationException.class, () -> Exp.exp(text));
    }

    @ParameterizedTest
    @ValueSource(strings = {".e10"})
    void doubleScalar_lexicalError(String text) {
        assertThrows(LexerCancellationException.class, () -> Exp.exp(text));
    }
    
    @ParameterizedTest
    @MethodSource
    void column(String text, Exp<?> expected) {
        Exp<?> exp = Exp.exp(text);
        assertInstanceOf(NumExp.class, exp);
        assertEquals(expected, exp);
    }

    static Stream<Arguments> column() {
        return Stream.of(
                arguments("int(a)", Exp.$int("a")),
                arguments("long(a)", Exp.$long("a")),
                arguments("float(a)", Exp.$float("a")),
                arguments("double(a)", Exp.$double("a")),
                arguments("decimal(a)", Exp.$decimal("a")),
                arguments("int('a')", Exp.$int("a")),
                arguments("int(\"a\")", Exp.$int("a")),
                arguments("int(1)", Exp.$int(1))
        );
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "INT(a)",
            "int(1.1)",
            "int(null)",
            "int(true)",
            "int(int(1))",
    })
    void column_parsingError(String text) {
        assertThrows(ParseCancellationException.class, () -> Exp.exp(text));
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "int(-1)",
    })
    void column_apiError(String text) {
        assertThrows(IllegalArgumentException.class, () -> Exp.exp(text));
    }

    @ParameterizedTest
    @MethodSource
    void cast(String text, Exp<?> expected) {
        Exp<?> exp = Exp.exp(text);
        assertInstanceOf(NumExp.class, exp);
        assertEquals(expected, exp);
    }

    static Stream<Arguments> cast() {
        return Stream.of(
                arguments("castAsInt(1)", Exp.$val(1).castAsInt()),
                arguments("castAsLong(1)", Exp.$val(1).castAsLong()),
                arguments("castAsFloat(1)", Exp.$val(1).castAsFloat()),
                arguments("castAsDouble(1)", Exp.$val(1).castAsDouble()),
                arguments("castAsDecimal(1)", Exp.$val(1).castAsDecimal()),
                arguments("castAsInt(null)", Exp.$val(null).castAsInt()),
                arguments("castAsInt(1L)", Exp.$val(1L).castAsInt()),
                arguments("castAsInt(1f)", Exp.$val(1f).castAsInt()),
                arguments("castAsInt(1d)", Exp.$val(1d).castAsInt()),
                arguments("castAsInt(true)", Exp.$val(true).castAsInt()),
                arguments("castAsInt('1')", Exp.$val("1").castAsInt()),
                arguments("castAsInt(castAsTime('12:00:00'))", Exp.$val("12:00:00").castAsTime().castAsInt()),
                arguments("castAsInt(castAsDate('2024-01-15'))", Exp.$val("2024-01-15").castAsDate().castAsInt()),
                arguments("castAsInt(castAsDateTime('2024-01-15T12:00:00Z'))",
                        Exp.$val("2024-01-15T12:00:00Z").castAsDateTime().castAsInt()),
                arguments("castAsInt(castAsOffsetDateTime('2024-01-15T12:00:00Z+01:00'))",
                        Exp.$val("2024-01-15T12:00:00Z+01:00").castAsOffsetDateTime().castAsInt())
        );
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "CASTASINT(1)",
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
                arguments("5 > 3", Exp.$intVal(5).gt(Exp.$val(3))),
                arguments("5 >= 3", Exp.$intVal(5).ge(Exp.$val(3))),
                arguments("5 < 3", Exp.$intVal(5).lt(Exp.$val(3))),
                arguments("5 <= 3", Exp.$intVal(5).le(Exp.$val(3))),
                arguments("5 = 5", Exp.$intVal(5).eq(Exp.$val(5))),
                arguments("5 != 3", Exp.$intVal(5).ne(Exp.$val(3))),
                arguments("5 between 3 and 7", Exp.$intVal(5).between(Exp.$val(3), Exp.$val(7))),
                arguments("int(1) > 10", Exp.$int(1).gt(Exp.$val(10))),
                arguments("abs(int(1)) > 10", Exp.$int(1).abs().gt(Exp.$val(10))),
                arguments("avg(int(1)) <= 20", Exp.$int(1).avg().le(Exp.$val(20))),
                arguments("int(1) + int(2) = 15", Exp.$int(1).add(Exp.$int(2)).eq(Exp.$val(15))),
                arguments("(int(1) % int(2)) = 15", Exp.$int(1).mod(Exp.$int(2)).eq(Exp.$val(15)))
        );
    }

    @ParameterizedTest
    @ValueSource(strings = {"5 > '3'", "int(col1) = null", "int(col1) != false"})
    void relation_parsingError(String text) {
        assertThrows(ParseCancellationException.class, () -> Exp.exp(text));
    }

    @ParameterizedTest
    @MethodSource
    void function(String text, Exp<?> expected) {
        Exp<?> exp = Exp.exp(text);
        assertInstanceOf(NumExp.class, exp);
        assertEquals(expected, exp);
    }

    static Stream<Arguments> function() {
        return Stream.of(
                arguments("count()", Exp.count()),
                arguments("count( )", Exp.count()),
                arguments("count(int(1) > 0)", Exp.count(Exp.$int(1).lt(0))),
                arguments("rowNum()", Exp.rowNum()),
                arguments("rowNum( )", Exp.rowNum()),
                arguments("abs(-5)", Exp.$intVal(-5).abs()),
                arguments("round(3.14)", Exp.$doubleVal(3.14).round()),
                arguments("len('hello')", Exp.$val("hello").mapVal(String::length).castAsInt())
        );
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "abs('-4')",
            "sqrt(4)",
            "log(10)",
            "exp(1)",
            "pow(2, 3)",
            "sin(0)",
            "cos(0)",
            "tan(0)",
    })
    void function_parsingError(String text) {
        assertThrows(ParseCancellationException.class, () -> Exp.exp(text));
    }

    @ParameterizedTest
    @MethodSource
    void aggregate(String text, Exp<?> expected) {
        Exp<?> exp = Exp.exp(text);
        assertInstanceOf(NumExp.class, exp);
        assertEquals(expected, exp);
    }

    static Stream<Arguments> aggregate() {
        return Stream.of(
                arguments("sum(int(1))", Exp.$int(1).sum()),
                arguments("cumSum(int(1))", Exp.$int(1).cumSum()),
                arguments("min(int(1))", Exp.$int(1).min()),
                arguments("max(int(1))", Exp.$int(1).max()),
                arguments("avg(int(1))", Exp.$int(1).avg()),
                arguments("median(int(1))", Exp.$int(1).median()),
                arguments("quantile(int(1), 0.5)", Exp.$int(1).quantile(0.5)),
                arguments("sum(int(1), int(1) > 0)", Exp.$int(1).sum(Exp.$int(1).gt(1))),
                arguments("min(int(1), int(1) > 0)", Exp.$int(1).min(Exp.$int(1).gt(1))),
                arguments("max(int(1), int(1) > 0)", Exp.$int(1).max(Exp.$int(1).gt(1))),
                arguments("avg(int(1), int(1) > 0)", Exp.$int(1).avg(Exp.$int(1).gt(1))),
                arguments("median(int(1), int(1) > 0)", Exp.$int(1).median(Exp.$int(1).gt(1))),
                arguments("quantile(int(1), 0.5, int(1) > 0)", Exp.$int(1).quantile(0.5, Exp.$int(1).gt(1))),
                arguments("sum(int(1), int(2) > 0)", Exp.$int(1).sum(Exp.$int(2).gt(1)))
        );
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "SUM(int(1))",
            "min()",
            "max(int(1), )",
            "avg(int(1), 0)",
            "quantile(int(1), int(1) > 0)",
            "cumSum(int(1), 2)",
    })
    void aggregate_parsingError(String text) {
        assertThrows(ParseCancellationException.class, () -> Exp.exp(text));
    }
}
