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
import static org.dflib.Exp.$str;
import static org.dflib.Exp.$strVal;
import static org.dflib.Exp.concat;
import static org.dflib.Exp.count;
import static org.dflib.Exp.parseExp;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.params.provider.Arguments.arguments;

/**
 * Tests of functions passed to the parser as {@code ?f} parameters.
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
                arguments("?f()", new Object[]{COUNT}, count()),
                arguments("?f(a)", new Object[]{LEN}, len($col("a"))),
                arguments("?f(a, b)", new Object[]{PAIR}, pair($col("a"), $col("b"))),
                arguments("?f(a, b, c)", new Object[]{TRIPLE}, triple($col("a"), $col("b"), $col("c"))),
                arguments("?f(a, b, c, d)", new Object[]{JOIN}, concat($col("a"), $col("b"), $col("c"), $col("d"))),
                arguments("?f(int(a), 'x', ?)", new Object[]{TRIPLE, 5}, triple($int("a"), $strVal("x"), $intVal(5))),

                // nested, and mixed with named functions and operators
                arguments("?f(?f(a, b))", new Object[]{LEN, PAIR}, len(pair($col("a"), $col("b")))),
                arguments("abs(?f(a))", new Object[]{LEN}, len($col("a")).abs()),
                arguments("?f(trim(a)) + 1", new Object[]{LEN}, len($col("a").trim()).add($intVal(1))),
                arguments("?f(a) in (?, 2)", new Object[]{LEN, 1}, len($col("a")).in(1, 2)),

                // parameters are bound in the order of the placeholders
                arguments("?f(a, ?) = ?", new Object[]{PAIR, "x", "a-x"},
                        pair($col("a"), $strVal("x")).eq($strVal("a-x"))),
                arguments("?f(a) > ? and castAsBool(?f(e, ?))", new Object[]{LEN, 5, PAIR, 1},
                        len($col("a")).gt($intVal(5)).and(pair($col("e"), $intVal(1)).castAsBool()))
        );
    }

    @Test
    void typedUdf() {

        // an untyped argument is cast to the declared parameter type, as for a registered function
        assertEquals(
                new TwiceFunction().call($col("a").castAsNumber().castAsInt()),
                parseExp("?f(a)", new TwiceFunction()));

        assertEquals(new TwiceFunction().call($int("a")), parseExp("?f(int(a))", new TwiceFunction()));
    }

    @Test
    void qlFunction_Overloads() {
        assertEquals($str("a").upper(), parseExp("?f(str(a))", new UpperOrAbsFunction()));
        assertEquals($int("a").abs(), parseExp("?f(int(a))", new UpperOrAbsFunction()));
    }

    @ParameterizedTest
    @MethodSource
    void parameterError(String text, Object[] params, String expectedMessage) {
        QLParserException e = assertThrows(QLParserException.class, () -> parseExp(text, params));
        assertEquals(expectedMessage, e.getMessage());
    }

    static Stream<Arguments> parameterError() {
        return Stream.of(
                arguments("?f(a)", new Object[0],
                        "Parameter '?f' at 1:0 has no value: 0 parameter(s) bound, at least 1 used"),
                arguments("?f(a) > ?", new Object[]{LEN},
                        "Parameter '?' at 1:8 has no value: 1 parameter(s) bound, at least 2 used"),
                arguments("?f(a)", new Object[]{5},
                        "Parameter '?f' at 1:0 is used as a function, so its value must be a Udf0..UdfN or a QLFunction"
                                + " object, got: java.lang.Integer"),
                arguments("int(a) > 1 and ?f(a)", new Object[]{null},
                        "Parameter '?f' at 1:15 is used as a function, so its value must be a Udf0..UdfN or a QLFunction"
                                + " object, got: null"),
                arguments("?f(a)", new Object[]{PAIR},
                        "Function '?f' at 1:0: No overload of ?f matches ?f(OBJECT). Available: ?f(OBJECT, OBJECT)"),
                arguments("?f(str(a))", new Object[]{new TwiceFunction()},
                        "Function '?f' at 1:0: No overload of ?f matches ?f(STRING). Available: ?f(NUMERIC<Integer>)"),
                arguments("?f(a)", new Object[]{new UpperOrAbsFunction()},
                        "Function '?f' at 1:0: Ambiguous call to ?f(OBJECT): argument 1 is untyped, cast it explicitly."),
                arguments("?f(a)", new Object[]{new QLFunctionAndUdf()},
                        "Function '?f' at 1:0: A QLFunction must not also implement Udf0..UdfN, as their default methods"
                                + " are public 'call' methods and would register as bogus signatures: "
                                + QLFunctionAndUdf.class.getName()),
                arguments("?f(a)", new Object[]{Udf1.of(e -> {
                            throw new IllegalStateException("boom");
                        })},
                        "Function '?f' at 1:0: boom")
        );
    }

    @ParameterizedTest
    @MethodSource
    void syntaxError(String text) {
        assertThrows(QLParserException.class, () -> parseExp(text, LEN));
    }

    static Stream<String> syntaxError() {
        return Stream.of(
                // only the lowercase "?f" is a function placeholder
                "?F(a)",
                // a function placeholder must be called
                "?f",
                "?f + 1",
                "? f(a)"
        );
    }

    @Test
    void dataFrame() {
        DataFrame df = DataFrame.foldByRow("a", "b").of(
                "x", 1,
                "yy", 2,
                "zzz", 3);

        new DataFrameAsserts(df.rows("?f(a) > ?", LEN, 1).select(), "a", "b")
                .expectHeight(2)
                .expectRow(0, "yy", 2)
                .expectRow(1, "zzz", 3);

        new DataFrameAsserts(df.cols("c").select("?f(a, b)", PAIR), "c")
                .expectHeight(3)
                .expectRow(0, "x-1")
                .expectRow(1, "yy-2")
                .expectRow(2, "zzz-3");

        new DataFrameAsserts(df.sort("?f(a) desc", LEN), "a", "b")
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

    public static class UpperOrAbsFunction implements QLFunction {

        public StrExp call(StrExp e) {
            return e.upper();
        }

        public NumExp<?> call(NumExp<?> e) {
            return e.abs();
        }
    }

    public static class QLFunctionAndUdf implements QLFunction, Udf1<Object, Object> {
        @Override
        public Exp<Object> call(Exp<Object> e) {
            return e;
        }
    }
}
