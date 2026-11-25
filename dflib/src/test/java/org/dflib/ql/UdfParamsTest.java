package org.dflib.ql;

import org.dflib.DataFrame;
import org.dflib.Exp;
import org.dflib.NumExp;
import org.dflib.StrExp;
import org.dflib.Udf0;
import org.dflib.Udf1;
import org.dflib.Udf2;
import org.dflib.Udf3;
import org.dflib.UdfN;
import org.dflib.unit.DataFrameAsserts;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.dflib.Exp.$col;
import static org.dflib.Exp.$int;
import static org.dflib.Exp.$intVal;
import static org.dflib.Exp.$strVal;
import static org.dflib.Exp.concat;
import static org.dflib.Exp.count;
import static org.dflib.Exp.parseExp;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.params.provider.Arguments.arguments;

/**
 * Tests of functions passed to the parser as {@code ?(..)} parameters.
 */
public class UdfParamsTest {

    static final Udf0<Integer> COUNT = Udf0.of(Exp::count);
    static final Udf1<Object, Integer> LEN = Udf1.of(UdfParamsTest::len);
    static final Udf2<Object, Object, String> PAIR = Udf2.of(UdfParamsTest::pair);
    static final Udf3<Object, Object, Object, String> TRIPLE = Udf3.of(UdfParamsTest::triple);
    static final UdfN<String> JOIN = UdfN.of(Exp::concat);

    static NumExp<Integer> len(Exp<?> e) {
        return e.castAsStr().len();
    }

    static StrExp pair(Exp<?> a, Exp<?> b) {
        return concat(a, "-", b);
    }

    static StrExp triple(Exp<?> a, Exp<?> b, Exp<?> c) {
        return concat(a, b, c);
    }

    @ParameterizedTest
    @MethodSource
    void udfExpression(String text, Object[] params, Exp<?> expected) {
        assertEquals(expected, parseExp(text, params));
    }

    static Stream<Arguments> udfExpression() {
        return Stream.of(
                arguments("?()", new Object[]{COUNT}, count()),
                arguments("?(a)", new Object[]{LEN}, len($col("a"))),
                arguments("?(a, b)", new Object[]{PAIR}, pair($col("a"), $col("b"))),
                arguments("?(a, b, c)", new Object[]{TRIPLE}, triple($col("a"), $col("b"), $col("c"))),
                arguments("?(a, b, c, d)", new Object[]{JOIN}, concat($col("a"), $col("b"), $col("c"), $col("d"))),
                arguments("?(int(a), 'x', ?)", new Object[]{TRIPLE, 5}, triple($int("a"), $strVal("x"), $intVal(5))),
                arguments("? (a)", new Object[]{LEN}, len($col("a"))),

                // nested, and mixed with named functions and operators
                arguments("?(?(a, b))", new Object[]{LEN, PAIR}, len(pair($col("a"), $col("b")))),
                arguments("abs(?(a))", new Object[]{LEN}, len($col("a")).abs()),
                arguments("?(trim(a)) + 1", new Object[]{LEN}, len($col("a").trim()).add($intVal(1))),
                arguments("?(a) in (?, 2)", new Object[]{LEN, 1}, len($col("a")).in(1, 2)),

                // parameters are bound in the order of the placeholders
                arguments("?(a, ?) = ?", new Object[]{PAIR, "x", "a-x"},
                        pair($col("a"), $strVal("x")).eq($strVal("a-x"))),
                arguments("?(a) > ? and castAsBool(?(e, ?))", new Object[]{LEN, 5, PAIR, 1},
                        len($col("a")).gt($intVal(5)).and(pair($col("e"), $intVal(1)).castAsBool()))
        );
    }

    @Test
    void typedUdf() {

        // an untyped argument is cast to the declared parameter type, as for a registered function
        assertEquals(
                $col("a").castAsNumber().castAsInt().mul($intVal(2)).castAsInt(),
                parseExp("?(a)", new TwiceFunction()));

        assertEquals(new TwiceFunction().call($int("a")), parseExp("?(int(a))", new TwiceFunction()));
    }

    @ParameterizedTest
    @MethodSource
    void parameterError(String text, Object[] params, String expectedMessage) {
        QLParserException e = assertThrows(QLParserException.class, () -> parseExp(text, params));
        assertEquals(expectedMessage, e.getMessage());
    }

    static Stream<Arguments> parameterError() {
        return Stream.of(
                arguments("?(a)", new Object[0],
                        "Parameter '?' at 1:0 has no value: 0 parameter(s) bound, at least 1 used"),
                arguments("?(a) > ?", new Object[]{LEN},
                        "Parameter '?' at 1:7 has no value: 1 parameter(s) bound, at least 2 used"),
                arguments("?(a)", new Object[]{5},
                        "Parameter '?' at 1:0 is used as a function, so its value must be a Udf0..UdfN object, got:"
                                + " java.lang.Integer"),
                arguments("int(a) > 1 and ?(a)", new Object[]{null},
                        "Parameter '?' at 1:15 is used as a function, so its value must be a Udf0..UdfN object, got:"
                                + " null"),
                arguments("?(a)", new Object[]{PAIR},
                        "Function '?' at 1:0: No overload of ? matches ?(OBJECT). Available: ?(OBJECT, OBJECT)"),
                arguments("?(str(a))", new Object[]{new TwiceFunction()},
                        "Function '?' at 1:0: No overload of ? matches ?(STRING). Available: ?(NUMERIC)"),
                arguments("?(a)", new Object[]{Udf1.of(e -> {
                            throw new IllegalStateException("boom");
                        })},
                        "Function '?' at 1:0: boom")
        );
    }

    @ParameterizedTest
    @MethodSource
    void syntaxError(String text) {
        assertThrows(QLParserException.class, () -> parseExp(text, LEN));
    }

    static Stream<String> syntaxError() {
        return Stream.of(
                // a function placeholder has no name
                "?f(a)",
                "?F(a)",
                "? f(a)"
        );
    }

    @Test
    void dataFrame() {
        DataFrame df = DataFrame.foldByRow("a", "b").of(
                "x", 1,
                "yy", 2,
                "zzz", 3);

        new DataFrameAsserts(df.rows("?(a) > ?", LEN, 1).select(), "a", "b")
                .expectHeight(2)
                .expectRow(0, "yy", 2)
                .expectRow(1, "zzz", 3);

        new DataFrameAsserts(df.cols("c").select("?(a, b)", PAIR), "c")
                .expectHeight(3)
                .expectRow(0, "x-1")
                .expectRow(1, "yy-2")
                .expectRow(2, "zzz-3");

        new DataFrameAsserts(df.sort("?(a) desc", LEN), "a", "b")
                .expectHeight(3)
                .expectRow(0, "zzz", 3)
                .expectRow(1, "yy", 2)
                .expectRow(2, "x", 1);
    }

    private static class TwiceFunction implements Udf1<Integer, Integer> {
        @Override
        public Exp<Integer> call(Exp<Integer> exp) {
            return exp.castAsInt().mul($intVal(2)).castAsInt();
        }
    }
}
