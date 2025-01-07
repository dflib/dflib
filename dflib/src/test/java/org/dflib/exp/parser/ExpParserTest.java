package org.dflib.exp.parser;

import org.antlr.v4.runtime.misc.ParseCancellationException;
import org.dflib.Exp;
import org.dflib.exp.parser.antlr4.LexerCancellationException;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.params.provider.Arguments.arguments;

class ExpParserTest {

    @ParameterizedTest
    @MethodSource
    void numExp(String text, Exp<?> expected) {
        Exp<?> exp = Exp.exp(text);
        assertEquals(expected, exp);
    }

    static Stream<Arguments> numExp() {
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
    void numExp_parsingError(String text) {
        assertThrows(ParseCancellationException.class, () -> Exp.exp(text));
    }

    @ParameterizedTest
    @MethodSource
    void boolExp(String text, Exp<?> expected) {
        Exp<?> exp = Exp.exp(text);
        assertEquals(expected, exp);
    }

    static Stream<Arguments> boolExp() {
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
    void boolExp_parsingError(String text) {
        assertThrows(ParseCancellationException.class, () -> Exp.exp(text));
    }

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
    void integerScalar(String text, Exp<?> expected) {
        Exp<?> exp = Exp.exp(text);
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
    void boolScalar(String text, Exp<?> expected) {
        Exp<?> exp = Exp.exp(text);
        assertEquals(expected, exp);
    }

    static Stream<Arguments> boolScalar() {
        return Stream.of(
                arguments("true", Exp.$val(true)),
                arguments("false", Exp.$val(false))
        );
    }

    @ParameterizedTest
    @ValueSource(strings = {"TRUE", "FALSE", "True", "False"})
    void boolScalar_parsingError(String text) {
        assertThrows(ParseCancellationException.class, () -> Exp.exp(text));
    }

    @ParameterizedTest
    @MethodSource
    void strScalar(String text, Exp<?> expected) {
        Exp<?> exp = Exp.exp(text);
        assertEquals(expected, exp);
    }

    static Stream<Arguments> strScalar() {
        return Stream.of(
                arguments("'single quotes'", Exp.$val("single quotes")),
                arguments("\"double quotes\"", Exp.$val("double quotes")),
                arguments("\"unicode \\u1234\"", Exp.$val("unicode ሴ")),
                arguments("\"escaped \\\"quote\\\"\"", Exp.$val("escaped \"quote\"")),
                arguments("\"newline\\nnew\"", Exp.$val("newline\nnew")),
                arguments("\"\"", Exp.$val(""))
        );
    }

    @ParameterizedTest
    @ValueSource(strings = {"\"missing \" escape\""})
    void strScalar_parsingError(String text) {
        assertThrows(ParseCancellationException.class, () -> Exp.exp(text));
    }

    @ParameterizedTest
    @ValueSource(strings = {"\"missing quote", "'mismatched quotes\"", "\"multi\nline\""})
    void strScalar_lexicalError(String text) {
        assertThrows(LexerCancellationException.class, () -> Exp.exp(text));
    }

    @ParameterizedTest
    @MethodSource
    void column(String text, Exp<?> expected) {
        Exp<?> exp = Exp.exp(text);
        assertEquals(expected, exp);
    }

    static Stream<Arguments> column() {
        return Stream.of(
                arguments("bool(a)", Exp.$bool("a")),
                arguments("int(a)", Exp.$int("a")),
                arguments("long(a)", Exp.$long("a")),
                arguments("float(a)", Exp.$float("a")),
                arguments("double(a)", Exp.$double("a")),
                arguments("decimal(a)", Exp.$decimal("a")),
                arguments("str(a)", Exp.$str("a")),
                arguments("date(a)", Exp.$date("a")),
                arguments("time(a)", Exp.$time("a")),
                arguments("dateTime(a)", Exp.$dateTime("a")),
                arguments("int('a')", Exp.$int("a")),
                arguments("int(\"a\")", Exp.$int("a")),
                arguments("int(1)", Exp.$int(1))
        );
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "BOOL(a)",
            "int(1.1)",
            "long(null)",
            "double(true)",
            "decimal(int(1))",
    })
    void column_parsingError(String text) {
        assertThrows(ParseCancellationException.class, () -> Exp.exp(text));
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "float(-1)",
    })
    void column_apiError(String text) {
        assertThrows(IllegalArgumentException.class, () -> Exp.exp(text));
    }

    @ParameterizedTest
    @MethodSource
    void numRelation(String text, Exp<?> expected) {
        Exp<?> exp = Exp.exp(text);
        assertEquals(expected, exp);
    }

    static Stream<Arguments> numRelation() {
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
    void numRelation_parsingError(String text) {
        assertThrows(ParseCancellationException.class, () -> Exp.exp(text));
    }

    @ParameterizedTest
    @MethodSource
    void strRelation(String text, Exp<?> expected) {
        Exp<?> exp = Exp.exp(text);
        assertEquals(expected, exp);
    }

    static Stream<Arguments> strRelation() {
        return Stream.of(
                arguments("'hello' = 'hello'", Exp.$val("hello").eq(Exp.$val("hello"))),
                arguments("'hello' != 'world'", Exp.$val("hello").ne(Exp.$val("world"))),
                arguments("str(1) = 'test'", Exp.$str(1).eq(Exp.$val("test"))),
                arguments("trim(str(1)) = 'test'", Exp.$str(1).trim().eq(Exp.$val("test")))
        );
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "'hello' > 'world'",
            "'hello' >= 'world'",
            "'hello' < 'world'",
            "'hello' <= 'world'",
            "'hello' between 'a' and 'z'",
            "len(str(col1)) = '4'",
    })
    void strRelation_parsingError(String text) {
        assertThrows(ParseCancellationException.class, () -> Exp.exp(text));
    }

    @ParameterizedTest
    @MethodSource
    void timeRelation(String text, Exp<?> expected) {
        Exp<?> exp = Exp.exp(text);
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
                arguments("castAsTime('12:00:00') = time(2)", Exp.$val("12:00:00").castAsTime().eq(Exp.$time(2))),
                arguments("time(1) = plusHours(time(2), 1)", Exp.$time(1).eq(Exp.$time(2).plusHours(1)))
        );
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "time(1) = '11:00:00'",
            "time(1) = dateTime(2)",
            "time(1) = true",
            "time(1) = null",
    })
    void timeRelation_parsingError(String text) {
        assertThrows(ParseCancellationException.class, () -> Exp.exp(text));
    }

    @ParameterizedTest
    @MethodSource
    void dateRelation(String text, Exp<?> expected) {
        Exp<?> exp = Exp.exp(text);
        assertEquals(expected, exp);
    }

    static Stream<Arguments> dateRelation() {
        return Stream.of(
                arguments("date(1) > date(2)", Exp.$date(1).gt(Exp.$date(2))),
                arguments("date(1) >= date(2)", Exp.$date(1).ge(Exp.$date(2))),
                arguments("date(1) < date(2)", Exp.$date(1).lt(Exp.$date(2))),
                arguments("date(1) <= date(2)", Exp.$date(1).le(Exp.$date(2))),
                arguments("date(1) = date(2)", Exp.$date(1).eq(Exp.$date(2))),
                arguments("date(1) != date(2)", Exp.$date(1).ne(Exp.$date(2))),
                arguments("date(1) between date(2) and date(3)",
                        Exp.$date(1).between(Exp.$date(2), Exp.$date(3))),
                arguments("castAsDate('1970-01-01') = date(2)",
                        Exp.$val("1970-01-01").castAsDate().eq(Exp.$date(2))),
                arguments("date(1) = plusDays(date(2), 1)",
                        Exp.$date(1).eq(Exp.$date(2).plusDays(1)))
        );
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "date(1) = '1970-01-01'",
            "date(1) = dateTime(2)",
            "date(1) = true",
            "date(1) = null",
    })
    void dateRelation_parsingError(String text) {
        assertThrows(ParseCancellationException.class, () -> Exp.exp(text));
    }

    @ParameterizedTest
    @MethodSource
    void dateTimeRelation(String text, Exp<?> expected) {
        Exp<?> exp = Exp.exp(text);
        assertEquals(expected, exp);
    }

    static Stream<Arguments> dateTimeRelation() {
        return Stream.of(
                arguments("dateTime(1) > dateTime(2)", Exp.$dateTime(1).gt(Exp.$dateTime(2))),
                arguments("dateTime(1) >= dateTime(2)", Exp.$dateTime(1).ge(Exp.$dateTime(2))),
                arguments("dateTime(1) < dateTime(2)", Exp.$dateTime(1).lt(Exp.$dateTime(2))),
                arguments("dateTime(1) <= dateTime(2)", Exp.$dateTime(1).le(Exp.$dateTime(2))),
                arguments("dateTime(1) = dateTime(2)", Exp.$dateTime(1).eq(Exp.$dateTime(2))),
                arguments("dateTime(1) != dateTime(2)", Exp.$dateTime(1).ne(Exp.$dateTime(2))),
                arguments("dateTime(1) between dateTime(2) and dateTime(3)",
                        Exp.$dateTime(1).between(Exp.$dateTime(2), Exp.$dateTime(3))),
                arguments("castAsDateTime('1970-01-01T12:00:00Z') = dateTime(2)",
                        Exp.$val("1970-01-01T12:00:00Z").castAsDateTime().eq(Exp.$dateTime(2))),
                arguments("dateTime(1) = plusDays(dateTime(2), 1)",
                        Exp.$dateTime(1).eq(Exp.$dateTime(2).plusDays(1)))
        );
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "dateTime(1) = '1970-01-01T12:00:00Z'",
            "dateTime(1) = time(2)",
            "dateTime(1) = date(2)",
            "dateTime(1) = true",
            "dateTime(1) = null",
    })
    void dateTimeRelation_parsingError(String text) {
        assertThrows(ParseCancellationException.class, () -> Exp.exp(text));
    }

    @ParameterizedTest
    @MethodSource
    void numFn(String text, Exp<?> expected) {
        Exp<?> exp = Exp.exp(text);
        assertEquals(expected, exp);
    }

    static Stream<Arguments> numFn() {
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
    void numFn_parsingError(String text) {
        assertThrows(ParseCancellationException.class, () -> Exp.exp(text));
    }

    @ParameterizedTest
    @MethodSource
    void timeFieldFn(String text, Exp<?> expected) {
        Exp<?> exp = Exp.exp(text);
        assertEquals(expected, exp);
    }

    static Stream<Arguments> timeFieldFn() {
        return Stream.of(
                arguments("hour(time(1))", Exp.$time(1).hour()),
                arguments("minute(time(1))", Exp.$time(1).minute()),
                arguments("second(time(1))", Exp.$time(1).second()),
                arguments("millisecond(time(1))", Exp.$time(1).millisecond()),
                arguments("hour(castAsTime('12:34:56'))", Exp.$val("12:34:56").castAsTime().hour())
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
    void timeFieldFn_parsingError(String text) {
        assertThrows(ParseCancellationException.class, () -> Exp.exp(text));
    }

    @ParameterizedTest
    @MethodSource
    void dateFieldFn(String text, Exp<?> expected) {
        Exp<?> exp = Exp.exp(text);
        assertEquals(expected, exp);
    }

    static Stream<Arguments> dateFieldFn() {
        return Stream.of(
                arguments("year(date(1))", Exp.$date(1).year()),
                arguments("month(date(1))", Exp.$date(1).month()),
                arguments("day(date(1))", Exp.$date(1).day()),
                arguments("year(castAsDate('1970-01-01'))", Exp.$val("1970-01-01").castAsDate().year())
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
    void dateFieldFn_parsingError(String text) {
        assertThrows(ParseCancellationException.class, () -> Exp.exp(text));
    }

    @ParameterizedTest
    @MethodSource
    void dateTimeFieldFn(String text, Exp<?> expected) {
        Exp<?> exp = Exp.exp(text);
        assertEquals(expected, exp);
    }

    static Stream<Arguments> dateTimeFieldFn() {
        return Stream.of(
                arguments("year(dateTime(1))", Exp.$dateTime(1).year()),
                arguments("month(dateTime(1))", Exp.$dateTime(1).month()),
                arguments("day(dateTime(1))", Exp.$dateTime(1).day()),
                arguments("hour(dateTime(1))", Exp.$dateTime(1).hour()),
                arguments("minute(dateTime(1))", Exp.$dateTime(1).minute()),
                arguments("second(dateTime(1))", Exp.$dateTime(1).second()),
                arguments("millisecond(dateTime(1))", Exp.$dateTime(1).millisecond()),
                arguments("year(castAsDateTime('1970-01-01T12:00:00Z'))",
                        Exp.$val("1970-01-01T12:00:00Z").castAsDateTime().year())
        );
    }

    @ParameterizedTest
    @ValueSource(strings = {"year('1970-01-01T12:34:56Z')", "day(castAsDateTime('1970-01-01T12:00:00Z'), 2)"})
    void dateTimeFieldFn_parsingError(String text) {
        assertThrows(ParseCancellationException.class, () -> Exp.exp(text));
    }

    @ParameterizedTest
    @MethodSource
    void offsetDateTimeFieldFn(String text, Exp<?> expected) {
        Exp<?> exp = Exp.exp(text);
        assertEquals(expected, exp);
    }

    static Stream<Arguments> offsetDateTimeFieldFn() {
        return Stream.of(
                arguments("year(offsetDateTime(1))", Exp.$offsetDateTime(1).year()),
                arguments("month(offsetDateTime(1))", Exp.$offsetDateTime(1).month()),
                arguments("day(offsetDateTime(1))", Exp.$offsetDateTime(1).day()),
                arguments("hour(offsetDateTime(1))", Exp.$offsetDateTime(1).hour()),
                arguments("minute(offsetDateTime(1))", Exp.$offsetDateTime(1).minute()),
                arguments("second(offsetDateTime(1))", Exp.$offsetDateTime(1).second()),
                arguments("millisecond(offsetDateTime(1))", Exp.$offsetDateTime(1).millisecond()),
                arguments("year(castAsOffsetDateTime('1970-01-01T12:00:00Z+01:00'))",
                        Exp.$val("1970-01-01T12:00:00Z+01:00").castAsOffsetDateTime().year())
        );
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "year('1970-01-01T12:34:56Z+01:00')",
            "day(castAsOffsetDateTime('1970-01-01T12:00:00Z+01:00'), 2)"
    })
    void offsetDateTimeFieldFn_parsingError(String text) {
        assertThrows(ParseCancellationException.class, () -> Exp.exp(text));
    }

    @ParameterizedTest
    @MethodSource
    void boolFn(String text, Exp<?> expected) {
        Exp<?> exp = Exp.exp(text);
        assertEquals(expected, exp);
    }

    static Stream<Arguments> boolFn() {
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
    void boolFn_parsingError(String text) {
        assertThrows(ParseCancellationException.class, () -> Exp.exp(text));
    }

    @ParameterizedTest
    @MethodSource
    void timeFn(String text, Exp<?> expected) {
        Exp<?> exp = Exp.exp(text);
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
    void timeFn_parsingError(String text) {
        assertThrows(ParseCancellationException.class, () -> Exp.exp(text));
    }

    @ParameterizedTest
    @MethodSource
    void dateFn(String text, Exp<?> expected) {
        Exp<?> exp = Exp.exp(text);
        assertEquals(expected, exp);
    }

    static Stream<Arguments> dateFn() {
        return Stream.of(
                arguments("plusYears(date(1), 2)", Exp.$date(1).plusYears(2)),
                arguments("plusMonths(date(1), 6)", Exp.$date(1).plusMonths(6)),
                arguments("plusWeeks(date(1), 3)", Exp.$date(1).plusWeeks(3)),
                arguments("plusDays(date(1), 10)", Exp.$date(1).plusDays(10)),
                arguments("plusYears(date(1), -2)", Exp.$date(1).plusYears(-2)),
                arguments("plusYears(castAsDate('1970-01-01'), 2)", Exp.$val("1970-01-01").castAsDate().plusYears(2))
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
    void dateFn_parsingError(String text) {
        assertThrows(ParseCancellationException.class, () -> Exp.exp(text));
    }

    @ParameterizedTest
    @MethodSource
    void dateTimeFn(String text, Exp<?> expected) {
        Exp<?> exp = Exp.exp(text);
        assertEquals(expected, exp);
    }

    static Stream<Arguments> dateTimeFn() {
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
                arguments("plusYears(dateTime(1), -2)", Exp.$dateTime(1).plusYears(-2)),
                arguments("plusYears(castAsDateTime('1970-01-01T12:00:00Z'), 2)",
                        Exp.$val("1970-01-01T12:00:00Z").castAsDateTime().plusYears(2))
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
    void dateTimeFn_parsingError(String text) {
        assertThrows(ParseCancellationException.class, () -> Exp.exp(text));
    }

    @ParameterizedTest
    @MethodSource
    void offsetDateTimeFn(String text, Exp<?> expected) {
        Exp<?> exp = Exp.exp(text);
        assertEquals(expected, exp);
    }

    static Stream<Arguments> offsetDateTimeFn() {
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
                arguments("plusYears(offsetDateTime(1), -2)", Exp.$offsetDateTime(1).plusYears(-2)),
                arguments("plusYears(castAsOffsetDateTime('1970-01-01T12:00:00Z+01:00'), 2)",
                        Exp.$val("1970-01-01T12:00:00Z+01:00").castAsOffsetDateTime().plusYears(2))
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
    void offsetDateTimeFn_parsingError(String text) {
        assertThrows(ParseCancellationException.class, () -> Exp.exp(text));
    }

    @ParameterizedTest
    @MethodSource
    void strFn(String text, Exp<?> expected) {
        Exp<?> exp = Exp.exp(text);
        assertEquals(expected, exp);
    }

    static Stream<Arguments> strFn() {
        return Stream.of(
                arguments("concat(str(1), 'example')", Exp.concat(Exp.$str(1), Exp.$strVal("example"))),
                arguments("concat(str(1))", Exp.concat(Exp.$str(1))),
                arguments("concat( )", Exp.concat()),
                arguments("concat()", Exp.concat()),
                arguments("trim('  example  ')", Exp.$strVal("  example  ").trim()),
                arguments("substr('example', 2)", Exp.$strVal("example").substr(2)),
                arguments("substr('example', 2, 3)", Exp.$strVal("example").substr(2, 3)),
                arguments("substr('example', -2, 3)", Exp.$strVal("example").substr(-2, 3)),
                arguments("trim(str(1))", Exp.$str(1).trim()),
                arguments("trim(castAsStr(3))", Exp.$intVal(3).castAsStr().trim())
        );
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "trim()",
            "trim(  abc  )",
            "substr('example')",
            "substr('example', 1 + 2)",
            "substr('example', 2, null)",
    })
    void strFn_parsingError(String text) {
        assertThrows(ParseCancellationException.class, () -> Exp.exp(text));
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "substr('example', 2, -1)",
    })
    void strFn_apiError(String text) {
        assertThrows(IllegalArgumentException.class, () -> Exp.exp(text));
    }

    @ParameterizedTest
    @MethodSource
    void cast(String text, Exp<?> expected) {
        Exp<?> exp = Exp.exp(text);
        assertEquals(expected, exp);
    }

    static Stream<Arguments> cast() {
        return Stream.of(
                arguments("castAsInt(1)", Exp.$val(1).castAsInt()),
                arguments("castAsLong(1)", Exp.$val(1).castAsLong()),
                arguments("castAsFloat(1)", Exp.$val(1).castAsFloat()),
                arguments("castAsDouble(1)", Exp.$val(1).castAsDouble()),
                arguments("castAsDecimal(1)", Exp.$val(1).castAsDecimal()),
                arguments("castAsStr(1)", Exp.$val(1).castAsStr()),
                arguments("castAsTime('12:00:00')", Exp.$val("12:00:00").castAsTime()),
                arguments("castAsDate('1970-01-01')", Exp.$val("1970-01-01").castAsDate()),
                arguments("castAsDateTime('1970-01-01T12:00:00Z')",
                        Exp.$val("1970-01-01T12:00:00Z").castAsDateTime()),
                arguments("castAsOffsetDateTime('1970-01-01T12:00:00Z+01:00')",
                        Exp.$val("1970-01-01T12:00:00Z+01:00").castAsOffsetDateTime()),
                arguments("castAsStr(null)", Exp.$val(null).castAsStr()),
                arguments("castAsStr(1 + 2)", Exp.$intVal(1).add(2).castAsStr()),
                arguments("castAsStr(true)", Exp.$val(true).castAsStr()),
                arguments("castAsStr(time(1))", Exp.$time(1).castAsStr()),
                arguments("castAsStr(sum(int(1)))", Exp.$int(1).sum().castAsStr())
        );
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "CASTASINT(1)",
            "CAST_AS_INT(1)",
    })
    void cast_parsingError(String text) {
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
            "if(true, 1, null)",
            "if(true, 1, 'other')",
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
                arguments("ifNull(date(1), castAsDate('1970-01-01'))",
                        Exp.ifNull(Exp.$date(1), Exp.$val("1970-01-01").castAsDate()))
        );
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "IFNULL($int(1), 0)",
            "ifNull($int(1))",
            "ifNull($int(1), )",
            "ifNull(, $int(1))",
            "ifNull($int(1), $str('name'))",
            "ifNull(int(1), ifNull(int(2), 0))",
            "ifNull(int(1), null)",
    })
    void ifNull_parsingError(String text) {
        assertThrows(ParseCancellationException.class, () -> Exp.exp(text));
    }

    @ParameterizedTest
    @MethodSource
    void split(String text, Exp<?> expected) {
        Exp<?> exp = Exp.exp(text);
        assertEquals(expected, exp);
    }

    static Stream<Arguments> split() {
        return Stream.of(
                arguments("split('a,b,c', ',')", Exp.$strVal("a,b,c").split(",")),
                arguments("split('a,b,c', ',', 2)", Exp.$strVal("a,b,c").split(",", 2)),
                arguments("split(str(1), '|')", Exp.$str(1).split("|")),
                arguments("split(trim(' a|b|c '), '|', 2)", Exp.$strVal(" a|b|c ").trim().split("|", 2))
        );
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "SPLIT('a,b,c', ',')",
            "split('a,b,c')",
            "split('a,b,c', )",
            "split(, ',')",
            "split(time(1), ':')",
    })
    void split_parsingError(String text) {
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
    void positionalAgg(String text, Exp<?> expected) {
        Exp<?> exp = Exp.exp(text);
        assertEquals(expected, exp);
    }

    static Stream<Arguments> positionalAgg() {
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
    void positionalAgg_parsingError(String text) {
        assertThrows(ParseCancellationException.class, () -> Exp.exp(text));
    }

    @ParameterizedTest
    @MethodSource
    void numAgg(String text, Exp<?> expected) {
        Exp<?> exp = Exp.exp(text);
        assertEquals(expected, exp);
    }

    static Stream<Arguments> numAgg() {
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
    void numAgg_parsingError(String text) {
        assertThrows(ParseCancellationException.class, () -> Exp.exp(text));
    }

    @ParameterizedTest
    @MethodSource
    void strAgg(String text, Exp<?> expected) {
        Exp<?> exp = Exp.exp(text);
        assertEquals(expected, exp);
    }

    static Stream<Arguments> strAgg() {
        return Stream.of(
                arguments("min(str(1))", Exp.$str(1).min()),
                arguments("max(str(1))", Exp.$str(1).max()),
                arguments("min(str(1), matches(str(1), '#.*'))", Exp.$str(1).min(Exp.$str(1).matches("#.*"))),
                arguments("max(str(1), matches(str(1), '#.*'))", Exp.$str(1).max(Exp.$str(1).matches("#.*"))),
                arguments("min(str(1), matches(str(2), '#.*'))", Exp.$str(1).min(Exp.$str(2).matches("#.*")))
        );
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "MIN(str(1))",
            "max()",
            "min(str(1), )",
            "max(str(1), 0)",
    })
    void strAgg_parsingError(String text) {
        assertThrows(ParseCancellationException.class, () -> Exp.exp(text));
    }

    @ParameterizedTest
    @MethodSource
    void timeAgg(String text, Exp<?> expected) {
        Exp<?> exp = Exp.exp(text);
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
    void timeAgg_parsingError(String text) {
        assertThrows(ParseCancellationException.class, () -> Exp.exp(text));
    }

    @ParameterizedTest
    @MethodSource
    void dateAgg(String text, Exp<?> expected) {
        Exp<?> exp = Exp.exp(text);
        assertEquals(expected, exp);
    }

    static Stream<Arguments> dateAgg() {
        return Stream.of(
                arguments("min(date(1))", Exp.$date(1).min()),
                arguments("max(date(1))", Exp.$date(1).max()),
                arguments("avg(date(1))", Exp.$date(1).avg()),
                arguments("median(date(1))", Exp.$date(1).median()),
                arguments("quantile(date(1), 0.5)", Exp.$date(1).quantile(0.5)),
                arguments("min(date(1), date(1) > date(2))", Exp.$date(1).min(Exp.$date(1).gt(Exp.$date(2)))),
                arguments("max(date(1), date(1) > date(2))", Exp.$date(1).max(Exp.$date(1).gt(Exp.$date(2)))),
                arguments("avg(date(1), date(1) > date(2))", Exp.$date(1).avg(Exp.$date(1).gt(Exp.$date(2)))),
                arguments("median(date(1), date(1) > date(2))", Exp.$date(1).median(Exp.$date(1).gt(Exp.$date(2)))),
                arguments("quantile(date(1), 0.5, date(1) > date(2))",
                        Exp.$date(1).quantile(0.5, Exp.$date(1).gt(Exp.$date(2)))),
                arguments("min(date(1), int(2) > 0)", Exp.$date(1).min(Exp.$int(2).gt(0)))
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
    void dateAgg_parsingError(String text) {
        assertThrows(ParseCancellationException.class, () -> Exp.exp(text));
    }

    @ParameterizedTest
    @MethodSource
    void dateTimeAgg(String text, Exp<?> expected) {
        Exp<?> exp = Exp.exp(text);
        assertEquals(expected, exp);
    }

    static Stream<Arguments> dateTimeAgg() {
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
    void dateTimeAgg_parsingError(String text) {
        assertThrows(ParseCancellationException.class, () -> Exp.exp(text));
    }
}
