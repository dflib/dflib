package org.dflib.ql;

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
import java.math.BigInteger;
import java.util.stream.Stream;

import static org.dflib.Exp.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.params.provider.Arguments.arguments;

public class NumExpTest {

    @ParameterizedTest
    @MethodSource
    void test(String text, Exp<?> expected) {
        Exp<?> exp = parseExp(text);
        assertInstanceOf(NumExp.class, exp);
        assertEquals(expected, exp);
    }

    static Stream<Arguments> test() {
        return Stream.of(
                arguments("1 + 2", $intVal(1).add(2)),
                arguments("3 - 1", $intVal(3).sub(1)),
                arguments("2 * 3", $intVal(2).mul(3)),
                arguments("6 / 2", $intVal(6).div(2)),
                arguments("5 % 2", $intVal(5).mod(2)),
                arguments("1 + 2 * 3", $intVal(1).add($intVal(2).mul(3))),
                arguments("-int(1)", $int(1).negate()),
                arguments("sum(int(1)) + abs(int(2))", $int(1).sum().add($int(2).abs())),
                arguments("int(1) + castAsDouble('3.0')", $int(1).add($strVal("3.0").castAsDouble()))
        );
    }

    @ParameterizedTest
    @MethodSource
    void precedence(String text, Exp<?> expected) {
        Exp<?> exp = parseExp(text);
        assertInstanceOf(NumExp.class, exp);
        assertEquals(expected, exp);
    }

    // associativity must follow Java precedence rules (where operators match with DFLib)
    // https://docs.oracle.com/javase/tutorial/java/nutsandbolts/operators.html

    static Stream<Arguments> precedence() {
        return Stream.of(

                // () override precedence
                arguments("(1 + 2) * 3", $intVal(1).add($intVal(2)).mul(3)),
                arguments("1 + (2 - 3) + 4", $intVal(1).add($intVal(2).sub(3)).add(4)),

                // [*, /, %] take precedence over [+, -]
                arguments("int(1) + long(2) * long(3)", $int(1).add($long(2).mul($long(3)))),
                arguments("1 + 2 * 3", $intVal(1).add($intVal(2).mul(3))),
                arguments("1 + 2 / 3", $intVal(1).add($intVal(2).div(3))),
                arguments("1 + 2 % 3", $intVal(1).add($intVal(2).mod(3))),
                arguments("1 - 2 * 3", $intVal(1).sub($intVal(2).mul(3))),

                // equal precedence, must evaluate left to right
                arguments("1 / 2 * 3 % 4", $intVal(1).div(2).mul(3).mod(4)),
                arguments("1 % 2 * 3 / 4", $intVal(1).mod(2).mul(3).div(4)),
                arguments("1 + 2 - 3 + 4", $intVal(1).add(2).sub(3).add(4)),
                arguments("1 - 2 + 3 - 4", $intVal(1).sub(2).add(3).sub(4))
        );
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "1 +",
            "(1 + 2",
            "1 ** 2",
    })
    void test_throws(String text) {
        assertThrows(QLParserException.class, () -> parseExp(text));
    }

    @ParameterizedTest
    @MethodSource
    void integerScalar(String text, Exp<?> expected) {
        Exp<?> exp = parseExp(text);
        assertInstanceOf(IntScalarExp.class, exp);
        assertEquals(expected, exp);
    }

    static Stream<Arguments> integerScalar() {
        return Stream.of(
                arguments("123", $val(123)),
                arguments("123i", $val(123)),
                arguments("123I", $val(123)),
                arguments("-456", $val(-456)),
                arguments("123_456", $val(123_456)),
                arguments("+0", $val(0)),
                arguments("0", $val(0)),
                arguments("0X1A3F", $val(0x1A3F)),
                arguments("0xabc", $val(0xABC)),
                arguments("-0xFF", $val(-0xFF)),
                arguments("+0x10", $val(0x10)),
                arguments("2147483647", $val(Integer.MAX_VALUE)),
                arguments("-2147483648", $val(Integer.MIN_VALUE)),
                arguments("000", $val(0)),
                arguments("-000", $val(0))
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
        assertThrows(QLParserException.class, () -> parseExp(text));
    }

    @ParameterizedTest
    @MethodSource
    void longScalar(String text, Exp<?> expected) {
        Exp<?> exp = parseExp(text);
        assertInstanceOf(LongScalarExp.class, exp);
        assertEquals(expected, exp);
    }

    static Stream<Arguments> longScalar() {
        return Stream.of(
                arguments("999999999999999", $val(999999999999999L)),
                arguments("999999999999999l", $val(999999999999999L)),
                arguments("999999999999999L", $val(999999999999999L)),
                arguments("-999999999999999", $val(-999999999999999L)),
                arguments("+999999999999999", $val(999999999999999L)),
                arguments("999_999_987_654_321", $val(999_999_987_654_321L)),
                arguments("0x999999999999A3F", $val(0x999999999999A3FL)),
                arguments("-0xFFFFFFFFFFF", $val(-0xFFFFFFFFFFFL)),
                arguments("0123123123123123", $val(123123123123123L)),
                arguments("-0123123123123123", $val(-123123123123123L)),
                arguments("9223372036854775807", $val(Long.MAX_VALUE)),
                arguments("-9223372036854775808", $val(Long.MIN_VALUE))
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
        assertThrows(QLParserException.class, () -> parseExp(text));
    }

    @ParameterizedTest
    @MethodSource
    void bigintScalar(String text, Exp<?> expected) {
        Exp<?> exp = parseExp(text);
        assertInstanceOf(NumExp.class, exp);
        assertEquals(expected, exp);
    }

    static Stream<Arguments> bigintScalar() {
        return Stream.of(
                arguments("999999999999999999999", $val(new BigInteger("999999999999999999999"))),
                arguments("9h", $val(new BigInteger("9"))),
                arguments("999999999999999999999h", $val(new BigInteger("999999999999999999999"))),
                arguments("999999999999999999999H", $val(new BigInteger("999999999999999999999"))),
                arguments("-999999999999999999999", $val(new BigInteger("-999999999999999999999"))),
                arguments("+999999999999999999999", $val(new BigInteger("999999999999999999999"))),
                arguments("999_999_999_999_987_654_321", $val(new BigInteger("999999999999987654321"))),
                arguments("0x999999999999999999A3F", $val(new BigInteger("11605687868300440077179455"))),
                arguments("-0xFFFFFFFFFFFFFFFFF", $val(new BigInteger("-295147905179352825855"))),
                arguments("0123123123123123123123123", $val(new BigInteger("123123123123123123123123"))),
                arguments("-0123123123123123123123123", $val(new BigInteger("-123123123123123123123123")))
        );
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "99999999999999999999abc",
            "0xFFFFFFFFFFFFFFFFFFFGHIJ",
            "0xFFFFFFFFFFFFFFFFFF1A3.5",
    })
    void bigintScalar_throws(String text) {
        assertThrows(QLParserException.class, () -> parseExp(text));
    }

    @ParameterizedTest
    @MethodSource
    void floatScalar(String text, Exp<?> expected) {
        Exp<?> exp = parseExp(text);
        assertInstanceOf(FloatScalarExp.class, exp);
        assertEquals(expected, exp);
    }

    static Stream<Arguments> floatScalar() {
        return Stream.of(
                arguments("1f", $floatVal(1f)),
                arguments("123.4", $floatVal(123.4f)),
                arguments("123.4f", $floatVal(123.4f)),
                arguments("123.4F", $floatVal(123.4f)),
                arguments("-456.7", $floatVal(-456.7f)),
                arguments("123_456.7", $floatVal(123_456.7f)),
                arguments("+0.0", $floatVal(0.0f)),
                arguments("0.0", $floatVal(0.0f)),
                arguments(".5", $floatVal(.5f)),
                arguments("-.5", $floatVal(-.5f)),
                arguments("+.0", $floatVal(.0f)),
                arguments("123.4000", $floatVal(123.4f)),
                arguments("0.0000", $floatVal(0.0f)),
                arguments("1e10", $floatVal(1e10f)),
                arguments("1E10", $floatVal(1e10f)),
                arguments("-1e-10", $floatVal(-1e-10f)),
                arguments("+1.23e+4", $floatVal(1.23e4f)),
                arguments("9.999999E37", $floatVal(9.999999E37f)),
                arguments("-9.999999E37", $floatVal(-9.999999E37f)),
                arguments("1E-37", $floatVal(1E-37f)),
                arguments("-1E-37", $floatVal(-1E-37f)),
                arguments("0x1.0p-12f", $floatVal(0x1.0p-12f)),
                arguments("-0x1.0p-12f", $floatVal(-0x1.0p-12f)),
                arguments("0x1.fffffeP+12f", $floatVal(0x1.fffffeP+12f)),
                arguments("-0x1.fffffep+12f", $floatVal(-0x1.fffffeP+12f))
        );
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "1e",
            "1e1.0",
            ".e10",
    })
    void floatScalar_throws(String text) {
        assertThrows(QLParserException.class, () -> parseExp(text));
    }

    @ParameterizedTest
    @MethodSource
    void doubleScalar(String text, Exp<?> expected) {
        Exp<?> exp = parseExp(text);
        assertInstanceOf(DoubleScalarExp.class, exp);
        assertEquals(expected, exp);
    }

    static Stream<Arguments> doubleScalar() {
        return Stream.of(
                arguments("1d", $doubleVal(1)),
                arguments("123.45e50", $doubleVal(123.45e50)),
                arguments("123.45e50d", $doubleVal(123.45e50)),
                arguments("123.45e50D", $doubleVal(123.45e50)),
                arguments("-456.78e50", $doubleVal(-456.78e50)),
                arguments("123_456.78_9e50", $doubleVal(123_456.78_9e50)),
                arguments(".5e50", $doubleVal(.5e50)),
                arguments("-.5e50", $doubleVal(-.5e50)),
                arguments("123.45000e50", $doubleVal(123.45e50)),
                arguments("1e50", $doubleVal(1e50)),
                arguments("1E50", $doubleVal(1E50)),
                arguments("-1e-50", $doubleVal(-1e-50)),
                arguments("9.999999999999999E307", $doubleVal(9.999999999999999E307)),
                arguments("-9.999999999999999E307", $doubleVal(-9.999999999999999E307)),
                arguments("1E-307", $doubleVal(1E-307)),
                arguments("-1E-307", $doubleVal(-1E-307)),
                arguments("0x1.0p-122d", $doubleVal(0x1.0p-122)),
                arguments("-0x1.0p-122d", $doubleVal(-0x1.0p-122)),
                arguments("0x1.fffffeP+123d", $doubleVal(0x1.fffffeP+123)),
                arguments("-0x1.fffffep+123d", $doubleVal(-0x1.fffffeP+123))
        );
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "1ed",
            "1e1.0d",
            ".e10d",
    })
    void doubleScalar_throws(String text) {
        assertThrows(QLParserException.class, () -> parseExp(text));
    }

    @ParameterizedTest
    @MethodSource
    void decimalScalar(String text, Exp<?> expected) {
        Exp<?> exp = parseExp(text);
        assertInstanceOf(DecimalScalarExp.class, exp);
        assertEquals(expected, exp);
    }

    static Stream<Arguments> decimalScalar() {
        return Stream.of(
                arguments("1m", $decimalVal(new BigDecimal(1))),
                arguments("123.45e1000m", $decimalVal(new BigDecimal("123.45e1000"))),
                arguments("123.45e1000M", $decimalVal(new BigDecimal("123.45e1000"))),
                arguments("-456.78e1000", $decimalVal(new BigDecimal("-456.78e1000"))),
                arguments("123_456.78_9e1000", $decimalVal(new BigDecimal("123456.789e1000"))),
                arguments(".5e1000", $decimalVal(new BigDecimal(".5e1000"))),
                arguments("-.5e1000", $decimalVal(new BigDecimal("-.5e1000"))),
                arguments("123.45000e1000", $decimalVal(new BigDecimal("123.45e1000"))),
                arguments("1e1000", $decimalVal(new BigDecimal("1e1000"))),
                arguments("1E1000", $decimalVal(new BigDecimal("1E1000"))),
                arguments("-1e-1000", $decimalVal(new BigDecimal("-1e-1000"))),
                arguments("0x1.0p-1044", $decimalVal(new BigDecimal("5.30498947741318078777846E-315"))),
                arguments("-0x1.0p-1044", $decimalVal(new BigDecimal("-5.30498947741318078777846E-315")))
        );
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "1em",
            "1e1.0m",
            ".e10m",
    })
    void decimalScalar_throws(String text) {
        assertThrows(QLParserException.class, () -> parseExp(text));
    }

    @ParameterizedTest
    @MethodSource
    void column(String text, Exp<?> expected) {
        Exp<?> exp = parseExp(text);
        assertInstanceOf(NumExp.class, exp);
        assertEquals(expected, exp);
    }

    static Stream<Arguments> column() {
        return Stream.of(
                arguments("int(a)", $int("a")),
                arguments("long(a)", $long("a")),
                arguments("float(a)", $float("a")),
                arguments("double(a)", $double("a")),
                arguments("decimal(a)", $decimal("a")),
                arguments("int(`a`)", $int("a")),
                arguments("int(1)", $int(1))
        );
    }

    @ParameterizedTest
    @MethodSource
    void cast(String text, Exp<?> expected) {
        Exp<?> exp = parseExp(text);
        assertInstanceOf(NumExp.class, exp);
        assertEquals(expected, exp);
    }

    static Stream<Arguments> cast() {
        return Stream.of(
                arguments("castAsInt(1)", $intVal(1).castAsInt()),
                arguments("castAsLong(9999999999)", $longVal(9999999999L).castAsLong()),
                arguments("castAsFloat(1.1)", $floatVal(1.1f).castAsFloat()),
                arguments("castAsDouble(1e-100)", $doubleVal(1e-100).castAsDouble()),
                arguments("castAsBigint(1" + "0".repeat(20) + ")",
                        $bigintVal(new BigInteger("1" + "0".repeat(20))).castAsBigint()),
                arguments("castAsDecimal(1e1000)", $decimalVal(new BigDecimal("1e1000")).castAsDecimal()),
                arguments("castAsInt(null)", $val(null).castAsInt()),
                arguments("castAsInt(true)", $boolVal(true).castAsInt()),
                arguments("castAsInt('1')", $strVal("1").castAsInt())
        );
    }

    @ParameterizedTest
    @MethodSource
    void relation(String text, Exp<?> expected) {
        Exp<?> exp = parseExp(text);
        assertInstanceOf(Condition.class, exp);
        assertEquals(expected, exp);
    }

    static Stream<Arguments> relation() {
        return Stream.of(
                arguments("5 > 3", $intVal(5).gt($val(3))),
                arguments("5 >= 3", $intVal(5).ge($val(3))),
                arguments("5 < 3", $intVal(5).lt($val(3))),
                arguments("5 <= 3", $intVal(5).le($val(3))),
                arguments("5 = 5", $intVal(5).eq($val(5))),
                arguments("5 != 3", $intVal(5).ne($val(3))),
                arguments("5 between 3 and 7", $intVal(5).between($val(3), $val(7))),
                arguments("5 not between 3 and 7", $intVal(5).notBetween($val(3), $val(7))),
                arguments("int(1) in (1)", $int(1).in(1)),
                arguments("int(1) not in (1)", $int(1).notIn(1)),
                arguments("int(1) in (1.0)", $int(1).in(1)),
                arguments("int(1) in (1, 2, 3)", $int(1).in(1, 2, 3)),
                arguments("int(1) not in (1, 2, 3)", $int(1).notIn(1, 2, 3)),
                arguments("int(1) in (1.0, 2.0, 3.0)", $int(1).in(1.0, 2.0, 3.0)),
                arguments("int(1) > 10", $int(1).gt($val(10))),
                arguments("abs(int(1)) > 10", $int(1).abs().gt($val(10))),
                arguments("avg(int(1)) <= 20", $int(1).avg().le($val(20))),
                arguments("int(1) + int(2) = 15", $int(1).add($int(2)).eq($val(15))),
                arguments("(int(1) % int(2)) = 15", $int(1).mod($int(2)).eq($val(15)))
        );
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "5 > '3'",
            "int(col1) = null",
            "int(col1) != false",
            "int(1) in ()",
            "int(1) in (1 2)",
            "int(1) in ('a', 'b', 'c')",
            "int(1) not in ()",
            "int(1) not in (1 2)",
            "int(1) not in ('a', 'b', 'c')",
    })
    void relation_throws(String text) {
        assertThrows(QLParserException.class, () -> parseExp(text));
    }

    @ParameterizedTest
    @MethodSource
    void function(String text, Exp<?> expected) {
        Exp<?> exp = parseExp(text);
        assertInstanceOf(NumExp.class, exp);
        assertEquals(expected, exp);
    }

    static Stream<Arguments> function() {
        return Stream.of(
                arguments("count()", count()),
                arguments("count( )", count()),
                arguments("count(int(1) > 0)", count($int(1).lt(0))),
                arguments("count(int(a) > 0)", count($int("a").lt(0))),
                arguments("rowNum()", rowNum()),
                arguments("rowNum( )", rowNum()),
                arguments("abs(-5)", $intVal(-5).abs()),
                arguments("round(3.14)", $floatVal(3.14f).round()),
                arguments("scale(decimal(1), 3)", $decimal(1).scale(3)),
                arguments("scale(double(1), 3)", $double(1).castAsDecimal().scale(3)),
                arguments("len(str(a))", $str("a").len())
        );
    }

    @ParameterizedTest
    @MethodSource
    void aggregate(String text, Exp<?> expected) {
        Exp<?> exp = parseExp(text);
        assertInstanceOf(NumExp.class, exp);
        assertEquals(expected, exp);
    }

    static Stream<Arguments> aggregate() {
        return Stream.of(
                arguments("sum(int(1))", $int(1).sum()),
                arguments("cumSum(int(1))", $int(1).cumSum()),
                arguments("min(int(1))", $int(1).min()),
                arguments("max(int(1))", $int(1).max()),
                arguments("avg(int(1))", $int(1).avg()),
                arguments("median(int(1))", $int(1).median()),
                arguments("quantile(int(1), 0.5)", $int(1).quantile(0.5)),
                arguments("sum(int(1), int(1) > 0)", $int(1).sum($int(1).gt(1))),
                arguments("min(int(1), int(1) > 0)", $int(1).min($int(1).gt(1))),
                arguments("max(int(1), int(1) > 0)", $int(1).max($int(1).gt(1))),
                arguments("avg(int(1), int(1) > 0)", $int(1).avg($int(1).gt(1))),
                arguments("median(int(1), int(1) > 0)", $int(1).median($int(1).gt(1))),
                arguments("quantile(int(1), 0.5, int(1) > 0)", $int(1).quantile(0.5, $int(1).gt(1))),
                arguments("sum(int(1), int(2) > 0)", $int(1).sum($int(2).gt(1)))
        );
    }
}
