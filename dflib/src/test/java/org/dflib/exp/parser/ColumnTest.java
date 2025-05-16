package org.dflib.exp.parser;

import org.dflib.Exp;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.dflib.Exp.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.params.provider.Arguments.arguments;

public class ColumnTest {

    @ParameterizedTest
    @MethodSource
    public void named(String exp, Exp<?> result) {
        assertEquals(result, exp(exp));
    }

    static Stream<Arguments> named() {
        return Stream.of(

                // must allow special chars from Java identifiers without quotes
                arguments("a", $col("a")),
                arguments("_a", $col("_a")),
                arguments("$a", $col("$a")),

                // must allow unquoted names in col(..). TODO: dubious syntax
                arguments("col(a)", $col("a")),
                arguments("col(_a)", $col("_a")),
                arguments("col($a)", $col("$a")),

                // standalone quoted strings are treated as values not column names
                arguments("\"a b\"", $val("a b")),
                arguments("'a b'", $val("a b")),

                arguments("col(\"a b\")", $col("a b")),
                arguments("col('a b')", $col("a b")),
                arguments("col('a)b')", $col("a)b")),
                arguments("col('a\\'b')", $col("a'b")),
                arguments("col(\"a\\\"b\")", $col("a\"b"))
        );
    }

    @ParameterizedTest
    @MethodSource
    public void namedInExp(String exp, Exp<?> expected) {
        assertEquals(expected, exp(exp));
    }

    static Stream<Arguments> namedInExp() {
        return Stream.of(

                arguments("a = b", $col("a").eq($col("b"))),
                arguments("col(a) = col(b)", $col("a").eq($col("b"))),
                arguments("int(a) = int(b)", $int("a").eq($int("b"))),

                arguments("a = 3", $col("a").eq($val(3))),
                arguments("a = col(b)", $col("a").eq($col("b"))),
                arguments("col(1) = col(2)", $col(1).eq($col(2)))
        );
    }

    @ParameterizedTest
    @MethodSource
    public void posInExp(String exp, Exp<?> expected) {
        assertEquals(expected, exp(exp));
    }

    static Stream<Arguments> posInExp() {
        return Stream.of(
                arguments("col(1) = col(2)", $col(1).eq($col(2)))
        );
    }
}
