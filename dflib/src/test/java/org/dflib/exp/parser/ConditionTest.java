package org.dflib.exp.parser;

import org.dflib.Condition;
import org.dflib.Exp;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.stream.Stream;

import static org.dflib.Exp.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.params.provider.Arguments.arguments;

public class ConditionTest {

    @ParameterizedTest
    @MethodSource
    void test(String text, Exp<?> expected) {
        Exp<?> exp = parseExp(text);
        assertInstanceOf(Condition.class, exp);
        assertEquals(expected, exp);
    }

    static Stream<Arguments> test() {
        return Stream.of(
                arguments("1 > 0", $intVal(1).gt($val(0))),
                arguments("1 >= 0", $intVal(1).ge($val(0))),
                arguments("1 < 0", $intVal(1).lt($val(0))),
                arguments("1 <= 0", $intVal(1).le($val(0))),
                arguments("1 = 1", $intVal(1).eq($val(1))),
                arguments("1 != 0", $intVal(1).ne($val(0))),
                arguments("true and false", $boolVal(true).and($boolVal(false))),
                arguments("true or false", $boolVal(true).or($boolVal(false))),
                arguments("true or false and true", $boolVal(true).or($boolVal(false).and($boolVal(true)))),
                arguments("true and false or true", $boolVal(true).and($boolVal(false)).or($boolVal(true))),
                arguments("true and (false or true)", $boolVal(true).and($boolVal(false).or($boolVal(true)))),
                arguments("not true", $boolVal(true).not()),
                arguments("not bool(1)", $bool(1).not()),
                arguments("not int(1) > 0", $int(1).gt(0).not())
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
        assertThrows(ExpParserException.class, () -> parseExp(text));
    }

    @ParameterizedTest
    @MethodSource
    void precedence(String text, Condition expected) {
        Exp<?> exp = parseExp(text);
        assertInstanceOf(Condition.class, exp);
        assertEquals(expected, exp);
    }

    // associativity must follow Java precedence rules (where operators match with DFLib)
    // https://docs.oracle.com/javase/tutorial/java/nutsandbolts/operators.html
    static Stream<Arguments> precedence() {
        return Stream.of(

                // 0. () override precedence
                arguments("(bool(a) or bool(b)) and bool(c)", $bool("a").or($bool("b")).and($bool("c"))),
                arguments("not (bool(a) and bool(c))", not($bool("a").and($bool("c")))),

                // 1. [not] takes precedence over everything
                arguments("not bool(a) and bool(c)", not($bool("a")).and($bool("c"))),

                // 2. [*, /, %, +, -] takes precedence over [<, >, <=, >=]
                arguments("5 + 1 < 6 * 2", $intVal(5).add(1).lt($intVal(6).mul(2))),
                arguments("5 + 1 <= 6 * 2", $intVal(5).add(1).le($intVal(6).mul(2))),
                arguments("5 - 1 > 6 / 2", $intVal(5).sub(1).gt($intVal(6).div(2))),
                arguments("5 - 1 >= 6 % 2", $intVal(5).sub(1).ge($intVal(6).mod(2))),

                // 3. [<, >, <=, >=] takes precedence over [=, !=]
                arguments("true = 5 < 4", $boolVal(true).eq($intVal(5).lt(4))),
                arguments("true != 5 > 4", $boolVal(true).ne($intVal(5).gt(4))),
                arguments("6 < 2 = 5 > 4", $intVal(6).lt($intVal(2)).eq($intVal(5).gt(4))),

                // 4. [=, !=] takes precedence over [and]
                arguments("true and 5 != 4", $boolVal(true).and($intVal(5).ne(4))),
                arguments("true and 5 = 4", $boolVal(true).and($intVal(5).eq(4))),

                // 5. [and] takes precedence over [or]
                arguments("bool(a) or bool(b) and bool(c)", $bool("a").or($bool("b").and($bool("c")))),

                // 6. left to right eval of [=, !=]
                arguments("5 = 1 != (6 = 2)", $intVal(5).eq(1).ne($intVal(6).eq(2)))
        );
    }
    
    @ParameterizedTest
    @MethodSource
    void scalar(String text, Exp<?> expected) {
        Exp<?> exp = parseExp(text);
        assertInstanceOf(Condition.class, exp);
        assertEquals(expected, exp);
    }

    static Stream<Arguments> scalar() {
        return Stream.of(
                arguments("true", $boolVal(true)),
                arguments("false", $boolVal(false))
        );
    }

    @ParameterizedTest
    @ValueSource(strings = {"TRUE", "FALSE", "True", "False"})
    void wrongCapitalizationTreatedAsColName(String text) {
        assertEquals(parseExp(text), $col(text));
    }

    @ParameterizedTest
    @MethodSource
    void column(String text, Exp<?> expected) {
        Exp<?> exp = parseExp(text);
        assertInstanceOf(Condition.class, exp);
        assertEquals(expected, exp);
    }

    static Stream<Arguments> column() {
        return Stream.of(
                arguments("bool(a)", $bool("a")),
                arguments("bool(`a`)", $bool("a")),
                arguments("bool(1)", $bool(1))
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
        assertThrows(ExpParserException.class, () -> parseExp(text));
    }

    @ParameterizedTest
    @MethodSource
    void cast(String text, Exp<?> expected) {
        Exp<?> exp = parseExp(text);
        assertInstanceOf(Condition.class, exp);
        assertEquals(expected, exp);
    }

    static Stream<Arguments> cast() {
        return Stream.of(
                arguments("castAsBool(null)", $val(null).castAsBool()),
                arguments("castAsBool(1)", $val(1).castAsBool()),
                arguments("castAsBool(true)", $boolVal(true).castAsBool()),
                arguments("castAsBool('1')", $strVal("1").castAsBool())
        );
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "CASTASBOOL(1)",
    })
    void cast_throws(String text) {
        assertThrows(ExpParserException.class, () -> parseExp(text));
    }

    @ParameterizedTest
    @MethodSource
    void function(String text, Exp<?> expected) {
        Exp<?> exp = parseExp(text);
        assertInstanceOf(Condition.class, exp);
        assertEquals(expected, exp);
    }

    static Stream<Arguments> function() {
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
    void function_throws(String text) {
        assertThrows(ExpParserException.class, () -> parseExp(text));
    }
}
