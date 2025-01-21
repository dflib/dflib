package org.dflib.exp.parser;

import org.dflib.Condition;
import org.dflib.Exp;
import org.dflib.NumExp;
import org.dflib.exp.num.DecimalScalarExp;
import org.dflib.exp.num.DoubleScalarExp;
import org.dflib.exp.num.FloatScalarExp;
import org.dflib.exp.num.IntScalarExp;
import org.dflib.exp.num.LongScalarExp;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.math.BigDecimal;
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
                arguments("int(1) + castAsDouble('3.0')", Exp.$int(1).add(Exp.$strVal("3.0").castAsDouble()))
        );
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "1 +",
            "(1 + 2",
            "1 ** 2",
    })
    void exp_throws(String text) {
        assertThrows(ExpParserException.class, () -> Exp.exp(text));
    }

    @ParameterizedTest
    @MethodSource
    void integerScalar(String text, Exp<?> expected) {
        Exp<?> exp = Exp.exp(text);
        assertInstanceOf(IntScalarExp.class, exp);
        assertEquals(expected, exp);
    }

    static Stream<Arguments> integerScalar() {
        return Stream.of(
                arguments("123", Exp.$val(123)),
                arguments("123i", Exp.$val(123)),
                arguments("123I", Exp.$val(123)),
                arguments("-456", Exp.$val(-456)),
                arguments("123_456", Exp.$val(123_456)),
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
            "999999999999999i",
            "123abc",
            "0xGHIJ",
            "0x1A3.5",
            "0b102",
            "0b1.01",
            "0x",
            "0b",
    })
    void integerScalar_throws(String text) {
        assertThrows(ExpParserException.class, () -> Exp.exp(text));
    }

    @ParameterizedTest
    @MethodSource
    void longScalar(String text, Exp<?> expected) {
        Exp<?> exp = Exp.exp(text);
        assertInstanceOf(LongScalarExp.class, exp);
        assertEquals(expected, exp);
    }

    static Stream<Arguments> longScalar() {
        return Stream.of(
                arguments("999999999999999", Exp.$val(999999999999999L)),
                arguments("999999999999999l", Exp.$val(999999999999999L)),
                arguments("999999999999999L", Exp.$val(999999999999999L)),
                arguments("-999999999999999", Exp.$val(-999999999999999L)),
                arguments("+999999999999999", Exp.$val(999999999999999L)),
                arguments("999_999_987_654_321", Exp.$val(999_999_987_654_321L)),
                arguments("0x999999999999A3F", Exp.$val(0x999999999999A3FL)),
                arguments("-0xFFFFFFFFFFF", Exp.$val(-0xFFFFFFFFFFFL)),
                arguments("0123123123123123", Exp.$val(0123123123123123L)),
                arguments("-0123123123123123", Exp.$val(-0123123123123123L)),
                arguments("9223372036854775807", Exp.$val(Long.MAX_VALUE)),
                arguments("-9223372036854775808", Exp.$val(Long.MIN_VALUE))
        );
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "9999999999999999999999999l",
            "99999999999999abc",
            "0xFFFFFFFFFFFFFGHIJ",
            "0xFFFFFFFFFFFF1A3.5",
    })
    void longScalar_throws(String text) {
        assertThrows(ExpParserException.class, () -> Exp.exp(text));
    }

    // TODO: BigIntScalarExp ???

    @ParameterizedTest
    @MethodSource
    void floatScalar(String text, Exp<?> expected) {
        Exp<?> exp = Exp.exp(text);
        assertInstanceOf(FloatScalarExp.class, exp);
        assertEquals(expected, exp);
    }

    static Stream<Arguments> floatScalar() {
        return Stream.of(
                arguments("1f", Exp.$floatVal(1f)),
                arguments("123.4", Exp.$floatVal(123.4f)),
                arguments("123.4f", Exp.$floatVal(123.4f)),
                arguments("123.4F", Exp.$floatVal(123.4f)),
                arguments("-456.7", Exp.$floatVal(-456.7f)),
                arguments("123_456.7", Exp.$floatVal(123_456.7f)),
                arguments("+0.0", Exp.$floatVal(0.0f)),
                arguments("0.0", Exp.$floatVal(0.0f)),
                arguments(".5", Exp.$floatVal(.5f)),
                arguments("-.5", Exp.$floatVal(-.5f)),
                arguments("+.0", Exp.$floatVal(.0f)),
                arguments("123.4000", Exp.$floatVal(123.4f)),
                arguments("0.0000", Exp.$floatVal(0.0f)),
                arguments("1e10", Exp.$floatVal(1e10f)),
                arguments("1E10", Exp.$floatVal(1e10f)),
                arguments("-1e-10", Exp.$floatVal(-1e-10f)),
                arguments("+1.23e+4", Exp.$floatVal(1.23e4f)),
                arguments("9.999999E37", Exp.$floatVal(9.999999E37f)),
                arguments("-9.999999E37", Exp.$floatVal(-9.999999E37f)),
                arguments("1E-37", Exp.$floatVal(1E-37f)),
                arguments("-1E-37", Exp.$floatVal(-1E-37f)),
                arguments("0x1.0p-12f", Exp.$floatVal(0x1.0p-12f)),
                arguments("-0x1.0p-12f", Exp.$floatVal(-0x1.0p-12f)),
                arguments("0x1.fffffeP+12f", Exp.$floatVal(0x1.fffffeP+12f)),
                arguments("-0x1.fffffep+12f", Exp.$floatVal(-0x1.fffffeP+12f))
        );
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "1e",
            "1e1.0",
            ".e10",
    })
    void floatScalar_throws(String text) {
        assertThrows(ExpParserException.class, () -> Exp.exp(text));
    }

    @ParameterizedTest
    @MethodSource
    void doubleScalar(String text, Exp<?> expected) {
        Exp<?> exp = Exp.exp(text);
        assertInstanceOf(DoubleScalarExp.class, exp);
        assertEquals(expected, exp);
    }

    static Stream<Arguments> doubleScalar() {
        return Stream.of(
                arguments("1d", Exp.$doubleVal(1)),
                arguments("123.45e50", Exp.$doubleVal(123.45e50)),
                arguments("123.45e50d", Exp.$doubleVal(123.45e50)),
                arguments("123.45e50D", Exp.$doubleVal(123.45e50)),
                arguments("-456.78e50", Exp.$doubleVal(-456.78e50)),
                arguments("123_456.78_9e50", Exp.$doubleVal(123_456.78_9e50)),
                arguments(".5e50", Exp.$doubleVal(.5e50)),
                arguments("-.5e50", Exp.$doubleVal(-.5e50)),
                arguments("123.45000e50", Exp.$doubleVal(123.45e50)),
                arguments("1e50", Exp.$doubleVal(1e50)),
                arguments("1E50", Exp.$doubleVal(1E50)),
                arguments("-1e-50", Exp.$doubleVal(-1e-50)),
                arguments("9.999999999999999E307", Exp.$doubleVal(9.999999999999999E307)),
                arguments("-9.999999999999999E307", Exp.$doubleVal(-9.999999999999999E307)),
                arguments("1E-307", Exp.$doubleVal(1E-307)),
                arguments("-1E-307", Exp.$doubleVal(-1E-307)),
                arguments("0x1.0p-122d", Exp.$doubleVal(0x1.0p-122)),
                arguments("-0x1.0p-122d", Exp.$doubleVal(-0x1.0p-122)),
                arguments("0x1.fffffeP+123d", Exp.$doubleVal(0x1.fffffeP+123)),
                arguments("-0x1.fffffep+123d", Exp.$doubleVal(-0x1.fffffeP+123))
        );
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "1ed",
            "1e1.0d",
            ".e10d",
    })
    void doubleScalar_throws(String text) {
        assertThrows(ExpParserException.class, () -> Exp.exp(text));
    }

    @ParameterizedTest
    @MethodSource
    void decimalScalar(String text, Exp<?> expected) {
        Exp<?> exp = Exp.exp(text);
        assertInstanceOf(DecimalScalarExp.class, exp);
        assertEquals(expected, exp);
    }

    static Stream<Arguments> decimalScalar() {
        return Stream.of(
                arguments("1m", Exp.$decimalVal(new BigDecimal(1))),
                arguments("123.45e1000m", Exp.$decimalVal(new BigDecimal("123.45e1000"))),
                arguments("123.45e1000M", Exp.$decimalVal(new BigDecimal("123.45e1000"))),
                arguments("-456.78e1000", Exp.$decimalVal(new BigDecimal("-456.78e1000"))),
                arguments("123_456.78_9e1000", Exp.$decimalVal(new BigDecimal("123456.789e1000"))),
                arguments(".5e1000", Exp.$decimalVal(new BigDecimal(".5e1000"))),
                arguments("-.5e1000", Exp.$decimalVal(new BigDecimal("-.5e1000"))),
                arguments("123.45000e1000", Exp.$decimalVal(new BigDecimal("123.45e1000"))),
                arguments("1e1000", Exp.$decimalVal(new BigDecimal("1e1000"))),
                arguments("1E1000", Exp.$decimalVal(new BigDecimal("1E1000"))),
                arguments("-1e-1000", Exp.$decimalVal(new BigDecimal("-1e-1000"))),
                arguments("0x1.0p-1044", Exp.$decimalVal(new BigDecimal("5.30498947741318078777846E-315"))),
                arguments("-0x1.0p-1044", Exp.$decimalVal(new BigDecimal("-5.30498947741318078777846E-315")))
        );
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "1em",
            "1e1.0m",
            ".e10m",
    })
    void decimalScalar_throws(String text) {
        assertThrows(ExpParserException.class, () -> Exp.exp(text));
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
    @MethodSource
    void cast(String text, Exp<?> expected) {
        Exp<?> exp = Exp.exp(text);
        assertInstanceOf(NumExp.class, exp);
        assertEquals(expected, exp);
    }

    static Stream<Arguments> cast() {
        return Stream.of(
                arguments("castAsInt(1)", Exp.$intVal(1).castAsInt()),
                arguments("castAsLong(9999999999)", Exp.$longVal(9999999999L).castAsLong()),
                arguments("castAsFloat(1.1)", Exp.$floatVal(1.1f).castAsFloat()),
                arguments("castAsDouble(1e-100)", Exp.$doubleVal(1e-100).castAsDouble()),
                arguments("castAsDecimal(1e1000)", Exp.$decimalVal(new BigDecimal("1e1000")).castAsDecimal()),
                arguments("castAsInt(null)", Exp.$val(null).castAsInt()),
                arguments("castAsInt(true)", Exp.$boolVal(true).castAsInt()),
                arguments("castAsInt('1')", Exp.$strVal("1").castAsInt())
        );
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
    void relation_throws(String text) {
        assertThrows(ExpParserException.class, () -> Exp.exp(text));
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
                arguments("round(3.14)", Exp.$floatVal(3.14f).round())
        );
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
}
