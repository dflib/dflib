package org.dflib.exp.parser;

import org.antlr.v4.runtime.misc.ParseCancellationException;
import org.dflib.Condition;
import org.dflib.Exp;
import org.dflib.StrExp;
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

public class StrExpTest {

    @ParameterizedTest
    @MethodSource
    void scalar(String text, Exp<?> expected) {
        Exp<?> exp = Exp.exp(text);
        assertInstanceOf(StrExp.class, exp);
        assertEquals(expected, exp);
    }

    static Stream<Arguments> scalar() {
        return Stream.of(
                arguments("'single quotes'", Exp.$val("single quotes")),
                arguments("'^\\d+$'", Exp.$val("^\\d+$")),
                arguments("\"double quotes\"", Exp.$val("double quotes")),
                arguments("\"unicode \\u1234\"", Exp.$val("unicode ሴ")),
                arguments("\"escaped \\\"quote\\\"\"", Exp.$val("escaped \"quote\"")),
                arguments("\"newline\nline\"", Exp.$val("newline\nline")),
                arguments("\"newline\\not\"", Exp.$val("newline\\not")),
                arguments("\"\\tab\"", Exp.$val("\\tab")),
                arguments("\"\"", Exp.$val(""))
        );
    }

    @ParameterizedTest
    @ValueSource(strings = {"\"missing \" escape\""})
    void scalar_parsingError(String text) {
        assertThrows(ParseCancellationException.class, () -> Exp.exp(text));
    }

    @ParameterizedTest
    @ValueSource(strings = {"\"missing quote", "'mismatched quotes\""})
    void scalar_lexicalError(String text) {
        assertThrows(LexerCancellationException.class, () -> Exp.exp(text));
    }

    @ParameterizedTest
    @MethodSource
    void column(String text, Exp<?> expected) {
        Exp<?> exp = Exp.exp(text);
        assertInstanceOf(StrExp.class, exp);
        assertEquals(expected, exp);
    }

    static Stream<Arguments> column() {
        return Stream.of(
                arguments("str(a)", Exp.$str("a")),
                arguments("str('a')", Exp.$str("a")),
                arguments("str(\"a\")", Exp.$str("a")),
                arguments("str(1)", Exp.$str(1))
        );
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "STR(a)",
            "str(1.1)",
            "str(null)",
            "str(true)",
            "str(int(1))",
    })
    void column_parsingError(String text) {
        assertThrows(ParseCancellationException.class, () -> Exp.exp(text));
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "str(-1)",
    })
    void column_apiError(String text) {
        assertThrows(IllegalArgumentException.class, () -> Exp.exp(text));
    }

    @ParameterizedTest
    @MethodSource
    void cast(String text, Exp<?> expected) {
        Exp<?> exp = Exp.exp(text);
        assertInstanceOf(StrExp.class, exp);
        assertEquals(expected, exp);
    }

    static Stream<Arguments> cast() {
        return Stream.of(
                arguments("castAsStr(null)", Exp.$val(null).castAsStr()),
                arguments("castAsStr(1)", Exp.$val(1).castAsStr()),
                arguments("castAsStr(true)", Exp.$val(true).castAsStr()),
                arguments("castAsStr('1')", Exp.$strVal("1").castAsStr())
        );
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "CASTASSTR(1)",
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
    void relation_parsingError(String text) {
        assertThrows(ParseCancellationException.class, () -> Exp.exp(text));
    }

    @ParameterizedTest
    @MethodSource
    void function_returnsCondition(String text, Exp<?> expected) {
        Exp<?> exp = Exp.exp(text);
        assertInstanceOf(Condition.class, exp);
        assertEquals(expected, exp);
    }

    static Stream<Arguments> function_returnsCondition() {
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
    void function_returnsCondition_parsingError(String text) {
        assertThrows(ParseCancellationException.class, () -> Exp.exp(text));
    }

    @ParameterizedTest
    @MethodSource
    void function_returnsStrExp(String text, Exp<?> expected) {
        Exp<?> exp = Exp.exp(text);
        assertInstanceOf(StrExp.class, exp);
        assertEquals(expected, exp);
    }

    static Stream<Arguments> function_returnsStrExp() {
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
            "CONCAT()",
            "trim()",
            "trim(  abc  )",
            "substr('example')",
            "substr('example', 1 + 2)",
            "substr('example', 2, null)",
    })
    void function_returnsStrExp_parsingError(String text) {
        assertThrows(ParseCancellationException.class, () -> Exp.exp(text));
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "substr('example', 2, -1)",
    })
    void function_returnsStrExp_apiError(String text) {
        assertThrows(IllegalArgumentException.class, () -> Exp.exp(text));
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
    void aggregate(String text, Exp<?> expected) {
        Exp<?> exp = Exp.exp(text);
        assertInstanceOf(StrExp.class, exp);
        assertEquals(expected, exp);
    }

    static Stream<Arguments> aggregate() {
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
    void aggregate_parsingError(String text) {
        assertThrows(ParseCancellationException.class, () -> Exp.exp(text));
    }
}
