package org.dflib;

import org.dflib.ql.QLParserException;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.stream.Stream;

import static org.dflib.Exp.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.params.provider.Arguments.arguments;

public class Exp_ParseExpTest {

    @ParameterizedTest
    @MethodSource
    public void nullScalar(String text, Exp<?> expected) {
        Exp<?> exp = parseExp(text);
        assertEquals(expected, exp);
    }

    static Stream<Arguments> nullScalar() {
        return Stream.of(
                arguments("null", $val(null))
        );
    }

    @ParameterizedTest
    @ValueSource(strings = {"NULL", "Null", "None", "nil", "void"})
    public void nullScalar_parsingErrorAsGenericColumn(String text) {
        assertEquals(parseExp(text), $col(text));
    }


    @ParameterizedTest
    @MethodSource
    public void positionalAggregate(String text, Exp<?> expected) {
        Exp<?> exp = parseExp(text);
        assertEquals(expected, exp);
    }

    static Stream<Arguments> positionalAggregate() {
        return Stream.of(
                arguments("first(int(1))", $int(1).first()),
                arguments("last(int(1))", $int(1).last()),
                arguments("first(int(1), int(1) > 0)", $int(1).first($int(1).gt($val(0)))),
                arguments("first(1 + 2)", $intVal(1).add(2).first()),
                arguments("first(sum(int(1)))", $int(1).sum().first()),
                arguments("first(int(1) > 0)", $int(1).gt(0).first())
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
    public void positionalAggregate_throws(String text) {
        assertThrows(QLParserException.class, () -> parseExp(text));
    }

    @ParameterizedTest
    @MethodSource
    public void genericAggregate(String text, Exp<?> expected) {
        Exp<?> exp = parseExp(text);
        assertEquals(expected, exp);
    }

    static Stream<Arguments> genericAggregate() {
        return Stream.of(
                arguments("vConcat(a, ',')", $col("a").vConcat(",", "", "")),
                arguments("vConcat(a, int(1) > 0, ',')", $col("a").vConcat($int(1).gt(0), ",", "", "")),
                arguments("vConcat(a, ',', 'p: ', 's: ')", $col("a").vConcat(",", "p: ", "s: ")),
                arguments("vConcat(a, int(1) > 0, ',', 'p: ', 's: ')", $col("a").vConcat($int(1).gt(0), ",", "p: ", "s: ")),

                arguments("list(a)", $col("a").list()),
                arguments("set(a)", $col("a").set()),
                arguments("array(a, 'java.lang.String')", $col("a").array(new String[0])),
                arguments("array(a, 'java.lang.Integer')", $col("a").array(new Integer[0]))
        );
    }

    @ParameterizedTest
    @MethodSource
    public void genericRelation(String text, Exp<?> expected) {
        Exp<?> exp = parseExp(text);
        assertEquals(expected, exp);
    }

    static Stream<Arguments> genericRelation() {
        return Stream.of(
                arguments("a = 1", $col("a").eq(1)),
                arguments("a = 'abc'", $col("a").eq("abc")),
                arguments("a = false", $col("a").eq(false)),
                arguments("a != 1", $col("a").ne(1)),
                arguments("a != 'abc'", $col("a").ne("abc")),
                arguments("a != false", $col("a").ne(false)),
                arguments("a in (1)", $col("a").in(1)),
                arguments("a in (1, 2, 3)", $col("a").in(1, 2, 3)),
                arguments("a in (1, 'abc', false)", $col("a").in(1, "abc", false)),
                arguments("a not in (1)", $col("a").notIn(1)),
                arguments("a not in (1, 2, 3)", $col("a").notIn(1, 2, 3)),
                arguments("a not in (1, 'abc', false)", $col("a").notIn(1, "abc", false))
        );
    }
}
