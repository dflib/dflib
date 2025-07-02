package org.dflib.exp.parser;

import org.dflib.Exp;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.stream.Stream;

import static org.dflib.Exp.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.params.provider.Arguments.arguments;

public class ColumnTest {

    @ParameterizedTest
    @MethodSource
    public void named(String exp, Exp<?> result) {
        assertEquals(result, parseExp(exp));
    }

    static Stream<Arguments> named() {
        return Stream.of(

                // must allow special chars from Java identifiers without quotes
                arguments("a", $col("a")),
                arguments("_a", $col("_a")),
                arguments("$a", $col("$a")),

                // quoted identifiers
                arguments("`a`", $col("a")),
                arguments("`_a`", $col("_a")),
                arguments("`$a`", $col("$a")),
                arguments("`a b`", $col("a b")),
                arguments("`a``b`", $col("a`b")),

                // must allow unquoted names in col(..). TODO: dubious syntax
                arguments("col(a)", $col("a")),
                arguments("col(_a)", $col("_a")),
                arguments("col($a)", $col("$a")),

                // standalone quoted strings are treated as values not column names
                arguments("'a b'", $val("a b")),

                arguments("col(`a b`)", $col("a b")),
                arguments("col(`a b`)", $col("a b")),
                arguments("col(`a)b`)", $col("a)b")),
                arguments("col(`a'b`)", $col("a'b")),
                arguments("col(`a``b`)", $col("a`b")),
                arguments("col(`a``b`)", $col("a`b")),
                arguments("col(`a\"b`)", $col("a\"b")),

                // keywords, just a sanity check see keywordAsCol() test for a full coverage of keywords
                arguments("bool", $col("bool")),
                arguments("if", $col("if"))
        );
    }

    @ParameterizedTest
    @MethodSource
    public void namedInExp(String exp, Exp<?> expected) {
        assertEquals(expected, parseExp(exp));
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
        assertEquals(expected, parseExp(exp));
    }

    static Stream<Arguments> posInExp() {
        return Stream.of(
                arguments("col(1) = col(2)", $col(1).eq($col(2)))
        );
    }

    @ParameterizedTest
    // Using the same names as tokens in grammar for easier copy-paste
    // should be in sync with the grammar
    @ValueSource(strings = {
            "BOOL",
            "INT",
            "LONG",
            "BIGINT",
            "FLOAT",
            "DOUBLE",
            "DECIMAL",
            "STR",
            "COL",
            "CAST_AS_BOOL",
            "CAST_AS_INT",
            "CAST_AS_LONG",
            "CAST_AS_BIGINT",
            "CAST_AS_FLOAT",
            "CAST_AS_DOUBLE",
            "CAST_AS_DECIMAL",
            "CAST_AS_STR",
            "CAST_AS_TIME",
            "CAST_AS_DATE",
            "CAST_AS_DATETIME",
            "CAST_AS_OFFSET_DATETIME",
            "IF",
            "IF_NULL",
            "SPLIT",
            "SHIFT",
            "CONCAT",
            "SUBSTR",
            "TRIM",
            "LEN",
            "MATCHES",
            "STARTS_WITH",
            "ENDS_WITH",
            "CONTAINS",
            "DATE",
            "TIME",
            "DATETIME",
            "OFFSET_DATETIME",
            "YEAR",
            "MONTH",
            "DAY",
            "HOUR",
            "MINUTE",
            "SECOND",
            "MILLISECOND",
            "PLUS_YEARS",
            "PLUS_MONTHS",
            "PLUS_WEEKS",
            "PLUS_DAYS",
            "PLUS_HOURS",
            "PLUS_MINUTES",
            "PLUS_SECONDS",
            "PLUS_MILLISECONDS",
            "PLUS_NANOS",
            "ABS",
            "ROUND",
            "ROW_NUM",
            "SCALE",
            "COUNT",
            "SUM",
            "CUMSUM",
            "MIN",
            "MAX",
            "AVG",
            "MEDIAN",
            "QUANTILE",
            "FIRST",
            "LAST",
            "VCONCAT",
            "LIST",
            "SET",
            "ARRAY",
    })
    public void keywordAsColumnName(String name) {
        String keyword = name.toLowerCase();
        assertEquals($col(keyword), parseExp(keyword));
    }
}
