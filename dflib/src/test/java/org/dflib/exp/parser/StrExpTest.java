package org.dflib.exp.parser;

import org.dflib.Condition;
import org.dflib.Exp;
import org.dflib.StrExp;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.stream.Stream;

import static org.dflib.Exp.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.params.provider.Arguments.arguments;

public class StrExpTest {

    @ParameterizedTest
    @MethodSource
    void scalar(String text, Exp<?> expected) {
        Exp<?> exp = parseExp(text);
        assertInstanceOf(StrExp.class, exp);
        assertEquals(expected, exp);
    }

    @SuppressWarnings("UnnecessaryUnicodeEscape")
    static Stream<Arguments> scalar() {
        return Stream.of(
                arguments("'single quotes'", $val("single quotes")),
                arguments("'^\\d+$'", $val("^\\d+$")),
                arguments("'unicode \u1234'", $val("unicode ሴ")),
                arguments("'escaped ''quote'''", $val("escaped 'quote'")),
                arguments("'newline\nline'", $val("newline\nline")),
                arguments("'newline\\not'", $val("newline\\not")),
                arguments("'\\tab'", $val("\\tab")),
                arguments("''", $val(""))
        );
    }

    @ParameterizedTest
    @ValueSource(strings = {"'missing ' escape'", "'missing quote", "'mismatched quotes\""})
    void scalar_throws(String text) {
        assertThrows(ExpParserException.class, () -> parseExp(text));
    }

    @ParameterizedTest
    @MethodSource
    void column(String text, Exp<?> expected) {
        Exp<?> exp = parseExp(text);
        assertInstanceOf(StrExp.class, exp);
        assertEquals(expected, exp);
    }

    static Stream<Arguments> column() {
        return Stream.of(
                arguments("str(a)", $str("a")),
                arguments("str(`a`)", $str("a")),
                arguments("str(1)", $str(1))
        );
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "STR(a)",
            "str(1.1)",
            "str(null)",
            "str(true)",
            "str(int(1))",
            "str(-1)",
    })
    void column_throws(String text) {
        assertThrows(ExpParserException.class, () -> parseExp(text));
    }

    @ParameterizedTest
    @MethodSource
    void cast(String text, Exp<?> expected) {
        Exp<?> exp = parseExp(text);
        assertInstanceOf(StrExp.class, exp);
        assertEquals(expected, exp);
    }

    static Stream<Arguments> cast() {
        return Stream.of(
                arguments("castAsStr(null)", $val(null).castAsStr()),
                arguments("castAsStr(1)", $val(1).castAsStr()),
                arguments("castAsStr(true)", $val(true).castAsStr()),
                arguments("castAsStr('1')", $strVal("1").castAsStr())
        );
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "CASTASSTR(1)",
    })
    void cast_throws(String text) {
        assertThrows(ExpParserException.class, () -> parseExp(text));
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
                arguments("'hello' = 'hello'", $val("hello").eq($val("hello"))),
                arguments("'hello' != 'world'", $val("hello").ne($val("world"))),
                arguments("str(1) = 'test'", $str(1).eq($val("test"))),
                arguments("trim(str(1)) = 'test'", $str(1).trim().eq($val("test"))),
                arguments("str(1) in ('a')", $str(1).in("a")),
                arguments("str(1) in ('a', 'b', 'c')", $str(1).in("a", "b", "c"))
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
            "str(1) in ()",
            "str(1) in ('a' 'b')",
            "str(1) in (1, 2, 3)",
            "str(1) not in ()",
            "str(1) not in ('a' 'b')",
            "str(1) not in (1, 2, 3)",
    })
    void relation_throws(String text) {
        assertThrows(ExpParserException.class, () -> parseExp(text));
    }

    @ParameterizedTest
    @MethodSource
    void function_returnsCondition(String text, Exp<?> expected) {
        Exp<?> exp = parseExp(text);
        assertInstanceOf(Condition.class, exp);
        assertEquals(expected, exp);
    }

    static Stream<Arguments> function_returnsCondition() {
        return Stream.of(
                arguments("matches('hello', 'he.*')", $strVal("hello").matches("he.*")),
                arguments("startsWith('hello', 'he')", $strVal("hello").startsWith("he")),
                arguments("endsWith('hello', 'lo')", $strVal("hello").endsWith("lo")),
                arguments("contains('hello', 'ell')", $strVal("hello").contains("ell")),
                arguments("matches(str(1), 'he.*')", $str(1).matches("he.*")),
                arguments("matches(trim('  hello'), 'he.*')", $strVal("  hello").trim().matches("he.*"))
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
    void function_returnsCondition_throws(String text) {
        assertThrows(ExpParserException.class, () -> parseExp(text));
    }

    @ParameterizedTest
    @MethodSource
    void function_returnsStrExp(String text, Exp<?> expected) {
        Exp<?> exp = parseExp(text);
        assertInstanceOf(StrExp.class, exp);
        assertEquals(expected, exp);
    }

    static Stream<Arguments> function_returnsStrExp() {
        return Stream.of(
                arguments("concat(str(1), 'example')", concat($str(1), $strVal("example"))),
                arguments("concat(str(1))", concat($str(1))),
                arguments("concat( )", concat()),
                arguments("concat()", concat()),
                arguments("concat('abc', 123, double(1), abc = 32.0)", concat(
                        $strVal("abc"),
                        $intVal(123),
                        $double(1),
                        $col("abc").eq($floatVal(32.0f))
                )),
                arguments("trim('  example  ')", $strVal("  example  ").trim()),
                arguments("substr('example', 2)", $strVal("example").substr(2)),
                arguments("substr('example', 2, 3)", $strVal("example").substr(2, 3)),
                arguments("substr('example', -2, 3)", $strVal("example").substr(-2, 3)),
                arguments("trim(str(1))", $str(1).trim()),
                arguments("trim(castAsStr(3))", $intVal(3).castAsStr().trim())
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
            "substr('example', 2, -1)",
    })
    void function_returnsStrExp_throws(String text) {
        assertThrows(ExpParserException.class, () -> parseExp(text));
    }

    @ParameterizedTest
    @MethodSource
    void split(String text, Exp<?> expected) {
        Exp<?> exp = parseExp(text);
        assertEquals(expected, exp);
    }

    static Stream<Arguments> split() {
        return Stream.of(
                arguments("split('a,b,c', ',')", $strVal("a,b,c").split(",")),
                arguments("split('a,b,c', ',', 2)", $strVal("a,b,c").split(",", 2)),
                arguments("split(str(1), '|')", $str(1).split("|")),
                arguments("split(trim(' a|b|c '), '|', 2)", $strVal(" a|b|c ").trim().split("|", 2))
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
    void split_throws(String text) {
        assertThrows(ExpParserException.class, () -> parseExp(text));
    }

    @ParameterizedTest
    @MethodSource
    void aggregate(String text, Exp<?> expected) {
        Exp<?> exp = parseExp(text);
        assertInstanceOf(StrExp.class, exp);
        assertEquals(expected, exp);
    }

    static Stream<Arguments> aggregate() {
        return Stream.of(
                arguments("min(str(1))", $str(1).min()),
                arguments("max(str(1))", $str(1).max()),
                arguments("min(str(1), matches(str(1), '#.*'))", $str(1).min($str(1).matches("#.*"))),
                arguments("max(str(1), matches(str(1), '#.*'))", $str(1).max($str(1).matches("#.*"))),
                arguments("min(str(1), matches(str(2), '#.*'))", $str(1).min($str(2).matches("#.*")))
        );
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "MIN(str(1))",
            "max()",
            "min(str(1), )",
            "max(str(1), 0)",
    })
    void aggregate_throws(String text) {
        assertThrows(ExpParserException.class, () -> parseExp(text));
    }
}
