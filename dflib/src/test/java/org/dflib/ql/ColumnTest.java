package org.dflib.ql;

import org.dflib.Exp;
import org.dflib.ql.antlr4.ExpParser;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.Set;
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
    @MethodSource
    public void expAlias(String exp, Exp<?> expected) {
        assertEquals(expected, parseExp(exp));
    }

    static Stream<Arguments> expAlias() {
        return Stream.of(
                arguments("a as b", $col("a").as("b")),
                arguments("a as `new column`", $col("a").as("new column")),
                arguments("`old column` as `new column`", $col("old column").as("new column")),
                arguments("int(a) * 8 as int", $int("a").mul(8).as("int")),
                arguments("int(a) = int(b) as bool", $int("a").eq($int("b")).as("bool"))
        );
    }

    /**
     * This test checks every token in the parser if it's a valid identifier.
     * <p>
     * If it fails after adding new token you should either allow it in the `fnName` rule in the grammar,
     * or add to the `RULES_TO_IGNORE` set below.
     */
    @Test()
    public void keywordAsColumnName() {
        int maxTokenType = ExpParser.VOCABULARY.getMaxTokenType();
        for (int rule = 1; rule <= maxTokenType; rule++) {
            if (RULES_TO_IGNORE.contains(rule)) {
                continue;
            }

            String keyword = ExpParser.VOCABULARY.getDisplayName(rule);
            keyword = keyword.substring(1, keyword.length() - 1);// rule name goes as 'rule'

            assertEquals($col(keyword), parseExp(keyword),
                    "Token '" + keyword + "' (" + rule + ") is not a valid identifier name. " +
                            "Check comments of this test on how to resolve that.");
        }
    }

    static final Set<Integer> RULES_TO_IGNORE = Set.of(
            ExpParser.LP,
            ExpParser.RP,
            ExpParser.COMMA,
            ExpParser.NOT,
            ExpParser.EQ,
            ExpParser.NE,
            ExpParser.LE,
            ExpParser.GE,
            ExpParser.LT,
            ExpParser.GT,
            ExpParser.BETWEEN,
            ExpParser.IN,
            ExpParser.ADD,
            ExpParser.SUB,
            ExpParser.MUL,
            ExpParser.DIV,
            ExpParser.MOD,
            ExpParser.AND,
            ExpParser.OR,
            ExpParser.NULL,
            ExpParser.TRUE,
            ExpParser.FALSE,
            ExpParser.AS,
            ExpParser.INTEGER_LITERAL,
            ExpParser.FLOAT_LITERAL,
            ExpParser.STRING_LITERAL,
            ExpParser.QUOTED_IDENTIFIER,
            ExpParser.IDENTIFIER,
            ExpParser.WS
    );

}
